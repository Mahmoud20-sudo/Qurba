package com.qurba.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.databinding.ItemCommentBinding;
import com.qurba.android.databinding.ItemPlaceBinding;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.ui.comments.view_models.CommentItemViewModel;
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment;
import com.qurba.android.ui.places.view_models.PlaceItemViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.CommentsCallBack;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.SharedPreferencesManager;
import com.vanniktech.emoji.EmojiPopup;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import static com.qurba.android.utils.extenstions.ExtesionsKt.showKeyboard;

public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private List<PlaceModel> placeModels;
    private BaseActivity activity;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    public PlacesAdapter(BaseActivity activity, List<PlaceModel> placeModels) {
        this.placeModels = placeModels;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaceViewHolder placeViewHolder = (PlaceViewHolder) holder;
        PlaceItemViewModel placeItemViewModel = new PlaceItemViewModel(activity, placeModels.get(position));
        placeViewHolder.itemBinding.setViewModel(placeItemViewModel);
        placeItemViewModel.setAddCommentCallBack(placeViewHolder);
        placeViewHolder.itemBinding.setPosition(position);
        placeViewHolder.itemView.setTag(position);
        setCommentsUserData(activity, placeViewHolder, placeModels.get(position));

        Glide.with(activity).load(placeModels.get(position).getPlaceProfilePictureUrl()).into(placeViewHolder.itemBinding.placeImageIv);
        Glide.with(activity).load(placeModels.get(position).getPlaceProfileCoverPictureUrl()).placeholder(R.mipmap.offer_details_place_holder).into(placeViewHolder.itemBinding.placeCoverIv);
        if (sharedPref.getUser() != null)
            Glide.with(activity).load(sharedPref.getUser().getProfilePictureUrl())
                    .placeholder(R.drawable.ic_profile_placeholder).into(placeViewHolder.itemBinding.profileAvatarIv);
    }

    private void setCommentsUserData(Context context, PlaceViewHolder placeViewHolder, PlaceModel placeModel) {
        placeViewHolder.itemBinding.commentsContainerFm.removeAllViews();
        if (placeModel.getRecentComment() == null || placeModel.getRecentComment().get_id() == null || placeModel.getRecentComment().getUser() == null) {
            placeViewHolder.itemBinding.commentsParentLl.setVisibility(View.GONE);
            return;
        }
        placeViewHolder.itemBinding.commentsParentLl.setVisibility(View.VISIBLE);
        ItemCommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_comment, placeViewHolder.itemBinding.commentsContainerFm, false);
        CommentItemViewModel commentItemViewModel = new CommentItemViewModel(activity, placeModel.getRecentComment());
        binding.setViewModel(commentItemViewModel);
        placeModel.getRecentComment().setParentId(placeModel.get_id());
        placeModel.getRecentComment().setType("place");
        Glide.with(context).load(placeModel.getRecentComment().getUser().getProfilePictureUrl()).placeholder(R.drawable.ic_profile_placeholder).into(binding.userImageIv);
        placeViewHolder.itemBinding.commentsContainerFm.addView(binding.getRoot());
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(placeViewHolder.itemView).build(placeViewHolder.itemBinding.addCommentEt);
        placeViewHolder.itemBinding.emjoisIv.setOnClickListener(v -> emojiPopup.toggle());
        placeViewHolder.itemBinding.addCommentEt.setOnFocusChangeListener((v, hasFocus) -> placeViewHolder.itemBinding.actionsFl.setVisibility(hasFocus ? View.VISIBLE : View.GONE));
        binding.parentRv.setOnLongClickListener(placeViewHolder.itemBinding.getViewModel().commentLongPress());
        binding.viewMoreTv.setOnTouchListener(placeViewHolder.itemBinding.getViewModel().replyClickListener());
        binding.replyLl.setOnTouchListener(placeViewHolder.itemBinding.getViewModel().replyClickListener());
        binding.commentsReplyTv.setOnTouchListener(placeViewHolder.itemBinding.getViewModel().replyClickListener());
        placeViewHolder.itemBinding.getViewModel().setCommentActionEvents(placeViewHolder);
        if (placeModel.getRecentComment().getRecentReply() != null) {
            binding.replyLl.setVisibility(View.VISIBLE);
            Glide.with(context).load(placeModel.getRecentComment().getRecentReply().getUser().getProfilePictureUrl()).placeholder(R.drawable.ic_profile_placeholder).into(binding.replyUserImageIv);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return placeModels.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder implements CommentsCallBack, EditDeleteOverlayFragment.CommentActionEvents {

        ItemPlaceBinding itemBinding;

        PlaceViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void onCommentClick(@NotNull CommentModel commentModel, boolean isEdit, boolean isReply, int commentPosition) {
            itemBinding.getViewModel().setCommentId(commentModel.get_id());
            if (isEdit) {
                itemBinding.getViewModel().setEdit(true);
                itemBinding.addCommentEt.setText(commentModel.getComment());
                itemBinding.addCommentEt.setSelection(Objects.requireNonNull(itemBinding.addCommentEt.getText()).length());
                itemBinding.addCommentEt.setFocusableInTouchMode(true);
                itemBinding.addCommentEt.requestFocus();
                showKeyboard(activity);
            } else {
                activity.showConfirmDialog(commentModel.getRepliesCount() > 0, true, () -> itemBinding.getViewModel().deletePLaceComment());
            }
        }

        @Override
        public void onCommentAdded(CommentModel var1, boolean isReply) {
            PlaceModel placeModel = placeModels.get((Integer) itemView.getTag());
            placeModel.setCommentsCount(placeModel.getCommentsCount() + 1);
            //in case
            if (placeModel.getRecentComment() != null) {
                if (isReply && !placeModel.getRecentComment().get_id().equalsIgnoreCase(var1.getParentId()))
                    return;
            }
            itemBinding.getViewModel().getCommentsCount().set(placeModel.getCommentsCount() + " " + activity.getString(R.string.comments));
            itemBinding.getViewModel().getCommentText().set("");
            itemBinding.actionsFl.setVisibility(View.GONE);
            itemBinding.addCommentEt.clearFocus();

            if (isReply) {
                placeModel.getRecentComment().setRecentReply(var1);
            } else {
                placeModel.setRecentComment(var1);
            }
            placeModels.set((Integer) itemView.getTag(), placeModel);
            notifyItemChanged((Integer) itemView.getTag());
        }

        @Override
        public void onCommentUpdated(CommentModel var1, boolean isReply) {
            PlaceModel placeModel = placeModels.get((Integer) itemView.getTag());

            if (!var1.get_id().equalsIgnoreCase(placeModel.getRecentComment().get_id()))
                return;

            if (isReply) {
                placeModel.getRecentComment().setRecentReply(var1);
            } else {
                placeModel.setRecentComment(var1);
            }
            placeModels.set((Integer) itemView.getTag(), placeModel);
            notifyItemChanged((Integer) itemView.getTag());
        }

        @Override
        public void onCommentDeleted(boolean isReply, int deletedCommentsount) {
            PlaceModel placeModel = placeModels.get((Integer) itemView.getTag());
            placeModel.setCommentsCount(placeModel.getCommentsCount() - (isReply ? 1 : deletedCommentsount));
            itemBinding.getViewModel().getCommentText().set(placeModel.getCommentsCount() + " " + activity.getString(R.string.comments));

            if (!isReply)
                placeModel.setRecentComment(null);
            else if (placeModel.getRecentComment().getRepliesCount() == deletedCommentsount)
                placeModel.getRecentComment().setRecentReply(null);

            placeModels.set((Integer) itemView.getTag(), placeModel);
            notifyItemChanged((Integer) itemView.getTag());
        }
    }

    public void add(PlaceModel response) {
        if (!placeModels.contains(response)) {
            placeModels.add(response);
            notifyItemInserted(placeModels.size() - 1);
        }
    }

    public void addAll(List<PlaceModel> postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (PlaceModel response : postItems) {
                add(response);
            }
        }
    }

    public void updatePlace(int position, PlaceModel placeModel) {
        try {
            this.placeModels.set(position, placeModel);
            this.notifyItemChanged(position);
            this.placeModels.set(position, placeModel);
            this.notifyItemChanged(position);
        } catch (java.lang.IndexOutOfBoundsException e) {
            QurbaLogger.Companion.logging(activity,
                    Constants.UPDATE_PLACE_CRASH, Line.LEVEL_ERROR,
                    "Failed to update place ", Arrays.toString(e.getStackTrace()));
        }
    }


    public void clearAll() {
        placeModels.clear();
        notifyDataSetChanged();
    }

}