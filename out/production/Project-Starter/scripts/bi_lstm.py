from tensorflow.keras.models import Sequential, load_model
from tensorflow.keras.layers import Bidirectional, LSTM, Dropout, Dense
from tensorflow.keras.optimizers import AdamW
import pandas as pd
import os

class ExchangeRateModel:
    def __init__(self, mode):
        if mode == "train":
            self.model = Sequential([
                Bidirectional(LSTM(50, return_sequences=True), input_shape=(None, 1)),
                Dropout(0.2),
                BatchNormalization(),
                Bidirectional(LSTM(50, return_sequences=True)),
                Dropout(0.2),
                BatchNormalization(),
                Bidirectional(LSTM(50)),
                Dropout(0.2),
                BatchNormalization(),
                Dense(1)
            ])
            self.model.compile(optimizer=AdamW(), loss='mse')
        else:
            self.model = load_model("model.h5")

    def load_data(self, file_path):
        with open(file_path, 'r') as f:
            lines = f.readlines()

        lines = [line for line in lines if len(line.split('\t')) == 1187]

        with open(file_path, 'r') as f:
            f.writelines(lines)

        data = pd.read_csv(file_path, sep='\t', header=None)
        data = data.fillna(data.mean())

        if self.mean is None:
            if os.path.isfile('./data/preprocessing_values.json'):
                self.load_preprocessing_values()
            else:
                self.mean = data.mean()
                self.std = data.std()

        data = (data - self.mean) / self.std

        return data.values.tolist()

    def save_preprocessing_values(self):
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

    def train_model(self, x_train, y_train, epochs=100, batch_size=32):
        self.model.fit(x_train, y_train, epochs=epochs, batch_size=batch_size, verbose=0)

    def save_model(self):
        self.model.save('model.h5')