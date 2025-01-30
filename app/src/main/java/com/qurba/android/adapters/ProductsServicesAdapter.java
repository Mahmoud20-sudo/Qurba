package com.qurba.android.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qurba.android.R;
import com.qurba.android.ui.place_details.views.GalleryPreviewActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductsServicesAdapter extends RecyclerView.Adapter<ProductsServicesAdapter.ImageViewHolder> {

    private Activity activity;
    private List<String> images;
    private LayoutInflater inflater;

    public ProductsServicesAdapter(Activity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        images = new ArrayList<>();
    }

    @Override
    public ProductsServicesAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductsServicesAdapter.ImageViewHolder(inflater.inflate(R.layout.gallery_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String url = images.get(position);

        //holder.imageView.layout(0, 0, 0, 0);
        Glide.with(activity).load(url).placeholder(R.mipmap.place_details_placeholder).into(holder.imageView);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 173, activity.getResources().getDisplayMetrics()));
        holder.imageView.setLayoutParams(layoutParams);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, GalleryPreviewActivity.class);
            intent.putExtra("image", url);
            activity.startActivity(intent);
        });

//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, ImageScrollActivity.class);
//                intent.putExtra("position", position);
//                activity.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setData(List<String> images) {
        this.images.clear();
        if (images != null) {
            this.images.addAll(images);
        }
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_thumbnail);
        }
    }
}