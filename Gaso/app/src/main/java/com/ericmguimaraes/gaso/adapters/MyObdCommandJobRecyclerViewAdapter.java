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

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.fragments.ObdLogFragment;
import com.ericmguimaraes.gaso.model.ObdLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ObdLog} and makes a call to the
 * specified {@link ObdLogFragment.OnObdLogListFragmentInteractionListener}.
 */
public class MyObdCommandJobRecyclerViewAdapter extends RecyclerView.Adapter<MyObdCommandJobRecyclerViewAdapter.ViewHolder> {

    private List<ObdLog> mValues;
    private final ObdLogFragment.OnObdLogListFragmentInteractionListener mListener;

    public MyObdCommandJobRecyclerViewAdapter(@Nullable List<ObdLog> items, ObdLogFragment.OnObdLogListFragmentInteractionListener listener) {
        mValues = items;
        if(mValues==null)
            mValues = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_obdlog_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.log = mValues.get(position);
        holder.name.setText(mValues.get(position).getName());
        holder.state.setText(mValues.get(position).getStatus());
        holder.value.setText(mValues.get(position).getData());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onObdLogListFragmentInteraction(holder.log);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        @Bind(R.id.name)
        TextView name;

        @Bind(R.id.state)
        TextView state;

        @Bind(R.id.value)
        TextView value;

        public ObdLog log;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this,view);
        }

    }

    public void addOrUpdateJob(ObdLog log){
        boolean found = false;
        if(log.getId()!=0)
            for(int i=0;i<mValues.size();i++){
                ObdLog l = mValues.get(i);
                if(l.getId()==log.getId()){
                    mValues.remove(i);
                    if(isLogNotBroke(log))
                        mValues.add(i,log);
                    found = true;
                    break;
                }
            }
        if(!found && isLogNotBroke(log))
            mValues.add(log);
        notifyDataSetChanged();
    }

    private boolean isLogNotBroke(ObdLog log){
        return ((log.getStatus()!=null && !log.getStatus().contains("ERROR")) || log.getStatus()==null)  &&
                ((log.getData()!=null && !log.getData().contains("NO_DATA") || log.getData()==null));
    }

}
