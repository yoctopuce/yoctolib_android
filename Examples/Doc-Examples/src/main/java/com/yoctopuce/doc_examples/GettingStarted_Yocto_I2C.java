/*
 *
 *  $Id: svn_id $
 *
 *  An example that shows how to use a  Yocto-I2C
 *
 *  You can find more information on our web site:
 *   Yocto-I2C documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-i2c/doc.html
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YI2cPort;

import java.util.ArrayList;
import java.util.Locale;

public class GettingStarted_Yocto_I2C extends Activity implements OnItemSelectedListener
{

    private YI2cPort _i2cPort = null;
    private ArrayAdapter<String> aa;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_i2c);
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
            YI2cPort s = YI2cPort.FirstI2cPort();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextI2cPort();
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
        _i2cPort = YI2cPort.FindI2cPort(hwid);
        try {
            // sample code reading MCP9804 temperature sensor
            _i2cPort.set_i2cMode("100kbps");
            _i2cPort.set_i2cVoltageLevel(YI2cPort.I2CVOLTAGELEVEL_3V3);
            _i2cPort.reset();
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

            if (_i2cPort != null) {
                try {
                    ArrayList<Integer> toSend = new ArrayList<>(1);
                    ArrayList<Integer> received;
                    toSend.add(0x05);
                    received = _i2cPort.i2cSendAndReceiveArray(0x1f, toSend, 2);
                    int tempReg = (received.get(0) << 8) + received.get(1);
                    if ((tempReg & 0x1000) != 0) {
                        tempReg -= 0x2000;   // perform sign extension
                    } else {
                        tempReg &= 0x0fff;   // clear status bits
                    }
                    TextView view = (TextView) findViewById(R.id.tempfield);
                    view.setText(String.format(Locale.US, "%.3f", tempReg / 16.0));

                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

}
