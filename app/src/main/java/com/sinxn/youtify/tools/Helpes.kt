package com.sinxn.youtify.tools

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.youtify.domain.model.Tracks
import java.util.Locale
import kotlin.math.abs

object DebugUtilis {
    var _charLimit = 2000

    @JvmStatic
    fun v(tag: String?, message: String): Int {
        // If the message is less than the limit just show
        if (message.length < _charLimit) {
            return Log.v(tag, message)
        }
        val sections = message.length / _charLimit
        for (i in 0..sections) {
            val max = _charLimit * (i + 1)
            if (max >= message.length) {
                Log.v(tag, message.substring(_charLimit * i))
            } else {
                Log.v(tag, message.substring(_charLimit * i, max))
            }
        }
        return 1
    }
}
fun getTopResult(res: JsonArray, track: Tracks): String? {
    for(it in res){ it as JsonObject
        if (it["category"].asString.equals("Top result") && it.has("videoId") && !it["videoId"].asString.isNullOrEmpty())
            return it["videoId"].asString
        else break
    }
    return getBestFitSongId(res,track)
}
fun getBestFitSongId(ytmResults: JsonArray, spoti: Tracks): String? {
    val matchScore = hashMapOf<String, Float>()
    val titleScore = hashMapOf<String, Float>()

    for (ytm in ytmResults) { ytm as JsonObject
        val resultType = if (ytm.has("resultType")) ytm["resultType"].asString else ""
        val title = if (ytm.has("title")) ytm["title"].asString else ""
        if (resultType !in listOf("song", "video") || title.isNullOrEmpty()) {
            continue
        }
        val videoId = if (ytm.has("videoId") && !ytm["videoId"].isJsonNull) ytm["videoId"].asString else continue

        val duration = if (ytm.has("duration") && !ytm["duration"].isJsonNull) ytm["duration"].asString else null
        val artists = if (ytm.has("artists")) ytm.getAsJsonArray("artists")
            ?.joinToString(" ") { it.asJsonObject.get("name").asString } else ""


        if (videoId.isNullOrEmpty()) continue

        val durationMatchScore: Float? = if (duration != null) {
            val durationItems = duration.split(":")
            val durationValue = durationItems[0].toInt() * 60 + durationItems[1].toInt()
            1f - (abs(durationValue * 1000 - spoti.durationMs) * 2 / spoti.durationMs.toFloat())
        } else {
            null
        }

        val sanitizedTitle = if (resultType == "video") {
            val titleSplit = title.split("-")
            if (titleSplit.size == 2) titleSplit[1].trim() else title
        } else title

        titleScore[videoId] = sanitizedTitle.lowercase(Locale.ROOT)
            .similarityTo(spoti.name.lowercase(Locale.ROOT))
        val scores = mutableListOf(
            titleScore[videoId] ?: 0f,
            spoti.artists.joinToString { it.name + " " }.lowercase(Locale.ROOT)
                .let { artists?.lowercase(Locale.ROOT)?.similarityTo(it) } ?: 0f
        )
        durationMatchScore?.let { scores.add((it * 5)) }
        matchScore[videoId] = ((scores.average() *  if (resultType == "song") 2 else 1).toFloat())
    }

    if (matchScore.isEmpty()) return null
    return matchScore.maxByOrNull { it.value }?.key
}