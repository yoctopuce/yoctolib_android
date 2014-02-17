/*********************************************************************
 *
 * $Id: YRefFrame.java 14977 2014-02-14 06:19:29Z mvuilleu $
 *
 * Implements yFindRefFrame(), the high-level API for RefFrame functions
 *
 * - - - - - - - - - License information: - - - - - - - - - 
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
 *********************************************************************/

package com.yoctopuce.YoctoAPI;
import org.json.JSONException;
import org.json.JSONObject;
import static com.yoctopuce.YoctoAPI.YAPI.SafeYAPI;

    //--- (YRefFrame return codes)
    //--- (end of YRefFrame return codes)
//--- (YRefFrame class start)
/**
 * YRefFrame Class: Reference frame configuration
 * 
 * This class is used to setup the base orientation of the device, so that
 * the orientation functions relative to the earth surface place use
 * the proper reference frame.
 */
public class YRefFrame extends YFunction
{
//--- (end of YRefFrame class start)
//--- (YRefFrame definitions)
    /**
     * invalid mountPos value
     */
    public static final int MOUNTPOS_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid bearing value
     */
    public static final double BEARING_INVALID = YAPI.INVALID_DOUBLE;
    /**
     * invalid calibrationParam value
     */
    public static final String CALIBRATIONPARAM_INVALID = YAPI.INVALID_STRING;
    public enum MOUNTPOSITION {
        BOTTOM(0),
        TOP(1),
        FRONT(2),
        RIGHT(3),
        REAR(4),
        LEFT(5);
        public final int value;
        private MOUNTPOSITION(int val) 
        {
            this.value = val;
        };
        public static MOUNTPOSITION fromInt(int intval) 
        {
            switch(intval) {
            case 0:
                return BOTTOM;
            case 1:
                return TOP;
            case 2:
                return FRONT;
            case 3:
                return RIGHT;
            case 4:
                return REAR;
            case 5:
                return LEFT;
            }
            return null;
        }
    };
    
    public enum MOUNTORIENTATION {
        TWELVE(0),
        THREE(1),
        SIX(2),
        NINE(3);
        public final int value;
        private MOUNTORIENTATION(int val) 
        {
            this.value = val;
        };
        public static MOUNTORIENTATION fromInt(int intval) 
        {
            switch(intval) {
            case 0:
                return TWELVE;
            case 1:
                return THREE;
            case 2:
                return SIX;
            case 3:
                return NINE;
            }
            return null;
        }
    };
    
    protected int _mountPos = MOUNTPOS_INVALID;
    protected double _bearing = BEARING_INVALID;
    protected String _calibrationParam = CALIBRATIONPARAM_INVALID;
    protected UpdateCallback _valueCallbackRefFrame = null;

    /**
     * Deprecated UpdateCallback for RefFrame
     */
    public interface UpdateCallback {
        /**
         * 
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YRefFrame function, String functionValue);
    }

    /**
     * TimedReportCallback for RefFrame
     */
    public interface TimedReportCallback {
        /**
         * 
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YRefFrame  function, YMeasure measure);
    }
    //--- (end of YRefFrame definitions)


    /**
     * 
     * @param func : functionid
     */
    protected YRefFrame(String func)
    {
        super(func);
        _className = "RefFrame";
        //--- (YRefFrame attributes initialization)
        //--- (end of YRefFrame attributes initialization)
    }

    //--- (YRefFrame implementation)
    @Override
    protected void  _parseAttr(JSONObject json_val) throws JSONException
    {
        if (json_val.has("mountPos")) {
            _mountPos =  json_val.getInt("mountPos");
        }
        if (json_val.has("bearing")) {
            _bearing =  json_val.getDouble("bearing")/65536.0;
        }
        if (json_val.has("calibrationParam")) {
            _calibrationParam =  json_val.getString("calibrationParam"); ;
        }
        super._parseAttr(json_val);
    }

    /**
     * @throws YAPI_Exception
     */
    public int get_mountPos()  throws YAPI_Exception
    {
        if (_cacheExpiration <= SafeYAPI().GetTickCount()) {
            if (load(YAPI.SafeYAPI().DefaultCacheValidity) != YAPI.SUCCESS) {
                return MOUNTPOS_INVALID;
            }
        }
        return _mountPos;
    }

    /**
     * @throws YAPI_Exception
     */
    public int getMountPos() throws YAPI_Exception

    { return get_mountPos(); }

    public int set_mountPos(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        rest_val = Integer.toString(newval);
        _setAttr("mountPos",rest_val);
        return YAPI.SUCCESS;
    }

    public int setMountPos(int newval)  throws YAPI_Exception

    { return set_mountPos(newval); }

    /**
     * Changes the reference bearing used by the compass. The relative bearing
     * indicated by the compass is the difference between the measured magnetic
     * heading and the reference bearing indicated here.
     * 
     * For instance, if you setup as reference bearing the value of the earth
     * magnetic declination, the compass will provide the orientation relative
     * to the geographic North.
     * 
     * Similarly, when the sensor is not mounted along the standard directions
     * because it has an additional yaw angle, you can set this angle in the reference
     * bearing so that the compass provides the expected natural direction.
     * 
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     * 
     * @param newval : a floating point number corresponding to the reference bearing used by the compass
     * 
     * @return YAPI.SUCCESS if the call succeeds.
     * 
     * @throws YAPI_Exception
     */
    public int set_bearing(double  newval)  throws YAPI_Exception
    {
        String rest_val;
        rest_val = Long.toString(Math.round(newval*65536.0));
        _setAttr("bearing",rest_val);
        return YAPI.SUCCESS;
    }

    /**
     * Changes the reference bearing used by the compass. The relative bearing
     * indicated by the compass is the difference between the measured magnetic
     * heading and the reference bearing indicated here.
     * 
     * For instance, if you setup as reference bearing the value of the earth
     * magnetic declination, the compass will provide the orientation relative
     * to the geographic North.
     * 
     * Similarly, when the sensor is not mounted along the standard directions
     * because it has an additional yaw angle, you can set this angle in the reference
     * bearing so that the compass provides the expected natural direction.
     * 
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     * 
     * @param newval : a floating point number corresponding to the reference bearing used by the compass
     * 
     * @return YAPI_SUCCESS if the call succeeds.
     * 
     * @throws YAPI_Exception
     */
    public int setBearing(double newval)  throws YAPI_Exception

    { return set_bearing(newval); }

    /**
     * Returns the reference bearing used by the compass. The relative bearing
     * indicated by the compassis the difference between the measured magnetic
     * heading and the reference bearing indicated here.
     * 
     * @return a floating point number corresponding to the reference bearing used by the compass
     * 
     * @throws YAPI_Exception
     */
    public double get_bearing()  throws YAPI_Exception
    {
        if (_cacheExpiration <= SafeYAPI().GetTickCount()) {
            if (load(YAPI.SafeYAPI().DefaultCacheValidity) != YAPI.SUCCESS) {
                return BEARING_INVALID;
            }
        }
        return _bearing;
    }

    /**
     * Returns the reference bearing used by the compass. The relative bearing
     * indicated by the compassis the difference between the measured magnetic
     * heading and the reference bearing indicated here.
     * 
     * @return a floating point number corresponding to the reference bearing used by the compass
     * 
     * @throws YAPI_Exception
     */
    public double getBearing() throws YAPI_Exception

    { return get_bearing(); }

    /**
     * @throws YAPI_Exception
     */
    public String get_calibrationParam()  throws YAPI_Exception
    {
        if (_cacheExpiration <= SafeYAPI().GetTickCount()) {
            if (load(YAPI.SafeYAPI().DefaultCacheValidity) != YAPI.SUCCESS) {
                return CALIBRATIONPARAM_INVALID;
            }
        }
        return _calibrationParam;
    }

    /**
     * @throws YAPI_Exception
     */
    public String getCalibrationParam() throws YAPI_Exception

    { return get_calibrationParam(); }

    public int set_calibrationParam(String  newval)  throws YAPI_Exception
    {
        String rest_val;
        rest_val = newval;
        _setAttr("calibrationParam",rest_val);
        return YAPI.SUCCESS;
    }

    public int setCalibrationParam(String newval)  throws YAPI_Exception

    { return set_calibrationParam(newval); }

    /**
     * Retrieves a reference frame for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     * 
     * This function does not require that the reference frame is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YRefFrame.isOnline() to test if the reference frame is
     * indeed online at a given time. In case of ambiguity when looking for
     * a reference frame by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     * 
     * @param func : a string that uniquely characterizes the reference frame
     * 
     * @return a YRefFrame object allowing you to drive the reference frame.
     */
    public static YRefFrame FindRefFrame(String func)
    {
        YRefFrame obj;
        obj = (YRefFrame) YFunction._FindFromCache("RefFrame", func);
        if (obj == null) {
            obj = new YRefFrame(func);
            YFunction._AddToCache("RefFrame", func, obj);
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
     * @noreturn
     */
    public int registerValueCallback(UpdateCallback callback)
    {
        String val;
        if (callback != null) {
            YFunction._UpdateValueCallbackList(this, true);
        } else {
            YFunction._UpdateValueCallbackList(this, false);
        }
        _valueCallbackRefFrame = callback;
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
        if (_valueCallbackRefFrame != null) {
            _valueCallbackRefFrame.yNewValue(this, value);
        } else {
            super._invokeValueCallback(value);
        }
        return 0;
    }

    /**
     * Returns the installation position of the device, as configured
     * in order to define the reference frame for the compass and the
     * pitch/roll tilt sensors.
     * 
     * @return a value among the YRefFrame.MOUNTPOSITION enumeration
     *         (YRefFrame.MOUNTPOSITION_BOTTOM,   YRefFrame.MOUNTPOSITION_TOP,
     *         YRefFrame.MOUNTPOSITION_FRONT,    YRefFrame.MOUNTPOSITION_RIGHT,
     *         YRefFrame.MOUNTPOSITION_REAR,     YRefFrame.MOUNTPOSITION_LEFT),
     *         corresponding to the installation in a box, on one of the six faces.
     * 
     * @throws YAPI_Exception
     */
    public MOUNTPOSITION get_mountPosition()  throws YAPI_Exception
    {
        int pos = 0;
        pos = get_mountPos();
        return MOUNTPOSITION.fromInt(((pos) >> (2)));
    }

    /**
     * Returns the installation orientation of the device, as configured
     * in order to define the reference frame for the compass and the
     * pitch/roll tilt sensors.
     * 
     * @return a value among the enumeration YRefFrame.MOUNTORIENTATION
     *         (YRefFrame.MOUNTORIENTATION_TWELVE, YRefFrame.MOUNTORIENTATION_THREE,
     *         YRefFrame.MOUNTORIENTATION_SIX,     YRefFrame.MOUNTORIENTATION_NINE)
     *         corresponding to the orientation of the "X" arrow on the device,
     *         as on a clock dial seen from an observer in the center of the box.
     *         On the bottom face, the 12H orientation points to the front, while
     *         on the top face, the 12H orientation points to the rear.
     * 
     * @throws YAPI_Exception
     */
    public MOUNTORIENTATION get_mountOrientation()  throws YAPI_Exception
    {
        int pos = 0;
        pos = get_mountPos();
        return MOUNTORIENTATION.fromInt(((pos) & (3)));
    }

    /**
     * Changes the compass and inclinometers frame of reference. The magnetic compass
     * and the tilt sensors (pitch and roll) naturally work in the plane
     * parallel to the earth surface. In case the device is not installed upright
     * and horizontally, you must select its reference orientation (parallel to
     * the earth surface) so that the measures are made relative to this position.
     * 
     * @param position: a value among the YRefFrame.MOUNTPOSITION enumeration
     *         (YRefFrame.MOUNTPOSITION_BOTTOM,   YRefFrame.MOUNTPOSITION_TOP,
     *         YRefFrame.MOUNTPOSITION_FRONT,    YRefFrame.MOUNTPOSITION_RIGHT,
     *         YRefFrame.MOUNTPOSITION_REAR,     YRefFrame.MOUNTPOSITION_LEFT),
     *         corresponding to the installation in a box, on one of the six faces.
     * @param orientation: a value among the enumeration YRefFrame.MOUNTORIENTATION
     *         (YRefFrame.MOUNTORIENTATION_TWELVE, YRefFrame.MOUNTORIENTATION_THREE,
     *         YRefFrame.MOUNTORIENTATION_SIX,     YRefFrame.MOUNTORIENTATION_NINE)
     *         corresponding to the orientation of the "X" arrow on the device,
     *         as on a clock dial seen from an observer in the center of the box.
     *         On the bottom face, the 12H orientation points to the front, while
     *         on the top face, the 12H orientation points to the rear.
     * 
     * Remember to call the saveToFlash()
     * method of the module if the modification must be kept.
     * 
     * @throws YAPI_Exception
     */
    public int set_mountPosition(MOUNTPOSITION position,MOUNTORIENTATION orientation)  throws YAPI_Exception
    {
        int pos = 0;
        pos = ((position.value) << (2)) + orientation.value;
        return set_mountPos(pos);
    }

    /**
     * Continues the enumeration of reference frames started using yFirstRefFrame().
     * 
     * @return a pointer to a YRefFrame object, corresponding to
     *         a reference frame currently online, or a null pointer
     *         if there are no more reference frames to enumerate.
     */
    public  YRefFrame nextRefFrame()
    {
        String next_hwid = SafeYAPI().getNextHardwareId(_className, _func);
        if(next_hwid == null) return null;
        return FindRefFrame(next_hwid);
    }

    /**
     * Starts the enumeration of reference frames currently accessible.
     * Use the method YRefFrame.nextRefFrame() to iterate on
     * next reference frames.
     * 
     * @return a pointer to a YRefFrame object, corresponding to
     *         the first reference frame currently online, or a null pointer
     *         if there are none.
     */
    public static YRefFrame FirstRefFrame()
    {
        String next_hwid = SafeYAPI().getFirstHardwareId("RefFrame");
        if (next_hwid == null)  return null;
        return FindRefFrame(next_hwid);
    }

    //--- (end of YRefFrame implementation)
}

