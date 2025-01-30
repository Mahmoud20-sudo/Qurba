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
import com.qurba.android.adapters.OffersAdapter
import com.qurba.android.databinding.ActivityLikedOffersBinding
import com.qurba.android.network.models.OffersModel
import com.qurba.android.ui.place_details.view_models.PagerAgentViewModel
import com.qurba.android.ui.profile.view_models.UserOffersModel
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.Constants.PAGE_START
import com.qurba.android.utils.EndlessRecyclerViewScrollListener
import com.qurba.android.utils.QurbaApplication

class UserOffersActivity : BaseActivity() {
    private var binding: ActivityLikedOffersBinding? = null
    private var viewModel: UserOffersModel? = null
    private var recyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var offersAdpater: OffersAdapter? = null
    private var isLastPage = false
    private var isLoading = false
    private var currentPage = PAGE_START
    private var pagerAgentViewModel: PagerAgentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialization()
    }

    private fun initialization() {
        viewModel = ViewModelProvider(this).get(UserOffersModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_liked_offers)
        binding?.viewModel = viewModel
        binding?.setPagerAgentVM(ViewModelProvider(this)[PagerAgentViewModel::class.java])
        binding?.animToolbar?.getNavigationIcon()?.setTint(Color.BLACK)

        binding?.emptyStateView?.findViewById<TextView>(R.id.no_result_tv)?.visibility = View.GONE
        binding?.emptyStateView?.findViewById<TextView>(R.id.clear_filters_tv)?.visibility = View.GONE
        pagerAgentViewModel = ViewModelProvider(this).get(PagerAgentViewModel::class.java)

        initAdapters()
        initializeObservables()
        initListeners()
    }

    private fun initAdapters() {
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding!!.offersRv.layoutManager = linearLayoutManager
        offersAdpater = OffersAdapter(this, ArrayList())
        binding!!.offersRv.adapter = offersAdpater
    }

    private fun initListeners() {
        binding!!.animToolbar.setNavigationOnClickListener { onBackPressed() }

        val callbacks: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                this@UserOffersActivity.isLoading = true
                viewModel!!.getLikedOffers(currentPage)
            }

            // Indicate whether new page loading is in progress or not
            override fun isLoading(): Boolean {
                return this@UserOffersActivity.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                // Indicate whether all data (pages) are loaded or not
                return isLastPage
            }
        }
//
        Paginate.with(binding?.offersRv, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(false)
                .build()
    }

    private fun initializeObservables() {
        viewModel!!.offersObservable.observe(this, { offers: List<OffersModel> -> publishPlaces(offers) })
    }

    private fun publishPlaces(offers: List<OffersModel>) {
        currentPage++
        isLoading = false
        offersAdpater!!.addAll(offers)
        if(offers.isEmpty()){
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