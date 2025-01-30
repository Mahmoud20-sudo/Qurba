package com.qurba.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qurba.android.R;
import com.qurba.android.network.models.ProductData;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroup;
import com.qurba.android.ui.customization.view_models.CustomizProductsViewModel;
import com.qurba.android.ui.customization.view_models.CustomizeOfferItemViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class CustomProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private CustomizProductsViewModel customizProductsViewModel;
    private BaseActivity activity;
    private ProductData productData;
    private List<Sections> sections = new ArrayList<>();
    private int sectionsGroupSize;
    String previousId = "";
    private SharedPreferencesManager sharePref = SharedPreferencesManager.getInstance();
    private String previousSectionGroupTitle = "";

    public CustomProductsAdapter(BaseActivity activity, ProductData productData) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.productData = productData;
        setSections();
    }

    public void setCustomizProductsViewModel(CustomizProductsViewModel customizProductsViewModel) {
        this.customizProductsViewModel = customizProductsViewModel;
    }

    private void setSections() {
        sections.addAll(productData.getSections());
        if (productData.getSectionGroups() != null && !productData.getSectionGroups().isEmpty()) {
            for (SectionsGroup sectionsGroup : productData.getSectionGroups()) {
                for (Sections sec : sectionsGroup.getSections()) {
//                    sec.setMinimumChoice(3);
                    sec.setSectionsGroup(sectionsGroup);
                    sections.add(sec);
                }
            }
        }
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
        customOffersViewHolder.itemBinding.groupLl.setVisibility(View.GONE);
        customOffersViewHolder.itemBinding.setViewModel(viewModel);
        customizProductsViewModel.setBasePrice(productData.getBasePrice());
        viewModel.setSectionItems(sections, customOffersViewHolder, customizProductsViewModel, this);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }
}
