package com.ianpedraza.streamingbootcamp.ui.player

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.recyclerview.widget.LinearLayoutManager
import com.ianpedraza.streamingbootcamp.databinding.ActivityMainBinding
import com.ianpedraza.streamingbootcamp.domain.MetaData
import com.ianpedraza.streamingbootcamp.domain.Video
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        setupUi()
        subscribeObservers()
        viewModel.fetchVideos()
    }

    private fun setupUi() {
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
