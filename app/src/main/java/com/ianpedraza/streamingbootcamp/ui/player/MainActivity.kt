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
    private val viewBinding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: PlayerViewModel by viewModels()

    private lateinit var adapter: ThumbnailAdapter

    private val pipActionsDelegate by lazy {
        PipActionsDelegate(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        setupUi()
        subscribeObservers()
        viewModel.fetchVideos()
    }

    private fun setupUi() {
        checkOrientation(resources.configuration)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        adapter = ThumbnailAdapter(::onAction)

        viewBinding.recyclerView.apply {
            this.adapter = this@MainActivity.adapter
            this.layoutManager = layoutManager
        }
    }

    private fun onAction(action: ThumbnailAdapter.Action) {
        when (action) {
            is ThumbnailAdapter.Action.OnClick -> viewModel.play(action.item)
        }
    }

    private fun subscribeObservers() {
        viewModel.player.observe(this) { exoPlayer ->
            viewBinding.videoView.player = exoPlayer
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            viewBinding.videoView.keepScreenOn = isPlaying
            setPictureInPictureParams(updatePipParams())
        }

        viewModel.metaData.observe(this) { metaData ->
            setMetaData(metaData)
        }

        viewModel.videos.observe(this) { dataState ->
            when (dataState) {
                is DataState.Error -> showError(dataState.exception)
                DataState.Loading -> showLoading()
                is DataState.Success -> setupVideos(dataState.data)
            }
        }
    }

    private fun setupVideos(videos: List<Video>) {
        viewBinding.progressBar.showView(false)
        viewModel.bindVideos(videos)
        adapter.submitList(videos)
    }

    private fun showLoading() {
        viewBinding.progressBar.showView()
    }

    private fun showError(exception: Exception) {
        viewBinding.progressBar.showView(false)
        Toast.makeText(this, "There was an error loading ${exception.message}", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setMetaData(metaData: MetaData) {
        viewBinding.apply {
            tvTitle.text = metaData.title
            tvDescription.text = metaData.description
            tvDate.text = metaData.date
            tvTags.text = tagsFormat(metaData.tags)
        }
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            viewBinding.videoView
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    private fun checkOrientation(config: Configuration) {
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterInFullScreen()
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullScreen()
        }
    }

    private fun exitFullScreen() {
        viewBinding.svContainer.showView(true)
        viewBinding.bPlayer.referencedIds = intArrayOf(R.id.gl_player_size)
        showSystemUi()
    }

    private fun enterInFullScreen() {
        viewBinding.svContainer.showView(false)
        viewBinding.bPlayer.referencedIds = intArrayOf(R.id.gl_player_bottom)
        hideSystemUi()
    }

    private fun updatePipParams(): PictureInPictureParams {
        return PictureInPictureParams.Builder()
            .setActions(PipActionsDelegate.createPipAction(this, viewModel.isPlaying.value == true))
            .build()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        checkOrientation(newConfig)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (viewModel.isPlaying.value == true) {
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
                    PlayerActions.Play -> viewModel.play()
                    PlayerActions.Pause -> viewModel.pause()
                }
            }
        } else {
            pipActionsDelegate.clearReceiver()
        }

        viewBinding.videoView.useController = !isInPictureInPictureMode
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
