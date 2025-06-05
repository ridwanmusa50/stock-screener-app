package io.rid.stockscreenerapp

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.rid.stockscreenerapp.api.ApiService
import io.rid.stockscreenerapp.api.ApiServiceBuilder
import io.rid.stockscreenerapp.api.Repository
import io.rid.stockscreenerapp.database.Dao
import io.rid.stockscreenerapp.database.Database
import io.rid.stockscreenerapp.network.NetworkMonitor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(@ApplicationContext context: Context): ApiService {
        return ApiServiceBuilder(context).build().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService, networkMonitor: NetworkMonitor): Repository {
        return Repository(apiService, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "APP_DB")
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: Database): Dao {
        return database.dao()
    }

}