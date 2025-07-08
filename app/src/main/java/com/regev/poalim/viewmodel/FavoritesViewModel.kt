package com.regev.poalim.viewmodel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regev.poalim.model.Media
import com.regev.poalim.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Media>>(emptyList())
    val favorites: StateFlow<List<Media>> = _favorites.asStateFlow()


    init {
        viewModelScope.launch {
            try {
                _favorites.value = repository.getFavorites()
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", e.message.toString())
            }
        }
    }
}