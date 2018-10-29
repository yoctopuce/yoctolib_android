/*
 *
 *  $Id: GettingStarted_Yocto_3D.java 32625 2018-10-10 13:27:32Z seb $
 *
 *  An example that show how to use a  Yocto-3D
 *
 *  You can find more information on our web site:
 *   Yocto-3D documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-3d/doc.html
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
import com.yoctopuce.YoctoAPI.YAccelerometer;
import com.yoctopuce.YoctoAPI.YCompass;
import com.yoctopuce.YoctoAPI.YGyro;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YSensor;
import com.yoctopuce.YoctoAPI.YTilt;

public class GettingStarted_Yocto_3D extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_3d);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
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
                if (module.get_productName().equals("Yocto-3D")) {
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
                YSensor tilt1 = YTilt.FindTilt(serial + ".tilt1");
                try {
                    TextView view = (TextView) findViewById(R.id.tilt1field);
                    view.setText(String.format("%.1f %s", tilt1.getCurrentValue(), tilt1.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
                YTilt tilt2 = YTilt.FindTilt(serial + ".tilt2");
                try {
                    TextView view = (TextView) findViewById(R.id.tilt2field);
                    view.setText(String.format("%.1f %s", tilt2.getCurrentValue(), tilt2.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
                YCompass compass = YCompass.FindCompass(serial + ".compass");
                try {
                    TextView view = (TextView) findViewById(R.id.compassfield);
                    view.setText(String.format("%.1f %s", compass.getCurrentValue(), compass.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
                YAccelerometer accelerometer = YAccelerometer.FindAccelerometer(serial + ".accelerometer");
                try {
                    TextView view = (TextView) findViewById(R.id.accelfield);
                    view.setText(String.format("%.1f %s", accelerometer.getCurrentValue(), accelerometer.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
                YGyro gyro = YGyro.FindGyro(serial + ".gyro");
                try {
                    TextView view = (TextView) findViewById(R.id.gyrofield);
                    view.setText(String.format("%.1f %s", gyro.getCurrentValue(), gyro.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 200);
        }
    };

}
