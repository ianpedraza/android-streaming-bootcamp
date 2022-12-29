package com.ianpedraza.streamingbootcamp.ui.common.pip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ianpedraza.streamingbootcamp.utils.Utils.parcelable

class PipActionsReceiver : BroadcastReceiver() {
    private val _pipAction: MutableLiveData<PlayerActions> = MutableLiveData()
    val pipAction: LiveData<PlayerActions> = _pipAction

    override fun onReceive(context: Context?, intent: Intent?) {
        _pipAction.value = intent?.parcelable(EXTRAS_CONTROLS_ACTION)
    }

    companion object {
        const val CONTROLS_ACTION = "controls_action"
        const val EXTRAS_CONTROLS_ACTION = "extra_controls_actions"

        val intentFilter = IntentFilter().apply {
            addAction(CONTROLS_ACTION)
        }
    }
}
