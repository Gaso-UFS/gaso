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

package com.ericmguimaraes.gaso.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.activities.LoginActivity;
import com.ericmguimaraes.gaso.activities.MainActivity;
import com.ericmguimaraes.gaso.activities.PlainTextActivity;
import com.ericmguimaraes.gaso.activities.registers.ExpensesRegisterActivity;
import com.ericmguimaraes.gaso.adapters.MyMonthlyExpensesRecyclerViewAdapter;
import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.config.SessionSingleton;
import com.ericmguimaraes.gaso.model.Expense;
import com.ericmguimaraes.gaso.model.MonthSpent;
import com.ericmguimaraes.gaso.persistence.ExpensesDAO;
import com.ericmguimaraes.gaso.util.ExpenseComparator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MonthlyExpensesFragment extends Fragment {

    private List<MonthSpent> monthSpentList;

    MyMonthlyExpensesRecyclerViewAdapter adapter;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.list)
    RecyclerView recyclerView;

    public MonthlyExpensesFragment() {
    }

    public static MonthlyExpensesFragment newInstance() {
        MonthlyExpensesFragment fragment = new MonthlyExpensesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    private void populateMonthList() {
        ExpensesDAO dao = new ExpensesDAO();
        dao.findAll(new ExpensesDAO.OnExpensesReceivedListener() {
            @Override
            public void OnExpensesReceived(List<Expense> expenses) {
                monthSpentList = new ArrayList<>();
                Calendar month=null;
                double value=-1;
                Collections.sort(expenses,new ExpenseComparator());
                MonthSpent monthSpentToSave=null;
                for(Expense s: expenses){
                    Calendar expenseDate = Calendar.getInstance();
                    expenseDate.setTimeInMillis(s.getDate());
                    if(month==null || month.get(Calendar.MONTH)!=expenseDate.get(Calendar.MONTH) || month.get(Calendar.YEAR)!=expenseDate.get(Calendar.YEAR)){
                        if(monthSpentToSave!=null)
                            monthSpentList.add(monthSpentToSave);
                        monthSpentToSave = new MonthSpent();
                        month = expenseDate;
                        value = s.getTotal();
                        monthSpentToSave.setMonth(month.getTime());
                        monthSpentToSave.setValue(value);
                    } else {
                        value += s.getTotal();
                        monthSpentToSave.setValue(value);
                    }
                }
                if(monthSpentToSave!=null)
                    monthSpentList.add(monthSpentToSave);
                adapter.resetList(monthSpentList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showGenericError();
            }
        });
    }

    private void showGenericError() {
        if (isAdded()) {
            Snackbar.make(recyclerView,Constants.genericError,Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthlyexpenses_list, container, false);
        ButterKnife.bind(this, view);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        populateMonthList();
        adapter = new MyMonthlyExpensesRecyclerViewAdapter(monthSpentList, getActivity(), recyclerView);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SessionSingleton.getInstance().currentCar == null || SessionSingleton.getInstance().getCurrentUser(getContext()) == null) {
                    Context context = getContext();
                    CharSequence text = "Porfavor, primeiro cadastre e selecione um carro e um usuario.";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    Intent intent = new Intent(getContext(), ExpensesRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_spent, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_export:
                ((MainActivity)getActivity()).createCSVFile();
                return true;
            case R.id.action_help:
                Intent intentHelp = new Intent(getActivity(), PlainTextActivity.class);
                intentHelp.putExtra(PlainTextActivity.EXTRA_TITLE, R.string.help_title);
                intentHelp.putExtra(PlainTextActivity.EXTRA_TEXT, R.string.help_text);
                startActivity(intentHelp);
                return true;
            case R.id.action_disclaimer:
                Intent intentDisclaimer = new Intent(getActivity(), PlainTextActivity.class);
                intentDisclaimer.putExtra(PlainTextActivity.EXTRA_TITLE, R.string.disclaimer_title);
                intentDisclaimer.putExtra(PlainTextActivity.EXTRA_TEXT, R.string.disclaimer_text);
                startActivity(intentDisclaimer);
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                forgetLoggedUser();
                intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateMonthList();
        adapter.resetList(monthSpentList);
    }

    private void forgetLoggedUser() {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.USER_LOGGED_TAG, "");
        editor.apply();
    }



}
