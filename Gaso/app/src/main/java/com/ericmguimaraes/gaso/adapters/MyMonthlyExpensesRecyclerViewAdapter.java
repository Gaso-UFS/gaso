package com.ericmguimaraes.gaso.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.Config;
import com.ericmguimaraes.gaso.lists.SpentListActivity;
import com.ericmguimaraes.gaso.model.MonthSpent;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyMonthlyExpensesRecyclerViewAdapter extends RecyclerView.Adapter<MyMonthlyExpensesRecyclerViewAdapter.ViewHolder> {

    private List<MonthSpent> monthSpentList;

    Activity activity;
    RecyclerView recyclerView;

    String[] monthNames = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

    public MyMonthlyExpensesRecyclerViewAdapter(List<MonthSpent> items, Activity activity, RecyclerView recyclerView) {
        monthSpentList = items;
        this.activity = activity;
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.month_item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = monthSpentList.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTime(monthSpentList.get(position).getMonth());
        holder.monthText.setText(monthNames[cal.get(Calendar.MONTH)].subSequence(0,3));
        holder.valueText.setText(Double.toString(monthSpentList.get(position).getValue()));
    }

    @Override
    public int getItemCount() {
        return monthSpentList.size();
    }

    public void resetList(List<MonthSpent> monthSpentList) {
        this.monthSpentList = monthSpentList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View view;

        @Bind(R.id.monthText)
        TextView monthText;

        @Bind(R.id.valueText)
        TextView valueText;

        public MonthSpent mItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                int itemPosition = recyclerView.getChildAdapterPosition(v);
                Intent intent = new Intent(activity, SpentListActivity.class);
                Calendar car = Calendar.getInstance();
                car.setTime(monthSpentList.get(itemPosition).getMonth());
                intent.putExtra("month", car.get(Calendar.MONTH));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.getApplicationContext().startActivity(intent);
        }
    }
}
