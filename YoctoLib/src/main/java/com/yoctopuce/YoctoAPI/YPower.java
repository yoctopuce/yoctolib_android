/*
 *
 *  $Id: svn_id $
 *
 *  Implements FindPower(), the high-level API for Power functions
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

//--- (YPower return codes)
//--- (end of YPower return codes)
//--- (YPower yapiwrapper)
//--- (end of YPower yapiwrapper)
//--- (YPower class start)
/**
 * YPower Class: electrical power sensor control interface, available for instance in the Yocto-Watt
 *
 * The YPower class allows you to read and configure Yoctopuce electrical power sensors.
 * It inherits from YSensor class the core functions to read measurements,
 * to register callback functions, and to access the autonomous datalogger.
 * This class adds the ability to access the energy counter and the power factor.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YPower extends YSensor
{
//--- (end of YPower class start)
//--- (YPower definitions)
    /**
     * invalid powerFactor value
     */
    public static final double POWERFACTOR_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid cosPhi value
     */
    public static final double COSPHI_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid meter value
     */
    public static final double METER_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid deliveredEnergyMeter value
     */
    public static final double DELIVEREDENERGYMETER_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid receivedEnergyMeter value
     */
    public static final double RECEIVEDENERGYMETER_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid meterTimer value
     */
    public static final int METERTIMER_INVALID = YAPI.INVALID_UINT;
    protected double _powerFactor = POWERFACTOR_INVALID;
    protected double _cosPhi = COSPHI_INVALID;
    protected double _meter = METER_INVALID;
    protected double _deliveredEnergyMeter = DELIVEREDENERGYMETER_INVALID;
    protected double _receivedEnergyMeter = RECEIVEDENERGYMETER_INVALID;
    protected int _meterTimer = METERTIMER_INVALID;
    protected UpdateCallback _valueCallbackPower = null;
    protected TimedReportCallback _timedReportCallbackPower = null;

    /**
     * Deprecated UpdateCallback for Power
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YPower function, String functionValue);
    }

    /**
     * TimedReportCallback for Power
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YPower  function, YMeasure measure);
    }
    //--- (end of YPower definitions)


    /**
     *
     * @param func : functionid
     */
    protected YPower(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "Power";
        //--- (YPower attributes initialization)
        //--- (end of YPower attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YPower(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (YPower implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("powerFactor")) {
            _powerFactor = Math.round(json_val.getDouble("powerFactor") / 65.536) / 1000.0;
        }
        if (json_val.has("cosPhi")) {
            _cosPhi = Math.round(json_val.getDouble("cosPhi") / 65.536) / 1000.0;
        }
        if (json_val.has("meter")) {
            _meter = Math.round(json_val.getDouble("meter") / 65.536) / 1000.0;
        }
        if (json_val.has("deliveredEnergyMeter")) {
            _deliveredEnergyMeter = Math.round(json_val.getDouble("deliveredEnergyMeter") / 65.536) / 1000.0;
        }
        if (json_val.has("receivedEnergyMeter")) {
            _receivedEnergyMeter = Math.round(json_val.getDouble("receivedEnergyMeter") / 65.536) / 1000.0;
        }
        if (json_val.has("meterTimer")) {
            _meterTimer = json_val.getInt("meterTimer");
        }
        super._parseAttr(json_val);
    }

    /**
     * Returns the power factor (PF), i.e. ratio between the active power consumed (in W)
     * and the apparent power provided (VA).
     *
     * @return a floating point number corresponding to the power factor (PF), i.e
     *
     * @throws YAPI_Exception on error
     */
    public double get_powerFactor() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return POWERFACTOR_INVALID;
                }
            }
            res = _powerFactor;
            if (res == POWERFACTOR_INVALID) {
                res = _cosPhi;
            }
            res = (double)Math.round(res * 1000) / 1000;
        }
        return res;
    }

    /**
     * Returns the power factor (PF), i.e. ratio between the active power consumed (in W)
     * and the apparent power provided (VA).
     *
     * @return a floating point number corresponding to the power factor (PF), i.e
     *
     * @throws YAPI_Exception on error
     */
    public double getPowerFactor() throws YAPI_Exception
    {
        return get_powerFactor();
    }

    /**
     * Returns the Displacement Power factor (DPF), i.e. cosine of the phase shift between
     * the voltage and current fundamentals.
     * On the Yocto-Watt (V1), the value returned by this method correponds to the
     * power factor as this device is cannot estimate the true DPF.
     *
     * @return a floating point number corresponding to the Displacement Power factor (DPF), i.e
     *
     * @throws YAPI_Exception on error
     */
    public double get_cosPhi() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return COSPHI_INVALID;
                }
            }
            res = _cosPhi;
        }
        return res;
    }

    /**
     * Returns the Displacement Power factor (DPF), i.e. cosine of the phase shift between
     * the voltage and current fundamentals.
     * On the Yocto-Watt (V1), the value returned by this method correponds to the
     * power factor as this device is cannot estimate the true DPF.
     *
     * @return a floating point number corresponding to the Displacement Power factor (DPF), i.e
     *
     * @throws YAPI_Exception on error
     */
    public double getCosPhi() throws YAPI_Exception
    {
        return get_cosPhi();
    }

    public int set_meter(double  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Long.toString(Math.round(newval * 65536.0));
            _setAttr("meter",rest_val);
        }
        return YAPI.SUCCESS;
    }


    /**
     * Returns the energy counter, maintained by the wattmeter by integrating the
     * power consumption over time. This is the sum of forward and backwad energy transfers,
     * if you are insterested in only one direction, use  get_receivedEnergyMeter() or
     * get_deliveredEnergyMeter(). Note that this counter is reset at each start of the device.
     *
     *  @return a floating point number corresponding to the energy counter, maintained by the wattmeter by
     * integrating the
     *         power consumption over time
     *
     * @throws YAPI_Exception on error
     */
    public double get_meter() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return METER_INVALID;
                }
            }
            res = _meter;
        }
        return res;
    }

    /**
     * Returns the energy counter, maintained by the wattmeter by integrating the
     * power consumption over time. This is the sum of forward and backwad energy transfers,
     * if you are insterested in only one direction, use  get_receivedEnergyMeter() or
     * get_deliveredEnergyMeter(). Note that this counter is reset at each start of the device.
     *
     *  @return a floating point number corresponding to the energy counter, maintained by the wattmeter by
     * integrating the
     *         power consumption over time
     *
     * @throws YAPI_Exception on error
     */
    public double getMeter() throws YAPI_Exception
    {
        return get_meter();
    }

    /**
     * Returns the energy counter, maintained by the wattmeter by integrating the power consumption over time,
     * but only when positive. Note that this counter is reset at each start of the device.
     *
     *  @return a floating point number corresponding to the energy counter, maintained by the wattmeter by
     * integrating the power consumption over time,
     *         but only when positive
     *
     * @throws YAPI_Exception on error
     */
    public double get_deliveredEnergyMeter() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return DELIVEREDENERGYMETER_INVALID;
                }
            }
            res = _deliveredEnergyMeter;
        }
        return res;
    }

    /**
     * Returns the energy counter, maintained by the wattmeter by integrating the power consumption over time,
     * but only when positive. Note that this counter is reset at each start of the device.
     *
     *  @return a floating point number corresponding to the energy counter, maintained by the wattmeter by
     * integrating the power consumption over time,
     *         but only when positive
     *
     * @throws YAPI_Exception on error
     */
    public double getDeliveredEnergyMeter() throws YAPI_Exception
    {
        return get_deliveredEnergyMeter();
    }

    /**
     * Returns the energy counter, maintained by the wattmeter by integrating the power consumption over time,
     * but only when negative. Note that this counter is reset at each start of the device.
     *
     *  @return a floating point number corresponding to the energy counter, maintained by the wattmeter by
     * integrating the power consumption over time,
     *         but only when negative
     *
     * @throws YAPI_Exception on error
     */
    public double get_receivedEnergyMeter() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return RECEIVEDENERGYMETER_INVALID;
                }
            }
            res = _receivedEnergyMeter;
        }
        return res;
    }

    /**
     * Returns the energy counter, maintained by the wattmeter by integrating the power consumption over time,
     * but only when negative. Note that this counter is reset at each start of the device.
     *
     *  @return a floating point number corresponding to the energy counter, maintained by the wattmeter by
     * integrating the power consumption over time,
     *         but only when negative
     *
     * @throws YAPI_Exception on error
     */
    public double getReceivedEnergyMeter() throws YAPI_Exception
    {
        return get_receivedEnergyMeter();
    }

    /**
     * Returns the elapsed time since last energy counter reset, in seconds.
     *
     * @return an integer corresponding to the elapsed time since last energy counter reset, in seconds
     *
     * @throws YAPI_Exception on error
     */
    public int get_meterTimer() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return METERTIMER_INVALID;
                }
            }
            res = _meterTimer;
        }
        return res;
    }

    /**
     * Returns the elapsed time since last energy counter reset, in seconds.
     *
     * @return an integer corresponding to the elapsed time since last energy counter reset, in seconds
     *
     * @throws YAPI_Exception on error
     */
    public int getMeterTimer() throws YAPI_Exception
    {
        return get_meterTimer();
    }

    /**
     * Retrieves a electrical power sensor for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the electrical power sensor is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YPower.isOnline() to test if the electrical power sensor is
     * indeed online at a given time. In case of ambiguity when looking for
     * a electrical power sensor by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the electrical power sensor, for instance
     *         YWATTMK1.power.
     *
     * @return a YPower object allowing you to drive the electrical power sensor.
     */
    public static YPower FindPower(String func)
    {
        YPower obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YPower) YFunction._FindFromCache("Power", func);
            if (obj == null) {
                obj = new YPower(func);
                YFunction._AddToCache("Power", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a electrical power sensor for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the electrical power sensor is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YPower.isOnline() to test if the electrical power sensor is
     * indeed online at a given time. In case of ambiguity when looking for
     * a electrical power sensor by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the electrical power sensor, for instance
     *         YWATTMK1.power.
     *
     * @return a YPower object allowing you to drive the electrical power sensor.
     */
    public static YPower FindPowerInContext(YAPIContext yctx,String func)
    {
        YPower obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YPower) YFunction._FindFromCacheInContext(yctx, "Power", func);
            if (obj == null) {
                obj = new YPower(yctx, func);
                YFunction._AddToCache("Power", func, obj);
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
        _valueCallbackPower = callback;
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
        if (_valueCallbackPower != null) {
            _valueCallbackPower.yNewValue(this, value);
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
        _timedReportCallbackPower = callback;
        return 0;
    }

    @Override
    public int _invokeTimedReportCallback(YMeasure value)
    {
        if (_timedReportCallbackPower != null) {
            _timedReportCallbackPower.timedReportCallback(this, value);
        } else {
            super._invokeTimedReportCallback(value);
        }
        return 0;
    }

    /**
     * Resets the energy counters.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int reset() throws YAPI_Exception
    {
        return set_meter(0);
    }

    /**
     * Continues the enumeration of electrical power sensors started using yFirstPower().
     * Caution: You can't make any assumption about the returned electrical power sensors order.
     * If you want to find a specific a electrical power sensor, use Power.findPower()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YPower object, corresponding to
     *         a electrical power sensor currently online, or a null pointer
     *         if there are no more electrical power sensors to enumerate.
     */
    public YPower nextPower()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindPowerInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of electrical power sensors currently accessible.
     * Use the method YPower.nextPower() to iterate on
     * next electrical power sensors.
     *
     * @return a pointer to a YPower object, corresponding to
     *         the first electrical power sensor currently online, or a null pointer
     *         if there are none.
     */
    public static YPower FirstPower()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("Power");
        if (next_hwid == null)  return null;
        return FindPowerInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of electrical power sensors currently accessible.
     * Use the method YPower.nextPower() to iterate on
     * next electrical power sensors.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YPower object, corresponding to
     *         the first electrical power sensor currently online, or a null pointer
     *         if there are none.
     */
    public static YPower FirstPowerInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("Power");
        if (next_hwid == null)  return null;
        return FindPowerInContext(yctx, next_hwid);
    }

    //--- (end of YPower implementation)
}

