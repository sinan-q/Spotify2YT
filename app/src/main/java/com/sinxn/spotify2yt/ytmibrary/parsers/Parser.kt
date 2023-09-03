package com.sinxn.spotify2yt.ytmibrary.parsers

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.YTAuth
import com.sinxn.spotify2yt.ytmibrary.nav
import java.util.Locale

class Parser() {
    private var language: String = ""

    constructor(lang: String) : this() {
        this.language =  lang
    }


    fun getSearchResultTypes(): List<String> {
        return listOf("artist", "playlist", "song", "video", "station", "profile")
    }

    fun parseArtistContents(results: JsonArray): JsonObject {
        val categories = listOf("albums", "singles", "videos", "playlists", "related")
        val categoriesLocal = listOf("albums", "singles", "videos", "playlists", "related") // Adjust these translations
        val categoriesParser = listOf(
            ::parseAlbum, ::parseSingle, ::parseVideo, ::parsePlaylist, ::parseRelatedArtist
        )
        val artist= JsonObject()

        categories.forEachIndexed { i, category ->
            val data = results.filter { r ->
                r.asJsonObject.has("musicCarouselShelfRenderer") &&
                        r.asJsonObject["musicCarouselShelfRenderer"].isJsonObject  &&
                        nav(r.asJsonObject, YTAuth.CAROUSEL + YTAuth.CAROUSEL_TITLE)?.asJsonObject?.get("text")
                            .toString().lowercase(Locale.ROOT) == categoriesLocal[i]
            }.map { it.asJsonObject["musicCarouselShelfRenderer"] }

            if (data.isNotEmpty()) {
                artist.add(category,JsonObject())
                artist.getAsJsonObject(category).apply {
                    addProperty("browseId","")
                    add("results",JsonObject())
                }

                if (nav(data[0].asJsonObject, YTAuth.CAROUSEL_TITLE)?.asJsonObject?.has("navigationEndpoint") == true) {
                    artist[category].asJsonObject.add(
                        "browseId", nav(data[0].asJsonObject, YTAuth.CAROUSEL_TITLE + YTAuth.NAVIGATION_BROWSE_ID)
                    )

                    if (category in listOf("albums", "singles", "playlists")) {
                        artist[category].asJsonObject.add(
                            "params",
                            nav(data[0].asJsonObject, YTAuth.CAROUSEL_TITLE)?.asJsonObject?.get("navigationEndpoint")?.asJsonObject?.get("browseEndpoint")?.asJsonObject?.get("params") ?: JsonObject()
                        )
                    }
                }

                val resultsList = parseContentList(data[0].asJsonObject["contents"].asJsonArray, categoriesParser[i])
                artist[category].asJsonObject.add("results",resultsList)
            }
        }

        return artist
    }

    // Define nav, parse_album, parse_single, parse_video, parse_playlist, parse_related_artist functions
}

