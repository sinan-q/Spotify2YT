package com.sinxn.youtify.data.local.converters

import androidx.room.TypeConverter
import com.adamratzman.spotify.models.ArtistUri
import com.adamratzman.spotify.models.Followers
import com.adamratzman.spotify.models.SimpleArtist
import com.adamratzman.spotify.models.SpotifyPublicUser
import com.adamratzman.spotify.models.UserUri

class DBConverter {

    @TypeConverter
    fun fromSpotifyPublicUser(owner: SpotifyPublicUser): String {
            return "${owner.displayName?:"null"},${owner.followers.total?:"null"},${owner.id}"
    }

    @TypeConverter
    fun toSpotifyPublicUser(value: String): SpotifyPublicUser {
        val values = value.split(",")
        return SpotifyPublicUser(mapOf(Pair("","")),"",values[2], UserUri("user"),values[0], Followers(total = values[1].toIntOrNull()), emptyList(),"")
    }

    @TypeConverter
    fun fromSimpleArtists(artist: List<SimpleArtist>): String {
        val s= StringBuilder()
        for (i in artist) s.append(i.id).append(",").append(i.name).append(";")
        return s.toString()
    }

    @TypeConverter
    fun toSimpleArtists(value: String): List<SimpleArtist> {
        val list= mutableListOf<SimpleArtist>()
        val values = value.split(";")
        for (i in values) {
            if (i.isNotEmpty()) {
                val v = i.split(",")
                list.add(SimpleArtist(mapOf(Pair("", "")), "", v[0], ArtistUri("spotify:artist:${v[0]}"), v[1], ""))
            } }
        return list
    }
}