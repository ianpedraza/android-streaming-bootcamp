package com.ianpedraza.streamingbootcamp.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.usecases.FetchVideosUseCase
import com.ianpedraza.streamingbootcamp.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchVideosUseCase: FetchVideosUseCase
) : ViewModel() {
    private val _videos = MutableLiveData<DataState<List<Video>>>()
    val videos: LiveData<DataState<List<Video>>> get() = _videos

    init {
        fetchVideos()
    }

    private fun fetchVideos() {
        viewModelScope.launch {
            fetchVideosUseCase().onEach { dataState ->
                _videos.value = dataState
            }.launchIn(viewModelScope)
        }
    }
}
