package com.yoctopuce.examples.yocto_graph;

import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YMeasure;
import com.yoctopuce.YoctoAPI.YSensor;

import org.achartengine.model.XYSeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ThreadSafeSensor
{
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "SENSOR";
    private final String _fuctionId;
    private final String _serial;
    private String _displayName;
    private String _unit;
    private LinkedList<YMeasure> _measures = null;
    private double _lastValue = YSensor.CURRENTVALUE_INVALID;
    private double _resolution;
    private long _iresol;
    private int _loadingProgress = 0;


    ThreadSafeSensor(String serial, String functionId)
    {
        _serial = serial;
        _fuctionId = functionId;
    }


    void updateFromYSensor(YSensor sensor) throws YAPI_Exception
    {
        _displayName = sensor.getFriendlyName();
        _unit = sensor.getUnit();
        _lastValue = sensor.get_currentValue();
        _resolution = sensor.get_resolution();
        _iresol = Math.round(1.0 / _resolution);
    }

    synchronized void setMeasures(ArrayList<YMeasure> measures)
    {
        _measures = new LinkedList<YMeasure>(measures);
    }

    public String getFuctionId()
    {
        return _fuctionId;
    }

    String getSerial()
    {
        return _serial;
    }

    String getHwId()
    {
        return _serial + "." + _fuctionId;
    }

    String getDisplayName()
    {
        return _displayName;
    }

    String getUnit()
    {
        return _unit;
    }

    public synchronized ArrayList<YMeasure> getMeasures()
    {
        if (_measures != null) {
            return new ArrayList<YMeasure>(_measures);
        }
        return null;
    }


    int fillGraphSerie(XYSeries serie, double timestart, double timestop)
    {
        List<YMeasure> myCopy;
        synchronized (this) {
            if (_measures == null) {
                return 0;
            }
            myCopy = new LinkedList<YMeasure>(_measures);

        }
        int count = 0;
        for (YMeasure m : myCopy) {
            double end = m.get_endTimeUTC();
            if (end >= timestart && end < timestop) {
                //    double x = m.get_endTimeUTC() * 1000;
                double y = m.get_averageValue();
                double x = end * 1000;
                //serie.add(x, y);
                count++;
            }
        }
        return count;
    }

    int fillGraphSerie(XYSeries serie, double timestart)
    {
        int count = 0;
        synchronized (this) {
            if (_measures == null) {
                return 0;
            }
            Iterator lit = _measures.descendingIterator();
            while (lit.hasNext()) {
                YMeasure m = (YMeasure) lit.next();
                double end = m.get_endTimeUTC();
                if (end < timestart) {
                    break;
                }
                //    double x = m.get_endTimeUTC() * 1000;
                double y = m.get_averageValue();
                double x = end * 1000;
                serie.add(x, y);
                count++;
            }
        }
        return count;
    }


    synchronized void addMeasure(YMeasure measure)
    {
        if (_measures == null) {
            _measures = new LinkedList<YMeasure>();
        }
        _measures.add(measure);
        double roundvalue = measure.get_averageValue() * _iresol;
        _lastValue = (double) Math.round(roundvalue) / _iresol;
    }

    @Override
    public String toString()
    {
        return _displayName + " =  " + Double.toString(_lastValue);
    }

    double getLastValue()
    {
        return _lastValue;
    }


    boolean isLoading()
    {
        return _loadingProgress != 100;
    }

    void setLoading(int loading)
    {
        _loadingProgress = loading;
    }

    int getLoading()
    {
        return _loadingProgress;
    }
}
