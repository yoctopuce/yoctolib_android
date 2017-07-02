package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;


public class USBDevicePluggedActivity extends Activity
{

    private static final String TAG = "USBDevicePluggedActivity";

    private static Class __activityToStart = Doc_Examples.class;


    public static void SetActivity(Class activity)
    {
        __activityToStart = activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // Get the intent that started this activity
        Intent intent = getIntent();
        if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
            if (__activityToStart !=null) {
                Log.i("APP", "start form device plug ");
                Intent new_intent = new Intent(this, __activityToStart);
                new_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                new_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(new_intent);
            } else {
                Log.i("APP", "Ignore ACTION_USB_DEVICE_ATTACHED");
            }
            finish();
        } else {
            Log.i("APP", "This should never append");
        }
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        Log.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart()
    {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        Log.i(TAG, "onStop");
        super.onDestroy();
    }


}
