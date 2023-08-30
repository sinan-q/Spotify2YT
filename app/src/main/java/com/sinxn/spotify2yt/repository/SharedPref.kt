package com.sinxn.spotify2yt.repository

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPref @Inject constructor(
    private val preferences: SharedPreferences,

    ) {

    fun isLogged():Boolean {
        return preferences.getString("isLogged","false") != "false"
    }

    var accessToken: String?
        get() = preferences.getString("access_token",null)
        set(token) {
            preferences.edit().putString("access_token", token).apply()
        }
    var refreshToken: String?
        get() = preferences.getString("refresh_token",null)
        set(token) {
            preferences.edit().putString("refresh_token", token).apply()
        }
    var expiresAt: Int
        get() = preferences.getInt("expires_at",0)
        set(token) {
            preferences.edit().putInt("expires_at", token).apply()
        }

    var spotifyClientId: String?
        get() = preferences.getString("spotify_client_id",null)
        set(token) {
            preferences.edit().putString("spotify_client_id", token).apply()
        }
    var spotifyClientSecret: String?
        get() = preferences.getString("spotify_client_secret",null)
        set(token) {
            preferences.edit().putString("spotify_client_secret", token).apply()
        }
}