/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  com.qurba.android.network.models.ProductCommentModel
 *  java.lang.Object
 */
package com.qurba.android.utils;

import com.qurba.android.network.models.AddAddressModel;

public interface ItemClickEvents {
    void onItemClickListener(AddAddressModel addAddressModel);
    void onItemClickListener(int position , boolean isAddress);
    void onDeleteListener(int position , boolean isAddress);
    void onBackClickListener();
}

