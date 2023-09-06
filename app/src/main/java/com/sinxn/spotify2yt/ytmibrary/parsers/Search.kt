package com.sinxn.spotify2yt.ytmibrary.parsers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.YTAuth
import com.sinxn.spotify2yt.ytmibrary.nav
import java.util.Locale


fun getSearchResultType(resultTypeLocal: String?, resultTypesLocal: List<String>): String? {
    if (resultTypeLocal.isNullOrEmpty()) {
        return null
    }
    val resultTypes = listOf("artist", "playlist", "song", "video", "station", "profile")
    val resultTypeLocalLower = resultTypeLocal.lowercase(Locale.ROOT)

    val resultType = if (resultTypeLocalLower !in resultTypesLocal) {
        "album"
    } else {
        resultTypes[resultTypesLocal.indexOf(resultTypeLocalLower)]
    }
    return resultType
}

fun parseTopResult(data: JsonObject, searchResultTypes: List<String>): JsonObject {
    val resultType = getSearchResultType(nav(data, YTAuth.SUBTITLE)?.asString, searchResultTypes)
    val searchResult = JsonObject()

    searchResult.addProperty("category", nav(data, YTAuth.CARD_SHELF_TITLE)?.asString)
    searchResult.addProperty("resultType", resultType)

    if (resultType == "artist") {
        val subscribers = nav(data, YTAuth.SUBTITLE2, true)?.asString
        if (subscribers != null) {
            searchResult.addProperty("subscribers", subscribers.split(' ')[0])
        }

        val artistInfo = nav(data, listOf("title", "runs"))?.asJsonArray?.let { parseSongRuns(it) }
        // Update the searchResult with artistInfo

    }

    if (resultType in listOf("song", "video")) {
        val onTap = data["onTap"].asJsonObject
        if (onTap != null) {
            searchResult.addProperty("videoId", nav(onTap, YTAuth.WATCH_VIDEO_ID)?.asString)
            searchResult.addProperty("videoType", nav(onTap, YTAuth.NAVIGATION_VIDEO_TYPE)?.asString)
        }
    }

    if (resultType in listOf("song", "video", "album")) {
        searchResult.addProperty("title", nav(data, YTAuth.TITLE_TEXT)?.asString ?: "")
        val runs =
            nav(data, listOf("subtitle", "runs"))?.asJsonArray?.size()?.let {
                nav(data, listOf("subtitle", "runs"))?.asJsonArray?.toList()
                    ?.subList(2, it)
            }
        val songInfo = runs?.let { parseSongRuns(it) }
        // Update the searchResult with songInfo
    }

    searchResult.add("thumbnails", nav(data, YTAuth.THUMBNAILS, true))

    return searchResult
}

fun getSearchParams(filter: String?, scope: String?, ignoreSpelling: Boolean): String? {
    val filteredParam1 = "EgWKAQ"
    var param1: String? = null
    var param2: String? = null
    var param3: String? = null
    var params: String? = null

    if (filter == null && scope == null && !ignoreSpelling) {
        return params
    }

    if (scope == "uploads") {
        params = "agIYAw%3D%3D"
    }

    if (scope == "library") {
        if (filter != null) {
            param1 = filteredParam1
            param2 = getParam2(filter)
            param3 = "AWoKEAUQCRADEAoYBA%3D%3D"
        } else {
            params = "agIYBA%3D%3D"
        }
    }


    if (scope == null && filter != null) {
        if (filter == "playlists") {
            params = "Eg-KAQwIABAAGAAgACgB"
            params += if (!ignoreSpelling) {
                "MABqChAEEAMQCRAFEAo%3D"
            } else {
                "MABCAggBagoQBBADEAkQBRAK"
            }
        } else if ("playlists" in filter) {
            param1 = "EgeKAQQoA"
            param2 = if (filter == "featured_playlists") "Dg" else "EA"
            param3 =
                if (!ignoreSpelling) "BagwQDhAKEAMQBBAJEAU%3D" else "BQgIIAWoMEA4QChADEAQQCRAF"
        } else {
            param1 = filteredParam1
            param2 = getParam2(filter)
            param3 =
                if (!ignoreSpelling) "AWoMEA4QChADEAQQCRAF" else "AUICCAFqDBAOEAoQAxAEEAkQBQ%3D%3D"
        }
    }

    if (scope == null && filter == null && ignoreSpelling) {
        params = "EhGKAQ4IARABGAEgASgAOAFAAUICCAE%3D"
    }

    return params ?: (param1 + param2 + param3)
}
private fun getParam2(filter: String): String {
    val filterParams = mapOf(
        "songs" to "II",
        "videos" to "IQ",
        "albums" to "IY",
        "artists" to "Ig",
        "playlists" to "Io",
        "profiles" to "JY"
    )
    return filterParams[filter] ?: ""
}

fun parseSearchResult(
    data: JsonObject,
    searchResultTypes: List<String>,
    resultType: String?,
    category: String?
): JsonObject {
    val defaultOffset = if (resultType == null) 2 else 0
    var searchResult = JsonObject().apply { addProperty("category", category) }
    val videoType = nav(data, YTAuth.PLAY_BUTTON + listOf("playNavigationEndpoint") + YTAuth.NAVIGATION_VIDEO_TYPE, true)?.asString

    val updatedResultType = if (resultType == null && videoType != null) {
        if (videoType == "MUSIC_VIDEO_TYPE_ATV") "song" else "video"
    } else {
        resultType
    }

    val finalResultType = updatedResultType ?: getSearchResultType(getItemText(data, 1), searchResultTypes)

    searchResult.addProperty("resultType", finalResultType)

    if (finalResultType != "artist") {
        searchResult.addProperty("title", getItemText(data, 0))
    }

    when (finalResultType) {
        "artist" -> {
            searchResult.addProperty("artist", getItemText(data, 0))
            searchResult = parseMenuPlaylists(data, searchResult)
        }
        "album" -> {
            searchResult.addProperty("type", getItemText(data, 1))
        }
        "playlist" -> {
            val flexItem = getFlexColumnItem(data, 1)["text"].asJsonObject["runs"].asJsonArray
            val hasAuthor = flexItem.size() == defaultOffset + 3
            searchResult.addProperty("itemCount",
                getItemText(data, 1, defaultOffset + (if (hasAuthor) 2 else 0))?.split(' ')?.get(0) ?: ""
            )
            searchResult.addProperty("author", if (!hasAuthor) "" else getItemText(data, 1, defaultOffset))
        }
        // Handle other cases...
    }

    return searchResult
}


// Define other necessary functions and constants...

fun parseSearchResults(results: JsonArray?, searchResultTypes: List<String>, resultType: String?, category: String?): List<JsonObject>{
    val parsedResults = mutableListOf<JsonObject>()

    if (results != null) {
        for (result in results) {
            if (!result.asJsonObject.has(YTAuth.MRLIR)) continue
            val parsedResult = parseSearchResult(result.asJsonObject[YTAuth.MRLIR].asJsonObject, searchResultTypes, resultType, category)
            parsedResults.add(parsedResult)
        }
    }

    return parsedResults
}
