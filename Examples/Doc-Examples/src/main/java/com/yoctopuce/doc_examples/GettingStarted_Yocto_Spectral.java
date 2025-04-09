/*
 *
 *  $Id: svn_id $
 *
 *  An example that shows how to use a  Yocto-Spectral
 *
 *  You can find more information on our web site:
 *   Yocto-Spectral documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-spectral/doc.html
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
import com.yoctopuce.YoctoAPI.YColorSensor;

import java.util.Locale;

public class GettingStarted_Yocto_Spectral extends Activity implements OnItemSelectedListener
{

    private YColorSensor _colorSensor = null;
    private ArrayAdapter<String> aa;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_spectral);
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
        aa.clear();
        try {
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YColorSensor s = YColorSensor.FirstColorSensor();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextColorSensor();
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
        String hwid = (String) parent.getItemAtPosition(pos);
        _colorSensor = YColorSensor.FindColorSensor(hwid);
        try {
            _colorSensor.set_workingMode(YColorSensor.WORKINGMODE_AUTO); // Set Working Mode Auto
            _colorSensor.set_estimationModel(YColorSensor.ESTIMATIONMODEL_REFLECTION); // Set Prediction Model Reflexion
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    final Runnable r = new Runnable()
    {
        public void run()
        {

            if (_colorSensor != null) {
                try {

                    String simple = _colorSensor.get_nearSimpleColor();
                    int estimated = _colorSensor.get_estimatedRGB();
                    TextView view = (TextView) findViewById(R.id.simple);
                    view.setText(simple);
                    TextView view2 = (TextView) findViewById(R.id.estimated);
                    view2.setText(String.format(Locale.US, "#%x", estimated));

                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

}
