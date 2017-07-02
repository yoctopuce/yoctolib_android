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
import com.yoctopuce.YoctoAPI.YTemperature;

public class GettingStarted_Yocto_Thermistor_C extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_thermistor_c);
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
                if (module.get_productName().equals("Yocto-Thermistor-C")) {
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
                TextView view;
                try {
                    YTemperature ch1 = YTemperature.FindTemperature(serial + ".temperature1");
                    view = (TextView) findViewById(R.id.tempfield1);
                    view.setText(String.format("%.1f %s", ch1.getCurrentValue(), ch1.getUnit()));
                    YTemperature ch2 = YTemperature.FindTemperature(serial + ".temperature2");
                    view = (TextView) findViewById(R.id.tempfield2);
                    view.setText(String.format("%.1f %s", ch2.getCurrentValue(), ch2.getUnit()));
                    YTemperature ch3 = YTemperature.FindTemperature(serial + ".temperature3");
                    view = (TextView) findViewById(R.id.tempfield3);
                    view.setText(String.format("%.1f %s", ch3.getCurrentValue(), ch3.getUnit()));
                    YTemperature ch4 = YTemperature.FindTemperature(serial + ".temperature4");
                    view = (TextView) findViewById(R.id.tempfield4);
                    view.setText(String.format("%.1f %s", ch4.getCurrentValue(), ch4.getUnit()));
                    YTemperature ch5 = YTemperature.FindTemperature(serial + ".temperature5");
                    view = (TextView) findViewById(R.id.tempfield5);
                    view.setText(String.format("%.1f %s", ch5.getCurrentValue(), ch5.getUnit()));
                    YTemperature ch6 = YTemperature.FindTemperature(serial + ".temperature6");
                    view = (TextView) findViewById(R.id.tempfield6);
                    view.setText(String.format("%.1f %s", ch6.getCurrentValue(), ch6.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();

                }
            }
            handler.postDelayed(this, 1000);
        }
    };

}
