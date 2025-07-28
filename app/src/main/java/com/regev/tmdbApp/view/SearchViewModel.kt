package com.regev.tmdbApp.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regev.tmdbApp.model.Media
import com.regev.tmdbApp.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Media>>(emptyList())
    val searchResults: StateFlow<List<Media>> = _searchResults.asStateFlow()

    fun searchMedia(query: String) {
        viewModelScope.launch {
            try {
                _searchResults.value = repository.searchMedia(query)
            } catch (e: Exception) {
                Log.e("SearchViewModel", e.message.toString())
            }
        }
    }
}