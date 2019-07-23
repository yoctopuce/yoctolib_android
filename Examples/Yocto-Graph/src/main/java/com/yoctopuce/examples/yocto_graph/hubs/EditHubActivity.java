package com.yoctopuce.examples.yocto_graph.hubs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.yoctopuce.examples.helpers.Hub;
import com.yoctopuce.examples.helpers.YoctopuceBgThread;
import com.yoctopuce.examples.yocto_graph.R;

import java.util.UUID;

public class EditHubActivity extends AppCompatActivity
{


    private static final String ARG_HUB_UUID = "HUB_UUID";
    private UUID _hubUUID;
    private EditHubFragment _fragment;

    public static Intent intentWithParams(Context context, Hub hub)
    {
        Intent intent = new Intent(context, EditHubActivity.class);
        intent.putExtra(ARG_HUB_UUID, hub.getUuid().toString());
        return intent;
    }

    public static Intent intentWithParams(Context context)
    {
        return new Intent(context, EditHubActivity.class);
    }


    @Override
    public void onBackPressed()
    {
        if (_fragment.checkBackAllowed()) {
            super.onBackPressed();
        }
    }

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
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        FragmentManager fm = getSupportFragmentManager();
        _fragment = (EditHubFragment) fm.findFragmentById(R.id.fragmentContainter);
        if (_fragment == null) {

            Intent intent = getIntent();
            String uuid_str = intent.getStringExtra(ARG_HUB_UUID);

            if (uuid_str != null) {
                _hubUUID = UUID.fromString(uuid_str);
                if (actionBar != null) {
                    actionBar.setTitle(R.string.edit_hub);
                }
                _fragment = EditHubFragment.getFragment(_hubUUID);
            } else {
                if (actionBar != null) {
                    actionBar.setTitle(R.string.new_hub);
                }
                _fragment = EditHubFragment.getFragment();
            }
            fm.beginTransaction().add(R.id.fragmentContainter, _fragment)
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
