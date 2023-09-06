package com.sinxn.spotify2yt.repository

import android.content.SharedPreferences
import java.io.File
import javax.inject.Inject

class SharedPref @Inject constructor(
    private val preferences: SharedPreferences,
    private val storage: File

    ) {


    fun isLogged():Boolean {
        return !(
                !File(storage,"auth").exists()
                        || spotifyClientId.isNullOrEmpty()
                        || spotifyClientSecret.isNullOrEmpty()
                )

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