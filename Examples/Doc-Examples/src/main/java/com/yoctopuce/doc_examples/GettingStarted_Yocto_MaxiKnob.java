/*
 *
 *  $Id: svn_id $
 *
 *  An example that shows how to use a  Yocto-MaxiKnob
 *
 *  You can find more information on our web site:
 *   Yocto-MaxiKnob documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-maxiknob/doc.html
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
import android.widget.Spinner;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YBuzzer;
import com.yoctopuce.YoctoAPI.YAnButton;
import com.yoctopuce.YoctoAPI.YQuadratureDecoder;
import com.yoctopuce.YoctoAPI.YColorLedCluster;
import com.yoctopuce.YoctoAPI.YModule;

import static java.lang.Math.exp;
import static java.lang.Math.log;

public class GettingStarted_Yocto_MaxiKnob extends Activity implements OnItemSelectedListener, Runnable {

    private String serial = null;
    private ArrayAdapter<String> aa;
    private Thread bgthread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_maxiknob);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            aa.clear();
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YModule module = YModule.FirstModule();
            while (module != null) {
                if (module.get_productName().equals("Yocto-MaxiKnob")) {
                    String serial = module.get_serialNumber();
                    aa.add(serial);
                }
                module = module.nextModule();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        aa.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        serial = null;
        try {
            bgthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        YAPI.FreeAPI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        serial = parent.getItemAtPosition(pos).toString();
        bgthread = new Thread(this);
        bgthread.start();
        TextView textView = (TextView) findViewById(R.id.message);
        textView.setText("Press a test button, or turn the encoder on the Yocto-MaxiBuzzer");
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void run() {
        while (serial != null) {
            try {
                YBuzzer buzzer = YBuzzer.FindBuzzer(serial + ".buzzer");
                YColorLedCluster leds = YColorLedCluster.FindColorLedCluster(serial + ".colorLedCluster");
                YAnButton button = YAnButton.FindAnButton(serial + ".anButton1");
                YQuadratureDecoder qd = YQuadratureDecoder.FindQuadratureDecoder(serial + ".quadratureDecoder1");
                int lastPos = (int) qd.get_currentValue();
                buzzer.set_volume(75);
                while (button.isOnline()) {
                    if ((button.get_isPressed() == YAnButton.ISPRESSED_TRUE) && (lastPos != 0)) {
                        lastPos = 0;
                        qd.set_currentValue(0);
                        buzzer.playNotes("'E32 C8");
                        leds.set_rgbColor(0, 1, 0x000000);
                    } else {
                        int p = (int) qd.get_currentValue();
                        if (lastPos != p) {
                            lastPos = p;
                            buzzer.pulse(notefreq(p), 100);
                            leds.set_hslColor(0, 1, 0x00FF7f | (p % 255) << 16);
                        }
                    }
                }
            } catch (YAPI_Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private int notefreq(int note) {
        return (int) (220.0 * exp(note * log(2) / 12));
    }
}
