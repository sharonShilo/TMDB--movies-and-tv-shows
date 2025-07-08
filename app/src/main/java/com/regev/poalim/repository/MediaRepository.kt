package com.regev.poalim.repository

import android.content.Context
import com.regev.poalim.BuildConfig
import com.regev.poalim.model.CachedImage
import com.regev.poalim.local.FavoriteDao
import com.regev.poalim.local.ImageCacheDao
import com.regev.poalim.model.CachedFavoriteMedia
import com.regev.poalim.model.Media
import com.regev.poalim.model.MediaDetails
import com.regev.poalim.network.TmbdApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val apiService: TmbdApiService,
    private val context: Context,
    private val imageCacheDao: ImageCacheDao,
    private val favoriteDao: FavoriteDao
) {
    private val apiKey = BuildConfig.API_KEY
    private val cacheExpirationMs = TimeUnit.DAYS.toMillis(1)
    private val tmdbImageBaseUrl = "https://image.tmdb.org/t/p/w500"


    suspend fun getPopularMovies(): List<Media> {
        val favorites = favoriteDao.getFavorites().map { it.id }.toSet()
        val apiResults = apiService.getPopularMovies(apiKey).results.map {  media ->
            media.copy(isFavorite = media.id in favorites)
        }
        return withContext(Dispatchers.IO) {
            apiResults.map { media ->
                val imageUrl = media.posterPath?.let { "$tmdbImageBaseUrl$it" }
                val cachedPath = imageUrl?.let { getCachedImagePath(it) }
                media.copy(cachedPosterFullPath = cachedPath)
            }
        }
    }

    suspend fun getPopularTvShows(): List<Media> {
        val favorites = favoriteDao.getFavorites().map { it.id }.toSet()
        val apiResults =  apiService.getPopularTvShows(apiKey).results.map {  media ->
            media.copy(isFavorite = media.id in favorites)
        }
        return withContext(Dispatchers.IO) {
            apiResults.map { media ->
                val imageUrl = media.posterPath?.let { "$tmdbImageBaseUrl$it" }
                val cachedPath = imageUrl?.let { getCachedImagePath(it) }
                media.copy(cachedPosterFullPath = cachedPath)
            }
        }
    }

    suspend fun searchMedia(searchQuery: String): List<Media> {
        return apiService.searchMedia(apiKey,searchQuery).results
            .filter { it.mediaType == "movie" || it.mediaType == "tv" }
    }

    suspend fun addFavorites(media: Media) {
        favoriteDao.addFavorite(mediaToCachedFavoriteMedia(media))
    }

    private fun mediaToCachedFavoriteMedia(media: Media): CachedFavoriteMedia {
        return CachedFavoriteMedia(
            id = media.id,
            title = media.title,
            voteAverage = media.voteAverage,
            posterPath = media.posterPath,
            mediaType = media.mediaType.toString()
        )
    }

    suspend fun removeFavorite(media: Media) {
        favoriteDao.removeFavorite(mediaToCachedFavoriteMedia(media))
    }

    suspend fun getFavorites(): List<Media> {
        return favoriteDao.getFavorites().map {  favorite ->
            Media(
                id = favorite.id,
                title = favorite.title,
                voteAverage = favorite.voteAverage,
                posterPath = favorite.posterPath,
                mediaType = favorite.mediaType,
                overview = "",
                isFavorite = true
            )
        }

    }

    suspend fun getMediaDetails(id: Int, mediaType: String): MediaDetails {
        return apiService.getMediaDetails(mediaType, id, apiKey)
    }

    private suspend fun getCachedImagePath(imageUrl: String): String? {
        eraseExpiredImages()//first clean expired images from database and files system

        //then, check if image cached
        val cachedImage = imageCacheDao.getCachedImage(imageUrl)
        if (cachedImage != null && !isExpired(cachedImage.cachedAt)) {
            val file = File(cachedImage.filePath)
            if (file.exists()) {
                return "file://${file.absolutePath}"
            }
        }

        //if not, download and cache it
        return downloadAndCacheImage(imageUrl)
    }

    //download image and cache it using streams
    private suspend fun downloadAndCacheImage(imageUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            //download
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()

            //save into files
            val fileName = imageUrl.hashCode().toString() + ".png"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }

            //cache into database
            val cachedImage = CachedImage(
                imageUrl = imageUrl,
                filePath = file.absolutePath,
                cachedAt = System.currentTimeMillis()
            )
            imageCacheDao.insertImage(cachedImage)

            return@withContext "file://${file.absoluteFile}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isExpired(cachedAt: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - cachedAt) > cacheExpirationMs
    }

    // erase expired images
    private suspend fun eraseExpiredImages() = withContext(Dispatchers.IO) {
        val expirationTime = System.currentTimeMillis() - cacheExpirationMs
        imageCacheDao.deleteExpiredImage(expirationTime)//delete from database

        val filesDir = context.filesDir
        filesDir.listFiles()?.forEach { file ->
            if (file.lastModified() < expirationTime)
                file.delete()//delete from fileSystem
        }
    }
}