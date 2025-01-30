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
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.paginate.Paginate
import com.qurba.android.R
import com.qurba.android.adapters.CommentsAdapter
import com.qurba.android.databinding.FragmentCommentsBinding
import com.qurba.android.network.models.CommentModel
import com.qurba.android.network.models.response_models.CommentsListResponse
import com.qurba.android.network.models.response_models.RepliesResponse
import com.qurba.android.ui.comments.view_models.CommentsViewModel
import com.qurba.android.utils.*
import com.qurba.android.utils.Constants.PAGE_START
import com.qurba.android.utils.extenstions.addFragment
import com.qurba.android.utils.extenstions.shortToast
import com.qurba.android.utils.extenstions.showKeyboard
import com.vanniktech.emoji.EmojiPopup
import java.util.*


class CommentsFragment : Fragment(), ItemClickListener,
    EditDeleteOverlayFragment.CommentActionEvents {
    private var addCommentCallBack: CommentsCallBack? = null
    private lateinit var binding: FragmentCommentsBinding
    private val commentList: MutableList<CommentModel?> = ArrayList()
    private var commentsAdapter: CommentsAdapter? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var viewModel: CommentsViewModel? = null
    private var commentModel: CommentModel? = null
    private var type: String = ""
    private var currentPage = PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var isReply = false
    private var includeDelimiter = false
    private var commentPosition = -1
    private var delimiter = ""
    private var direction = "older"

    private fun initListeners() {
        val emojiPopup = EmojiPopup.Builder.fromRootView(binding.root).build(binding.addCommentEt)
        binding.emjoisIv.setOnClickListener { emojiPopup.toggle() }

        binding!!.commentsSendIv.setOnClickListener {
            if (sharedPref.isGuest) (activity as BaseActivity?)!!.showLoginDialog(*arrayOfNulls(0)) else {
                when {
                    requireArguments().containsKey("product_id") -> {
                        if (viewModel!!.isEdit) viewModel!!.updateProductComment() else viewModel!!.addProductComment()
                    }
                    requireArguments().containsKey("offer_id") -> {
                        if (viewModel!!.isEdit) viewModel!!.updateOfferComment() else viewModel!!.addOfferComment()
                    }
                    else -> {
                        if (viewModel!!.isEdit) viewModel!!.updatePlaceComment() else viewModel!!.addPlaceComment()
                    }
                }
            }
        }

    }

    fun refreshCommentModel(commentModel: CommentModel) {
        if (commentList.indexOf(commentModel) == -1) {
            commentList.add(0, commentModel)
            commentsAdapter?.notifyItemInserted(0)
            binding.commentsRecyclerview.scrollToPosition(0)
        } else {
            commentList[commentList.indexOf(commentModel)] = commentModel
            commentsAdapter?.notifyItemChanged(commentList.indexOf(commentModel))
        }
    }

    fun getData() {
        when {
            requireArguments().containsKey("product_id") -> {
                viewModel!!.getProductComments(delimiter, direction, includeDelimiter)
            }
            requireArguments().containsKey("offer_id") -> {
                viewModel!!.getOfferComments(delimiter, direction, includeDelimiter)
            }
            else -> {
                viewModel!!.getPlaceComments(delimiter, direction, includeDelimiter)
            }
        }
    }

    fun setIngredients() {
        when {
            requireArguments().containsKey("product_id") -> {
                viewModel!!.id = requireArguments().getString("product_id")
                type = "product"
            }
            requireArguments().containsKey("offer_id") -> {
                viewModel!!.id = requireArguments().getString("offer_id")
                type = "offer"
            }
            else -> {
                viewModel!!.id = requireArguments().getString("place_id")
                type = "place"
            }
        }
    }

    private fun initialization() {
        if (requireArguments().getString("like_count") != "0") viewModel!!.likeCount.set(
            requireArguments().getString("like_count") + " " + getString(R.string.like)
        )
        if (requireArguments().getString("share_count") != "0") viewModel!!.shareCount.set(
            requireArguments().getString("share_count") + " " + getString(R.string.shares)
        )
        currentPage =
            if (arguments?.containsKey("comments-page")!!) arguments?.getInt("comments-page")!! else PAGE_START
        delimiter =
            if (arguments?.containsKey(Constants.COMMENT_ID)!!) arguments?.getString(Constants.COMMENT_ID)!! else ""
        includeDelimiter = arguments?.containsKey("includeDelimiter")!!
//        direction = if (arguments?.containsKey("includeDelimiter")!!) "newer" else "older"

        setIngredients()
        initListeners()
        initializeAdapters()
        initializeObservables()
        viewModel!!.setAddCommentCallBack(addCommentCallBack)
        Glide.with(requireContext()).load(sharedPref.user?.profilePictureUrl)
            .placeholder(R.drawable.ic_profile_placeholder).into(binding.profileAvatarIv)

        Handler(Looper.getMainLooper()).postDelayed({
            if (requireArguments().containsKey(Constants.IS_REPLY) && requireArguments().getBoolean(
                    Constants.IS_REPLY
                )
            ) {
                addReplyFragment(requireArguments().getString(Constants.COMMENT_ID).toString())
            }
        }, 0)
    }

    private fun initializeAdapters() {
        commentsAdapter = CommentsAdapter(
            requireActivity() as BaseActivity,
            this.commentList,
            viewModel!!.id,
            type,
            addCommentCallBack
        )
        binding!!.commentsRecyclerview.adapter = commentsAdapter

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = false
        linearLayoutManager.reverseLayout = false

        binding.commentsRecyclerview.layoutManager = linearLayoutManager

        commentsAdapter!!.setItemClickEvents(this)
        commentsAdapter!!.setCommentActionEvents(this)
        binding!!.shimmerLayout.startShimmer()

        val callbacks: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                this@CommentsFragment.isLoading = true
                getData()
                this@CommentsFragment.direction = "older"
                this@CommentsFragment.includeDelimiter = false
            }

            // Indicate whether new page loading is in progress or not
            override fun isLoading(): Boolean {
                return this@CommentsFragment.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                // Indicate whether all data (pages) are loaded or not
                return isLastPage
            }
        }
//
        Paginate.with(binding.commentsRecyclerview, callbacks)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(true)
            .build()
    }

    private fun initializeObservables() {
        viewModel?.commentsObservable?.observe(
            viewLifecycleOwner,
            { payload: CommentsListResponse ->
                Handler(Looper.getMainLooper()).postDelayed(
                    { publishComments(payload) },
                    50
                )
            })

        viewModel!!.addCommentsObservable()
            .observe(viewLifecycleOwner, Observer { commentsList: List<CommentModel> ->
                commentsAdapter?.addAllRecent(commentsList)
                binding.commentsRecyclerview.scrollToPosition(0)
            })
        viewModel!!.updateCommentsObservable().observe(
            viewLifecycleOwner,
            Observer { comment: CommentModel ->
                commentsAdapter?.updateLastComment(
                    comment,
                    isReply,
                    commentPosition
                )
            })
        viewModel!!.deleteCommentsObservable().observe(viewLifecycleOwner, Observer {
            commentsAdapter?.deleteComment(this.commentModel, isReply, commentPosition)
            viewModel!!.isDataFound = commentList.isNotEmpty()
        })
    }

    private fun publishComments(payload: CommentsListResponse) {
        isLoading = false
        if (currentPage == 1) {
            binding!!.shimmerLayout.stopShimmer()
            binding!!.shimmerLayout.visibility = View.GONE
            binding.commentsRecyclerview.scrollToPosition(0)
            commentsAdapter?.setHasPrevious(viewModel?.isHasPrevious == true)
        }

        if (payload.docs.isEmpty() || commentsAdapter?.commentsList?.size == payload.total) {
            if (direction == "older") {
                isLastPage = true
                commentsAdapter?.notifyDataSetChanged()
            }
            if (direction == "newer") shortToast(getString(R.string.have_recent_comments))
            return
        }

        if (direction == "newer")
            commentsAdapter?.addAllRecent(payload.docs)
        else{
            commentsAdapter?.addAll(payload.docs)
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
            layoutInflater, R.layout.fragment_comments, viewGroup, false
        )
        val view = binding.root
        viewModel = ViewModelProvider(this).get(CommentsViewModel::class.java)
        binding.viewModel = viewModel
        initialization()
        return view
    }

    fun setAddCommentCallBack(addCommentCallBack: CommentsCallBack?) {
        this.addCommentCallBack = addCommentCallBack
    }

    override fun onClick(_id: String) {
        if (_id == "view-more") {
            currentPage = 1
            if (commentList.isNotEmpty()) this@CommentsFragment.delimiter =
                commentList[0]?._id.toString()
            direction = "newer"
            includeDelimiter = false
            getData()
            return
        }
        addReplyFragment(_id)
    }

    private fun addReplyFragment(commentId: String) {
        val repliesFragment = RepliesFragment()
        val bundle = Bundle()
        bundle.putString(
            if (requireArguments().containsKey("offer_id")) "offer_id" else if (requireArguments().containsKey(
                    "place_id"
                )
            ) "place_id" else "product_id", viewModel?.id
        )//for products and comments
        bundle.putString(Constants.COMMENT_ID, commentId)
        repliesFragment.setAddCommentCallBack(addCommentCallBack)
        repliesFragment.arguments = bundle
        addFragment((requireView().parent as ViewGroup).id, repliesFragment)
    }

    override fun onCommentClick(
        commentModel: CommentModel,
        isEdit: Boolean,
        isReply: Boolean,
        commentPosition: Int
    ) {
        binding.viewModel!!.setCommentModel(commentModel)
        this.isReply = isReply
        this.commentPosition = commentPosition
        if (isEdit) {
            binding.viewModel!!.isEdit = true
//            binding.viewModel!!.commentText.set(commentModel.comment)
            binding.addCommentEt.setText(commentModel.comment)
            binding.addCommentEt.setSelection(binding.addCommentEt.text!!.length);
            binding.addCommentEt.isFocusableInTouchMode = true
            binding.addCommentEt.requestFocus()
            (activity as BaseActivity).showKeyboard()
        } else {
            this.commentModel = commentModel
            viewModel!!.setCommentsCount(commentModel.repliesCount + 1)
            (activity as BaseActivity).showConfirmDialog(commentModel.repliesCount > 0, true) {
                when {
                    requireArguments().containsKey("product_id") -> {
                        viewModel?.deleteProductComment()
                    }
                    requireArguments().containsKey("offer_id") -> {
                        viewModel?.deleteOfferComment()
                    }
                    else -> {
                        viewModel!!.deletePlaceComment()
                    }
                }
            }
        }
    }
}