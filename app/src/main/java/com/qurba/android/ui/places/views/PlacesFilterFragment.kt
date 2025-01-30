package com.qurba.android.ui.places.views

/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.app.Dialog
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnShowListener
 *  android.os.Bundle
 *  android.view.KeyEvent
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.view.ViewParent
 *  android.view.inputmethod.InputMethodManager
 *  android.widget.FrameLayout
 *  android.widget.ImageView
 *  android.widget.TextView
 *  android.widget.TextView$OnEditorActionListener
 *  androidx.appcompat.widget.AppCompatEditText
 *  androidx.databinding.DataBindingUtil
 *  androidx.fragment.app.Fragment
 *  androidx.fragment.app.FragmentActivity
 *  androidx.lifecycle.LifecycleOwner
 *  androidx.lifecycle.MutableLiveData
 *  androidx.lifecycle.Observer
 *  androidx.lifecycle.ViewModel
 *  androidx.lifecycle.ViewModelProviders
 *  androidx.recyclerview.widget.LinearLayoutManager
 *  androidx.recyclerview.widget.RecyclerView
 *  androidx.recyclerview.widget.RecyclerView$Adapter
 *  androidx.recyclerview.widget.RecyclerView$LayoutManager
 *  com.google.android.material.bottomsheet.BottomSheetBehavior
 *  com.google.android.material.bottomsheet.BottomSheetDialog
 *  com.google.android.material.bottomsheet.BottomSheetDialogFragment
 *  com.qurba.android.adapters.CommentsAdapter
 *  com.qurba.android.network.models.ProductCommentModel
 *  com.qurba.android.ui.comments.view_models.CommentsPopUpViewModel
 *  com.qurba.android.utils.AddCommentCallBack
 *  com.qurba.android.utils.BaseActivity
 *  com.qurba.android.utils.ScreenUtils
 *  com.qurba.android.utils.SharedPreferencesManager
 *  com.qurba.android.utils.SocialLoginCallBack
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 */
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qurba.android.R
import com.qurba.android.adapters.OfferPricesAdapter
import com.qurba.android.databinding.DialogOffersFilterBinding
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.OffersFilteringModel
import com.qurba.android.network.models.PlaceCategoryModel
import com.qurba.android.network.models.response_models.CategoriesResponseModel
import com.qurba.android.ui.places.view_models.FilterViewModel
import com.qurba.android.ui.splash.view_models.SplashViewModel
import com.qurba.android.utils.*

class PlacesFilterFragment : BottomSheetDialogFragment(), OfferPricesAdapter.FilteringListeners {
    private lateinit var linf: LayoutInflater
    private lateinit var binding: DialogOffersFilterBinding
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var mContext: Context? = null
    private lateinit var placeFilterModel: OffersFilteringModel
    private var selectOfferCallBack: SelectAddressCallBack? = null
    private var viewModel: FilterViewModel? = null

    fun setSelectOfferCallBack(selectOfferCallBack: SelectAddressCallBack) {
        this.selectOfferCallBack = selectOfferCallBack
    }

    private fun initialization() {
        linf = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        placeFilterModel = sharedPref.placeFilterModel ?: OffersFilteringModel()
        initListeners()
        prepareViews()
        initObservables()
        if (!sharedPref.placeCatgories.isNullOrEmpty())
            setDataIngredients()
        else
            viewModel?.getPlaceCategories()
    }

    private fun initObservables() {
        viewModel?.getCategoriesObservable()?.observe(viewLifecycleOwner, Observer { this.setDataIngredients() })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun prepareViews() {
        binding.offerTypeTv.visibility = View.GONE
        binding.fexLayout.visibility = View.GONE
        binding.offerPriceTv.visibility = View.GONE
        binding.priceFiltersRv.visibility = View.GONE
        binding.categoriesFexLayout.visibility = View.VISIBLE
        binding.categoriesTv.visibility = View.VISIBLE

        binding.availabilityFexLayout.removeAllViews()
        val availabilityList = resources.getStringArray(R.array.offer_availabilty_options)
        for (serch in availabilityList) {
            val v: View = linf.inflate(R.layout.item_offer_type, null)
            binding.availabilityFexLayout.addView(v)
            v.tag = availabilityList.indexOf(serch)
            v.findViewById<TextView>(R.id.title_tv).text = serch
            v.setOnClickListener {
                //clearAvailabilitySelections()
                if (v.tag == 0 && placeFilterModel?.canDeliver?.equals("true") == true) {
                    clearSelecttion(v)
                    return@setOnClickListener
                }

                if (v.tag == 1 && placeFilterModel?.activeNow?.equals("true") == true) {
                    clearSelecttion(v)
                    return@setOnClickListener
                }

                setSelecttion(v)
                when (v.tag) {
                    0 -> placeFilterModel.canDeliver = "true"
                    1 -> placeFilterModel.activeNow = "true"
                }
            }
        }
    }

    private fun initListeners() {
        binding.offerItemResetBtn.setOnClickListener {
            clearSelections()
            sharedPref.clearPlaceFilterModel()
            selectOfferCallBack?.onSelect(AddAddressModel())
            dismiss()
        }

        binding.offerApplyBtn.setOnClickListener {
            sharedPref.placeFilterModel = placeFilterModel
            selectOfferCallBack?.onSelect(AddAddressModel())
            dismiss()
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(bundle) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialogInterface: DialogInterface ->
            BottomSheetBehavior.from((dialogInterface as BottomSheetDialog).findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet) as View)
                    .setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.dialog_offers_filter, viewGroup, false)
        viewModel = ViewModelProvider(this).get(FilterViewModel::class.java)
        val view = binding.root
        initialization()
        return view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
    }

    private fun setDataIngredients() {
        binding.categoriesFexLayout.removeAllViews()


        val categoriesList = sharedPref.placeCatgories
        val categories = placeFilterModel.categoryModels


        for (category in categoriesList) {
            val v: View = linf.inflate(R.layout.item_offer_type, null)

            binding.categoriesFexLayout.addView(v)
            v.tag = categoriesList.indexOf(category)
            v.findViewById<TextView>(R.id.title_tv).text = category.name.en

            if (categories.contains(category))
                setCategorySelection(categories)

            v.setOnClickListener {
                var category = categoriesList[v.tag as Int]
                if (categories?.contains(category) == true) {
                    clearCatorgry(v)
                    categories.remove(category)
                    return@setOnClickListener
                }
                categories.add(category)
                setCategorySelection(categories)
                placeFilterModel.categoryModels = categories
            }
        }

        if (!placeFilterModel.activeNow.isNullOrEmpty())
            setSelecttion(binding.availabilityFexLayout.getChildAt(1))
        if (!placeFilterModel.canDeliver.isNullOrEmpty())
            setSelecttion(binding.availabilityFexLayout.getChildAt(0))
    }

    private fun setSelecttion(v: View) {
        val txtView = v.findViewById<TextView>(R.id.title_tv)
        txtView.setTextColor(Color.WHITE)
        txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        txtView.background = resources.getDrawable(R.drawable.offer_filter_selected, null)
    }

    private fun clearSelecttion(v: View) {
        val txtView = v.findViewById<TextView>(R.id.title_tv)
        txtView.setTextColor(Color.parseColor("#949494"))
        txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        txtView.background = resources.getDrawable(R.drawable.offer_filter_unselected, null)
        when (v.tag) {
            0 -> placeFilterModel.canDeliver = null
            1 -> placeFilterModel.activeNow = null
        }
    }

    private fun setCategorySelection(categories: List<PlaceCategoryModel>) {
        for (index in 0 until binding.categoriesFexLayout.childCount) {
            val txtView = binding.categoriesFexLayout.getChildAt(index).findViewById<TextView>(R.id.title_tv)
            for (category in categories) {
                if (txtView.text.toString() == category.name.en) {
                    txtView.setTextColor(Color.WHITE)
                    txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                    txtView.background = resources.getDrawable(R.drawable.offer_filter_selected, null)
                }
            }
        }
    }

    private fun clearSelections() {
        clearCategpriesSelections()
        clearAvailabilitySelections()
    }

    private fun clearCatorgry(v: View) {
        val txtView = v.findViewById<TextView>(R.id.title_tv)
        txtView.setTextColor(Color.parseColor("#949494"))
        txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        txtView.background = resources.getDrawable(R.drawable.offer_filter_unselected, null)
    }

    private fun clearCategpriesSelections() {
        for (index in 0 until binding.categoriesFexLayout.childCount) {
            val txtView = binding.categoriesFexLayout.getChildAt(index).findViewById<TextView>(R.id.title_tv)
            txtView.setTextColor(Color.parseColor("#949494"))
            txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            txtView.background = resources.getDrawable(R.drawable.offer_filter_unselected, null)
        }
        placeFilterModel.categoryModels = null
    }

    private fun clearAvailabilitySelections() {
        for (index in 0 until binding.availabilityFexLayout.childCount) {
            val txtView = binding.availabilityFexLayout.getChildAt(index).findViewById<TextView>(R.id.title_tv)
            txtView.setTextColor(Color.parseColor("#949494"))
            txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            txtView.background = resources.getDrawable(R.drawable.offer_filter_unselected, null)
        }
        placeFilterModel.activeNow = null
        placeFilterModel.canDeliver = null
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, n: Int) {
        super.setupDialog(dialog, n)
        val view = View.inflate(this.context, R.layout.dialog_offers_filter, null)
        dialog.setContentView(view)
        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        val bottomSheetBehavior = mBottomSheetBehavior
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.peekHeight = ScreenUtils(this.context).height
            view.requestLayout()
        }
    }

    override fun onFilterClicked(price: Int) {
        placeFilterModel.price = price
    }
}