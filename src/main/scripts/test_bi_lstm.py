import sys
import pandas as pd
import numpy as np
from bi_lstm import ExchangeRateModel

model = ExchangeRateModel("test")
model.load_preprocessing_values()
new_data = np.array(eval(sys.argv[1]))
new_data = new_data.reshape(1, -1)
data = model.preprocess_data(new_data)

# predictions is 2D
predictions = model.model.predict(data)
print(predictions[0][0])