/*
 *
 *  $Id: svn_id $
 *
 *  Implements FindPwmInput(), the high-level API for PwmInput functions
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

//--- (YPwmInput return codes)
//--- (end of YPwmInput return codes)
//--- (YPwmInput yapiwrapper)
//--- (end of YPwmInput yapiwrapper)
//--- (YPwmInput class start)
/**
 * YPwmInput Class: PWM input control interface, available for instance in the Yocto-PWM-Rx
 *
 * The YPwmInput class allows you to read and configure Yoctopuce PWM inputs.
 * It inherits from YSensor class the core functions to read measurements,
 * to register callback functions, and to access the autonomous datalogger.
 * This class adds the ability to configure the signal parameter used to transmit
 * information: the duty cycle, the frequency or the pulse width.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YPwmInput extends YSensor
{
//--- (end of YPwmInput class start)
//--- (YPwmInput definitions)
    /**
     * invalid dutyCycle value
     */
    public static final double DUTYCYCLE_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid pulseDuration value
     */
    public static final double PULSEDURATION_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid frequency value
     */
    public static final double FREQUENCY_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid period value
     */
    public static final double PERIOD_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid pulseCounter value
     */
    public static final long PULSECOUNTER_INVALID = YAPI.INVALID_LONG;
    /**
     * invalid pulseTimer value
     */
    public static final long PULSETIMER_INVALID = YAPI.INVALID_LONG;
    /**
     * invalid pwmReportMode value
     */
    public static final int PWMREPORTMODE_PWM_DUTYCYCLE = 0;
    public static final int PWMREPORTMODE_PWM_FREQUENCY = 1;
    public static final int PWMREPORTMODE_PWM_PULSEDURATION = 2;
    public static final int PWMREPORTMODE_PWM_EDGECOUNT = 3;
    public static final int PWMREPORTMODE_PWM_PULSECOUNT = 4;
    public static final int PWMREPORTMODE_PWM_CPS = 5;
    public static final int PWMREPORTMODE_PWM_CPM = 6;
    public static final int PWMREPORTMODE_PWM_STATE = 7;
    public static final int PWMREPORTMODE_PWM_FREQ_CPS = 8;
    public static final int PWMREPORTMODE_PWM_FREQ_CPM = 9;
    public static final int PWMREPORTMODE_PWM_PERIODCOUNT = 10;
    public static final int PWMREPORTMODE_INVALID = -1;
    /**
     * invalid debouncePeriod value
     */
    public static final int DEBOUNCEPERIOD_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid minFrequency value
     */
    public static final double MINFREQUENCY_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid bandwidth value
     */
    public static final int BANDWIDTH_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid edgesPerPeriod value
     */
    public static final int EDGESPERPERIOD_INVALID = YAPI.INVALID_UINT;
    protected double _dutyCycle = DUTYCYCLE_INVALID;
    protected double _pulseDuration = PULSEDURATION_INVALID;
    protected double _frequency = FREQUENCY_INVALID;
    protected double _period = PERIOD_INVALID;
    protected long _pulseCounter = PULSECOUNTER_INVALID;
    protected long _pulseTimer = PULSETIMER_INVALID;
    protected int _pwmReportMode = PWMREPORTMODE_INVALID;
    protected int _debouncePeriod = DEBOUNCEPERIOD_INVALID;
    protected double _minFrequency = MINFREQUENCY_INVALID;
    protected int _bandwidth = BANDWIDTH_INVALID;
    protected int _edgesPerPeriod = EDGESPERPERIOD_INVALID;
    protected UpdateCallback _valueCallbackPwmInput = null;
    protected TimedReportCallback _timedReportCallbackPwmInput = null;

    /**
     * Deprecated UpdateCallback for PwmInput
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YPwmInput function, String functionValue);
    }

    /**
     * TimedReportCallback for PwmInput
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YPwmInput  function, YMeasure measure);
    }
    //--- (end of YPwmInput definitions)


    /**
     *
     * @param func : functionid
     */
    protected YPwmInput(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "PwmInput";
        //--- (YPwmInput attributes initialization)
        //--- (end of YPwmInput attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YPwmInput(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (YPwmInput implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("dutyCycle")) {
            _dutyCycle = Math.round(json_val.getDouble("dutyCycle") / 65.536) / 1000.0;
        }
        if (json_val.has("pulseDuration")) {
            _pulseDuration = Math.round(json_val.getDouble("pulseDuration") / 65.536) / 1000.0;
        }
        if (json_val.has("frequency")) {
            _frequency = Math.round(json_val.getDouble("frequency") / 65.536) / 1000.0;
        }
        if (json_val.has("period")) {
            _period = Math.round(json_val.getDouble("period") / 65.536) / 1000.0;
        }
        if (json_val.has("pulseCounter")) {
            _pulseCounter = json_val.getLong("pulseCounter");
        }
        if (json_val.has("pulseTimer")) {
            _pulseTimer = json_val.getLong("pulseTimer");
        }
        if (json_val.has("pwmReportMode")) {
            _pwmReportMode = json_val.getInt("pwmReportMode");
        }
        if (json_val.has("debouncePeriod")) {
            _debouncePeriod = json_val.getInt("debouncePeriod");
        }
        if (json_val.has("minFrequency")) {
            _minFrequency = Math.round(json_val.getDouble("minFrequency") / 65.536) / 1000.0;
        }
        if (json_val.has("bandwidth")) {
            _bandwidth = json_val.getInt("bandwidth");
        }
        if (json_val.has("edgesPerPeriod")) {
            _edgesPerPeriod = json_val.getInt("edgesPerPeriod");
        }
        super._parseAttr(json_val);
    }

    /**
     * Changes the measuring unit for the measured quantity. That unit
     * is just a string which is automatically initialized each time
     * the measurement mode is changed. But is can be set to an
     * arbitrary value.
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     * @param newval : a string corresponding to the measuring unit for the measured quantity
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
     * Changes the measuring unit for the measured quantity. That unit
     * is just a string which is automatically initialized each time
     * the measurement mode is changed. But is can be set to an
     * arbitrary value.
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     * @param newval : a string corresponding to the measuring unit for the measured quantity
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
     * Returns the PWM duty cycle, in per cents.
     *
     * @return a floating point number corresponding to the PWM duty cycle, in per cents
     *
     * @throws YAPI_Exception on error
     */
    public double get_dutyCycle() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return DUTYCYCLE_INVALID;
                }
            }
            res = _dutyCycle;
        }
        return res;
    }

    /**
     * Returns the PWM duty cycle, in per cents.
     *
     * @return a floating point number corresponding to the PWM duty cycle, in per cents
     *
     * @throws YAPI_Exception on error
     */
    public double getDutyCycle() throws YAPI_Exception
    {
        return get_dutyCycle();
    }

    /**
     * Returns the PWM pulse length in milliseconds, as a floating point number.
     *
     *  @return a floating point number corresponding to the PWM pulse length in milliseconds, as a
     * floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double get_pulseDuration() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PULSEDURATION_INVALID;
                }
            }
            res = _pulseDuration;
        }
        return res;
    }

    /**
     * Returns the PWM pulse length in milliseconds, as a floating point number.
     *
     *  @return a floating point number corresponding to the PWM pulse length in milliseconds, as a
     * floating point number
     *
     * @throws YAPI_Exception on error
     */
    public double getPulseDuration() throws YAPI_Exception
    {
        return get_pulseDuration();
    }

    /**
     * Returns the PWM frequency in Hz.
     *
     * @return a floating point number corresponding to the PWM frequency in Hz
     *
     * @throws YAPI_Exception on error
     */
    public double get_frequency() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return FREQUENCY_INVALID;
                }
            }
            res = _frequency;
        }
        return res;
    }

    /**
     * Returns the PWM frequency in Hz.
     *
     * @return a floating point number corresponding to the PWM frequency in Hz
     *
     * @throws YAPI_Exception on error
     */
    public double getFrequency() throws YAPI_Exception
    {
        return get_frequency();
    }

    /**
     * Returns the PWM period in milliseconds.
     *
     * @return a floating point number corresponding to the PWM period in milliseconds
     *
     * @throws YAPI_Exception on error
     */
    public double get_period() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PERIOD_INVALID;
                }
            }
            res = _period;
        }
        return res;
    }

    /**
     * Returns the PWM period in milliseconds.
     *
     * @return a floating point number corresponding to the PWM period in milliseconds
     *
     * @throws YAPI_Exception on error
     */
    public double getPeriod() throws YAPI_Exception
    {
        return get_period();
    }

    /**
     * Returns the pulse counter value. Actually that
     * counter is incremented twice per period. That counter is
     * limited  to 1 billion.
     *
     * @return an integer corresponding to the pulse counter value
     *
     * @throws YAPI_Exception on error
     */
    public long get_pulseCounter() throws YAPI_Exception
    {
        long res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PULSECOUNTER_INVALID;
                }
            }
            res = _pulseCounter;
        }
        return res;
    }

    /**
     * Returns the pulse counter value. Actually that
     * counter is incremented twice per period. That counter is
     * limited  to 1 billion.
     *
     * @return an integer corresponding to the pulse counter value
     *
     * @throws YAPI_Exception on error
     */
    public long getPulseCounter() throws YAPI_Exception
    {
        return get_pulseCounter();
    }

    public int set_pulseCounter(long  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Long.toString(newval);
            _setAttr("pulseCounter",rest_val);
        }
        return YAPI.SUCCESS;
    }


    /**
     * Returns the timer of the pulses counter (ms).
     *
     * @return an integer corresponding to the timer of the pulses counter (ms)
     *
     * @throws YAPI_Exception on error
     */
    public long get_pulseTimer() throws YAPI_Exception
    {
        long res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PULSETIMER_INVALID;
                }
            }
            res = _pulseTimer;
        }
        return res;
    }

    /**
     * Returns the timer of the pulses counter (ms).
     *
     * @return an integer corresponding to the timer of the pulses counter (ms)
     *
     * @throws YAPI_Exception on error
     */
    public long getPulseTimer() throws YAPI_Exception
    {
        return get_pulseTimer();
    }

    /**
     *  Returns the parameter (frequency/duty cycle, pulse width, edges count) returned by the
     * get_currentValue function and callbacks. Attention
     *
     *  @return a value among YPwmInput.PWMREPORTMODE_PWM_DUTYCYCLE, YPwmInput.PWMREPORTMODE_PWM_FREQUENCY,
     *  YPwmInput.PWMREPORTMODE_PWM_PULSEDURATION, YPwmInput.PWMREPORTMODE_PWM_EDGECOUNT,
     *  YPwmInput.PWMREPORTMODE_PWM_PULSECOUNT, YPwmInput.PWMREPORTMODE_PWM_CPS,
     *  YPwmInput.PWMREPORTMODE_PWM_CPM, YPwmInput.PWMREPORTMODE_PWM_STATE,
     *  YPwmInput.PWMREPORTMODE_PWM_FREQ_CPS, YPwmInput.PWMREPORTMODE_PWM_FREQ_CPM and
     *  YPwmInput.PWMREPORTMODE_PWM_PERIODCOUNT corresponding to the parameter (frequency/duty cycle, pulse
     * width, edges count) returned by the get_currentValue function and callbacks
     *
     * @throws YAPI_Exception on error
     */
    public int get_pwmReportMode() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PWMREPORTMODE_INVALID;
                }
            }
            res = _pwmReportMode;
        }
        return res;
    }

    /**
     *  Returns the parameter (frequency/duty cycle, pulse width, edges count) returned by the
     * get_currentValue function and callbacks. Attention
     *
     *  @return a value among YPwmInput.PWMREPORTMODE_PWM_DUTYCYCLE, YPwmInput.PWMREPORTMODE_PWM_FREQUENCY,
     *  YPwmInput.PWMREPORTMODE_PWM_PULSEDURATION, YPwmInput.PWMREPORTMODE_PWM_EDGECOUNT,
     *  YPwmInput.PWMREPORTMODE_PWM_PULSECOUNT, YPwmInput.PWMREPORTMODE_PWM_CPS,
     *  YPwmInput.PWMREPORTMODE_PWM_CPM, YPwmInput.PWMREPORTMODE_PWM_STATE,
     *  YPwmInput.PWMREPORTMODE_PWM_FREQ_CPS, YPwmInput.PWMREPORTMODE_PWM_FREQ_CPM and
     *  YPwmInput.PWMREPORTMODE_PWM_PERIODCOUNT corresponding to the parameter (frequency/duty cycle, pulse
     * width, edges count) returned by the get_currentValue function and callbacks
     *
     * @throws YAPI_Exception on error
     */
    public int getPwmReportMode() throws YAPI_Exception
    {
        return get_pwmReportMode();
    }

    /**
     *  Changes the  parameter  type (frequency/duty cycle, pulse width, or edge count) returned by the
     * get_currentValue function and callbacks.
     *  The edge count value is limited to the 6 lowest digits. For values greater than one million, use
     * get_pulseCounter().
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     *  @param newval : a value among YPwmInput.PWMREPORTMODE_PWM_DUTYCYCLE,
     *  YPwmInput.PWMREPORTMODE_PWM_FREQUENCY, YPwmInput.PWMREPORTMODE_PWM_PULSEDURATION,
     *  YPwmInput.PWMREPORTMODE_PWM_EDGECOUNT, YPwmInput.PWMREPORTMODE_PWM_PULSECOUNT,
     *  YPwmInput.PWMREPORTMODE_PWM_CPS, YPwmInput.PWMREPORTMODE_PWM_CPM,
     *  YPwmInput.PWMREPORTMODE_PWM_STATE, YPwmInput.PWMREPORTMODE_PWM_FREQ_CPS,
     *  YPwmInput.PWMREPORTMODE_PWM_FREQ_CPM and YPwmInput.PWMREPORTMODE_PWM_PERIODCOUNT corresponding to
     *  the  parameter  type (frequency/duty cycle, pulse width, or edge count) returned by the
     * get_currentValue function and callbacks
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_pwmReportMode(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("pwmReportMode",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     *  Changes the  parameter  type (frequency/duty cycle, pulse width, or edge count) returned by the
     * get_currentValue function and callbacks.
     *  The edge count value is limited to the 6 lowest digits. For values greater than one million, use
     * get_pulseCounter().
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     *  @param newval : a value among YPwmInput.PWMREPORTMODE_PWM_DUTYCYCLE,
     *  YPwmInput.PWMREPORTMODE_PWM_FREQUENCY, YPwmInput.PWMREPORTMODE_PWM_PULSEDURATION,
     *  YPwmInput.PWMREPORTMODE_PWM_EDGECOUNT, YPwmInput.PWMREPORTMODE_PWM_PULSECOUNT,
     *  YPwmInput.PWMREPORTMODE_PWM_CPS, YPwmInput.PWMREPORTMODE_PWM_CPM,
     *  YPwmInput.PWMREPORTMODE_PWM_STATE, YPwmInput.PWMREPORTMODE_PWM_FREQ_CPS,
     *  YPwmInput.PWMREPORTMODE_PWM_FREQ_CPM and YPwmInput.PWMREPORTMODE_PWM_PERIODCOUNT corresponding to
     *  the  parameter  type (frequency/duty cycle, pulse width, or edge count) returned by the
     * get_currentValue function and callbacks
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setPwmReportMode(int newval)  throws YAPI_Exception
    {
        return set_pwmReportMode(newval);
    }

    /**
     * Returns the shortest expected pulse duration, in ms. Any shorter pulse will be automatically ignored (debounce).
     *
     * @return an integer corresponding to the shortest expected pulse duration, in ms
     *
     * @throws YAPI_Exception on error
     */
    public int get_debouncePeriod() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return DEBOUNCEPERIOD_INVALID;
                }
            }
            res = _debouncePeriod;
        }
        return res;
    }

    /**
     * Returns the shortest expected pulse duration, in ms. Any shorter pulse will be automatically ignored (debounce).
     *
     * @return an integer corresponding to the shortest expected pulse duration, in ms
     *
     * @throws YAPI_Exception on error
     */
    public int getDebouncePeriod() throws YAPI_Exception
    {
        return get_debouncePeriod();
    }

    /**
     * Changes the shortest expected pulse duration, in ms. Any shorter pulse will be automatically ignored (debounce).
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     * @param newval : an integer corresponding to the shortest expected pulse duration, in ms
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_debouncePeriod(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("debouncePeriod",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the shortest expected pulse duration, in ms. Any shorter pulse will be automatically ignored (debounce).
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     * @param newval : an integer corresponding to the shortest expected pulse duration, in ms
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setDebouncePeriod(int newval)  throws YAPI_Exception
    {
        return set_debouncePeriod(newval);
    }

    /**
     * Changes the minimum detected frequency, in Hz. Slower signals will be consider as zero frequency.
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     * @param newval : a floating point number corresponding to the minimum detected frequency, in Hz
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_minFrequency(double  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Long.toString(Math.round(newval * 65536.0));
            _setAttr("minFrequency",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the minimum detected frequency, in Hz. Slower signals will be consider as zero frequency.
     * Remember to call the saveToFlash() method of the module if the modification must be kept.
     *
     * @param newval : a floating point number corresponding to the minimum detected frequency, in Hz
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setMinFrequency(double newval)  throws YAPI_Exception
    {
        return set_minFrequency(newval);
    }

    /**
     * Returns the minimum detected frequency, in Hz. Slower signals will be consider as zero frequency.
     *
     * @return a floating point number corresponding to the minimum detected frequency, in Hz
     *
     * @throws YAPI_Exception on error
     */
    public double get_minFrequency() throws YAPI_Exception
    {
        double res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return MINFREQUENCY_INVALID;
                }
            }
            res = _minFrequency;
        }
        return res;
    }

    /**
     * Returns the minimum detected frequency, in Hz. Slower signals will be consider as zero frequency.
     *
     * @return a floating point number corresponding to the minimum detected frequency, in Hz
     *
     * @throws YAPI_Exception on error
     */
    public double getMinFrequency() throws YAPI_Exception
    {
        return get_minFrequency();
    }

    /**
     * Returns the input signal sampling rate, in kHz.
     *
     * @return an integer corresponding to the input signal sampling rate, in kHz
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
     * Returns the input signal sampling rate, in kHz.
     *
     * @return an integer corresponding to the input signal sampling rate, in kHz
     *
     * @throws YAPI_Exception on error
     */
    public int getBandwidth() throws YAPI_Exception
    {
        return get_bandwidth();
    }

    /**
     * Changes the input signal sampling rate, measured in kHz.
     * A lower sampling frequency can be used to hide hide-frequency bounce effects,
     * for instance on electromechanical contacts, but limits the measure resolution.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     * @param newval : an integer corresponding to the input signal sampling rate, measured in kHz
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
     * Changes the input signal sampling rate, measured in kHz.
     * A lower sampling frequency can be used to hide hide-frequency bounce effects,
     * for instance on electromechanical contacts, but limits the measure resolution.
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     *
     * @param newval : an integer corresponding to the input signal sampling rate, measured in kHz
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
     * Returns the number of edges detected per preiod. For a clean PWM signal, this should be exactly two,
     * but in cas the signal is created by a mechanical contact with bounces, it can get higher.
     *
     * @return an integer corresponding to the number of edges detected per preiod
     *
     * @throws YAPI_Exception on error
     */
    public int get_edgesPerPeriod() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return EDGESPERPERIOD_INVALID;
                }
            }
            res = _edgesPerPeriod;
        }
        return res;
    }

    /**
     * Returns the number of edges detected per preiod. For a clean PWM signal, this should be exactly two,
     * but in cas the signal is created by a mechanical contact with bounces, it can get higher.
     *
     * @return an integer corresponding to the number of edges detected per preiod
     *
     * @throws YAPI_Exception on error
     */
    public int getEdgesPerPeriod() throws YAPI_Exception
    {
        return get_edgesPerPeriod();
    }

    /**
     * Retrieves a PWM input for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the PWM input is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YPwmInput.isOnline() to test if the PWM input is
     * indeed online at a given time. In case of ambiguity when looking for
     * a PWM input by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the PWM input, for instance
     *         YPWMRX01.pwmInput1.
     *
     * @return a YPwmInput object allowing you to drive the PWM input.
     */
    public static YPwmInput FindPwmInput(String func)
    {
        YPwmInput obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YPwmInput) YFunction._FindFromCache("PwmInput", func);
            if (obj == null) {
                obj = new YPwmInput(func);
                YFunction._AddToCache("PwmInput", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a PWM input for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the PWM input is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YPwmInput.isOnline() to test if the PWM input is
     * indeed online at a given time. In case of ambiguity when looking for
     * a PWM input by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the PWM input, for instance
     *         YPWMRX01.pwmInput1.
     *
     * @return a YPwmInput object allowing you to drive the PWM input.
     */
    public static YPwmInput FindPwmInputInContext(YAPIContext yctx,String func)
    {
        YPwmInput obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YPwmInput) YFunction._FindFromCacheInContext(yctx, "PwmInput", func);
            if (obj == null) {
                obj = new YPwmInput(yctx, func);
                YFunction._AddToCache("PwmInput", func, obj);
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
        _valueCallbackPwmInput = callback;
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
        if (_valueCallbackPwmInput != null) {
            _valueCallbackPwmInput.yNewValue(this, value);
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
        _timedReportCallbackPwmInput = callback;
        return 0;
    }

    @Override
    public int _invokeTimedReportCallback(YMeasure value)
    {
        if (_timedReportCallbackPwmInput != null) {
            _timedReportCallbackPwmInput.timedReportCallback(this, value);
        } else {
            super._invokeTimedReportCallback(value);
        }
        return 0;
    }

    /**
     * Resets the periodicity detection algorithm.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int resetPeriodDetection() throws YAPI_Exception
    {
        return set_bandwidth(get_bandwidth());
    }

    /**
     * Resets the pulse counter value as well as its timer.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int resetCounter() throws YAPI_Exception
    {
        return set_pulseCounter(0);
    }

    /**
     * Continues the enumeration of PWM inputs started using yFirstPwmInput().
     * Caution: You can't make any assumption about the returned PWM inputs order.
     * If you want to find a specific a PWM input, use PwmInput.findPwmInput()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YPwmInput object, corresponding to
     *         a PWM input currently online, or a null pointer
     *         if there are no more PWM inputs to enumerate.
     */
    public YPwmInput nextPwmInput()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindPwmInputInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of PWM inputs currently accessible.
     * Use the method YPwmInput.nextPwmInput() to iterate on
     * next PWM inputs.
     *
     * @return a pointer to a YPwmInput object, corresponding to
     *         the first PWM input currently online, or a null pointer
     *         if there are none.
     */
    public static YPwmInput FirstPwmInput()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("PwmInput");
        if (next_hwid == null)  return null;
        return FindPwmInputInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of PWM inputs currently accessible.
     * Use the method YPwmInput.nextPwmInput() to iterate on
     * next PWM inputs.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YPwmInput object, corresponding to
     *         the first PWM input currently online, or a null pointer
     *         if there are none.
     */
    public static YPwmInput FirstPwmInputInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("PwmInput");
        if (next_hwid == null)  return null;
        return FindPwmInputInContext(yctx, next_hwid);
    }

    //--- (end of YPwmInput implementation)
}

