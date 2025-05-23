/*
 *
 *  $Id: svn_id $
 *
 *  Implements FindColorLedCluster(), the high-level API for ColorLedCluster functions
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
import java.util.Locale;
import java.util.ArrayList;

//--- (YColorLedCluster return codes)
//--- (end of YColorLedCluster return codes)
//--- (YColorLedCluster yapiwrapper)
//--- (end of YColorLedCluster yapiwrapper)
//--- (YColorLedCluster class start)
/**
 *  YColorLedCluster Class: RGB LED cluster control interface, available for instance in the
 * Yocto-Color-V2, the Yocto-MaxiBuzzer or the Yocto-MaxiKnob
 *
 * The YColorLedCluster class allows you to drive a
 * color LED cluster. Unlike the ColorLed class, the YColorLedCluster
 * class allows to handle several LEDs at once. Color changes can be done using RGB
 * coordinates as well as HSL coordinates.
 * The module performs all conversions form RGB to HSL automatically. It is then
 * self-evident to turn on a LED with a given hue and to progressively vary its
 * saturation or lightness. If needed, you can find more information on the
 * difference between RGB and HSL in the section following this one.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YColorLedCluster extends YFunction
{
//--- (end of YColorLedCluster class start)
//--- (YColorLedCluster definitions)
    /**
     * invalid activeLedCount value
     */
    public static final int ACTIVELEDCOUNT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid ledType value
     */
    public static final int LEDTYPE_RGB = 0;
    public static final int LEDTYPE_RGBW = 1;
    public static final int LEDTYPE_WS2811 = 2;
    public static final int LEDTYPE_INVALID = -1;
    /**
     * invalid maxLedCount value
     */
    public static final int MAXLEDCOUNT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid dynamicLedCount value
     */
    public static final int DYNAMICLEDCOUNT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid blinkSeqMaxCount value
     */
    public static final int BLINKSEQMAXCOUNT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid blinkSeqMaxSize value
     */
    public static final int BLINKSEQMAXSIZE_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid command value
     */
    public static final String COMMAND_INVALID = YAPI.INVALID_STRING;
    protected int _activeLedCount = ACTIVELEDCOUNT_INVALID;
    protected int _ledType = LEDTYPE_INVALID;
    protected int _maxLedCount = MAXLEDCOUNT_INVALID;
    protected int _dynamicLedCount = DYNAMICLEDCOUNT_INVALID;
    protected int _blinkSeqMaxCount = BLINKSEQMAXCOUNT_INVALID;
    protected int _blinkSeqMaxSize = BLINKSEQMAXSIZE_INVALID;
    protected String _command = COMMAND_INVALID;
    protected UpdateCallback _valueCallbackColorLedCluster = null;

    /**
     * Deprecated UpdateCallback for ColorLedCluster
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YColorLedCluster function, String functionValue);
    }

    /**
     * TimedReportCallback for ColorLedCluster
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YColorLedCluster  function, YMeasure measure);
    }
    //--- (end of YColorLedCluster definitions)


    /**
     *
     * @param func : functionid
     */
    protected YColorLedCluster(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "ColorLedCluster";
        //--- (YColorLedCluster attributes initialization)
        //--- (end of YColorLedCluster attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YColorLedCluster(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (YColorLedCluster implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("activeLedCount")) {
            _activeLedCount = json_val.getInt("activeLedCount");
        }
        if (json_val.has("ledType")) {
            _ledType = json_val.getInt("ledType");
        }
        if (json_val.has("maxLedCount")) {
            _maxLedCount = json_val.getInt("maxLedCount");
        }
        if (json_val.has("dynamicLedCount")) {
            _dynamicLedCount = json_val.getInt("dynamicLedCount");
        }
        if (json_val.has("blinkSeqMaxCount")) {
            _blinkSeqMaxCount = json_val.getInt("blinkSeqMaxCount");
        }
        if (json_val.has("blinkSeqMaxSize")) {
            _blinkSeqMaxSize = json_val.getInt("blinkSeqMaxSize");
        }
        if (json_val.has("command")) {
            _command = json_val.getString("command");
        }
        super._parseAttr(json_val);
    }

    /**
     * Returns the number of LEDs currently handled by the device.
     *
     * @return an integer corresponding to the number of LEDs currently handled by the device
     *
     * @throws YAPI_Exception on error
     */
    public int get_activeLedCount() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return ACTIVELEDCOUNT_INVALID;
                }
            }
            res = _activeLedCount;
        }
        return res;
    }

    /**
     * Returns the number of LEDs currently handled by the device.
     *
     * @return an integer corresponding to the number of LEDs currently handled by the device
     *
     * @throws YAPI_Exception on error
     */
    public int getActiveLedCount() throws YAPI_Exception
    {
        return get_activeLedCount();
    }

    /**
     * Changes the number of LEDs currently handled by the device.
     * Remember to call the matching module
     * saveToFlash() method to save the setting permanently.
     *
     * @param newval : an integer corresponding to the number of LEDs currently handled by the device
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_activeLedCount(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("activeLedCount",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the number of LEDs currently handled by the device.
     * Remember to call the matching module
     * saveToFlash() method to save the setting permanently.
     *
     * @param newval : an integer corresponding to the number of LEDs currently handled by the device
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setActiveLedCount(int newval)  throws YAPI_Exception
    {
        return set_activeLedCount(newval);
    }

    /**
     * Returns the RGB LED type currently handled by the device.
     *
     *  @return a value among YColorLedCluster.LEDTYPE_RGB, YColorLedCluster.LEDTYPE_RGBW and
     * YColorLedCluster.LEDTYPE_WS2811 corresponding to the RGB LED type currently handled by the device
     *
     * @throws YAPI_Exception on error
     */
    public int get_ledType() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return LEDTYPE_INVALID;
                }
            }
            res = _ledType;
        }
        return res;
    }

    /**
     * Returns the RGB LED type currently handled by the device.
     *
     *  @return a value among YColorLedCluster.LEDTYPE_RGB, YColorLedCluster.LEDTYPE_RGBW and
     * YColorLedCluster.LEDTYPE_WS2811 corresponding to the RGB LED type currently handled by the device
     *
     * @throws YAPI_Exception on error
     */
    public int getLedType() throws YAPI_Exception
    {
        return get_ledType();
    }

    /**
     * Changes the RGB LED type currently handled by the device.
     * Remember to call the matching module
     * saveToFlash() method to save the setting permanently.
     *
     *  @param newval : a value among YColorLedCluster.LEDTYPE_RGB, YColorLedCluster.LEDTYPE_RGBW and
     * YColorLedCluster.LEDTYPE_WS2811 corresponding to the RGB LED type currently handled by the device
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_ledType(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("ledType",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the RGB LED type currently handled by the device.
     * Remember to call the matching module
     * saveToFlash() method to save the setting permanently.
     *
     *  @param newval : a value among YColorLedCluster.LEDTYPE_RGB, YColorLedCluster.LEDTYPE_RGBW and
     * YColorLedCluster.LEDTYPE_WS2811 corresponding to the RGB LED type currently handled by the device
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setLedType(int newval)  throws YAPI_Exception
    {
        return set_ledType(newval);
    }

    /**
     * Returns the maximum number of LEDs that the device can handle.
     *
     * @return an integer corresponding to the maximum number of LEDs that the device can handle
     *
     * @throws YAPI_Exception on error
     */
    public int get_maxLedCount() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return MAXLEDCOUNT_INVALID;
                }
            }
            res = _maxLedCount;
        }
        return res;
    }

    /**
     * Returns the maximum number of LEDs that the device can handle.
     *
     * @return an integer corresponding to the maximum number of LEDs that the device can handle
     *
     * @throws YAPI_Exception on error
     */
    public int getMaxLedCount() throws YAPI_Exception
    {
        return get_maxLedCount();
    }

    /**
     * Returns the maximum number of LEDs that can perform autonomous transitions and sequences.
     *
     *  @return an integer corresponding to the maximum number of LEDs that can perform autonomous
     * transitions and sequences
     *
     * @throws YAPI_Exception on error
     */
    public int get_dynamicLedCount() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return DYNAMICLEDCOUNT_INVALID;
                }
            }
            res = _dynamicLedCount;
        }
        return res;
    }

    /**
     * Returns the maximum number of LEDs that can perform autonomous transitions and sequences.
     *
     *  @return an integer corresponding to the maximum number of LEDs that can perform autonomous
     * transitions and sequences
     *
     * @throws YAPI_Exception on error
     */
    public int getDynamicLedCount() throws YAPI_Exception
    {
        return get_dynamicLedCount();
    }

    /**
     * Returns the maximum number of sequences that the device can handle.
     *
     * @return an integer corresponding to the maximum number of sequences that the device can handle
     *
     * @throws YAPI_Exception on error
     */
    public int get_blinkSeqMaxCount() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return BLINKSEQMAXCOUNT_INVALID;
                }
            }
            res = _blinkSeqMaxCount;
        }
        return res;
    }

    /**
     * Returns the maximum number of sequences that the device can handle.
     *
     * @return an integer corresponding to the maximum number of sequences that the device can handle
     *
     * @throws YAPI_Exception on error
     */
    public int getBlinkSeqMaxCount() throws YAPI_Exception
    {
        return get_blinkSeqMaxCount();
    }

    /**
     * Returns the maximum length of sequences.
     *
     * @return an integer corresponding to the maximum length of sequences
     *
     * @throws YAPI_Exception on error
     */
    public int get_blinkSeqMaxSize() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return BLINKSEQMAXSIZE_INVALID;
                }
            }
            res = _blinkSeqMaxSize;
        }
        return res;
    }

    /**
     * Returns the maximum length of sequences.
     *
     * @return an integer corresponding to the maximum length of sequences
     *
     * @throws YAPI_Exception on error
     */
    public int getBlinkSeqMaxSize() throws YAPI_Exception
    {
        return get_blinkSeqMaxSize();
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
     * Retrieves a RGB LED cluster for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the RGB LED cluster is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YColorLedCluster.isOnline() to test if the RGB LED cluster is
     * indeed online at a given time. In case of ambiguity when looking for
     * a RGB LED cluster by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the RGB LED cluster, for instance
     *         YRGBLED2.colorLedCluster.
     *
     * @return a YColorLedCluster object allowing you to drive the RGB LED cluster.
     */
    public static YColorLedCluster FindColorLedCluster(String func)
    {
        YColorLedCluster obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YColorLedCluster) YFunction._FindFromCache("ColorLedCluster", func);
            if (obj == null) {
                obj = new YColorLedCluster(func);
                YFunction._AddToCache("ColorLedCluster", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a RGB LED cluster for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the RGB LED cluster is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YColorLedCluster.isOnline() to test if the RGB LED cluster is
     * indeed online at a given time. In case of ambiguity when looking for
     * a RGB LED cluster by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the RGB LED cluster, for instance
     *         YRGBLED2.colorLedCluster.
     *
     * @return a YColorLedCluster object allowing you to drive the RGB LED cluster.
     */
    public static YColorLedCluster FindColorLedClusterInContext(YAPIContext yctx,String func)
    {
        YColorLedCluster obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YColorLedCluster) YFunction._FindFromCacheInContext(yctx, "ColorLedCluster", func);
            if (obj == null) {
                obj = new YColorLedCluster(yctx, func);
                YFunction._AddToCache("ColorLedCluster", func, obj);
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
        _valueCallbackColorLedCluster = callback;
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
        if (_valueCallbackColorLedCluster != null) {
            _valueCallbackColorLedCluster.yNewValue(this, value);
        } else {
            super._invokeValueCallback(value);
        }
        return 0;
    }

    public int sendCommand(String command) throws YAPI_Exception
    {
        return set_command(command);
    }

    /**
     *  Changes the current color of consecutive LEDs in the cluster, using a RGB color. Encoding is done
     * as follows: 0xRRGGBB.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param rgbValue :  new color.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_rgbColor(int ledIndex,int count,int rgbValue) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "SR%d,%d,%x",ledIndex,count,rgbValue));
    }

    /**
     * Changes the  color at device startup of consecutive LEDs in the cluster, using a RGB color.
     * Encoding is done as follows: 0xRRGGBB. Don't forget to call saveLedsConfigAtPowerOn()
     * to make sure the modification is saved in the device flash memory.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param rgbValue :  new color.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_rgbColorAtPowerOn(int ledIndex,int count,int rgbValue) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "SC%d,%d,%x",ledIndex,count,rgbValue));
    }

    /**
     * Changes the  color at device startup of consecutive LEDs in the cluster, using a HSL color.
     * Encoding is done as follows: 0xHHSSLL. Don't forget to call saveLedsConfigAtPowerOn()
     * to make sure the modification is saved in the device flash memory.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param hslValue :  new color.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_hslColorAtPowerOn(int ledIndex,int count,int hslValue) throws YAPI_Exception
    {
        int rgbValue;
        rgbValue = hsl2rgb(hslValue);
        return sendCommand(String.format(Locale.US, "SC%d,%d,%x",ledIndex,count,rgbValue));
    }

    /**
     *  Changes the current color of consecutive LEDs in the cluster, using a HSL color. Encoding is done
     * as follows: 0xHHSSLL.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param hslValue :  new color.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_hslColor(int ledIndex,int count,int hslValue) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "SH%d,%d,%x",ledIndex,count,hslValue));
    }

    /**
     * Allows you to modify the current color of a group of adjacent LEDs to another color, in a seamless and
     * autonomous manner. The transition is performed in the RGB space.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param rgbValue :  new color (0xRRGGBB).
     * @param delay    :  transition duration in ms
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int rgb_move(int ledIndex,int count,int rgbValue,int delay) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "MR%d,%d,%x,%d",ledIndex,count,rgbValue,delay));
    }

    /**
     * Allows you to modify the current color of a group of adjacent LEDs  to another color, in a seamless and
     * autonomous manner. The transition is performed in the HSL space. In HSL, hue is a circular
     * value (0..360 deg). There are always two paths to perform the transition: by increasing
     * or by decreasing the hue. The module selects the shortest transition.
     * If the difference is exactly 180 deg, the module selects the transition which increases
     * the hue.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param hslValue :  new color (0xHHSSLL).
     * @param delay    :  transition duration in ms
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int hsl_move(int ledIndex,int count,int hslValue,int delay) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "MH%d,%d,%x,%d",ledIndex,count,hslValue,delay));
    }

    /**
     * Adds an RGB transition to a sequence. A sequence is a transition list, which can
     * be executed in loop by a group of LEDs.  Sequences are persistent and are saved
     * in the device flash memory as soon as the saveBlinkSeq() method is called.
     *
     * @param seqIndex :  sequence index.
     * @param rgbValue :  target color (0xRRGGBB)
     * @param delay    :  transition duration in ms
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int addRgbMoveToBlinkSeq(int seqIndex,int rgbValue,int delay) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "AR%d,%x,%d",seqIndex,rgbValue,delay));
    }

    /**
     * Adds an HSL transition to a sequence. A sequence is a transition list, which can
     * be executed in loop by an group of LEDs.  Sequences are persistent and are saved
     * in the device flash memory as soon as the saveBlinkSeq() method is called.
     *
     * @param seqIndex : sequence index.
     * @param hslValue : target color (0xHHSSLL)
     * @param delay    : transition duration in ms
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int addHslMoveToBlinkSeq(int seqIndex,int hslValue,int delay) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "AH%d,%x,%d",seqIndex,hslValue,delay));
    }

    /**
     * Adds a mirror ending to a sequence. When the sequence will reach the end of the last
     * transition, its running speed will automatically be reversed so that the sequence plays
     * in the reverse direction, like in a mirror. After the first transition of the sequence
     * is played at the end of the reverse execution, the sequence starts again in
     * the initial direction.
     *
     * @param seqIndex : sequence index.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int addMirrorToBlinkSeq(int seqIndex) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "AC%d,0,0",seqIndex));
    }

    /**
     * Adds to a sequence a jump to another sequence. When a pixel will reach this jump,
     * it will be automatically relinked to the new sequence, and will run it starting
     * from the beginning.
     *
     * @param seqIndex : sequence index.
     * @param linkSeqIndex : index of the sequence to chain.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int addJumpToBlinkSeq(int seqIndex,int linkSeqIndex) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "AC%d,100,%d,1000",seqIndex,linkSeqIndex));
    }

    /**
     * Adds a to a sequence a hard stop code. When a pixel will reach this stop code,
     * instead of restarting the sequence in a loop it will automatically be unlinked
     * from the sequence.
     *
     * @param seqIndex : sequence index.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int addUnlinkToBlinkSeq(int seqIndex) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "AC%d,100,-1,1000",seqIndex));
    }

    /**
     * Links adjacent LEDs to a specific sequence. These LEDs start to execute
     * the sequence as soon as  startBlinkSeq is called. It is possible to add an offset
     * in the execution: that way we  can have several groups of LED executing the same
     * sequence, with a  temporal offset. A LED cannot be linked to more than one sequence.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param seqIndex :  sequence index.
     * @param offset   :  execution offset in ms.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int linkLedToBlinkSeq(int ledIndex,int count,int seqIndex,int offset) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "LS%d,%d,%d,%d",ledIndex,count,seqIndex,offset));
    }

    /**
     * Links adjacent LEDs to a specific sequence at device power-on. Don't forget to configure
     * the sequence auto start flag as well and call saveLedsConfigAtPowerOn(). It is possible to add an offset
     * in the execution: that way we  can have several groups of LEDs executing the same
     * sequence, with a  temporal offset. A LED cannot be linked to more than one sequence.
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param seqIndex :  sequence index.
     * @param offset   :  execution offset in ms.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int linkLedToBlinkSeqAtPowerOn(int ledIndex,int count,int seqIndex,int offset) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "LO%d,%d,%d,%d",ledIndex,count,seqIndex,offset));
    }

    /**
     * Links adjacent LEDs to a specific sequence. These LED start to execute
     * the sequence as soon as  startBlinkSeq is called. This function automatically
     * introduces a shift between LEDs so that the specified number of sequence periods
     * appears on the group of LEDs (wave effect).
     *
     * @param ledIndex :  index of the first affected LED.
     * @param count    :  affected LED count.
     * @param seqIndex :  sequence index.
     * @param periods  :  number of periods to show on LEDs.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int linkLedToPeriodicBlinkSeq(int ledIndex,int count,int seqIndex,int periods) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "LP%d,%d,%d,%d",ledIndex,count,seqIndex,periods));
    }

    /**
     * Unlinks adjacent LEDs from a  sequence.
     *
     * @param ledIndex  :  index of the first affected LED.
     * @param count     :  affected LED count.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int unlinkLedFromBlinkSeq(int ledIndex,int count) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "US%d,%d",ledIndex,count));
    }

    /**
     * Starts a sequence execution: every LED linked to that sequence starts to
     * run it in a loop. Note that a sequence with a zero duration can't be started.
     *
     * @param seqIndex :  index of the sequence to start.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int startBlinkSeq(int seqIndex) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "SS%d",seqIndex));
    }

    /**
     * Stops a sequence execution. If started again, the execution
     * restarts from the beginning.
     *
     * @param seqIndex :  index of the sequence to stop.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int stopBlinkSeq(int seqIndex) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "XS%d",seqIndex));
    }

    /**
     * Stops a sequence execution and resets its contents. LEDs linked to this
     * sequence are not automatically updated anymore.
     *
     * @param seqIndex :  index of the sequence to reset
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int resetBlinkSeq(int seqIndex) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "ZS%d",seqIndex));
    }

    /**
     * Configures a sequence to make it start automatically at device
     * startup. Note that a sequence with a zero duration can't be started.
     * Don't forget to call saveBlinkSeq() to make sure the
     * modification is saved in the device flash memory.
     *
     * @param seqIndex :  index of the sequence to reset.
     * @param autostart : 0 to keep the sequence turned off and 1 to start it automatically.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_blinkSeqStateAtPowerOn(int seqIndex,int autostart) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "AS%d,%d",seqIndex,autostart));
    }

    /**
     * Changes the execution speed of a sequence. The natural execution speed is 1000 per
     * thousand. If you configure a slower speed, you can play the sequence in slow-motion.
     * If you set a negative speed, you can play the sequence in reverse direction.
     *
     * @param seqIndex :  index of the sequence to start.
     * @param speed :     sequence running speed (-1000...1000).
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_blinkSeqSpeed(int seqIndex,int speed) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "CS%d,%d",seqIndex,speed));
    }

    /**
     * Saves the LEDs power-on configuration. This includes the start-up color or
     * sequence binding for all LEDs. Warning: if some LEDs are linked to a sequence, the
     * method saveBlinkSeq() must also be called to save the sequence definition.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int saveLedsConfigAtPowerOn() throws YAPI_Exception
    {
        return sendCommand("WL");
    }

    public int saveLedsState() throws YAPI_Exception
    {
        return sendCommand("WL");
    }

    /**
     * Saves the definition of a sequence. Warning: only sequence steps and flags are saved.
     * to save the LEDs startup bindings, the method saveLedsConfigAtPowerOn()
     * must be called.
     *
     * @param seqIndex :  index of the sequence to start.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int saveBlinkSeq(int seqIndex) throws YAPI_Exception
    {
        return sendCommand(String.format(Locale.US, "WS%d",seqIndex));
    }

    /**
     * Sends a binary buffer to the LED RGB buffer, as is.
     * First three bytes are RGB components for LED specified as parameter, the
     * next three bytes for the next LED, etc.
     *
     * @param ledIndex : index of the first LED which should be updated
     * @param buff : the binary buffer to send
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_rgbColorBuffer(int ledIndex,byte[] buff) throws YAPI_Exception
    {
        return _upload(String.format(Locale.US, "rgb:0:%d",ledIndex), buff);
    }

    /**
     * Sends 24bit RGB colors (provided as a list of integers) to the LED RGB buffer, as is.
     * The first number represents the RGB value of the LED specified as parameter, the second
     * number represents the RGB value of the next LED, etc.
     *
     * @param ledIndex : index of the first LED which should be updated
     * @param rgbList : a list of 24bit RGB codes, in the form 0xRRGGBB
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_rgbColorArray(int ledIndex,ArrayList<Integer> rgbList) throws YAPI_Exception
    {
        int listlen;
        byte[] buff = new byte[0];
        int idx;
        int rgb;
        int res;
        listlen = rgbList.size();
        buff = new byte[3*listlen];
        idx = 0;
        while (idx < listlen) {
            rgb = rgbList.get(idx).intValue();
            buff[3*idx] = (byte)(((rgb >> 16) & 255) & 0xff);
            buff[3*idx+1] = (byte)(((rgb >> 8) & 255) & 0xff);
            buff[3*idx+2] = (byte)((rgb & 255) & 0xff);
            idx = idx + 1;
        }

        res = _upload(String.format(Locale.US, "rgb:0:%d",ledIndex), buff);
        return res;
    }

    /**
     * Sets up a smooth RGB color transition to the specified pixel-by-pixel list of RGB
     * color codes. The first color code represents the target RGB value of the first LED,
     * the next color code represents the target value of the next LED, etc.
     *
     * @param ledIndex : index of the first LED which should be updated
     * @param rgbList : a list of target 24bit RGB codes, in the form 0xRRGGBB
     * @param delay   : transition duration in ms
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int rgbArrayOfs_move(int ledIndex,ArrayList<Integer> rgbList,int delay) throws YAPI_Exception
    {
        int listlen;
        byte[] buff = new byte[0];
        int idx;
        int rgb;
        int res;
        listlen = rgbList.size();
        buff = new byte[3*listlen];
        idx = 0;
        while (idx < listlen) {
            rgb = rgbList.get(idx).intValue();
            buff[3*idx] = (byte)(((rgb >> 16) & 255) & 0xff);
            buff[3*idx+1] = (byte)(((rgb >> 8) & 255) & 0xff);
            buff[3*idx+2] = (byte)((rgb & 255) & 0xff);
            idx = idx + 1;
        }

        res = _upload(String.format(Locale.US, "rgb:%d:%d",delay,ledIndex), buff);
        return res;
    }

    /**
     * Sets up a smooth RGB color transition to the specified pixel-by-pixel list of RGB
     * color codes. The first color code represents the target RGB value of the first LED,
     * the next color code represents the target value of the next LED, etc.
     *
     * @param rgbList : a list of target 24bit RGB codes, in the form 0xRRGGBB
     * @param delay   : transition duration in ms
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int rgbArray_move(ArrayList<Integer> rgbList,int delay) throws YAPI_Exception
    {
        int res;

        res = rgbArrayOfs_move(0,rgbList,delay);
        return res;
    }

    /**
     * Sends a binary buffer to the LED HSL buffer, as is.
     * First three bytes are HSL components for the LED specified as parameter, the
     * next three bytes for the second LED, etc.
     *
     * @param ledIndex : index of the first LED which should be updated
     * @param buff : the binary buffer to send
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_hslColorBuffer(int ledIndex,byte[] buff) throws YAPI_Exception
    {
        return _upload(String.format(Locale.US, "hsl:0:%d",ledIndex), buff);
    }

    /**
     * Sends 24bit HSL colors (provided as a list of integers) to the LED HSL buffer, as is.
     * The first number represents the HSL value of the LED specified as parameter, the second number represents
     * the HSL value of the second LED, etc.
     *
     * @param ledIndex : index of the first LED which should be updated
     * @param hslList : a list of 24bit HSL codes, in the form 0xHHSSLL
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_hslColorArray(int ledIndex,ArrayList<Integer> hslList) throws YAPI_Exception
    {
        int listlen;
        byte[] buff = new byte[0];
        int idx;
        int hsl;
        int res;
        listlen = hslList.size();
        buff = new byte[3*listlen];
        idx = 0;
        while (idx < listlen) {
            hsl = hslList.get(idx).intValue();
            buff[3*idx] = (byte)(((hsl >> 16) & 255) & 0xff);
            buff[3*idx+1] = (byte)(((hsl >> 8) & 255) & 0xff);
            buff[3*idx+2] = (byte)((hsl & 255) & 0xff);
            idx = idx + 1;
        }

        res = _upload(String.format(Locale.US, "hsl:0:%d",ledIndex), buff);
        return res;
    }

    /**
     * Sets up a smooth HSL color transition to the specified pixel-by-pixel list of HSL
     * color codes. The first color code represents the target HSL value of the first LED,
     * the second color code represents the target value of the second LED, etc.
     *
     * @param hslList : a list of target 24bit HSL codes, in the form 0xHHSSLL
     * @param delay   : transition duration in ms
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int hslArray_move(ArrayList<Integer> hslList,int delay) throws YAPI_Exception
    {
        int res;

        res = hslArrayOfs_move(0,hslList, delay);
        return res;
    }

    /**
     * Sets up a smooth HSL color transition to the specified pixel-by-pixel list of HSL
     * color codes. The first color code represents the target HSL value of the first LED,
     * the second color code represents the target value of the second LED, etc.
     *
     * @param ledIndex : index of the first LED which should be updated
     * @param hslList : a list of target 24bit HSL codes, in the form 0xHHSSLL
     * @param delay   : transition duration in ms
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int hslArrayOfs_move(int ledIndex,ArrayList<Integer> hslList,int delay) throws YAPI_Exception
    {
        int listlen;
        byte[] buff = new byte[0];
        int idx;
        int hsl;
        int res;
        listlen = hslList.size();
        buff = new byte[3*listlen];
        idx = 0;
        while (idx < listlen) {
            hsl = hslList.get(idx).intValue();
            buff[3*idx] = (byte)(((hsl >> 16) & 255) & 0xff);
            buff[3*idx+1] = (byte)(((hsl >> 8) & 255) & 0xff);
            buff[3*idx+2] = (byte)((hsl & 255) & 0xff);
            idx = idx + 1;
        }

        res = _upload(String.format(Locale.US, "hsl:%d:%d",delay,ledIndex), buff);
        return res;
    }

    /**
     * Returns a binary buffer with content from the LED RGB buffer, as is.
     * First three bytes are RGB components for the first LED in the interval,
     * the next three bytes for the second LED in the interval, etc.
     *
     * @param ledIndex : index of the first LED which should be returned
     * @param count    : number of LEDs which should be returned
     *
     * @return a binary buffer with RGB components of selected LEDs.
     *
     * @throws YAPI_Exception on error
     */
    public byte[] get_rgbColorBuffer(int ledIndex,int count) throws YAPI_Exception
    {
        return _download(String.format(Locale.US, "rgb.bin?typ=0&pos=%d&len=%d",3*ledIndex,3*count));
    }

    /**
     * Returns a list on 24bit RGB color values with the current colors displayed on
     * the RGB LEDs. The first number represents the RGB value of the first LED,
     * the second number represents the RGB value of the second LED, etc.
     *
     * @param ledIndex : index of the first LED which should be returned
     * @param count    : number of LEDs which should be returned
     *
     * @return a list of 24bit color codes with RGB components of selected LEDs, as 0xRRGGBB.
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<Integer> get_rgbColorArray(int ledIndex,int count) throws YAPI_Exception
    {
        byte[] buff = new byte[0];
        ArrayList<Integer> res = new ArrayList<>();
        int idx;
        int r;
        int g;
        int b;

        buff = _download(String.format(Locale.US, "rgb.bin?typ=0&pos=%d&len=%d",3*ledIndex,3*count));
        res.clear();
        idx = 0;
        while (idx < count) {
            r = (buff[3*idx] & 0xff);
            g = (buff[3*idx+1] & 0xff);
            b = (buff[3*idx+2] & 0xff);
            res.add(r*65536+g*256+b);
            idx = idx + 1;
        }
        return res;
    }

    /**
     * Returns a list on 24bit RGB color values with the RGB LEDs startup colors.
     * The first number represents the startup RGB value of the first LED,
     * the second number represents the RGB value of the second LED, etc.
     *
     * @param ledIndex : index of the first LED  which should be returned
     * @param count    : number of LEDs which should be returned
     *
     * @return a list of 24bit color codes with RGB components of selected LEDs, as 0xRRGGBB.
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<Integer> get_rgbColorArrayAtPowerOn(int ledIndex,int count) throws YAPI_Exception
    {
        byte[] buff = new byte[0];
        ArrayList<Integer> res = new ArrayList<>();
        int idx;
        int r;
        int g;
        int b;

        buff = _download(String.format(Locale.US, "rgb.bin?typ=4&pos=%d&len=%d",3*ledIndex,3*count));
        res.clear();
        idx = 0;
        while (idx < count) {
            r = (buff[3*idx] & 0xff);
            g = (buff[3*idx+1] & 0xff);
            b = (buff[3*idx+2] & 0xff);
            res.add(r*65536+g*256+b);
            idx = idx + 1;
        }
        return res;
    }

    /**
     * Returns a list on sequence index for each RGB LED. The first number represents the
     * sequence index for the the first LED, the second number represents the sequence
     * index for the second LED, etc.
     *
     * @param ledIndex : index of the first LED which should be returned
     * @param count    : number of LEDs which should be returned
     *
     * @return a list of integers with sequence index
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<Integer> get_linkedSeqArray(int ledIndex,int count) throws YAPI_Exception
    {
        byte[] buff = new byte[0];
        ArrayList<Integer> res = new ArrayList<>();
        int idx;
        int seq;

        buff = _download(String.format(Locale.US, "rgb.bin?typ=1&pos=%d&len=%d",ledIndex,count));
        res.clear();
        idx = 0;
        while (idx < count) {
            seq = (buff[idx] & 0xff);
            res.add(seq);
            idx = idx + 1;
        }
        return res;
    }

    /**
     * Returns a list on 32 bit signatures for specified blinking sequences.
     * Since blinking sequences cannot be read from the device, this can be used
     * to detect if a specific blinking sequence is already programmed.
     *
     * @param seqIndex : index of the first blinking sequence which should be returned
     * @param count    : number of blinking sequences which should be returned
     *
     * @return a list of 32 bit integer signatures
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<Integer> get_blinkSeqSignatures(int seqIndex,int count) throws YAPI_Exception
    {
        byte[] buff = new byte[0];
        ArrayList<Integer> res = new ArrayList<>();
        int idx;
        int hh;
        int hl;
        int lh;
        int ll;

        buff = _download(String.format(Locale.US, "rgb.bin?typ=2&pos=%d&len=%d",4*seqIndex,4*count));
        res.clear();
        idx = 0;
        while (idx < count) {
            hh = (buff[4*idx] & 0xff);
            hl = (buff[4*idx+1] & 0xff);
            lh = (buff[4*idx+2] & 0xff);
            ll = (buff[4*idx+3] & 0xff);
            res.add((hh << 24)+(hl << 16)+(lh << 8)+ll);
            idx = idx + 1;
        }
        return res;
    }

    /**
     * Returns a list of integers with the current speed for specified blinking sequences.
     *
     * @param seqIndex : index of the first sequence speed which should be returned
     * @param count    : number of sequence speeds which should be returned
     *
     * @return a list of integers, 0 for sequences turned off and 1 for sequences running
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<Integer> get_blinkSeqStateSpeed(int seqIndex,int count) throws YAPI_Exception
    {
        byte[] buff = new byte[0];
        ArrayList<Integer> res = new ArrayList<>();
        int idx;
        int lh;
        int ll;

        buff = _download(String.format(Locale.US, "rgb.bin?typ=6&pos=%d&len=%d",seqIndex,count));
        res.clear();
        idx = 0;
        while (idx < count) {
            lh = (buff[2*idx] & 0xff);
            ll = (buff[2*idx+1] & 0xff);
            res.add((lh << 8)+ll);
            idx = idx + 1;
        }
        return res;
    }

    /**
     * Returns a list of integers with the "auto-start at power on" flag state for specified blinking sequences.
     *
     * @param seqIndex : index of the first blinking sequence which should be returned
     * @param count    : number of blinking sequences which should be returned
     *
     * @return a list of integers, 0 for sequences turned off and 1 for sequences running
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<Integer> get_blinkSeqStateAtPowerOn(int seqIndex,int count) throws YAPI_Exception
    {
        byte[] buff = new byte[0];
        ArrayList<Integer> res = new ArrayList<>();
        int idx;
        int started;

        buff = _download(String.format(Locale.US, "rgb.bin?typ=5&pos=%d&len=%d",seqIndex,count));
        res.clear();
        idx = 0;
        while (idx < count) {
            started = (buff[idx] & 0xff);
            res.add(started);
            idx = idx + 1;
        }
        return res;
    }

    /**
     * Returns a list of integers with the started state for specified blinking sequences.
     *
     * @param seqIndex : index of the first blinking sequence which should be returned
     * @param count    : number of blinking sequences which should be returned
     *
     * @return a list of integers, 0 for sequences turned off and 1 for sequences running
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<Integer> get_blinkSeqState(int seqIndex,int count) throws YAPI_Exception
    {
        byte[] buff = new byte[0];
        ArrayList<Integer> res = new ArrayList<>();
        int idx;
        int started;

        buff = _download(String.format(Locale.US, "rgb.bin?typ=3&pos=%d&len=%d",seqIndex,count));
        res.clear();
        idx = 0;
        while (idx < count) {
            started = (buff[idx] & 0xff);
            res.add(started);
            idx = idx + 1;
        }
        return res;
    }

    public int hsl2rgbInt(int temp1,int temp2,int temp3)
    {
        if (temp3 >= 170) {
            return ((temp1 + 127) / 255);
        }
        if (temp3 > 42) {
            if (temp3 <= 127) {
                return ((temp2 + 127) / 255);
            }
            temp3 = 170 - temp3;
        }
        return ((temp1*255 + (temp2-temp1) * (6 * temp3) + 32512) / 65025);
    }

    public int hsl2rgb(int hslValue)
    {
        int R;
        int G;
        int B;
        int H;
        int S;
        int L;
        int temp1;
        int temp2;
        int temp3;
        int res;
        L = (hslValue & 0xff);
        S = ((hslValue >> 8) & 0xff);
        H = ((hslValue >> 16) & 0xff);
        if (S==0) {
            res = (L << 16)+(L << 8)+L;
            return res;
        }
        if (L<=127) {
            temp2 = L * (255 + S);
        } else {
            temp2 = (L+S) * 255 - L*S;
        }
        temp1 = 510 * L - temp2;
        // R
        temp3 = (H + 85);
        if (temp3 > 255) {
            temp3 = temp3-255;
        }
        R = hsl2rgbInt(temp1, temp2, temp3);
        // G
        temp3 = H;
        if (temp3 > 255) {
            temp3 = temp3-255;
        }
        G = hsl2rgbInt(temp1, temp2, temp3);
        // B
        if (H >= 85) {
            temp3 = H - 85 ;
        } else {
            temp3 = H + 170;
        }
        B = hsl2rgbInt(temp1, temp2, temp3);
        // just in case
        if (R>255) {
            R=255;
        }
        if (G>255) {
            G=255;
        }
        if (B>255) {
            B=255;
        }
        res = (R << 16)+(G << 8)+B;
        return res;
    }

    /**
     * Continues the enumeration of RGB LED clusters started using yFirstColorLedCluster().
     * Caution: You can't make any assumption about the returned RGB LED clusters order.
     * If you want to find a specific a RGB LED cluster, use ColorLedCluster.findColorLedCluster()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YColorLedCluster object, corresponding to
     *         a RGB LED cluster currently online, or a null pointer
     *         if there are no more RGB LED clusters to enumerate.
     */
    public YColorLedCluster nextColorLedCluster()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindColorLedClusterInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of RGB LED clusters currently accessible.
     * Use the method YColorLedCluster.nextColorLedCluster() to iterate on
     * next RGB LED clusters.
     *
     * @return a pointer to a YColorLedCluster object, corresponding to
     *         the first RGB LED cluster currently online, or a null pointer
     *         if there are none.
     */
    public static YColorLedCluster FirstColorLedCluster()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("ColorLedCluster");
        if (next_hwid == null)  return null;
        return FindColorLedClusterInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of RGB LED clusters currently accessible.
     * Use the method YColorLedCluster.nextColorLedCluster() to iterate on
     * next RGB LED clusters.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YColorLedCluster object, corresponding to
     *         the first RGB LED cluster currently online, or a null pointer
     *         if there are none.
     */
    public static YColorLedCluster FirstColorLedClusterInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("ColorLedCluster");
        if (next_hwid == null)  return null;
        return FindColorLedClusterInContext(yctx, next_hwid);
    }

    //--- (end of YColorLedCluster implementation)
}

