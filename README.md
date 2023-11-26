# Currency Exchange Application

## Overview
This project uses Swing for the UI, MySQL and TSV files for historical currency data storage, JFreeChart for line graphs, TensorFlow (Python) for predictive analysis, JUnit for testing, and Requires/Modifies/Effects for documentation.

## Features

### 1. Currency Convertor (Swing UI)

<img src="./data/phase3_ui.png" alt="Phase 3 UI" width="100%">

Users use this app to convert an amount of money from one currency (e.g. CAD) to another (e.g. USD), using data from JSON locally or live TSV data online.
Users make two selections from **31 currencies** locally or **78 currencies** online, enter the amount they want to convert in the "from" currency section, and the app will automatically display the converted amount in the "to" currency section.
This app is useful to users such as international students, travelers, and people interested in currency exchange.

### 2. Historical Currency Values (MySQL)
Stores TSV data in `Map<String, Map<String, String>>` format by inserting unique rows of Currency (String), Date (String), and Value (String: Double | "NA").

### 3. Line Graphs (JFreeChart)


### 4. Predictive Analysis (TensorFlow)




### User Stories for Currency Convertor (Swing UI)

-  As a user, I want to be able to add an exchange to a list that shows the history of exchanges.

-  As a user, I want to be able to view the list of all my previous currency exchanges.

-  As a user, I want to be able to select two currency names for the "from" and "to" currency selections, respectively, manually enter an amount in the "from" currency section, and then see the converted amount in the "to" currency section.

-  As a user, I want to be able to clear the history of currency exchanges.

-  As a user, I want to be able to load the history of currency exchanges from a JSON file (if I choose to do so).

-  As a user, I want to be able to save the updated history of exchanges to a JSON file (if I choose to do so).

-  As a user, I want to be able to add multiple exchanges to a list of previous exchanges.

-  As a user, I want to be able to load and save the state of the application.

