package com.ericmguimaraes.gaso.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.model.FuelSource;

import java.util.List;

/**
 * Created by adrianodias on 5/7/17.
 */

public class MyFuelSourceRecyclerViewAdapter extends RecyclerView.Adapter<MyFuelSourceRecyclerViewAdapter.ViewHolder> {
    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private List<FuelSource> itemList;

    private Milestone milestone;

    // Constructor of the class
    public MyFuelSourceRecyclerViewAdapter(List<FuelSource> itemList, Milestone milestone) {
        this.itemList = itemList;
        this.milestone = milestone;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stationfuel, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        String nomePosto = itemList.get(listPosition).getStationName();
        String quantidadeL;
        if(milestone.isHasTankMax())
            quantidadeL = String.format("%.2f", itemList.get(listPosition).getValue()*milestone.getTankMax()/100) + "%";
        else
            quantidadeL = String.format("%.2f", itemList.get(listPosition).getValue()) + "%";
        item.setText(nomePosto + ": "+ quantidadeL);
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.stationFuel);
        }
        @Override
        public void onClick(View view) {
        }
    }
}
