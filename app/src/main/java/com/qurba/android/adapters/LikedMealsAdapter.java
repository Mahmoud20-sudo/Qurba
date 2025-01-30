package com.qurba.android.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemCommentBinding;
import com.qurba.android.databinding.ItemProductBinding;
import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.ProductData;
import com.qurba.android.ui.comments.view_models.CommentItemViewModel;
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment;
import com.qurba.android.ui.products.view_models.ProductItemViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.CommentsCallBack;
import com.vanniktech.emoji.EmojiPopup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showKeyboard;

public class LikedMealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private List<ProductData> productDataList;
    private BaseActivity activity;

    public LikedMealsAdapter(BaseActivity activity, List<ProductData> productDataList) {
        this.productDataList = productDataList;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductData product = productDataList.get(position);

        ProductsViewHolder productViewHolder = (ProductsViewHolder) holder;
        ProductItemViewModel productItemViewModel = new ProductItemViewModel(activity, product, position);
        productViewHolder.itemBinding.setViewModel(productItemViewModel);
        Glide.with(activity).load(product.getImageURL())
                .placeholder(R.mipmap.offer_details_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(productViewHolder.itemBinding.itemProductAvatarIv);
        Glide.with(activity).load(product.getPlaceInfo()
                .getPlaceProfilePictureUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(productViewHolder
                        .itemBinding.placeImageIv);

        productViewHolder.itemBinding.setPosition(position);
        this.setCommentsUserData(productViewHolder.itemView.getContext(), productViewHolder, product);
        productItemViewModel.setAddCommentCallBack(productViewHolder);
        productViewHolder.itemBinding.productOldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        productViewHolder.itemView.setTag(position);
    }

    private void setCommentsUserData(Context context, ProductsViewHolder productsViewHolder, ProductData productData) {
        productsViewHolder.itemBinding.commentsContainerFm.removeAllViews();
        if (productData.getRecentComment() == null || productData.getRecentComment().get_id() == null || productData.getRecentComment().getUser() == null) {
            productsViewHolder.itemBinding.commentsParentLl.setVisibility(View.GONE);
            return;
        }
        productsViewHolder.itemBinding.commentsParentLl.setVisibility(View.VISIBLE);
        ItemCommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_comment, productsViewHolder.itemBinding.commentsContainerFm, false);
        CommentItemViewModel commentItemViewModel = new CommentItemViewModel(activity, productData.getRecentComment());
        binding.setViewModel(commentItemViewModel);
        productData.getRecentComment().setParentId(productData.get_id());
        productData.getRecentComment().setType(productData.getModule());
        Glide.with(context).load(productData.getRecentComment().getUser().getProfilePictureUrl()).placeholder(R.drawable.ic_profile_placeholder).into(binding.userImageIv);
        productsViewHolder.itemBinding.commentsContainerFm.addView(binding.getRoot());
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(productsViewHolder.itemView).build(productsViewHolder.itemBinding.addCommentEt);
        productsViewHolder.itemBinding.emjoisIv.setOnClickListener(v -> emojiPopup.toggle());
        productsViewHolder.itemBinding.addCommentEt.setOnFocusChangeListener((v, hasFocus) -> productsViewHolder.itemBinding.actionsFl.setVisibility(hasFocus ? View.VISIBLE : View.GONE));
        binding.parentRv.setOnLongClickListener(productsViewHolder.itemBinding.getViewModel().commentLongPress());
        binding.viewMoreTv.setOnTouchListener(productsViewHolder.itemBinding.getViewModel().replyClickListener());
        binding.replyLl.setOnTouchListener(productsViewHolder.itemBinding.getViewModel().replyClickListener());
        productsViewHolder.itemBinding.getViewModel().setCommentActionEvents(productsViewHolder);
        if (productData.getRecentComment().getRecentReply() != null) {
            binding.replyLl.setVisibility(View.VISIBLE);
            Glide.with(context).load(productData.getRecentComment().getRecentReply().getUser().getProfilePictureUrl()).placeholder(R.drawable.ic_profile_placeholder).into(binding.replyUserImageIv);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return productDataList.size();
    }

    public void add(ProductData response) {
        if (!productDataList.contains(response)) {
            productDataList.add(response);
            notifyItemInserted(productDataList.size() - 1);
        }
    }

    public void addAll(List<ProductData> postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (ProductData response : postItems) {
                add(response);
            }
        }
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder implements CommentsCallBack, EditDeleteOverlayFragment.CommentActionEvents {

        ItemProductBinding itemBinding;
        private int postion = -1;

        ProductsViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void onCommentAdded(CommentModel var1, boolean isReply) {
            ProductData productData = productDataList.get((Integer) itemView.getTag());

            if (productData.getRecentComment() != null) {
                if (isReply && !productData.getRecentComment().get_id().equalsIgnoreCase(var1.getParentId()))
                    return;
            }

            itemBinding.getViewModel().getCommentsCount().set(productData.getCommentsCount() + " " + activity.getString(R.string.comments));
            itemBinding.getViewModel().getCommentText().set("");
            itemBinding.actionsFl.setVisibility(View.GONE);
            itemBinding.addCommentEt.clearFocus();

            if (isReply) {
                productData.getRecentComment().setRecentReply(var1);
            } else {
                productData.setRecentComment(var1);
                productData.setCommentsCount(productData.getCommentsCount() + 1);
            }
            productDataList.set((Integer) itemView.getTag(), productData);
            notifyItemChanged((Integer) itemView.getTag());
//            setCommentsUserData(itemView.getContext(), this, productData);
        }

        @Override
        public void onCommentUpdated(CommentModel var1, boolean isReply) {
            ProductData productData = productDataList.get((Integer) itemView.getTag());

            if (!var1.get_id().equalsIgnoreCase(productData.getRecentComment().get_id()))
                return;

            if (isReply) {
                productData.getRecentComment().setRecentReply(var1);
            } else {
                productData.setRecentComment(var1);
            }
//            orderNowEntityModels.set(getAdapterPosition(), new Gson().toJsonTree(productData));
//            setCommentsUserData(itemView.getContext(), this, productData);
            productDataList.set((Integer) itemView.getTag(), productData);
            notifyItemChanged((Integer) itemView.getTag());
        }

        @Override
        public void onCommentDeleted(boolean isReply, int deletedCommentsount) {
            ProductData productData = productDataList.get((Integer) itemView.getTag());
            productData.setCommentsCount(productData.getCommentsCount() - (isReply ? 1 : deletedCommentsount));
            itemBinding.getViewModel().getCommentsCount().set(productData.getCommentsCount() + " " + activity.getString(R.string.comments));

            if (!isReply)
                productData.setRecentComment(null);
            else if (isReply && productData.getRecentComment().getRepliesCount() == deletedCommentsount)
                productData.getRecentComment().setRecentReply(null);

//            orderNowEntityModels.set(getAdapterPosition(), new Gson().toJsonTree(productData));
//            setCommentsUserData(itemView.getContext(), this, productData);
            productDataList.set((Integer) itemView.getTag(), productData);
            notifyItemChanged((Integer) itemView.getTag());
        }

        @Override
        public void onCommentClick(@NotNull CommentModel commentModel, boolean isEdit, boolean isReply, int commentPosition) {
            itemBinding.getViewModel().setCommentId(commentModel.get_id());
            if (isEdit) {
                itemBinding.getViewModel().setEdit(true);
//                itemBinding.getViewModel().commentText.set(commentModel.getComment());
                itemBinding.addCommentEt.setText(commentModel.getComment());
                itemBinding.addCommentEt.setSelection(itemBinding.addCommentEt.getText().length());
                itemBinding.addCommentEt.setFocusableInTouchMode(true);
                itemBinding.addCommentEt.requestFocus();
                showKeyboard(activity);
            } else {
                activity.showConfirmDialog(commentModel.getRepliesCount() > 0, true, () -> itemBinding.getViewModel().deleteProductComment());
            }
        }

        public void setItemPosition(int position) {
            this.postion = position;
        }
    }

}