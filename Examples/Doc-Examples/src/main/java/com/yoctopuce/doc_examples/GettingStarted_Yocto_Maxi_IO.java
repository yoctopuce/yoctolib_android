/*
 *
 *  $Id: GettingStarted_Yocto_Maxi_IO.java 58172 2023-11-30 17:10:23Z martinm $
 *
 *  An example that shows how to use a  Yocto-Maxi-IO
 *
 *  You can find more information on our web site:
 *   Yocto-Maxi-IO documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-maxi-io/doc.html
 *   Android API Reference:
 *      https://www.yoctopuce.com/EN/doc/reference/yoctolib-android-EN.html
 *
 */

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
import com.yoctopuce.YoctoAPI.YDigitalIO;
import com.yoctopuce.YoctoAPI.YModule;

public class GettingStarted_Yocto_Maxi_IO extends Activity implements OnItemSelectedListener {

    private ArrayAdapter<String> aa;
    private String serial = "";
    private Handler handler = null;
    private int _outputdata;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_maxi_io);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
        handler = new Handler();
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
                if (module.get_productName().equals("Yocto-Maxi-IO")) {
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
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(r);
        YAPI.FreeAPI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        serial = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    final Runnable r = new Runnable() {
        public void run() {
            if (serial != null) {
                YDigitalIO io = YDigitalIO.FindDigitalIO(serial);
                try {

                    // lets configure the channels direction
                    // bits 0..3 as output
                    // bits 4..7 as input
                    io.set_portDirection(0x0F);
                    io.set_portPolarity(0); // polarity set to regular
                    io.set_portOpenDrain(0); // No open drain
                    _outputdata = (_outputdata + 1) % 16; // cycle ouput 0..15
                    io.set_portState(_outputdata); // We could have used set_bitState as well
                    int inputdata = io.get_portState(); // read port values
                    String line = "";  // display part state value as binary
                    for (int i = 0; i < 8; i++) {
                        if ((inputdata & (128 >> i)) > 0) {
                            line = line + '1';
                        } else {
                            line = line + '0';
                        }
                    }
                    TextView view = (TextView) findViewById(R.id.portfield);
                    view.setText("port value = " + line);
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

}
