package com.sinxn.spotify2yt.ytmibrary.auth

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sinxn.spotify2yt.ytmibrary.YTAuth
import com.sinxn.spotify2yt.ytmibrary.initializeHeaders
import com.sinxn.spotify2yt.ytmibrary.update
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

fun isOAuth(headers: JsonObject?): Boolean {
    val oauthStructure = setOf("access_token", "expires_at", "expires_in", "token_type", "refresh_token")
    return oauthStructure.all { headers?.has(it) == true }
}

fun isCustomOAuth(headers: JsonObject): Boolean {
    return headers.has("authorization") && headers["authorization"]?.toString()?.startsWith("Bearer ") ?: false
}


class YTMusicOAuth(private val session: OkHttpClient, private val proxies:JsonObject? = null) {


    private fun sendRequest(url: String, data: JsonObject): Response {
        data.addProperty("client_id", YTAuth.OAUTH_CLIENT_ID)
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = data.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("User-Agent", YTAuth.OAUTH_USER_AGENT)
            .build()

        return session.newCall(request).execute()
    }

    private fun parseToken(response: Response, refreshToken: String? = null): JsonObject {
        val tokenObj = JsonParser().parse(response.body.string()).asJsonObject
        if (!tokenObj.has("refreshToken") && refreshToken!=null) tokenObj.addProperty("refreshToken",refreshToken)
        tokenObj.addProperty("expires_at", (System.currentTimeMillis() / 1000) + getExpiresIn(tokenObj))
        return tokenObj
    }

    private fun getExpiresIn(tokenObj: JsonObject?): Long {
        return if (tokenObj != null && tokenObj.has("expires_in")) {
            tokenObj.get("expires_in").asLong
        } else 0

    }

    private fun loadToken(filepath: String): JsonObject? {
        val file = File(filepath)
        if (file.exists()) {
            try {
                val content = String(Files.readAllBytes(Paths.get(filepath)))
                return JsonParser().parse(content).asJsonObject
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun saveToken(token: JsonObject, filepath: String) {
        try {
            val jsonStr = token.toString()
            File(filepath).writeText(jsonStr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getCode(): JsonObject {
        val codeResponse =
            sendRequest(YTAuth.OAUTH_CODE_URL, JsonObject().apply { addProperty("scope", YTAuth.OAUTH_SCOPE) })
        return JsonParser().parse(codeResponse.body.string())?.asJsonObject ?: JsonObject()
    }

    private fun getTokenFromCode(deviceCode: String): JsonObject {
        val response = sendRequest(
            YTAuth.OAUTH_TOKEN_URL,
            JsonObject().apply {
                addProperty("client_secret", YTAuth.OAUTH_CLIENT_SECRET)
                addProperty("grant_type", "http://oauth.net/grant_type/device/1.0")
                addProperty("code", deviceCode)
            }
        )
        return parseToken(response)
    }

    private fun refreshToken(refreshToken: String): JsonObject {
        val response = sendRequest(
            YTAuth.OAUTH_TOKEN_URL,
            JsonObject().apply {
                addProperty("client_secret", YTAuth.OAUTH_CLIENT_SECRET)
                addProperty("grant_type", "refresh_token")
                addProperty("refresh_token", refreshToken)
            }
        )
        return parseToken(response,refreshToken)
    }

    fun setup(filepath: String? = null, openBrowser: Boolean = false): JsonObject {
        val code = getCode()
        val url = "${code["verification_url"]}?user_code=${code["user_code"]}"

        TODO()
        val token = getTokenFromCode(code["device_code"].asString)
        if (filepath != null) {
            saveToken(token, filepath)
        }
        return token
    }


    fun loadHeaders(token: JsonObject, filepath: String): JsonObject {
        val headers = initializeHeaders()
        if (System.currentTimeMillis() / 1000 > token["expires_at"].asLong - 3600) {
            token.update(refreshToken(token["refresh_token"].asString))
            saveToken(token, filepath)
        }
        headers.addProperty("Authorization","${token["token_type"]} ${token["access_token"]}")
        headers.addProperty("Content-Type", "application/json")
        headers.addProperty("X-Goog-Request-Time",System.currentTimeMillis() / 1000)
        return headers
    }
}
