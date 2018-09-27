package com.yoctopuce.doc_examples;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YAnButton;
import com.yoctopuce.YoctoAPI.YMeasure;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YSensor;

import java.util.Date;
import java.util.Locale;

public class ProgEventBasedFragment extends ListFragment implements YAPI.DeviceArrivalCallback, YAnButton.UpdateCallback, YSensor.TimedReportCallback, YSensor.UpdateCallback, YAPI.DeviceRemovalCallback, YModule.ConfigChangeCallback, YModule.BeaconCallback
{

    private static final String TAG = "Prog-EventBased";


    private class Events
    {
        private Date mDate;
        private String mLastNotifications;

        public Events(Date date, String msg)
        {
            mDate = date;
            mLastNotifications = msg;
        }

        @Override
        public String toString()
        {
            return mDate.toString() + ": " + mLastNotifications;
        }
    }

    private void pushEvent(String message)
    {
        mAdapter.add(new Events(new Date(), message));
    }


    private ArrayAdapter<Events> mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAdapter = new ArrayAdapter<Events>(getActivity(),
                android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        try {
            YAPI.RegisterDeviceArrivalCallback(this);
            YAPI.RegisterDeviceRemovalCallback(this);
            YAPI.EnableUSBHost(getActivity());
            YAPI.RegisterHub("usb");
            mHandler.postDelayed(mRunnable, 300);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }


    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            try {
                YAPI.UpdateDeviceList();
                YAPI.HandleEvents();
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(mRunnable, 300);
        }
    };


    @Override
    public void onPause()
    {
        super.onPause();
        YAPI.FreeAPI();
    }


    @Override
    public void yNewValue(YAnButton fct, String value)
    {
        try {
            pushEvent(fct.get_hardwareId() + ": " + value + " (new value)");
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void yNewValue(YSensor fct, String value)
    {
        try {
            pushEvent(fct.get_hardwareId() + ": " + value + " (new value)");
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void timedReportCallback(YSensor fct, YMeasure measure)
    {
        try {
            pushEvent(fct.get_hardwareId() + ": " + measure.get_averageValue() + " " + fct.get_unit() + " (timed report)" + measure.get_minValue() + "/" + measure.get_maxValue());
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void yDeviceArrival(YModule module)
    {
        try {
            String serial = module.get_serialNumber();
            pushEvent("Device arrival : " + serial);
            module.registerConfigChangeCallback(this);
            module.registerBeaconCallback(this);

            // First solution: look for a specific type of function (eg. anButton)
            int fctcount = module.functionCount();
            for (int i = 0; i < fctcount; i++) {
                String fctName = module.functionId(i);
                String hardwareId = serial + "." + fctName;

                // register call back for anbuttons
                if (fctName.startsWith("anButton")) {
                    YAnButton bt = YAnButton.FindAnButton(hardwareId);
                    bt.registerValueCallback(this);
                }
            }

            // Alternate solution: register any kind of sensor on the device
            YSensor sensor = YSensor.FirstSensor();
            while (sensor != null) {
                if (sensor.get_module().get_serialNumber().equals(serial)) {
                    sensor.registerValueCallback(this);
                    sensor.registerTimedReportCallback(this);
                }
                sensor = sensor.nextSensor();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void yDeviceRemoval(YModule module)
    {
        pushEvent("Device removal : " + module);
    }

    @Override
    public void configChangeCallback(YModule module)
    {
        pushEvent("Config change : " + module);
    }

    @Override
    public void beaconCallback(YModule module, int beacon)
    {
        pushEvent(String.format(Locale.US, "Beacon changed to %d : %s", beacon, module.toString()));
    }


}
