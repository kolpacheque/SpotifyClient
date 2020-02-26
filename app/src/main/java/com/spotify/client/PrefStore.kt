package com.spotify.client

import android.content.Context
import android.content.SharedPreferences

class PrefStore constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("shared preferences", 0)

    fun setAuthToken(authToken: String) {
        sharedPreferences.edit()
            .putString("token", "Bearer $authToken")
            .apply()
    }

    fun getAuthToken(): String {
        return sharedPreferences.getString("token", "") ?: ""
    }

    fun removeAuthToken() {
        sharedPreferences.edit()
            .remove("token")
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString("token", "") != ""
    }

}