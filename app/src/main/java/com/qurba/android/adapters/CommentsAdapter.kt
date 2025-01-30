package com.qurba.android.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qurba.android.R
import com.qurba.android.databinding.ItemCommentBinding
import com.qurba.android.databinding.ItemViewRecentBinding
import com.qurba.android.network.models.CommentModel
import com.qurba.android.ui.comments.view_models.CommentItemViewModel
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment.CommentActionEvents
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.CommentsCallBack
import com.qurba.android.utils.ItemClickListener
import java.text.NumberFormat
import java.util.ArrayList

class CommentsAdapter(
    activity: BaseActivity,
    val commentsList: MutableList<CommentModel?>,
    val _id: String,
    val type: String,
    val addCommentCallBack: CommentsCallBack?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: BaseActivity = activity
    private val inflater: LayoutInflater = LayoutInflater.from(activity)
    private var itemClickEvents: ItemClickListener? = null
    private var commentActionEvents: CommentActionEvents? = null

    private val TYPE_VIEW = 1
    private val TYPE_COMMENT = 2
    private var isHasPrevious = false

    fun setHasPrevious(isHasPrevious: Boolean) {
        this.isHasPrevious = isHasPrevious
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && isHasPrevious)
            return TYPE_VIEW

        return TYPE_COMMENT
    }

    fun setItemClickEvents(itemClickEvents: ItemClickListener?) {
        this.itemClickEvents = itemClickEvents
    }

    fun setCommentActionEvents(commentActionEvents: CommentActionEvents?) {
        this.commentActionEvents = commentActionEvents
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_COMMENT -> return CommentViewHolder(
                inflater.inflate(
                    R.layout.item_comment,
                    parent,
                    false
                )
            )
            else -> return RecentViewHolder(
                inflater.inflate(
                    R.layout.item_view_recent,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_COMMENT -> {
                val pos = if (isHasPrevious) position - 1 else position
                val commentModel = commentsList[pos]
                if (commentModel?.user == null) return
                commentModel?.parentId = _id
                commentModel?.type = type
                val commentItemViewModel = CommentItemViewModel(activity, commentModel!!)
                addCommentCallBack?.let { commentItemViewModel.setAddCommentCallBack(it) }
                (holder as CommentViewHolder).itemBinding!!.viewModel = commentItemViewModel
                holder.itemBinding!!.replyLl.visibility = View.GONE
                holder.itemBinding!!.parentRv.setOnLongClickListener(
                    commentItemViewModel.commentLongPress(
                        false,
                        pos
                    )
                )
                holder.itemBinding!!.viewMoreTv.setOnClickListener {
                    itemClickEvents?.onClick(
                        commentModel._id
                    )
                }
                holder.itemBinding!!.commentsReplyTv.setOnClickListener {
                    itemClickEvents?.onClick(
                        commentModel._id
                    )
                }
                holder.itemBinding!!.parentRv.setOnClickListener {
                    itemClickEvents?.onClick(
                        commentModel._id
                    )
                }
                commentActionEvents?.let { commentItemViewModel.setCommentActionEvents(it) }
                Glide.with(activity).load(commentModel.user.profilePictureUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(holder.itemBinding!!.userImageIv)
                setCommentsReplies(activity, holder, commentModel)
            }
            else -> {
                (holder as RecentViewHolder)?.itemBinding?.previousCommentsTv?.setOnClickListener {
                    itemClickEvents?.onClick(
                        "view-more"
                    )
                }
            }
        }

    }

    private fun setCommentsReplies(
        context: Context,
        viewHolder: CommentViewHolder,
        commentModel: CommentModel
    ) {
        if (commentModel.replies == null || commentModel.replies.isEmpty()) {
            viewHolder.itemBinding!!.viewMoreTv.visibility = View.GONE
            viewHolder.itemBinding!!.commentsContainerFm.visibility = View.GONE
            viewHolder.itemBinding!!.commentsContainerFm.removeAllViews()
            return
        }
        //in case of comments not replies shown
        if (commentModel.repliesCount > 2) {
            viewHolder.itemBinding!!.viewMoreTv.visibility = View.VISIBLE
            viewHolder.itemBinding!!.viewMoreTv.text =
                activity.getString(R.string.view) + " " + (NumberFormat.getInstance()
                    .format(commentModel.repliesCount - 2)) + " " + activity.getString(R.string.more_replies)
        }
        viewHolder.itemBinding!!.commentsContainerFm.visibility = View.VISIBLE
        viewHolder.itemBinding!!.commentsContainerFm.removeAllViews()
        val count = if (commentModel.repliesCount > 2) 2 else commentModel.replies.size
        for (i in 0 until count) {
            val replyModel = commentModel.replies[i]
            if (replyModel?.user == null) return
            replyModel?.type = type
            val binding: ItemCommentBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_comment,
                viewHolder.itemBinding!!.commentsContainerFm,
                false
            )
            val commentItemViewModel = CommentItemViewModel(activity, replyModel!!)
            binding.replyLl.visibility = View.GONE
            binding.commentsReplyTv.visibility = View.GONE
            binding.viewModel = commentItemViewModel
            replyModel.parentId = commentModel.parentId
            binding.parentRv.setOnLongClickListener(
                commentItemViewModel.commentLongPress(
                    true,
                    viewHolder.adapterPosition
                )
            )
            commentActionEvents?.let { commentItemViewModel.setCommentActionEvents(it) }
            Glide.with(context).load(replyModel?.user?.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder).into(binding.userImageIv)
            viewHolder.itemBinding!!.commentsContainerFm.addView(binding.root)
        }
    }

    fun addLastComment(commentModel: CommentModel) {
        //if (productCommentModels.isEmpty()) {
        commentsList.add(commentModel)
//        } else {
//            productCommentModels.add(0,  commentModel)
//        }
        notifyItemInserted(commentsList.size - 1)
    }

    fun updateLastComment(commentModel: CommentModel, isReply: Boolean, commentPosition: Int) {
        if (isReply) {//edit on comment reply
            commentsList[commentPosition]?.replies?.indexOf(commentModel)?.let {
                commentsList[commentPosition]?.replies?.set(it, commentModel)
                notifyItemChanged(commentPosition)
            }
        } else {//edit on comment itself
            commentsList[commentsList.indexOf(commentModel)] = commentModel
            notifyItemChanged(commentsList.indexOf(commentModel))
        }
    }

    fun deleteComment(commentModel: CommentModel?, isReply: Boolean, commentPosition: Int) {
        if (isReply) {//delete comment reply
            commentsList[commentPosition]?.replies?.indexOf(commentModel)?.let {
                commentsList[commentPosition]?.replies?.remove(commentModel)
                notifyItemChanged(commentPosition)
            }
        } else {//delete comment itself
            val index = commentsList.indexOf(commentModel)
            commentsList.remove(commentModel)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int {
        return if (isHasPrevious) commentsList.size + 1 else commentsList.size
    }

    class CommentViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var itemBinding: ItemCommentBinding? = DataBindingUtil.bind(itemView!!)
    }

    class RecentViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var itemBinding: ItemViewRecentBinding? = DataBindingUtil.bind(itemView!!)
    }

    fun add(response: CommentModel?) {
        if (!commentsList.contains(response)) {
            commentsList.add(response)
            notifyItemInserted(commentsList.size - 1)
        }
    }

    fun addAll(postItems: List<CommentModel>) {
        if (postItems != null && postItems.isNotEmpty()) {
            for (response in postItems) {
                add(response)
            }
        }
    }

    fun addAllRecent(postItems: List<CommentModel>) {
        if (postItems != null && postItems.isNotEmpty()) {
            for (response in postItems) {
                addRecent(response)
            }
        }
    }

    fun addRecent(response: CommentModel?) {
        if (!commentsList.contains(response)) {
            commentsList.add(0, response)
            notifyItemInserted(0)
        }
    }
}