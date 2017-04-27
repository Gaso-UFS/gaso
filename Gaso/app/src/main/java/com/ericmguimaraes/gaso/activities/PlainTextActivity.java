package com.ericmguimaraes.gaso.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlainTextActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TEXT = "extra_text";

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plain_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Gaso");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if(!getIntent().hasExtra(EXTRA_TITLE) || !getIntent().hasExtra(EXTRA_TEXT)) {
            onBackPressed();
            return;
        }

        title.setText(getResources().getString(getIntent().getExtras().getInt(EXTRA_TITLE)));
        text.setText(getResources().getString(getIntent().getExtras().getInt(EXTRA_TEXT)));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
