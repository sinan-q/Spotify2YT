package com.sinxn.spotify2yt.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyApi
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.SpotifyCredentials
import com.sinxn.spotify2yt.repository.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class HomeViewModel @Inject constructor(
   private val sharedPref: SharedPref,
): ViewModel()
{
    private var spotifyAppApi: SpotifyAppApi? = null

    fun onConvert(playlist: String) {
        viewModelScope.launch {
            Log.d("TAG", "initSpotify: ${spotifyAppApi?.playlists?.getPlaylist(playlist)}")

            Log.d("TAG", "initSpotify: ${spotifyAppApi?.playlists?.getPlaylistTracks(playlist)}")
        }


    }

    var isLogged = false

    init {
        isLogged = sharedPref.isLogged()
        if (isLogged) {
            if (sharedPref.expiresAt< System.currentTimeMillis()/1000) {
                //refreshToken()
            }
            initYTM()
            initSpotify()

        }
    }
    private fun initSpotify() {
        viewModelScope.launch {
            val spotifyCred = SpotifyCredentials()
            spotifyCred.clientId = sharedPref.spotifyClientId
            spotifyCred.clientSecret = sharedPref.spotifyClientSecret
            spotifyAppApi = SpotifyAppApiBuilder(spotifyCred).build()
        }

    }
    private fun initYTM() {

    }
}