/*
 *
 *  $Id: GettingStarted_Yocto_Motor_DC.java 32625 2018-10-10 13:27:32Z seb $
 *
 *  An example that show how to use a  Yocto-Motor-DC
 *
 *  You can find more information on our web site:
 *   Yocto-Motor-DC documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-motor-dc/doc.html
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YCurrent;
import com.yoctopuce.YoctoAPI.YMotor;
import com.yoctopuce.YoctoAPI.YTemperature;
import com.yoctopuce.YoctoAPI.YVoltage;

public class GettingStarted_Yocto_Motor_DC extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private Handler handler;
    private YMotor motor;
    private YCurrent current;
    private YTemperature temperature;
    private YVoltage voltage;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_motor_dc);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
        handler = new Handler();
    }

    public void updateMotor(View view)
    {
        if (motor!=null) {
            EditText control = (EditText)findViewById(R.id.controlfield);
            Integer power = Integer.valueOf(String.valueOf(control.getText()));
            try {
                // if motor is in error state, reset it.
                if (motor.get_motorStatus()>=YMotor.MOTORSTATUS_LOVOLT) {
                    motor.resetStatus();
                }
                motor.drivingForceMove(power, 2000);  // ramp up to power in 2 seconds
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        try {
            aa.clear();
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YMotor m = YMotor.FirstMotor();
            while (m != null) {
                String serial = m.get_module().get_serialNumber();
                aa.add(serial);
                m = m.nextMotor();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        // refresh Spinner with detected relay
        aa.notifyDataSetChanged();
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
        String serial = parent.getItemAtPosition(pos).toString();
        motor       = YMotor.FindMotor(serial + ".motor");
        current     = YCurrent.FindCurrent(serial + ".current");
        voltage     = YVoltage.FindVoltage(serial + ".voltage");
        temperature = YTemperature.FindTemperature(serial + ".temperature");

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    final Runnable r = new Runnable()
    {
        public void run()
        {
            if (motor != null && motor.isOnline()) {
                try {
                    TextView view = (TextView) findViewById(R.id.statefield);
                    view.setText(motor.get_advertisedValue());
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
            }
            if (current != null && current.isOnline()) {
                try {
                    TextView view = (TextView) findViewById(R.id.currentfield);
                    view.setText(String.format("%.1f %s", current.getCurrentValue(), current.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
            }
            if (voltage != null && voltage.isOnline()) {
                try {
                    TextView view = (TextView) findViewById(R.id.votltagefield);
                    view.setText(String.format("%.1f %s", voltage.getCurrentValue(), voltage.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
            }
            if (temperature != null && temperature.isOnline()) {
                try {
                    TextView view = (TextView) findViewById(R.id.tempfield);
                    view.setText(String.format("%.1f %s", temperature.getCurrentValue(), temperature.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };


}
