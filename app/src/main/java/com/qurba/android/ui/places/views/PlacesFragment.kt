package com.qurba.android.ui.places.views

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.qurba.android.R
import com.qurba.android.adapters.PlacesAdapter
import com.qurba.android.databinding.FragmentPlacesBinding
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.PlaceModel
import com.qurba.android.ui.places.view_models.PlacesViewModel
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.addBottomSheetFragment
import java.util.*
import java.util.concurrent.TimeUnit
import com.qurba.android.utils.extenstions.addRxTextWatcher
import kotlin.collections.ArrayList

class PlacesFragment : Fragment(), OnRefreshListener, SelectAddressCallBack {
    private var isLastPage: Boolean = false
    private var viewModel: PlacesViewModel? = null
    private var binding: FragmentPlacesBinding? = null
    private var placesAdapter: PlacesAdapter? = null
    private var currentPage = Constants.PAGE_START
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    private var placesFilterFragment: PlacesFilterFragment? = null
    private var querySearch = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_places, container, false)
        setHasOptionsMenu(true)
        //        ((BaseActivity) getActivity()).setSupportActionBar(binding.toolbar);
        initialization()
        initListeners()
        return binding!!.root
    }

    private fun initialization() {
        viewModel = ViewModelProvider(this).get(PlacesViewModel::class.java)
        viewModel?.activity = activity as BaseActivity?
        binding!!.viewModel = viewModel
        placesFilterFragment = PlacesFilterFragment()
        placesFilterFragment!!.setSelectOfferCallBack { deliveryResponse: AddAddressModel -> onSelect(deliveryResponse) }
        initializeAdapters()
        initializeObservables()
        binding!!.refreshLayout.setOnRefreshListener(this)
        onRefresh()
        binding!!.shimmerLayout.startShimmer()
        setFilterResultsData()
    }

    private fun clearFilterations(){
        sharedPreferencesManager.clearPlaceFilterModel()
        binding?.placeSearchView?.setQuery("", false)
        binding?.placeSearchView?.clearFocus()
        querySearch = ""
        binding?.filteringResultsLl?.visibility = View.GONE
        binding?.divider?.visibility = View.GONE
    }

    private fun initListeners() {
        binding!!.clearFiltersTv.setOnClickListener { v ->
            clearFilterations()
        }

        binding!!.filterPlaceTv.setOnClickListener { v ->
            SystemUtils.hideKeyBoard(context, v)
            addBottomSheetFragment(placesFilterFragment!!, "PlaceTypesFragment")
        }

        binding!!.placeSearchView.findViewById<SearchView.SearchAutoComplete
                >(androidx.appcompat.R.id.search_src_text)?.addRxTextWatcher()?.debounce(500, TimeUnit.MILLISECONDS)
                ?.observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                ?.subscribeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                ?.subscribe {
                    //DO api request
                    currentPage = 1
                    querySearch = it.toString()
                    placesAdapter!!.clearAll()
                    viewModel?.getPlaces(currentPage, it ?: "")
                }
    }

    private fun initializeAdapters() {
        val linearLayoutManager = LinearLayoutManager(activity)
        placesAdapter = PlacesAdapter(requireActivity() as BaseActivity, ArrayList())
        binding!!.placesRv.adapter = placesAdapter
        binding!!.placesRv.layoutManager = linearLayoutManager
        binding!!.placesRv.isNestedScrollingEnabled = false
        linearLayoutManager.orientation = RecyclerView.VERTICAL
//        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        itemDecoration.setDrawable(resources.getDrawable(R.drawable.layer, null))
//        binding!!.placesRv.addItemDecoration(itemDecoration)
        binding!!.nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = binding!!.nestedScrollView.getChildAt(binding!!.nestedScrollView.childCount - 1)
            val diff = view.bottom - (binding!!.nestedScrollView.height + binding!!.nestedScrollView
                    .scrollY)
            if (diff == 0 && !isLastPage) {
                binding!!.progressBar.visibility = View.VISIBLE
                viewModel!!.getPlaces(currentPage, querySearch)
            }
        }
    }

    private fun initializeObservables() {
        viewModel!!.placesObservable!!.observe(viewLifecycleOwner, { offersModels: List<PlaceModel> -> publishPlaces(offersModels) })
    }

    private fun publishPlaces(offersModels: List<PlaceModel>) {
        isLastPage = false
        binding?.refreshLayout?.isRefreshing = false
        binding!!.progressBar.visibility = View.GONE
        if (!offersModels.isNullOrEmpty()) {
            placesAdapter!!.addAll(offersModels)

            if (currentPage == 1) {
                Handler(Looper.getMainLooper()).postDelayed({  viewModel?.setLoading(false) }, 100)
                binding?.shimmerLayout?.stopShimmer()
                binding?.refreshLayout?.isRefreshing = false
                Handler(Looper.getMainLooper()).postDelayed({ binding?.nestedScrollView?.smoothScrollTo(0, 0) }, 10)
            }

            currentPage++
            return
        }
        if (placesAdapter!!.itemCount == 0)
            binding!!.shimmerLayout.stopShimmer()

        isLastPage = true
    }

    override fun onRefresh() {
        isLastPage = false
        currentPage = Constants.PAGE_START
        binding!!.shimmerLayout.startShimmer()
        placesAdapter!!.clearAll()
        viewModel!!.getPlaces(currentPage, querySearch)
    }

    fun filterPlaces() {
        currentPage = Constants.PAGE_START
        binding!!.shimmerLayout.startShimmer()
        viewModel!!.getPlaces(currentPage, querySearch)
        placesAdapter!!.clearAll()
        setFilterResultsData()
    }

    private fun setFilterResultsData() {
        binding!!.filteringResultsLl.removeAllViews()
        val filterModel = sharedPreferencesManager.placeFilterModel ?: return
        binding!!.filteringResultsLl.visibility = View.VISIBLE
        binding!!.divider.visibility = View.VISIBLE
        val layoutInflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val categories = filterModel.categoryModels
        if (!categories.isNullOrEmpty()) {
            for (i in categories.indices) {
                val view = layoutInflater.inflate(R.layout.item_filter_result, null)
                view.id = i
                val category = view.findViewById<TextView>(R.id.title_tv)
                category.text = categories[i]!!.name.en
                binding!!.filteringResultsLl.addView(view)
                view.setOnClickListener { v: View ->
                    categories.removeAt(v.id)
                    filterModel.categoryModels = categories
                    sharedPreferencesManager.placeFilterModel = filterModel
                    onSelect(null)
                }
            }
        }
        if (filterModel.activeNow != null) {
            val view = layoutInflater.inflate(R.layout.item_filter_result, null)
            (view.findViewById<View>(R.id.title_tv) as TextView).text = getString(R.string.active_now)
            binding!!.filteringResultsLl.addView(view)
            view.setOnClickListener { v: View? ->
                filterModel.activeNow = null
                sharedPreferencesManager.placeFilterModel = filterModel
                onSelect(null)
            }
        }
        if (filterModel.canDeliver != null) {
            val view = layoutInflater.inflate(R.layout.item_filter_result, null)
            (view.findViewById<View>(R.id.title_tv) as TextView).text = getString(R.string.can_deliver)
            binding!!.filteringResultsLl.addView(view)
            view.setOnClickListener {
                filterModel.canDeliver = null
                sharedPreferencesManager.placeFilterModel = filterModel
                onSelect(null)
            }
        }
    }

    override fun onSelect(deliveryResponse: AddAddressModel?) {
        filterPlaces()
    }

    override fun onActivityResult(n: Int, n2: Int, intent: Intent?) {
        super.onActivityResult(n, n2, intent)
        if (n == 1087 || n2 == -1) {
            val placeModel = if (intent?.extras?.getString(Constants.PLACE) != null) Gson().fromJson(intent?.extras?.getString(Constants.PLACE), PlaceModel::class.java) else null
            if (SharedPreferencesManager.getInstance().placePosition != -1 && placeModel != null) {
                placesAdapter!!.updatePlace(SharedPreferencesManager.getInstance().placePosition, placeModel)
                sharedPreferencesManager.placePosition = -1
            }
        }
    }

    companion object {
        fun newInstance(): PlacesFragment {
            return PlacesFragment()
        }
    }
}