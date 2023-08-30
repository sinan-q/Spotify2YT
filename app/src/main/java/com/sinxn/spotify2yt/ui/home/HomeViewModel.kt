package com.sinxn.spotify2yt.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyApi
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.SpotifyCredentials
import com.adamratzman.spotify.models.PlaylistTrack
import com.sinxn.spotify2yt.repository.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Playlist(
    val id: String,
    val collaborative: Boolean,
    val followers: Followers,
    val images: List<Image>,
    val tracks: Tracks
)

data class Followers(
    val total: Int
)

data class Image(
    val height: Int,
    val width: Int
)


data class Tracks(
    val total: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
   private val sharedPref: SharedPref,
): ViewModel()
{
    private var spotifyAppApi: SpotifyAppApi? = null
    var playlistSongs: List<PlaylistTrack> = emptyList()
    var playlistName: String = ""

    fun onConvert(playlist: String) {
        viewModelScope.launch {
            val playlistId = playlist.trim('/').split('/').last()
            val res = spotifyAppApi?.playlists?.getPlaylist(playlistId)
            res?.let {
                playlistSongs = res.tracks.items
                playlistName = res.name
            }


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