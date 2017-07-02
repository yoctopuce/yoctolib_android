package com.yoctopuce.yocto_firmware;

import android.content.Context;
import android.util.Log;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;

public class YoctolibManager implements YAPI.LogCallback
{
    private static YoctolibManager __instance;
    private final Context _ctx;
    private int _refcount;

    public YoctolibManager(Context applicationContext)
    {
        _ctx = applicationContext;
        _refcount = 0;
    }

    public static YoctolibManager Get(Context ctx)
    {
        if (__instance == null) {
            __instance = new YoctolibManager(ctx.getApplicationContext());
        }
        return __instance;
    }


    public synchronized void StartUsage() throws YAPI_Exception
    {
        if (_refcount==0){
            YAPI.EnableUSBHost(_ctx);
            YAPI.RegisterLogFunction(this);
            YAPI.RegisterHub("usb");
        }
        _refcount++;
    }

    public synchronized void StopUsage()
    {
        _refcount--;
        if (_refcount == 0) {
            YAPI.FreeAPI();
        }
    }

    @Override
    public void yLog(String line)
    {
        Log.d("YAPI", line.trim());
    }


}
