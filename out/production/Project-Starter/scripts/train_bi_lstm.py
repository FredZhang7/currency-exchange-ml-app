import sys
from bi_lstm import ExchangeRateModel

file_path = sys.argv[1]

model = ExchangeRateModel("train")
data = model.load_data(file_path)

x_train, y_train = data[:, :-1], data[:, -1]

print("x.shape: ", x_train.shape)
print("y.shape: ", y_train.shape)

model.train_model(x_train, y_train)
model.save_model()
model.save_preprocessing_values()
