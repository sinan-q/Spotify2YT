package com.sinxn.youtify.domain.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.adamratzman.spotify.models.SimpleArtist
import com.adamratzman.spotify.models.SpotifyPublicUser

@Entity(tableName = "playlists")
data class Playlists(
    val name: String,
    val spotify_id: String,
    val youtube_id: String?,
    val owner: SpotifyPublicUser?,
    val description: String?,
    val followers: Int?,
    val primaryColor: String?, // You can define a proper data class for primary color
    val image_url: String?,
    val count: Int,
    val exported: Boolean,
    val date: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L
)


@Entity(
    tableName = "tracks",
    foreignKeys = [
        ForeignKey(
            entity = Playlists::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Tracks(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val spotify_id: String,
    var youtube_id: String? = null,
    val album_id: String? = null,
    val artists: List<SimpleArtist>,
    val discNumber: Int,
    val durationMs: Long,
    val explicit: Boolean,
    val popularity: Int,
    val parent_id: Long = 0,
)

data class ParentWithChildren(
    @Embedded val playlist: Playlists,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val tracks: List<Tracks>
)