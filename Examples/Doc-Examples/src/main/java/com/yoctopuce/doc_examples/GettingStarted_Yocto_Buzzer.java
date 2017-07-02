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
import com.yoctopuce.YoctoAPI.YLed;
import com.yoctopuce.YoctoAPI.YModule;

public class GettingStarted_Yocto_Buzzer extends Activity implements OnItemSelectedListener, Runnable {

    private String serial = null;
    private ArrayAdapter<String> aa;
    private Thread bgthread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_buzzer);
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
                if (module.get_productName().equals("Yocto-Buzzer")) {
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
                YBuzzer buzzer = YBuzzer.FindBuzzer(serial + ".buzzer");
                YAnButton button1 = YAnButton.FindAnButton(serial + ".anButton1");
                YAnButton button2 = YAnButton.FindAnButton(serial + ".anButton2");
                Boolean b1 = (button1.get_isPressed() == YAnButton.ISPRESSED_TRUE);
                Boolean b2 = (button2.get_isPressed() == YAnButton.ISPRESSED_TRUE);
                if (b1 || b2) {
                    YLed led;
                    if (b1) {
                        led = YLed.FindLed(serial + ".led1");
                        frequency = 1500;
                    } else {
                        led = YLed.FindLed(serial + ".led2");
                        frequency = 750;
                    }
                    led.set_power(YLed.POWER_ON);
                    led.set_luminosity(100);
                    led.set_blinking(YLed.BLINKING_PANIC);
                    int i;
                    for (i = 0; i < 5; i++) {
                        // this can be done using sequence as well
                        buzzer.set_frequency(frequency);
                        buzzer.freqMove(2 * frequency, 250);
                        YAPI.Sleep(250);
                    }
                    buzzer.set_frequency(0);
                    led.set_power(YLed.POWER_OFF);
                }
            } catch (YAPI_Exception ex) {
                System.out.println("Module not connected (check identification and USB cable)");
            }
        }
    }
}
