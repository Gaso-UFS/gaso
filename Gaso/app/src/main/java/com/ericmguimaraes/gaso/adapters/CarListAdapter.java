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
import android.widget.Toast;

import com.ericmguimaraes.gaso.config.Config;
import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.persistence.CarDAO;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ericm on 2/28/2016.
 */
public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.ViewHolder> {

    List<Car> carList;
    private Car lastRemoved;
    private Context context;
    RecyclerView recyclerView;

    public CarListAdapter(List<Car> carList, RecyclerView view, Context context) {
        this.carList = carList;
        this.context = context;
        recyclerView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_item_recyclerview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            Car car = carList.get(position);
            holder.model.setText(car.getModel());
            holder.description.setText(car.getDescription());
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void remove(final int posistion){
        try {
            lastRemoved = carList.get(posistion);

            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Carro " + lastRemoved.getModel() + " removido com sucesso.", Snackbar.LENGTH_LONG)
                    .setAction("Desfazer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CarDAO dao = new CarDAO(context);
                            dao.add(lastRemoved);
                            carList.add(posistion, lastRemoved);
                            notifyItemInserted(posistion);
                        }
                    });
            snackbar.show();

            carList.remove(lastRemoved);

            CarDAO dao = new CarDAO(context);
            dao.remove(lastRemoved);

            if(carList.isEmpty())
                Config.getInstance().currentCar = null;
            else
                Config.getInstance().currentCar = carList.get(0);

            notifyDataSetChanged();
        } catch (Exception e){
            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Ops, tivemos um pequeno problema.", Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("remove",e.getMessage(),e);
        }
    }

    public class ViewHolder  extends RecyclerView.ViewHolder  implements View.OnClickListener {

        @Bind(R.id.model)
        TextView model;
        @Bind(R.id.description)
        TextView description;

        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = recyclerView.getChildAdapterPosition(v);
            Car c = carList.get(itemPosition);
            Toast.makeText(context, "Carro "+c.getModel()+" selecionado com sucesso.", Toast.LENGTH_LONG).show();
            Config config = Config.getInstance();
            config.currentCar = c;
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
