package com.qurba.android.ui.comments.views

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qurba.android.R
import com.qurba.android.databinding.DialogCommentsBinding
import com.qurba.android.network.models.CommentModel
import com.qurba.android.ui.comments.view_models.CommentsOverlayViewModel
import com.qurba.android.utils.CommentsCallBack
import com.qurba.android.utils.extenstions.replaceFragmentWithoutAnime
import java.text.NumberFormat


class OverlayFragment : BottomSheetDialogFragment() {
    private var addCommentCallBack: CommentsCallBack? = null
    private lateinit var binding: DialogCommentsBinding
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var viewModel: CommentsOverlayViewModel? = null
    private var commentsFragment: CommentsFragment? = null
    private fun initListeners() {
        binding!!.optionsCloseIv.setOnClickListener { view -> dismiss() }
    }

    private fun initialization() {
        initListeners()
        commentsFragment = CommentsFragment()
        commentsFragment!!.setAddCommentCallBack(addCommentCallBack)
        commentsFragment!!.arguments = arguments
        replaceFragmentWithoutAnime(binding.containerFm.id, commentsFragment!!)
        val likesHint = if(requireArguments().getInt("like_count") > 1)
                getString(R.string.likes) else getString(R.string.like_hint)

        if (requireArguments().containsKey("product_id")) viewModel!!.overlayTitle.set(requireArguments().getString("product_name")) else if (requireArguments().containsKey("offer_id")) viewModel!!.overlayTitle.set(requireArguments().getString("offer_name")) else viewModel!!.overlayTitle.set(requireArguments().getString("place_name"))
        if (requireArguments().getInt("like_count") != 0) viewModel!!.likeCount.set(NumberFormat.getInstance().format(requireArguments().getInt("like_count")) + " " + likesHint)
        if (requireArguments().getInt("share_count") != 0) viewModel!!.shareCount.set(NumberFormat.getInstance().format(requireArguments().getInt("share_count")) + " " + getString(R.string.shares))  }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(bundle) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialogInterface: DialogInterface ->
            val behavior = (dialogInterface as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { BottomSheetBehavior.from(it) }
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                        dismiss()
//                    }
                    if(newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN)
                        dismiss()

                }

                override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
            })

        }

        return bottomSheetDialog
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.dialog_comments, viewGroup, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(CommentsOverlayViewModel::class.java)
        binding.viewModel = viewModel
        initialization()
        return view
    }

    fun refreshCommentModel(commentModel: CommentModel?) {
        childFragmentManager?.popBackStack()
        for (fragment in childFragmentManager?.fragments) {
            if (fragment is CommentsFragment){
                commentModel?.let { fragment.refreshCommentModel(it) }
            }
        }
    }

    fun setAddCommentCallBack(addCommentCallBack: CommentsCallBack?) {
        this.addCommentCallBack = addCommentCallBack
    }

//    override fun setupDialog(dialog: Dialog, n: Int) {
//        super.setupDialog(dialog, n)
//        val view = View.inflate(this.context, R.layout.dialog_comments, null)
//        dialog.setContentView(view)
//        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
//        val bottomSheetBehavior = mBottomSheetBehavior
//        if (bottomSheetBehavior != null) {
////            bottomSheetBehavior.peekHeight = ScreenUtils(this.context).height
//            view.requestLayout()
//        }
//    }

}