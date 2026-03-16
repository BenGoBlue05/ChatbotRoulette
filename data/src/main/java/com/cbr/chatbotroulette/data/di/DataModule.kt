package com.cbr.chatbotroulette.data.di

import android.content.Context
import androidx.room.Room
import com.cbr.chatbotroulette.data.BuildConfig
import com.cbr.chatbotroulette.data.local.ChatDatabase
import com.cbr.chatbotroulette.data.local.dao.ChatDao
import com.cbr.chatbotroulette.data.remote.GeminiApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "chatbot_roulette_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: ChatDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            if (BuildConfig.DEBUG) {
                install(Logging) {
                    level = LogLevel.INFO
                    logger = object : Logger {
                        override fun log(message: String) {
                            println("Ktor: $message")
                        }
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideGeminiApiClient(httpClient: HttpClient): GeminiApiClient {
        return GeminiApiClient(httpClient)
    }
}
