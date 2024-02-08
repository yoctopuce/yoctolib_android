/*
 *
 *  $Id: svn_id $
 *
 *  An example that shows how to use a  Yocto-MaxiBuzzer
 *
 *  You can find more information on our web site:
 *   Yocto-MaxiBuzzer documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-maxibuzzer/doc.html
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
import com.yoctopuce.YoctoAPI.YAnButton;
import com.yoctopuce.YoctoAPI.YBuzzer;
import com.yoctopuce.YoctoAPI.YColorLed;
import com.yoctopuce.YoctoAPI.YModule;

public class GettingStarted_Yocto_MaxiBuzzer extends Activity implements OnItemSelectedListener, Runnable {

    private String serial = null;
    private ArrayAdapter<String> aa;
    private Thread bgthread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_maxibuzzer);
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
                if (module.get_productName().equals("Yocto-MaxiBuzzer")) {
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
        textView.setText("press a test button on the Yocto-Buzzer");
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void run() {
        while (serial != null) {
            try {
                int frequency;
                int color;
                int volume;
                YBuzzer buzzer = YBuzzer.FindBuzzer(serial + ".buzzer");
                YAnButton button1 = YAnButton.FindAnButton(serial + ".anButton1");
                YAnButton button2 = YAnButton.FindAnButton(serial + ".anButton2");
                YColorLed led = YColorLed.FindColorLed(serial + ".colorLed");
                Boolean b1 = (button1.get_isPressed() == YAnButton.ISPRESSED_TRUE);
                Boolean b2 = (button2.get_isPressed() == YAnButton.ISPRESSED_TRUE);
                if (b1 || b2) {
                    if (b1) {
                        volume = 60;
                        frequency = 1500;
                        color = 0xff0000;
                    } else {
                        volume = 30;
                        color = 0x00ff00;
                        frequency = 750;
                    }
                    led.resetBlinkSeq();
                    led.addRgbMoveToBlinkSeq(color, 100);
                    led.addRgbMoveToBlinkSeq(0, 100);
                    led.startBlinkSeq();
                    buzzer.set_volume(volume);
                    int i;
                    for (i = 0; i < 5; i++) {
                        // this can be done using sequence as well
                        buzzer.set_frequency(frequency);
                        buzzer.freqMove(2 * frequency, 250);
                        YAPI.Sleep(250);
                    }
                    buzzer.set_frequency(0);
                    led.stopBlinkSeq();
                    led.set_rgbColor(0);
                }
            } catch (YAPI_Exception ex) {
                System.out.println("Module not connected (check identification and USB cable)");
            }
        }
    }
}
