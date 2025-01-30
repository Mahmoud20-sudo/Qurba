package com.qurba.android.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.databinding.adapters.SearchViewBindingAdapter.OnQueryTextSubmit
import com.qurba.android.R

class CustomSearchView : SearchView, OnQueryTextSubmit {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        val searchAutoComplete: SearchAutoComplete = findViewById(androidx.appcompat.R.id.search_src_text)
        searchAutoComplete.setTextColor(Color.BLACK)
        searchAutoComplete.setHint(R.string.search_hint)
        searchAutoComplete.setHintTextColor(context.resources.getColor(R.color.search_hint))
        val closeButtonImage = findViewById<ImageView>(R.id.search_close_btn)
        closeButtonImage.setOnClickListener { v: View? ->
            searchAutoComplete.setText("")
            clearFocus()
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        // your search methods
        clearFocus()
        return true
    }
}