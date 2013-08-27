package com.yoctopuce.examples.yocto_relay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI.DeviceArrivalCallback;
import com.yoctopuce.YoctoAPI.YAPI.DeviceRemovalCallback;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YRelay;
import com.yoctopuce.YoctoAPI.YRelay.UpdateCallback;

public class YoctoService extends Service implements DeviceArrivalCallback, DeviceRemovalCallback, UpdateCallback
{
    private static final String TAG = "com.yoctopuce.examples.yocto_relay.YoctoService";
    public static final String EXTRA_NEWHUB = "com.yoctopuce.examples.yocto_relay.YoctoService.NEWHUB";
    public static final String EXTRA_REFRESH = "com.yoctopuce.examples.yocto_relay.YoctoService.REFRESH";
    public static final String EXTRA_TOGGLE = "com.yoctopuce.examples.yocto_relay.YoctoService.TOGGLE";
    private static final long REFRESH_INTERVAL = 1000;
    private String mHubHostName;
    private RelayListStorage mYoctoSingelton;
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            onHandleIntent((Intent) msg.obj);
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + TAG + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        Log.d(TAG, "init YoctoInterface");
        mYoctoSingelton = RelayListStorage.get(this);
        try {
            YAPI.EnableUSBHost(getApplicationContext());
        } catch (YAPI_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        YAPI.InitAPI(0);
        YAPI.RegisterDeviceArrivalCallback(this);
        YAPI.RegisterDeviceRemovalCallback(this);
        mHubHostName = PreferenceManager.getDefaultSharedPreferences(this).getString(RelayListStorage.PREF_HUB_HOSTNAME, "usb");
        try {
            YAPI.PreregisterHub(mHubHostName);
        } catch (YAPI_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        onStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mServiceLooper.quit();
        Log.d(TAG, "free YoctoInterface");
        YAPI.FreeAPI();
    }

    protected void onHandleIntent(Intent intent)
    {

        if(intent==null) {
            return;
        }
        String hwid = intent.getStringExtra(EXTRA_TOGGLE);
        if (hwid != null) {
            Log.d(TAG, "Toggle " + hwid);
            Relay relay = mYoctoSingelton.getRelay(hwid);
            if (relay != null) {
                YRelay yrelay = relay.getYRelay();
                try {
                    yrelay.set_output(relay.isOn() ? YRelay.OUTPUT_ON : YRelay.OUTPUT_OFF);
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
            }
        }
        String newhub = intent.getStringExtra(EXTRA_NEWHUB);
        if (newhub != null) {
            Log.d(TAG, "Change hub from" + mHubHostName + " to " + newhub);
            YAPI.UnregisterHub(mHubHostName);
            mHubHostName = newhub;
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(RelayListStorage.PREF_HUB_HOSTNAME, mHubHostName).commit();
            try {
                YAPI.RegisterHub(newhub);
            } catch (YAPI_Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

        }
        try {
            YAPI.UpdateDeviceList();
            YAPI.HandleEvents();
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void yDeviceRemoval(YModule module)
    {
        String serial;
        Log.d(TAG, "Device removal: " + module);
        try {
            serial = module.get_serialNumber();
        } catch (YAPI_Exception e) {
            return;
        }
        mYoctoSingelton.removeRelayWithSerial(serial);

    }

    @Override
    public void yDeviceArrival(YModule module)
    {
        try {
            Log.d(TAG, "Device arrival: " + module);
            int fctcount = module.functionCount();
            String fctName, fctFullName;

            for (int i = 0; i < fctcount; i++) {
                fctName = module.functionId(i);
                fctFullName = module.get_serialNumber() + "." + fctName;

                // register call back for anbuttons
                if (fctName.startsWith("relay")) {
                    YRelay r = YRelay.FindRelay(fctFullName);
                    mYoctoSingelton.addRelay(new Relay(r));
                    r.registerValueCallback(this);
                }
            }
        } catch (YAPI_Exception ex) {
            System.out.println("Device access error : " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void yNewValue(YRelay yrelay, String functionValue)
    {
        try {
            Log.d(TAG, "New valelue "+functionValue+" for : " + yrelay);
            mYoctoSingelton.addRelay(new Relay(yrelay));
        } catch (YAPI_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent i = new Intent(context, YoctoService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), REFRESH_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context)
    {
        Intent i = new Intent(context, YoctoService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

}
