from tensorflow.keras.models import Sequential, load_model
from tensorflow.keras.layers import Bidirectional, LSTM, Dropout, Dense, BatchNormalization, Conv1D, MaxPooling1D
from tensorflow.keras.optimizers import AdamW
from tensorflow.keras.metrics import MeanSquaredError
from tensorflow.keras.callbacks import Callback, LearningRateScheduler

import matplotlib.pyplot as plt
from tqdm import tqdm
import numpy as np
import pandas as pd
import json
import os


def lr_schedule(epoch, initial_lr):
    if epoch < 30:
        return 0.0010
    else:
        return 0.0001


def test_model(model, x_test, y_test, decimal_places=[2, 3, 4]):
    y_pred = model.predict(x_test)
    accuracies = []

    for dp in decimal_places:
        y_test_rounded = np.round(y_test, dp)
        y_pred_rounded = np.round(y_pred, dp)
        correct_predictions = np.isclose(y_test_rounded, y_pred_rounded, atol=10**-dp)
        accuracy = np.mean(correct_predictions)
        accuracies.append(accuracy)

        print(f"Accuracy at {dp} decimal places: {accuracy:.5f}")

    avg_accuracy = np.mean(accuracies)
    print(f"Average accuracy: {avg_accuracy:.5f}")
    return avg_accuracy


class EpochCallback(Callback):
    def __init__(self, x_train, y_train, decimal_places=[2, 3, 4]):
        super().__init__()
        self.x_train = x_train
        self.y_train = y_train
        self.decimal_places = decimal_places
        self.losses = []
        self.accuracies = []

    def on_epoch_end(self, epoch, logs=None):
        avg_accuracy = test_model(self.model, self.x_train, self.y_train)
        self.accuracies.append(avg_accuracy)
        self.losses.append(logs.get('loss'))
        print(f"Average accuracy: {avg_accuracy:.5f}")
        return avg_accuracy


class ExchangeRateModel:
    def __init__(self, mode):
        if mode == "train":
            self.mean = None
            self.std = None
            self.model = Sequential()
#             self.model.add(Conv1D(filters=450, kernel_size=3, activation='silu'))
#             self.model.add(MaxPooling1D(pool_size=2))
            self.model.add(Bidirectional(LSTM(50, return_sequences=True), input_shape=(None, 1)))
            self.model.add(Dropout(0.1))
            self.model.add(BatchNormalization())
            self.model.add(Bidirectional(LSTM(50, return_sequences=True)))
            self.model.add(Dropout(0.1))
            self.model.add(BatchNormalization())
            self.model.add(Bidirectional(LSTM(50, return_sequences=True)))
            self.model.add(Dropout(0.1))
            self.model.add(BatchNormalization())
            self.model.add(LSTM(25))
            self.model.add(Dropout(0.1))
            self.model.add(BatchNormalization())
            self.model.add(Dense(50, activation='relu'))
            self.model.add(Dense(1))
            self.model.compile(optimizer=AdamW(), loss='mse')
        else:
            self.model = load_model("model.h5")


    def load_data(self, file_path, column_width=31):
        with open(file_path, 'r') as f:
            lines = f.readlines()

        if isinstance(column_width, str):
            column_width = int(column_width)

        lines = [line for line in lines if len(line.split('\t')) == column_width]

        with open(file_path, 'w') as f:
            f.writelines(lines)

        data = pd.read_csv(file_path, sep='\t', header=None)
        data = data.replace("NA", np.nan)
        data = data.fillna(data.mean())

        if self.mean is None or self.std is None:
            if os.path.isfile('./data/preprocessing_values.json'):
                self.load_preprocessing_values()
            else:
                self.mean = data.mean()
                self.std = data.std()

        data = (data - self.mean) / self.std
        return data.values.tolist()


    def save_preprocessing_values(self):
        if not os.path.exists("./data"):
            os.makedirs("./data")

        with open('./data/preprocessing_values.json', 'w') as f:
            json.dump({'mean': self.mean.tolist(), 'std': self.std.tolist()}, f)


    def load_preprocessing_values(self):
        with open('./data/preprocessing_values.json', 'r') as f:
            values = json.load(f)
        self.mean = pd.Series(values['mean'])
        self.std = pd.Series(values['std'])


    def preprocess_data(self, data):
        data = pd.DataFrame(data)
        data = data.fillna(self.mean)
        data = (data - self.mean) / self.std
        return data.values


    def cross_validate(self, data, n_splits=5):
        from sklearn.model_selection import KFold
        data = np.array(data)
        kfold = KFold(n_splits=n_splits, shuffle=True)
        epochs = [50, 75]
        batch_sizes = [16, 32]

        best_score = float('inf')
        best_params = None

        total_iterations = len(epochs) * len(batch_sizes) * n_splits
        progress_bar = tqdm(total=total_iterations, desc="cross-validation progress")

        for epoch in epochs:
            for batch_size in batch_sizes:
                scores = []
                for train, val in kfold.split(data):
                    x_train, y_train = data[train, :-1], data[train, -1]
                    x_val, y_val = data[val, :-1], data[val, -1]

                    self.train_model(x_train, y_train, epochs=epoch, batch_size=batch_size)
                    score = test_model(self.model, x_val, y_val)
                    scores.append(score)

                    progress_bar.update()

                avg_score = np.mean(scores)
                if avg_score < best_score:
                    best_score = avg_score
                    best_params = (epoch, batch_size)

        progress_bar.close()

        return best_params


    def train_model(self, x_train, y_train, epochs=100, batch_size=32):
        lr_scheduler = LearningRateScheduler(lr_schedule)
        callback = EpochCallback(x_train, y_train)
        self.model.fit(x_train, y_train, epochs=epochs, batch_size=batch_size, callbacks=[callback, lr_scheduler])

        epochs = range(1, len(callback.losses) + 1)
        plt.figure(figsize=(12, 5))

        plt.subplot(1, 2, 1)
        plt.plot(epochs, callback.losses, 'b', label='Training loss')
        plt.title('Training loss')
        plt.xlabel('Epochs')
        plt.ylabel('Loss')
        plt.legend()

        plt.subplot(1, 2, 2)
        plt.plot(epochs, callback.scores, 'y', label='Training Accuracy')
        plt.title('Custom score')
        plt.xlabel('Epochs')
        plt.ylabel('Score')
        plt.legend()

        plt.tight_layout()
        plt.show()
        plt.imsave("history.png")


    def save_model(self):
        self.model.save('model.h5')