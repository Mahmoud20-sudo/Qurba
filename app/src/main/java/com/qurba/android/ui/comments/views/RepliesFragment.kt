package com.qurba.android.ui.comments.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.paginate.Paginate
import com.qurba.android.R
import com.qurba.android.adapters.RepliesAdapter
import com.qurba.android.databinding.FragmentRepliesBinding
import com.qurba.android.network.models.CommentModel
import com.qurba.android.network.models.response_models.RepliesResponse
import com.qurba.android.ui.comments.view_models.RepliesViewModel
import com.qurba.android.utils.*
import com.qurba.android.utils.SystemUtils.hideKeyBoard
import com.qurba.android.utils.extenstions.shortToast
import com.qurba.android.utils.extenstions.showKeyboard
import com.vanniktech.emoji.EmojiPopup
import java.util.*

class RepliesFragment : Fragment(), EditDeleteOverlayFragment.CommentActionEvents {

    private var currentPage = 1
    private var addCommentCallBack: CommentsCallBack? = null
    private lateinit var binding: FragmentRepliesBinding
    private val commentList: MutableList<CommentModel?> = ArrayList()
    private var commentsAdapter: RepliesAdapter? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private lateinit var viewModel: RepliesViewModel

    private var isLastPage = false
    private var isLoading = false

    private var includeDelimiter = false
    private var delimiter = ""
    private var direction = "older"

    private fun initListeners() {
        val emojiPopup = EmojiPopup.Builder.fromRootView(binding.root).build(binding.addCommentEt)
        binding.emjoisIv.setOnClickListener { emojiPopup.toggle() }

        binding.repliesBackIv.setOnClickListener {
            (parentFragment as OverlayFragment)?.refreshCommentModel(viewModel?.parentCommentModel)
            hideKeyBoard(QurbaApplication.getContext(), binding.repliesBackIv)
        }

        binding!!.commentsSendIv.setOnClickListener {
            if (sharedPref.isGuest) (activity as BaseActivity?)!!.showLoginDialog(*arrayOfNulls(0)) else {
                when {
                    requireArguments().containsKey("product_id") -> {
                        if (viewModel?.isEdit) viewModel?.updateProductReply() else viewModel?.addProductReply()
                    }
                    requireArguments().containsKey("offer_id") -> {
                        if (viewModel?.isEdit) viewModel?.updateOfferReply() else viewModel?.addOfferReply()
                    }
                    else -> {
                        if (viewModel?.isEdit) viewModel?.updatePlaceReply() else viewModel?.addPlaceReply()
                    }
                }
            }
        }

        binding.previousCommentsTv.setOnClickListener {
            if (commentList.isNotEmpty()) delimiter = commentList[0]?._id.toString()
            direction = "newer"
            includeDelimiter = false
            getData()
        }
    }

    private fun initialization() {
        delimiter =
            if (arguments?.containsKey("delimiter")!!) arguments?.getString("delimiter")!! else ""
        includeDelimiter = arguments?.containsKey("includeDelimiter")!!
//        direction = if (arguments?.containsKey("includeDelimiter")!!) "newer" else "older"

        viewModel?.setActivity(requireActivity() as BaseActivity?)
        viewModel?.setCommentId(arguments?.getString(Constants.COMMENT_ID))
        viewModel?.setAddCommentCallBack(addCommentCallBack)
        setIngredients()
        initListeners()
        initializeAdapters()
        initializeObservables()
    }

    private fun getData() {
        when {
            requireArguments().containsKey("product_id") -> {
                viewModel.getProductReplies(currentPage, delimiter, direction, includeDelimiter)
            }
            requireArguments().containsKey("offer_id") -> {
                viewModel.getOfferReplies(currentPage, delimiter, direction, includeDelimiter)
            }
            else -> {
                viewModel.getPlaceReplies(currentPage, delimiter, direction, includeDelimiter)
            }
        }
    }

    private fun setIngredients() {
        when {
            requireArguments().containsKey("product_id") -> {
                viewModel.id = requireArguments().getString("product_id")
                viewModel?.type = "product"
            }
            requireArguments().containsKey("offer_id") -> {
                viewModel.id = requireArguments().getString("offer_id")
                viewModel?.type = "offer"
            }
            else -> {
                viewModel.id = requireArguments().getString("place_id")
                viewModel?.type = "place"
            }
        }
    }

    private fun initializeAdapters() {
        commentsAdapter = RepliesAdapter(
            requireActivity() as BaseActivity,
            commentList,
            viewModel.id,
            viewModel?.type
        )
        binding!!.repliesRv.adapter = commentsAdapter

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = false
        linearLayoutManager.reverseLayout = false

        binding.repliesRv.layoutManager = linearLayoutManager

        commentsAdapter?.setCommentActionEvents(this)
//        binding.repliesRv.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
//                getData()
//            }
//        })


        val callbacks: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                this@RepliesFragment.isLoading = true
                getData()
                this@RepliesFragment.direction = "older"
                this@RepliesFragment.includeDelimiter = false
            }

            // Indicate whether new page loading is in progress or not
            override fun isLoading(): Boolean {
                return this@RepliesFragment.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                // Indicate whether all data (pages) are loaded or not
                return isLastPage
            }
        }
//
        Paginate.with(binding.repliesRv, callbacks)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(currentPage > 1)
            .build()
    }

    private fun initializeObservables() {
        viewModel.commentsObservable.observe(
            viewLifecycleOwner, { payload: RepliesResponse -> Handler(Looper.getMainLooper()).postDelayed({ publishComments(payload) }, 50) })
        viewModel.addCommentsObservable()
            .observe(viewLifecycleOwner, Observer { commentList: List<CommentModel> ->
                commentsAdapter?.addAllRecent(commentList)
                binding?.repliesRv.scrollToPosition(0)
            })
        viewModel.updateCommentsObservable().observe(
            viewLifecycleOwner,
            Observer { comment: CommentModel -> commentsAdapter?.updateLastComment(comment) })
        viewModel.deleteCommentsObservable()
            .observe(viewLifecycleOwner, Observer { comment: CommentModel ->
                commentsAdapter?.deleteComment(comment)
                viewModel.parentCommentModel.repliesCount--
            })
    }

    private fun publishComments(payload: RepliesResponse) {
        context?.let {
            Glide.with(it).load(viewModel?.parentCommentModel?.user?.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder).into(binding.userImageIv)
            Glide.with(it).load(sharedPref?.user?.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder).into(binding.profileAvatarIv)
        }

        isLoading = false
        if (currentPage == 1) {
            binding!!.shimmerLayout.stopShimmer()
            binding!!.shimmerLayout.visibility = View.GONE
            binding.repliesRv.scrollToPosition(0)
        }

        if (payload.replies.docs.isEmpty() || commentList.size == payload.replies.total) {
            if (direction == "older") {
                isLastPage = true
                commentsAdapter?.notifyDataSetChanged()
            }
            if (direction == "newer") shortToast(getString(R.string.have_recent_comments))
            return
        }

        if (direction == "newer")
            commentsAdapter?.addAllRecent(payload.replies.docs)
        else{
            commentsAdapter?.addAll(payload.replies.docs)
            currentPage++
        }

        delimiter = commentList[commentList.size - 1]?._id.toString()
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_replies, viewGroup, false
        )
        val view = binding.root
        viewModel = ViewModelProvider(this).get(RepliesViewModel::class.java)
        binding.viewModel = viewModel
        initialization()
        return view
    }

    fun setAddCommentCallBack(addCommentCallBack: CommentsCallBack?) {
        this.addCommentCallBack = addCommentCallBack
    }

    override fun onCommentClick(
        replyModel: CommentModel,
        isEdit: Boolean,
        isReply: Boolean,
        commentPosition: Int
    ) {
        binding.viewModel?.setReplyModel(replyModel)
        if (isEdit) {
            binding.viewModel?.isEdit = true
//            binding.viewModel.replyText.set(commentModel.comment)
            binding.addCommentEt.setText(replyModel.comment)
            binding.addCommentEt.setSelection(binding.addCommentEt.text!!.length);
            binding.addCommentEt.isFocusableInTouchMode = true
            binding.addCommentEt.requestFocus()
            (activity as BaseActivity).showKeyboard()
        } else {
//            this.commentModel = commentModel
            (activity as BaseActivity).showConfirmDialog(false, false) {
                when {
                    requireArguments().containsKey("product_id") -> {
                        viewModel?.deleteProductReply()
                    }
                    requireArguments().containsKey("offer_id") -> {
                        viewModel?.deleteOfferReply()
                    }
                    else -> {
                        viewModel?.deletePlaceReply()
                    }
                }
            }
        }
    }
}