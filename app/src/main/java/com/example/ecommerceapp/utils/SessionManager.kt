package com.example.ecommerceapp.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(name: String) {
        prefs.edit()
            .putBoolean("isLoggedIn", true)
            .putString("username", name)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }

    fun getUserName(): String {
        return prefs.getString("username", "") ?: ""
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}