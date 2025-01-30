package com.qurba.android.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemMenuOfferBinding;
import com.qurba.android.databinding.ItemSectionProductBinding;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.network.models.ProductData;
import com.qurba.android.network.models.Sections;
import com.qurba.android.ui.place_details.view_models.PlaceProductsViewModel;
import com.qurba.android.ui.products.view_models.ProductItemViewModel;
import com.qurba.android.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private List<ProductData> addedProductsList = new ArrayList<>();
    private List<ProductData> products;
    private PlaceModel placeModel;
    private BaseActivity activity;

    private static final int TYPE_MENU_OFFER = 1;
    private static final int TYPE_PRODUCT = 2;
    private boolean isDiscountMenu;
    private PlaceProductsViewModel placeProductsViewModel;

    public void setDiscountMenu(boolean discountMenu) {
        isDiscountMenu = discountMenu;
        notifyDataSetChanged();
    }

    public void setPlaceProductsViewModel(PlaceProductsViewModel placeProductsViewModel) {
        this.placeProductsViewModel = placeProductsViewModel;
    }

    public ProductsAdapter(BaseActivity activity, List<ProductData> products) {
        this.products = products;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MENU_OFFER:
                return new MenuOfferViewHolder(inflater.inflate(R.layout.item_menu_offer, parent, false));
            default:
                return new ProductViewHolder(inflater.inflate(R.layout.item_section_product, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isDiscountMenu)
            return TYPE_MENU_OFFER;
        return TYPE_PRODUCT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_MENU_OFFER:
                MenuOfferViewHolder menuOfferViewHolder = (MenuOfferViewHolder) holder;
                menuOfferViewHolder.itemBinding.setPlaceProductsViewModel(placeProductsViewModel);
                break;
            default:
                int pos = isDiscountMenu ? position - 1 : position;
                if (products.get(pos) != null) {
                    ProductViewHolder productViewHolder = (ProductViewHolder) holder;
                    ProductItemViewModel productItemViewModel = new ProductItemViewModel(activity, products.get(pos), pos);
                    productItemViewModel.setPlaceInfo(placeModel);
                    productViewHolder.itemBinding.setViewModel(productItemViewModel);
                    Glide.with(activity).load(products.get(pos).getImageURL())
                            .placeholder(R.drawable.ic_image_placeholder).into(productViewHolder.itemBinding.itemProductAvatarIv);

                    productViewHolder.itemBinding.setPosition(pos);
                    productViewHolder.itemBinding.itemSectionNameTv.setText(products.get(pos).getCategory().getName().getEn());
                    productViewHolder.itemBinding.itemSectionNameTv.setVisibility(setCategory(pos) ? View.VISIBLE : View.GONE);
                    productViewHolder.itemBinding.productOldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                break;
        }
    }

    private boolean setCategory(int position) {
        if (position != 0) {
            if (!products.get(position).getCategory().get_id().equalsIgnoreCase(products.get(position - 1).getCategory().get_id()))
                return true;
            else
                return false;
        } else {
            return true;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return isDiscountMenu ? products.size() + 1 : products.size();
    }

    public void setPlaceInfo(PlaceModel placeInfo) {
        this.placeModel = placeInfo;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        ItemSectionProductBinding itemBinding;

        ProductViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }

    class MenuOfferViewHolder extends RecyclerView.ViewHolder {

        ItemMenuOfferBinding itemBinding;

        MenuOfferViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }

    public void add(ProductData response) {
        if (!products.contains(response)) {
            products.add(response);
            notifyItemInserted(products.size() - 1);
        }
    }

    public void addAll(List<ProductData> postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (ProductData response : postItems) {
                add(response);
            }
        }
    }

    public void updateProduct(int position, ProductData productData) {
        try {
//            JsonElement product = this.orderNowEntityModels.get(position);
//            product.getAsJsonObject().addProperty("isLikedByUser", productData.isProductLikedByUser());
//            product.getAsJsonObject().addProperty("likesCount", productData.getLikesCount());
            productData.setHassections(products.get(position).isHassections());
            products.set(position, productData);
            this.notifyItemChanged(position);
        } catch (Exception e) {
            Log.e("exception", e.getLocalizedMessage());
        }
    }
}