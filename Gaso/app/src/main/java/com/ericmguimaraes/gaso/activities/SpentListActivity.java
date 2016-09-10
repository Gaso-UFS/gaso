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

package com.ericmguimaraes.gaso.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.adapters.SpentListAdapter;
import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.maps.LocationHelper;
import com.ericmguimaraes.gaso.model.CombustiveType;
import com.ericmguimaraes.gaso.model.Spent;
import com.ericmguimaraes.gaso.persistence.SpentDAO;
import com.ericmguimaraes.gaso.activities.registers.SpentRegisterActivity;
import com.ericmguimaraes.gaso.util.CSVHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SpentListActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.spent_recycler_view)
    RecyclerView recyclerView;

    SpentListAdapter adapter;

    Date monthAndYear;

    String[] monthNames = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private List<Spent> spents;
    private int month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spent_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SessionSingleton.getInstance().currentCar == null || SessionSingleton.getInstance().getCurrentUser(getApplicationContext()) == null) {
                    Context context = getApplicationContext();
                    CharSequence text = "Porfavor, primeiro cadastre e selecione um carro e um usuario.";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), SpentRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });

        month = getIntent().getExtras().getInt("month");
        int year = getIntent().getExtras().getInt("year");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.YEAR,year);
        monthAndYear = cal.getTime();

        SpentDAO dao = new SpentDAO(getApplicationContext());
        spents = dao.findByMonthAndYear(monthAndYear,SessionSingleton.getInstance().getCurrentUser(getApplicationContext()));
        adapter = new SpentListAdapter(spents, recyclerView, getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                adapter.remove(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        SpentDAO dao = new SpentDAO(getApplicationContext());
        spents = dao.findByMonthAndYear(monthAndYear,SessionSingleton.getInstance().getCurrentUser(getApplicationContext()));
        adapter.resetList(spents);
        toolbar.setTitle(monthNames[month]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_month, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_export:
                createCSVFile();
                break;
            default:
                break;
        }
        return true;
    }

    private void createCSVFile() {
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        for(Spent s:spents)
            data.add(new String[]{
                    Integer.toString(s.getId()),
                    s.getCar().getModel(),
                    CombustiveType.fromInteger(s.getType()).toString(),
                    s.getStation().getName(),
                    format.format(s.getDate()),
                    Double.toString(s.getAmount())+"L",
                    Double.toString(s.getTotal())});

        format = new SimpleDateFormat("MM_yyyy", Locale.FRANCE);

        String msg = "";

        String fileName = "gaso_gastos_"+format.format(monthAndYear)+".csv";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.WRITE_MONTH_SPENT);
            Log.e("writing spents", "NO PERMISSION");
            return;
        }
        try {
            CSVHelper.createCSV(fileName,data);
            msg = "Arquivo "+fileName+" foi salvo com sucesso.";
        } catch (IOException e) {
            Log.e("SAVING_FILE",e.getMessage(),e);
            msg = "Desculpe, tivemos um problema exportando o arquivo.";
        }
        Snackbar.make(recyclerView,msg,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.WRITE_MONTH_SPENT: {
                if (grantResults .length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                    createCSVFile();
                } else {
                    Snackbar.make(recyclerView,"Não é possivel exportar os dados sem permissão.",Snackbar.LENGTH_LONG).show();
                }
                LocationHelper.isLocationPermissionAsked = true;
            }
        }
    }

}
