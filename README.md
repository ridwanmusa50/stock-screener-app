# 📈 Stock Screener App

A modern stock market app built with Android, allowing users to explore listings, manage a watchlist, and analyze stock trends over time.

---

## ⏳ Features

This application offers the following functionality:

- ✅ Display all listed stocks and ETFs
- ✅ Add favorite stocks to a personalized watchlist
- ✅ View stock overviews and 12-month historical charts

---

## 🚀 How to Set Up the Project

1. **Create a `values.properties` file** in the root of the project.

2. Add the following **required values**:

   ```properties
   SIGN_KEY_ALIAS=stock_screener
   SIGN_PW=NoorR&7!dwanG3n*+T3q
   API_ACCESS_KEY=YOUR_OWN_KEY

🔑 Replace YOUR_OWN_KEY with your personal API key.
**📌 Note: You can get your API key for free from Alpha Vantage: [AlphaVantage](https://www.alphavantage.co/)**

---

## ⚙️ Architecture Overview
**MVVM (Model–View–ViewModel)**
The app follows the MVVM architecture, separating data, UI, and business logic. This structure improves scalability, maintainability, and testability.

*Model:* Responsible for handling API responses and business logic.
*ViewModel:* Manages UI-related data in a lifecycle-conscious way.
*View (Composable):* Displays UI based on state from the ViewModel.


Currently the data loaded for list stocks using 3 levels from top to bottom:
- API response
- Local saved from previous api response
- Local csv file in the project

---

## 🔧 Planned Improvements

## 🎨 UI/UX Enhancements
1. Show more details in the company overview: sector and top holders.
2. Add pinch-to-zoom for chart interactivity.
3. Use different app icons for each build variant (Dev/Prod).
4. Display skeleton loaders while data is loading.

## 🧠 Code Enhancements
1. Standardize naming conventions for variables, strings, and functions.
2. Introduce screen-level event classes to reduce parameter passing.
3. Improve local sorting using query optimization rather than manual sorting.
4. Enhance error handling by customizing API error messages per case.
