/*
 *
 *  $Id: svn_id $
 *
 *  An example that shows how to use a  Yocto-MaxiMicroVolt-Rx
 *
 *  You can find more information on our web site:
 *   Yocto-MaxiMicroVolt-Rx documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-maximicrovolt-rx/doc.html
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
import com.yoctopuce.YoctoAPI.YGenericSensor;
import com.yoctopuce.YoctoAPI.YModule;

public class GettingStarted_Yocto_MaxiMicroVolt_Rx extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;
    private TextView mChannel1Field,mChannel2Field;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_maximicrovolt_rx);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
        handler = new Handler();
        mChannel1Field = (TextView) findViewById(R.id.channel1field);
        mChannel2Field = (TextView) findViewById(R.id.channel2field);

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
                if (module.get_productName().equals("Yocto-0-10V-Rx")) {
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
                YGenericSensor sensor1 = YGenericSensor.FindGenericSensor(serial + ".genericSensor1");
                try {
                    mChannel1Field.setText(String.format("%.1f %s", sensor1.getCurrentValue(), sensor1.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
                YGenericSensor sensor2 = YGenericSensor.FindGenericSensor(serial + ".genericSensor2");
                try {
                    mChannel2Field.setText(String.format("%.1f %s", sensor2.getCurrentValue(), sensor2.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}
