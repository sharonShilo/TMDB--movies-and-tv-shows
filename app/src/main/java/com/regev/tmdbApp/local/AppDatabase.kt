package com.regev.tmdbApp.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.regev.tmdbApp.model.CachedImage
import com.regev.tmdbApp.model.CachedFavoriteMedia

@Database(entities = [CachedImage::class, CachedFavoriteMedia::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun imageCacheDao(): ImageCacheDao
    abstract fun favoriteDao(): FavoriteDao
}