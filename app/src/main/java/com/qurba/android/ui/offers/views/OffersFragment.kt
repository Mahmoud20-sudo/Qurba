package com.qurba.android.ui.offers.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.qurba.android.adapters.FeaturedPlacesAdapter
import com.qurba.android.adapters.PlaceOffersAdapter
import com.qurba.android.databinding.FragmentOffersBinding
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.OffersModel
import com.qurba.android.network.models.PlaceModel
import com.qurba.android.ui.offers.view_models.OffersViewModel
import com.qurba.android.utils.*
import com.qurba.android.utils.RecyclerItemClickListener.OnItemClickListener
import com.qurba.android.utils.extenstions.addBottomSheetFragment
import com.qurba.android.utils.extenstions.addRxTextWatcher
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class OffersFragment : Fragment(), OnRefreshListener, SelectAddressCallBack {
    private var isLastPage: Boolean = false
    private var viewModel: OffersViewModel? = null
    private var binding: FragmentOffersBinding? = null
    private var offersAdapter: PlaceOffersAdapter? = null
    private var featuredPlacesAdapter: FeaturedPlacesAdapter? = null
    private var currentPage = Constants.PAGE_START
    private var placeId: String? = null
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    private val placeModels: List<PlaceModel> = ArrayList()
    private var offerTypesFragment: OfferTypesFragment? = null
    private var querySearch = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offers, container, false)
        setHasOptionsMenu(true)
        //        ((BaseActivity) getActivity()).setSupportActionBar(binding.toolbar);
        initialization()
        initListeners()
        return binding?.root
    }

    private fun initialization() {
        viewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        binding?.viewModel = viewModel
        offerTypesFragment = OfferTypesFragment()
        offerTypesFragment!!.setSelectOfferCallBack { deliveryResponse: AddAddressModel ->
            onSelect(
                deliveryResponse
            )
        }
        initializeAdapters()
        initializeObservables()
        binding?.refreshLayout?.setOnRefreshListener(this)
        onRefresh()
        featuredPlaces
        binding?.shimmerLayout?.startShimmer()
        setFilterResultsData()
    }

    val featuredPlaces: Unit
        get() {
            viewModel!!.getFeaturedPlaces()
        }

    private fun clearFilterations() {
        sharedPreferencesManager.clearFilterModel()
        placeId = null
        featuredPlacesAdapter!!.selectedPosition = -1
        featuredPlacesAdapter!!.notifyDataSetChanged()
        binding?.filteringResultsLl?.visibility = View.GONE
        binding?.offersSearchView?.setQuery("", false)//submit false not working anymore
        binding?.offersSearchView?.clearFocus()
        querySearch = ""
    }

    private fun initListeners() {
        binding?.filterOfferTv?.setOnClickListener { v ->
            SystemUtils.hideKeyBoard(context, v)
            addBottomSheetFragment(offerTypesFragment!!, "OfferTypesFragment")
        }

        binding?.clearFiltersTv?.setOnClickListener { v ->
            clearFilterations()
        }

        binding?.offersSearchView?.findViewById<SearchView.SearchAutoComplete
                >(androidx.appcompat.R.id.search_src_text)?.addRxTextWatcher()
            ?.debounce(500, TimeUnit.MILLISECONDS)
            ?.observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            ?.subscribeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            ?.subscribe {
                //DO api request
                currentPage = 1
                querySearch = it.toString()
                viewModel?.getOffers(currentPage, placeId, it)
            }
    }

    private fun initializeAdapters() {
        val linearLayoutManager = LinearLayoutManager(activity)
        offersAdapter = PlaceOffersAdapter(requireActivity() as BaseActivity, ArrayList())
        binding?.offersRv?.adapter = offersAdapter
        binding?.offersRv?.layoutManager = linearLayoutManager
        binding?.offersRv?.isNestedScrollingEnabled = false
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        val horizontalLinearLayoutManager = LinearLayoutManager(activity)
        featuredPlacesAdapter =
            FeaturedPlacesAdapter(requireActivity() as BaseActivity, placeModels)
        binding?.featuredPlacesRv?.adapter = featuredPlacesAdapter
        binding?.featuredPlacesRv?.layoutManager = horizontalLinearLayoutManager
        horizontalLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.layer, null))
        binding?.offersRv?.addItemDecoration(itemDecoration)
        binding?.nestedScrollView?.viewTreeObserver?.addOnScrollChangedListener {
            val view =
                binding!!.nestedScrollView.getChildAt(binding?.nestedScrollView!!.childCount - 1)
            val diff =
                view.bottom - (binding!!.nestedScrollView?.height + binding?.nestedScrollView?.scrollY!!)
            if (diff == 0 && !isLastPage) {
                binding?.progressBar?.visibility = if (currentPage > 1) View.VISIBLE else View.GONE
                currentPage++
                viewModel!!.getOffers(currentPage, placeId, querySearch)
            }
        }

        binding?.featuredPlacesRv?.addOnItemTouchListener(
            RecyclerItemClickListener(requireContext(),
                binding?.featuredPlacesRv!!, object : OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        when {
                            featuredPlacesAdapter!!.selectedPosition == position || position == -1 -> {
                                featuredPlacesAdapter!!.selectedPosition = -1
                                placeId = null
                            }
                            else -> {
                                featuredPlacesAdapter!!.selectedPosition = position
                                placeId = placeModels[position]._id
                            }
                        }
                        filterOffers()
                        featuredPlacesAdapter?.notifyDataSetChanged()
                    }
                })
        )
    }

    private fun initializeObservables() {
        viewModel!!.offersObservable.observe(
            viewLifecycleOwner,
            { offersModels: List<OffersModel> -> publishOffers(offersModels) })
        viewModel!!.placesObservable.observe(
            viewLifecycleOwner,
            { placeModels: List<PlaceModel> -> publishPlaces(placeModels) })
    }

    private fun publishPlaces(placeModels: List<PlaceModel>) {
        if (placeModels.isEmpty()) return
        binding?.featuredPlacesLl?.visibility = View.VISIBLE
        featuredPlacesAdapter?.addAll(placeModels)
    }

    private fun publishOffers(offersModels: List<OffersModel>) {
        binding?.progressBar?.visibility = View.GONE
        isLastPage = false
        if (currentPage == 1) offersAdapter?.clearAll()
        if (!offersModels.isNullOrEmpty()) {
            if (offersAdapter?.itemCount == 0) {
                binding?.refreshLayout?.isRefreshing = false
                binding?.shimmerLayout?.stopShimmer()
                Handler(Looper.getMainLooper()).postDelayed({ viewModel?.isLoading = false }, 100)
                binding?.offersRv?.scrollToPosition(0)
            }
            offersAdapter?.addAll(offersModels)
            return
        }
        if (offersAdapter?.itemCount == 0)
            binding?.shimmerLayout?.stopShimmer()

        isLastPage = true
    }

    override fun onActivityResult(n: Int, n2: Int, intent: Intent?) {
        super.onActivityResult(n, n2, intent)
        if (!isResumed) return
        if (n == 1000 || n2 == -1) {
            val offersModel = if (intent!!.extras != null) Gson().fromJson(
                intent.extras!!.getString(Constants.OFFERS),
                OffersModel::class.java
            ) else null
            if (SharedPreferencesManager.getInstance().offerPosition != -1 && offersModel != null) {
                offersAdapter?.updateOffer(
                    SharedPreferencesManager.getInstance().offerPosition,
                    offersModel
                )
            }
        }
    }

    override fun onRefresh() {
        filterOffers()
    }

    fun filterOffers() {
        isLastPage = false
        currentPage = Constants.PAGE_START
        binding?.shimmerLayout?.startShimmer()
        viewModel?.getOffers(currentPage, placeId, querySearch)
        offersAdapter?.clearAll()
        setFilterResultsData()
    }

    private fun setFilterResultsData() {
        binding?.filteringResultsLl?.removeAllViews()
        if (sharedPreferencesManager.filterModel == null) return
        binding?.filteringResultsLl?.visibility = View.VISIBLE
        binding!!.divider.visibility = View.VISIBLE
        val layoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (sharedPreferencesManager.filterModel.offerType != null && !sharedPreferencesManager.filterModel.offerType.isEmpty()) {

            for (i in sharedPreferencesManager.filterModel.offerType.indices) {
                val view = layoutInflater.inflate(R.layout.item_filter_result, null)
                val offerType = view.findViewById<TextView>(R.id.title_tv)

                when (sharedPreferencesManager.filterModel.offerType[i]) {
                    Constants.OFFER_TYPE_DISCOUNT -> offerType.text = getString(R.string.discount)
                    Constants.OFFER_TYPE_FREE_ITEMS -> offerType.text =
                        getString(R.string.free_items)
                    Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS -> offerType.text =
                        getString(R.string.discount_on_items)
                    Constants.OFFER_TYPE_FREE_DELIVERY -> offerType.text =
                        getString(R.string.free_delivery_hint)
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU -> offerType.text =
                        getString(R.string.discount_on_menu)
                }
                binding!!.filteringResultsLl.addView(view)
                view.setOnClickListener {
                    val filterModel = sharedPreferencesManager.filterModel
                    filterModel.offerType = ArrayList()
                    sharedPreferencesManager.filterModel = filterModel
                    onSelect(null)
                }
            }
        }
        if (sharedPreferencesManager.filterModel.activeNow != null) {
            val view = layoutInflater.inflate(R.layout.item_filter_result, null)
            (view.findViewById<View>(R.id.title_tv) as TextView).text =
                getString(R.string.active_now)
            binding?.filteringResultsLl?.addView(view)
            view.setOnClickListener { v: View? ->
                val filterModel = sharedPreferencesManager.filterModel
                filterModel.activeNow = null
                sharedPreferencesManager.filterModel = filterModel
                onSelect(null)
            }
        }
        if (sharedPreferencesManager.filterModel.price > 0) {
            val view = layoutInflater.inflate(R.layout.item_filter_result, null)
            (view.findViewById<View>(R.id.title_tv) as TextView).text =
                sharedPreferencesManager.filterModel.localePrice + " " + getString(R.string.currency)
            binding?.filteringResultsLl?.addView(view)
            view.setOnClickListener { v: View? ->
                val filterModel = sharedPreferencesManager.filterModel
                filterModel.price = 0
                sharedPreferencesManager.filterModel = filterModel
                onSelect(null)
            }
        }
        if (sharedPreferencesManager.filterModel.canDeliver != null) {
            val view = layoutInflater.inflate(R.layout.item_filter_result, null)
            (view.findViewById<View>(R.id.title_tv) as TextView).text =
                getString(R.string.can_deliver)
            binding?.filteringResultsLl?.addView(view)
            view.setOnClickListener { v: View? ->
                val filterModel = sharedPreferencesManager.filterModel
                filterModel.canDeliver = null
                sharedPreferencesManager.filterModel = filterModel
                onSelect(null)
            }
        }
    }

    override fun onSelect(deliveryResponse: AddAddressModel?) {
        filterOffers()
    }

    companion object {
        fun newInstance(): OffersFragment {
            return OffersFragment()
        }
    }
}