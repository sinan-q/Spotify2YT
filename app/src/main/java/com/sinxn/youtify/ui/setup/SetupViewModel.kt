package com.sinxn.youtify.ui.setup

import android.util.Log
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
                    val data = res.body().toString()
                    val file = File(storage,"auth")
                    file.writeText(data)


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
