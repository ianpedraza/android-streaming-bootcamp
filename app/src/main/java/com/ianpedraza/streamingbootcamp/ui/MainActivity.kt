package com.ianpedraza.streamingbootcamp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import com.ianpedraza.streamingbootcamp.databinding.ActivityMainBinding
import com.ianpedraza.streamingbootcamp.utils.viewBinding

class MainActivity : AppCompatActivity() {
    private val viewBinding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.player.observe(this) { exoPlayer ->
            viewBinding.videoView.player = exoPlayer
        }
    }

    @UnstableApi
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            viewModel.initializePlayer(this)
        }
    }

    @UnstableApi
    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || !viewModel.isPlayerInitialized()) {
            viewModel.initializePlayer(this)
        }
    }

    @UnstableApi
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            viewModel.releasePlayer()
        }
    }

    @UnstableApi
    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            viewModel.releasePlayer()
        }
    }
}
