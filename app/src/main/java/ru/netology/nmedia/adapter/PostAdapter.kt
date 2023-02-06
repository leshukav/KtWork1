package ru.netology.nmedia.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.Placeholder
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import ru.netology.nmedia.DisplayCount
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.enumeration.AttachmentType

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onPlay(post: Post)
//    fun onPostFragment(post: Post)
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
     //       fabPlay.hide()
                author.text = post.author
                if (post.authorAvatar != "") {
                    val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
                    binding.authorAvatar.load(url)
                } else {
                    authorAvatar.setImageResource(R.drawable.ic_error_100)
                }
                if ((post.attachment?.type == AttachmentType.IMAGE) && (post.attachment?.type != null)) {
                    val url = "http://10.0.2.2:9999/images/${post.attachment?.url}"
                    Glide.with(binding.play)
                        .load(url)
                        .timeout(10_000)
                        .into(binding.play)
                }
                publish.text = post.published.toString()
                content.text = post.content
                like.text = DisplayCount.logic(post.likes)
                share.text = DisplayCount.logic(post.share)
                visibility.text = DisplayCount.logic(post.viewEye)
                like.isChecked = post.likedByMe


                play.setOnClickListener {
                    onInteractionListener.onPlay(post)
                }
                fabPlay.setOnClickListener {
                    onInteractionListener.onPlay(post)
                }
//            content.setOnClickListener{
//               onInteractionListener.onPostFragment(post)
//            }
                like.setOnClickListener {
                    onInteractionListener.onLike(post)
                }
                share.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.option_post)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }
                                else -> false
                            }

                        }
                    }.show()
                }
            }

    }
    fun ImageView.load(url: String,
    @DrawableRes placeholder: Int = R.drawable.ic_loading_100dp,
    @DrawableRes fallBack: Int = R.drawable.ic_error_100,
    timeOutMs: Int = 10_000) {
        Glide.with(this)
            .load(url)
            .placeholder(placeholder)
            .error(fallBack)
            .timeout(timeOutMs)
            .circleCrop()
            .into(this)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}