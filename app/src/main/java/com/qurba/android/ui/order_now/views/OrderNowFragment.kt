package com.qurba.android.ui.order_now.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.paginate.Paginate
import com.qurba.android.R
import com.qurba.android.adapters.OfferPricesAdapter
import com.qurba.android.adapters.OfferPricesAdapter.FilteringListeners
import com.qurba.android.adapters.OrderNowAdapter
import com.qurba.android.databinding.FragmentOrderNowBinding
import com.qurba.android.network.models.OffersModel
import com.qurba.android.network.models.OrdersModel
import com.qurba.android.network.models.ProductData
import com.qurba.android.ui.home.views.HomeActivityKotlin
import com.qurba.android.ui.order_now.view_models.OrderNowViewModel
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.Constants
import com.qurba.android.utils.SharedPreferencesManager
import com.qurba.android.utils.extenstions.addRxTextWatcher
import com.robinhood.ticker.TickerUtils
import java.util.concurrent.TimeUnit

class OrderNowFragment : Fragment(), FilteringListeners, OnRefreshListener {
    private var viewModel: OrderNowViewModel? = null
    private var binding: FragmentOrderNowBinding? = null
    private var orderNowAdapter: OrderNowAdapter? = null
    private var offerPricesAdapter: OfferPricesAdapter? = null
    private var currentPrice = 0
    private var currentPage = Constants.PAGE_START
    private val orderNowEntityModels = ArrayList<JsonElement>()
    private var isLoading = false
    private var isLastPage = false
    private var querySearch = ""
    private var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_now, container, false)
        initialization()
        return binding?.root
    }

    private fun initialization() {
        viewModel = ViewModelProvider(this).get(OrderNowViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.cartNumberTv.setCharacterLists(TickerUtils.provideNumberList())
        binding!!.refreshLayout.setOnRefreshListener(this)
        initAdapter()
        initializeObservables()
        initListeners()
        onRefresh()
    }

    fun filterOrders() {
        currentPage = Constants.PAGE_START
        orderNowEntityModels.clear()
        orderNowAdapter!!.notifyDataSetChanged()
        binding!!.shimmerLayout.visibility = View.VISIBLE
        binding!!.shimmerLayout.startShimmer()
        if (!isLoading) viewModel!!.getOrderNowData(currentPage, currentPrice, querySearch)
        (activity as HomeActivityKotlin?)!!.getActiveOrders()
    }

    fun updateQuantity() {
        viewModel!!.updateQuantity()
    }

    private fun initListeners() {
        val callbacks: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                this@OrderNowFragment.isLoading = true
                if (orderNowAdapter?.itemCount == 0) currentPage = 1
                viewModel!!.getOrderNowData(currentPage, currentPrice, querySearch)
            }

            override fun isLoading(): Boolean {
                // Indicate whether new page loading is in progress or not
                return this@OrderNowFragment.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                // Indicate whether all data (pages) are loaded or not
                return isLastPage
            }
        }
        Paginate.with(binding!!.orderNowRv, callbacks)
            .setLoadingTriggerThreshold(0)
            .addLoadingListItem(true)
            .build()

        binding!!.orderNowSearchView.findViewById<SearchView.SearchAutoComplete
                >(androidx.appcompat.R.id.search_src_text)?.addRxTextWatcher()
            ?.debounce(500, TimeUnit.MILLISECONDS)
            ?.observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            ?.subscribeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            ?.subscribe {
                //DO api request
                currentPage = 1
                querySearch = it.toString()
                orderNowAdapter?.clearAll()
                viewModel?.getOrderNowData(currentPage, currentPrice, it)
            }

        binding!!.emptyStateView.findViewById<TextView>(R.id.clear_filters_tv).setOnClickListener {
            isLoading = false
            binding?.orderNowSearchView?.setQuery("", false)
            binding?.orderNowSearchView?.clearFocus()
            offerPricesAdapter?.setSelction(-1)
            currentPrice = 0
            querySearch = ""
            filterOrders()
        }
    }

    private fun initAdapter() {
        orderNowAdapter =
            OrderNowAdapter(activity as BaseActivity, orderNowEntityModels, viewModel)
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager!!.orientation = RecyclerView.VERTICAL
        binding!!.orderNowRv.layoutManager = linearLayoutManager
        binding!!.orderNowRv.isNestedScrollingEnabled = true
        binding!!.orderNowRv.adapter = orderNowAdapter
        offerPricesAdapter = OfferPricesAdapter(
            activity as BaseActivity?,
            resources.getStringArray(R.array.offer_price_options), this
        )
        val horizontalLayoutManager = LinearLayoutManager(context)
        horizontalLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding!!.priceFiltersRv.isNestedScrollingEnabled = true
        binding!!.priceFiltersRv.layoutManager = horizontalLayoutManager
        binding!!.priceFiltersRv.adapter = offerPricesAdapter
    }

    private fun initializeObservables() {
        viewModel!!.offersObservable.observe(
            viewLifecycleOwner,
            { orderNowEntityModels: List<JsonElement> -> publishOffers(orderNowEntityModels) })
    }

    fun publishOrders(ordersModels: List<OrdersModel?>?) {
        if (ordersModels.isNullOrEmpty()) {
            orderNowAdapter?.clearOrders()
            return
        }
        if (orderNowEntityModels.isNotEmpty() && orderNowEntityModels[0].toString() == "{}") orderNowEntityModels.removeAt(
            0
        )
        orderNowAdapter!!.addOrders(ordersModels?.subList(0, 1))
    }

    private fun publishOffers(orderNowEntityModels: List<JsonElement>) {
        binding!!.shimmerLayout.visibility = View.GONE
        if (!orderNowEntityModels.isNullOrEmpty()) {
            if (currentPage == 1) {
                binding?.shimmerLayout?.stopShimmer()
                binding?.refreshLayout?.isRefreshing = false
                binding?.orderNowRv?.scrollToPosition(0)
            }
            isLoading = false
            orderNowAdapter!!.addAll(orderNowEntityModels)
            currentPage++
            return
        }
        if (orderNowAdapter!!.itemCount == 0) {
            viewModel?.isDataFound = false
            return
        }
        isLastPage = true
        orderNowAdapter?.notifyDataSetChanged()
    }

    override fun onFilterClicked(price: Int) {
        isLoading = false
        currentPrice = price
        filterOrders()
    }

    override fun onActivityResult(n: Int, n2: Int, intent: Intent?) {
        super.onActivityResult(n, n2, intent)
        if (n == 1000 && n2 == -1) {
            val offersModel = Gson().fromJson(
                intent!!.extras!!.getString(Constants.OFFERS),
                OffersModel::class.java
            )
            if (SharedPreferencesManager.getInstance().offerPosition != -1 && offersModel != null) {
                orderNowAdapter!!.updateOffer(
                    SharedPreferencesManager.getInstance().offerPosition,
                    offersModel
                )
            }
            val productData = Gson().fromJson(
                intent.extras!!.getString(Constants.PRODUCTS),
                ProductData::class.java
            )
            if (SharedPreferencesManager.getInstance().productPosition != -1 && productData != null) {
                orderNowAdapter!!.updateProduct(
                    SharedPreferencesManager.getInstance().productPosition,
                    productData
                )
                SharedPreferencesManager.getInstance().productPosition = -1
            }
        }
    }

    override fun onRefresh() {
        filterOrders()
    }

    companion object {
        fun newInstance(): OrderNowFragment {
            return OrderNowFragment()
        }
    }
}