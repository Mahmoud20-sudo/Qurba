package com.qurba.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qurba.android.R
import com.qurba.android.databinding.ItemReplyBinding
import com.qurba.android.network.models.CommentModel
import com.qurba.android.ui.comments.view_models.CommentItemViewModel
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment.CommentActionEvents
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.BaseViewModel
import com.qurba.android.utils.SharedPreferencesManager

class RepliesAdapter(activity: BaseActivity, val repliesList: MutableList<CommentModel?>, val _id: String, val type: String) : RecyclerView.Adapter<RepliesAdapter.ViewHolder>() {
    private val activity: BaseActivity = activity
    private val inflater: LayoutInflater = LayoutInflater.from(activity)
    private var commentActionEvents: CommentActionEvents? = null
    fun setCommentActionEvents(commentActionEvents: CommentActionEvents?) {
        this.commentActionEvents = commentActionEvents
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_reply, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //OffersModel offersModel = offersModels.get(position);
        val commentModel = repliesList[position]
        commentModel?.parentId = _id
        commentModel?.type = type
        val commentItemViewModel = CommentItemViewModel(activity, commentModel!!)
        holder.itemBinding!!.viewModel = commentItemViewModel
        holder.itemView.setOnLongClickListener(commentItemViewModel.commentLongPress(true, position))
        commentItemViewModel.setCommentActionEvents(commentActionEvents!!)
//        holder.itemBinding!!.commentsReplyTv.visibility = View.GONE
        Glide.with(activity).load(commentModel.user.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.itemBinding!!.userImageIv)
    }

    override fun getItemCount(): Int {
        return repliesList.size
    }

    fun addLastComment(commentModel: CommentModel) {
        if (repliesList.isEmpty()) {
            repliesList.add(commentModel)
            notifyItemInserted(repliesList.size - 1)
        } else {
            repliesList.add(0, commentModel)
            notifyItemInserted(0)
        }
    }

    fun updateLastComment(commentModel: CommentModel) {
        repliesList[repliesList.indexOf(commentModel)] = commentModel
        notifyItemChanged(repliesList.indexOf(commentModel))
    }

    fun deleteComment(commentModel: CommentModel?) {
        val index = repliesList.indexOf(commentModel)
        repliesList.remove(commentModel)
        notifyItemRemoved(index)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var itemBinding: ItemReplyBinding? = DataBindingUtil.bind(itemView!!)
    }

    fun addAllRecent(postItems: List<CommentModel>) {
        if (postItems != null && postItems.isNotEmpty()) {
            for (response in postItems) {
                addRecent(response)
            }
        }
    }

    fun addRecent(response: CommentModel?) {
        if (!repliesList.contains(response)) {
            repliesList.add(0, response)
            notifyItemInserted(0)
        }
    }

    fun add(response: CommentModel?) {
        if (!repliesList.contains(response)) {
            repliesList.add(response)
            notifyItemInserted(repliesList.size - 1)
        }
    }

    fun addAll(postItems: List<CommentModel>) {
        if (postItems != null && postItems.isNotEmpty()) {
            for (response in postItems) {
                add(response)
            }
        }
    }
    
}