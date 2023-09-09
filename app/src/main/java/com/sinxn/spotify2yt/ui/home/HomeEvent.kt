package com.sinxn.spotify2yt.ui.home

import com.sinxn.spotify2yt.domain.model.Playlists
import com.sinxn.spotify2yt.domain.model.Tracks

sealed class HomeEvent {
    data class OnConvert(val playlistUrl: String): HomeEvent()
    object UploadPlayList: HomeEvent()
    data class OnSelect(val play: Playlists): HomeEvent()
    data class OnDelete(val play: Playlists): HomeEvent()

    data class ErrorDisplayed(val error: String): HomeEvent()
    data class OnSongAction(val songEvent: SongEvent) : HomeEvent()

}
sealed class SongEvent {
    data class Play(val play: Tracks?): SongEvent()
    data class Reload(val track: Tracks): SongEvent()
}
