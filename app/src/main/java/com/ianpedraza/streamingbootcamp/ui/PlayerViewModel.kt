package com.ianpedraza.streamingbootcamp.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.ianpedraza.streamingbootcamp.R

@UnstableApi
class PlayerViewModel : ViewModel() {
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private val _player = MutableLiveData<ExoPlayer>()
    val player: LiveData<ExoPlayer> get() = _player

    fun initializePlayer(context: Context) {
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        _player.value = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                val adaptiveMediaItem = MediaItem.Builder()
                    .setUri(context.resources.getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()

                exoPlayer.apply {
                    addMediaItem(adaptiveMediaItem)
                    playWhenReady = true
                    seekTo(currentItem, playbackPosition)
                    prepare()
                }
            }
    }

    fun releasePlayer() {
        player.value?.let { exoPlayer ->
            playWhenReady = exoPlayer.playWhenReady
            currentItem = exoPlayer.currentMediaItemIndex
            playbackPosition = exoPlayer.currentPosition
            exoPlayer.release()
        }
        _player.value = null
    }

    fun isPlayerInitialized(): Boolean {
        return _player.value != null
    }
}
