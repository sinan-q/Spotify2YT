package com.sinxn.spotify2yt.ytmibrary.auth

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.initializeHeaders
import com.sinxn.spotify2yt.ytmibrary.update
import java.io.File

fun loadHeadersFile(auth: String): JsonObject {
    val gson = Gson()
    val inputJson: JsonObject = if (File(auth).isFile) {
        File(auth).bufferedReader().use {
            gson.fromJson(it, JsonObject::class.java)
        }
    } else {
        gson.fromJson(auth, JsonObject::class.java)
    }
    return inputJson
}

suspend fun prepareHeaders(
    session: okhttp3.OkHttpClient,
    proxies: JsonObject? = null,
    inputMap: JsonObject? = null
): JsonObject {
    val headers = JsonObject()

    if (inputMap != null) {
        if (isOAuth(inputMap)) {
            val oauth = YTMusicOAuth(session, proxies)
            val authHeaders = oauth.loadHeaders(inputMap, inputMap["filepath"].asString)
            headers.update(authHeaders)
        } else if (isCustomOAuth(inputMap)) {
            headers.update(inputMap)
        } else {
            throw Exception("Could not detect credential type. Please ensure your oauth or browser credentials are set up correctly.")
        }
    } else {
        // No authentication
        headers.update(initializeHeaders())
    }

    return headers
}
