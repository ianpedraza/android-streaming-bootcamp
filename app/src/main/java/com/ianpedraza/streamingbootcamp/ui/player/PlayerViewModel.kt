package com.ianpedraza.streamingbootcamp.ui.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.ianpedraza.streamingbootcamp.domain.MetaData
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.utils.MediaUtils.toMediaItem
import com.ianpedraza.streamingbootcamp.utils.MediaUtils.toMetaData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _playWhenReady: MutableLiveData<Boolean> =
        savedStateHandle.getLiveData(KEY_PLAY_WHEN_READY, true)
    private val playWhenReady: Boolean get() = _playWhenReady.value!!

    private val _currentItem: MutableLiveData<Int> =
        savedStateHandle.getLiveData(KEY_CURRENT_ITEM, 0)
    private val currentItem: Int get() = _currentItem.value!!

    private val _playbackPosition: MutableLiveData<Long> =
        savedStateHandle.getLiveData(KEY_PLAYBACK_POSITION, 0L)
    private val playbackPosition: Long get() = _playbackPosition.value!!

    private val _currentVideos: MutableLiveData<MutableList<Video>> =
        savedStateHandle.getLiveData(KEY_CURRENT_VIDEOS, mutableListOf())
    private val currentVideos: MutableList<Video> get() = _currentVideos.value!!

    private val _player = MutableLiveData<ExoPlayer>()
    val player: LiveData<ExoPlayer> get() = _player

    private val _metaData = MutableLiveData<MetaData>()
    val metaData: LiveData<MetaData> get() = _metaData

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    fun bindVideos(videos: List<Video>) {
        currentVideos.clear()
        currentVideos.addAll(videos)
        prepareVideos()
    }

    @UnstableApi
    fun initializePlayer(context: Context) {
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        _player.value = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()

        _player.value?.let { exoPlayer ->
            exoPlayer.addListener(listener)
            prepareVideos()
        }
    }

    fun releasePlayer() {
        player.value?.let { exoPlayer ->
            _playWhenReady.value = exoPlayer.playWhenReady
            _currentItem.value = exoPlayer.currentMediaItemIndex
            _playbackPosition.value = exoPlayer.currentPosition
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
        _player.value = null
    }

    fun isPlayerInitialized(): Boolean {
        return _player.value != null
    }

    fun play(video: Video) {
        _player.value?.apply {
            playWhenReady = true
            val itemIndex = currentVideos.indexOf(video)
            seekTo(itemIndex, 0L)
            prepare()
        }
    }

    fun play() {
        _player.value?.play()
    }

    fun pause() {
        _player.value?.pause()
    }

    private fun prepareVideos() {
        val mediaItems = currentVideos.map { video -> video.toMediaItem() }
        _player.value?.setMediaItems(mediaItems)

        _player.value?.apply {
            playWhenReady = this@PlayerViewModel.playWhenReady
            seekTo(currentItem, playbackPosition)
            prepare()
        }
    }

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                _metaData.value = player.mediaMetadata.toMetaData()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }
    }

    companion object {
        const val KEY_PLAY_WHEN_READY = "playWhenReady"
        const val KEY_CURRENT_ITEM = "currentItem"
        const val KEY_PLAYBACK_POSITION = "playbackPosition"
        const val KEY_CURRENT_VIDEOS = "currentVideos"
    }
}
