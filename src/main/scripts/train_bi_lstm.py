import sys
from bi_lstm import ExchangeRateModel

file_path = sys.argv[1]

model = ExchangeRateModel("train")
data = model.load_data(file_path)

x, y = data[:, :-1], data[:, -1]

model.train_model(X, y)
model.save_model()
model.save_preprocessing_values()
