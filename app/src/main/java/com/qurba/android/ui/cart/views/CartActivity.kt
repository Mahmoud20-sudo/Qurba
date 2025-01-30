package com.qurba.android.ui.cart.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.adapters.CartItemsAdapter
import com.qurba.android.databinding.ActivityCartBinding
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.CartItems
import com.qurba.android.network.models.CartModel
import com.qurba.android.ui.cart.view_models.MyCartViewModel
import com.qurba.android.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.lang.Exception
import java.util.*

class CartActivity : BaseActivity(), DeletionListener {
    private var binding: ActivityCartBinding? = null
    private var viewModel: MyCartViewModel? = null
    private val itemList: MutableList<CartItems> = ArrayList()
    private var cartAdapter: CartItemsAdapter? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var queryTextChangedJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        initialization()
        initListeners()
        initializeAdapters()
    }

    override fun onResume() {
        super.onResume()
        QurbaApplication.setCurrentActivity(this)
        binding!!.toolbar.navigationIcon!!.setTint(Color.BLACK)
        cart
    }

    private val cart: Unit
        get() {
            viewModel!!.getCart(this) { cartModel: CartModel? ->
                clearData()
                viewModel!!.setLoading(false)
                if (cartModel!!.products.isNullOrEmpty() && cartModel.offers.isNullOrEmpty()) {
                    binding!!.loadingView.progress.startProgress()
                    viewModel!!.setItemsCleared(true)
                    null
                }
                sharedPref.setCart(cartModel, true)
                addAll(cartModel.cartItems)
                viewModel!!.setCart(cartModel)
                null
            }
        }

    override fun onPause() {
        super.onPause()
    }

    fun add(response: CartItems) {
        if (!itemList.contains(response)) {
            itemList.add(response)
            cartAdapter!!.notifyItemInserted(itemList.size - 1)
        }
    }

    private fun clearData() {
        itemList.clear()
        cartAdapter?.notifyDataSetChanged()
    }

    fun addAll(postItems: List<CartItems>?) {
        if (postItems != null && postItems.isNotEmpty()) {
            for (response in postItems) {
                add(response)
            }
        }
    }

    private fun initListeners() {
        binding?.addressNoteEdt?.setOnFocusChangeListener { view, isFocused ->
            if (isFocused)
                binding?.nestedScrollView?.smoothScrollTo(
                    0,
                    view.height + 5000
                )
        }
        binding?.addressNoteEdt?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                queryTextChangedJob?.cancel()
                queryTextChangedJob = CoroutineScope(Main).launch {
                    delay(500)
                    viewModel!!.updateCartNote(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
    }

    private fun initialization() {
        viewModel = ViewModelProvider(this).get(MyCartViewModel::class.java)
        binding!!.viewModel = viewModel
        sharedPref.clearCart()
        viewModel!!.setActivity(this)
        viewModel!!.setLoading(true)
        binding!!.loadingView.progress.startProgress()
        binding!!.loadingViewFr.bringToFront()
        binding!!.loadingViewFr.elevation = 100f
        binding!!.toolbar.setNavigationOnClickListener { onBackPressed() }
        logging(
            this, Constants.USER_VIEW_CART_SUCCESS, LEVEL_INFO,
            "User viewing cart from place page menu/offer action success", ""
        )
    }

    private fun initializeAdapters() {
        val linearLayoutManager = LinearLayoutManager(this)
        cartAdapter = CartItemsAdapter(this)
        cartAdapter?.diffResult?.submitList(itemList)
        binding!!.cartItemsRv.adapter = cartAdapter
        binding!!.cartItemsRv.layoutManager = linearLayoutManager
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding!!.cartItemsRv.isNestedScrollingEnabled = false
    }

    override fun onDeleteItemListener(pos: Int) {
        try {
            val item = itemList[pos]
            itemList.remove(item)
            showHideLoading(true)
            if (itemList.isEmpty()) {
                viewModel!!.clearCart(this) { unit: Boolean? ->
                    showHideLoading(false)
                    null
                }
                viewModel!!.setItemsCleared(true)
                return
            }
            updateCart(pos)
            viewModel!!.deleteItemFromCart(item.cartItemId, item.itemType) { result: Boolean? ->
                if (!result!!) {
                    itemList[pos] = item
                    cartAdapter!!.notifyItemInserted(pos)
                }
                null
            }
        } catch (e: Exception) {
            logging(
                this,
                Constants.USER_DELETE_ITEM_FROM_CART_FAIL, LEVEL_ERROR,
                "User failed to delete his cart ", Arrays.toString(e.stackTrace)
            )
        }
    }

    private fun updateCart(pos: Int) {
        sharedPref.copyOrUpdate(itemList)
        sharedPref.checkIfFreeDeliveryCanceled()
        cartAdapter!!.notifyItemRemoved(pos)
        cartAdapter!!.notifyItemRangeChanged(pos, itemList.size)
        viewModel!!.updateCartData()
    }

    //    private void update(int pos){
    //        sharedPref.copyOrUpdate(itemList);
    //        sharedPref.checkIfFreeDeliveryCanceled();
    //        cartAdapter.notifyItemInserted(pos);
    //        cartAdapter.notifyItemRangeChanged(pos, itemList.size());
    //        viewModel.updateCartData();
    //    }
    private fun updateCartItem(pos: Int, qty: Int) {
        sharedPref.copyOrUpdate(pos, qty)
        itemList[pos].quantity = qty
        cartAdapter!!.notifyItemChanged(pos, itemList[pos])
        viewModel!!.updateCartData()
    }

    override fun onQuantityChangeListener(newQty: Int, pos: Int) {
        try {
            val item = itemList[pos]
            val oldQty = itemList[pos].quantity
            if (item.cartItemId == null) { //happens on users devices somehow
                Toast.makeText(this, getString(R.string.update_item_qty_failed), Toast.LENGTH_SHORT)
                    .show()
                return
            }
            showHideLoading(true)
            updateCartItem(pos, newQty)
            viewModel!!.updateCartItemQty(
                item.cartItemId,
                newQty,
                item.itemType
            ) { result: Boolean? ->
                showHideLoading(false)
                if (!result!!) updateCartItem(pos, oldQty)
                null
            }
        } catch (e: Exception) {
            logging(
                this,
                Constants.USER_UPDATE_CART_ITEM_QTY_FAIL, LEVEL_ERROR,
                "User failed to update cart's item qty ", Arrays.toString(e.stackTrace)
            )
        }
    }

    private fun showHideLoading(flag: Boolean) {
        if (flag) binding!!.loadingView.progress.startProgress() else binding!!.loadingView.progress.stopProgress()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51120 && resultCode == -1) finish()
    }
}
