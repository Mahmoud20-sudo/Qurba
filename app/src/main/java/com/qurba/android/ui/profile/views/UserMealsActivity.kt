package com.qurba.android.ui.profile.views

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paginate.Paginate
import com.qurba.android.R
import com.qurba.android.adapters.LikedMealsAdapter
import com.qurba.android.databinding.ActivityLikedMealsBinding
import com.qurba.android.network.models.ProductData
import com.qurba.android.ui.place_details.view_models.PagerAgentViewModel
import com.qurba.android.ui.profile.view_models.UserMealsModel
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.Constants.PAGE_START
import com.qurba.android.utils.EndlessRecyclerViewScrollListener
import com.qurba.android.utils.QurbaApplication

class UserMealsActivity : BaseActivity() {
    private var binding: ActivityLikedMealsBinding? = null
    private var viewModel: UserMealsModel? = null
    private var recyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var mealsAdapter: LikedMealsAdapter? = null
    private var isLastPage = false
    private var isLoading = false
    private var currentPage = PAGE_START
    private var pagerAgentViewModel: PagerAgentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialization()
    }

    private fun initialization() {
        viewModel = ViewModelProvider(this).get(UserMealsModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_liked_meals)
        binding?.viewModel = viewModel
        binding?.emptyStateView?.findViewById<TextView>(R.id.no_result_tv)?.visibility = View.GONE
        binding?.emptyStateView?.findViewById<TextView>(R.id.clear_filters_tv)?.visibility = View.GONE
        binding?.setPagerAgentVM(ViewModelProvider(this)[PagerAgentViewModel::class.java])
        binding?.animToolbar?.getNavigationIcon()?.setTint(Color.BLACK)

        initAdapters()
        initializeObservables()
        initListeners()

        pagerAgentViewModel = ViewModelProvider(this).get(PagerAgentViewModel::class.java)
        pagerAgentViewModel?.init(this)
    }

    private fun initAdapters() {
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding!!.mealsRv.layoutManager = linearLayoutManager
        mealsAdapter = LikedMealsAdapter(this, ArrayList())
        binding!!.mealsRv.adapter = mealsAdapter
    }

    private fun initListeners() {
        binding!!.animToolbar.setNavigationOnClickListener { onBackPressed() }

        val callbacks: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                this@UserMealsActivity.isLoading = true
                viewModel!!.getLikedProducts(currentPage)
            }

            // Indicate whether new page loading is in progress or not
            override fun isLoading(): Boolean {
                return this@UserMealsActivity.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                // Indicate whether all data (pages) are loaded or not
                return isLastPage
            }
        }
//
        Paginate.with(binding?.mealsRv, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(false)
                .build()
    }

    private fun initializeObservables() {
        viewModel!!.productsObservable.observe(this, { productData: List<ProductData> -> publishPlaces(productData) })
    }

    private fun publishPlaces(productData: List<ProductData>) {
        currentPage++
        isLoading = false
        mealsAdapter!!.addAll(productData)
        if(productData.isEmpty()){
            isLastPage = true
//            mealsAdapter?.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        QurbaApplication.setCurrentActivity(null)
    }

    override fun onResume() {
        super.onResume()
        pagerAgentViewModel?.init(this)
        QurbaApplication.setCurrentActivity(this)
    }
}