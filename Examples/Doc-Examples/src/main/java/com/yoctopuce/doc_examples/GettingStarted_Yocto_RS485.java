/*
 *
 *  $Id: GettingStarted_Yocto_RS485.java 52208 2022-12-07 08:17:21Z mvuilleu $
 *
 *  An example that show how to use a  Yocto-RS485
 *
 *  You can find more information on our web site:
 *   Yocto-RS485 documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-rs485/doc.html
 *   Android API Reference:
 *      https://www.yoctopuce.com/EN/doc/reference/yoctolib-android-EN.html
 *
 */

package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YSerialPort;

public class GettingStarted_Yocto_RS485 extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private YModule module = null;
    private TextView resultTextView;
    private EditText valueEditText;
    private EditText registerEditText;
    private EditText slaveEditText;
    private Spinner my_spin;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_rs485);
        my_spin = (Spinner) findViewById(R.id.spinner1);
        my_spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        my_spin.setAdapter(aa);
        slaveEditText = (EditText) findViewById(R.id.slavefield);
        registerEditText = (EditText) findViewById(R.id.registerfield);
        valueEditText = (EditText) findViewById(R.id.valuefield);
        resultTextView = (TextView) findViewById(R.id.resultvalue);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        try {
            aa.clear();
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YSerialPort r = YSerialPort.FirstSerialPort();
            while (r != null) {
                String hwid = r.get_hardwareId();
                aa.add(hwid);
                r = r.nextSerialPort();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        // refresh Spinner with detected relay
        aa.notifyDataSetChanged();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        YAPI.FreeAPI();
    }

    private int _doModbus(String hwid, String slavefield, String registerfield, String cmdfield)
    {
        int slave;
        int reg;
        try {
            slave = Integer.parseInt(slavefield);
            reg = Integer.parseInt(registerfield);
        } catch (NumberFormatException ex) {
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show();
            return 0;
        }
        try {
            YSerialPort serialPort = YSerialPort.FindSerialPort(hwid);
            // send new value to modbus device
            if(!cmdfield.equals("") && (reg % 40000) < 10000) {
                int cmd = Integer.parseInt(cmdfield);
                if(reg >= 40001) {
                    serialPort.modbusWriteRegister(slave, reg-40001, cmd);
                } else {
                    serialPort.modbusWriteBit(slave, reg-1, cmd);
                }
            }
            // read it again

            int val;
            if(reg >= 40001) {
                val = serialPort.modbusReadRegisters(slave, reg-40001, 1).get(0);
            } else if(reg >= 30001) {
                val = serialPort.modbusReadInputRegisters(slave, reg-30001, 1).get(0);
            } else if(reg >= 10001) {
                val = serialPort.modbusReadInputBits(slave, reg-10001, 1).get(0);
            } else {
                val = serialPort.modbusReadBits(slave, reg-1, 1).get(0);
            }
            return val;
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        resultTextView.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    public void refreshInfo(View view)
    {
        Object selectedItem = my_spin.getSelectedItem();
        if (selectedItem!=null) {
            String hwid = selectedItem.toString();
            int val = _doModbus(hwid, slaveEditText.getText().toString(),
                    registerEditText.getText().toString(), valueEditText.getText().toString());
            resultTextView.setText(Integer.toString(val));
        }
    }


}
