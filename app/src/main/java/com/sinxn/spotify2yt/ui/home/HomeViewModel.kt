package com.sinxn.spotify2yt.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.SpotifyCredentials
import com.adamratzman.spotify.models.Track
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sinxn.spotify2yt.repository.SharedPref
import com.sinxn.spotify2yt.tools.similarityTo
import com.sinxn.spotify2yt.tools.ytId
import com.sinxn.spotify2yt.ui.home.DebugUtilis.v
import com.sinxn.spotify2yt.ytmibrary.YTMusic
import com.sinxn.spotify2yt.ytmibrary.mixins.SearchMixin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs


@HiltViewModel
class HomeViewModel @Inject constructor(
   private val sharedPref: SharedPref,
    private val storage: File
): ViewModel()
{
    private var spotifyAppApi: SpotifyAppApi? = null
    private lateinit var ytmApi: YTMusic

    var playlistSongs: List<Track?> = emptyList()
    var playlistName: String = ""

    fun onConvert(playlist: String) {
        viewModelScope.launch {
            val playlistId = playlist.trim('/').split('/').last()
            val res = spotifyAppApi?.playlists?.getPlaylist(playlistId)
            res?.let {
                playlistName = res.name
                playlistSongs = res.tracks.items.map {
                   it.track?.asTrack
                }
            }
            getYTSongs()
        }
    }


    private fun getYTSongs() {
        viewModelScope.launch {
            playlistSongs.forEach { track ->
                require(track!=null) {"track is null"}
                val songName = track.name.replace(Regex(" \\(feat.*\\..+\\)"), "")
                var query = track.artists.joinToString{it.name + " " } + songName
                query = query.replace("&", "")
                val res = SearchMixin(ytmApi).search(query)
                if (res.size()>0) {
                    Log.d("TAG", "getYTSongs: $res")

                    val targetSongId = getTopResult(res)
                    if (!targetSongId.isNullOrEmpty()) track.ytId = targetSongId
                    Log.d("TAG", "getYTSongs: $targetSongId")
                }


            }
        }
    }


    private fun getTopResult(res: JsonArray): String? {
        res.forEach {it as JsonObject
            if (it["category"].asString.equals("Top result"))
                return if (it.has("videoId"))
                    it["videoId"].asString
                else null
        }
        return null
    }

    var isLogged = false

    init {
        isLogged = sharedPref.isLogged()
        if (isLogged) {
            viewModelScope.launch {
                ytmApi = YTMusic(File(storage, "auth").path)
                v("tag",SearchMixin(ytmApi).search("Liquid Smooth").toString())

                val spotifyCred = SpotifyCredentials().apply {
                    clientId = sharedPref.spotifyClientId
                    clientSecret = sharedPref.spotifyClientSecret
                }
                spotifyAppApi = SpotifyAppApiBuilder(spotifyCred).build()
            }

        }
    }


    private fun getBestFitSongId(ytmResults: List<JsonObject>, spoti: Track?): String? {
        require(spoti!=null) {"spotify Track is null"}

        val matchScore = hashMapOf<String, Int>()
        val titleScore = hashMapOf<String, Int>()

        for (ytm in ytmResults) {
            val resultType = ytm.get("resultType")?.asString
            val title = ytm.get("title")?.asString
            val duration = ytm.get("duration")?.asString
            val videoId = ytm.get("videoId")?.asString
            val artists = ytm.getAsJsonArray("artists")?.joinToString(" ") { it.asJsonObject.get("name").asString }
            val album = ytm.getAsJsonObject("album")?.get("name")?.asString

            if (resultType !in listOf("song", "video") || title.isNullOrEmpty()) {
                continue
            }
            require(videoId!=null) {"$ytm video Id null"}


            val durationMatchScore: Int? = if (duration != null) {
                val durationItems = duration.split(":")
                val durationValue = durationItems[0].toInt() * 60 + durationItems[1].toInt()
                1 - abs(durationValue - spoti.durationMs) * 2 / spoti.durationMs
            } else {
                null
            }

            val sanitizedTitle = if (resultType == "video") {
                val titleSplit = title.split("-")
                if (titleSplit.size == 2) {
                    titleSplit[1].trim()
                } else {
                    title
                }
            } else {
                title
            }
            titleScore[videoId] = sanitizedTitle.lowercase(Locale.ROOT).similarityTo(spoti.name.lowercase(Locale.ROOT))
            val scores = mutableListOf(
                titleScore[videoId]?:0,
                spoti.artists.joinToString{it.name + " " }.lowercase(Locale.ROOT)
                    .let { artists?.lowercase(Locale.ROOT)?.similarityTo(it) }?: 0
            )

            durationMatchScore?.let {
                scores.add(it * 5)
            }

            if (resultType == "song" && album != null) {
                scores.add(album.lowercase(Locale.ROOT).similarityTo(spoti.album.name.lowercase(
                    Locale.ROOT
                )))
            }

            matchScore[videoId] =
                (scores.average() * maxOf(1, if (resultType == "song") 2 else 1)).toInt()
        }

        if (matchScore.isEmpty()) {
            return null
        }

        return matchScore.maxByOrNull { it.value }?.key
    }

}

object DebugUtilis {
    var _charLimit = 2000
    @JvmStatic
    fun v(tag: String?, message: String): Int {
        // If the message is less than the limit just show
        if (message.length < _charLimit) {
            return Log.v(tag, message)
        }
        val sections = message.length / _charLimit
        for (i in 0..sections) {
            val max = _charLimit * (i + 1)
            if (max >= message.length) {
                Log.v(tag, message.substring(_charLimit * i))
            } else {
                Log.v(tag, message.substring(_charLimit * i, max))
            }
        }
        return 1
    }
}