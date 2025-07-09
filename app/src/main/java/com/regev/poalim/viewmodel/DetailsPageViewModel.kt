package com.regev.poalim.viewmodel

import android.util.Log
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
class DetailsPageViewModel @Inject constructor(
    private val repository: MediaRepository
) : MainPageViewModel(repository) {

    private val youtubePreUrl = "https://www.youtube.com/watch?v="

    private val _trailerUrl = MutableStateFlow<String?>(null)
    val trailerUrl: StateFlow<String?> = _trailerUrl


    fun getMediaDetails(media: Media) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val details =
                    media.mediaType?.let { repository.getMediaDetails(id = media.id, mediaType = it) }

                val trailer = details?.videos?.results
                    ?.firstOrNull{ video -> video.site.equals("youTube", ignoreCase = true) && video.type.equals("Trailer", ignoreCase = true) }
                _trailerUrl.value = trailer?.key?.let { key -> "$youtubePreUrl$key"}
            } catch (e: Exception) {
                Log.e("MainPageViewModel", "retrieve data error $e")
            }
        }
    }
}