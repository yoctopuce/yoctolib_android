/*
 *
 *  $Id: GettingStarted_Yocto_RangeFinder.java 32625 2018-10-10 13:27:32Z seb $
 *
 *  An example that show how to use a  Yocto-RangeFinder
 *
 *  You can find more information on our web site:
 *   Yocto-RangeFinder documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-rangefinder/doc.html
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
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YRangeFinder;
import com.yoctopuce.YoctoAPI.YLightSensor;
import com.yoctopuce.YoctoAPI.YTemperature;

import java.util.Locale;

public class GettingStarted_Yocto_RangeFinder extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;
    private TextView _rfView;
    private TextView _irView;
    private TextView _tmpView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_rangefinder);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        _rfView = (TextView) findViewById(R.id.rffield);
        _irView = (TextView) findViewById(R.id.irfield);
        _tmpView = (TextView) findViewById(R.id.tmpfield);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
        handler = new Handler();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        try {
            aa.clear();
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YModule module = YModule.FirstModule();
            while (module != null) {
                if (module.get_productName().equals("Yocto-RangeFinder")) {
                    String serial = module.get_serialNumber();
                    aa.add(serial);
                }
                module = module.nextModule();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        aa.notifyDataSetChanged();
        handler.postDelayed(r, 500);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        handler.removeCallbacks(r);
        YAPI.FreeAPI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        serial = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    final Runnable r = new Runnable()
    {
        public void run()
        {
            if (serial != null) {
                YRangeFinder rf = YRangeFinder.FindRangeFinder(serial + ".rangeFinder1");
                YLightSensor ir = YLightSensor.FindLightSensor(serial + ".lightSensor1");
                YTemperature tmp = YTemperature.FindTemperature(serial + ".temperature1");
                try {
                    _rfView.setText(String.format(Locale.US, "%.1f", rf.getCurrentValue()));
                    _irView.setText(String.format(Locale.US, "%.1f", ir.getCurrentValue()));
                    _tmpView.setText(String.format(Locale.US, "%.1f", tmp.getCurrentValue()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}
