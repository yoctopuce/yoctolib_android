/*
 *
 *  $Id: svn_id $
 *
 *  Doc-GettingStarted-Yocto-RFID example
 *
 *  You can find more information on our web site:
 *   Android API Reference:
 *      https://www.yoctopuce.com/EN/doc/reference/yoctolib-android-EN.html
 *
 */

package com.yoctopuce.doc_examples;

import static java.lang.Math.exp;
import static java.lang.Math.log;

import android.app.Activity;
import android.os.Bundle;
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
import com.yoctopuce.YoctoAPI.YAnButton;
import com.yoctopuce.YoctoAPI.YBuzzer;
import com.yoctopuce.YoctoAPI.YColorLedCluster;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YPwmOutput;
import com.yoctopuce.YoctoAPI.YQuadratureDecoder;
import com.yoctopuce.YoctoAPI.YRfidOptions;
import com.yoctopuce.YoctoAPI.YRfidReader;
import com.yoctopuce.YoctoAPI.YRfidStatus;
import com.yoctopuce.YoctoAPI.YRfidTagInfo;
import com.yoctopuce.YoctoAPI.YSerialPort;

import java.util.ArrayList;

public class GettingStarted_Yocto_RFID extends Activity implements OnItemSelectedListener, Runnable {

    private String serial = null;
    private ArrayAdapter<String> aa;
    private Thread bgthread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_rfid);
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
                if (module.get_productName().startsWith("Yocto-RFID")) {
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
            if (bgthread != null) {
                bgthread.join();
            }
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
        TextView textView = (TextView) findViewById(R.id.response);
        textView.setText("Place a RFID tag near the antenna");
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void run() {
        while (serial != null) {
            try {
                YRfidReader reader = YRfidReader.FindRfidReader(serial + ".rfidReader");
                YColorLedCluster leds = YColorLedCluster.FindColorLedCluster(serial + ".colorLedCluster");
                YAnButton button = YAnButton.FindAnButton(serial + ".anButton1");
                YBuzzer buzzer = YBuzzer.FindBuzzer(serial + ".buzzer");
                leds.set_rgbColor(0, 1, 0x000000);
                buzzer.set_volume(75);
                StringBuilder sb = new StringBuilder();
                ArrayList<String> tagList;
                do {
                    YAPI.Sleep(250);
                    tagList = reader.get_tagIdList();
                } while (tagList.isEmpty());

                String tagId = tagList.get(0);
                YRfidStatus opStatus = new YRfidStatus();
                YRfidOptions options = new YRfidOptions();
                YRfidTagInfo taginfo = reader.get_tagInfo(tagId, opStatus);
                int blocksize = taginfo.get_tagBlockSize();
                int firstBlock = taginfo.get_tagFirstBlock();
                sb.append("Tag ID          = ").append(taginfo.get_tagId()).append("\n");
                sb.append("Tag Memory size = ").append(taginfo.get_tagMemorySize()).append(" bytes\n");
                sb.append("Tag Block  size = ").append(taginfo.get_tagBlockSize()).append(" bytes\n");

                String data = reader.tagReadHex(tagId, firstBlock, 3 * blocksize, options, opStatus);
                if (opStatus.get_errorCode() == YRfidStatus.SUCCESS) {
                    sb.append("First 3 blocks  = ").append(data);
                    leds.set_rgbColor(0, 1, 0x00FF00);
                    buzzer.pulse(1000, 100);
                } else {
                    sb.append("Cannot read tag contents (").append(opStatus.get_errorMessage()).append(")\n");
                    leds.set_rgbColor(0, 1, 0xFF0000);
                }
                leds.rgb_move(0, 1, 0x000000, 200);
                String text = sb.toString();
                runOnUiThread(() -> {
                    TextView textView = (TextView) findViewById(R.id.response);
                    textView.setText(text);
                });
            } catch (YAPI_Exception ex) {
                runOnUiThread(() -> {
                    TextView textView = (TextView) findViewById(R.id.response);
                    textView.setText(ex.getLocalizedMessage());
                });
                ex.printStackTrace();
            }
        }
    }

}
