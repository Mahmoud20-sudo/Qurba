package com.qurba.android.ui.my_orders.views;

import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qurba.android.R;
import com.qurba.android.adapters.MyOrdersAdapter;
import com.qurba.android.databinding.ActivityMyOrdersBinding;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.ui.my_orders.view_models.MyOrderViewModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.EndlessRecyclerViewScrollListener;
import com.qurba.android.utils.QurbaApplication;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends BaseActivity {

    private ActivityMyOrdersBinding binding;
    private MyOrderViewModel viewModel;
    private List<OrdersModel> ordersModels = new ArrayList<>();
    private EndlessRecyclerViewScrollListener recyclerViewScrollListener = null;
    private LinearLayoutManager linearLayoutManager;
    private MyOrdersAdapter rcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialization();
    }

    private void initialization() {
        viewModel = new ViewModelProvider(this).get(MyOrderViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders);
        binding.setViewModel(viewModel);
        binding.animToolbar.getNavigationIcon().setTint(Color.BLACK);

        viewModel.setActivity(this);
        viewModel.getMyOrders(1);
        initAdapters();
        initializeObservables();
        initListeners();
    }

    private void initAdapters() {
        binding.ordersRv.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.ordersRv.setLayoutManager(linearLayoutManager);

        rcAdapter = new MyOrdersAdapter(this, ordersModels);
        binding.ordersRv.setAdapter(rcAdapter);
    }

    private void initListeners() {
        binding.animToolbar.setNavigationOnClickListener(view -> onBackPressed());
        recyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                viewModel.getMyOrders(page);
            }
        };

        binding.ordersRv.addOnScrollListener(recyclerViewScrollListener);
    }

    private void initializeObservables() {
        viewModel.getOrdersObservalble().observe(this, this::publishOrders);
    }

    private void publishOrders(List<OrdersModel> ordersModels) {
        addAll(ordersModels);
    }

    public void add(OrdersModel response) {
        if (!this.ordersModels.contains(response)) {
            this.ordersModels.add(response);
            rcAdapter.notifyItemInserted(this.ordersModels.size() - 1);
        }
    }

    public void addAll(List<OrdersModel>  postItems) {
        if (postItems != null && postItems.size() > 0) {
            for (OrdersModel response : postItems) {
                add(response);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        QurbaApplication.setCurrentActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QurbaApplication.setCurrentActivity(this);
    }
}
