package com.regev.poalim.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regev.poalim.model.Media
import com.regev.poalim.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class MainPageViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {
    private val _popularMovies = MutableStateFlow<List<Media>>(emptyList())
    val popularMovies: StateFlow<List<Media>> = _popularMovies

    private val _popularTvShows = MutableStateFlow<List<Media>>(emptyList())
    val popularTvShow: StateFlow<List<Media>> = _popularTvShows

    init {
        refreshPopularMedia()
    }

    fun refreshPopularMedia() {
        getPopularMovies()
        getPopularTvShows()
    }

    private fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _popularMovies.value = mediaRepository.getPopularMovies()

            } catch (e: Exception) {
                Log.e("MainPageViewModel", "retrieve data error $e")
            }
        }
    }

    private fun getPopularTvShows() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _popularTvShows.value = mediaRepository.getPopularTvShows()
            } catch (e: Exception) {
                Log.e("MainPageViewModel", "retrieve data error $e")
            }
        }
    }

    private fun addFavorite(media: Media) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.addFavorites(media)
        }
    }

    private fun removeFavorite(media: Media) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.removeFavorite(media)
        }
    }

    fun toggleFavorite(media: Media) {
            if (media.isFavorite == true) {
                removeFavorite(media)
                // Update the media lists with new favorite status for UI
                updateFavoriteStatus(media.id, false)
            } else {
                addFavorite(media)
                updateFavoriteStatus(media.id, true)
            }

    }

    private fun updateFavoriteStatus(mediaId: Int, isFavorite: Boolean) {
        _popularMovies.value = _popularMovies.value.map {
            if (it.id == mediaId) it.copy(isFavorite = isFavorite) else it
        }
        _popularTvShows.value = _popularTvShows.value.map {
            if (it.id == mediaId) it.copy(isFavorite = isFavorite) else it
        }
    }
}