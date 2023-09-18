package com.sinxn.youtify.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sinxn.youtify.api.YTMGetCode
import com.sinxn.youtify.api.YTMGetToken
import com.sinxn.youtify.data.local.YoutifyDataBase
import com.sinxn.youtify.data.local.dao.Dao
import com.sinxn.youtify.data.repository.PlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
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


    private val interceptor: HttpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val client = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES) // write timeout
            .readTimeout(2, TimeUnit.MINUTES) // read timeout
            .addInterceptor(interceptor).build()
    val gson: Gson = GsonBuilder()
            .setLenient()
            .create()


    @Provides
    fun provideYTMGetCode(): YTMGetCode {
        return Retrofit.Builder()
            .baseUrl("https://www.youtube.com") // change this IP for testing by your actual machine IP
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(YTMGetCode::class.java)
    }

    @Provides
    fun provideYTMGetToken(): YTMGetToken {
        return Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com") // change this IP for testing by your actual machine IP
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(YTMGetToken::class.java)
    }

    @Provides
    fun provideStorage(@ApplicationContext context: Context): File {
        return context.filesDir
    }
}