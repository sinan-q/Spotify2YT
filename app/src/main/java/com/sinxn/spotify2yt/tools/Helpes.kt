package com.sinxn.spotify2yt.tools

import com.adamratzman.spotify.models.Track
import com.google.gson.JsonObject
import java.util.Locale
import kotlin.math.abs

fun getBestFitSongId(ytmResults: List<JsonObject>, spoti: Track?): String? {
    require(spoti != null) { "spotify Track is null" }

    val matchScore = hashMapOf<String, Int>()
    val titleScore = hashMapOf<String, Int>()

    for (ytm in ytmResults) {
        val resultType = ytm.get("resultType")?.asString
        val title = ytm.get("title")?.asString
        val duration = ytm.get("duration")?.asString
        val videoId = ytm.get("videoId")?.asString
        val artists = ytm.getAsJsonArray("artists")
            ?.joinToString(" ") { it.asJsonObject.get("name").asString }
        val album = ytm.getAsJsonObject("album")?.get("name")?.asString

        if (resultType !in listOf("song", "video") || title.isNullOrEmpty()) {
            continue
        }
        require(videoId != null) { "$ytm video Id null" }


        val durationMatchScore: Int? = if (duration != null) {
            val durationItems = duration.split(":")
            val durationValue = durationItems[0].toInt() * 60 + durationItems[1].toInt()
            1 - abs(durationValue - spoti.durationMs) * 2 / spoti.durationMs
        } else {
            null
        }

        val sanitizedTitle = if (resultType == "video") {
            val titleSplit = title.split("-")
            if (titleSplit.size == 2) {
                titleSplit[1].trim()
            } else {
                title
            }
        } else {
            title
        }
        titleScore[videoId] = sanitizedTitle.lowercase(Locale.ROOT)
            .similarityTo(spoti.name.lowercase(Locale.ROOT))
        val scores = mutableListOf(
            titleScore[videoId] ?: 0,
            spoti.artists.joinToString { it.name + " " }.lowercase(Locale.ROOT)
                .let { artists?.lowercase(Locale.ROOT)?.similarityTo(it) } ?: 0
        )

        durationMatchScore?.let {
            scores.add(it * 5)
        }

        if (resultType == "song" && album != null) {
            scores.add(
                album.lowercase(Locale.ROOT).similarityTo(
                    spoti.album.name.lowercase(
                        Locale.ROOT
                    )
                )
            )
        }

        matchScore[videoId] =
            (scores.average() * maxOf(1, if (resultType == "song") 2 else 1)).toInt()
    }

    if (matchScore.isEmpty()) {
        return null
    }

    return matchScore.maxByOrNull { it.value }?.key
}