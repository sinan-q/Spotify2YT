package com.sinxn.spotify2yt.ytmibrary

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.YTAuth
import okhttp3.Headers
import okhttp3.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.text.NumberFormat
import java.time.LocalDateTime

fun initializeHeaders(): JsonObject {
    val headers = JsonObject().apply {
        addProperty("user-agent",YTAuth.USER_AGENT)
        addProperty("accept", "*/*")
        addProperty("accept-encoding", "gzip, deflate")
        addProperty("content-type", "application/json")
        addProperty("origin", YTAuth.YTM_DOMAIN)
    }

    return headers
}

fun initializeContext(): JsonObject {
    val currentTime = LocalDateTime.now()
    val clientVersion = "1.${DateTimeFormatter.ofPattern("yyyyMMdd", Locale.US).format(currentTime)}.01.00"
    val context = JsonObject()
    val client = JsonObject()
    client.addProperty("clientName", "WEB_REMIX")
    client.addProperty("clientVersion", clientVersion)
    context.add("client", client)
    val user = JsonObject()
    context.add("user", user)
    val root = JsonObject()
    root.add("context", context)
    return root
}

suspend fun getVisitorId(requestFunc:suspend (String) -> Response): String {
    val response = requestFunc(YTAuth.YTM_DOMAIN)
    val responseBody = response.body.string()
    val matcher = Regex("""ytcfg\.set\s*\(\s*(\{.+?\})\s*\)\s*;""").find(responseBody)

    return matcher?.groupValues?.get(1) ?: ""
}

fun sapisidFromCookie(rawCookie: String): String {
    val cookies = rawCookie.replace("\"", "").split("; ")
    for (cookie in cookies) {
        val parts = cookie.split("=")
        if (parts.size == 2 && parts[0] == "__Secure-3PAPISID") {
            return parts[1]
        }
    }
    return ""
}

fun getAuthorization(auth: String): String {
    val unixTimestamp = Instant.now().epochSecond.toString()
    val input = "$unixTimestamp $auth"
    val sha1 = try {
        val md = MessageDigest.getInstance("SHA-1")
        md.digest(input.toByteArray())
    } catch (e: NoSuchAlgorithmException) {
        ByteArray(0)
    }
    val hexString = StringBuilder()
    for (byte in sha1) {
        val hex = Integer.toHexString(0xFF and byte.toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return "SAPISIDHASH $unixTimestamp" + "_" + hexString.toString()
}

fun toInt(string: String): Int {
    val normalizedString = java.text.Normalizer.normalize(string, java.text.Normalizer.Form.NFKD)
    val numberString = normalizedString.replace(Regex("\\D"), "")
    return try {
        NumberFormat.getInstance().parse(numberString)?.toInt() ?: 0
    } catch (e: Exception) {
        numberString.replace(",", "").toIntOrNull() ?: 0
    }
}

fun sumTotalDuration(item: JsonObject): Int {
    if (!item.has("tracks")) {
        return 0
    }
    val tracks = item.getAsJsonArray("tracks")
    var totalDuration = 0
    for (track in tracks) {
        val durationSeconds = track.asJsonObject.get("duration_seconds")?.asInt ?: 0
        totalDuration += durationSeconds
    }
    return totalDuration
}

operator fun JsonObject.plus(second: JsonObject): JsonObject {
    val mergedJsonObject = JsonObject()

    for ((key, value) in this.entrySet()) {
        mergedJsonObject.add(key, value)
    }

    for ((key, value) in second.entrySet()) {
        mergedJsonObject.add(key, value)
    }

    return mergedJsonObject
}

fun JsonObject.toHeaders(): Headers {
    val builder = Headers.Builder()

    for ((key, value) in entrySet()) {
        builder.add(key, value.toString())
    }

    return builder.build()
}

fun JsonObject.update(target: JsonObject) {
    for ((key, value) in target.entrySet()) {
        add(key, value)
    }
}
fun JsonArray.subList(startIndex: Int, endIndex: Int? = null): JsonArray {
    val sublist = JsonArray()
    val end = endIndex ?: size()
    for (i in startIndex until end) {
        sublist.add(this[i])
    }
    return sublist
}
