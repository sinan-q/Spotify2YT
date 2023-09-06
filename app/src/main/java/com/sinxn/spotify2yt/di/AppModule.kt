package com.sinxn.spotify2yt.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sinxn.spotify2yt.api.YTMGetCode
import com.sinxn.spotify2yt.api.YTMGetToken
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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
    }


    private val interceptor: HttpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val client = OkHttpClient.Builder()
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