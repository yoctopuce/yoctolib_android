/*********************************************************************
 * $Id: YGyro.java 67383 2025-06-11 05:44:27Z mvuilleu $
 *
 * Implements yFindGyro(), the high-level API for Gyro functions
 *
 * - - - - - - - - - License information: - - - - - - - - -
 *
 * Copyright (C) 2011 and beyond by Yoctopuce Sarl, Switzerland.
 *
 * Yoctopuce Sarl (hereafter Licensor) grants to you a perpetual
 * non-exclusive license to use, modify, copy and integrate this
 * file into your software for the sole purpose of interfacing
 * with Yoctopuce products.
 *
 * You may reproduce and distribute copies of this file in
 * source or object form, as long as the sole purpose of this
 * code is to interface with Yoctopuce products. You must retain
 * this notice in the distributed source file.
 *
 * You should refer to Yoctopuce General Terms and Conditions
 * for additional information regarding your rights and
 * obligations.
 *
 * THE SOFTWARE AND DOCUMENTATION ARE PROVIDED 'AS IS' WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING
 * WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL LICENSOR BE LIABLE FOR ANY INCIDENTAL, SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA,
 * COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY OR
 * SERVICES, ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT
 * LIMITED TO ANY DEFENSE THEREOF), ANY CLAIMS FOR INDEMNITY OR
 * CONTRIBUTION, OR OTHER SIMILAR COSTS, WHETHER ASSERTED ON THE
 * BASIS OF CONTRACT, TORT (INCLUDING NEGLIGENCE), BREACH OF
 * WARRANTY, OR OTHERWISE.
 *********************************************************************/

package com.yoctopuce.YoctoAPI;


import java.util.Locale;

//--- (generated code: YGyro return codes)
//--- (end of generated code: YGyro return codes)
//--- (generated code: YGyro class start)
/**
 * YGyro Class: gyroscope control interface, available for instance in the Yocto-3D-V2
 *
 * The YGyro class allows you to read and configure Yoctopuce gyroscopes.
 * It inherits from YSensor class the core functions to read measurements,
 * to register callback functions, and to access the autonomous datalogger.
 * This class adds the possibility to access x, y and z components of the rotation
 * vector separately, as well as the possibility to deal with quaternion-based
 * orientation estimates.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YGyro extends YSensor
{
//--- (end of generated code: YGyro class start)
    public interface YQuatCallback
    {
        void yQuaternionCallback(YGyro yGyro, double w, double x, double y, double z);
    }

    public interface YAnglesCallback
    {
        void yAnglesCallback(YGyro yGyro, double roll, double pitch, double head);
    }

    static private YQt.UpdateCallback yInternalGyroCallback = new YQt.UpdateCallback()
    {
        @Override
        public void yNewValue(YQt obj, String value)
        {
            YGyro gyro = (YGyro) obj.get_userData();
            if (gyro == null) {
                return;
            }
            try {
                int idx = Integer.parseInt(obj.get_functionId().substring(2));
                gyro._invokeGyroCallbacks(idx, Double.parseDouble(value));
            } catch (YAPI_Exception ignore) {
            }
        }
    };

//--- (generated code: YGyro definitions)
    /**
     * invalid bandwidth value
     */
    public static final int BANDWIDTH_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid xValue value
     */
    public static final double XVALUE_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid yValue value
     */
    public static final double YVALUE_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid zValue value
     */
    public static final double ZVALUE_INVALID = YAPI.INVALID_DOUBLE;
    protected int _bandwidth = BANDWIDTH_INVALID;
    protected double _xValue = XVALUE_INVALID;
    protected double _yValue = YVALUE_INVALID;
    protected double _zValue = ZVALUE_INVALID;
    protected UpdateCallback _valueCallbackGyro = null;
    protected TimedReportCallback _timedReportCallbackGyro = null;
    protected int _qt_stamp = 0;
    protected YQt _qt_w;
    protected YQt _qt_x;
    protected YQt _qt_y;
    protected YQt _qt_z;
    protected double _w = 0;
    protected double _x = 0;
    protected double _y = 0;
    protected double _z = 0;
    protected int _angles_stamp = 0;
    protected double _head = 0;
    protected double _pitch = 0;
    protected double _roll = 0;
    protected YQuatCallback _quatCallback;
    protected YAnglesCallback _anglesCallback;

    /**
     * Deprecated UpdateCallback for Gyro
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YGyro function, String functionValue);
    }

    /**
     * TimedReportCallback for Gyro
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YGyro  function, YMeasure measure);
    }
    //--- (end of generated code: YGyro definitions)


    /**
     * @param func : functionid
     */
    protected YGyro(YAPIContext yctx, String func)
    {
        super(yctx, func);
        _className = "Gyro";
        //--- (generated code: YGyro attributes initialization)
        //--- (end of generated code: YGyro attributes initialization)
    }

    protected YGyro(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }


    //--- (generated code: YGyro implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("bandwidth")) {
            _bandwidth = json_val.getInt("bandwidth");
        }
        if (json_val.has("xValue")) {
            _xValue = Math.round(json_val.getDouble("xValue") / 65.536) / 1000.0;
        }
        if (json_val.has("yValue")) {
            _yValue = Math.round(json_val.getDouble("yValue") / 65.536) / 1000.0;
        }
        if (json_val.has("zValue")) {
            _zValue = Math.round(json_val.getDouble("zValue") / 65.536) / 1000.0;
        }
        super._parseAttr(json_val);
    }

    /**
     * Returns the measure update frequency, measured in Hz.
     *
     * @return an integer corresponding to the measure update frequency, measured in Hz
     *
     * @throws YAPI_Exception on error
     */
    public int get_bandwidth() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return BANDWIDTH_INVALID;
                }
            }
            res = _bandwidth;
        }
        return res;
    }

    /**
     * Returns the measure update frequency, measured in Hz.
     *
     * @return an integer corresponding to the measure update frequency, measured in Hz
     *
     * @throws YAPI_Exception on error
     */
    public int getBandwidth() throws YAPI_Exception
    {
        return get_bandwidth();
    }

    /**
     * Changes the measure update frequency, measured in Hz. When the
     * frequency is lower, the device performs averaging.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     * @param newval : an integer corresponding to the measure update frequency, measured in Hz
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_bandwidth(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("bandwidth",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the measure update frequency, measured in Hz. When the
     * frequency is lower, the device performs averaging.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     * @param newval : an integer corresponding to the measure update frequency, measured in Hz
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setBandwidth(int newval)  throws YAPI_Exception
    {
        return set_bandwidth(newval);
    }

    /**
     * Returns the angular velocity around the X axis of the device, as a floating point number.
     *
     *  @return a floating point number corresponding to the angular velocity around the X axis of the
     * device, as a floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double get_xValue() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return XVALUE_INVALID;
                }
            }
            res = _xValue;
        }
        return res;
    }

    /**
     * Returns the angular velocity around the X axis of the device, as a floating point number.
     *
     *  @return a floating point number corresponding to the angular velocity around the X axis of the
     * device, as a floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double getXValue() throws YAPI_Exception
    {
        return get_xValue();
    }

    /**
     * Returns the angular velocity around the Y axis of the device, as a floating point number.
     *
     *  @return a floating point number corresponding to the angular velocity around the Y axis of the
     * device, as a floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double get_yValue() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return YVALUE_INVALID;
                }
            }
            res = _yValue;
        }
        return res;
    }

    /**
     * Returns the angular velocity around the Y axis of the device, as a floating point number.
     *
     *  @return a floating point number corresponding to the angular velocity around the Y axis of the
     * device, as a floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double getYValue() throws YAPI_Exception
    {
        return get_yValue();
    }

    /**
     * Returns the angular velocity around the Z axis of the device, as a floating point number.
     *
     *  @return a floating point number corresponding to the angular velocity around the Z axis of the
     * device, as a floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double get_zValue() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return ZVALUE_INVALID;
                }
            }
            res = _zValue;
        }
        return res;
    }

    /**
     * Returns the angular velocity around the Z axis of the device, as a floating point number.
     *
     *  @return a floating point number corresponding to the angular velocity around the Z axis of the
     * device, as a floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double getZValue() throws YAPI_Exception
    {
        return get_zValue();
    }

    /**
     * Retrieves a gyroscope for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the gyroscope is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YGyro.isOnline() to test if the gyroscope is
     * indeed online at a given time. In case of ambiguity when looking for
     * a gyroscope by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the gyroscope, for instance
     *         Y3DMK002.gyro.
     *
     * @return a YGyro object allowing you to drive the gyroscope.
     */
    public static YGyro FindGyro(String func)
    {
        YGyro obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YGyro) YFunction._FindFromCache("Gyro", func);
            if (obj == null) {
                obj = new YGyro(func);
                YFunction._AddToCache("Gyro", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a gyroscope for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the gyroscope is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YGyro.isOnline() to test if the gyroscope is
     * indeed online at a given time. In case of ambiguity when looking for
     * a gyroscope by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the gyroscope, for instance
     *         Y3DMK002.gyro.
     *
     * @return a YGyro object allowing you to drive the gyroscope.
     */
    public static YGyro FindGyroInContext(YAPIContext yctx,String func)
    {
        YGyro obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YGyro) YFunction._FindFromCacheInContext(yctx, "Gyro", func);
            if (obj == null) {
                obj = new YGyro(yctx, func);
                YFunction._AddToCache("Gyro", func, obj);
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
        _valueCallbackGyro = callback;
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
        if (_valueCallbackGyro != null) {
            _valueCallbackGyro.yNewValue(this, value);
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
        _timedReportCallbackGyro = callback;
        return 0;
    }

    @Override
    public int _invokeTimedReportCallback(YMeasure value)
    {
        if (_timedReportCallbackGyro != null) {
            _timedReportCallbackGyro.timedReportCallback(this, value);
        } else {
            super._invokeTimedReportCallback(value);
        }
        return 0;
    }

    public int _loadQuaternion() throws YAPI_Exception
    {
        int now_stamp;
        int age_ms;
        now_stamp = (int) (YAPIContext.GetTickCount() & 0x7FFFFFFF);
        age_ms = ((now_stamp - _qt_stamp) & 0x7FFFFFFF);
        if ((age_ms >= 10) || (_qt_stamp == 0)) {
            if (load(10) != YAPI.SUCCESS) {
                return YAPI.DEVICE_NOT_FOUND;
            }
            if (_qt_stamp == 0) {
                _qt_w = YQt.FindQtInContext(_yapi, String.format(Locale.US, "%s.qt1",_serial));
                _qt_x = YQt.FindQtInContext(_yapi, String.format(Locale.US, "%s.qt2",_serial));
                _qt_y = YQt.FindQtInContext(_yapi, String.format(Locale.US, "%s.qt3",_serial));
                _qt_z = YQt.FindQtInContext(_yapi, String.format(Locale.US, "%s.qt4",_serial));
            }
            if (_qt_w.load(9) != YAPI.SUCCESS) {
                return YAPI.DEVICE_NOT_FOUND;
            }
            if (_qt_x.load(9) != YAPI.SUCCESS) {
                return YAPI.DEVICE_NOT_FOUND;
            }
            if (_qt_y.load(9) != YAPI.SUCCESS) {
                return YAPI.DEVICE_NOT_FOUND;
            }
            if (_qt_z.load(9) != YAPI.SUCCESS) {
                return YAPI.DEVICE_NOT_FOUND;
            }
            _w = _qt_w.get_currentValue();
            _x = _qt_x.get_currentValue();
            _y = _qt_y.get_currentValue();
            _z = _qt_z.get_currentValue();
            _qt_stamp = now_stamp;
        }
        return YAPI.SUCCESS;
    }

    public int _loadAngles() throws YAPI_Exception
    {
        double sqw;
        double sqx;
        double sqy;
        double sqz;
        double norm;
        double delta;

        if (_loadQuaternion() != YAPI.SUCCESS) {
            return YAPI.DEVICE_NOT_FOUND;
        }
        if (_angles_stamp != _qt_stamp) {
            sqw = _w * _w;
            sqx = _x * _x;
            sqy = _y * _y;
            sqz = _z * _z;
            norm = sqx + sqy + sqz + sqw;
            delta = _y * _w - _x * _z;
            if (delta > 0.499 * norm) {
                // singularity at north pole
                _pitch = 90.0;
                _head  = (double)Math.round(2.0 * 1800.0/java.lang.Math.PI * java.lang.Math.atan2(_x,-_w)) / 10.0;
            } else {
                if (delta < -0.499 * norm) {
                    // singularity at south pole
                    _pitch = -90.0;
                    _head  = (double)Math.round(-2.0 * 1800.0/java.lang.Math.PI * java.lang.Math.atan2(_x,-_w)) / 10.0;
                } else {
                    _roll  = (double)Math.round(1800.0/java.lang.Math.PI * java.lang.Math.atan2(2.0 * (_w * _x + _y * _z),sqw - sqx - sqy + sqz)) / 10.0;
                    _pitch = (double)Math.round(1800.0/java.lang.Math.PI * java.lang.Math.asin(2.0 * delta / norm)) / 10.0;
                    _head  = (double)Math.round(1800.0/java.lang.Math.PI * java.lang.Math.atan2(2.0 * (_x * _y + _z * _w),sqw + sqx - sqy - sqz)) / 10.0;
                }
            }
            _angles_stamp = _qt_stamp;
        }
        return YAPI.SUCCESS;
    }

    /**
     * Returns the estimated roll angle, based on the integration of
     * gyroscopic measures combined with acceleration and
     * magnetic field measurements.
     * The axis corresponding to the roll angle can be mapped to any
     * of the device X, Y or Z physical directions using methods of
     * the class YRefFrame.
     *
     * @return a floating-point number corresponding to roll angle
     *         in degrees, between -180 and +180.
     */
    public double get_roll() throws YAPI_Exception
    {
        _loadAngles();
        return _roll;
    }

    /**
     * Returns the estimated pitch angle, based on the integration of
     * gyroscopic measures combined with acceleration and
     * magnetic field measurements.
     * The axis corresponding to the pitch angle can be mapped to any
     * of the device X, Y or Z physical directions using methods of
     * the class YRefFrame.
     *
     * @return a floating-point number corresponding to pitch angle
     *         in degrees, between -90 and +90.
     */
    public double get_pitch() throws YAPI_Exception
    {
        _loadAngles();
        return _pitch;
    }

    /**
     * Returns the estimated heading angle, based on the integration of
     * gyroscopic measures combined with acceleration and
     * magnetic field measurements.
     * The axis corresponding to the heading can be mapped to any
     * of the device X, Y or Z physical directions using methods of
     * the class YRefFrame.
     *
     * @return a floating-point number corresponding to heading
     *         in degrees, between 0 and 360.
     */
    public double get_heading() throws YAPI_Exception
    {
        _loadAngles();
        return _head;
    }

    /**
     * Returns the w component (real part) of the quaternion
     * describing the device estimated orientation, based on the
     * integration of gyroscopic measures combined with acceleration and
     * magnetic field measurements.
     *
     * @return a floating-point number corresponding to the w
     *         component of the quaternion.
     */
    public double get_quaternionW() throws YAPI_Exception
    {
        _loadQuaternion();
        return _w;
    }

    /**
     * Returns the x component of the quaternion
     * describing the device estimated orientation, based on the
     * integration of gyroscopic measures combined with acceleration and
     * magnetic field measurements. The x component is
     * mostly correlated with rotations on the roll axis.
     *
     * @return a floating-point number corresponding to the x
     *         component of the quaternion.
     */
    public double get_quaternionX() throws YAPI_Exception
    {
        _loadQuaternion();
        return _x;
    }

    /**
     * Returns the y component of the quaternion
     * describing the device estimated orientation, based on the
     * integration of gyroscopic measures combined with acceleration and
     * magnetic field measurements. The y component is
     * mostly correlated with rotations on the pitch axis.
     *
     * @return a floating-point number corresponding to the y
     *         component of the quaternion.
     */
    public double get_quaternionY() throws YAPI_Exception
    {
        _loadQuaternion();
        return _y;
    }

    /**
     * Returns the x component of the quaternion
     * describing the device estimated orientation, based on the
     * integration of gyroscopic measures combined with acceleration and
     * magnetic field measurements. The x component is
     * mostly correlated with changes of heading.
     *
     * @return a floating-point number corresponding to the z
     *         component of the quaternion.
     */
    public double get_quaternionZ() throws YAPI_Exception
    {
        _loadQuaternion();
        return _z;
    }

    /**
     * Registers a callback function that will be invoked each time that the estimated
     * device orientation has changed. The call frequency is typically around 95Hz during a move.
     * The callback is invoked only during the execution of ySleep or yHandleEvents.
     * This provides control over the time when the callback is triggered.
     * For good responsiveness, remember to call one of these two functions periodically.
     * To unregister a callback, pass a null pointer as argument.
     *
     * @param callback : the callback function to invoke, or a null pointer.
     *         The callback function should take five arguments:
     *         the YGyro object of the turning device, and the floating
     *         point values of the four components w, x, y and z
     *         (as floating-point numbers).
     *
     */
    public int registerQuaternionCallback(YQuatCallback callback) throws YAPI_Exception
    {
        _quatCallback = callback;
        if (callback != null) {
            if (_loadQuaternion() != YAPI.SUCCESS) {
                return YAPI.DEVICE_NOT_FOUND;
            }
            _qt_w.set_userData(this);
            _qt_x.set_userData(this);
            _qt_y.set_userData(this);
            _qt_z.set_userData(this);
            _qt_w.registerValueCallback(yInternalGyroCallback);
            _qt_x.registerValueCallback(yInternalGyroCallback);
            _qt_y.registerValueCallback(yInternalGyroCallback);
            _qt_z.registerValueCallback(yInternalGyroCallback);
        } else {
            if (!(_anglesCallback != null)) {
                _qt_w.registerValueCallback((YQt.UpdateCallback) null);
                _qt_x.registerValueCallback((YQt.UpdateCallback) null);
                _qt_y.registerValueCallback((YQt.UpdateCallback) null);
                _qt_z.registerValueCallback((YQt.UpdateCallback) null);
            }
        }
        return 0;
    }

    /**
     * Registers a callback function that will be invoked each time that the estimated
     * device orientation has changed. The call frequency is typically around 95Hz during a move.
     * The callback is invoked only during the execution of ySleep or yHandleEvents.
     * This provides control over the time when the callback is triggered.
     * For good responsiveness, remember to call one of these two functions periodically.
     * To unregister a callback, pass a null pointer as argument.
     *
     * @param callback : the callback function to invoke, or a null pointer.
     *         The callback function should take four arguments:
     *         the YGyro object of the turning device, and the floating
     *         point values of the three angles roll, pitch and heading
     *         in degrees (as floating-point numbers).
     *
     */
    public int registerAnglesCallback(YAnglesCallback callback) throws YAPI_Exception
    {
        _anglesCallback = callback;
        if (callback != null) {
            if (_loadQuaternion() != YAPI.SUCCESS) {
                return YAPI.DEVICE_NOT_FOUND;
            }
            _qt_w.set_userData(this);
            _qt_x.set_userData(this);
            _qt_y.set_userData(this);
            _qt_z.set_userData(this);
            _qt_w.registerValueCallback(yInternalGyroCallback);
            _qt_x.registerValueCallback(yInternalGyroCallback);
            _qt_y.registerValueCallback(yInternalGyroCallback);
            _qt_z.registerValueCallback(yInternalGyroCallback);
        } else {
            if (!(_quatCallback != null)) {
                _qt_w.registerValueCallback((YQt.UpdateCallback) null);
                _qt_x.registerValueCallback((YQt.UpdateCallback) null);
                _qt_y.registerValueCallback((YQt.UpdateCallback) null);
                _qt_z.registerValueCallback((YQt.UpdateCallback) null);
            }
        }
        return 0;
    }

    public int _invokeGyroCallbacks(int qtIndex,double qtValue) throws YAPI_Exception
    {
        switch (qtIndex - 1) {
        case 0:
            _w = qtValue;
            break;
        case 1:
            _x = qtValue;
            break;
        case 2:
            _y = qtValue;
            break;
        case 3:
            _z = qtValue;
            break;
        }
        if (qtIndex < 4) {
            return 0;
        }
        _qt_stamp = (int) (YAPIContext.GetTickCount() & 0x7FFFFFFF);
        if (_quatCallback != null) {
            _quatCallback.yQuaternionCallback(this, _w, _x, _y, _z);
        }
        if (_anglesCallback != null) {
            _loadAngles();
            _anglesCallback.yAnglesCallback(this, _roll, _pitch, _head);
        }
        return 0;
    }

    /**
     * Continues the enumeration of gyroscopes started using yFirstGyro().
     * Caution: You can't make any assumption about the returned gyroscopes order.
     * If you want to find a specific a gyroscope, use Gyro.findGyro()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YGyro object, corresponding to
     *         a gyroscope currently online, or a null pointer
     *         if there are no more gyroscopes to enumerate.
     */
    public YGyro nextGyro()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindGyroInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of gyroscopes currently accessible.
     * Use the method YGyro.nextGyro() to iterate on
     * next gyroscopes.
     *
     * @return a pointer to a YGyro object, corresponding to
     *         the first gyro currently online, or a null pointer
     *         if there are none.
     */
    public static YGyro FirstGyro()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("Gyro");
        if (next_hwid == null)  return null;
        return FindGyroInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of gyroscopes currently accessible.
     * Use the method YGyro.nextGyro() to iterate on
     * next gyroscopes.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YGyro object, corresponding to
     *         the first gyro currently online, or a null pointer
     *         if there are none.
     */
    public static YGyro FirstGyroInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("Gyro");
        if (next_hwid == null)  return null;
        return FindGyroInContext(yctx, next_hwid);
    }

    //--- (end of generated code: YGyro implementation)
}

