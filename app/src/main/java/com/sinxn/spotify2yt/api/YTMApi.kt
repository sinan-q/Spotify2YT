package com.sinxn.spotify2yt.api

import com.sinxn.spotify2yt.tools.YTAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface YTMGetCode {

    @Headers("User-Agent:${YTAuth.OAUTH_USER_AGENT}")
    @POST("/o/oauth2/device/code")
    suspend fun getCode(@Body sendCodeRequest: SendCodeRequest): Response<ResposeGetCode>

}
interface YTMGetToken {

    @Headers("User-Agent:${YTAuth.OAUTH_USER_AGENT}")
    @POST("/token")
    suspend fun getCode(@Body tokenRequest: TokenRequest): Response<TokenResponse>

}

data class SendCodeRequest(
    val client_id: String = YTAuth.OAUTH_CLIENT_ID,
    val scope:  String = YTAuth.OAUTH_SCOPE,
)
data class TokenRequest(
    val client_id: String = YTAuth.OAUTH_CLIENT_ID,
    val client_secret: String = YTAuth.OAUTH_CLIENT_SECRET,
    val grant_type: String = "http://oauth.net/grant_type/device/1.0",
    val code: String,
)
data class TokenResponse(
    val access_token: String? = null,
    val expires_in: Int = 0,
    val refresh_token: String? =null,
    val scope: String? = null,
    val token_type: String? = null
)
data class ResposeGetCode(
    val client_id: String = YTAuth.OAUTH_CLIENT_ID,
    val device_code: String? = null,
    val user_code: String? =null,
    val verification_url: String? = null,
)