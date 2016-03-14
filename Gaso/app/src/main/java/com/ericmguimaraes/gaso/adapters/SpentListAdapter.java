package com.ericmguimaraes.gaso.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.model.Spent;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.persistence.SpentDAO;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ericm on 2/28/2016.
 */
public class SpentListAdapter extends RecyclerView.Adapter<SpentListAdapter.ViewHolder> {

    List<Spent> spentList;
    private Spent lastRemoved;
    private Context context;
    RecyclerView recyclerView;

    public SpentListAdapter(List<Spent> spentList, RecyclerView view, Context context) {
        this.spentList = spentList;
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
        Spent spent = spentList.get(position);
        holder.amountText.setText(Double.toString(spent.getAmount())+"L");
        Car car = spent.getCar();
        holder.carText.setText(car==null?"Esse carro foi excluido.":car.getModel());
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy hh:mm");
        holder.dateText.setText(formater.format(spent.getDate()));
        holder.typeText.setText(Integer.toString(spent.getType()));
        User user = spent.getUser();
        holder.userText.setText(user==null?"Esse usuario foi excluido.":user.getName());
        holder.valueText.setText("R$"+Double.toString(spent.getTotal()));
        //holder.stationText.setText(spent.getStation().toString());
    }

    @Override
    public int getItemCount() {
        return spentList.size();
    }

    public void remove(final int posistion){
        try {
            lastRemoved = spentList.get(posistion);

            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Gasto removido com sucesso.", Snackbar.LENGTH_LONG)
                    .setAction("Desfazer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SpentDAO dao = new SpentDAO(context);
                            dao.add(lastRemoved);
                            spentList.add(posistion, lastRemoved);
                            notifyItemInserted(posistion);
                        }
                    });
            snackbar.show();

            spentList.remove(lastRemoved);

            SpentDAO dao = new SpentDAO(context);
            dao.remove(lastRemoved);

            notifyDataSetChanged();
        } catch (Exception e){
            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Ops, tivemos um pequeno problema.", Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("remove",e.getMessage(),e);
        }
    }

    public void resetList(List<Spent> spents) {
        this.spentList = spents;
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
            Spent c = spentList.get(itemPosition);
            Intent intent = new Intent(context, SpentDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*/
        }
    }

}
