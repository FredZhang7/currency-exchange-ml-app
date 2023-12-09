import sys
from bi_lstm import ExchangeRateModel

file_path = sys.argv[1]
column_width = sys.argv[2]

model = ExchangeRateModel("test")
data = model.load_data(file_path, column_width)

x_test, y_test = data[:, :-1], data[:, -1]

print("x.shape: ", x_test.shape)
print("y.shape: ", y_test.shape)

model.test_model(x_test, y_test)