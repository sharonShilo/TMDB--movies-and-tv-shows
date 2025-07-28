package com.regev.tmdbApp.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.regev.tmdbApp.model.CachedFavoriteMedia

@Dao
interface FavoriteDao {
    @Insert
    suspend fun addFavorite(cachedFavoriteMedia: CachedFavoriteMedia)

    @Delete
    suspend fun removeFavorite(cachedFavoriteMedia: CachedFavoriteMedia)

    @Query("SELECT * FROM favorites")
    suspend fun getFavorites(): List<CachedFavoriteMedia>
}