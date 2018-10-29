/*
 *
 *  $Id: GettingStarted_Yocto_PWM_Tx.java 32625 2018-10-10 13:27:32Z seb $
 *
 *  An example that show how to use a  Yocto-PWM-Tx
 *
 *  You can find more information on our web site:
 *   Yocto-PWM-Tx documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-pwm-tx/doc.html
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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YPwmOutput;

public class GettingStarted_Yocto_PWM_Tx extends Activity implements OnItemSelectedListener
{

    private YPwmOutput pwmoutput = null;
    private ArrayAdapter<String> aa;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_pwm_tx);
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
            YPwmOutput s = YPwmOutput.FirstPwmOutput();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextPwmOutput();
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
        String hwid = (String) parent.getItemAtPosition(pos);
        pwmoutput = YPwmOutput.FindPwmOutput(hwid);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    /** Called when the user touches the button State A */
    public void update(View view)
    {
        if (pwmoutput == null)
            return;

        try {
            EditText editText = (EditText) findViewById(R.id.frequency);
            String frequency = editText.getText().toString();
            if (frequency.length() > 0) {
                pwmoutput.set_frequency(Integer.valueOf(frequency));
            }
            pwmoutput.set_enabled(YPwmOutput.ENABLED_TRUE);
            SeekBar bar = (SeekBar) findViewById(R.id.seekBarPos);
            double newpow = bar.getProgress();
            switch (view.getId()) {
                case R.id.movePosButton:
                    pwmoutput.dutyCycleMove(newpow, 1000);
                    break;
                case R.id.setPosButton:
                    pwmoutput.set_dutyCycle(newpow);
                    break;
            }

        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }

    }

}
