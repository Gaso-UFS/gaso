package com.ericmguimaraes.gaso.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.adapters.MyStationRecyclerViewAdapter;
import com.ericmguimaraes.gaso.fragments.dummy.DummyContent;
import com.ericmguimaraes.gaso.fragments.dummy.DummyContent.DummyItem;
import com.ericmguimaraes.gaso.model.Station;

import java.util.List;

public class StationFragment extends Fragment {

    private static List<Station> stationList;

    private MyStationRecyclerViewAdapter adapter;

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

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyStationRecyclerViewAdapter(DummyContent.ITEMS);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public void setStationList(List<Station> stationList) {
        StationFragment.stationList = stationList;
        notifyDataChange();
    }

    private void notifyDataChange() {
        adapter.setStationList(stationList);
        adapter.notifyDataSetChanged();
    }
}