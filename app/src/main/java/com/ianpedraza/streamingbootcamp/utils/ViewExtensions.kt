package com.ianpedraza.streamingbootcamp.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ianpedraza.streamingbootcamp.R

object ViewExtensions {
    fun View.showView(show: Boolean = true) {
        visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun ImageView.from(
        urlImage: String
    ) = Glide.with(this)
        .load(urlImage)
        .centerCrop()
        .placeholder(R.drawable.ic_image_placeholder)
        .error(R.drawable.ic_image_broken)
        .into(this)
}
