package com.sinxn.spotify2yt.ytmibrary.parsers

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.YTAuth
import com.sinxn.spotify2yt.ytmibrary.nav

fun parseSongArtists(data: JsonObject, index: Int): JsonObject {
    val flexItem = getFlexColumnItem(data, index)

    return if (flexItem == JsonObject()) {
        JsonObject()
    } else {
        val runs = flexItem.getAsJsonObject("text").getAsJsonArray("runs")
        parseSongArtistsRuns(runs.toList())
    }
}

fun parseSongArtistsRuns(runs: List<JsonElement>): JsonObject {
    val artists = JsonArray()
    for (j in 0 until (runs.size / 2) + 1) {
        val artist = JsonObject().apply {
            addProperty("name", runs[j * 2].asJsonObject["text"].asString)
            addProperty("id",
                nav(runs[j * 2].asJsonObject, YTAuth.NAVIGATION_BROWSE_ID, true)?.asString ?: ""
            )
        }
        artists.add(artist)
    }
    return artists.asJsonObject
}

fun parseSongRuns(runs: JsonArray): JsonObject {
    val parsed = JsonObject()
    for ((i, run) in runs.withIndex()) {
        if (i % 2 != 0) { // Uneven items are separators
            continue
        }
        val text = run.asJsonObject["text"].asString
        if (run.asJsonObject.has("navigationEndpoint")) { // Artist or album
            val item = JsonObject().apply {
                addProperty("name", text)
                addProperty("id",
                    nav(run.asJsonObject, YTAuth.NAVIGATION_BROWSE_ID, true)?.asString ?: ""
                )
            }
            if (item["id"].asString.startsWith("MPRE") || "release_detail" in item["id"].asString) {
                parsed.add("album", item)
            } else {
                parsed.getAsJsonArray("artists").add(item)
            }
        } else {
            if (text.matches(Regex("^\\d([^ ])* [^ ]*$")) && i > 0) {
                parsed.addProperty("views", text.split(' ')[0])
            } else if (text.matches(Regex("^(\\d+:)*\\d+:\\d+$"))) {
                parsed.addProperty("duration", text)
                parsed.addProperty("duration_seconds", parseDuration(text)) // Replace with actual implementation
            } else if (text.matches(Regex("^\\d{4}$"))) {
                parsed.addProperty("year", text)
            } else {
                parsed.getAsJsonArray("artists").add(JsonObject().apply {
                    addProperty("name", text)
                    add("id", JsonObject())
                })
            }
        }
    }
    return parsed
}

fun parseSongRuns(runs: List<JsonElement>): JsonObject {
    val parsed = JsonObject()
    for ((i, run) in runs.withIndex()) {
        if (i % 2 != 0) { // Uneven items are separators
            continue
        }
        val text = run.asJsonObject["text"].asString
        if (run.asJsonObject.has("navigationEndpoint")) { // Artist or album
            val item = JsonObject().apply {
                addProperty("name", text)
                addProperty("id",
                    nav(run.asJsonObject, YTAuth.NAVIGATION_BROWSE_ID, true)?.asString ?: ""
                )
            }
            if (item["id"].asString.startsWith("MPRE") || "release_detail" in item["id"].asString) {
                parsed.add("album", item)
            } else {
                parsed.getAsJsonArray("artists").add(item)
            }
        } else {
            if (text.matches(Regex("^\\d([^ ])* [^ ]*$")) && i > 0) {
                parsed.addProperty("views", text.split(' ')[0])
            } else if (text.matches(Regex("^(\\d+:)*\\d+:\\d+$"))) {
                parsed.addProperty("duration", text)
                parsed.addProperty("duration_seconds", parseDuration(text)) // Replace with actual implementation
            } else if (text.matches(Regex("^\\d{4}$"))) {
                parsed.addProperty("year", text)
            } else {
                parsed.getAsJsonArray("artists").add(JsonObject().apply {
                    addProperty("name", text)
                    add("id", JsonObject())
                })
            }
        }
    }
    return parsed
}

fun parseSongAlbum(data: JsonObject, index: Int): JsonObject? {
    val flexItem = getFlexColumnItem(data, index)
    return if (flexItem == null) {
        null
    } else {
        JsonObject().apply {
            addProperty("name", getItemText(data, index) ?: "")
            addProperty("id", getBrowseId(flexItem, 0) ?: "")
        }
    }
}
