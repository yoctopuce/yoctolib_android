package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YDisplay;
import com.yoctopuce.YoctoAPI.YDisplayLayer;

public class GettingStarted_Yocto_MiniDisplay extends Activity implements OnItemSelectedListener
{

       
    private YDisplay display = null;
    private ArrayAdapter<String> aa;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_minidisplay);
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
            YDisplay d = YDisplay.FirstDisplay();
            while (d != null) {
                String hwid = d.get_hardwareId();
                aa.add(hwid);
                d = d.nextDisplay();
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
        display = YDisplay.FindDisplay(hwid);
        updateDisplay(null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    public void updateDisplay(View view)
    {
        if (display == null)
            return;
        
        EditText message = (EditText) findViewById(R.id.editText1);        
        // clean up
        try {
            display.resetAll();

            // retreive the display size
            int w = display.get_displayWidth();
            int h = display.get_displayHeight();

            // reteive the first layer
            YDisplayLayer l0 = display.get_displayLayer(0);

            // display a text in the middle of the screen
            l0.drawText(w / 2, h / 2, YDisplayLayer.ALIGN.CENTER, message.getText().toString());

            // visualize each corner
            l0.moveTo(0, 5);
            l0.lineTo(0, 0);
            l0.lineTo(5, 0);
            l0.moveTo(0, h - 6);
            l0.lineTo(0, h - 1);
            l0.lineTo(5, h - 1);
            l0.moveTo(w - 1, h - 6);
            l0.lineTo(w - 1, h - 1);
            l0.lineTo(w - 6, h - 1);
            l0.moveTo(w - 1, 5);
            l0.lineTo(w - 1, 0);
            l0.lineTo(w - 6, 0);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }

    }

       
}
