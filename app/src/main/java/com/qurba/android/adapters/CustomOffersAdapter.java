package com.qurba.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qurba.android.R;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroup;
import com.qurba.android.ui.customization.view_models.CustomizOffersViewModel;
import com.qurba.android.ui.customization.view_models.CustomizeOfferItemViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class CustomOffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //    private List<PlaceCategoryModel> deliveries;
    private final LayoutInflater inflater;
    private CustomizOffersViewModel customizOffersViewModel;
    private SharedPreferencesManager sharePref = SharedPreferencesManager.getInstance();

    private BaseActivity activity;
    private List<Sections> sections = new ArrayList<>();
    private OffersModel offersModel;

    public CustomOffersAdapter(BaseActivity activity, OffersModel offersModel) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.offersModel = offersModel;
        setSections();
    }

    private void setSections() {
        sections.addAll(offersModel.getSections());
        if ((offersModel.getSectionGroups() != null && !offersModel.getSectionGroups().isEmpty())) {
            for (SectionsGroup sectionsGroup : offersModel.getSectionGroups()) {
                for (Sections sec : sectionsGroup.getSections()) {
//                    sec.setMinimumChoice(3);
                    sec.setRequired(sectionsGroup.isRequired());
                    sec.setSectionsGroup(sectionsGroup);
                    sections.add(sec);
                }
            }
        }
    }

    public void setCustomizOffersViewModel(CustomizOffersViewModel customizOffersViewModel) {
        this.customizOffersViewModel = customizOffersViewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_customization, parent, false);
        return new CustomizationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Sections section = sections.get(position);
        if (section.getBackupItems().isEmpty())// for backup purpose
            section.setBackupItems(section.getItems());

        CustomizationsViewHolder customOffersViewHolder = (CustomizationsViewHolder) holder;
        CustomizeOfferItemViewModel viewModel = new CustomizeOfferItemViewModel(activity, section, position);
        customOffersViewHolder.itemBinding.setViewModel(viewModel);
        customOffersViewHolder.itemBinding.groupLl.setVisibility(View.GONE);
        customizOffersViewModel.setBasePrice(offersModel.getBasePrice());
        viewModel.setSectionItems(sections, customOffersViewHolder, customizOffersViewModel, this);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

}
