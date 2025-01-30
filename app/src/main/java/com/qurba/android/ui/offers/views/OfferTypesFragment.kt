package com.qurba.android.ui.offers.views

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qurba.android.R
import com.qurba.android.adapters.CachedDeliveryAdapter
import com.qurba.android.adapters.OfferPricesAdapter
import com.qurba.android.databinding.DialogOffersFilterBinding
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.OffersFilteringModel
import com.qurba.android.network.models.PlaceCategoryModel
import com.qurba.android.network.models.request_models.FilterModel
import com.qurba.android.utils.*

class OfferTypesFragment : BottomSheetDialogFragment(), OfferPricesAdapter.FilteringListeners {
    private lateinit var binding: DialogOffersFilterBinding
    private val adapter: CachedDeliveryAdapter? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var mContext: Context? = null
    private lateinit var offerPricesAdapter: OfferPricesAdapter

    private lateinit var offerFilterModel: OffersFilteringModel
    private var selectOfferCallBack: SelectAddressCallBack? = null

    fun setSelectOfferCallBack(selectOfferCallBack: SelectAddressCallBack) {
        this.selectOfferCallBack = selectOfferCallBack
    }

    private fun initialization() {
        offerFilterModel = sharedPref.filterModel ?: OffersFilteringModel()
        initializeAdapters()
        initListeners()
        setDataIngredients()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initListeners() {
        binding.offerItemResetBtn.setOnClickListener {
            clearSelections()
            sharedPref.clearFilterModel()
            selectOfferCallBack?.onSelect(AddAddressModel())
            dismiss()
        }

        binding.offerApplyBtn.setOnClickListener {
            sharedPref.filterModel = offerFilterModel
            selectOfferCallBack?.onSelect(AddAddressModel())
            dismiss()
        }
    }

    private fun initializeAdapters() {

        val copyArray: Array<String> =
            resources.getStringArray(R.array.offer_price_options).copyOfRange(
                1,
                resources.getStringArray(R.array.offer_price_options).size
            )

        offerPricesAdapter = OfferPricesAdapter(
            activity as BaseActivity?,
            copyArray, this, true
        )
        val horizontalLayoutManager = LinearLayoutManager(context)
        horizontalLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.priceFiltersRv.isNestedScrollingEnabled = true
        binding.priceFiltersRv.layoutManager = horizontalLayoutManager
        binding.priceFiltersRv.adapter = offerPricesAdapter

        offerPricesAdapter.setSelction(copyArray.indexOf(offerFilterModel.price.toString()))
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

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.dialog_offers_filter, viewGroup, false
        )
        val view = binding.root
        initialization()
        return view
    }

    private fun setDataIngredients() {
        val linf =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        binding.fexLayout.removeAllViews()
        val typesList = resources.getStringArray(R.array.offer_types_options)
        val availabilityList = resources.getStringArray(R.array.offer_availabilty_options)

        for (index in typesList.indices) {
            val v: View = linf.inflate(R.layout.item_offer_type, null)
            v.id = index
            v.tag = Constants.OFFER_TYPES[index]
            v.findViewById<TextView>(R.id.title_tv).text = typesList[index]
            binding.fexLayout.addView(v)

            v.setOnClickListener {
                val types = offerFilterModel.offerType
                var selctedType = ""

                when (it.id) {
                    0 -> selctedType = Constants.OFFER_TYPE_DISCOUNT
                    1 -> selctedType = Constants.OFFER_TYPE_FREE_ITEMS
                    2 -> selctedType = Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS
                    3 -> selctedType = Constants.OFFER_TYPE_FREE_DELIVERY
                    4 -> selctedType = Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU
                }

                if (types?.contains(selctedType) == true) {
                    clearSelecttion(v)
                    types.remove(selctedType)
                    return@setOnClickListener
                }
                types.add(selctedType)
                setTypesSelection(types)
                offerFilterModel.offerType = types
            }
        }

        binding.availabilityFexLayout.removeAllViews()
        for (serch in availabilityList) {
            val v: View = linf.inflate(R.layout.item_offer_type, null)
            binding.availabilityFexLayout.addView(v)
            v.tag = availabilityList.indexOf(serch)
            v.findViewById<TextView>(R.id.title_tv).text = serch
            v.setOnClickListener {
                if (v.tag == 0 && offerFilterModel?.canDeliver?.equals("true") == true) {
                    clearSelecttion(v)
                    return@setOnClickListener
                }

                if (v.tag == 1 && offerFilterModel?.activeNow?.equals("true") == true) {
                    clearSelecttion(v)
                    return@setOnClickListener
                }

                setSelecttion(v)
                when (v.tag) {
                    0 -> offerFilterModel.canDeliver = "true"
                    1 -> offerFilterModel.activeNow = "true"
                }
            }
        }

        if (!offerFilterModel.offerType.isNullOrEmpty()) {
            for (i in offerFilterModel.offerType.indices) {

                when (offerFilterModel.offerType[i]) {
                    Constants.OFFER_TYPE_DISCOUNT -> setSelecttion(binding.fexLayout.getChildAt(0))
                    Constants.OFFER_TYPE_FREE_ITEMS -> setSelecttion(binding.fexLayout.getChildAt(1))
                    Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS -> setSelecttion(binding.fexLayout.getChildAt(2))
                    Constants.OFFER_TYPE_FREE_DELIVERY -> setSelecttion(binding.fexLayout.getChildAt(3))
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU -> setSelecttion(binding.fexLayout.getChildAt(4))
                }
            }
        }

        if (!offerFilterModel.activeNow.isNullOrEmpty())
            setSelecttion(binding.availabilityFexLayout.getChildAt(1))
        if (!offerFilterModel.canDeliver.isNullOrEmpty())
            setSelecttion(binding.availabilityFexLayout.getChildAt(0))
    }

    private fun clearSelecttion(v: View) {
        val txtView = v.findViewById<TextView>(R.id.title_tv)
        txtView.setTextColor(Color.parseColor("#949494"))
        txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        txtView.background = resources.getDrawable(R.drawable.offer_filter_unselected, null)
        when (v.tag) {
            0 -> offerFilterModel.canDeliver = null
            1 -> offerFilterModel.activeNow = null
        }
    }

    private fun setSelecttion(v: View) {
        val txtView = v.findViewById<TextView>(R.id.title_tv)
        txtView.setTextColor(Color.WHITE)
        txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        txtView.background = resources.getDrawable(R.drawable.offer_filter_selected, null)
    }

    private fun clearSelections() {
        offerPricesAdapter.setSelction(-1)
        clearTypeSelections()
        clearAvailabilitySelections()
    }

    private fun clearTypeSelections() {
        for (index in 0 until binding.fexLayout.childCount) {
            val txtView = binding.fexLayout.getChildAt(index).findViewById<TextView>(R.id.title_tv)
            txtView.setTextColor(Color.parseColor("#949494"))
            txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            txtView.background = resources.getDrawable(R.drawable.offer_filter_unselected, null)
        }
        offerFilterModel.offerType = null
    }

    private fun setTypesSelection(types: List<String>) {
        for (index in 0 until binding.fexLayout.childCount) {
            val txtView = binding.fexLayout.getChildAt(index).findViewById<TextView>(R.id.title_tv)
            for (type in types) {
                if (type.equals(binding.fexLayout.getChildAt(index).tag.toString(), true)) {
                    txtView.setTextColor(Color.WHITE)
                    txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                    txtView.background =
                        resources.getDrawable(R.drawable.offer_filter_selected, null)
                }
            }
        }
    }

    private fun clearAvailabilitySelections() {
        for (index in 0 until binding.availabilityFexLayout.childCount) {
            val txtView = binding.availabilityFexLayout.getChildAt(index)
                .findViewById<TextView>(R.id.title_tv)
            txtView.setTextColor(Color.parseColor("#949494"))
            txtView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            txtView.background = resources.getDrawable(R.drawable.offer_filter_unselected, null)
        }
        offerFilterModel.activeNow = null
        offerFilterModel.canDeliver = null
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
        offerFilterModel.price = price
    }
}