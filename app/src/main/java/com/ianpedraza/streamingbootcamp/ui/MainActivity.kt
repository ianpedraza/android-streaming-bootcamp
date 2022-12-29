package com.ianpedraza.streamingbootcamp.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.cast.framework.CastContext
import com.ianpedraza.streamingbootcamp.R
import com.ianpedraza.streamingbootcamp.databinding.ActivityMainBinding
import com.ianpedraza.streamingbootcamp.ui.common.pip.PipActionsDelegate
import com.ianpedraza.streamingbootcamp.ui.player.PlayerFragment
import com.ianpedraza.streamingbootcamp.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navHostFragment.navController
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val fragments = setOf(
        R.id.playerFragment
    )

    private val pipActionsDelegate by lazy {
        PipActionsDelegate(this)
    }

    private var castContext: CastContext? = null

    private val playerFragment: PlayerFragment?
        get() {
            return supportFragmentManager.fragments.firstOrNull { fragment ->
                fragment is PlayerFragment
            } as PlayerFragment?
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
    }

    private fun setupUi() {
        castContext = CastContext.getSharedInstance()
        setupActionBar()
    }

    private fun setupActionBar() {
        appBarConfiguration = AppBarConfiguration.Builder(fragments).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        playerFragment?.onUserLeaveHint()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        playerFragment?.onPictureInPictureModeChanged(isInPictureInPictureMode, pipActionsDelegate)
    }
}
