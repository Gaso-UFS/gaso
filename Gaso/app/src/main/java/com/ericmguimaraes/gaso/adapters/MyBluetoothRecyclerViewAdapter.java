/*
 *     Gaso
 *
 *     Copyright (C) 2016  Eric Guimarães
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ericmguimaraes.gaso.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.fragments.BluetoothFragment.OnBluetoothDeviceListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BluetoothDevice} and makes a call to the
 * specified {@link OnBluetoothDeviceListFragmentInteractionListener}.
 */
public class MyBluetoothRecyclerViewAdapter extends RecyclerView.Adapter<MyBluetoothRecyclerViewAdapter.ViewHolder> {

    private List<BluetoothDevice> mValues;
    private final OnBluetoothDeviceListFragmentInteractionListener mListener;
    private HashMap<String,BluetoothDevice> devicesHashMap;

    public MyBluetoothRecyclerViewAdapter(List<BluetoothDevice> items, OnBluetoothDeviceListFragmentInteractionListener listener) {
        mValues = items;
        if(items==null)
            mValues = new ArrayList<>();
        mListener = listener;
        devicesHashMap = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bluetooth, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String name = mValues.get(position).getName();
        holder.mNameView.setText(name);
        holder.mAddressView.setText(mValues.get(position).getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onBluetoothDeviceListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues==null?0:mValues.size();
    }

    public void addList(List<BluetoothDevice> devices) {
        for (BluetoothDevice device:devices)
            add(device);
        notifyDataSetChanged();
    }

    public void add(BluetoothDevice device) {
        if(!devicesHashMap.containsKey(device.getAddress()))
            mValues.add(device);
        devicesHashMap.put(device.getAddress(),device);
        Log.d("BLUE",device.getName()+":"+device.getAddress());
        notifyDataSetChanged();
    }

    public void resetData() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mAddressView;
        public BluetoothDevice mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameText);
            mAddressView = (TextView) view.findViewById(R.id.addressText);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAddressView.getText() + "'";
        }
    }
}
