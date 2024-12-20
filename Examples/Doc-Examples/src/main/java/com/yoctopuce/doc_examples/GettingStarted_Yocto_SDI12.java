/*
 *
 *  $Id: svn_id $
 *
 *  An example that shows how to use a  Yocto-SDI12
 *
 *  You can find more information on our web site:
 *   Yocto-SDI12 documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-sdi12/doc.html
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
import com.yoctopuce.YoctoAPI.YSdi12Port;
import com.yoctopuce.YoctoAPI.YSdi12SensorInfo;

import java.util.ArrayList;

public class GettingStarted_Yocto_SDI12 extends Activity implements OnItemSelectedListener {

    private YSdi12Port sdi12Port = null;
    private ArrayAdapter<String> aa;
    private YSdi12SensorInfo singleSensor = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_sdi12);
        Spinner my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
    }

    @Override
    protected void onStart() {
        super.onStart();
        aa.clear();
        try {
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YSdi12Port s = YSdi12Port.FirstSdi12Port();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextSdi12Port();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        aa.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        YAPI.FreeAPI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String hwid = (String) parent.getItemAtPosition(pos);
        sdi12Port = YSdi12Port.FindSdi12Port(hwid);
        try {
            sdi12Port.reset();
            singleSensor = sdi12Port.discoverSingleSensor();

        } catch (YAPI_Exception e) {
            TextView textView = (TextView) findViewById(R.id.response);
            textView.setText(e.getStackTraceToString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     * Called when the user touches the button State A
     */
    public void update(View view) {
        TextView textView = (TextView) findViewById(R.id.response);
        StringBuilder response = new StringBuilder();
        if (sdi12Port == null || singleSensor == null) {
            textView.setText("No module connected (check USB cable)");
            return;
        }
        try {
            response.append(String.format("%-35s %s ", "Sensor address :", singleSensor.get_sensorAddress())).append("\n");
            response.append(String.format("%-35s %s ", "Sensor SDI-12 compatibility : ", singleSensor.get_sensorProtocol())).append("\n");
            response.append(String.format("%-35s %s ", "Sensor company name : ", singleSensor.get_sensorVendor())).append("\n");
            response.append(String.format("%-35s %s ", "Sensor model number : ", singleSensor.get_sensorModel())).append("\n");
            response.append(String.format("%-35s %s ", "Sensor version : ", singleSensor.get_sensorVersion())).append("\n");
            response.append(String.format("%-35s %s ", "Sensor serial number : ", singleSensor.get_sensorSerial())).append("\n");
            ArrayList<Double> valSensor = sdi12Port.readSensor(singleSensor.get_sensorAddress(), "M", 5000);
            for (int i = 0; i < valSensor.size(); i = i + 1) {
                if (singleSensor.get_measureCount() > 1) {
                    response.append(String.format("%s : %-6.2f %-10s (%s)\n",
                            singleSensor.get_measureSymbol(i), valSensor.get(i),
                            singleSensor.get_measureUnit(i), singleSensor.get_measureDescription(i)));
                } else {
                    response.append(String.format("%f\n", valSensor.get(i)));
                }
            }
        } catch (YAPI_Exception e) {
            response.append(e.getStackTraceToString());
        }
        textView.setText(response.toString());
    }
}
