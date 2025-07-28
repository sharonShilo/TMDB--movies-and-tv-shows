package com.regev.tmdbApp.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.regev.tmdbApp.model.CachedImage

@Dao
interface ImageCacheDao {
    @Query("SELECT * FROM cached_images WHERE imageUrl = :url")
    suspend fun getCachedImage(url: String): CachedImage?

    @Insert
    suspend fun insertImage(image: CachedImage)

    @Query("DELETE FROM cached_images WHERE cachedAt < :expirationTime")
    suspend fun deleteExpiredImage(expirationTime: Long)
}