package com.sinxn.spotify2yt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sinxn.spotify2yt.data.local.converters.DBConverter
import com.sinxn.spotify2yt.data.local.dao.Dao
import com.sinxn.spotify2yt.domain.model.Playlists
import com.sinxn.spotify2yt.domain.model.Tracks


@Database(
    entities = [Playlists::class, Tracks::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DBConverter::class)
abstract class YoutifyDataBase: RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        const val DATABASE_NAME = "youtify_db"
    }
}