/*
 *
 *  $Id: YInputCapture.java 67383 2025-06-11 05:44:27Z mvuilleu $
 *
 *  Implements FindI2cPort(), the high-level API for I2cPort functions
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

//--- (generated code: YInputCapture return codes)
//--- (end of generated code: YInputCapture return codes)
//--- (generated code: YInputCapture class start)
/**
 * YInputCapture Class: instant snapshot trigger control interface
 *
 * The YInputCapture class allows you to access data samples
 * measured by a Yoctopuce electrical sensor. The data capture can be
 * triggered manually, or be configured to detect specific events.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YInputCapture extends YFunction
{
//--- (end of generated code: YInputCapture class start)
//--- (generated code: YInputCapture definitions)
    /**
     * invalid lastCaptureTime value
     */
    public static final long LASTCAPTURETIME_INVALID = YAPI.INVALID_LONG;
    /**
     * invalid nSamples value
     */
    public static final int NSAMPLES_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid samplingRate value
     */
    public static final int SAMPLINGRATE_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid captureType value
     */
    public static final int CAPTURETYPE_NONE = 0;
    public static final int CAPTURETYPE_TIMED = 1;
    public static final int CAPTURETYPE_V_MAX = 2;
    public static final int CAPTURETYPE_V_MIN = 3;
    public static final int CAPTURETYPE_I_MAX = 4;
    public static final int CAPTURETYPE_I_MIN = 5;
    public static final int CAPTURETYPE_P_MAX = 6;
    public static final int CAPTURETYPE_P_MIN = 7;
    public static final int CAPTURETYPE_V_AVG_MAX = 8;
    public static final int CAPTURETYPE_V_AVG_MIN = 9;
    public static final int CAPTURETYPE_V_RMS_MAX = 10;
    public static final int CAPTURETYPE_V_RMS_MIN = 11;
    public static final int CAPTURETYPE_I_AVG_MAX = 12;
    public static final int CAPTURETYPE_I_AVG_MIN = 13;
    public static final int CAPTURETYPE_I_RMS_MAX = 14;
    public static final int CAPTURETYPE_I_RMS_MIN = 15;
    public static final int CAPTURETYPE_P_AVG_MAX = 16;
    public static final int CAPTURETYPE_P_AVG_MIN = 17;
    public static final int CAPTURETYPE_PF_MIN = 18;
    public static final int CAPTURETYPE_DPF_MIN = 19;
    public static final int CAPTURETYPE_INVALID = -1;
    /**
     * invalid condValue value
     */
    public static final double CONDVALUE_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid condAlign value
     */
    public static final int CONDALIGN_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid captureTypeAtStartup value
     */
    public static final int CAPTURETYPEATSTARTUP_NONE = 0;
    public static final int CAPTURETYPEATSTARTUP_TIMED = 1;
    public static final int CAPTURETYPEATSTARTUP_V_MAX = 2;
    public static final int CAPTURETYPEATSTARTUP_V_MIN = 3;
    public static final int CAPTURETYPEATSTARTUP_I_MAX = 4;
    public static final int CAPTURETYPEATSTARTUP_I_MIN = 5;
    public static final int CAPTURETYPEATSTARTUP_P_MAX = 6;
    public static final int CAPTURETYPEATSTARTUP_P_MIN = 7;
    public static final int CAPTURETYPEATSTARTUP_V_AVG_MAX = 8;
    public static final int CAPTURETYPEATSTARTUP_V_AVG_MIN = 9;
    public static final int CAPTURETYPEATSTARTUP_V_RMS_MAX = 10;
    public static final int CAPTURETYPEATSTARTUP_V_RMS_MIN = 11;
    public static final int CAPTURETYPEATSTARTUP_I_AVG_MAX = 12;
    public static final int CAPTURETYPEATSTARTUP_I_AVG_MIN = 13;
    public static final int CAPTURETYPEATSTARTUP_I_RMS_MAX = 14;
    public static final int CAPTURETYPEATSTARTUP_I_RMS_MIN = 15;
    public static final int CAPTURETYPEATSTARTUP_P_AVG_MAX = 16;
    public static final int CAPTURETYPEATSTARTUP_P_AVG_MIN = 17;
    public static final int CAPTURETYPEATSTARTUP_PF_MIN = 18;
    public static final int CAPTURETYPEATSTARTUP_DPF_MIN = 19;
    public static final int CAPTURETYPEATSTARTUP_INVALID = -1;
    /**
     * invalid condValueAtStartup value
     */
    public static final double CONDVALUEATSTARTUP_INVALID = YAPI.INVALID_DOUBLE;
    protected long _lastCaptureTime = LASTCAPTURETIME_INVALID;
    protected int _nSamples = NSAMPLES_INVALID;
    protected int _samplingRate = SAMPLINGRATE_INVALID;
    protected int _captureType = CAPTURETYPE_INVALID;
    protected double _condValue = CONDVALUE_INVALID;
    protected int _condAlign = CONDALIGN_INVALID;
    protected int _captureTypeAtStartup = CAPTURETYPEATSTARTUP_INVALID;
    protected double _condValueAtStartup = CONDVALUEATSTARTUP_INVALID;
    protected UpdateCallback _valueCallbackInputCapture = null;

    /**
     * Deprecated UpdateCallback for InputCapture
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YInputCapture function, String functionValue);
    }

    /**
     * TimedReportCallback for InputCapture
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YInputCapture  function, YMeasure measure);
    }
    //--- (end of generated code: YInputCapture definitions)


    /**
     *
     * @param func : functionid
     */
    protected YInputCapture(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "I2cPort";
        //--- (generated code: YInputCapture attributes initialization)
        //--- (end of generated code: YInputCapture attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YInputCapture(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (generated code: YInputCapture implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("lastCaptureTime")) {
            _lastCaptureTime = json_val.getLong("lastCaptureTime");
        }
        if (json_val.has("nSamples")) {
            _nSamples = json_val.getInt("nSamples");
        }
        if (json_val.has("samplingRate")) {
            _samplingRate = json_val.getInt("samplingRate");
        }
        if (json_val.has("captureType")) {
            _captureType = json_val.getInt("captureType");
        }
        if (json_val.has("condValue")) {
            _condValue = Math.round(json_val.getDouble("condValue") / 65.536) / 1000.0;
        }
        if (json_val.has("condAlign")) {
            _condAlign = json_val.getInt("condAlign");
        }
        if (json_val.has("captureTypeAtStartup")) {
            _captureTypeAtStartup = json_val.getInt("captureTypeAtStartup");
        }
        if (json_val.has("condValueAtStartup")) {
            _condValueAtStartup = Math.round(json_val.getDouble("condValueAtStartup") / 65.536) / 1000.0;
        }
        super._parseAttr(json_val);
    }

    /**
     * Returns the number of elapsed milliseconds between the module power on
     * and the last capture (time of trigger), or zero if no capture has been done.
     *
     * @return an integer corresponding to the number of elapsed milliseconds between the module power on
     *         and the last capture (time of trigger), or zero if no capture has been done
     *
     * @throws YAPI_Exception on error
     */
    public long get_lastCaptureTime() throws YAPI_Exception
    {
        long res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return LASTCAPTURETIME_INVALID;
                }
            }
            res = _lastCaptureTime;
        }
        return res;
    }

    /**
     * Returns the number of elapsed milliseconds between the module power on
     * and the last capture (time of trigger), or zero if no capture has been done.
     *
     * @return an integer corresponding to the number of elapsed milliseconds between the module power on
     *         and the last capture (time of trigger), or zero if no capture has been done
     *
     * @throws YAPI_Exception on error
     */
    public long getLastCaptureTime() throws YAPI_Exception
    {
        return get_lastCaptureTime();
    }

    /**
     * Returns the number of samples that will be captured.
     *
     * @return an integer corresponding to the number of samples that will be captured
     *
     * @throws YAPI_Exception on error
     */
    public int get_nSamples() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return NSAMPLES_INVALID;
                }
            }
            res = _nSamples;
        }
        return res;
    }

    /**
     * Returns the number of samples that will be captured.
     *
     * @return an integer corresponding to the number of samples that will be captured
     *
     * @throws YAPI_Exception on error
     */
    public int getNSamples() throws YAPI_Exception
    {
        return get_nSamples();
    }

    /**
     * Changes the type of automatic conditional capture.
     * The maximum number of samples depends on the device memory.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     * @param newval : an integer corresponding to the type of automatic conditional capture
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_nSamples(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("nSamples",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the type of automatic conditional capture.
     * The maximum number of samples depends on the device memory.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     * @param newval : an integer corresponding to the type of automatic conditional capture
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setNSamples(int newval)  throws YAPI_Exception
    {
        return set_nSamples(newval);
    }

    /**
     * Returns the sampling frequency, in Hz.
     *
     * @return an integer corresponding to the sampling frequency, in Hz
     *
     * @throws YAPI_Exception on error
     */
    public int get_samplingRate() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SAMPLINGRATE_INVALID;
                }
            }
            res = _samplingRate;
        }
        return res;
    }

    /**
     * Returns the sampling frequency, in Hz.
     *
     * @return an integer corresponding to the sampling frequency, in Hz
     *
     * @throws YAPI_Exception on error
     */
    public int getSamplingRate() throws YAPI_Exception
    {
        return get_samplingRate();
    }

    /**
     * Returns the type of automatic conditional capture.
     *
     *  @return a value among YInputCapture.CAPTURETYPE_NONE, YInputCapture.CAPTURETYPE_TIMED,
     *  YInputCapture.CAPTURETYPE_V_MAX, YInputCapture.CAPTURETYPE_V_MIN, YInputCapture.CAPTURETYPE_I_MAX,
     *  YInputCapture.CAPTURETYPE_I_MIN, YInputCapture.CAPTURETYPE_P_MAX, YInputCapture.CAPTURETYPE_P_MIN,
     *  YInputCapture.CAPTURETYPE_V_AVG_MAX, YInputCapture.CAPTURETYPE_V_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_V_RMS_MAX, YInputCapture.CAPTURETYPE_V_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_I_AVG_MAX, YInputCapture.CAPTURETYPE_I_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_I_RMS_MAX, YInputCapture.CAPTURETYPE_I_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_P_AVG_MAX, YInputCapture.CAPTURETYPE_P_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_PF_MIN and YInputCapture.CAPTURETYPE_DPF_MIN corresponding to the type of
     * automatic conditional capture
     *
     * @throws YAPI_Exception on error
     */
    public int get_captureType() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return CAPTURETYPE_INVALID;
                }
            }
            res = _captureType;
        }
        return res;
    }

    /**
     * Returns the type of automatic conditional capture.
     *
     *  @return a value among YInputCapture.CAPTURETYPE_NONE, YInputCapture.CAPTURETYPE_TIMED,
     *  YInputCapture.CAPTURETYPE_V_MAX, YInputCapture.CAPTURETYPE_V_MIN, YInputCapture.CAPTURETYPE_I_MAX,
     *  YInputCapture.CAPTURETYPE_I_MIN, YInputCapture.CAPTURETYPE_P_MAX, YInputCapture.CAPTURETYPE_P_MIN,
     *  YInputCapture.CAPTURETYPE_V_AVG_MAX, YInputCapture.CAPTURETYPE_V_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_V_RMS_MAX, YInputCapture.CAPTURETYPE_V_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_I_AVG_MAX, YInputCapture.CAPTURETYPE_I_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_I_RMS_MAX, YInputCapture.CAPTURETYPE_I_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_P_AVG_MAX, YInputCapture.CAPTURETYPE_P_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_PF_MIN and YInputCapture.CAPTURETYPE_DPF_MIN corresponding to the type of
     * automatic conditional capture
     *
     * @throws YAPI_Exception on error
     */
    public int getCaptureType() throws YAPI_Exception
    {
        return get_captureType();
    }

    /**
     * Changes the type of automatic conditional capture.
     *
     *  @param newval : a value among YInputCapture.CAPTURETYPE_NONE, YInputCapture.CAPTURETYPE_TIMED,
     *  YInputCapture.CAPTURETYPE_V_MAX, YInputCapture.CAPTURETYPE_V_MIN, YInputCapture.CAPTURETYPE_I_MAX,
     *  YInputCapture.CAPTURETYPE_I_MIN, YInputCapture.CAPTURETYPE_P_MAX, YInputCapture.CAPTURETYPE_P_MIN,
     *  YInputCapture.CAPTURETYPE_V_AVG_MAX, YInputCapture.CAPTURETYPE_V_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_V_RMS_MAX, YInputCapture.CAPTURETYPE_V_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_I_AVG_MAX, YInputCapture.CAPTURETYPE_I_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_I_RMS_MAX, YInputCapture.CAPTURETYPE_I_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_P_AVG_MAX, YInputCapture.CAPTURETYPE_P_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_PF_MIN and YInputCapture.CAPTURETYPE_DPF_MIN corresponding to the type of
     * automatic conditional capture
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_captureType(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("captureType",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the type of automatic conditional capture.
     *
     *  @param newval : a value among YInputCapture.CAPTURETYPE_NONE, YInputCapture.CAPTURETYPE_TIMED,
     *  YInputCapture.CAPTURETYPE_V_MAX, YInputCapture.CAPTURETYPE_V_MIN, YInputCapture.CAPTURETYPE_I_MAX,
     *  YInputCapture.CAPTURETYPE_I_MIN, YInputCapture.CAPTURETYPE_P_MAX, YInputCapture.CAPTURETYPE_P_MIN,
     *  YInputCapture.CAPTURETYPE_V_AVG_MAX, YInputCapture.CAPTURETYPE_V_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_V_RMS_MAX, YInputCapture.CAPTURETYPE_V_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_I_AVG_MAX, YInputCapture.CAPTURETYPE_I_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_I_RMS_MAX, YInputCapture.CAPTURETYPE_I_RMS_MIN,
     *  YInputCapture.CAPTURETYPE_P_AVG_MAX, YInputCapture.CAPTURETYPE_P_AVG_MIN,
     *  YInputCapture.CAPTURETYPE_PF_MIN and YInputCapture.CAPTURETYPE_DPF_MIN corresponding to the type of
     * automatic conditional capture
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setCaptureType(int newval)  throws YAPI_Exception
    {
        return set_captureType(newval);
    }

    /**
     * Changes current threshold value for automatic conditional capture.
     *
     *  @param newval : a floating point number corresponding to current threshold value for automatic
     * conditional capture
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_condValue(double  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Long.toString(Math.round(newval * 65536.0));
            _setAttr("condValue",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes current threshold value for automatic conditional capture.
     *
     *  @param newval : a floating point number corresponding to current threshold value for automatic
     * conditional capture
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setCondValue(double newval)  throws YAPI_Exception
    {
        return set_condValue(newval);
    }

    /**
     * Returns current threshold value for automatic conditional capture.
     *
     * @return a floating point number corresponding to current threshold value for automatic conditional capture
     *
     * @throws YAPI_Exception on error
     */
    public double get_condValue() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return CONDVALUE_INVALID;
                }
            }
            res = _condValue;
        }
        return res;
    }

    /**
     * Returns current threshold value for automatic conditional capture.
     *
     * @return a floating point number corresponding to current threshold value for automatic conditional capture
     *
     * @throws YAPI_Exception on error
     */
    public double getCondValue() throws YAPI_Exception
    {
        return get_condValue();
    }

    /**
     * Returns the relative position of the trigger event within the capture window.
     * When the value is 50%, the capture is centered on the event.
     *
     * @return an integer corresponding to the relative position of the trigger event within the capture window
     *
     * @throws YAPI_Exception on error
     */
    public int get_condAlign() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return CONDALIGN_INVALID;
                }
            }
            res = _condAlign;
        }
        return res;
    }

    /**
     * Returns the relative position of the trigger event within the capture window.
     * When the value is 50%, the capture is centered on the event.
     *
     * @return an integer corresponding to the relative position of the trigger event within the capture window
     *
     * @throws YAPI_Exception on error
     */
    public int getCondAlign() throws YAPI_Exception
    {
        return get_condAlign();
    }

    /**
     * Changes the relative position of the trigger event within the capture window.
     * The new value must be between 10% (on the left) and 90% (on the right).
     * When the value is 50%, the capture is centered on the event.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     * @param newval : an integer corresponding to the relative position of the trigger event within the capture window
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_condAlign(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("condAlign",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the relative position of the trigger event within the capture window.
     * The new value must be between 10% (on the left) and 90% (on the right).
     * When the value is 50%, the capture is centered on the event.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     * @param newval : an integer corresponding to the relative position of the trigger event within the capture window
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setCondAlign(int newval)  throws YAPI_Exception
    {
        return set_condAlign(newval);
    }

    /**
     * Returns the type of automatic conditional capture
     * applied at device power on.
     *
     *  @return a value among YInputCapture.CAPTURETYPEATSTARTUP_NONE,
     *  YInputCapture.CAPTURETYPEATSTARTUP_TIMED, YInputCapture.CAPTURETYPEATSTARTUP_V_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_PF_MIN and
     * YInputCapture.CAPTURETYPEATSTARTUP_DPF_MIN corresponding to the type of automatic conditional capture
     *         applied at device power on
     *
     * @throws YAPI_Exception on error
     */
    public int get_captureTypeAtStartup() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return CAPTURETYPEATSTARTUP_INVALID;
                }
            }
            res = _captureTypeAtStartup;
        }
        return res;
    }

    /**
     * Returns the type of automatic conditional capture
     * applied at device power on.
     *
     *  @return a value among YInputCapture.CAPTURETYPEATSTARTUP_NONE,
     *  YInputCapture.CAPTURETYPEATSTARTUP_TIMED, YInputCapture.CAPTURETYPEATSTARTUP_V_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_PF_MIN and
     * YInputCapture.CAPTURETYPEATSTARTUP_DPF_MIN corresponding to the type of automatic conditional capture
     *         applied at device power on
     *
     * @throws YAPI_Exception on error
     */
    public int getCaptureTypeAtStartup() throws YAPI_Exception
    {
        return get_captureTypeAtStartup();
    }

    /**
     * Changes the type of automatic conditional capture
     * applied at device power on.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     *  @param newval : a value among YInputCapture.CAPTURETYPEATSTARTUP_NONE,
     *  YInputCapture.CAPTURETYPEATSTARTUP_TIMED, YInputCapture.CAPTURETYPEATSTARTUP_V_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_PF_MIN and
     * YInputCapture.CAPTURETYPEATSTARTUP_DPF_MIN corresponding to the type of automatic conditional capture
     *         applied at device power on
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_captureTypeAtStartup(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("captureTypeAtStartup",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the type of automatic conditional capture
     * applied at device power on.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     *  @param newval : a value among YInputCapture.CAPTURETYPEATSTARTUP_NONE,
     *  YInputCapture.CAPTURETYPEATSTARTUP_TIMED, YInputCapture.CAPTURETYPEATSTARTUP_V_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_V_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_I_RMS_MIN, YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MAX,
     *  YInputCapture.CAPTURETYPEATSTARTUP_P_AVG_MIN, YInputCapture.CAPTURETYPEATSTARTUP_PF_MIN and
     * YInputCapture.CAPTURETYPEATSTARTUP_DPF_MIN corresponding to the type of automatic conditional capture
     *         applied at device power on
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setCaptureTypeAtStartup(int newval)  throws YAPI_Exception
    {
        return set_captureTypeAtStartup(newval);
    }

    /**
     * Changes current threshold value for automatic conditional
     * capture applied at device power on.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     * @param newval : a floating point number corresponding to current threshold value for automatic conditional
     *         capture applied at device power on
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_condValueAtStartup(double  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Long.toString(Math.round(newval * 65536.0));
            _setAttr("condValueAtStartup",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes current threshold value for automatic conditional
     * capture applied at device power on.
     *
     * If you want the change to be kept after a device reboot,
     * make sure  to call the matching module saveToFlash().
     *
     * @param newval : a floating point number corresponding to current threshold value for automatic conditional
     *         capture applied at device power on
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setCondValueAtStartup(double newval)  throws YAPI_Exception
    {
        return set_condValueAtStartup(newval);
    }

    /**
     * Returns the threshold value for automatic conditional
     * capture applied at device power on.
     *
     * @return a floating point number corresponding to the threshold value for automatic conditional
     *         capture applied at device power on
     *
     * @throws YAPI_Exception on error
     */
    public double get_condValueAtStartup() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return CONDVALUEATSTARTUP_INVALID;
                }
            }
            res = _condValueAtStartup;
        }
        return res;
    }

    /**
     * Returns the threshold value for automatic conditional
     * capture applied at device power on.
     *
     * @return a floating point number corresponding to the threshold value for automatic conditional
     *         capture applied at device power on
     *
     * @throws YAPI_Exception on error
     */
    public double getCondValueAtStartup() throws YAPI_Exception
    {
        return get_condValueAtStartup();
    }

    /**
     * Retrieves an instant snapshot trigger for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the instant snapshot trigger is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YInputCapture.isOnline() to test if the instant snapshot trigger is
     * indeed online at a given time. In case of ambiguity when looking for
     * an instant snapshot trigger by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the instant snapshot trigger, for instance
     *         MyDevice.inputCapture.
     *
     * @return a YInputCapture object allowing you to drive the instant snapshot trigger.
     */
    public static YInputCapture FindInputCapture(String func)
    {
        YInputCapture obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YInputCapture) YFunction._FindFromCache("InputCapture", func);
            if (obj == null) {
                obj = new YInputCapture(func);
                YFunction._AddToCache("InputCapture", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves an instant snapshot trigger for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the instant snapshot trigger is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YInputCapture.isOnline() to test if the instant snapshot trigger is
     * indeed online at a given time. In case of ambiguity when looking for
     * an instant snapshot trigger by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the instant snapshot trigger, for instance
     *         MyDevice.inputCapture.
     *
     * @return a YInputCapture object allowing you to drive the instant snapshot trigger.
     */
    public static YInputCapture FindInputCaptureInContext(YAPIContext yctx,String func)
    {
        YInputCapture obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YInputCapture) YFunction._FindFromCacheInContext(yctx, "InputCapture", func);
            if (obj == null) {
                obj = new YInputCapture(yctx, func);
                YFunction._AddToCache("InputCapture", func, obj);
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
        _valueCallbackInputCapture = callback;
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
        if (_valueCallbackInputCapture != null) {
            _valueCallbackInputCapture.yNewValue(this, value);
        } else {
            super._invokeValueCallback(value);
        }
        return 0;
    }

    /**
     * Returns all details about the last automatic input capture.
     *
     * @return an YInputCaptureData object including
     *         data series and all related meta-information.
     * @throws YAPI_Exception on error
     */
    public YInputCaptureData get_lastCapture() throws YAPI_Exception
    {
        byte[] snapData = new byte[0];

        snapData = _download("snap.bin");
        return new YInputCaptureData(this, snapData);
    }

    /**
     * Returns a new immediate capture of the device inputs.
     *
     * @param msDuration : duration of the capture window,
     *         in milliseconds (eg. between 20 and 1000).
     *
     * @return an YInputCaptureData object including
     *         data series for the specified duration.
     * @throws YAPI_Exception on error
     */
    public YInputCaptureData get_immediateCapture(int msDuration) throws YAPI_Exception
    {
        String snapUrl;
        byte[] snapData = new byte[0];
        int snapStart;
        if (msDuration < 1) {
            msDuration = 20;
        }
        if (msDuration > 1000) {
            msDuration = 1000;
        }
        snapStart = (-msDuration / 2);
        snapUrl = String.format(Locale.US, "snap.bin?t=%d&d=%d",snapStart,msDuration);

        snapData = _download(snapUrl);
        return new YInputCaptureData(this, snapData);
    }

    /**
     * Continues the enumeration of instant snapshot triggers started using yFirstInputCapture().
     * Caution: You can't make any assumption about the returned instant snapshot triggers order.
     * If you want to find a specific an instant snapshot trigger, use InputCapture.findInputCapture()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YInputCapture object, corresponding to
     *         an instant snapshot trigger currently online, or a null pointer
     *         if there are no more instant snapshot triggers to enumerate.
     */
    public YInputCapture nextInputCapture()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindInputCaptureInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of instant snapshot triggers currently accessible.
     * Use the method YInputCapture.nextInputCapture() to iterate on
     * next instant snapshot triggers.
     *
     * @return a pointer to a YInputCapture object, corresponding to
     *         the first instant snapshot trigger currently online, or a null pointer
     *         if there are none.
     */
    public static YInputCapture FirstInputCapture()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("InputCapture");
        if (next_hwid == null)  return null;
        return FindInputCaptureInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of instant snapshot triggers currently accessible.
     * Use the method YInputCapture.nextInputCapture() to iterate on
     * next instant snapshot triggers.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YInputCapture object, corresponding to
     *         the first instant snapshot trigger currently online, or a null pointer
     *         if there are none.
     */
    public static YInputCapture FirstInputCaptureInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("InputCapture");
        if (next_hwid == null)  return null;
        return FindInputCaptureInContext(yctx, next_hwid);
    }

    //--- (end of generated code: YInputCapture implementation)
}

