package com.qurba.android.ui.comments.views

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qurba.android.R
import com.qurba.android.databinding.DialogEditDeleteBinding
import com.qurba.android.network.models.CommentModel
import com.qurba.android.utils.Constants
import com.qurba.android.utils.ScreenUtils

class EditDeleteOverlayFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogEditDeleteBinding
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var commentActionEvents: CommentActionEvents? = null

    interface CommentActionEvents {
        fun onCommentClick(commentModel: CommentModel, isEdit: Boolean, isReply: Boolean, commentPosition: Int)
    }

    fun setCommentActionEvents(commentActionEvents: CommentActionEvents) {
        this.commentActionEvents = commentActionEvents
    }

    private fun initListeners() {
        binding.editLl.setOnClickListener {
            commentActionEvents?.onCommentClick(arguments?.get(Constants.COMMENTS) as CommentModel, true, requireArguments().getBoolean(Constants.IS_REPLY), requireArguments().getInt(Constants.COMMENT_POSITION))
            dismiss()
        }
        binding.deleteLl.setOnClickListener {
            commentActionEvents?.onCommentClick(arguments?.get(Constants.COMMENTS) as CommentModel, false, requireArguments().getBoolean(Constants.IS_REPLY), requireArguments().getInt(Constants.COMMENT_POSITION))
            dismiss()
        }
    }

    private fun initialization() {
        initListeners()
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(bundle) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialogInterface: DialogInterface -> BottomSheetBehavior.from((dialogInterface as BottomSheetDialog).findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet) as View).setState(BottomSheetBehavior.STATE_EXPANDED) }
        return bottomSheetDialog
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.dialog_edit_delete, viewGroup, false)
        val view = binding.root
        initialization()
        return view
    }

    override fun onResume() {
        super.onResume()
        for (fragment in childFragmentManager?.fragments) {
            fragment.onResume()
        }
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
    }

    override fun setupDialog(dialog: Dialog, n: Int) {
        super.setupDialog(dialog, n)
        val view = View.inflate(this.context, R.layout.dialog_edit_delete, null)
        dialog.setContentView(view)
        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        val bottomSheetBehavior = mBottomSheetBehavior
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.peekHeight = ScreenUtils(this.context).height
            view.requestLayout()
        }
    }

}