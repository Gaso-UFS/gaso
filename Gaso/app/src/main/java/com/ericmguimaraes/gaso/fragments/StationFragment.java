package com.ericmguimaraes.gaso.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.adapters.MyStationRecyclerViewAdapter;
import com.ericmguimaraes.gaso.model.Station;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StationFragment extends Fragment {

    private static List<Station> stationList;

    private MyStationRecyclerViewAdapter adapter;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.emptyListText)
    TextView emptyListTextView;

    Handler recyclerViewUpdateHandler;

    private boolean isNotificationPendent;

    private static int RECYCLERVIEW_REFRESH_TIME = 2000;

    public StationFragment() {
    }
    public static StationFragment newInstance(List<Station> stationList) {
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        StationFragment.stationList = stationList;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_list, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyStationRecyclerViewAdapter(stationList);
        recyclerView.setAdapter(adapter);

        recyclerViewUpdateHandler = new Handler();
        recyclerViewUpdater.run();

        return view;
    }

    public void setStationList(List<Station> stationList) {
        StationFragment.stationList = stationList;
        isNotificationPendent = true;
    }

    private void notifyDataChange() {
        adapter.setStationList(stationList);
        adapter.notifyDataSetChanged();
        isNotificationPendent = false;
    }

    Runnable recyclerViewUpdater = new Runnable() {
        @Override
        public void run() {
            if(isNotificationPendent)
                notifyDataChange();
            if(((GasFragment) getParentFragment()).isSearching()){
                progressBar.setVisibility(View.VISIBLE);
                emptyListTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            } else if(stationList.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                emptyListTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                emptyListTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            recyclerViewUpdateHandler.postDelayed(recyclerViewUpdater,RECYCLERVIEW_REFRESH_TIME);
        }
    };

}