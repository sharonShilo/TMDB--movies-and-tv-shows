package com.regev.poalim.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_images")
data class CachedImage(
    @PrimaryKey val imageUrl: String,
    val filePath: String,
    val cachedAt: Long
)
