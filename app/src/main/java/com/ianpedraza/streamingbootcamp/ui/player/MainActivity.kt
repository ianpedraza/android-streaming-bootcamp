package com.ianpedraza.streamingbootcamp.ui.player

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.recyclerview.widget.LinearLayoutManager
import com.ianpedraza.streamingbootcamp.R
import com.ianpedraza.streamingbootcamp.databinding.ActivityMainBinding
import com.ianpedraza.streamingbootcamp.domain.MetaData
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.ui.common.pip.PipActionsDelegate
import com.ianpedraza.streamingbootcamp.ui.common.pip.PlayerActions
import com.ianpedraza.streamingbootcamp.utils.DataState
import com.ianpedraza.streamingbootcamp.utils.MediaUtils.tagsFormat
import com.ianpedraza.streamingbootcamp.utils.ViewExtensions.showView
import com.ianpedraza.streamingbootcamp.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val playerViewModel: PlayerViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var adapter: ThumbnailAdapter

    private val pipActionsDelegate by lazy {
        PipActionsDelegate(this)
    }

    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        subscribeObservers()
    }

    private fun setupUi() {
        checkOrientation(resources.configuration)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        adapter = ThumbnailAdapter(::onAction)

        binding.recyclerView.apply {
            this.adapter = this@MainActivity.adapter
            this.layoutManager = layoutManager
        }
    }

    private fun onAction(action: ThumbnailAdapter.Action) {
        when (action) {
            is ThumbnailAdapter.Action.OnClick -> playerViewModel.play(action.item)
        }
    }

    private fun subscribeObservers() {
        playerViewModel.player.observe(this) { exoPlayer ->
            binding.videoView.player = exoPlayer
        }

        playerViewModel.isPlaying.observe(this) { isPlaying ->
            this.isPlaying = isPlaying
            binding.videoView.keepScreenOn = isPlaying
            setPictureInPictureParams(updatePipParams())
        }

        playerViewModel.metaData.observe(this) { metaData ->
            setMetaData(metaData)
        }

        mainViewModel.videos.observe(this) { dataState ->
            when (dataState) {
                is DataState.Error -> showError(dataState.exception)
                DataState.Loading -> showLoading()
                is DataState.Success -> setupVideos(dataState.data)
            }
        }
    }

    private fun setupVideos(videos: List<Video>) {
        binding.progressBar.showView(false)
        playerViewModel.bindVideos(videos)
        adapter.submitList(videos)
    }

    private fun showLoading() {
        binding.progressBar.showView()
    }

    private fun showError(exception: Exception) {
        binding.progressBar.showView(false)
        Toast.makeText(this, "There was an error loading ${exception.message}", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setMetaData(metaData: MetaData) {
        binding.apply {
            tvTitle.text = metaData.title
            tvDescription.text = metaData.description
            tvDate.text = metaData.date
            tvTags.text = tagsFormat(metaData.tags)
        }
    }

    /* FullScreen - Start */

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        checkOrientation(newConfig)
    }

    private fun checkOrientation(config: Configuration) {
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterInFullScreen()
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullScreen()
        }
    }

    private fun exitFullScreen() {
        binding.svContainer.showView(true)
        binding.bPlayer.referencedIds = intArrayOf(R.id.gl_player_size)
        showSystemUi()
    }

    private fun enterInFullScreen() {
        binding.svContainer.showView(false)
        binding.bPlayer.referencedIds = intArrayOf(R.id.gl_player_bottom)
        hideSystemUi()
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.videoView
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    /* FullScreen - End */

    /* PiP - Start */

    private fun updatePipParams(): PictureInPictureParams {
        return PictureInPictureParams.Builder()
            .setActions(PipActionsDelegate.createPipActions(this, isPlaying))
            .build()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (isPlaying) {
            enterPictureInPictureMode(updatePipParams())
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            val receiver = pipActionsDelegate.registerReceiver()

            receiver.pipAction.observe(this) { action ->
                when (action) {
                    PlayerActions.Play -> playerViewModel.play()
                    PlayerActions.Pause -> playerViewModel.pause()
                }
            }
        } else {
            pipActionsDelegate.clearReceiver()
        }

        binding.videoView.useController = !isInPictureInPictureMode
    }

    /* PiP - End*/

    @UnstableApi
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            playerViewModel.initializePlayer(this)
        }
    }

    @UnstableApi
    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || !playerViewModel.isPlayerInitialized()) {
            playerViewModel.initializePlayer(this)
        }
    }

    @UnstableApi
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            playerViewModel.releasePlayer()
        }
    }

    @UnstableApi
    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            playerViewModel.releasePlayer()
        }
    }
}
