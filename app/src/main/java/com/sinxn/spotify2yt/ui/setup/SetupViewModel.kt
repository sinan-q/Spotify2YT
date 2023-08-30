package com.sinxn.spotify2yt.ui.setup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.spotify2yt.api.ResposeGetCode
import com.sinxn.spotify2yt.api.SendCodeRequest
import com.sinxn.spotify2yt.api.YTMGetCode
import com.sinxn.spotify2yt.api.YTMGetToken
import com.sinxn.spotify2yt.api.TokenRequest
import com.sinxn.spotify2yt.api.TokenResponse
import com.sinxn.spotify2yt.repository.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val ytmGetCode: YTMGetCode,
    private val ytmGetToken: YTMGetToken,
    private val sharedPref: SharedPref,
): ViewModel()
{
    var ytmUrl by mutableStateOf("")
    var code: ResposeGetCode = ResposeGetCode()

    fun ytmGetCode() {
        viewModelScope.launch {
            try {
                val res = ytmGetCode.getCode(SendCodeRequest())
                if (res.isSuccessful) {
                    code = res.body()!!
                    ytmUrl = "${code.verification_url}?user_code=${code.user_code}"

                }
            }catch (e: HttpException) {
                Log.d("TAG", "setupSpotify: $e")

            }
        }
    }

    fun getTokenFromCode() {
        viewModelScope.launch {
            try {
                val res = ytmGetToken.getCode(tokenRequest = TokenRequest(code=code.device_code?: ""))
                if (res.isSuccessful) {
                    val data = res.body()?: TokenResponse()
                    sharedPref.accessToken = data.access_token
                    sharedPref.refreshToken = data.refresh_token
                    sharedPref.expiresAt = (System.currentTimeMillis()/1000).toInt() + data.expires_in

                }
            }catch (e: HttpException) {}
        }
    }

    fun setSpotify(spotifyClientId: String, spotifyClientSecret: String) {
        if (spotifyClientId!="" && spotifyClientSecret!="") {
            sharedPref.spotifyClientId = spotifyClientId
            sharedPref.spotifyClientSecret = spotifyClientSecret
        }
    }

}
