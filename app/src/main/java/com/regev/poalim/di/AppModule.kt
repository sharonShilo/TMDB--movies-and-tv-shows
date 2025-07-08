package com.regev.poalim.di

import android.content.Context
import androidx.room.Room
import com.regev.poalim.local.AppDatabase
import com.regev.poalim.local.FavoriteDao
import com.regev.poalim.local.ImageCacheDao
import com.regev.poalim.network.TmbdApiService
import com.regev.poalim.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTmbdApiService(): TmbdApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmbdApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(tmbdApiService: TmbdApiService, @ApplicationContext context: Context, imageCacheDao: ImageCacheDao, favoriteDao: FavoriteDao): MediaRepository {
        return MediaRepository(tmbdApiService, context, imageCacheDao, favoriteDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "store_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageCachedDao(database: AppDatabase) = database.imageCacheDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase) = database.favoriteDao()
}