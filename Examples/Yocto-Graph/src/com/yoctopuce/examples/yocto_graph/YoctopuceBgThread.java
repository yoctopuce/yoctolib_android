package com.yoctopuce.examples.yocto_graph;

import android.content.Context;
import android.content.Intent;
//import android.util.Log;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YDataLogger;
import com.yoctopuce.YoctoAPI.YMeasure;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YSensor;

import java.util.ArrayList;

public class YoctopuceBgThread  implements Runnable, YAPI.DeviceArrivalCallback, YAPI.DeviceRemovalCallback, YSensor.TimedReportCallback {
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "YoctpuceBgThread";
    public static final String ACTION_SENSOR_LIST_CHANGED = "ACTION_SENSOR_LIST_CHANGED";
    public static final String ACTION_SENSOR_NEW_VALUE = "ACTION_SENSOR_NEW_VALUE";
    public static final String EXTRA_HWID = "HWID";
    // Static variable to handle reference counting
    private static YoctopuceBgThread sInstance;
    private static int sRefCounter=0;
    private static long sLastStop;
    // application context used for Yoctopuce API and messaging
    private final Context _appcontext;
    private ArrayList<String> _hubs;
    private long _lastUpdate;

    synchronized public static YoctopuceBgThread Start(Context context)
    {
        if (sInstance == null) {
            sInstance = new YoctopuceBgThread(context.getApplicationContext());
        }
        sRefCounter++;

        return sInstance;
    }

    synchronized public static void Stop()
    {
        sRefCounter--;
        sLastStop = System.currentTimeMillis();
    }


    synchronized static boolean stillRunInBG()
    {
        return sRefCounter > 0 || (System.currentTimeMillis() - sLastStop) < 5000;
    }



    public YoctopuceBgThread(Context applicationContext)
    {
        _appcontext = applicationContext;
        Thread thread = new Thread(this);
        thread.start();
        _hubs = new ArrayList<String>();
        _hubs.add("usb");

    }


    @Override
    public void run()
    {
        try {
            YAPI.EnableUSBHost(_appcontext);
            YAPI.InitAPI(0);
            YAPI.RegisterDeviceArrivalCallback(this);
            YAPI.RegisterDeviceRemovalCallback(this);
            ArrayList<String> hubs = getHubs();
            for (String hub : hubs) {
                YAPI.RegisterHub(hub);
            }
        } catch (YAPI_Exception e) {
            //Log.e(TAG, "YAPI_Exception:" + e.getLocalizedMessage());
            e.printStackTrace();
            YAPI.FreeAPI();
            sRefCounter = 0;
            sInstance = null;
            return;
        }

        while(YoctopuceBgThread.stillRunInBG()) {
            try {
                YAPI.UpdateDeviceList();
                YAPI.Sleep(1000);
            } catch (YAPI_Exception e) {
                //Log.e(TAG, "YAPI_Exception:" + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        YAPI.FreeAPI();
        SensorStorage.get().clearAll();
        //Log.i(TAG, "Thread exited normally and clear all instance");
        sInstance = null;
    }

    @Override
    public void yDeviceArrival(YModule module)
    {
        try {
            String serial = module.get_serialNumber();
            //Log.d(TAG,"DeviceArrival:"+serial);
            //activate datalogger;
            YDataLogger dataLogger = YDataLogger.FindDataLogger(serial + ".dataLogger");


            // Alternate solution: register any kind of ysensor on the device
            YSensor ysensor = YSensor.FirstSensor();
            while (ysensor != null) {
                if (ysensor.get_module().get_serialNumber().equals(serial)) {
                    String functionId = ysensor.get_functionId();
                    //Log.d(TAG, "- " + functionId);
                    ThreadSafeSensor sens = new ThreadSafeSensor(serial, functionId);
                    SensorStorage.get().add(sens);
                    _appcontext.sendBroadcast(new Intent(ACTION_SENSOR_LIST_CHANGED));
                    sens.updateFromYSensor(ysensor);
                    sens.loadFromYSensorDatalogger(ysensor);
                    ysensor.set_reportFrequency("60/m");
                    ysensor.set_logFrequency("60/m");
                    ysensor.registerTimedReportCallback(this);
                    sens.setLoading(false);
                    _appcontext.sendBroadcast(new Intent(ACTION_SENSOR_LIST_CHANGED));
                }
                ysensor = ysensor.nextSensor();
            }
            if (dataLogger.isOnline()) {
                if (dataLogger.get_autoStart()!= YDataLogger.AUTOSTART_ON){
                    // we have datalogger
                    dataLogger.set_autoStart(YDataLogger.AUTOSTART_ON);
                    dataLogger.set_recording(YDataLogger.RECORDING_ON);
                }
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void yDeviceRemoval(YModule module)
    {
        try {
            String serial = module.get_serialNumber();
            //Log.d(TAG,"DeviceRemoval:" + serial);
            SensorStorage.get().removeAll(serial);
            _appcontext.sendBroadcast(new Intent(ACTION_SENSOR_LIST_CHANGED));
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getHubs()
    {
        return _hubs;
    }

    @Override
    public void timedReportCallback(YSensor sensor, YMeasure measure)
    {
        try {
            String hwid = sensor.getHardwareId();
            //Log.d(TAG,"New measure for" + hwid+":"+measure.get_averageValue());
            ThreadSafeSensor graph = SensorStorage.get().get(hwid);
            graph.addMeasure(measure);
            if (System.currentTimeMillis()-_lastUpdate > 500){
                Intent intent = new Intent(ACTION_SENSOR_NEW_VALUE);
                intent.putExtra(EXTRA_HWID, hwid);
                _appcontext.sendBroadcast(intent);
                _lastUpdate = System.currentTimeMillis();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }
}
