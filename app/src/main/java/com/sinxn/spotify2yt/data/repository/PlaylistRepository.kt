package com.sinxn.spotify2yt.data.repository

import com.sinxn.spotify2yt.data.local.dao.Dao
import com.sinxn.spotify2yt.domain.model.Playlists
import com.sinxn.spotify2yt.domain.model.Tracks

class PlaylistRepository(
    private val dao: Dao
) {
    suspend fun getAllPlaylists(): List<Playlists> {
        return dao.getAllPlaylists()
    }

    suspend fun getTracks(playlistId: Long): MutableList<Tracks> {
        return dao.getTracks(playlistId).toMutableList()
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
}