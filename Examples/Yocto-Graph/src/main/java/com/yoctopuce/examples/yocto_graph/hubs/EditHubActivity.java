package com.yoctopuce.examples.yocto_graph.hubs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.yoctopuce.examples.helpers.YoctopuceBgThread;
import com.yoctopuce.examples.yocto_graph.R;

import java.util.UUID;

public class EditHubActivity extends FragmentActivity
{


    private static final String ARG_HUB_UUID = "HUB_UUID";
    private UUID _hubUUID;

    public static Intent intentWithParams(Context context, UUID hubUUID)
    {
        Intent intent = new Intent(context, EditHubActivity.class);
        intent.putExtra(ARG_HUB_UUID, hubUUID.toString());
        return intent;
    }

    public static Intent intentWithParams(Context context)
    {
        return new Intent(context, EditHubActivity.class);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainter);
        if (fragment == null) {
            fragment = EditHubFragment.getFragment();
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_hub_list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.new_hub) {
            Intent detailIntent = new Intent(this, EditHubActivity.class);
            startActivity(detailIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
