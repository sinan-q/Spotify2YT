package com.sinxn.youtify.ui.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.youtify.api.ResposeGetCode
import com.sinxn.youtify.api.SendCodeRequest
import com.sinxn.youtify.api.TokenRequest
import com.sinxn.youtify.api.YTMGetCode
import com.sinxn.youtify.api.YTMGetToken
import com.sinxn.youtify.repository.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val ytmGetCode: YTMGetCode,
    private val ytmGetToken: YTMGetToken,
    private val sharedPref: SharedPref,
    private val storage: File
): ViewModel()
{
    var uiState by mutableStateOf(SetupUiState())
        private set
    fun onEvent(event: SetupEvent) {
        when(event) {
            is SetupYoutubeEvent.GetCode -> {
                viewModelScope.launch {
                    try {
                        val res = ytmGetCode.getCode(SendCodeRequest())
                        if (res.isSuccessful) {
                            res.body()?.let {
                                uiState = uiState.copy(
                                    code = it,
                                    ytmUrl = "${it.verification_url}?user_code=${it.user_code}")
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
                        val res = ytmGetToken.getCode(tokenRequest = TokenRequest(code=uiState.code.device_code?: ""))
                        if (res.isSuccessful) {
                            val data = res.body().toString()
                            val file = File(storage,"auth")
                            file.writeText(data)
                        }
                    }catch (error: HttpException) {
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
    var code: ResposeGetCode = ResposeGetCode(),

    val error: String? = null,
)
