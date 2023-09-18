package com.sinxn.youtify.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.sinxn.youtify.data.local.YoutifyDataBase
import com.sinxn.youtify.data.local.dao.Dao
import com.sinxn.youtify.data.repository.PlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataBase(
        @ApplicationContext context: Context
    ): YoutifyDataBase {
        return Room.databaseBuilder(
            context,
            YoutifyDataBase::class.java,
            YoutifyDataBase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideMetaDao(youtifyDataBase: YoutifyDataBase) = youtifyDataBase.dao()

    @Provides
    fun providePlaylistRepository(dao: Dao): PlaylistRepository {
        return PlaylistRepository(dao)
    }

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideStorage(@ApplicationContext context: Context): File {
        return context.filesDir
    }
}