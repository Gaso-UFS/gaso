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

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.CombustiveType;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.persistence.ExpensesDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ericm on 2/28/2016.
 */
public class ExpensesListAdapter extends RecyclerView.Adapter<ExpensesListAdapter.ViewHolder> {

    List<Expense> expenseList;
    private Expense lastRemoved;
    private Context context;
    RecyclerView recyclerView;

    public ExpensesListAdapter(List<Expense> expenseList, RecyclerView view, Context context) {
        this.expenseList = expenseList;
        if(this.expenseList==null)
            this.expenseList = new ArrayList<>();
        this.context = context;
        recyclerView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spent_item_recyclerview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.amountText.setText(Double.toString(expense.getAmount())+"L");
        Car car = expense.getCar();
        holder.carText.setText(car==null?"Carro não encontrado.":car.getModel());
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy hh:mm");
        holder.dateText.setText(formater.format(expense.getDate()));
        holder.typeText.setText(CombustiveType.fromInteger(expense.getType()).toString());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.userText.setText(user==null?"Usuário não encontrado.":user.getDisplayName());
        holder.valueText.setText("R$"+Double.toString(expense.getTotal()));
        holder.stationText.setText(expense.getStationName());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void remove(final int posistion){
        try {
            lastRemoved = expenseList.get(posistion);

            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Gasto removido com sucesso.", Snackbar.LENGTH_LONG)
                    .setAction("Desfazer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ExpensesDAO dao = new ExpensesDAO();
                            dao.add(lastRemoved);
                            expenseList.add(posistion, lastRemoved);
                            notifyItemInserted(posistion);
                        }
                    });
            snackbar.show();

            expenseList.remove(lastRemoved);

            ExpensesDAO dao = new ExpensesDAO();
            dao.remove(lastRemoved);

            notifyDataSetChanged();
        } catch (Exception e){
            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Ops, tivemos um pequeno problema.", Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("remove",e.getMessage(),e);
        }
    }

    public void resetList(List<Expense> expenses) {
        this.expenseList = expenses;
        if(this.expenseList==null)
            this.expenseList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder  implements View.OnClickListener {

        @Bind(R.id.dateText)
        TextView dateText;
        @Bind(R.id.typeText)
        TextView typeText;
        @Bind(R.id.userText)
        TextView userText;
        @Bind(R.id.amountText)
        TextView amountText;
        @Bind(R.id.valueText)
        TextView valueText;
        @Bind(R.id.carText)
        TextView carText;
        @Bind(R.id.stationText)
        TextView stationText;

        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*int itemPosition = recyclerView.getChildAdapterPosition(v);
            Expense c = expenseList.get(itemPosition);
            Intent intent = new Intent(context, SpentDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*/
        }
    }

}
