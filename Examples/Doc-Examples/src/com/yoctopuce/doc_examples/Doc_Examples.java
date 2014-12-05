package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;

public class Doc_Examples extends Activity
{


    private static final String TAG = "Doc_Examples";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doc_examples);
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new ExampleListFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_doc_examples, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showAbout()
    {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        Resources resources = getResources();
        // set application message
        String app_name = resources.getString(R.string.app_name);
        TextView textView = (TextView) messageView.findViewById(R.id.about_message);
        String format = resources.getString(R.string.about_intro);
        textView.setText(String.format(format, app_name));
        // set

        String version = "0";
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textView = (TextView) messageView.findViewById(R.id.app_version);
        textView.setText(version);

        textView = (TextView) messageView.findViewById(R.id.yapi_version);
        textView.setText(YAPI.GetAPIVersion());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }


}
