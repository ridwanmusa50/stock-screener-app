# 📈 Stock Screener App

A modern and intuitive Android application that helps users explore stock listings, manage a personalized watchlist, and analyze 12-month stock trends—all in one place.

---

## ✨ Features

- ✔️ Browse all listed stocks and ETFs  
- ⭐ Add favorite stocks to your custom watchlist  
- 📊 View detailed company overviews and historical price charts  
- ⚡ Fast local caching with graceful fallback for offline support  

---

## 🚀 Getting Started

### 🔧 Project Setup

1. **Create a `values.properties` file** at the root of the project.
2. Add the following required fields:

   ```properties
   SIGN_KEY_ALIAS=stock_screener
   SIGN_PW=NoorR&7!dwanG3n*+T3q
   API_ACCESS_KEY=YOUR_OWN_KEY


🔑 Replace YOUR_OWN_KEY with your personal API key.

**📌 Note: You can get your API key for free from Alpha Vantage: [AlphaVantage](https://www.alphavantage.co/)**

---

## 🏗️ Architecture Overview

The app follows the **MVVM (Model–View–ViewModel)** architecture for better modularity, testability, and separation of concerns.

---

### 🧠 MVVM Breakdown

- **🔹 Model**  
  Handles business logic, API communication, and local data access (Room & DataStore).

- **🔹 ViewModel**  
  Serves as the bridge between Model and View. It exposes state through `StateFlow` and handles UI logic.

- **🔹 View (Jetpack Compose)**  
  Observes state from ViewModel and displays it reactively using Composables.

---

### 🔄 Multi-Source Data Strategy

To ensure fast, reliable access to stock data with fallback mechanisms, the app uses **three integrated data sources**:

#### 1. 📡 **Remote API** – *Primary Source*

- Fetches real-time stock data from [Alpha Vantage](https://www.alphavantage.co/)
- Used for:
  - Company overviews
  - Monthly historical stock prices
  - List stocks
  - Filter stocks by symbol

#### 2. 🏛️ **Room Database** – *Local Persistence*

- Caches all listed stocks locally after fetching from API
- Adds support for:
  - Marking stocks as **favorites**
  - Searching and filtering **offline**
  - Fallback data when API rate limits are reached

#### 3. 🧾 **DataStore** – *Lightweight Preferences Storage*

- Stores metadata such as:
  - **Timestamp** of the last monthly stock data fetch
- Helps determine **when to refresh** data for favourited stocks to avoid wasting api limit

---

### 🧩 Fallback & Filtering Logic

The app smartly adapts when the free API limit is hit:

1. ✅ **Primary Load** – From API
2. 🔄 **Fallback** – From Room (cached stocks with favourite flags)
3. 📁 **Final Backup** – From local CSV asset embedded in the project

> 💡 If the API fails or rate limits are exceeded, the app **automatically filters** and processes data from Room to maintain core functionality.

---

### 📊 Stock Filtering Flow

- **Online**: Data is fetched and filtered via API (e.g., sector filter)
- **Offline / Rate Limited**: Filtering is performed on locally cached data in **Room**

---

### 📦 Tech Components Summary

| Layer          | Technology Used      | Purpose                                      |
|----------------|----------------------|----------------------------------------------|
| Model          | Retrofit, Room, DataStore | Handles network, local storage, and preferences |
| ViewModel      | Kotlin Coroutines, StateFlow | UI logic and state management                 |
| View           | Jetpack Compose       | UI rendering based on state                  |

---

By combining modern Android tools with clean architecture principles, the app ensures **robust offline support**, **use**


⚠️ This app uses a free-tier API, which may hit rate limits.
To avoid this, use the **devDevelopmentDebug** build variant with demo data enabled.

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
