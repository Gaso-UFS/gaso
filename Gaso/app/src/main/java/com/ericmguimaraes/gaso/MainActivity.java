package com.ericmguimaraes.gaso;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ericmguimaraes.gaso.adapters.ViewPagerAdapter;
import com.ericmguimaraes.gaso.fragments.GasFragment;
import com.ericmguimaraes.gaso.fragments.MyCarFragment;
import com.ericmguimaraes.gaso.fragments.SpentFragment;
import com.ericmguimaraes.gaso.lists.CarListActivity;
import com.ericmguimaraes.gaso.lists.UserListActivity;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.persistence.CarDAO;
import com.ericmguimaraes.gaso.persistence.UserDAO;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GasFragment.OnFragmentInteractionListener, MyCarFragment.OnFragmentInteractionListener, SpentFragment.OnFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @BindString(R.string.gas)
    String gas;

    @BindString(R.string.spent)
    String spent;

    @BindString(R.string.my_car)
    String myCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);

        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.user_list_menu_item).getIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_settings:
            return true;
            case R.id.user_list_menu_item:
                intent = new Intent(this, UserListActivity.class);
                startActivity(intent);
            return true;
            case R.id.car_list_menu_item:
                intent = new Intent(this, CarListActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GasFragment(), gas);
        adapter.addFragment(new MyCarFragment(), myCar);
        adapter.addFragment(new SpentFragment(), spent);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private void init(){
        UserDAO userDAO = new UserDAO(getApplicationContext());
        CarDAO carDAO = new CarDAO(getApplicationContext());
        if(Config.getInstance().currentUser==null)
            Config.getInstance().currentUser = userDAO.findFirst();
        if(Config.getInstance().currentCar==null)
            Config.getInstance().currentCar = carDAO.findFirst();
    }
}
