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
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.model.Car;
import com.ericmguimaraes.gaso.persistence.CarDAO;

import java.util.ArrayList;
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
        if(carList==null)
            carList = new ArrayList<>();
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
                            CarDAO dao = new CarDAO();
                            dao.addOrUpdate(lastRemoved);
                            carList.add(posistion, lastRemoved);
                            notifyItemInserted(posistion);
                        }
                    });
            snackbar.show();

            carList.remove(lastRemoved);

            CarDAO dao = new CarDAO();
            dao.remove(lastRemoved);

            if(carList.isEmpty())
                SessionSingleton.getInstance().currentCar = null;
            else
                SessionSingleton.getInstance().currentCar = carList.get(carList.size()-1);

            notifyDataSetChanged();
        } catch (Exception e){
            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Ops, tivemos um pequeno problema.", Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("remove",e.getMessage(),e);
        }
    }

    public void add(Car c) {
        carList.add(c);
        notifyDataSetChanged();
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

            CarDAO dao = new CarDAO();
            dao.setFavoriteCar(c);

            Toast.makeText(context, "Carro "+c.getModel()+" selecionado com sucesso.", Toast.LENGTH_LONG).show();
            SessionSingleton sessionSingleton = SessionSingleton.getInstance();
            sessionSingleton.currentCar = c;
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
