/*
 *
 *  $Id: GettingStarted_Yocto_4_20mA_Tx.java 58172 2023-11-30 17:10:23Z martinm $
 *
 *  An example that shows how to use a  Yocto-4-20mA-Tx
 *
 *  You can find more information on our web site:
 *   Yocto-4-20mA-Tx documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-4-20ma-tx/doc.html
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
import android.widget.Toast;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YCurrentLoopOutput;
import com.yoctopuce.YoctoAPI.YModule;

public class GettingStarted_Yocto_4_20mA_Tx extends Activity implements OnItemSelectedListener
{

    private ArrayAdapter<String> aa;
    private String serial = "";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_4_20ma_tx);
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
        try {
            aa.clear();
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YModule module = YModule.FirstModule();
            while (module != null) {
                if (module.get_productName().equals("Yocto-4-20mA-Tx")) {
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
    protected void onStop()
    {
        super.onStop();
        YAPI.FreeAPI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        serial = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }


    public void updateOutput(View view)
    {
        TextView editText1 = (TextView) findViewById(R.id.editText1);
        double value;
        try {
            value = Double.parseDouble(editText1.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Invalid Number. ", Toast.LENGTH_LONG).show();
            editText1.setText("");
            return;
        }
        YCurrentLoopOutput loop = YCurrentLoopOutput.FindCurrentLoopOutput(serial + ".currentLoopOutput");
        int loopPower = 0;
        try {
            loopPower = loop.get_loopPower();
            if (loopPower == YCurrentLoopOutput.LOOPPOWER_NOPWR) {
                Toast.makeText(this, "Current loop not powered", Toast.LENGTH_LONG).show();
                editText1.setText("");
                return;
            }
            if (loopPower == YCurrentLoopOutput.LOOPPOWER_LOWPWR) {
                Toast.makeText(this, "Insufficient voltage on current loop", Toast.LENGTH_LONG).show();
                editText1.setText("");
                return;
            }
            loop.set_current(value);
            Toast.makeText(this, "current loop set to " + Double.toString(value) + " mA", Toast.LENGTH_LONG).show();
        } catch (YAPI_Exception e) {
            Toast.makeText(this, "Error:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
