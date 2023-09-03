package com.sinxn.spotify2yt.ytmibrary.parsers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.YTAuth
import com.sinxn.spotify2yt.ytmibrary.nav

fun parseContentList(results: JsonArray, parseFunc: (JsonObject) -> JsonObject, key: String = YTAuth.MTRIR): JsonArray {
    val contents = JsonArray()
    for (result in results) {
        contents.add(parseFunc(result.asJsonObject[key].asJsonObject))
    }
    return contents
}

fun parseAlbum(result: JsonObject):JsonObject {
    return JsonObject().apply {
        add("title", nav(result, YTAuth.TITLE_TEXT))
        add("year", nav(result, YTAuth.SUBTITLE2, true))
        add("browseId", nav(result, YTAuth.TITLE + YTAuth.NAVIGATION_BROWSE_ID))
        add("thumbnails", nav(result, YTAuth.THUMBNAIL_RENDERER))
        addProperty("isExplicit", (nav(result, YTAuth.SUBTITLE_BADGE_LABEL, true) != JsonObject()))
    }
}

fun parseSingle(result: JsonObject): JsonObject {
    return JsonObject().apply {
        addProperty("title", nav(result, YTAuth.TITLE_TEXT)?.asString)
        addProperty("year", nav(result, YTAuth.SUBTITLE, true)?.asString)
        addProperty("browseId", nav(result, YTAuth.TITLE + YTAuth.NAVIGATION_BROWSE_ID)?.asString)
        addProperty("thumbnails", nav(result, YTAuth.THUMBNAIL_RENDERER)?.asString)
    }
}

fun parseSong(result: JsonObject): JsonObject {
    val song = JsonObject().apply {
        addProperty("title", nav(result, YTAuth.TITLE_TEXT)?.asString)
        addProperty("videoId", nav(result, YTAuth.NAVIGATION_VIDEO_ID)?.asString)
        addProperty("playlistId", nav(result, YTAuth.NAVIGATION_PLAYLIST_ID, true)?.asString)
        addProperty("thumbnails", nav(result, YTAuth.THUMBNAIL_RENDERER)?.asString)
    }

    return song
}

// Define nav and parseSongRuns functions
fun parseSongFlat(data: JsonObject): JsonObject {
    val columns = (0 until (data["flexColumns"].asJsonArray).size())
        .map { getFlexColumnItem(data, it) }

    val song = JsonObject().apply {
        addProperty("title", nav(columns[0], YTAuth.TEXT_RUN_TEXT)?.asString)
        addProperty("videoId", nav(columns[0], YTAuth.TEXT_RUN + YTAuth.NAVIGATION_VIDEO_ID, true)?.asString)
        add("artists", parseSongArtists(data, 1)) // Replace with actual implementation
        add("thumbnails", nav(data, YTAuth.THUMBNAILS))
        addProperty("isExplicit", (nav(data, YTAuth.BADGE_LABEL, true) != JsonObject()))
    }

    if (columns.size > 2 && columns[2] != JsonObject() && nav(columns[2], YTAuth.TEXT_RUN)?.asJsonObject?.has("navigationEndpoint") == true) {
        val albumObject = JsonObject().apply {
            addProperty("name", nav(columns[2], YTAuth.TEXT_RUN)?.asString)
            addProperty("id", nav(columns[2], YTAuth.TEXT_RUN+ YTAuth.NAVIGATION_BROWSE_ID)?.asString)
        }
        song.add("album", albumObject)
    } else {
        song.addProperty("views", nav(columns[1], listOf("text", "runs", -1, "text"))?.asString?.split(' ')
            ?.get(0)
            ?:"")
    }

    return song
}

fun parseVideo(result: JsonObject): JsonObject {
    val runs = nav(result, YTAuth.SUBTITLE_RUNS)?.asJsonArray
    val artistsLen = runs?.let { getDotSeparatorIndex(it) } // Replace with actual implementation

    val video = JsonObject().apply {
        addProperty("title", nav(result, YTAuth.TITLE_TEXT)?.asString)
        addProperty("videoId", nav(result, YTAuth.NAVIGATION_VIDEO_ID)?.asString)
        add("artists", artistsLen?.let { parseSongArtistsRuns(runs.take(it)) }) // Replace with actual implementation
        addProperty("playlistId", nav(result, YTAuth.NAVIGATION_PLAYLIST_ID, true)?.asString)
        add("thumbnails", nav(result, YTAuth.THUMBNAIL_RENDERER, true))
        addProperty("views",
            runs?.get(runs.size() - 1)?.asJsonObject?.get("text")?.asString?.split(' ')?.get(0) ?: ""
        )
    }

    return video
}

fun parsePlaylist(data: JsonObject): JsonObject {
    val playlist = JsonObject().apply {
        addProperty("title", nav(data, YTAuth.TITLE_TEXT)?.asString)
        addProperty("playlistId",
            (nav(data, YTAuth.TITLE+YTAuth.NAVIGATION_BROWSE_ID))?.asString?.substring(2) ?: ""
        )
        add("thumbnails", nav(data, YTAuth.THUMBNAIL_RENDERER))
    }

    val subtitle = data.getAsJsonObject("subtitle")
    if (subtitle.has("runs")) {
        val description = subtitle.getAsJsonArray("runs").joinToString("") { run ->
            run.asJsonObject["text"].asString
        }
        if (subtitle.getAsJsonArray("runs").size() == 3 && nav(data, YTAuth.SUBTITLE2)?.asString?.let {
                Regex("\\d+ ").find(
                    it
                )
            } != null) {
            playlist.addProperty("count",
                nav(data, YTAuth.SUBTITLE2)?.asString?.split(' ')?.get(0) ?: ""
            )
            playlist.add("author", parseSongArtistsRuns(subtitle.getAsJsonArray("runs").take( 1))) // Replace with actual implementation
        }
    }

    return playlist
}

fun parseRelatedArtist(data: JsonObject): JsonObject {
    val subscribers = nav(data, YTAuth.SUBTITLE, true)?.asString?.split(' ')?.get(0)

    val relatedArtist = JsonObject().apply {
        addProperty("title", nav(data, YTAuth.TITLE_TEXT)?.asString)
        addProperty("browseId", nav(data, YTAuth.TITLE+YTAuth.NAVIGATION_BROWSE_ID)?.asString)
        addProperty("subscribers", subscribers)
        add("thumbnails", nav(data, YTAuth.THUMBNAIL_RENDERER))
    }

    return relatedArtist
}

fun parseWatchPlaylist(data: JsonObject): JsonObject {
    val watchPlaylist = JsonObject().apply {
        addProperty("title", nav(data, YTAuth.TITLE_TEXT)?.asString)
        addProperty("playlistId", nav(data, YTAuth.NAVIGATION_WATCH_PLAYLIST_ID)?.asString)
        add("thumbnails", nav(data, YTAuth.THUMBNAIL_RENDERER))
    }

    return watchPlaylist
}

// Define nav, parseSongArtists, parseSongArtistsRuns, getFlexColumnItem, getDotSeparatorIndex, etc.
