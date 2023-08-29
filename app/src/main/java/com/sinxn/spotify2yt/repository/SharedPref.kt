package com.sinxn.spotify2yt.repository

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPref @Inject constructor(
    private val preferences: SharedPreferences,

    ) {

    fun isLogged():Boolean {
        return preferences.getString("isLogged","false") != "false"
    }
}