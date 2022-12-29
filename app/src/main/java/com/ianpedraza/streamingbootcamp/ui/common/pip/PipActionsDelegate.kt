package com.ianpedraza.streamingbootcamp.ui.common.pip

import android.app.PendingIntent
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.os.bundleOf
import com.ianpedraza.streamingbootcamp.R

class PipActionsDelegate(private val context: Context) {

    private val pipActionsReceiver = PipActionsReceiver()

    fun registerReceiver(): PipActionsReceiver {
        context.registerReceiver(
            pipActionsReceiver,
            PipActionsReceiver.intentFilter
        )

        return pipActionsReceiver
    }

    fun clearReceiver() {
        try {
            context.unregisterReceiver(pipActionsReceiver)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "clearReceiver: ", e)
        }
    }

    companion object {
        private const val TAG = "PipActionsDelegate"

        fun createPipActions(context: Context, isPlaying: Boolean): List<RemoteAction> {
            val playAnPauseAction = if (isPlaying) {
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.exo_styled_controls_pause),
                    context.getString(R.string.pause_text),
                    context.getString(R.string.pause_text_description),
                    createPendingIntent(context, 0, PlayerActions.Pause)
                )
            } else {
                RemoteAction(
                    Icon.createWithResource(context, R.drawable.exo_styled_controls_play),
                    context.getString(R.string.play_text),
                    context.getString(R.string.play_text_description),
                    createPendingIntent(context, 0, PlayerActions.Play)
                )
            }

            return listOf(playAnPauseAction)
        }

        private fun createPendingIntent(
            context: Context,
            requestCode: Int,
            type: PlayerActions
        ): PendingIntent {
            val intent = Intent(PipActionsReceiver.CONTROLS_ACTION).apply {
                setPackage(context.packageName)

                val extras = bundleOf(
                    PipActionsReceiver.EXTRAS_CONTROLS_ACTION to type
                )

                putExtras(extras)
            }

            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}
