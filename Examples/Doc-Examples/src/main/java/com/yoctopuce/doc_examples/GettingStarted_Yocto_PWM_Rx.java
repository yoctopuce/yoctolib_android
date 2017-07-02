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
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YPwmInput;

public class GettingStarted_Yocto_PWM_Rx extends Activity implements OnItemSelectedListener
{
    private ArrayAdapter<String> aa;
    private Handler handler;
    private String serial;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_pwm_rx);
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
                if (module.get_productName().equals("Yocto-PWM-Rx")) {
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
                YPwmInput pwm1 = YPwmInput.FindPwmInput(serial + ".pwmInput1");
                try {
                    TextView view = (TextView) findViewById(R.id.freq1);
                    view.setText(String.format("%.1f Hz", pwm1.get_frequency()));
                    view = (TextView) findViewById(R.id.cycle1);
                    view.setText(String.format("%.1f %%", pwm1.get_dutyCycle()));
                    view = (TextView) findViewById(R.id.pulse1);
                    view.setText(String.format("%d ", pwm1.get_pulseCounter()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
                YPwmInput pwm2 = YPwmInput.FindPwmInput(serial + ".pwmInput2");
                try {
                    TextView view = (TextView) findViewById(R.id.freq2);
                    view.setText(String.format("%.1f Hz", pwm2.get_frequency()));
                    view = (TextView) findViewById(R.id.cycle2);
                    view.setText(String.format("%.1f %%", pwm2.get_dutyCycle()));
                    view = (TextView) findViewById(R.id.pulse2);
                    view.setText(String.format("%d ", pwm2.get_pulseCounter()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

}
