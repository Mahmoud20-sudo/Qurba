package com.qurba.android.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.ui.image_fragment.ImageFragment
import com.qurba.android.utils.SelectAddressCallBack

class ImagePagerAdapter(manager: FragmentManager, private val images: List<String>) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), SelectAddressCallBack {

    private var onItemSelectionListener: SelectAddressCallBack? = null

    fun setItemSelectionListener(onItemSelectionListener: SelectAddressCallBack) {
        this.onItemSelectionListener = onItemSelectionListener
    }

    override fun getItem(position: Int): Fragment {
        val fragment = ImageFragment()
        fragment.imageUrl = images[position]
        fragment.setItemSelectionListener(this)
        return fragment
    }

    override fun getCount(): Int = images.size

    override fun onSelect(deliveryResponse: AddAddressModel?) {
        onItemSelectionListener?.onSelect(deliveryResponse)
    }

}