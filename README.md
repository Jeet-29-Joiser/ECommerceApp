# 🛒 SHOP.CO – E-Commerce Android App

A modern, scalable E-Commerce Android application built using **Kotlin**, following **MVVM architecture**, and integrating **REST APIs**, **offline support**, and **clean UI/UX design**.

---

## 🚀 Features

### 🏠 Home Screen
- Dynamic product listing from API
- Category-based product sections
- Modern banner UI
- Horizontal and grid product layouts

### 📂 Categories
- Dynamic categories fetched from API
- Category-wise product listing
- Grid-based responsive UI

### 📦 Product Details
- Detailed product information
- Image gallery (multiple images)
- Rating, price, discount display
- Add to cart UI (basic)

### 🔐 Authentication
- Login & Register screens
- Session management using SharedPreferences
- Dynamic header:
  - Shows **Login** when user is not logged in
  - Shows **Username** when logged in
- Logout confirmation dialog

### 🌐 API Integration
- REST API using Retrofit
- DummyJSON / Fake Store API support
- Dynamic product & category fetching

### 📶 Offline Support (Production Level)
- Room Database for caching products
- Shows cached data when offline
- Automatic fallback strategy:
  - API → Save → Display
  - Error → Load from cache

### ⚠️ Error Handling (Production Ready)
- Network timeout handling
- HTTP error handling (404, 500, etc.)
- Graceful fallback messages
- Empty state handling
- No crashes (safe try-catch implementation)

---

## 🏗️ Tech Stack

- **Language:** Kotlin  
- **Architecture:** MVVM  
- **Dependency Injection:** Hilt  
- **Networking:** Retrofit + Gson  
- **Database:** Room  
- **Async:** Coroutines  
- **UI:** XML + RecyclerView + Navigation Component  
- **Image Loading:** Glide  

---

## 📂 Project Structure

com.example.ecommerceapp
│
├── data
│ ├── api
│ ├── db
│ ├── repository
│ └── mapper
│
├── domain
│ └── model
│
├── ui
│ ├── home
│ ├── category
│ ├── detail
│ ├── auth
│ └── state
│
├── di
├── utils
└── MainActivity.kt

---

## 📡 API Used

- https://dummyjson.com/products
 
---

## 🔥 Key Highlights

- Clean and scalable architecture  
- Offline-first approach  
- Production-level error handling  
- Dynamic UI rendering  
- Reusable components  
- Smooth navigation with Navigation Component  

---

## 📱 Screenshots


## ⚙️ Setup Instructions

1. Clone the repository:

2. Open in Android Studio  

3. Sync Gradle  

4. Run on Emulator / Device  

---

## 🔑 Future Improvements

- Cart functionality (Room-based)  
- Payment integration  
- Wishlist feature  
- Profile screen  
- Firebase authentication  
- Pagination support  
- Push notifications  

---

## 👨‍💻 Developed By

**Jeet Joiser**

GitHub: https://github.com/Jeet-29-Joiser  

---

## 📄 License

This project is for educational purposes.
