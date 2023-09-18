package com.sinxn.youtify.ui.setup

sealed class SetupEvent {
    data class OnError(val error: String?) : SetupEvent()
}
sealed class SetupYoutubeEvent {
    object Init: SetupEvent()
    object GetToken: SetupEvent()
}
sealed class SetupSpotifyEvent {
    data class OnCred(val clientId: String, val clientSecret: String): SetupEvent()
}