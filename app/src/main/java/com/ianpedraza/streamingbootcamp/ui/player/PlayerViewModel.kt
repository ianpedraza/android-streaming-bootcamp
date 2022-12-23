package com.ianpedraza.streamingbootcamp.ui.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.ianpedraza.streamingbootcamp.R
import com.ianpedraza.streamingbootcamp.domain.MetaData
import com.ianpedraza.streamingbootcamp.domain.toMetaData

class PlayerViewModel : ViewModel() {
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private val _player = MutableLiveData<ExoPlayer>()
    val player: LiveData<ExoPlayer> get() = _player

    private val _metaData = MutableLiveData<MetaData>()
    val metaData: LiveData<MetaData> get() = _metaData

    @UnstableApi
    fun initializePlayer(context: Context) {
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        _player.value = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                val mediaItem = MediaItem.fromUri(context.getString(R.string.media_url_mp3))

                val adaptiveMediaItem = MediaItem.Builder()
                    .setUri(context.resources.getString(R.string.media_url_hls))
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()

                exoPlayer.apply {
                    addMediaItem(mediaItem)
                    addMediaItem(adaptiveMediaItem)
                    playWhenReady = true
                    seekTo(currentItem, playbackPosition)
                    addListener(listener)
                    prepare()
                }
            }
    }

    fun releasePlayer() {
        player.value?.let { exoPlayer ->
            playWhenReady = exoPlayer.playWhenReady
            currentItem = exoPlayer.currentMediaItemIndex
            playbackPosition = exoPlayer.currentPosition
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
        _player.value = null
    }

    fun isPlayerInitialized(): Boolean {
        return _player.value != null
    }

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                _metaData.value = player.mediaMetadata.toMetaData()
            }
        }
    }
}
