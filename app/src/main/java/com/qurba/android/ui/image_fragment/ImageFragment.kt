package com.qurba.android.ui.image_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.qurba.android.R
import com.qurba.android.utils.SelectAddressCallBack

class ImageFragment : Fragment() {
    var imageUrl = ""
    private var onItemSelectionListener: SelectAddressCallBack? = null



    fun setItemSelectionListener(onItemSelectionListener: SelectAddressCallBack) {
        this.onItemSelectionListener = onItemSelectionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(requireContext()).load(imageUrl).placeholder(R.mipmap.offer_details_place_holder)
                    .into(view.findViewById<ImageView>(R.id.itemImage));
        }

        view.findViewById<ImageView>(R.id.itemImage).setOnClickListener {
            onItemSelectionListener?.onSelect(null)
        }
    }
}