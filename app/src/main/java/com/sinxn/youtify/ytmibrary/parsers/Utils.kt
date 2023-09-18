package com.sinxn.youtify.ytmibrary.parsers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.youtify.ytmibrary.YTAuth
import com.sinxn.youtify.ytmibrary.findObjectsByKey
import com.sinxn.youtify.ytmibrary.nav

fun parseMenuPlaylists(data: JsonObject, result: JsonObject): JsonObject {
    val watchMenu = nav(data, YTAuth.MENU_ITEMS)?.asJsonArray?.let { findObjectsByKey(it, "menuNavigationItemRenderer").asJsonArray }

    if (watchMenu != null) {
        for (item in watchMenu.map { it.asJsonObject["menuNavigationItemRenderer"].asJsonObject }) {
            val icon = nav(item, listOf("icon", "iconType"))?.asString

            val watchKey: String = when (icon) {
                "MUSIC_SHUFFLE" -> "shuffleId"
                "MIX" -> "radioId"
                else -> continue
            }

            val watchId = nav(item, listOf("navigationEndpoint", "watchPlaylistEndpoint", "playlistId"), true)
            if (watchId == JsonObject()) nav(item, listOf("navigationEndpoint", "watchEndpoint", "playlistId"), true)

            if (watchId != null) {
                if (!watchId.isJsonNull) {
                    if (watchId != null) {
                        result.addProperty(watchKey, watchId.asString)
                    }
                }
            }
        }
    }
    return result
}


fun getItemText(item: JsonObject, index: Int, runIndex: Int = 0, noneIfAbsent: Boolean = false): String? {
    val column = getFlexColumnItem(item, index) ?: return null

    if (noneIfAbsent && column["text"].asJsonObject["runs"].asJsonArray.size() < runIndex + 1) {
        return null
    }

    return column["text"].asJsonObject["runs"].asJsonArray[runIndex].asJsonObject["text"].asString
}

fun getFlexColumnItem(item: JsonObject, index: Int): JsonObject? {
    val flexColumns = item["flexColumns"].asJsonArray
    if (flexColumns.size() <= index ||
        !flexColumns[index].asJsonObject.has("musicResponsiveListItemFlexColumnRenderer") ||
        !flexColumns[index].asJsonObject["musicResponsiveListItemFlexColumnRenderer"]
            .asJsonObject["text"].asJsonObject.has("runs")) {
        return null
    }

    return flexColumns[index].asJsonObject["musicResponsiveListItemFlexColumnRenderer"]?.asJsonObject
}

fun getFixedColumnItem(item: JsonObject, index: Int): JsonObject? {
    val fixedColumns = item["fixedColumns"].asJsonArray
    if (!fixedColumns[index].asJsonObject["musicResponsiveListItemFixedColumnRenderer"]
            .asJsonObject["text"].asJsonObject.has("runs")) {
        return null
    }

    return fixedColumns[index].asJsonObject["musicResponsiveListItemFixedColumnRenderer"].asJsonObject
}
fun getBrowseId(item: JsonObject, index: Int): String? {
    return if (!item.getAsJsonObject("text").getAsJsonArray("runs")[index].asJsonObject.has("navigationEndpoint")) {
        null
    } else {
        nav(item.getAsJsonObject("text").getAsJsonArray("runs")[index].asJsonObject, YTAuth.NAVIGATION_BROWSE_ID)?.asString
    }
}

fun getDotSeparatorIndex(runs: JsonArray): Int {
    val separator = JsonObject().apply { addProperty("text", " • ") }
    return try {
        runs.indexOf(separator)
    } catch (e: NoSuchElementException) {
        runs.size()
    }
}

fun parseDuration(duration: String?): Int? {
    if (duration == null) {
        return null
    }
    val increments = listOf(1, 60, 3600)
    val timeUnits = duration.split(":").reversed().map { it.toInt() }
    return increments.zip(timeUnits).sumOf { (multiplier, time) -> multiplier * time }
}

