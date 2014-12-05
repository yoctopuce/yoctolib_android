package com.yoctopuce.examples.yocto_relay;

import java.util.ArrayList;
import java.util.Iterator;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RelayListStorage
{
    public static final String PREF_HUB_HOSTNAME = "hub_hostname";

    private static final String TAG = "RelayListStorage";

    public static final String ACTION_RELAY_LIST_CHANGED = "com.yoctopuce.examples.yocto_relay.RelayListStorage.LIST_CHANGED";
    private static RelayListStorage sInstance = null;
    private ArrayList<Relay> mRelays;
    private int mPebbleCursor;
    private Relay mPebbleRelay;
    private Context mContext;

    private RelayListStorage(Context context)
    {
        Log.d(TAG, "init YoctoInterface");
        mRelays = new ArrayList<Relay>();
        mPebbleCursor = 0;
        mContext = context;

    }

    public static RelayListStorage get(Context context)
    {
        if (sInstance == null) {
            sInstance = new RelayListStorage(context.getApplicationContext());
        }
        return sInstance;
    }

    public static void release()
    {
        sInstance = null;
    }

    //public ArrayList<Relay> getAllRelays()
   // {
   //     return mRelays;
    //}

    public synchronized void removeRelayWithSerial(String serial)
    {
        Iterator<Relay> i = mRelays.iterator();
        while (i.hasNext()) {
            Relay r = i.next(); // must be called before i.remove
            if (r.getSerial().equals(serial)) {
                mRelays.remove(r);
                notifyChanges();
            }
            i.remove();
        }

    }

    public void notifyChanges()
    {
        mContext.sendBroadcast(new Intent(ACTION_RELAY_LIST_CHANGED));
        if(mPebbleRelay!=null){
            PebbleDictionary dictionary = mPebbleRelay.toPebbleDictionary();
            PebbleKit.sendDataToPebble(mContext, PebbleInterface.PEBBLE_APP_UUID, dictionary);
        }
    }


    
    public synchronized void addRelay(Relay newrelay)
    {
        for (Relay r : mRelays) {
            if (r.getHwId().equals(newrelay.getHwId())) {
                if (r.updateFromYRelay(newrelay)) {
                    notifyChanges();
                }
                return;
            }
        }
        notifyChanges();
        mRelays.add(newrelay);
    }

    public synchronized Relay getRelay(String hwid)
    {
        for (Relay r : mRelays) {
            if (r.getHwId().equals(hwid))
                return r;
        }
        return null;
    }

    public synchronized Relay getRelay(int pos)
    {
        if(mRelays.size()<=pos)
            return null;
        return mRelays.get(pos);
    }

    public synchronized int getRelayCount()
    {
        return mRelays.size();
    }


    
    
    public void pebblePrevious()
    {
        if (mPebbleCursor > 0)
            mPebbleCursor--;
        if (mRelays.size() > 0) {
            mPebbleRelay = mRelays.get(mPebbleCursor);
            return;
        }
        mPebbleRelay = null;
    }

    public void pebbleNext()
    {
        if (mRelays.size() == 0) {
            mPebbleRelay = null;
            return;
        }
        if (mPebbleCursor + 1 < mRelays.size())
            mPebbleCursor++;
        mPebbleRelay = mRelays.get(mPebbleCursor);
    }

    public Relay pebbleGetCurrent()
    {
        if(mPebbleRelay==null)
            pebblePrevious();
        return mPebbleRelay;
    }

}
