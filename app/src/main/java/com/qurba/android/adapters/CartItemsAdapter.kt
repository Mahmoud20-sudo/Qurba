package com.qurba.android.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.qurba.android.R
import com.qurba.android.databinding.ItemCartBinding
import com.qurba.android.network.models.CartItems
import com.qurba.android.ui.cart.view_models.CartItemViewModel
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.DeletionListener

class CartItemsAdapter(
    private val deletionListener: DeletionListener
) :
    RecyclerView.Adapter<CartItemsAdapter.CartViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<CartItems>() {
        override fun areItemsTheSame(oldItem: CartItems, newItem: CartItems): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartItems, newItem: CartItems): Boolean {
            return oldItem._id == newItem._id
        }
    }

    val diffResult = AsyncListDiffer(this,diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = diffResult.currentList[position]
        val cartItemViewModel =
            CartItemViewModel(holder.itemView.context as BaseActivity?, item, position)
        holder.itemBinding?.cartItemVM = cartItemViewModel
        holder.itemBinding?.cartImg?.let {
            Glide.with(holder.itemView.context).load(item.imageUrl)
                .placeholder(R.mipmap.offer_details_place_holder)
                .into(it)
        }
        holder.itemBinding?.valueSelector?.setValue(item.quantity)
//        cartItemViewModel.setEditable(isEditable)
//        cartViewHolder.itemBinding.deleteIv.setOnClickListener(view -> deletionListener.onDeleteItemListener(position));
        holder.itemBinding?.valueSelector?.setItemPrice(item.price)
        holder.itemBinding?.valueSelector?.setDeletionListener(deletionListener)
        holder.itemBinding?.valueSelector?.tag = position
        holder.itemBinding?.view?.visibility = if (position == diffResult.currentList.size - 1) View.GONE else View.VISIBLE
        val ingredients = cartItemViewModel.setDatangredient()
        if (ingredients.isNotEmpty()) {
            holder.itemBinding?.descriptionTv?.setText(
                if (ingredients[ingredients.length - 1] != '-') ingredients else ingredients.substring(
                    0,
                    ingredients.length - 1
                )
            )
            holder.itemBinding?.descriptionTv?.setVisibility(View.VISIBLE)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return diffResult.currentList.size
    }

    inner class CartViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        var itemBinding: ItemCartBinding? = DataBindingUtil.bind(itemView!!)

    }
}