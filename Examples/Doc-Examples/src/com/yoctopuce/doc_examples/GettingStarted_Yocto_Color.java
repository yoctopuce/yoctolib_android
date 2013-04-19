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
import com.yoctopuce.YoctoAPI.YColorLed;

public class GettingStarted_Yocto_Color extends Activity implements OnItemSelectedListener
{

    private YColorLed color = null;
    private ArrayAdapter<String> aa;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_color);
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
            YAPI.UpdateDeviceList();
            YColorLed c = YColorLed.FirstColorLed();
            while (c != null) {
                String hwid = c.get_hardwareId();
                aa.add(hwid);
                c = c.nextColorLed();
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
        color = YColorLed.FindColorLed(hwid);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    /** Called when the user touches the button State A */
    public void updateColor(View view)
    {
        if (color == null)
            return;

        SeekBar red_bar = (SeekBar) findViewById(R.id.seekBarRed);
        int red = red_bar.getProgress() * 255 / red_bar.getMax();
        SeekBar green_bar = (SeekBar) findViewById(R.id.seekBarGreen);
        int green = green_bar.getProgress() * 255 / green_bar.getMax();
        SeekBar blue_bar = (SeekBar) findViewById(R.id.seekBarBlue);
        int blue = blue_bar.getProgress() * 255 / blue_bar.getMax();
        int newcolor = (red << 16) + (green << 8) + blue;
        switch (view.getId()) {
        case R.id.moveColorButton:
            try {
                color.rgbMove(newcolor, 1000);
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
            break;
        case R.id.setColorButton:
            try {
                color.set_rgbColor(newcolor);
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
            break;
        }

    }

}
