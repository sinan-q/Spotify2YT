package com.sinxn.youtify.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.SpotifyCredentials
import com.adamratzman.spotify.models.Track
import com.sinxn.youtify.data.repository.PlaylistRepository
import com.sinxn.youtify.domain.model.Playlists
import com.sinxn.youtify.domain.model.Tracks
import com.sinxn.youtify.repository.SharedPref
import com.sinxn.youtify.tools.getTopResult
import com.sinxn.youtify.ytmibrary.YTMusic
import com.sinxn.youtify.ytmibrary.mixins.PlaylistMixin
import com.sinxn.youtify.ytmibrary.mixins.SearchMixin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPref: SharedPref,
    private val storage: File,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    var spotifyAppApi: SpotifyAppApi? = null
    var uiState by mutableStateOf(UiState())
        private set
    var isLogged = false

    fun init() {
        isLogged = sharedPref.isLogged()
        if (isLogged) {
            viewModelScope.launch {
                if (spotifyAppApi==null) {
                    try {
                        val spotifyCred = SpotifyCredentials().apply {
                            clientId = sharedPref.spotifyClientId
                            clientSecret = sharedPref.spotifyClientSecret
                        }
                        spotifyAppApi = SpotifyAppApiBuilder(spotifyCred).build()

                        uiState = uiState.copy(
                            ytmApi = YTMusic(File(storage, "auth").path),
                            playlists = playlistRepository.getAllPlaylists().toMutableStateList()
                        )
                    }catch (e:Exception) {
                        uiState = uiState.copy(
                            error = e.message
                        )
                    }


                }
            }

        }
    }
    fun onEvent(event: PlaylistEvent){
        when (event) {
            is PlaylistEvent.OnConvert -> {
                viewModelScope.launch {
                    if (spotifyAppApi==null) init()
                    val playlistId = event.playlistUrl.trim('/').split('/').last().split('?').first()
                    val res = spotifyAppApi?.playlists?.getPlaylist(playlistId)
                    res?.let {
                        uiState = uiState.copy(
                            playlist = Playlists(res.name, playlistId, null, res.owner, res.description, res.followers.total, res.primaryColor, res.images[0].url, res.tracks.total, false, System.currentTimeMillis())
                        )
                        res.tracks.items.forEach { playlistTrack ->
                            playlistTrack.track?.asTrack?.let {
                                val track = getTrack(it)
                                uiState.playlistSongs.add(track)
                                getYTSong(track)
                            }
                        }
                    }
                    uiState.playlist?.let { playlistRepository.addPlaylistWithTracks(it,uiState.playlistSongs) }
                }
            }
            is PlaylistEvent.OnSelect -> {
                viewModelScope.launch {
                    uiState = uiState.copy(
                        playlist = event.play,
                        playlistSongs = playlistRepository.getTracks(playlistId = event.play.id)
                    )
                }
            }
            is PlaylistEvent.Upload -> {
                viewModelScope.launch {
                    if (event.title.isEmpty()) {
                        uiState = uiState.copy(error = "Title is Empty")
                        return@launch
                    }
                    val videoIds = event.videoIds.mapNotNull { it.youtube_id }
                    if (videoIds.isEmpty()) {
                        uiState = uiState.copy(error = "videoIds is Empty")
                        return@launch
                    }
                    var sourcePlaylist: String? = event.sourcePlaylist
                    if (sourcePlaylist.isNullOrEmpty()) sourcePlaylist = null
                    try {
                        val ytPlaylistId = PlaylistMixin(yTMusic = uiState.ytmApi!!).createPlaylist(
                            event.title, event.description, event.privacyStatus, videoIds, sourcePlaylist
                        )
                        uiState.playlist?.id?.let { playlistRepository.updateYoutubePlaylistId(it,ytPlaylistId) }
                    }catch (_: Exception) {

                    }

                }

            }

            is PlaylistEvent.OnDelete -> {
                viewModelScope.launch {
                    playlistRepository.deletePlaylist(event.play)
                    uiState.playlists.remove(event.play)
                }

            }
            is PlaylistEvent.ErrorDisplayed -> {
                uiState = uiState.copy(
                    error = event.error
                )
            }
            is PlaylistEvent.Play -> {
                uiState = uiState.copy(
                    play = "https://music.youtube.com/playlist?list=${event.play.youtube_id}"
                )
            }
            is SongEvent.Play -> {
                if (event.play?.youtube_id != null) {
                    uiState = uiState.copy(
                        play = "https://music.youtube.com/watch?v=${event.play.youtube_id}"
                    )
                } else {
                    viewModelScope.launch {
                        event.play?.let { getYTSong(it) }
                    }
                }
            }
            is SongEvent.Reload -> {
                uiState.playlistSongs.findLast { it == event.track}?.youtube_id = null
            }
        }
    }

    private fun getTrack(it: Track): Tracks {
        return Tracks(name = it.name, spotify_id = it.id, album_id = it.album.id,
            artists = it.artists, discNumber = it.discNumber, durationMs = it.durationMs.toLong(),
            explicit = it.explicit, popularity = it.popularity, parent_id = 0
        )

    }


    private suspend fun getYTSong(track: Tracks) {
        val songName = track.name.replace(Regex(" \\(feat.*\\..+\\)"), "")
        var query = "${track.artists.joinToString { it.name + " " }}$songName "
        query = query.replace("&", "")
        val res = uiState.ytmApi?.let { SearchMixin(it).search(query) }
        if (res != null && res.size() > 0) {
            val targetSongId = getTopResult(res,track)
            if (!targetSongId.isNullOrEmpty()) {
                playlistRepository.updateYoutubeId(track.id, targetSongId)
                track.youtube_id = targetSongId
            }
        }
    }

}

data class UiState (
    val playlistSongs: SnapshotStateList<Tracks> = mutableStateListOf(),
    val playlist: Playlists? = null,
    val playlists: SnapshotStateList<Playlists> = mutableStateListOf(),
    val ytmApi: YTMusic? = null,

    val error: String? = null,
    val play: String? = null
    )

