package com.sinxn.youtify.ytmibrary.mixins

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.youtify.ytmibrary.YTAuth
import com.sinxn.youtify.ytmibrary.YTMusic
import com.sinxn.youtify.ytmibrary.getContinuations
import com.sinxn.youtify.ytmibrary.parsers.getLibraryContents
import com.sinxn.youtify.ytmibrary.parsers.parseContentList
import com.sinxn.youtify.ytmibrary.parsers.parsePlaylist
import com.sinxn.youtify.ytmibrary.subList

class LibraryMixin {
    suspend fun getLibraryPlaylists(self: YTMusic, limit: Int = 25): JsonArray {
        // Ensure proper authentication checks here
        self.checkAuth()
        val body = JsonObject().apply {  addProperty("browseId", "FEmusic_liked_playlists")}
        val endpoint = "browse"
        val response = self.sendRequest(endpoint, body)

        val results = getLibraryContents(response, YTAuth.GRID)
        require(results!=null && results.isJsonObject)
        val playlists = parseContentList(results.asJsonObject["items"].asJsonArray.subList(1), ::parsePlaylist)

        if ( results.asJsonObject.has("continuations")) {
            val requestFunc: suspend (String) -> JsonObject = { additionalParams ->
                self.sendRequest(endpoint, body, additionalParams)
            }
            val parseFunc: (JsonArray) -> JsonArray = { contents->
                parseContentList(contents, ::parsePlaylist) }
            val remainingLimit = if (limit < 0) null else limit - playlists.size()
            playlists.addAll(getContinuations(results.asJsonObject, "gridContinuation", remainingLimit, requestFunc , parseFunc ))
        }

        return playlists
    }
}