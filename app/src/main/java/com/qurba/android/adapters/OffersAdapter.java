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
import com.qurba.android.databinding.ItemOfferBinding;
import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.ui.comments.view_models.CommentItemViewModel;
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment;
import com.qurba.android.ui.offers.view_models.OfferItemViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.CommentsCallBack;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.SharedPreferencesManager;
import com.vanniktech.emoji.EmojiPopup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showKeyboard;

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private List<OffersModel> offers;
    private ArrayList<OrdersModel> orders = new ArrayList<>();

    private BaseActivity activity;
    private static final int TYPE_OFFER = 1;
    private static final int TYPE_ORDER = 2;
    public static final int ORDER_DISPLAY_FREQUENCY = 1;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    public OffersAdapter(BaseActivity activity, List<OffersModel> offers) {
        this.offers = offers;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OffersModel offersModel = offers.get(position);

        OfferViewHolder offerViewHolder = (OfferViewHolder) holder;
        offerViewHolder.itemView.setTag(position);
        OfferItemViewModel offerItemViewModel = new OfferItemViewModel(activity, offersModel, true);

        Glide.with(activity).load(offersModel.getPictureUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.offer_details_place_holder).into(offerViewHolder.itemBinding.offerImageIv);
        Glide.with(activity).load(offersModel.getPlaceId()
                .getPlaceProfilePictureUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(offerViewHolder
                        .itemBinding.placeImageIv);

        offerItemViewModel.setAddCommentCallBack(offerViewHolder);
        offerViewHolder.itemBinding.setViewModel(offerItemViewModel);
        offerViewHolder.itemBinding.offerOldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        offerViewHolder.itemBinding.setPosition(position);

        DateUtils.getShortDateFromTimeStamp(offersModel.getEndDate());
        setCommentsUserData(offerViewHolder.itemView.getContext(), offerViewHolder, offersModel);

        if(!offersModel.isOrderable()){
            offerViewHolder.itemBinding.availabilityTv.setCompoundDrawablesWithIntrinsicBounds(
                    sharedPref.getLanguage().equalsIgnoreCase("ar") ? null : activity.getResources().getDrawable(R.drawable.ic_closed),
                    null,
                    sharedPref.getLanguage().equalsIgnoreCase("en") ? null : activity.getResources().getDrawable(R.drawable.ic_closed),
                    null);
        }
    }

    private void setCommentsUserData(Context context, OfferViewHolder offerViewHolder, OffersModel offersModel) {
        offerViewHolder.itemBinding.commentsContainerFm.removeAllViews();
        if (offersModel.getRecentComment() == null || offersModel.getRecentComment().get_id() == null || offersModel.getRecentComment().getUser() == null) {
            offerViewHolder.itemBinding.commentsParentLl.setVisibility(View.GONE);
            return;
        }
        offerViewHolder.itemBinding.commentsParentLl.setVisibility(View.VISIBLE);
        ItemCommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_comment, offerViewHolder.itemBinding.commentsContainerFm, false);
        CommentItemViewModel commentItemViewModel = new CommentItemViewModel(activity, offersModel.getRecentComment());
        binding.setViewModel(commentItemViewModel);
        offersModel.getRecentComment().setParentId(offersModel.get_id());
        offersModel.getRecentComment().setType(offersModel.getModule());
        offerViewHolder.itemBinding.commentsContainerFm.addView(binding.getRoot());
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(offerViewHolder.itemView).build(offerViewHolder.itemBinding.addCommentEt);
        offerViewHolder.itemBinding.emjoisIv.setOnClickListener(v -> emojiPopup.toggle());
        offerViewHolder.itemBinding.addCommentEt.setOnFocusChangeListener((v, hasFocus) -> offerViewHolder.itemBinding.actionsFl.setVisibility(hasFocus ? View.VISIBLE : View.GONE));
        binding.parentRv.setOnLongClickListener(offerViewHolder.itemBinding.getViewModel().commentLongPress());
        binding.viewMoreTv.setOnTouchListener(offerViewHolder.itemBinding.getViewModel().replyClickListener());
        binding.replyLl.setOnTouchListener(offerViewHolder.itemBinding.getViewModel().replyClickListener());
        binding.commentsReplyTv.setOnTouchListener(offerViewHolder.itemBinding.getViewModel().replyClickListener());
        offerViewHolder.itemBinding.getViewModel().setCommentActionEvents(offerViewHolder);
        if (offersModel.getRecentComment().getRecentReply() != null) {
            binding.replyLl.setVisibility(View.VISIBLE);
            Glide.with(context).load(offersModel.getRecentComment().getRecentReply().getUser().getProfilePictureUrl()).placeholder(R.drawable.ic_profile_placeholder).into(binding.replyUserImageIv);
        }
        Glide.with(context).load(offersModel.getRecentComment().getUser().getProfilePictureUrl()).placeholder(R.drawable.ic_profile_placeholder).into(binding.userImageIv);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < ORDER_DISPLAY_FREQUENCY && position < orders.size()) {
            return TYPE_ORDER;
        }
        return TYPE_OFFER;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public void add(OffersModel response) {
        if (!offers.contains(response)) {
            offers.add(response);
            notifyItemInserted(offers.size() - 1);
        }
    }

    public void addAll(List<OffersModel> postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (OffersModel response : postItems) {
                add(response);
            }
        }
    }

    public void addOrders(List<OrdersModel> postItems) {
        if (!postItems.isEmpty()) {
            orders.addAll(postItems);
            notifyDataSetChanged();
        }
    }

    public void updateOffer(int position, OffersModel offersModel) {
        this.offers.set(position, offersModel);
        this.notifyItemChanged(position);
    }

    public void clearAll() {
        offers.clear();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder implements CommentsCallBack, EditDeleteOverlayFragment.CommentActionEvents {

        ItemOfferBinding itemBinding;

        OfferViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void onCommentAdded(CommentModel var1, boolean isReply) {
            OffersModel offersModel = offers.get((Integer) itemView.getTag());

            //in case
            if (offersModel.getRecentComment() != null) {
                if (isReply && !offersModel.getRecentComment().get_id().equalsIgnoreCase(var1.getParentId()))
                    return;
            }

            itemBinding.getViewModel().getCommentsCount().set(offersModel.getCommentsCount() + " " + activity.getString(R.string.comments));
            itemBinding.getViewModel().getCommentText().set("");
            itemBinding.actionsFl.setVisibility(View.GONE);
            itemBinding.addCommentEt.clearFocus();

            if (isReply) {
                offersModel.getRecentComment().setRecentReply(var1);
            } else {
                offersModel.setRecentComment(var1);
                offersModel.setCommentsCount(offersModel.getCommentsCount() + 1);
            }
//            setCommentsUserData(itemView.getContext(), this, offersModel);
            offers.set((Integer) itemView.getTag(),offersModel);
            notifyItemChanged((Integer) itemView.getTag());
        }

        @Override
        public void onCommentUpdated(CommentModel var1, boolean isReply) {
            OffersModel offersModel = offers.get((Integer) itemView.getTag());

            if (!var1.get_id().equalsIgnoreCase(offersModel.getRecentComment().get_id()))
                return;

            if (isReply) {
                offersModel.getRecentComment().setRecentReply(var1);
            } else {
                offersModel.setRecentComment(var1);
            }
//            orderNowEntityModels.set(getAdapterPosition(), new Gson().toJsonTree(offersModel));
//            setCommentsUserData(itemView.getContext(), this, offersModel);
            offers.set((Integer) itemView.getTag(), offersModel);
            notifyItemChanged((Integer) itemView.getTag());
        }

        @Override
        public void onCommentDeleted(boolean isReply, int deletedCommentsount) {
            OffersModel offersModel = offers.get((Integer) itemView.getTag());
            offersModel.setCommentsCount(offersModel.getCommentsCount() - (isReply ? 1 : deletedCommentsount));
            itemBinding.getViewModel().getCommentsCount().set(offersModel.getCommentsCount() + " " + activity.getString(R.string.comments));

            if (!isReply)
                offersModel.setRecentComment(null);
            else if (isReply && offersModel.getRecentComment().getRepliesCount() == deletedCommentsount)
                offersModel.getRecentComment().setRecentReply(null);

            offers.set((Integer) itemView.getTag(), offersModel);
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
                activity.showConfirmDialog(commentModel.getRepliesCount() > 0, true, () -> itemBinding.getViewModel().deleteOfferComment());
            }
        }
    }

}