package com.qurba.android.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qurba.android.R;
import com.qurba.android.databinding.ItemOpeningTimesBinding;
import com.qurba.android.network.models.DayModel;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.qurba.android.utils.SystemUtils.setLocalizedDays;

public class OpeningTimesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private SortedMap<String, DayModel> days;
    private Context context;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    public OpeningTimesAdapter(Context context, SortedMap<String, DayModel> days) {
        this.days = days;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_opening_times, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DayViewHolder placeViewHolder = (DayViewHolder) holder;
        DayModel dayModel = ((DayModel) days.values().toArray()[position]);

        if (days.keySet().toArray()[position].toString().equalsIgnoreCase(DateUtils.getCurrentDayName())) {
            placeViewHolder.itemBinding.dayName.setText(R.string.today);
            placeViewHolder.itemBinding.dayName.setTextColor(ContextCompat.getColor(context, R.color.black));
            placeViewHolder.itemBinding.firstShiftTv.setTextColor(ContextCompat.getColor(context, R.color.black));
            placeViewHolder.itemBinding.secondShiftTv.setTextColor(ContextCompat.getColor(context, R.color.black));

            placeViewHolder.itemBinding.firstShiftTv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            placeViewHolder.itemBinding.dayName.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            placeViewHolder.itemBinding.secondShiftTv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        } else {
            placeViewHolder.itemBinding.dayName.setText(sharedPref.getLanguage().equalsIgnoreCase("ar") ?
                    setLocalizedDays(context, days.keySet().toArray()[position].toString()) :
                    days.keySet().toArray()[position].toString());
        }

        if (dayModel.isDayOpen()) {
            placeViewHolder.itemBinding.firstShiftTv.setText(
                    dayModel.getShifts().get(0).getOpens().getHour() +
                            ":" + dayModel.getShifts().get(0).getOpens().getMinutes() +
                            " - " + dayModel.getShifts().get(0).getCloses().getHour() + ":"
                            + dayModel.getShifts().get(0).getCloses().getMinutes());
            if (dayModel.getShifts().size() > 1) {
                placeViewHolder.itemBinding.secondShiftTv.setVisibility(View.VISIBLE);
                placeViewHolder.itemBinding.secondShiftTv.setText(dayModel.getShifts().get(1).getOpens().getHour() +
                        ":" + dayModel.getShifts().get(1).getOpens().getMinutes()
                        + " - " + dayModel.getShifts().get(1).getCloses().getHour() + ":"
                        + dayModel.getShifts().get(1).getCloses().getMinutes());
            }
        } else {
            placeViewHolder.itemBinding.firstShiftTv.setText(context.getString(R.string.closed));
            placeViewHolder.itemBinding.firstShiftTv.setTextColor(ContextCompat.getColor(context, R.color.main_red_color));
        }
    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {

        ItemOpeningTimesBinding itemBinding;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }
    }
}