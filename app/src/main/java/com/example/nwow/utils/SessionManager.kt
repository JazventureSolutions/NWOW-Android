package com.example.nwow.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) {
            prefs.edit().putString(KEY_USERNAME, value).apply()
        }

    var name: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) {
            prefs.edit().putString(KEY_NAME, value).apply()
        }

    var role: String?
        get() = prefs.getString(KEY_ROLE, null)
        set(value) {
            prefs.edit().putString(KEY_ROLE, value).apply()
        }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            prefs.edit().putString(KEY_TOKEN, value).apply()
        }

    val isAdmin: Boolean
        get() = role == ROLE_ADMIN

    val isLoggedIn: Boolean
        get() = !token.isNullOrEmpty()

    fun save(login: com.example.nwow.ui.auth.model.LoginResponse) {
        prefs.edit()
            .putString(KEY_USERNAME, login.username)
            .putString(KEY_NAME, login.name)
            .putString(KEY_ROLE, login.role)
            .putString(KEY_TOKEN, login.token)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "nwow_session"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAME = "name"
        private const val KEY_ROLE = "role"
        private const val KEY_TOKEN = "token"

        const val ROLE_ADMIN = "admin"
    }
}
