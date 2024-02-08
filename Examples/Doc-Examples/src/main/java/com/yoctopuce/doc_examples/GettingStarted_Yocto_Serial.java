/*
 *
 *  $Id: GettingStarted_Yocto_Serial.java 58172 2023-11-30 17:10:23Z martinm $
 *
 *  An example that shows how to use a  Yocto-Serial
 *
 *  You can find more information on our web site:
 *   Yocto-Serial documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-serial/doc.html
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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YPwmOutput;
import com.yoctopuce.YoctoAPI.YSerialPort;

public class GettingStarted_Yocto_Serial extends Activity implements OnItemSelectedListener
{

    private YSerialPort serialPort = null;
    private ArrayAdapter<String> aa;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_serial);
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
            YSerialPort s = YSerialPort.FirstSerialPort();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextSerialPort();
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
        String hwid = (String) parent.getItemAtPosition(pos);
        serialPort = YSerialPort.FindSerialPort(hwid);
        try {
            serialPort.set_serialMode("9600,8N1");
            serialPort.set_protocol("Line");
            serialPort.reset();
        }catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    /** Called when the user touches the button State A */
    public void update(View view)
    {
        if (serialPort == null)
            return;

        try {

            EditText editText = (EditText) findViewById(R.id.editText1);
            String line = editText.getText().toString();
            serialPort.writeLine(line);
            YAPI.Sleep(500);
            String response = "";
            String outline;
            do {
                outline = serialPort.readLine();
                response += outline;
            } while (!outline.equals(""));
            TextView textView = (TextView) findViewById(R.id.response);
            textView.setText(response);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }
}
