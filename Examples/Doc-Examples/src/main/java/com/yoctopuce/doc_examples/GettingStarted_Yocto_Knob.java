/*
 *
 *  $Id: GettingStarted_Yocto_Knob.java 58172 2023-11-30 17:10:23Z martinm $
 *
 *  An example that shows how to use a  Yocto-Knob
 *
 *  You can find more information on our web site:
 *   Yocto-Knob documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-knob/doc.html
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
import com.yoctopuce.YoctoAPI.YAnButton;
import com.yoctopuce.YoctoAPI.YModule;

public class GettingStarted_Yocto_Knob extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_knob);
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
                if (module.get_productName().equals("Yocto-Knob")) {
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
                YAnButton input1 = YAnButton.FindAnButton(serial + ".anButton1");
                try {
                    TextView view = (TextView) findViewById(R.id.pressedfield1);
                    if (input1.get_isPressed() == YAnButton.ISPRESSED_TRUE) {
                        view.setText("pressed");
                    } else {
                        view.setText("not pressed");
                    }
                    view = (TextView) findViewById(R.id.valuefield1);
                    view.setText(String.valueOf(input1.get_calibratedValue()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
                YAnButton input5 = YAnButton.FindAnButton(serial + ".anButton5");
                try {
                    TextView view = (TextView) findViewById(R.id.pressedfield5);
                    if (input5.get_isPressed() == YAnButton.ISPRESSED_TRUE) {
                        view.setText("pressed");
                    } else {
                        view.setText("not pressed");
                    }
                    view = (TextView) findViewById(R.id.valuefield5);
                    view.setText(String.valueOf(input5.get_calibratedValue()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}
