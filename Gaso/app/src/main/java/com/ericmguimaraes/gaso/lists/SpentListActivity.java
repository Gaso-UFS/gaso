package com.ericmguimaraes.gaso.lists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.adapters.SpentListAdapter;
import com.ericmguimaraes.gaso.config.Config;
import com.ericmguimaraes.gaso.model.Spent;
import com.ericmguimaraes.gaso.persistence.SpentDAO;
import com.ericmguimaraes.gaso.activities.registers.SpentRegisterActivity;

import java.util.List;

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

    int month;

    String[] monthNames = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

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
                if (Config.getInstance().currentCar == null || Config.getInstance().currentUser == null) {
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

        SpentDAO dao = new SpentDAO(getApplicationContext());
        List<Spent> spents = dao.findByMonth(month);
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
        List<Spent> spents = dao.findByMonth(month);
        adapter.resetList(spents);
        toolbar.setTitle(monthNames[month]);
    }

}
