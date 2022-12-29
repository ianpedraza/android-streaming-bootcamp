package com.ianpedraza.streamingbootcamp.ui.common.pip

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class PlayerActions : Parcelable {
    object Play : PlayerActions()
    object Pause : PlayerActions()
}
