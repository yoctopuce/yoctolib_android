/*
 *
 *  $Id: svn_id $
 *
 *  Implements FindTemperature(), the high-level API for Temperature functions
 *
 *  - - - - - - - - - License information: - - - - - - - - -
 *
 *  Copyright (C) 2011 and beyond by Yoctopuce Sarl, Switzerland.
 *
 *  Yoctopuce Sarl (hereafter Licensor) grants to you a perpetual
 *  non-exclusive license to use, modify, copy and integrate this
 *  file into your software for the sole purpose of interfacing
 *  with Yoctopuce products.
 *
 *  You may reproduce and distribute copies of this file in
 *  source or object form, as long as the sole purpose of this
 *  code is to interface with Yoctopuce products. You must retain
 *  this notice in the distributed source file.
 *
 *  You should refer to Yoctopuce General Terms and Conditions
 *  for additional information regarding your rights and
 *  obligations.
 *
 *  THE SOFTWARE AND DOCUMENTATION ARE PROVIDED 'AS IS' WITHOUT
 *  WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING
 *  WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO
 *  EVENT SHALL LICENSOR BE LIABLE FOR ANY INCIDENTAL, SPECIAL,
 *  INDIRECT OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA,
 *  COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY OR
 *  SERVICES, ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT
 *  LIMITED TO ANY DEFENSE THEREOF), ANY CLAIMS FOR INDEMNITY OR
 *  CONTRIBUTION, OR OTHER SIMILAR COSTS, WHETHER ASSERTED ON THE
 *  BASIS OF CONTRACT, TORT (INCLUDING NEGLIGENCE), BREACH OF
 *  WARRANTY, OR OTHERWISE.
 *
 */

package com.yoctopuce.YoctoAPI;
import java.util.ArrayList;
import java.util.Locale;

//--- (YTemperature return codes)
//--- (end of YTemperature return codes)
//--- (YTemperature yapiwrapper)
//--- (end of YTemperature yapiwrapper)
//--- (YTemperature class start)
/**
 *  YTemperature Class: temperature sensor control interface, available for instance in the
 * Yocto-Meteo-V2, the Yocto-PT100, the Yocto-Temperature or the Yocto-Thermocouple
 *
 * The YTemperature class allows you to read and configure Yoctopuce temperature sensors.
 * It inherits from YSensor class the core functions to read measurements,
 * to register callback functions, and to access the autonomous datalogger.
 * This class adds the ability to configure some specific parameters
 * for some sensors (connection type, temperature mapping table).
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YTemperature extends YSensor
{
//--- (end of YTemperature class start)
//--- (YTemperature definitions)
    /**
     * invalid sensorType value
     */
    public static final int SENSORTYPE_DIGITAL = 0;
    public static final int SENSORTYPE_TYPE_K = 1;
    public static final int SENSORTYPE_TYPE_E = 2;
    public static final int SENSORTYPE_TYPE_J = 3;
    public static final int SENSORTYPE_TYPE_N = 4;
    public static final int SENSORTYPE_TYPE_R = 5;
    public static final int SENSORTYPE_TYPE_S = 6;
    public static final int SENSORTYPE_TYPE_T = 7;
    public static final int SENSORTYPE_PT100_4WIRES = 8;
    public static final int SENSORTYPE_PT100_3WIRES = 9;
    public static final int SENSORTYPE_PT100_2WIRES = 10;
    public static final int SENSORTYPE_RES_OHM = 11;
    public static final int SENSORTYPE_RES_NTC = 12;
    public static final int SENSORTYPE_RES_LINEAR = 13;
    public static final int SENSORTYPE_RES_INTERNAL = 14;
    public static final int SENSORTYPE_IR = 15;
    public static final int SENSORTYPE_RES_PT1000 = 16;
    public static final int SENSORTYPE_CHANNEL_OFF = 17;
    public static final int SENSORTYPE_INVALID = -1;
    /**
     * invalid signalValue value
     */
    public static final double SIGNALVALUE_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid signalUnit value
     */
    public static final String SIGNALUNIT_INVALID = YAPI.INVALID_STRING;
    /**
     * invalid command value
     */
    public static final String COMMAND_INVALID = YAPI.INVALID_STRING;
    protected int _sensorType = SENSORTYPE_INVALID;
    protected double _signalValue = SIGNALVALUE_INVALID;
    protected String _signalUnit = SIGNALUNIT_INVALID;
    protected String _command = COMMAND_INVALID;
    protected UpdateCallback _valueCallbackTemperature = null;
    protected TimedReportCallback _timedReportCallbackTemperature = null;

    /**
     * Deprecated UpdateCallback for Temperature
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YTemperature function, String functionValue);
    }

    /**
     * TimedReportCallback for Temperature
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YTemperature  function, YMeasure measure);
    }
    //--- (end of YTemperature definitions)


    /**
     *
     * @param func : functionid
     */
    protected YTemperature(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "Temperature";
        //--- (YTemperature attributes initialization)
        //--- (end of YTemperature attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YTemperature(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (YTemperature implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("sensorType")) {
            _sensorType = json_val.getInt("sensorType");
        }
        if (json_val.has("signalValue")) {
            _signalValue = Math.round(json_val.getDouble("signalValue") / 65.536) / 1000.0;
        }
        if (json_val.has("signalUnit")) {
            _signalUnit = json_val.getString("signalUnit");
        }
        if (json_val.has("command")) {
            _command = json_val.getString("command");
        }
        super._parseAttr(json_val);
    }

    /**
     * Changes the measuring unit for the measured temperature. That unit is a string.
     * If that strings end with the letter F all temperatures values will returned in
     * Fahrenheit degrees. If that String ends with the letter K all values will be
     * returned in Kelvin degrees. If that string ends with the letter C all values will be
     * returned in Celsius degrees.  If the string ends with any other character the
     * change will be ignored. Remember to call the
     * saveToFlash() method of the module if the modification must be kept.
     * WARNING: if a specific calibration is defined for the temperature function, a
     * unit system change will probably break it.
     *
     * @param newval : a string corresponding to the measuring unit for the measured temperature
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_unit(String  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = newval;
            _setAttr("unit",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the measuring unit for the measured temperature. That unit is a string.
     * If that strings end with the letter F all temperatures values will returned in
     * Fahrenheit degrees. If that String ends with the letter K all values will be
     * returned in Kelvin degrees. If that string ends with the letter C all values will be
     * returned in Celsius degrees.  If the string ends with any other character the
     * change will be ignored. Remember to call the
     * saveToFlash() method of the module if the modification must be kept.
     * WARNING: if a specific calibration is defined for the temperature function, a
     * unit system change will probably break it.
     *
     * @param newval : a string corresponding to the measuring unit for the measured temperature
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setUnit(String newval)  throws YAPI_Exception
    {
        return set_unit(newval);
    }

    /**
     * Returns the temperature sensor type.
     *
     *  @return a value among YTemperature.SENSORTYPE_DIGITAL, YTemperature.SENSORTYPE_TYPE_K,
     *  YTemperature.SENSORTYPE_TYPE_E, YTemperature.SENSORTYPE_TYPE_J, YTemperature.SENSORTYPE_TYPE_N,
     *  YTemperature.SENSORTYPE_TYPE_R, YTemperature.SENSORTYPE_TYPE_S, YTemperature.SENSORTYPE_TYPE_T,
     *  YTemperature.SENSORTYPE_PT100_4WIRES, YTemperature.SENSORTYPE_PT100_3WIRES,
     *  YTemperature.SENSORTYPE_PT100_2WIRES, YTemperature.SENSORTYPE_RES_OHM,
     *  YTemperature.SENSORTYPE_RES_NTC, YTemperature.SENSORTYPE_RES_LINEAR,
     *  YTemperature.SENSORTYPE_RES_INTERNAL, YTemperature.SENSORTYPE_IR,
     *  YTemperature.SENSORTYPE_RES_PT1000 and YTemperature.SENSORTYPE_CHANNEL_OFF corresponding to the
     * temperature sensor type
     *
     * @throws YAPI_Exception on error
     */
    public int get_sensorType() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SENSORTYPE_INVALID;
                }
            }
            res = _sensorType;
        }
        return res;
    }

    /**
     * Returns the temperature sensor type.
     *
     *  @return a value among YTemperature.SENSORTYPE_DIGITAL, YTemperature.SENSORTYPE_TYPE_K,
     *  YTemperature.SENSORTYPE_TYPE_E, YTemperature.SENSORTYPE_TYPE_J, YTemperature.SENSORTYPE_TYPE_N,
     *  YTemperature.SENSORTYPE_TYPE_R, YTemperature.SENSORTYPE_TYPE_S, YTemperature.SENSORTYPE_TYPE_T,
     *  YTemperature.SENSORTYPE_PT100_4WIRES, YTemperature.SENSORTYPE_PT100_3WIRES,
     *  YTemperature.SENSORTYPE_PT100_2WIRES, YTemperature.SENSORTYPE_RES_OHM,
     *  YTemperature.SENSORTYPE_RES_NTC, YTemperature.SENSORTYPE_RES_LINEAR,
     *  YTemperature.SENSORTYPE_RES_INTERNAL, YTemperature.SENSORTYPE_IR,
     *  YTemperature.SENSORTYPE_RES_PT1000 and YTemperature.SENSORTYPE_CHANNEL_OFF corresponding to the
     * temperature sensor type
     *
     * @throws YAPI_Exception on error
     */
    public int getSensorType() throws YAPI_Exception
    {
        return get_sensorType();
    }

    /**
     * Changes the temperature sensor type.  This function is used
     * to define the type of thermocouple (K,E...) used with the device.
     * It has no effect if module is using a digital sensor or a thermistor.
     * Remember to call the saveToFlash() method of the module if the
     * modification must be kept.
     *
     *  @param newval : a value among YTemperature.SENSORTYPE_DIGITAL, YTemperature.SENSORTYPE_TYPE_K,
     *  YTemperature.SENSORTYPE_TYPE_E, YTemperature.SENSORTYPE_TYPE_J, YTemperature.SENSORTYPE_TYPE_N,
     *  YTemperature.SENSORTYPE_TYPE_R, YTemperature.SENSORTYPE_TYPE_S, YTemperature.SENSORTYPE_TYPE_T,
     *  YTemperature.SENSORTYPE_PT100_4WIRES, YTemperature.SENSORTYPE_PT100_3WIRES,
     *  YTemperature.SENSORTYPE_PT100_2WIRES, YTemperature.SENSORTYPE_RES_OHM,
     *  YTemperature.SENSORTYPE_RES_NTC, YTemperature.SENSORTYPE_RES_LINEAR,
     *  YTemperature.SENSORTYPE_RES_INTERNAL, YTemperature.SENSORTYPE_IR,
     *  YTemperature.SENSORTYPE_RES_PT1000 and YTemperature.SENSORTYPE_CHANNEL_OFF corresponding to the
     * temperature sensor type
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_sensorType(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("sensorType",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the temperature sensor type.  This function is used
     * to define the type of thermocouple (K,E...) used with the device.
     * It has no effect if module is using a digital sensor or a thermistor.
     * Remember to call the saveToFlash() method of the module if the
     * modification must be kept.
     *
     *  @param newval : a value among YTemperature.SENSORTYPE_DIGITAL, YTemperature.SENSORTYPE_TYPE_K,
     *  YTemperature.SENSORTYPE_TYPE_E, YTemperature.SENSORTYPE_TYPE_J, YTemperature.SENSORTYPE_TYPE_N,
     *  YTemperature.SENSORTYPE_TYPE_R, YTemperature.SENSORTYPE_TYPE_S, YTemperature.SENSORTYPE_TYPE_T,
     *  YTemperature.SENSORTYPE_PT100_4WIRES, YTemperature.SENSORTYPE_PT100_3WIRES,
     *  YTemperature.SENSORTYPE_PT100_2WIRES, YTemperature.SENSORTYPE_RES_OHM,
     *  YTemperature.SENSORTYPE_RES_NTC, YTemperature.SENSORTYPE_RES_LINEAR,
     *  YTemperature.SENSORTYPE_RES_INTERNAL, YTemperature.SENSORTYPE_IR,
     *  YTemperature.SENSORTYPE_RES_PT1000 and YTemperature.SENSORTYPE_CHANNEL_OFF corresponding to the
     * temperature sensor type
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setSensorType(int newval)  throws YAPI_Exception
    {
        return set_sensorType(newval);
    }

    /**
     * Returns the current value of the electrical signal measured by the sensor.
     *
     *  @return a floating point number corresponding to the current value of the electrical signal
     * measured by the sensor
     *
     * @throws YAPI_Exception on error
     */
    public double get_signalValue() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SIGNALVALUE_INVALID;
                }
            }
            res = (double)Math.round(_signalValue * 1000) / 1000;
        }
        return res;
    }

    /**
     * Returns the current value of the electrical signal measured by the sensor.
     *
     *  @return a floating point number corresponding to the current value of the electrical signal
     * measured by the sensor
     *
     * @throws YAPI_Exception on error
     */
    public double getSignalValue() throws YAPI_Exception
    {
        return get_signalValue();
    }

    /**
     * Returns the measuring unit of the electrical signal used by the sensor.
     *
     * @return a string corresponding to the measuring unit of the electrical signal used by the sensor
     *
     * @throws YAPI_Exception on error
     */
    public String get_signalUnit() throws YAPI_Exception
    {
        String res;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SIGNALUNIT_INVALID;
                }
            }
            res = _signalUnit;
        }
        return res;
    }

    /**
     * Returns the measuring unit of the electrical signal used by the sensor.
     *
     * @return a string corresponding to the measuring unit of the electrical signal used by the sensor
     *
     * @throws YAPI_Exception on error
     */
    public String getSignalUnit() throws YAPI_Exception
    {
        return get_signalUnit();
    }

    public String get_command() throws YAPI_Exception
    {
        String res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return COMMAND_INVALID;
                }
            }
            res = _command;
        }
        return res;
    }

    public int set_command(String  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = newval;
            _setAttr("command",rest_val);
        }
        return YAPI.SUCCESS;
    }


    /**
     * Retrieves a temperature sensor for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the temperature sensor is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YTemperature.isOnline() to test if the temperature sensor is
     * indeed online at a given time. In case of ambiguity when looking for
     * a temperature sensor by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the temperature sensor, for instance
     *         METEOMK2.temperature.
     *
     * @return a YTemperature object allowing you to drive the temperature sensor.
     */
    public static YTemperature FindTemperature(String func)
    {
        YTemperature obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YTemperature) YFunction._FindFromCache("Temperature", func);
            if (obj == null) {
                obj = new YTemperature(func);
                YFunction._AddToCache("Temperature", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a temperature sensor for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the temperature sensor is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YTemperature.isOnline() to test if the temperature sensor is
     * indeed online at a given time. In case of ambiguity when looking for
     * a temperature sensor by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the temperature sensor, for instance
     *         METEOMK2.temperature.
     *
     * @return a YTemperature object allowing you to drive the temperature sensor.
     */
    public static YTemperature FindTemperatureInContext(YAPIContext yctx,String func)
    {
        YTemperature obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YTemperature) YFunction._FindFromCacheInContext(yctx, "Temperature", func);
            if (obj == null) {
                obj = new YTemperature(yctx, func);
                YFunction._AddToCache("Temperature", func, obj);
            }
        }
        return obj;
    }

    /**
     * Registers the callback function that is invoked on every change of advertised value.
     * The callback is invoked only during the execution of ySleep or yHandleEvents.
     * This provides control over the time when the callback is triggered. For good responsiveness, remember to call
     * one of these two functions periodically. To unregister a callback, pass a null pointer as argument.
     *
     * @param callback : the callback function to call, or a null pointer. The callback function should take two
     *         arguments: the function object of which the value has changed, and the character string describing
     *         the new advertised value.
     *
     */
    public int registerValueCallback(UpdateCallback callback)
    {
        String val;
        if (callback != null) {
            YFunction._UpdateValueCallbackList(this, true);
        } else {
            YFunction._UpdateValueCallbackList(this, false);
        }
        _valueCallbackTemperature = callback;
        // Immediately invoke value callback with current value
        if (callback != null && isOnline()) {
            val = _advertisedValue;
            if (!(val.equals(""))) {
                _invokeValueCallback(val);
            }
        }
        return 0;
    }

    @Override
    public int _invokeValueCallback(String value)
    {
        if (_valueCallbackTemperature != null) {
            _valueCallbackTemperature.yNewValue(this, value);
        } else {
            super._invokeValueCallback(value);
        }
        return 0;
    }

    /**
     * Registers the callback function that is invoked on every periodic timed notification.
     * The callback is invoked only during the execution of ySleep or yHandleEvents.
     * This provides control over the time when the callback is triggered. For good responsiveness, remember to call
     * one of these two functions periodically. To unregister a callback, pass a null pointer as argument.
     *
     * @param callback : the callback function to call, or a null pointer. The callback function should take two
     *         arguments: the function object of which the value has changed, and an YMeasure object describing
     *         the new advertised value.
     *
     */
    public int registerTimedReportCallback(TimedReportCallback callback)
    {
        YSensor sensor;
        sensor = this;
        if (callback != null) {
            YFunction._UpdateTimedReportCallbackList(sensor, true);
        } else {
            YFunction._UpdateTimedReportCallbackList(sensor, false);
        }
        _timedReportCallbackTemperature = callback;
        return 0;
    }

    @Override
    public int _invokeTimedReportCallback(YMeasure value)
    {
        if (_timedReportCallbackTemperature != null) {
            _timedReportCallbackTemperature.timedReportCallback(this, value);
        } else {
            super._invokeTimedReportCallback(value);
        }
        return 0;
    }

    /**
     * Configures NTC thermistor parameters in order to properly compute the temperature from
     * the measured resistance. For increased precision, you can enter a complete mapping
     * table using set_thermistorResponseTable. This function can only be used with a
     * temperature sensor based on thermistors.
     *
     * @param res25 : thermistor resistance at 25 degrees Celsius
     * @param beta : Beta value
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_ntcParameters(double res25,double beta) throws YAPI_Exception
    {
        double t0;
        double t1;
        double res100;
        ArrayList<Double> tempValues = new ArrayList<>();
        ArrayList<Double> resValues = new ArrayList<>();
        t0 = 25.0+273.15;
        t1 = 100.0+273.15;
        res100 = res25 * java.lang.Math.exp(beta*(1.0/t1 - 1.0/t0));
        tempValues.clear();
        resValues.clear();
        tempValues.add(25.0);
        resValues.add(res25);
        tempValues.add(100.0);
        resValues.add(res100);
        return set_thermistorResponseTable(tempValues, resValues);
    }

    /**
     * Records a thermistor response table, in order to interpolate the temperature from
     * the measured resistance. This function can only be used with a temperature
     * sensor based on thermistors.
     *
     * @param tempValues : array of floating point numbers, corresponding to all
     *         temperatures (in degrees Celsius) for which the resistance of the
     *         thermistor is specified.
     * @param resValues : array of floating point numbers, corresponding to the resistance
     *         values (in Ohms) for each of the temperature included in the first
     *         argument, index by index.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_thermistorResponseTable(ArrayList<Double> tempValues,ArrayList<Double> resValues) throws YAPI_Exception
    {
        int siz;
        int res;
        int idx;
        int found;
        double prev;
        double curr;
        double currTemp;
        double idxres;
        siz = tempValues.size();
        //noinspection DoubleNegation
        if (!(siz >= 2)) { throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "thermistor response table must have at least two points");}
        //noinspection DoubleNegation
        if (!(siz == resValues.size())) { throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "table sizes mismatch");}

        res = set_command("Z");
        //noinspection DoubleNegation
        if (!(res==YAPI.SUCCESS)) { throw new YAPI_Exception(YAPI.IO_ERROR, "unable to reset thermistor parameters");}
        // add records in growing resistance value
        found = 1;
        prev = 0.0;
        while (found > 0) {
            found = 0;
            curr = 99999999.0;
            currTemp = -999999.0;
            idx = 0;
            while (idx < siz) {
                idxres = resValues.get(idx).doubleValue();
                if ((idxres > prev) && (idxres < curr)) {
                    curr = idxres;
                    currTemp = tempValues.get(idx).doubleValue();
                    found = 1;
                }
                idx = idx + 1;
            }
            if (found > 0) {
                res = set_command(String.format(Locale.US, "m%d:%d",(int) (double)Math.round(1000*curr),(int) (double)Math.round(1000*currTemp)));
                //noinspection DoubleNegation
                if (!(res==YAPI.SUCCESS)) { throw new YAPI_Exception(YAPI.IO_ERROR, "unable to reset thermistor parameters");}
                prev = curr;
            }
        }
        return YAPI.SUCCESS;
    }

    /**
     * Retrieves the thermistor response table previously configured using the
     * set_thermistorResponseTable function. This function can only be used with a
     * temperature sensor based on thermistors.
     *
     * @param tempValues : array of floating point numbers, that is filled by the function
     *         with all temperatures (in degrees Celsius) for which the resistance
     *         of the thermistor is specified.
     * @param resValues : array of floating point numbers, that is filled by the function
     *         with the value (in Ohms) for each of the temperature included in the
     *         first argument, index by index.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int loadThermistorResponseTable(ArrayList<Double> tempValues,ArrayList<Double> resValues) throws YAPI_Exception
    {
        String id;
        byte[] bin_json = new byte[0];
        ArrayList<byte[]> paramlist = new ArrayList<>();
        ArrayList<Double> templist = new ArrayList<>();
        int siz;
        int idx;
        double temp;
        int found;
        double prev;
        double curr;
        double currRes;
        tempValues.clear();
        resValues.clear();

        id = get_functionId();
        id = (id).substring(11, 11 + id.length() - 11);
        if (id.equals("")) {
            id = "1";
        }
        bin_json = _download(String.format(Locale.US, "extra.json?page=%s",id));
        paramlist = _json_get_array(bin_json);
        // first convert all temperatures to float
        siz = (paramlist.size() >> 1);
        templist.clear();
        idx = 0;
        while (idx < siz) {
            temp = YAPI.ystr2float(new String(paramlist.get(2*idx+1), _yapi._deviceCharset))/1000.0;
            templist.add(temp);
            idx = idx + 1;
        }
        // then add records in growing temperature value
        tempValues.clear();
        resValues.clear();
        found = 1;
        prev = -999999.0;
        while (found > 0) {
            found = 0;
            curr = 999999.0;
            currRes = -999999.0;
            idx = 0;
            while (idx < siz) {
                temp = templist.get(idx).doubleValue();
                if ((temp > prev) && (temp < curr)) {
                    curr = temp;
                    currRes = YAPI.ystr2float(new String(paramlist.get(2*idx), _yapi._deviceCharset))/1000.0;
                    found = 1;
                }
                idx = idx + 1;
            }
            if (found > 0) {
                tempValues.add(curr);
                resValues.add(currRes);
                prev = curr;
            }
        }
        return YAPI.SUCCESS;
    }

    /**
     * Continues the enumeration of temperature sensors started using yFirstTemperature().
     * Caution: You can't make any assumption about the returned temperature sensors order.
     * If you want to find a specific a temperature sensor, use Temperature.findTemperature()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YTemperature object, corresponding to
     *         a temperature sensor currently online, or a null pointer
     *         if there are no more temperature sensors to enumerate.
     */
    public YTemperature nextTemperature()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindTemperatureInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of temperature sensors currently accessible.
     * Use the method YTemperature.nextTemperature() to iterate on
     * next temperature sensors.
     *
     * @return a pointer to a YTemperature object, corresponding to
     *         the first temperature sensor currently online, or a null pointer
     *         if there are none.
     */
    public static YTemperature FirstTemperature()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("Temperature");
        if (next_hwid == null)  return null;
        return FindTemperatureInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of temperature sensors currently accessible.
     * Use the method YTemperature.nextTemperature() to iterate on
     * next temperature sensors.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YTemperature object, corresponding to
     *         the first temperature sensor currently online, or a null pointer
     *         if there are none.
     */
    public static YTemperature FirstTemperatureInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("Temperature");
        if (next_hwid == null)  return null;
        return FindTemperatureInContext(yctx, next_hwid);
    }

    //--- (end of YTemperature implementation)
}

