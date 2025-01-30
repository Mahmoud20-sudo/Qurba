package com.qurba.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.qurba.android.R

class MyInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    private val myContentsView: View = (
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.map_info_window, null)

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker): View? {
        return myContentsView
    }


}