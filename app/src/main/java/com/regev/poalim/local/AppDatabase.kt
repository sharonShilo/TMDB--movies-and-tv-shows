package com.regev.poalim.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.regev.poalim.model.CachedImage
import com.regev.poalim.model.CachedFavoriteMedia

@Database(entities = [CachedImage::class, CachedFavoriteMedia::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun imageCacheDao(): ImageCacheDao
    abstract fun favoriteDao(): FavoriteDao
}