package com.spotify.client.di

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import com.spotify.client.App
import com.spotify.client.BuildConfig
import com.spotify.client.PrefStore
import com.spotify.client.persistence.search.SearchResultsDatabase
import com.spotify.client.repo.SearchRepository
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class AppModule {

    @Provides
    fun provideContext(application: App): Context = application.applicationContext

    @Provides
    fun provideRetrofit(context: Context): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val logs = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        httpLoggingInterceptor.level = logs
        val storeService = PrefStore(context)
        val interceptor = Interceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
            val authToken = storeService.getAuthToken()
            if (authToken.isNotBlank()) {
                builder.addHeader("Authorization", authToken)
            }
            val updatedRequest = builder.build()
            chain.proceed(updatedRequest)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    fun provideSearchResultsDatabase(context: Context): SearchResultsDatabase {
        return SearchResultsDatabase.get(context)
    }

    @Provides
    fun provideSearchRepository(retrofit: Retrofit, database: SearchResultsDatabase): SearchRepository {
        return SearchRepository(retrofit, database)
    }

}