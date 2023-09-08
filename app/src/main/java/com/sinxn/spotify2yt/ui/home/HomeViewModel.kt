package com.sinxn.spotify2yt.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.SpotifyCredentials
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.Track
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.data.repository.PlaylistRepository
import com.sinxn.spotify2yt.domain.model.Playlists
import com.sinxn.spotify2yt.domain.model.Tracks
import com.sinxn.spotify2yt.repository.SharedPref
import com.sinxn.spotify2yt.tools.similarityTo
import com.sinxn.spotify2yt.tools.ytId
import com.sinxn.spotify2yt.ui.home.DebugUtilis.v
import com.sinxn.spotify2yt.ytmibrary.YTMusic
import com.sinxn.spotify2yt.ytmibrary.mixins.SearchMixin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPref: SharedPref,
    private val storage: File,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set
    fun onEvent(event: HomeEvent){
        when (event) {
            is HomeEvent.OnConvert -> {
                viewModelScope.launch {
                    val playlistId = event.playlistUrl.trim('/').split('/').last()
                    val res = uiState.spotifyAppApi?.playlists?.getPlaylist(playlistId)
                    res?.let {
                        uiState = uiState.copy(
                            playlist = Playlists(res.name, playlistId, null, res.owner, res.description, res.followers.total, res.primaryColor, res.images[0].url, res.tracks.total, false, System.currentTimeMillis())
                        )
                        res.tracks.items.forEach { playlistTrack ->
                            playlistTrack.track?.asTrack?.let {
                                uiState.playlistSongs.add(
                                    Tracks(
                                        name = it.name,
                                        spotify_id = it.id,
                                        album_id = it.album.id,
                                        artists = it.artists,
                                        discNumber = it.discNumber,
                                        durationMs = it.durationMs.toLong(),
                                        explicit = it.explicit,
                                        popularity = it.popularity,
                                        parent_id = 0
                                    )
                                )
                            }
                        }
                    }
                    getYTSongs()
                    savePlaylist()
                }
            }
            is HomeEvent.OnSelect -> {
                viewModelScope.launch {
                    uiState = uiState.copy(
                        playlist = event.play,
                        playlistSongs = playlistRepository.getTracks(playlistId = event.play.id)
                    )
                }
            }
            is HomeEvent.UploadPlayList -> {

            }

            is HomeEvent.OnDelete -> {
                viewModelScope.launch {
                    playlistRepository.deletePlaylist(event.play)
                    uiState.playlists.remove(event.play)
                }

            }
            is HomeEvent.ErrorDisplayed -> {
                uiState = uiState.copy(
                    error = event.error
                )
            }
        }
    }


    private suspend fun getYTSongs() {
        uiState.playlistSongs.forEach { track ->
            val songName = track.name.replace(Regex(" \\(feat.*\\..+\\)"), "")
            var query = track.artists.joinToString { it.name + " " } + songName
            query = query.replace("&", "")
            val res = uiState.ytmApi?.let { SearchMixin(it).search(query) }
            if (res != null && res.size() > 0) {
                val targetSongId = getTopResult(res)
                if (!targetSongId.isNullOrEmpty()) track.youtube_id = targetSongId
            }
        }

    }


    private fun getTopResult(res: JsonArray): String? {
        res.forEach {
            it as JsonObject
            if (it["category"].asString.equals("Top result"))
                return if (it.has("videoId"))
                    it["videoId"].asString
                else null
        }
        return null
    }

    var isLogged = false

    init {
        isLogged = sharedPref.isLogged()
        if (isLogged) {
            viewModelScope.launch {
                val spotifyCred = SpotifyCredentials().apply {
                    clientId = sharedPref.spotifyClientId
                    clientSecret = sharedPref.spotifyClientSecret
                }
                uiState = uiState.copy(
                    ytmApi = YTMusic(File(storage, "auth").path),
                    spotifyAppApi = SpotifyAppApiBuilder(spotifyCred).build(),
                    playlists = playlistRepository.getAllPlaylists().toMutableList()
                )
            }

        }
    }

    fun savePlaylist() {
        viewModelScope.launch {
            uiState.playlist?.let { playlistRepository.addPlaylistWithTracks(it,uiState.playlistSongs) }
        }

    }

    fun uploadPlaylist() {
        TODO("Not yet implemented")
    }
}

data class UiState (
    val playlistSongs: MutableList<Tracks> = mutableListOf(),
    val playlist: Playlists? = null,
    val playlists: MutableList<Playlists> = mutableListOf(),
    val spotifyAppApi: SpotifyAppApi? = null,
    val ytmApi: YTMusic? = null,

    val error: String? = null,

    )

object DebugUtilis {
    var _charLimit = 2000

    @JvmStatic
    fun v(tag: String?, message: String): Int {
        // If the message is less than the limit just show
        if (message.length < _charLimit) {
            return Log.v(tag, message)
        }
        val sections = message.length / _charLimit
        for (i in 0..sections) {
            val max = _charLimit * (i + 1)
            if (max >= message.length) {
                Log.v(tag, message.substring(_charLimit * i))
            } else {
                Log.v(tag, message.substring(_charLimit * i, max))
            }
        }
        return 1
    }
}