/*
 *
 *  $Id: GettingStarted_Yocto_Meteo.java 32625 2018-10-10 13:27:32Z seb $
 *
 *  An example that show how to use a  Yocto-Meteo
 *
 *  You can find more information on our web site:
 *   Yocto-Meteo documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-meteo/doc.html
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
import com.yoctopuce.YoctoAPI.YHumidity;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YPressure;
import com.yoctopuce.YoctoAPI.YTemperature;

public class GettingStarted_Yocto_Meteo extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_meteo);
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
                if (module.get_productName().equals("Yocto-Meteo")) {
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
                YTemperature temp_sensor = YTemperature.FindTemperature(serial);
                try {
                    TextView view = (TextView) findViewById(R.id.tempfield);
                    view.setText(String.format("%.1f %s", temp_sensor.getCurrentValue(), temp_sensor.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
                YHumidity hum_sensor = YHumidity.FindHumidity(serial);
                try {
                    TextView view = (TextView) findViewById(R.id.humfield);
                    view.setText(String.format("%.1f %s", hum_sensor.getCurrentValue(), hum_sensor.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
                YPressure pres_sensor = YPressure.FindPressure(serial);
                try {
                    TextView view = (TextView) findViewById(R.id.presfield);
                    view.setText(String.format("%.1f %s", pres_sensor.getCurrentValue(), pres_sensor.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
            }
            handler.postDelayed(this, 1000);
        }
    };

}
