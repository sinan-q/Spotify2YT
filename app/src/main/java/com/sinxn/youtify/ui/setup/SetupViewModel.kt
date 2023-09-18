package com.sinxn.youtify.ui.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.youtify.repository.SharedPref
import com.sinxn.youtify.ytmibrary.YTMusic
import com.sinxn.youtify.ytmibrary.auth.YTMusicOAuth
import com.sinxn.youtify.ytmibrary.auth.YoutubeApiGetCodeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val sharedPref: SharedPref,
    private val storage: File
): ViewModel()
{
    var uiState by mutableStateOf(SetupUiState())
        private set
    fun onEvent(event: SetupEvent) {
        when(event) {
            is SetupYoutubeEvent.Init -> {
                viewModelScope.launch {
                    try {
                        val file = File(storage,"auth")
                        if (sharedPref.spotifyClientId!=null && sharedPref.spotifyClientSecret!=null)
                            uiState.completed.add(SetupStatus.SPOTIFY)
                        if (file.exists()) uiState.completed.add(SetupStatus.YOUTUBE)
                        else {
                            val res = YTMusicOAuth().getCode()
                            if (res.isSuccess) {
                                uiState = uiState.copy(
                                    code = res,
                                    ytmUrl = "${res.verification_url}?user_code=${res.user_code}"
                                )

                            }
                        }
                    }catch (error: HttpException) {
                        uiState = uiState.copy(
                            error = error.message
                        )
                    }
                }
            }

            is SetupYoutubeEvent.GetToken -> {
                viewModelScope.launch {
                    try {
                        require(uiState.code?.device_code!=null)
                        val res = YTMusicOAuth().getTokenFromCode(uiState.code?.device_code!!)

                        val data = res.toString()
                        val file = File(storage,"auth")
                        file.writeText(data)
                        uiState.completed.add(SetupStatus.YOUTUBE)


                    }catch (error: Exception) {
                        uiState = uiState.copy(
                            error = error.message
                        )
                    }
                }
            }
            is SetupSpotifyEvent.OnCred -> {
                try {
                    require(event.clientId!="")
                    require(event.clientSecret!="")
                    sharedPref.spotifyClientId = event.clientId
                    sharedPref.spotifyClientSecret = event.clientSecret
                }
                catch (error: Exception) {
                    uiState = uiState.copy(
                        error = error.message
                    )
                }
            }

            is SetupEvent.OnError -> {
                uiState = uiState.copy(
                    error = null
                )
            }
        }
    }

}

data class SetupUiState (
    var ytmUrl:String = "",
    var code: YoutubeApiGetCodeResponse?=null,
    val ytmApi: YTMusic = YTMusic(),
    val completed: SnapshotStateList<SetupStatus> = SnapshotStateList(),

    val error: String? = null,
)
enum class SetupStatus {
    YOUTUBE,SPOTIFY,NONE
}