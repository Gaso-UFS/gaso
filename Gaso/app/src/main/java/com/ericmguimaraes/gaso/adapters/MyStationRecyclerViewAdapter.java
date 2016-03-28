package com.ericmguimaraes.gaso.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.StationDetailsActivity;
import com.ericmguimaraes.gaso.maps.LocationHelper;
import com.ericmguimaraes.gaso.model.Location;
import com.ericmguimaraes.gaso.model.Station;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyStationRecyclerViewAdapter extends RecyclerView.Adapter<MyStationRecyclerViewAdapter.ViewHolder> {

    private List<Station> stationList;

    Context context;

    Activity activity;

    public MyStationRecyclerViewAdapter(List<Station> stationList, Activity activity) {
        this.stationList = stationList;
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_station, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = stationList.get(position);
        holder.stationNameText.setText(stationList.get(position).getName());
        holder.ratingBar.setRating(stationList.get(position).getCombustiveRate());
        LocationHelper locationHelper = LocationHelper.getINSTANCE(activity);
        Location userLocation = locationHelper.getLastKnownLocation();
        double distance = 0;
        if(userLocation!=null)
            distance = LocationHelper.distance(userLocation,stationList.get(position).getLocation());
        holder.distanceText.setText(String.format("%1$,.2f", distance)+" metros");
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, StationDetailsActivity.class);
                intent.putExtra("station_id",holder.mItem.getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stationList==null?0:stationList.size();
    }

    public void setStationList(List<Station> stationList) {
        this.stationList = stationList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.stationNameText)
        TextView stationNameText;

        @Bind(R.id.rating)
        RatingBar ratingBar;

        @Bind(R.id.distanceText)
        TextView distanceText;

        public View view;

        public Station mItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

    }
}
