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
import com.yoctopuce.YoctoAPI.YWeighScale;

public class GettingStarted_Yocto_Bridge extends Activity implements OnItemSelectedListener
{

    private YWeighScale _weighScale = null;
    private ArrayAdapter<String> aa;
    private Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_bridge);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
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
            YWeighScale wc = YWeighScale.FirstWeighScale();
            while (wc != null) {
                String hwid = wc.get_hardwareId();
                aa.add(hwid);
                wc = wc.nextWeighScale();
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
        handler.removeCallbacks(r);
        YAPI.FreeAPI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        String hwid = parent.getItemAtPosition(pos).toString();
        _weighScale = YWeighScale.FindWeighScale(hwid);
        // On startup, enable excitation and tare weigh scale
        try {
            _weighScale.set_excitation(YWeighScale.EXCITATION_AC);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        _weighScale = null;
    }

    final Runnable r = new Runnable()
    {
        public void run()
        {
            if (_weighScale != null) {
                try {
                    TextView view = (TextView) findViewById(R.id.weightfield);
                    view.setText(String.format("%.1f %s", _weighScale.getCurrentValue(), _weighScale.getUnit()));
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };


    public void onTare(View view)
    {
        if (_weighScale != null) {
            try {
                _weighScale.tare();
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
        }
    }
}