package com.sinxn.spotify2yt.ytmibrary.mixins

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.YTAuth
import com.sinxn.spotify2yt.ytmibrary.YTMusic
import com.sinxn.spotify2yt.ytmibrary.getContinuations
import com.sinxn.spotify2yt.ytmibrary.nav
import com.sinxn.spotify2yt.ytmibrary.parsers.Parser
import com.sinxn.spotify2yt.ytmibrary.parsers.getSearchParams
import com.sinxn.spotify2yt.ytmibrary.parsers.parseSearchResults
import com.sinxn.spotify2yt.ytmibrary.parsers.parseTopResult
import java.util.Locale

open class SearchMixin(private val yTMusic: YTMusic) {
    private var body: JsonObject = JsonObject()
    private var endpoint: String = ""
    private val parser = Parser()

    suspend fun search(
        query: String,
        filterParam: String? = null,
        scope: String? = null,
        limit: Int = 20,
        ignoreSpelling: Boolean = false
    ): JsonArray {
        var filter = filterParam
        body = JsonObject().apply {
            addProperty("query", query)
        }

        endpoint = "search"
        val results: JsonObject
        val searchResults = JsonArray()
        val filters = listOf(
            "albums", "artists", "playlists", "community_playlists", "featured_playlists", "songs",
            "videos", "profiles"
        )

        if (filter != null && filter !in filters) {
            throw Exception(
                "Invalid filter provided. Please use one of the following filters or leave out the parameter: " +
                        filters.joinToString(", ")
            )
        }

        val scopes = listOf("library", "uploads")
        if (scope != null && scope !in scopes) {
            throw Exception(
                "Invalid scope provided. Please use one of the following scopes or leave out the parameter: " +
                        scopes.joinToString(", ")
            )
        }

        if (scope == scopes[1] && filter != null) {
            throw Exception(
                "No filter can be set when searching uploads. Please unset the filter parameter when scope is set to " +
                        "uploads."
            )
        }

        if (scope == scopes[0] && filter in filters.subList(3, 5)) {
            throw Exception(
                "$filter cannot be set when searching library. Please use one of the following filters or leave out the parameter: " +
                        filters.subList(0, 3) + filters.subList(5, filters.size).joinToString(", ")
            )
        }

        val params = getSearchParams(filter, scope, ignoreSpelling)
        if (params != null) {
            body.addProperty("params", params)
        }
        val response = yTMusic.sendRequest(endpoint, body)

        // Check for no results
        if (!response.has("contents")) {
            return searchResults
        }

        val tabbedSearchResults = response.getAsJsonObject("contents")
        results = if (tabbedSearchResults.has("tabbedSearchResultsRenderer")) {
            val tabbedSearchResultsRenderer =
                tabbedSearchResults.getAsJsonObject("tabbedSearchResultsRenderer")
            val tabIndex = if (scope == null || filter == null) 0 else scopes.indexOf(scope) + 1
            val tabs = tabbedSearchResultsRenderer["tabs"].asJsonArray
            val tabRenderer = tabs[tabIndex].asJsonObject["tabRenderer"].asJsonObject
            tabRenderer["content"].asJsonObject
        } else {
            response["contents"].asJsonObject
        }
        val resultsArray = nav(results, YTAuth.SECTION_LIST)?.asJsonArray
        // Check for no results
        if (resultsArray != null) {
            if ((resultsArray.size() == 1) && TODO()) {
                return searchResults
            }
        }

        // Set filter for parser
        if (filter != null && "playlists" in filter) {
            filter = "playlists"
        } else if (scope == scopes[1]) {
            filter = scopes[1]
        }

        if (resultsArray != null) {
            for (res in resultsArray) {
                var contents: JsonElement? = null
                var category: JsonElement? = null
                var type: String? = null
                when {
                    res.asJsonObject.has("musicCardShelfRenderer") -> {
                        val topResult = parseTopResult(
                            res.asJsonObject["musicCardShelfRenderer"].asJsonObject,
                            parser.getSearchResultTypes()
                        )
                        searchResults.add(topResult)

                        contents = nav(
                            res.asJsonObject,
                            listOf("musicCardShelfRenderer", "contents"),
                            true
                        )
                        if (contents != null) {
                            val category1 = contents.asJsonArray?.firstOrNull()?.asJsonObject
                            if (category1?.has("messageRenderer") == true)
                                category =
                                    nav(category1, listOf("messageRenderer") + YTAuth.TEXT_RUN_TEXT)
                        }
                    }

                    res.asJsonObject.has("musicShelfRenderer") -> {
                        contents = res.asJsonObject["musicShelfRenderer"].asJsonObject["contents"].asJsonArray
                        var typeFilter = filter
                        category = nav(
                            res.asJsonObject,
                            YTAuth.MUSIC_SHELF + YTAuth.TITLE_TEXT,
                            true
                        )

                        if (typeFilter == null && scope == scopes[0]) {
                            typeFilter = category?.asString
                        }

                        type = typeFilter?.substring(0, typeFilter.length - 1)?.lowercase(Locale.ROOT)
                    }

                    else -> continue
                }
                val searchResultTypes = parser.getSearchResultTypes()

                searchResults.addAll(
                    parseSearchResults(
                        contents?.asJsonArray,
                        searchResultTypes,
                        type,
                        category?.asString
                    )
                )

                if (filter != null) {
                    val requestFunc: suspend (String) -> JsonObject = {

                        yTMusic.sendRequest(endpoint, body, it)
                    }
                    val parseFunc: (JsonObject) -> JsonArray? = {
                        parseSearchResults(
                            it.asJsonArray,
                            searchResultTypes,
                            type,
                            category?.asString
                        )
                    }

                    searchResults.addAll(
                        getContinuations(
                            res.asJsonObject["musicShelfRenderer"].asJsonObject,
                            "musicShelfContinuation",
                            limit - searchResults.size(),
                            requestFunc,
                            parseFunc
                        )
                    )
                }
            }
        }
        return searchResults
    }

}