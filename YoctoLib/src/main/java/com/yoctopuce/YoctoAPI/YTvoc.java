/*
 *
 *  $Id: svn_id $
 *
 *  Implements FindTvoc(), the high-level API for Tvoc functions
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

//--- (YTvoc return codes)
//--- (end of YTvoc return codes)
//--- (YTvoc yapiwrapper)
//--- (end of YTvoc yapiwrapper)
//--- (YTvoc class start)
/**
 *  YTvoc Class: Total Volatile Organic Compound sensor control interface, available for instance in
 * the Yocto-VOC-V3
 *
 * The YTvoc class allows you to read and configure Yoctopuce Total Volatile Organic Compound sensors.
 * It inherits from YSensor class the core functions to read measurements,
 * to register callback functions, and to access the autonomous datalogger.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YTvoc extends YSensor
{
//--- (end of YTvoc class start)
//--- (YTvoc definitions)
    protected UpdateCallback _valueCallbackTvoc = null;
    protected TimedReportCallback _timedReportCallbackTvoc = null;

    /**
     * Deprecated UpdateCallback for Tvoc
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YTvoc function, String functionValue);
    }

    /**
     * TimedReportCallback for Tvoc
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YTvoc  function, YMeasure measure);
    }
    //--- (end of YTvoc definitions)


    /**
     *
     * @param func : functionid
     */
    protected YTvoc(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "Tvoc";
        //--- (YTvoc attributes initialization)
        //--- (end of YTvoc attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YTvoc(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (YTvoc implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        super._parseAttr(json_val);
    }

    /**
     * Retrieves a Total  Volatile Organic Compound sensor for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the Total  Volatile Organic Compound sensor is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YTvoc.isOnline() to test if the Total  Volatile Organic Compound sensor is
     * indeed online at a given time. In case of ambiguity when looking for
     * a Total  Volatile Organic Compound sensor by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the Total  Volatile Organic Compound sensor, for instance
     *         YVOCMK03.tvoc.
     *
     * @return a YTvoc object allowing you to drive the Total  Volatile Organic Compound sensor.
     */
    public static YTvoc FindTvoc(String func)
    {
        YTvoc obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YTvoc) YFunction._FindFromCache("Tvoc", func);
            if (obj == null) {
                obj = new YTvoc(func);
                YFunction._AddToCache("Tvoc", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a Total  Volatile Organic Compound sensor for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the Total  Volatile Organic Compound sensor is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YTvoc.isOnline() to test if the Total  Volatile Organic Compound sensor is
     * indeed online at a given time. In case of ambiguity when looking for
     * a Total  Volatile Organic Compound sensor by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the Total  Volatile Organic Compound sensor, for instance
     *         YVOCMK03.tvoc.
     *
     * @return a YTvoc object allowing you to drive the Total  Volatile Organic Compound sensor.
     */
    public static YTvoc FindTvocInContext(YAPIContext yctx,String func)
    {
        YTvoc obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YTvoc) YFunction._FindFromCacheInContext(yctx, "Tvoc", func);
            if (obj == null) {
                obj = new YTvoc(yctx, func);
                YFunction._AddToCache("Tvoc", func, obj);
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
        _valueCallbackTvoc = callback;
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
        if (_valueCallbackTvoc != null) {
            _valueCallbackTvoc.yNewValue(this, value);
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
        _timedReportCallbackTvoc = callback;
        return 0;
    }

    @Override
    public int _invokeTimedReportCallback(YMeasure value)
    {
        if (_timedReportCallbackTvoc != null) {
            _timedReportCallbackTvoc.timedReportCallback(this, value);
        } else {
            super._invokeTimedReportCallback(value);
        }
        return 0;
    }

    /**
     * Continues the enumeration of Total Volatile Organic Compound sensors started using yFirstTvoc().
     * Caution: You can't make any assumption about the returned Total Volatile Organic Compound sensors order.
     * If you want to find a specific a Total  Volatile Organic Compound sensor, use Tvoc.findTvoc()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YTvoc object, corresponding to
     *         a Total  Volatile Organic Compound sensor currently online, or a null pointer
     *         if there are no more Total Volatile Organic Compound sensors to enumerate.
     */
    public YTvoc nextTvoc()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindTvocInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of Total Volatile Organic Compound sensors currently accessible.
     * Use the method YTvoc.nextTvoc() to iterate on
     * next Total Volatile Organic Compound sensors.
     *
     * @return a pointer to a YTvoc object, corresponding to
     *         the first Total Volatile Organic Compound sensor currently online, or a null pointer
     *         if there are none.
     */
    public static YTvoc FirstTvoc()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("Tvoc");
        if (next_hwid == null)  return null;
        return FindTvocInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of Total Volatile Organic Compound sensors currently accessible.
     * Use the method YTvoc.nextTvoc() to iterate on
     * next Total Volatile Organic Compound sensors.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YTvoc object, corresponding to
     *         the first Total Volatile Organic Compound sensor currently online, or a null pointer
     *         if there are none.
     */
    public static YTvoc FirstTvocInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("Tvoc");
        if (next_hwid == null)  return null;
        return FindTvocInContext(yctx, next_hwid);
    }

    //--- (end of YTvoc implementation)
}

