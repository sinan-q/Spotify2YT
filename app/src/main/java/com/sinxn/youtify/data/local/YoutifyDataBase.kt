package com.sinxn.youtify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sinxn.youtify.data.local.converters.DBConverter
import com.sinxn.youtify.data.local.dao.Dao
import com.sinxn.youtify.domain.model.Playlists
import com.sinxn.youtify.domain.model.Tracks


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