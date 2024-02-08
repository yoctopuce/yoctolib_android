/*
 *
 *  $Id: GettingStarted_Yocto_Proximity.java 58172 2023-11-30 17:10:23Z martinm $
 *
 *  An example that shows how to use a  Yocto-Proximity
 *
 *  You can find more information on our web site:
 *   Yocto-Proximity documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-proximity/doc.html
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
import com.yoctopuce.YoctoAPI.YLightSensor;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YProximity;

import java.util.Locale;

public class GettingStarted_Yocto_Proximity extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;
    private TextView _irView;
    private TextView _lightView;
    private TextView _proxmitiyView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_proximity);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        _proxmitiyView = (TextView) findViewById(R.id.proximityfield);
        _lightView = (TextView) findViewById(R.id.lightfield);
        _irView = (TextView) findViewById(R.id.irtfield);
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
                if (module.get_productName().equals("Yocto-Proximity")) {
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
                YProximity proximity = YProximity.FindProximity(serial + ".proximity1");
                YLightSensor light_sensor = YLightSensor.FindLightSensor(serial + ".lightSensor1");
                YLightSensor ir_sensor = YLightSensor.FindLightSensor(serial + ".lightSensor2");
                try {
                    _proxmitiyView.setText(String.format(Locale.US, "%.1f", proximity.getCurrentValue()));
                    _lightView.setText(String.format(Locale.US, "%.1f", light_sensor.getCurrentValue()));
                    _irView.setText(String.format(Locale.US, "%.1f", ir_sensor.getCurrentValue()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}
