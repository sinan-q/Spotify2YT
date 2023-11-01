package com.sinxn.youtify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sinxn.youtify.domain.model.Playlists
import com.sinxn.youtify.domain.model.Tracks

@Dao
interface Dao {
    @Transaction
    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<Playlists>

    @Query("SELECT * FROM tracks WHERE parent_id = :playlistId")
    suspend fun getTracks(playlistId: Long): List<Tracks>

    @Insert
    suspend fun addPlaylist(playlist: Playlists): Long

    @Insert
    fun addTracks(tracks: Tracks): Long

    @Transaction
    suspend fun addPlaylistWithTracks(playlist: Playlists, tracks: List<Tracks>) {
        val playlistId = addPlaylist(playlist)
        tracks.forEach {
            addTracks(it.copy(parent_id = playlistId))
        }
    }
    @Transaction
    @Delete
    suspend fun deletePlaylist(play: Playlists)

    @Query("UPDATE tracks SET youtube_id=:youtubeSongId WHERE id=:id")
    suspend fun updateYoutubeId(id: Long, youtubeSongId: String)

    @Query("UPDATE playlists SET youtube_id=:youtubePlaylistId WHERE id=:playlistId")
    suspend fun updateYoutubePlaylistId(playlistId: Long, youtubePlaylistId: String)

}