package com.ianpedraza.streamingbootcamp.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ianpedraza.streamingbootcamp.databinding.ItemVideoBinding
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.utils.ViewExtensions.from

class ThumbnailAdapter(
    private val onAction: (Action) -> Unit
) : ListAdapter<Video, ThumbnailAdapter.ViewHolder>(VideoDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onAction)
    }

    class ViewHolder private constructor(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemVideoBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: Video, onAction: (Action) -> Unit) {
            with(binding) {
                textViewTitle.text = item.title
                imageViewThumbnail.from(item.thumbnail)
                root.setOnClickListener { onAction(Action.OnClick(item)) }
            }
        }
    }

    sealed class Action {
        data class OnClick(val item: Video) : Action()
    }
}

private object VideoDiffUtil : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(
        oldItem: Video,
        newItem: Video
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Video,
        newItem: Video
    ): Boolean = oldItem == newItem
}
