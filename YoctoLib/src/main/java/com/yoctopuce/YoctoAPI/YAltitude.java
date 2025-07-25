/*
 *
 *  $Id: svn_id $
 *
 *  Implements FindAltitude(), the high-level API for Altitude functions
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

//--- (YAltitude return codes)
//--- (end of YAltitude return codes)
//--- (YAltitude yapiwrapper)
//--- (end of YAltitude yapiwrapper)
//--- (YAltitude class start)
/**
 *  YAltitude Class: altimeter control interface, available for instance in the Yocto-Altimeter-V2 or
 * the Yocto-GPS-V2
 *
 * The YAltitude class allows you to read and configure Yoctopuce altimeters.
 * It inherits from YSensor class the core functions to read measurements,
 * to register callback functions, and to access the autonomous datalogger.
 * This class adds the ability to configure the barometric pressure adjusted to
 * sea level (QNH) for barometric sensors.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YAltitude extends YSensor
{
//--- (end of YAltitude class start)
//--- (YAltitude definitions)
    /**
     * invalid qnh value
     */
    public static final double QNH_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid technology value
     */
    public static final String TECHNOLOGY_INVALID = YAPI.INVALID_STRING;
    protected double _qnh = QNH_INVALID;
    protected String _technology = TECHNOLOGY_INVALID;
    protected UpdateCallback _valueCallbackAltitude = null;
    protected TimedReportCallback _timedReportCallbackAltitude = null;

    /**
     * Deprecated UpdateCallback for Altitude
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YAltitude function, String functionValue);
    }

    /**
     * TimedReportCallback for Altitude
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YAltitude  function, YMeasure measure);
    }
    //--- (end of YAltitude definitions)


    /**
     *
     * @param func : functionid
     */
    protected YAltitude(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "Altitude";
        //--- (YAltitude attributes initialization)
        //--- (end of YAltitude attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YAltitude(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (YAltitude implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("qnh")) {
            _qnh = Math.round(json_val.getDouble("qnh") / 65.536) / 1000.0;
        }
        if (json_val.has("technology")) {
            _technology = json_val.getString("technology");
        }
        super._parseAttr(json_val);
    }

    /**
     * Changes the current estimated altitude. This allows one to compensate for
     * ambient pressure variations and to work in relative mode.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     * @param newval : a floating point number corresponding to the current estimated altitude
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_currentValue(double  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Long.toString(Math.round(newval * 65536.0));
            _setAttr("currentValue",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the current estimated altitude. This allows one to compensate for
     * ambient pressure variations and to work in relative mode.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     * @param newval : a floating point number corresponding to the current estimated altitude
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setCurrentValue(double newval)  throws YAPI_Exception
    {
        return set_currentValue(newval);
    }

    /**
     * Changes the barometric pressure adjusted to sea level used to compute
     * the altitude (QNH). This enables you to compensate for atmospheric pressure
     * changes due to weather conditions. Applicable to barometric altimeters only.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     *  @param newval : a floating point number corresponding to the barometric pressure adjusted to sea
     * level used to compute
     *         the altitude (QNH)
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_qnh(double  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Long.toString(Math.round(newval * 65536.0));
            _setAttr("qnh",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the barometric pressure adjusted to sea level used to compute
     * the altitude (QNH). This enables you to compensate for atmospheric pressure
     * changes due to weather conditions. Applicable to barometric altimeters only.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     *  @param newval : a floating point number corresponding to the barometric pressure adjusted to sea
     * level used to compute
     *         the altitude (QNH)
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setQnh(double newval)  throws YAPI_Exception
    {
        return set_qnh(newval);
    }

    /**
     * Returns the barometric pressure adjusted to sea level used to compute
     * the altitude (QNH). Applicable to barometric altimeters only.
     *
     * @return a floating point number corresponding to the barometric pressure adjusted to sea level used to compute
     *         the altitude (QNH)
     *
     * @throws YAPI_Exception on error
     */
    public double get_qnh() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return QNH_INVALID;
                }
            }
            res = _qnh;
        }
        return res;
    }

    /**
     * Returns the barometric pressure adjusted to sea level used to compute
     * the altitude (QNH). Applicable to barometric altimeters only.
     *
     * @return a floating point number corresponding to the barometric pressure adjusted to sea level used to compute
     *         the altitude (QNH)
     *
     * @throws YAPI_Exception on error
     */
    public double getQnh() throws YAPI_Exception
    {
        return get_qnh();
    }

    /**
     * Returns the technology used by the sesnor to compute
     * altitude. Possibles values are  "barometric" and "gps"
     *
     * @return a string corresponding to the technology used by the sesnor to compute
     *         altitude
     *
     * @throws YAPI_Exception on error
     */
    public String get_technology() throws YAPI_Exception
    {
        String res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return TECHNOLOGY_INVALID;
                }
            }
            res = _technology;
        }
        return res;
    }

    /**
     * Returns the technology used by the sesnor to compute
     * altitude. Possibles values are  "barometric" and "gps"
     *
     * @return a string corresponding to the technology used by the sesnor to compute
     *         altitude
     *
     * @throws YAPI_Exception on error
     */
    public String getTechnology() throws YAPI_Exception
    {
        return get_technology();
    }

    /**
     * Retrieves an altimeter for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the altimeter is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YAltitude.isOnline() to test if the altimeter is
     * indeed online at a given time. In case of ambiguity when looking for
     * an altimeter by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the altimeter, for instance
     *         YALTIMK2.altitude.
     *
     * @return a YAltitude object allowing you to drive the altimeter.
     */
    public static YAltitude FindAltitude(String func)
    {
        YAltitude obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YAltitude) YFunction._FindFromCache("Altitude", func);
            if (obj == null) {
                obj = new YAltitude(func);
                YFunction._AddToCache("Altitude", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves an altimeter for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the altimeter is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YAltitude.isOnline() to test if the altimeter is
     * indeed online at a given time. In case of ambiguity when looking for
     * an altimeter by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the altimeter, for instance
     *         YALTIMK2.altitude.
     *
     * @return a YAltitude object allowing you to drive the altimeter.
     */
    public static YAltitude FindAltitudeInContext(YAPIContext yctx,String func)
    {
        YAltitude obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YAltitude) YFunction._FindFromCacheInContext(yctx, "Altitude", func);
            if (obj == null) {
                obj = new YAltitude(yctx, func);
                YFunction._AddToCache("Altitude", func, obj);
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
        _valueCallbackAltitude = callback;
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
        if (_valueCallbackAltitude != null) {
            _valueCallbackAltitude.yNewValue(this, value);
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
        _timedReportCallbackAltitude = callback;
        return 0;
    }

    @Override
    public int _invokeTimedReportCallback(YMeasure value)
    {
        if (_timedReportCallbackAltitude != null) {
            _timedReportCallbackAltitude.timedReportCallback(this, value);
        } else {
            super._invokeTimedReportCallback(value);
        }
        return 0;
    }

    /**
     * Continues the enumeration of altimeters started using yFirstAltitude().
     * Caution: You can't make any assumption about the returned altimeters order.
     * If you want to find a specific an altimeter, use Altitude.findAltitude()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YAltitude object, corresponding to
     *         an altimeter currently online, or a null pointer
     *         if there are no more altimeters to enumerate.
     */
    public YAltitude nextAltitude()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindAltitudeInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of altimeters currently accessible.
     * Use the method YAltitude.nextAltitude() to iterate on
     * next altimeters.
     *
     * @return a pointer to a YAltitude object, corresponding to
     *         the first altimeter currently online, or a null pointer
     *         if there are none.
     */
    public static YAltitude FirstAltitude()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("Altitude");
        if (next_hwid == null)  return null;
        return FindAltitudeInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of altimeters currently accessible.
     * Use the method YAltitude.nextAltitude() to iterate on
     * next altimeters.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YAltitude object, corresponding to
     *         the first altimeter currently online, or a null pointer
     *         if there are none.
     */
    public static YAltitude FirstAltitudeInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("Altitude");
        if (next_hwid == null)  return null;
        return FindAltitudeInContext(yctx, next_hwid);
    }

    //--- (end of YAltitude implementation)
}

