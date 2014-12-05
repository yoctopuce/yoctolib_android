package com.yoctopuce.examples.yocto_graph;

import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YDataSet;
import com.yoctopuce.YoctoAPI.YMeasure;
import com.yoctopuce.YoctoAPI.YSensor;

import org.achartengine.model.XYSeries;

import java.util.ArrayList;

public class ThreadSafeSensor {
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "SENSOR";
    private final String _fuctionId;
    private final String _serial;
    private String _displayName;
    private String _unit;
    private ArrayList<YMeasure> _measures=null;
    private double _lastValue = YSensor.CURRENTVALUE_INVALID;
    private double _resolution;
    private long _iresol;
    private boolean _loading = true;


    public ThreadSafeSensor(String serial, String functionId)
    {
        _serial = serial;
        _fuctionId = functionId;
    }




    public void updateFromYSensor(YSensor sensor) throws YAPI_Exception
    {
        _displayName = sensor.getFriendlyName();
        _unit = sensor.getUnit();
        _lastValue = sensor.get_currentValue();
        _resolution = sensor.get_resolution();
        _iresol = Math.round(1.0 / _resolution);
    }

    public void loadFromYSensorDatalogger(YSensor sensor) throws YAPI_Exception {
        //data loading
        YDataSet data = sensor.get_recordedData(0, 0);
        int progress = data.loadMore();
        while (progress < 100){
            progress = data.loadMore();
        }
        //transfer into an array
        synchronized(this){
            _measures = data.get_measures();
        }
    }

    public String getFuctionId()
    {
        return _fuctionId;
    }

    public String getSerial()
    {
        return _serial;
    }

    public  String getHwId()
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

    public synchronized ArrayList<YMeasure> getMeasures()
    {
        if (_measures != null) {
            return new ArrayList<YMeasure>(_measures);
        }
        return null;
    }


    public synchronized int fillGraphSerie(XYSeries serie, double timestart, double timestop)
    {
        int count = 0;
        if (_measures == null) {
            return count;
        }
        for (YMeasure m : _measures) {
            double end = m.get_endTimeUTC();
            if (end >= timestart && end < timestop) {
                //    double x = m.get_endTimeUTC() * 1000;
                double y = m.get_averageValue();
                serie.add(end * 1000, y);
                count++;
            }
        }
        return count;
    }




    public synchronized void addMeasure(YMeasure measure) {
        if (_measures == null) {
            _measures = new ArrayList<YMeasure>();
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

    public double getLastValue() {
        return _lastValue;
    }


    public boolean isLoading()
    {
        return _loading;
    }

    public void setLoading(boolean loading)
    {
        _loading = loading;
    }
}
