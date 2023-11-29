import sys
import numpy as np
from bi_lstm import ExchangeRateModel

file_path = sys.argv[1]
column_width = sys.argv[2]

model = ExchangeRateModel("train")
data = model.load_data(file_path, column_width)

# best_epochs, best_batch_size = model.cross_validate(data)
# print(f"Best epochs: {best_epochs}, Best batch size: {best_batch_size}")

data = np.array(data)
x_train, y_train = data[:, :-1], data[:, -1]

print("x.shape: ", x_train.shape)
print("y.shape: ", y_train.shape)

# model.train_model(x_train, y_train, epochs=best_epochs, batch_size=best_batch_size)
model.train_model(x_train, y_train, epochs=50, batch_size=50)
model.save_model()
model.save_preprocessing_values()
