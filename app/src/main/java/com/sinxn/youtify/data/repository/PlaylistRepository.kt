package com.sinxn.youtify.data.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.sinxn.youtify.data.local.dao.Dao
import com.sinxn.youtify.domain.model.Playlists
import com.sinxn.youtify.domain.model.Tracks

class PlaylistRepository(
    private val dao: Dao
) {
    suspend fun getAllPlaylists(): List<Playlists> {
        return dao.getAllPlaylists()
    }

    suspend fun getTracks(playlistId: Long): SnapshotStateList<Tracks> {
        return dao.getTracks(playlistId).toMutableStateList()
    }

    suspend fun addPlaylistWithTracks(playlists: Playlists,tracks: List<Tracks>) {
        dao.addPlaylistWithTracks(playlists,tracks)
    }

    suspend fun deletePlaylist(play: Playlists) {
        dao.deletePlaylist(play)
    }

    suspend fun updateYoutubeId(id: Long, youtubeSongId: String) {
        dao.updateYoutubeId(id,youtubeSongId)
    }

    suspend fun updateYoutubePlaylistId(playlistId: Long,ytPlaylistId: String) {
        dao.updateYoutubePlaylistId(playlistId,ytPlaylistId)
    }
}