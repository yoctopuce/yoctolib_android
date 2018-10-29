/*
 *
 *  $Id: GettingStarted_Yocto_SPI.java 32625 2018-10-10 13:27:32Z seb $
 *
 *  An example that show how to use a  Yocto-SPI
 *
 *  You can find more information on our web site:
 *   Yocto-SPI documentation:
 *      https://www.yoctopuce.com/EN/products/yocto-spi/doc.html
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

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YPwmOutput;
import com.yoctopuce.YoctoAPI.YSpiPort;

import java.util.ArrayList;

public class GettingStarted_Yocto_SPI extends Activity implements OnItemSelectedListener
{

    private YSpiPort _spiPort = null;
    private ArrayAdapter<String> aa;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gettingstarted_yocto_spi);
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
            YSpiPort s = YSpiPort.FirstSpiPort();
            while (s != null) {
                String hwid = s.get_hardwareId();
                aa.add(hwid);
                s = s.nextSpiPort();
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
        _spiPort = YSpiPort.FindSpiPort(hwid);
        try {
            _spiPort.set_spiMode("250000,2,msb");
            _spiPort.set_ssPolarity(YSpiPort.SSPOLARITY_ACTIVE_LOW);
            _spiPort.set_protocol("Frame:5ms");
            _spiPort.reset();
            _spiPort.writeHex("0c01"); // Exit from shutdown state
            _spiPort.writeHex("09ff"); // Enable BCD for all digits
            _spiPort.writeHex("0b07"); // Enable digits 0-7 (=8 in total)
            _spiPort.writeHex("0a0a"); // Set medium brightness
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    /**
     * Called when the user touches the button State A
     */
    public void update(View view)
    {
        if (_spiPort == null)
            return;

        EditText editText = (EditText) findViewById(R.id.value);

        int value;
        try {
            value = Integer.valueOf(editText.getText().toString());
        } catch (NumberFormatException ex) {
            return;
        }
        try {
            for (int i = 1; i <= 8; i++) {
                int digit = value % 10;
                ArrayList<Integer> dataToWrite = new ArrayList<>();
                dataToWrite.add(i);
                dataToWrite.add(digit);
                _spiPort.writeArray(dataToWrite);
                value = value / 10;
            }

        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }

    }

}
