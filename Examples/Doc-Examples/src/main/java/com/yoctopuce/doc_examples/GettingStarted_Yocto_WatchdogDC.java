/*
 *
 *  $Id: GettingStarted_Yocto_WatchdogDC.java 58172 2023-11-30 17:10:23Z martinm $
 *
 *  An example that shows how to use a  Yocto-WatchdogDC
 *
 *  You can find more information on our web site:
 *   Yocto-WatchdogDC documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-watchdogdc/doc.html
 *   Android API Reference:
 *      https://www.yoctopuce.com/EN/doc/reference/yoctolib-android-EN.html
 *
 */

package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YWatchdog;

public class GettingStarted_Yocto_WatchdogDC extends Activity implements OnItemSelectedListener
{

    private YWatchdog watchdog = null;
    private ArrayAdapter<String> aa;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_watchdogdc);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        aa.clear();
        try {
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YWatchdog s = YWatchdog.FirstWatchdog();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextWatchdog();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        aa.notifyDataSetChanged();
        handler = new Handler();
        handler.postDelayed(r, 500);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        YAPI.FreeAPI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        String hwid = (String) parent.getItemAtPosition(pos);
        watchdog = YWatchdog.FindWatchdog(hwid);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    /** Called when the user touches the button start */
    public void startWatchdog(View view)
    {
        if (watchdog == null)
            return;

        try {
            watchdog.set_state(YWatchdog.RUNNING_ON);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }

    }

    /** Called when the user touches the button start */
    public void resetWatchdog(View view)
    {
        if (watchdog == null)
            return;

        try {
            watchdog.resetWatchdog();
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }

    }

    final Runnable r = new Runnable()
    {
        public void run()
        {
            if (watchdog != null) {
                try {
                    TextView view = (TextView) findViewById(R.id.countdown);
                    long countdown = watchdog.get_countdown();
                    view.setText(String.format("%d", countdown));
                    if (countdown>0)
                        view.setTextColor(0xff00ff00);
                    else
                        view.setTextColor(0xffff0000);

                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 500);
        }
    };

}
