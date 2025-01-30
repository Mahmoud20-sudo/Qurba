package com.qurba.android.utils

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qurba.android.R

class SwipeToRefreshLayout(context: Context, attrs: AttributeSet) : SwipeRefreshLayout(context, attrs) {

    init {
        setColorSchemeResources(R.color.order_color, R.color.order_color, R.color.order_color);
    }

    var listener: OnRefreshListener? = null

    fun setRefreshing(refreshing: Boolean, fireCallback: Boolean) {
        super.setRefreshing(refreshing)
        listener?.onRefresh()
    }

    override fun setOnRefreshListener(listener: OnRefreshListener?) {
        super.setOnRefreshListener(listener)
        this.listener = listener
    }
}