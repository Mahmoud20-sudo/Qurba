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
import com.qurba.android.adapters.PlacesAdapter
import com.qurba.android.databinding.ActivityLikedPlacesBinding
import com.qurba.android.network.models.PlaceModel
import com.qurba.android.ui.profile.view_models.UserPlacesModel
import com.qurba.android.utils.BaseActivity
import com.qurba.android.utils.Constants.PAGE_START
import com.qurba.android.utils.QurbaApplication

class UserPlacesActivity : BaseActivity() {
    private var binding: ActivityLikedPlacesBinding? = null
    private var viewModel: UserPlacesModel? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var placesAdapter: PlacesAdapter? = null
    private var isLastPage = false
    private var isLoading = false
    private var currentPage = PAGE_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialization()
    }

    private fun initialization() {
        viewModel = ViewModelProvider(this).get(UserPlacesModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_liked_places)
        binding?.viewModel = viewModel
        binding?.emptyStateView?.findViewById<TextView>(R.id.no_result_tv)?.visibility = View.GONE
        binding?.emptyStateView?.findViewById<TextView>(R.id.clear_filters_tv)?.visibility = View.GONE
        binding?.animToolbar?.getNavigationIcon()?.setTint(Color.BLACK)

        initAdapters()
        initializeObservables()
        initListeners()
    }

    private fun initAdapters() {
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding!!.placesRv.layoutManager = linearLayoutManager
        placesAdapter = PlacesAdapter(this, ArrayList())
        binding!!.placesRv.adapter = placesAdapter
    }

    private fun initListeners() {
        binding!!.animToolbar.setNavigationOnClickListener { onBackPressed() }

        val callbacks: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                this@UserPlacesActivity.isLoading = true
                viewModel!!.getLikedPlaces(currentPage)
            }

            // Indicate whether new page loading is in progress or not
            override fun isLoading(): Boolean {
                return this@UserPlacesActivity.isLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                // Indicate whether all data (pages) are loaded or not
                return isLastPage
            }
        }
//
        Paginate.with(binding?.placesRv, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(false)
                .build()
    }

    private fun initializeObservables() {
        viewModel!!.placesObservable.observe(this, { places: List<PlaceModel> -> publishPlaces(places) })
    }

    private fun publishPlaces(placesModel: List<PlaceModel>) {
        currentPage++
        isLoading = false
        placesAdapter!!.addAll(placesModel)
        if(placesModel.isEmpty()){
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
        QurbaApplication.setCurrentActivity(this)
    }
}