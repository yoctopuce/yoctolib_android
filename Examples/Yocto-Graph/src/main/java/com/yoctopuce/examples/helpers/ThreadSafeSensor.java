package com.yoctopuce.examples.helpers;

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


    public void updateValues(String displayName, String unit, double lastValue, double resolution)
    {
        _displayName = displayName;
        _unit = unit;
        _lastValue = lastValue;
        _resolution = resolution;
        _iresol = Math.round(1.0 / _resolution);

    }


    synchronized void setMeasures(ArrayList<YMeasure> measures)
    {
        _measures = new LinkedList<>(measures);
    }

    String getSerial()
    {
        return _serial;
    }

    public String getHwId()
    {
        return _serial + "." + _fuctionId;
    }

    public String getDisplayName()
    {
        return _displayName;
    }

    public String getUnit()
    {
        return _unit;
    }


    public int fillGraphSerie(XYSeries serie, double timestart, double timestop)
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

    public int fillGraphSerie(XYSeries serie, double timestart)
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
            _measures = new LinkedList<>();
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

    public double getLastValue()
    {
        return _lastValue;
    }


    public boolean isLoading()
    {
        return _loadingProgress != 100;
    }

    void setLoading(int loading)
    {
        _loadingProgress = loading;
    }

    public int getLoading()
    {
        return _loadingProgress;
    }

}
