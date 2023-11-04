package com.sinxn.youtify.ui.home

import com.sinxn.youtify.domain.model.Playlists
import com.sinxn.youtify.domain.model.Tracks

sealed class PlaylistEvent {
    data class OnConvert(val playlistUrl: String): PlaylistEvent()
    data class Upload(
        val title: String,
        val description: String,
        val videoIds: List<Tracks>,
        val privacyStatus: String = "PRIVATE",
        val sourcePlaylist: String? = null): PlaylistEvent()
    data class OnSelect(val play: Playlists): PlaylistEvent()
    data class OnDelete(val play: Playlists): PlaylistEvent()
    data class Play(val play: Playlists): PlaylistEvent()

    data class ErrorDisplayed(val error: String?): PlaylistEvent()

}
sealed class SongEvent {
    data class Play(val play: Tracks?): PlaylistEvent()
    data class Reload(val track: Tracks): PlaylistEvent()
}
