package com.regev.poalim.viewmodel

import com.regev.poalim.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsPageViewModel @Inject constructor(
    repository: MediaRepository
) : MainPageViewModel(repository)