package com.sinxn.spotify2yt.ytmibrary.mixins

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.ytmibrary.YTMusic

open class PlaylistMixin(private val yTMusic: YTMusic) {
    suspend fun createPlaylist(
        title: String,
        description: String,
        privacyStatus: String = "PRIVATE",
        videoIds: List<String>? = null,
        sourcePlaylist: String? = null
    ): String {
        // Check authentication
        yTMusic.checkAuth()

        // Create a JSON object for the request body
        val body = JsonObject().apply {
            addProperty("title", title)
            addProperty("description", htmlToTxt(description)) // YT does not allow HTML tags
            addProperty("privacyStatus", privacyStatus)
        }

        // Add videoIds to the request body if provided
         body.add("videoIds",JsonArray().apply {
            videoIds?.forEach { this.add(it) }
         })

        // Add sourcePlaylist to the request body if provided
        sourcePlaylist?.let { body.addProperty("sourcePlaylistId", it) }

        // Define the endpoint
        val endpoint = "playlist/create"

        // Send the request and get the response
        val response = yTMusic.sendRequest(endpoint, body)
        if (!response.has("playlistId")) throw Exception(response.toString())
        // Return the playlistId if present, otherwise, return the full response
        return response["playlistId"].asString
    }
}