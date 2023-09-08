package com.sinxn.spotify2yt.ui.home

import com.sinxn.spotify2yt.domain.model.Playlists

sealed class HomeEvent {
    data class OnConvert(val playlistUrl: String): HomeEvent()
    object UploadPlayList: HomeEvent()
    data class OnSelect(val play: Playlists): HomeEvent()
    data class OnDelete(val play: Playlists): HomeEvent()

    data class ErrorDisplayed(val error: String): HomeEvent()
}
