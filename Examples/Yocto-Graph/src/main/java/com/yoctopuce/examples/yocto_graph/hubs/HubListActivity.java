package com.yoctopuce.examples.yocto_graph.hubs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yoctopuce.examples.helpers.YoctopuceBgThread;
import com.yoctopuce.examples.yocto_graph.R;

public class HubListActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.Configuration);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainter);
        if (fragment == null) {
            fragment = HubListFragment.getInstance();
            fm.beginTransaction().add(R.id.fragmentContainter, fragment)
                    .commit();
        }


    }


    @Override
    public void onStart()
    {
        super.onStart();
        YoctopuceBgThread.Start(this);

    }


    @Override
    public void onStop()
    {
        YoctopuceBgThread.Stop();
        super.onStop();
    }

}
