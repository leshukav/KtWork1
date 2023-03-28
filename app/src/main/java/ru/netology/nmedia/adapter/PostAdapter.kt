package ru.netology.nmedia.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.DisplayCount
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardTimingBinding
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.AttachmentType
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onImage(post: Post)
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TimingSeparator -> R.layout.card_timing
            null -> error("unknow layout")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.card_timing -> {
                val binding = CardTimingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                TimingViewHolder(binding)
            }
            else -> error("unkhow viewtype $viewType")
        }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as PostViewHolder)?.bind(item)
            is TimingSeparator -> (holder as TimingViewHolder).bind(item)
            null -> error("unknow layout")
        }
    }

}

class AdViewHolder(
    private val binding: CardAdBinding,
): RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
       val url = "http://10.0.2.2:9999/media/${ad.image}"
        Glide.with(binding.image)
            .load(url)
            .timeout(10_000)
            .into(binding.image)
    }
}

class TimingViewHolder(
    private val binding: CardTimingBinding,
): RecyclerView.ViewHolder(binding.root){
    fun bind(time: TimingSeparator) {
        when (time.timing) {
            Separator.TODAY -> binding.textTiming.text = "TODAY"
            Separator.YERSTUDAY -> binding.textTiming.text = "YERSTEODAY"
            Separator.LASTWEEK -> binding.textTiming.text = "LASTWEEK"
            else -> { null}
        }

    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {


    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(post: Post) {
        if (!post.hidden) {
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
                    image.visibility = View.VISIBLE
                    val url = "http://10.0.2.2:9999/media/${post.attachment?.url}"
                    Glide.with(binding.image)
                        .load(url)
                        .timeout(10_000)
                        .into(binding.image)
                } else {
                    image.visibility = View.GONE
                }

                val sdf = SimpleDateFormat("dd-MMMM-yyyy, HH:mm:ss", Locale.ENGLISH)

                publish.text = sdf.format(post.published * 1000L)
                content.text = post.content
                like.text = DisplayCount.logic(post.likes)
                share.text = DisplayCount.logic(990)
                visibility.text = DisplayCount.logic(1)
                like.isChecked = post.likedByMe


                image.setOnClickListener {
                    onInteractionListener.onImage(post)
                }
                fabPlay.setOnClickListener {
                    onInteractionListener.onImage(post)
                }

                like.setOnClickListener {
                    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F,1.25F,1F)
                    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F,1.25F,1F)
                    ObjectAnimator.ofPropertyValuesHolder(it,scaleX,scaleY).apply {
                        duration = 500
                        repeatCount = 100
                        interpolator = BounceInterpolator()
                    }.start()
                    onInteractionListener.onLike(post)
                }
                share.setOnClickListener {
                    onInteractionListener.onShare(post)
                }
                menu.isVisible = post.ownedByMe
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
    }


}
fun ImageView.load(
    url: String,
    @DrawableRes placeholder: Int = R.drawable.ic_loading_100dp,
    @DrawableRes fallBack: Int = R.drawable.ic_error_100,
    timeOutMs: Int = 10_000
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(fallBack)
        .timeout(timeOutMs)
        .circleCrop()
        .into(this)
}

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

}