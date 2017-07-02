package com.yoctopuce.examples.yocto_graph;

import java.util.ArrayList;
import java.util.Iterator;

public class SensorStorage {

    private static SensorStorage sInstance = null;
    private ArrayList<ThreadSafeSensor> _sensors = new ArrayList<ThreadSafeSensor>();


    public static SensorStorage get()
    {
        if (sInstance == null) {
            sInstance = new SensorStorage();
        }
        return sInstance;
    }


    public synchronized void add(ThreadSafeSensor sensor)
    {
        _sensors.add(sensor);
    }


    public synchronized ArrayList<ThreadSafeSensor> getSensorList()
    {
        return new ArrayList<ThreadSafeSensor>(_sensors);
    }


    public synchronized void removeAll(String serial)
    {
        Iterator<ThreadSafeSensor> iterator = _sensors.iterator();
        while ( iterator.hasNext() ) {
            ThreadSafeSensor next = iterator.next();
            if ( serial.equals( next.getSerial() ) ){
                iterator.remove();
            }
        }
    }

    public synchronized ThreadSafeSensor get(String hwid)
    {
        for (ThreadSafeSensor next : _sensors) {
            if (hwid.equals(next.getHwId())) {
                return next;
            }
        }
        return null;

    }

    public synchronized void clearAll()
    {
        _sensors.clear();
    }
}
