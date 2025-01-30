package com.qurba.android.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.qurba.android.R
import com.qurba.android.databinding.ItemCommentBinding
import com.qurba.android.databinding.ItemOfferBinding
import com.qurba.android.databinding.ItemOrderNowOrdersBinding
import com.qurba.android.databinding.ItemProductBinding
import com.qurba.android.network.models.CommentModel
import com.qurba.android.network.models.OffersModel
import com.qurba.android.network.models.OrdersModel
import com.qurba.android.network.models.ProductData
import com.qurba.android.ui.comments.view_models.CommentItemViewModel
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment.CommentActionEvents
import com.qurba.android.ui.my_orders.view_models.MyOrderItemViewModel
import com.qurba.android.ui.offers.view_models.OfferItemViewModel
import com.qurba.android.ui.order_now.view_models.OrderNowViewModel
import com.qurba.android.ui.order_status.views.OrderStatusActivity
import com.qurba.android.ui.products.view_models.ProductItemViewModel
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showKeyboard
import com.vanniktech.emoji.EmojiPopup
import java.lang.Exception
import java.util.ArrayList

class OrderNowAdapter(
    private val activity: BaseActivity,
    private val orderNowEntityModels: MutableList<JsonElement>,
    private val orderNowViewModel: OrderNowViewModel?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val orders = ArrayList<OrdersModel?>()
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ORDER -> {
                val view1 = inflater.inflate(R.layout.item_order_now_orders, parent, false)
                OrderViewHolder(view1)
            }
            TYPE_PRODUCT -> {
                val view2 = inflater.inflate(R.layout.item_product, parent, false)
                ProductsViewHolder(view2)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_offer, parent, false)
                OfferViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_PRODUCT -> {
                val product =
                    Gson().fromJson(orderNowEntityModels[position], ProductData::class.java)
                val productViewHolder = holder as ProductsViewHolder
                val productItemViewModel = ProductItemViewModel(activity, product, position)
                productViewHolder.itemBinding!!.viewModel = productItemViewModel
                Glide.with(activity).load(product.imageURL)
                    .placeholder(R.mipmap.offer_details_place_holder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(productViewHolder.itemBinding!!.itemProductAvatarIv)
                Glide.with(activity).load(product.placeInfo.placeProfilePictureUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(productViewHolder.itemBinding!!.placeImageIv)
                if (sharedPreferencesManager.user != null) Glide.with(activity)
                    .load(sharedPreferencesManager?.user?.profilePictureUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(productViewHolder.itemBinding!!.profileAvatarIv)
                productViewHolder.itemBinding!!.position = position
                productItemViewModel.setOrderNowViewModel(orderNowViewModel)
                this.setCommentsUserData(
                    productViewHolder.itemView.context,
                    productViewHolder,
                    product
                )
                productItemViewModel.setAddCommentCallBack(productViewHolder)
                productViewHolder.itemBinding!!.productOldPriceTv.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG
                productViewHolder.itemView.tag = position
                setIcon(product.isOrderable, holder)
            }
            TYPE_OFFER -> {
                val offersModel =
                    Gson().fromJson(orderNowEntityModels[position], OffersModel::class.java)
                val offerViewHolder = holder as OfferViewHolder
                offerViewHolder.itemView.tag = position
                val offerItemViewModel = OfferItemViewModel(activity, offersModel, true)
                Glide.with(activity).load(offersModel.pictureUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.offer_details_place_holder)
                    .into(offerViewHolder.itemBinding!!.offerImageIv)
                Glide.with(activity).load(offersModel?.placeId?.placeProfilePictureUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(offerViewHolder.itemBinding!!.placeImageIv)
                if (sharedPreferencesManager.user != null) Glide.with(activity)
                    .load(sharedPreferencesManager?.user?.profilePictureUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(offerViewHolder.itemBinding!!.profileAvatarIv)
                offerItemViewModel.setAddCommentCallBack(offerViewHolder)
                offerViewHolder.itemBinding!!.viewModel = offerItemViewModel
                offerItemViewModel.setOrderNowViewModel(orderNowViewModel)
                offerViewHolder.itemBinding!!.offerOldPriceTv.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG
                offerViewHolder.itemBinding!!.position = position
                DateUtils.getShortDateFromTimeStamp(offersModel.endDate)
                this.setCommentsUserData(
                    offerViewHolder.itemView.context,
                    offerViewHolder,
                    offersModel
                )
                setIcon(offersModel.isOrderable, holder)
            }
            TYPE_ORDER -> {
                val ordersModel = orders[position]
                val viewHolder = holder as OrderViewHolder
                val myOrderItemViewModel = MyOrderItemViewModel(activity, ordersModel, true)
                viewHolder.itemBinding!!.viewModel = myOrderItemViewModel
                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(activity, OrderStatusActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra(Constants.ORDER_ID, ordersModel?._id)
                    activity.startActivity(intent)
                }
            }
        }
    }

    private fun setIcon(orderable: Boolean, offerViewHolder: OfferViewHolder) {
        val imageRes = if (orderable) AppCompatResources.getDrawable(
            offerViewHolder.itemView.context,
            R.drawable.ic_grey_clock
        ) else AppCompatResources.getDrawable(
            offerViewHolder.itemView.context,
            R.drawable.ic_closed
        )
        offerViewHolder.itemBinding!!.availabilityTv.setCompoundDrawablesWithIntrinsicBounds(
            if (sharedPreferencesManager.language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) null else imageRes,
            null,
            if (sharedPreferencesManager.language.equals(
                    "en",
                    ignoreCase = true
                )
            ) null else imageRes,
            null
        )
    }

    private fun setIcon(orderable: Boolean, productsViewHolder: ProductsViewHolder) {
        val imageRes =
            if (orderable) AppCompatResources.getDrawable(
                productsViewHolder.itemView.context,
                R.drawable.ic_grey_clock
            ) else AppCompatResources.getDrawable(
                productsViewHolder.itemView.context,
                R.drawable.ic_closed
            )
        productsViewHolder.itemBinding!!.availabilityTv.setCompoundDrawablesWithIntrinsicBounds(
            if (sharedPreferencesManager.language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) null else imageRes,
            null,
            if (sharedPreferencesManager.language.equals(
                    "en",
                    ignoreCase = true
                )
            ) null else imageRes,
            null
        )
    }

    private fun setCommentsUserData(
        context: Context,
        productsViewHolder: ProductsViewHolder,
        productData: ProductData
    ) {
        productsViewHolder.itemBinding!!.commentsContainerFm.removeAllViews()
        if (productData.recentComment == null || productData.recentComment._id == null || productData.recentComment.user == null) {
            productsViewHolder.itemBinding!!.commentsParentLl.visibility = View.GONE
            return
        }
        productsViewHolder.itemBinding!!.commentsParentLl.visibility = View.VISIBLE
        val binding: ItemCommentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_comment,
            productsViewHolder.itemBinding!!.commentsContainerFm,
            false
        )
        val commentItemViewModel = CommentItemViewModel(activity, productData.recentComment)
        binding.viewModel = commentItemViewModel
        productData.recentComment.parentId = productData._id
        productData.recentComment.type = productData.module
        Glide.with(context).load(productData?.recentComment?.user?.profilePictureUrl)
            .placeholder(R.drawable.ic_profile_placeholder).into(binding.userImageIv)
        productsViewHolder.itemBinding!!.commentsContainerFm.addView(binding.root)
        val emojiPopup = EmojiPopup.Builder.fromRootView(productsViewHolder.itemView)
            .build(productsViewHolder.itemBinding!!.addCommentEt)
        productsViewHolder.itemBinding!!.emjoisIv.setOnClickListener { v -> emojiPopup.toggle() }
        productsViewHolder.itemBinding!!.addCommentEt.setOnFocusChangeListener { v, hasFocus ->
            productsViewHolder.itemBinding!!.actionsFl.visibility =
                if (hasFocus) View.VISIBLE else View.GONE
        }
        binding.parentRv.setOnLongClickListener(productsViewHolder.itemBinding!!.viewModel?.commentLongPress())
        binding.viewMoreTv.setOnTouchListener(productsViewHolder.itemBinding!!.viewModel?.replyClickListener())
        binding.replyLl.setOnTouchListener(productsViewHolder.itemBinding!!.viewModel?.replyClickListener())
        binding.commentsReplyTv.setOnTouchListener(productsViewHolder.itemBinding!!.viewModel?.replyClickListener())
        productsViewHolder.itemBinding!!.viewModel?.setCommentActionEvents(productsViewHolder)
        if (productData.recentComment.recentReply != null) {
            binding.replyLl.visibility = View.VISIBLE
            Glide.with(context)
                .load(productData.recentComment?.recentReply?.user?.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder).into(binding.replyUserImageIv)
        }
    }

    private fun setCommentsUserData(
        context: Context,
        offerViewHolder: OfferViewHolder,
        offersModel: OffersModel
    ) {
        offerViewHolder.itemBinding!!.commentsContainerFm.removeAllViews()
        if (offersModel.recentComment == null || (offersModel.recentComment._id
                    == null) || offersModel.recentComment.user == null
        ) {
            offerViewHolder.itemBinding!!.commentsParentLl.visibility = View.GONE
            return
        }
        offerViewHolder.itemBinding!!.commentsParentLl.visibility = View.VISIBLE
        val binding: ItemCommentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_comment,
            offerViewHolder.itemBinding!!.commentsContainerFm,
            false
        )
        val commentItemViewModel = CommentItemViewModel(activity, offersModel.recentComment)
        binding.viewModel = commentItemViewModel
        offersModel.recentComment.parentId = offersModel._id
        offersModel.recentComment.type = offersModel.module
        offerViewHolder.itemBinding!!.commentsContainerFm.addView(binding.root)
        val emojiPopup = EmojiPopup.Builder.fromRootView(offerViewHolder.itemView)
            .build(offerViewHolder.itemBinding!!.addCommentEt)
        offerViewHolder.itemBinding!!.emjoisIv.setOnClickListener { v -> emojiPopup.toggle() }
        offerViewHolder.itemBinding!!.addCommentEt.setOnFocusChangeListener { v, hasFocus ->
            offerViewHolder.itemBinding!!.actionsFl.visibility =
                if (hasFocus) View.VISIBLE else View.GONE
        }
        binding.parentRv.setOnLongClickListener(offerViewHolder.itemBinding!!.viewModel?.commentLongPress())
        binding.viewMoreTv.setOnTouchListener(offerViewHolder.itemBinding!!.viewModel?.replyClickListener())
        binding.replyLl.setOnTouchListener(offerViewHolder.itemBinding!!.viewModel?.replyClickListener())
        binding.commentsReplyTv.setOnTouchListener(offerViewHolder.itemBinding!!.viewModel?.replyClickListener())
        offerViewHolder.itemBinding!!.viewModel?.setCommentActionEvents(offerViewHolder)
        if (offersModel.recentComment.recentReply != null) {
            binding.replyLl.visibility = View.VISIBLE
            Glide.with(context)
                .load(offersModel.recentComment?.recentReply?.user?.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder).into(binding.replyUserImageIv)
        }
        Glide.with(context).load(offersModel.recentComment?.user?.profilePictureUrl)
            .placeholder(R.drawable.ic_profile_placeholder).into(binding.userImageIv)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return orderNowEntityModels.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position < ORDER_DISPLAY_FREQUENCY && position < orders.size) {
            return TYPE_ORDER
        } else if ((orderNowEntityModels[position] as JsonObject)["module"].asString.equals(
                "offer",
                ignoreCase = true
            )
        ) {
            return TYPE_OFFER
        }
        return TYPE_PRODUCT
    }

    internal inner class OfferViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!), CommentsCallBack, CommentActionEvents {
        var itemBinding: ItemOfferBinding? = DataBindingUtil.bind(itemView!!)
        override fun onCommentAdded(var1: CommentModel, isReply: Boolean) {
            val offersModel = Gson().fromJson(
                orderNowEntityModels[(itemView.tag as Int)],
                OffersModel::class.java
            )
            offersModel.commentsCount = offersModel.commentsCount + 1

            //in case
            if (offersModel.recentComment != null) {
                if (isReply && !offersModel.recentComment._id.equals(
                        var1.parentId,
                        ignoreCase = true
                    )
                ) return
            }
            itemBinding!!.viewModel?.commentsCount?.set(
                offersModel.commentsCount.toString() + " " + activity.getString(
                    R.string.comments
                )
            )
            itemBinding!!.viewModel?.commentsCount?.set("")
            itemBinding!!.actionsFl.visibility = View.GONE
            itemBinding!!.addCommentEt.clearFocus()
            if (isReply) offersModel.recentComment.recentReply =
                var1 else offersModel.recentComment = var1
            orderNowEntityModels[(itemView.tag as Int)] = Gson().toJsonTree(offersModel)
            orderNowEntityModels[(itemView.tag as Int)].asJsonObject.addProperty("module", "offer")
            notifyItemChanged((itemView.tag as Int))
        }

        override fun onCommentUpdated(var1: CommentModel, isReply: Boolean) {
            val offersModel = Gson().fromJson(
                orderNowEntityModels[(itemView.tag as Int)],
                OffersModel::class.java
            )
            if (!var1._id.equals(offersModel.recentComment._id, ignoreCase = true)) return
            if (isReply) offersModel.recentComment.recentReply =
                var1 else offersModel.recentComment = var1
            orderNowEntityModels[(itemView.tag as Int)] = Gson().toJsonTree(offersModel)
            orderNowEntityModels[(itemView.tag as Int)].asJsonObject.addProperty("module", "offer")
            notifyItemChanged((itemView.tag as Int))
        }

        override fun onCommentDeleted(isReply: Boolean, deletedCommentsount: Int) {
            val offersModel = Gson().fromJson(
                orderNowEntityModels[(itemView.tag as Int)],
                OffersModel::class.java
            )
            offersModel.commentsCount =
                offersModel.commentsCount - if (isReply) 1 else deletedCommentsount
            itemBinding!!.viewModel?.commentsCount?.set(
                offersModel.commentsCount.toString() + " " + activity.getString(
                    R.string.comments
                )
            )
            if (!isReply) offersModel.recentComment =
                null else if (isReply && offersModel.recentComment.repliesCount == deletedCommentsount) offersModel.recentComment.recentReply =
                null
            orderNowEntityModels[(itemView.tag as Int)] = Gson().toJsonTree(offersModel)
            orderNowEntityModels[(itemView.tag as Int)].asJsonObject.addProperty("module", "offer")
            notifyItemChanged((itemView.tag as Int))
        }

        override fun onCommentClick(
            commentModel: CommentModel,
            isEdit: Boolean,
            isReply: Boolean,
            commentPosition: Int
        ) {
            itemBinding!!.viewModel?.setCommentId(commentModel._id)
            if (isEdit) {
                itemBinding!!.viewModel?.setEdit(true)
                itemBinding!!.addCommentEt.setText(commentModel.comment)
                itemBinding!!.addCommentEt.text?.length?.let {
                    itemBinding!!.addCommentEt.setSelection(
                        it
                    )
                }
                itemBinding!!.addCommentEt.isFocusableInTouchMode = true
                itemBinding!!.addCommentEt.requestFocus()
                activity.showKeyboard()
            } else {
                activity.showConfirmDialog(
                    commentModel.repliesCount > 0,
                    true
                ) { itemBinding!!.viewModel?.deleteOfferComment() }
            }
        }

    }

    internal inner class OrderViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        var itemBinding: ItemOrderNowOrdersBinding? = DataBindingUtil.bind(itemView!!)
    }

    internal inner class ProductsViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!), CommentsCallBack, CommentActionEvents {
        var itemBinding: ItemProductBinding? = DataBindingUtil.bind(itemView!!)
        override fun onCommentAdded(var1: CommentModel, isReply: Boolean) {
            val productData = Gson().fromJson(
                orderNowEntityModels[(itemView.tag as Int)],
                ProductData::class.java
            )
            productData.commentsCount = productData.commentsCount + 1
            if (productData.recentComment != null) {
                if (isReply && !productData.recentComment._id.equals(
                        var1.parentId,
                        ignoreCase = true
                    )
                )
                    return
            }
            itemBinding!!.viewModel?.commentsCount?.set(
                productData.commentsCount.toString() + " " + activity.getString(
                    R.string.comments
                )
            )
            itemBinding!!.viewModel?.commentsCount?.set("")
            itemBinding!!.actionsFl.visibility = View.GONE
            itemBinding!!.addCommentEt.clearFocus()
            if (isReply) productData.recentComment.recentReply =
                var1 else productData.recentComment = var1
            orderNowEntityModels[(itemView.tag as Int)] = Gson().toJsonTree(productData)
            orderNowEntityModels[(itemView.tag as Int)].asJsonObject.addProperty(
                "module",
                "product"
            )
            notifyItemChanged((itemView.tag as Int))
        }

        override fun onCommentUpdated(var1: CommentModel, isReply: Boolean) {
            val productData = Gson().fromJson(
                orderNowEntityModels[(itemView.tag as Int)],
                ProductData::class.java
            )
            if (!var1._id.equals(productData.recentComment._id, ignoreCase = true)) return
            if (isReply) productData.recentComment.recentReply =
                var1 else productData.recentComment = var1
            orderNowEntityModels[(itemView.tag as Int)] = Gson().toJsonTree(productData)
            orderNowEntityModels[(itemView.tag as Int)].asJsonObject.addProperty(
                "module",
                "product"
            )
            notifyItemChanged((itemView.tag as Int))
        }

        override fun onCommentDeleted(isReply: Boolean, deletedCommentsount: Int) {
            val productData = Gson().fromJson(
                orderNowEntityModels[(itemView.tag as Int)],
                ProductData::class.java
            )
            productData.commentsCount =
                productData.commentsCount - if (isReply) 1 else deletedCommentsount
            itemBinding!!.viewModel?.commentsCount?.set(
                productData.commentsCount.toString() + " " + activity.getString(
                    R.string.comments
                )
            )
            if (!isReply) productData.recentComment =
                null else if (isReply && productData.recentComment.repliesCount == deletedCommentsount) productData.recentComment.recentReply =
                null

            orderNowEntityModels[(itemView.tag as Int)] = Gson().toJsonTree(productData)
            orderNowEntityModels[(itemView.tag as Int)].asJsonObject.addProperty(
                "module",
                "product"
            )
            notifyItemChanged((itemView.tag as Int))
        }

        override fun onCommentClick(
            commentModel: CommentModel,
            isEdit: Boolean,
            isReply: Boolean,
            commentPosition: Int
        ) {
            itemBinding!!.viewModel?.setCommentId(commentModel._id)
            if (isEdit) {
                itemBinding!!.viewModel?.setEdit(true)
                itemBinding!!.addCommentEt.setText(commentModel.comment)
                itemBinding!!.addCommentEt?.text?.length?.let {
                    itemBinding!!.addCommentEt.setSelection(
                        it
                    )
                }
                itemBinding!!.addCommentEt.isFocusableInTouchMode = true
                itemBinding!!.addCommentEt.requestFocus()
                activity.showKeyboard()
            } else {
                activity.showConfirmDialog(
                    commentModel.repliesCount > 0, true
                ) { itemBinding!!.viewModel?.deleteProductComment() }
            }
        }
    }

    fun add(response: JsonElement) {
        if (!orderNowEntityModels.contains(response)) {
            orderNowEntityModels.add(response)
            notifyItemInserted(orderNowEntityModels.size - 1)
        }
    }

    fun addAll(postItems: List<JsonElement>?) {
        if (!postItems.isNullOrEmpty()) {
            for (response in postItems) {
                add(response)
            }
        }
    }

    fun addOrders(postItems: List<OrdersModel?>) {
        if (postItems?.isNotEmpty()) {
            orders.clear()
            orders.addAll(postItems)
            notifyDataSetChanged()
        }
    }

    fun clearOrders() {
        orders.clear()
        notifyDataSetChanged()
    }

    fun updateOffer(position: Int, offersModel: OffersModel) {
        try {
            val posOffer = Gson().fromJson(orderNowEntityModels[position], OffersModel::class.java)
            if (posOffer._id != offersModel._id) return
            offersModel.recentComment = posOffer.recentComment
            orderNowEntityModels[position] = Gson().toJsonTree(offersModel)
            orderNowEntityModels[position].asJsonObject.addProperty("module", "offer")
            this.notifyItemChanged(position)
        } catch (e: Exception) {
            Log.e("exception", e.localizedMessage)
        }
    }

    fun clearAll() {
        orderNowEntityModels.clear()
        notifyDataSetChanged()
    }

    fun updateProduct(position: Int, productData: ProductData) {
        try {
            val posProduct =
                Gson().fromJson(orderNowEntityModels[position], ProductData::class.java)
            if (posProduct._id != productData._id) return
            productData.recentComment = posProduct.recentComment
            orderNowEntityModels[position] = Gson().toJsonTree(productData)
            orderNowEntityModels[position].asJsonObject.addProperty("module", "product")
            this.notifyItemChanged(position)
        } catch (e: Exception) {
            Log.e("exception", e.localizedMessage)
        }
    }

    companion object {
        private const val TYPE_OFFER = 1
        private const val TYPE_PRODUCT = 3
        private const val TYPE_ORDER = 2
        const val ORDER_DISPLAY_FREQUENCY = 1
    }

}