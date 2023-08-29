package com.sinxn.spotify2yt.ui.home

import androidx.lifecycle.ViewModel
import com.sinxn.spotify2yt.repository.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    sharedPref: SharedPref
): ViewModel()
{
    var isLogged = false

    init {
        isLogged = sharedPref.isLogged()
    }
}