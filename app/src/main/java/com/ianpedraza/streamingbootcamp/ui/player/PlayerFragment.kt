package com.ianpedraza.streamingbootcamp.ui.player

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.cast.framework.CastButtonFactory
import com.ianpedraza.streamingbootcamp.R
import com.ianpedraza.streamingbootcamp.databinding.FragmentPlayerBinding
import com.ianpedraza.streamingbootcamp.domain.MetaData
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.ui.common.pip.PipActionsDelegate
import com.ianpedraza.streamingbootcamp.ui.common.pip.PlayerActions
import com.ianpedraza.streamingbootcamp.utils.DataState
import com.ianpedraza.streamingbootcamp.utils.MediaUtils
import com.ianpedraza.streamingbootcamp.utils.ViewExtensions.showView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment : Fragment(), MenuProvider {

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    private val playerViewModel: PlayerViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var adapter: ThumbnailAdapter

    private var isPlaying: Boolean = false

    private var mediaRouteMenuItem: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeObservers()
    }

    private fun setupUi() {
        checkOrientation(resources.configuration)
        setupRecyclerView()
        setupMenu()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        adapter = ThumbnailAdapter(::onAction)

        binding.recyclerView.apply {
            this.adapter = this@PlayerFragment.adapter
            this.layoutManager = layoutManager
        }
    }

    private fun onAction(action: ThumbnailAdapter.Action) {
        when (action) {
            is ThumbnailAdapter.Action.OnClick -> playerViewModel.play(action.item)
        }
    }

    private fun subscribeObservers() {
        playerViewModel.player.observe(viewLifecycleOwner) { exoPlayer ->
            binding.videoView.player = exoPlayer
        }

        playerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            this@PlayerFragment.isPlaying = isPlaying
            binding.videoView.keepScreenOn = isPlaying
            requireActivity().setPictureInPictureParams(updatePipParams())
        }

        playerViewModel.metaData.observe(viewLifecycleOwner) { metaData ->
            setMetaData(metaData)
        }

        mainViewModel.videos.observe(viewLifecycleOwner) { dataState ->
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
        Toast.makeText(
            requireActivity(),
            "There was an error loading ${exception.message}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setMetaData(metaData: MetaData) {
        binding.apply {
            tvTitle.text = metaData.title
            tvDescription.text = metaData.description
            tvDate.text = metaData.date
            tvTags.text = MediaUtils.tagsFormat(metaData.tags)
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
        val window = requireActivity().window

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).supportActionBar!!.hide()
        }
    }

    private fun showSystemUi() {
        val window = requireActivity().window

        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.videoView
        ).show(WindowInsetsCompat.Type.systemBars())

        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).supportActionBar!!.show()
        }
    }

    /* FullScreen - End */

    /* PiP - Start */

    fun onUserLeaveHint() {
        if (isPlaying) {
            requireActivity().enterPictureInPictureMode(updatePipParams())
        }
    }

    fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        pipActionsDelegate: PipActionsDelegate
    ) {
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

    private fun updatePipParams(): PictureInPictureParams {
        return PictureInPictureParams.Builder()
            .setActions(PipActionsDelegate.createPipActions(requireActivity(), isPlaying))
            .build()
    }

    /* PiP - End*/

    /* Menu - Start */

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)

        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(
            requireActivity().applicationContext,
            menu,
            R.id.media_route_menu_item
        )
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.media_route_menu_item -> {
                true
            }
            else -> false
        }
    }

    /* Menu - End */

    @UnstableApi
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            playerViewModel.initializePlayer(requireActivity())
        }
    }

    @UnstableApi
    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || !playerViewModel.isPlayerInitialized()) {
            playerViewModel.initializePlayer(requireActivity())
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
