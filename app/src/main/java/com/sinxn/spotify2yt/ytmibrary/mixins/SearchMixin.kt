package com.sinxn.spotify2yt.ytmibrary.mixins

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
    ): List<JsonObject> {
        var filter = filterParam
        body  = JsonObject()
        body.addProperty("query", query)
        endpoint = "search"
        var results: JsonObject
        var searchResults = mutableListOf<JsonObject>()
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
            body.addProperty("params",params)
        }

        val response = yTMusic.sendRequest(endpoint, body)

        // Check for no results
        if ( !response.has("contents")) {
            return searchResults
        }

        val tabbedSearchResults = response.getAsJsonObject("contents")
        val tabbedSearchResultsRenderer = tabbedSearchResults.getAsJsonObject("tabbedSearchResultsRenderer")
        //TODO .has(contetts)
        if (tabbedSearchResultsRenderer!=null) {
            val tabIndex = if (scope == null || filter == null) 0 else scopes.indexOf(scope) + 1
            val tabs = tabbedSearchResultsRenderer["tabs"].asJsonArray
            val tabRenderer = tabs[tabIndex].asJsonObject["tabRenderer"].asJsonObject
            results = tabRenderer["content"].asJsonObject


        } else {
            results = response["contents"].asJsonObject
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
                when {
                    res.asJsonObject.has("musicCardShelfRenderer") -> {
                        val topResult = parseTopResult(
                            res.asJsonObject["musicCardShelfRenderer"].asJsonObject,
                            parser.getSearchResultTypes()
                        )
                        searchResults.add(topResult)

                        val contents = nav(res.asJsonObject, listOf("musicCardShelfRenderer", "contents"), true)
                        if (contents != JsonObject()) {
                            val category = contents?.asJsonArray?.firstOrNull()?.asJsonObject
                                ?.let { nav(it, listOf("messageRenderer") + YTAuth.TEXT_RUN_TEXT) }
                            var type: String? = null
                            // Continue handling based on your code logic
                        }
                    }
                    res.asJsonObject.has("musicShelfRenderer") -> {
                        val shelfRenderer = res.asJsonObject["musicShelfRenderer"].asJsonObject
                        val contents = shelfRenderer["contents"].asJsonArray
                        var typeFilter = filter
                        val category = nav(shelfRenderer, YTAuth.MUSIC_SHELF + YTAuth.TITLE_TEXT, true)?.asString

                        if (typeFilter == null && scope == scopes[0]) {
                            typeFilter = category
                        }

                        val type = typeFilter?.substring(0, typeFilter.length - 1)
                            ?.lowercase(Locale.ROOT)

                        val searchResultTypes = parser.getSearchResultTypes()
                        searchResults.addAll(
                            parseSearchResults(contents, searchResultTypes, type, category)
                        )

                        if (filter != null) {
                            // Define your request and parse functions
                            val requestFunc:suspend (String) -> JsonObject = {

                                yTMusic.sendRequest(endpoint, body, it)
                            }
                            val parseFunc: (JsonObject) -> List<JsonObject> = {
                                parseSearchResults(it.asJsonArray, searchResultTypes, type, category)
                            }

                            searchResults.addAll(
                                getContinuations(
                                    shelfRenderer,
                                    "musicShelfContinuation",
                                    limit - searchResults.size,
                                    requestFunc,
                                    parseFunc
                                )
                            )
                        }
                    }
                }
            }
        }

        return searchResults
    }

}