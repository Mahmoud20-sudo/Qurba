package com.qurba.android.utils;

import android.view.View;

public interface DeletionListener {
    void onDeleteItemListener(int pos);
    void onQuantityChangeListener(int newQty , int pos);
}

