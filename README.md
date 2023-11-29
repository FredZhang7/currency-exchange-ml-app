# Currency Exchange Application

## Overview
This project uses Swing for the UI, MySQL and TSV files for historical currency data storage, JFreeChart for line graphs, TensorFlow (Python) for predictive analysis, JUnit for testing, and Requires/Modifies/Effects for documentation.

## Features

### 1. Currency Convertor (Swing UI)

<img src="./data/phase3_ui.png" alt="Phase 3 UI" width="90%">

Users use this app to convert an amount of money from one currency (e.g. CAD) to another (e.g. USD), using data from JSON locally or live TSV data online.
Users make two selections from **31 currencies** locally or **38 currencies** online, enter the amount they want to convert in the "from" currency section, and the app will automatically display the converted amount in the "to" currency section.
This app is useful to users such as international students, travelers, and people interested in currency exchange.

### 2. Historical Currency Values (MySQL)

| Currency             | Date              | Value    |
|----------------------|-------------------|----------|
| Israeli New Shekel   | April 01, 2022    | 3.203000 |
| Israeli New Shekel   | April 03, 2023    | 3.593000 |
| Israeli New Shekel   | April 04, 2022    | 3.209000 |
| Brunei dollar        | July 08, 2022     | NA       |
| Brunei dollar        | July 10, 2023     | 1.349000 |
| Brunei dollar        | July 11, 2022     | NA       |

*A sample database structure used in this project.*

Stores TSV data in `Map<String, Map<String, String>>` format by inserting unique rows of Currency (String), Date (String), and Value (String: Double | "NA").
Safely retrieves data using stored procedures with parameters in SQL scripts or parametrized queries in Java, to prevent SQL injection attacks.

### 3. Line Graphs (JFreeChart)


### 4. Time Series Prediction (TensorFlow)
Uses Java's `InputStreamReader` to download monthly historical currency values from 2019 to the present in TSV format.
Prepares the training data by merging the monthly data, cleaning all rows, replacing "NA" values with column means, and finally normalizing the data by dividing each data point by subtracting the mean of each column and dividing by the column's std.

First, designs and tests several model architectures with Bidirectional LSTM at their core.
Next, scikit-learn is used to cross-validate with 5 K-folds to find the most appropriate training epoch and batch size.
This project uses a novel approach to calculate the training and testing accuracy: rounding data points and predicted values to two, three, and four decimal places for a more reasonable evaluation.
Finally, I use matplotlib to plot the historical training accuracy and loss over each epoch to clearly see how our model learns over time.


### User Stories for Currency Convertor (Swing UI)

-  As a user, I want to be able to add an exchange to a list that shows the history of exchanges.

-  As a user, I want to be able to view the list of all my previous currency exchanges.

-  As a user, I want to be able to select two currency names for the "from" and "to" currency selections, respectively, manually enter an amount in the "from" currency section, and then see the converted amount in the "to" currency section.

-  As a user, I want to be able to clear the history of currency exchanges.

-  As a user, I want to be able to load the history of currency exchanges from a JSON file (if I choose to do so).

-  As a user, I want to be able to save the updated history of exchanges to a JSON file (if I choose to do so).

-  As a user, I want to be able to add multiple exchanges to a list of previous exchanges.

-  As a user, I want to be able to load and save the state of the application.

