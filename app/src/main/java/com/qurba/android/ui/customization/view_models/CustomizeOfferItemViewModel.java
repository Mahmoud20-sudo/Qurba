package com.qurba.android.ui.customization.view_models;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.qurba.android.R;
import com.qurba.android.adapters.CustomizationsViewHolder;
import com.qurba.android.network.models.SectionItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.text.NumberFormat;
import java.util.List;

public class CustomizeOfferItemViewModel extends ViewModel implements Observable {

    private final Sections sectionModule;
    private final int position;
    private BaseActivity activity;
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private SharedPreferencesManager sharePref = SharedPreferencesManager.getInstance();
    private LayoutInflater inflater;


    @BindingAdapter("layoutMarginBottom")
    public void setLayoutMarginBottom(View view, Float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.bottomMargin = Math.round(dimen);
        view.setLayoutParams(layoutParams);
    }

    @Bindable
    public String getTitle() {
        String title = sectionModule.getTitle().getEn();
        if (sectionModule.getSectionsGroup() == null && sectionModule.getMinimumChoice() > 1) {
            title += " (" + activity.getString(R.string.choose) + " " + NumberFormat.getInstance().format(sectionModule.getMinimumChoice()) + ")";
        }

        return title;
    }

    @Bindable
    public String getSectionGroupTitle() {
        if (sectionModule.getSectionsGroup() == null)
            return "";

        return sectionModule.getSectionsGroup().getTitle().getEn();
    }

    @Bindable
    public String getDescription() {
        return sectionModule.getDescription() != null ? sectionModule.getDescription().getEn() : "";
    }

    @Bindable
    public String getType() {
        return sectionModule.isRequired() ? activity.getString(R.string.required) : activity.getString(R.string.optional);
    }

    @Bindable
    public String getGroupType() {
        if (sectionModule.getSectionsGroup() == null) return "";
        return sectionModule.getSectionsGroup().isRequired() ? activity.getString(R.string.required) : activity.getString(R.string.optional);
    }

    @Bindable
    public String getFreeItems() {
        if (sectionModule.getSectionsGroup() == null) {
            if (sectionModule.isFreeSection())
                return activity.getString(R.string.free_items);
            else if (sectionModule.isDiscountSection())
                return activity.getString(R.string.discount);
        } else {
            if (sectionModule.getSectionsGroup().isFreeSectionGroup())
                return activity.getString(R.string.free_items);
            else if (sectionModule.getSectionsGroup().isDiscountSectionGroup())
                return activity.getString(R.string.discount);
        }
        return "";
    }

    public CustomizeOfferItemViewModel(BaseActivity activity, Sections sectionModule, int position) {
        this.activity = activity;
        this.sectionModule = sectionModule;
        this.position = position;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View.OnClickListener followPlace() {
        return v -> {

        };
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public void setSectionItems(List<Sections> sectionsList, CustomizationsViewHolder customOffersViewHolder,
                                CustomizationViewModel customizationViewModel, RecyclerView.Adapter adapter) {
        customOffersViewHolder.itemBinding.radioGroup1.removeAllViews();

        final RadioButton[] rb = new RadioButton[sectionsList.get(position).getItems().size()];

        RecyclerView.LayoutParams bottomMargin = (RecyclerView.LayoutParams) customOffersViewHolder.itemView.getLayoutParams();
        bottomMargin.bottomMargin = sectionsList.get(position).getSectionsGroup() != null ? 0 : 15;
        customOffersViewHolder.itemView.setLayoutParams(bottomMargin);

        if (sectionsList.get(position).getSectionsGroup() != null) {
            customOffersViewHolder.itemBinding.itemStatusTv.setVisibility(View.GONE);
            customOffersViewHolder.itemBinding.itemCustomFreeItemTv.setVisibility(View.GONE);

            if (position == 0 || sectionsList.get(position - 1).getSectionsGroup() == null) {
                customOffersViewHolder.itemBinding.groupLl.setVisibility(View.VISIBLE);
            } else if (!sectionsList.get(position).getSectionsGroup().get_id().
                    equalsIgnoreCase(sectionsList.get(position - 1).getSectionsGroup().get_id())) {
                bottomMargin.topMargin = 15;
                customOffersViewHolder.itemBinding.groupLl.setVisibility(View.VISIBLE);
            }
        } else {
            customOffersViewHolder.itemBinding.itemStatusTv.setVisibility(View.VISIBLE);
            customOffersViewHolder.itemBinding.itemCustomFreeItemTv.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < rb.length; i++) {
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 15, 0, 15);
            LinearLayout child = (LinearLayout) inflater.inflate(R.layout.item_radio_group, null);

            CheckBox checkBox = child.findViewById(R.id.checkbox);
            RadioButton radioButton = child.findViewById(R.id.radio_btn);
            TextView descriptionTV = child.findViewById(R.id.description_tv);
            TextView titleTv = child.findViewById(R.id.title_tv);

            checkBox.setVisibility(sectionsList.get(position).isMultipleChoice() ? View.VISIBLE : View.GONE);
            radioButton.setVisibility(sectionsList.get(position).isMultipleChoice() ? View.GONE : View.VISIBLE);

            titleTv.setText(sectionsList.get(position).getItems().get(i).getTitle().getEn());

            descriptionTV.setText(sectionsList.get(position).getItems().get(i).getDescription().getEn());
            if (sectionsList.get(position).getItems().get(i).getDescription().getEn() == null || sectionsList.get(position).getItems().get(i).getDescription().getEn().isEmpty())
                descriptionTV.setVisibility(View.GONE);
            else
                descriptionTV.setVisibility(View.VISIBLE);

            radioButton.setBackgroundColor(Color.TRANSPARENT);
            radioButton.setTag(position);
            radioButton.setId(i);
            checkBox.setTag(position);
            checkBox.setId(i);

            TextView txtPrice = child.findViewById(R.id.item_price_tv);
            txtPrice.setText(sharePref.getLanguage().equalsIgnoreCase("ar") ? sectionsList.get(position).getItems().get(i).getPriceTxt() + " " + activity.getString(R.string.currency) :
                    activity.getString(R.string.currency) + " " + sectionsList.get(position).getItems().get(i).getPriceTxt());
            child.setLayoutParams(params);

            checkBox.setOnCheckedChangeListener((view, isChecked) -> {
                if (isChecked) {
                    //==================for multi-ple sections only============================
//                    if (sections.get((Integer) view.getTag()).getSectionsGroup() == null) {
                    if (sectionsList.get(position).getSelectedItem().size() >= sectionsList.get(position).getMaximumChoice()) {
                        checkBox.setChecked(false);
                        return;
                    }
//                  }
//                    if (customizOffersViewModel.checkIfGroupSectionAlreadySelected(sections.get((Integer) view.getTag())))
                    clearGroupSelection(sectionsList, sectionsList.get(position), customizationViewModel, adapter);

                    sectionsList.get(position).getSelectedItem().add(sectionsList.get(position).getBackupItems().get(view.getId()));
                    sectionsList.get(position).setItems(sectionsList.get(position).getSelectedItem());
                    customizationViewModel.addSectionWithCheckBox(sectionsList.get(position));
                    checkBox.setChecked(true);
                } else {
                    sectionsList.get(position).getSelectedItem().remove(sectionsList.get(position).getBackupItems().get(view.getId()));
                    sectionsList.get(position).setItems(sectionsList.get(position).getSelectedItem());
                    customizationViewModel.removeSectionWithCheckBox(sectionsList.get(position));
                }
            });

            radioButton.setOnClickListener(view -> {
                clearRadioChecked(customOffersViewHolder.itemBinding.radioGroup1);

                if (!sectionsList.get(position).getSelectedItem().isEmpty()) {
                    SectionItems sectionItems = sectionsList.get(position).getSelectedItem().get(0);
                    sectionsList.get(position).getSelectedItem().clear();
                    customizationViewModel.removeSection(sectionsList.get(position));
                    if (sectionItems.get_id().equalsIgnoreCase(sectionsList.get(position).getBackupItems().get(view.getId()).get_id()))
                        return;
                }

                clearGroupSelection(sectionsList, sectionsList.get(position), customizationViewModel, adapter);
                sectionsList.get(position).getSelectedItem().add(sectionsList.get(position).getBackupItems().get(view.getId()));
                sectionsList.get(position).setItems(sectionsList.get(position).getSelectedItem());
                customizationViewModel.addSection(sectionsList.get(position));
                radioButton.setChecked(true);
            });

            txtPrice.setVisibility(sectionsList.get(position).getItems().get(i).getPrice() == 0 || customizationViewModel.isVariablePrice() ? View.GONE : View.VISIBLE);
            customOffersViewHolder.itemBinding.radioGroup1.addView(child);
        }
    }

    public void clearGroupSelection(List<Sections> sections, Sections sectionModule,
                                    CustomizationViewModel customizationViewModel, RecyclerView.Adapter adapter) {
        if (sectionModule.getSectionsGroup() == null)
            return;

        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getSectionsGroup() != null
                    && !sections.get(i).get_id().equalsIgnoreCase(sectionModule.get_id())
                    && sections.get(i).getSectionsGroup().get_id().equalsIgnoreCase(sectionModule.getSectionsGroup().get_id())) {
                sections.get(i).getSelectedItem().clear();
                customizationViewModel.removeSection(sections.get(i));
                sections.get(i).setItems(sections.get(i).getBackupItems());
                adapter.notifyItemChanged(i);
            }
        }
    }

    public void clearRadioChecked(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            LinearLayout view = (LinearLayout) radioGroup.getChildAt(i);
            for (int j = 0; j < view.getChildCount(); j++) {
                if (view.getChildAt(j) instanceof RadioButton)
                    ((RadioButton) view.getChildAt(j)).setChecked(false);
            }
        }
    }
}