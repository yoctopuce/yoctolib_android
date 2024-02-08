/*
 *
 *  $Id: GettingStarted_Yocto_Servo.java 58172 2023-11-30 17:10:23Z martinm $
 *
 *  An example that shows how to use a  Yocto-Servo
 *
 *  You can find more information on our web site:
 *   Yocto-Servo documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-servo/doc.html
 *   Android API Reference:
 *      https://www.yoctopuce.com/EN/doc/reference/yoctolib-android-EN.html
 *
 */

package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YServo;

public class GettingStarted_Yocto_Servo extends Activity implements OnItemSelectedListener
{

    private YServo servo = null;
    private ArrayAdapter<String> aa;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_servo);
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
            YServo s = YServo.FirstServo();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextServo();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        aa.notifyDataSetChanged();
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
        String hwid = parent.getItemAtPosition(pos).toString();
        servo = YServo.FindServo(hwid);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    /** Called when the user touches the button State A */
    public void updatePos(View view)
    {
        if (servo == null)
            return;

        SeekBar bar = (SeekBar) findViewById(R.id.seekBarPos);
        int newpow = bar.getProgress() * 2000 / bar.getMax() - 1000;
        switch (view.getId()) {
        case R.id.movePosButton:
            try {
                servo.move(newpow, 1000);
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
            break;
        case R.id.setPosButton:
            try {
                servo.set_position(newpow);
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
            break;
        }

    }

}
