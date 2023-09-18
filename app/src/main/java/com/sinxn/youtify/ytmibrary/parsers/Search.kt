package com.sinxn.youtify.ytmibrary.parsers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.youtify.ytmibrary.YTAuth
import com.sinxn.youtify.ytmibrary.findObjectByKey
import com.sinxn.youtify.ytmibrary.nav
import com.sinxn.youtify.ytmibrary.subList
import com.sinxn.youtify.ytmibrary.update
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
        if (artistInfo != null) {
            searchResult.update(artistInfo)
        }

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
                nav(data, listOf("subtitle", "runs"))?.asJsonArray?.subList(2, it)
            }
        val songInfo = runs?.let { parseSongRuns(it) }
        songInfo?.let { searchResult.update(it) }
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
            val flexItem = getFlexColumnItem(data, 1)?.get("text")?.asJsonObject?.get("runs")?.asJsonArray
            val hasAuthor = flexItem?.size() == defaultOffset + 3
            searchResult.addProperty("itemCount",
                getItemText(data, 1, defaultOffset + (if (hasAuthor) 2 else 0))?.split(' ')?.get(0) ?: ""
            )
            searchResult.addProperty("author", if (!hasAuthor) "" else getItemText(data, 1, defaultOffset))
        }
        "station" -> {
            searchResult.addProperty("videoId", nav(data, YTAuth.NAVIGATION_VIDEO_ID)?.asString )
            searchResult.addProperty("playlistId", nav(data, YTAuth.NAVIGATION_PLAYLIST_ID)?.asString)
        }
        "profile" -> {
            searchResult.addProperty("name", getItemText(data, 1, 2, true))
        }
        "song" -> {
            searchResult.add("album", null)
            if (data.has("menu")) {
                val toggleMenu = findObjectByKey(nav(data, YTAuth.MENU_ITEMS)?.asJsonArray, YTAuth.TOGGLE_MENU)
                if (toggleMenu != null && toggleMenu!= JsonObject()) {
                    searchResult.add("feedbackTokens", parseSongMenuTokens(toggleMenu))
                }
            }
        }
        "upload" -> {
            val browseId = nav(data, YTAuth.NAVIGATION_BROWSE_ID, true)?.asString
            if (browseId == null) {
                val flexItems = (0 until 2).mapNotNull { i ->
                    val flexItem = getFlexColumnItem(data, i)
                    nav(flexItem!!, listOf("text", "runs"), true)?.asJsonArray
                }

                if (flexItems.isNotEmpty()) {
                    val flexItem0 = flexItems[0].get(0).asJsonObject
                    searchResult.addProperty("videoId", nav(flexItem0, YTAuth.NAVIGATION_VIDEO_ID, true)?.asString)
                    searchResult.addProperty("playlistId", nav(flexItem0, YTAuth.NAVIGATION_PLAYLIST_ID, true)?.asString)
                }

                if (flexItems.size > 1) {
                    val runs = parseSongRuns(flexItems[1])
                    runs.entrySet().forEach { (key, value) -> searchResult.add(key, value) }
                }

                searchResult.addProperty("resultType", "song")
            } else {
                searchResult.addProperty("browseId", browseId)
                if (browseId.contains("artist")) {
                    searchResult.addProperty("resultType", "artist")
                } else {
                    val flexItem1 = getFlexColumnItem(data, 1)
                    val runs =
                        (flexItem1?.get("text")?.asJsonObject?.get("runs")?.asJsonArray)?.mapNotNull { run ->
                            run.asJsonObject["text"]?.asString
                        }

                    if (runs != null) {
                        if (runs.size > 1) {
                            searchResult.addProperty("artist", runs[1])
                        }
                    }

                    if (runs != null) {
                        if (runs.size > 2) {
                            searchResult.addProperty("releaseDate", runs[2])
                        }
                    }

                    searchResult.addProperty("resultType", "album")
                }
            }
        }
    }
    if (finalResultType in listOf("song", "video")) {
        searchResult.addProperty("videoId", nav(data, YTAuth.PLAY_BUTTON + listOf("playNavigationEndpoint", "watchEndpoint", "videoId"), true)?.asString)
        searchResult.addProperty("videoType", videoType)
    }

    if (finalResultType in listOf("song", "video", "album")) {
        searchResult.add("duration", null)
        searchResult.add("year", null)
        val flexItem = getFlexColumnItem(data, 1)
        val runs = flexItem?.getAsJsonObject("text")?.getAsJsonArray("runs")?.subList(defaultOffset)
        val songInfo = parseSongRuns(runs)
        searchResult.update(songInfo)
    }

    if (finalResultType in listOf("artist", "album", "playlist", "profile")) {
        searchResult.add("browseId", nav(data, YTAuth.NAVIGATION_BROWSE_ID, true))
    }

    if (finalResultType in listOf("song", "album")) {
        searchResult.addProperty("isExplicit", nav(data, YTAuth.BADGE_LABEL, true) != null)
    }

    searchResult.add("thumbnails", nav(data, YTAuth.THUMBNAILS, true))
    return searchResult
}


// Define other necessary functions and constants...

fun parseSearchResults(results: JsonArray?, searchResultTypes: List<String>, resultType: String?, category: String?): JsonArray {
    val parsedResults = JsonArray()
    if (results != null) {
        for (result in results) {
            if (!result.asJsonObject.has(YTAuth.MRLIR)) continue
            val parsedResult = parseSearchResult(result.asJsonObject[YTAuth.MRLIR].asJsonObject, searchResultTypes, resultType, category)
            parsedResults.add(parsedResult)
        }
    }

    return parsedResults
}
