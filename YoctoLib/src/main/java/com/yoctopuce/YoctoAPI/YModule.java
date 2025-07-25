/*********************************************************************
 * $Id: YModule.java 67524 2025-06-16 14:47:10Z seb $
 *
 * YModule Class: Module control interface
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


import java.util.*;


//--- (generated code: YModule class start)
/**
 * YModule Class: Global parameters control interface for all Yoctopuce devices
 *
 * The YModule class can be used with all Yoctopuce USB devices.
 * It can be used to control the module global parameters, and
 * to enumerate the functions provided by each module.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YModule extends YFunction
{
//--- (end of generated code: YModule class start)


    // --- (generated code: YModule definitions)
    /**
     * invalid productName value
     */
    public static final String PRODUCTNAME_INVALID = YAPI.INVALID_STRING;
    /**
     * invalid serialNumber value
     */
    public static final String SERIALNUMBER_INVALID = YAPI.INVALID_STRING;
    /**
     * invalid productId value
     */
    public static final int PRODUCTID_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid productRelease value
     */
    public static final int PRODUCTRELEASE_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid firmwareRelease value
     */
    public static final String FIRMWARERELEASE_INVALID = YAPI.INVALID_STRING;
    /**
     * invalid persistentSettings value
     */
    public static final int PERSISTENTSETTINGS_LOADED = 0;
    public static final int PERSISTENTSETTINGS_SAVED = 1;
    public static final int PERSISTENTSETTINGS_MODIFIED = 2;
    public static final int PERSISTENTSETTINGS_INVALID = -1;
    /**
     * invalid luminosity value
     */
    public static final int LUMINOSITY_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid beacon value
     */
    public static final int BEACON_OFF = 0;
    public static final int BEACON_ON = 1;
    public static final int BEACON_INVALID = -1;
    /**
     * invalid upTime value
     */
    public static final long UPTIME_INVALID = YAPI.INVALID_LONG;
    /**
     * invalid usbCurrent value
     */
    public static final int USBCURRENT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid rebootCountdown value
     */
    public static final int REBOOTCOUNTDOWN_INVALID = YAPI.INVALID_INT;
    /**
     * invalid userVar value
     */
    public static final int USERVAR_INVALID = YAPI.INVALID_INT;
    protected String _productName = PRODUCTNAME_INVALID;
    protected String _serialNumber = SERIALNUMBER_INVALID;
    protected int _productId = PRODUCTID_INVALID;
    protected int _productRelease = PRODUCTRELEASE_INVALID;
    protected String _firmwareRelease = FIRMWARERELEASE_INVALID;
    protected int _persistentSettings = PERSISTENTSETTINGS_INVALID;
    protected int _luminosity = LUMINOSITY_INVALID;
    protected int _beacon = BEACON_INVALID;
    protected long _upTime = UPTIME_INVALID;
    protected int _usbCurrent = USBCURRENT_INVALID;
    protected int _rebootCountdown = REBOOTCOUNTDOWN_INVALID;
    protected int _userVar = USERVAR_INVALID;
    protected UpdateCallback _valueCallbackModule = null;
    protected YModule.LogCallback _logCallback = null;
    protected YModule.ConfigChangeCallback _confChangeCallback = null;
    protected YModule.BeaconCallback _beaconCallback = null;

    public interface LogCallback
    {
        /**
         *
         * @param module  : the module object of the device
         * @param logline : the log line (without carriage return)
         */
        void logCallback(YModule module, String logline);
    }
    public interface ConfigChangeCallback
    {
        /**
         *
         * @param module  : the module object of the device
         */
        void configChangeCallback(YModule module);
    }
    public interface BeaconCallback
    {
        /**
         *
         * @param module  : the module object of the device
         */
        /**
         *
         * @param beacon  : the beacon state
         */
        void beaconCallback(YModule module, int beacon);
    }
    /**
     * Deprecated UpdateCallback for Module
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YModule function, String functionValue);
    }

    /**
     * TimedReportCallback for Module
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YModule  function, YMeasure measure);
    }
    //--- (end of generated code: YModule definitions)

    private static void _updateModuleCallbackList(YModule module, boolean add)
    {
        module._yapi._UpdateModuleCallbackList(module, add);
    }


    // Return the internal device object hosting the function
    YDevice _getDev() throws YAPI_Exception
    {
        String devid = _func;
        int dotidx = devid.indexOf('.');
        if (dotidx >= 0) devid = devid.substring(0, dotidx);
        YDevice dev = _yapi._yHash.getDevice(devid);
        if (dev == null) {
            throw new YAPI_Exception(YAPI.DEVICE_NOT_FOUND, "Device [" + devid + "] is not online");
        }
        return dev;
    }

    /**
     * @param func : functionid
     */
    protected YModule(YAPIContext yctx, String func)
    {
        super(yctx, func);
        _className = "Module";
        //--- (generated code: YModule attributes initialization)
        //--- (end of generated code: YModule attributes initialization)
    }

    protected YModule(String func)
    {
        this(YAPI.GetYCtx(false), func);
    }


    /**
     * Returns the number of functions (beside the "module" interface) available on the module.
     *
     * @return the number of functions on the module
     *
     * @throws YAPI_Exception on error
     */
    public int functionCount() throws YAPI_Exception
    {
        YDevice dev = _getDev();
        return dev.getFunctions().size();
    }


    private YPEntry getYPFromIndex(int index) throws YAPI_Exception
    {
        YDevice dev = _getDev();
        Collection<YPEntry> functions = dev.getFunctions();
        int i = 0;
        for (YPEntry yp : functions) {
            if (i++ == index)
                return yp;
        }
        throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, String.format(Locale.US, "Invalid function index (%d/%d)", index, functions.size()));
    }


    /**
     * Retrieves the hardware identifier of the <i>n</i>th function on the module.
     *
     *  @param functionIndex : the index of the function for which the information is desired, starting at
     * 0 for the first function.
     *
     * @return a string corresponding to the unambiguous hardware identifier of the requested module function
     *
     * @throws YAPI_Exception on error
     */
    public String functionId(int functionIndex) throws YAPI_Exception
    {
        YPEntry ypEntry = getYPFromIndex(functionIndex);
        return ypEntry.getFuncId();
    }

    /**
     *  Retrieves the type of the <i>n</i>th function on the module. Yoctopuce functions type names match
     * their class names without the <i>Y</i> prefix, for instance <i>Relay</i>, <i>Temperature</i> etc..
     *
     *  @param functionIndex : the index of the function for which the information is desired, starting at
     * 0 for the first function.
     *
     * @return a string corresponding to the type of the function.
     *
     * @throws YAPI_Exception on error
     */
    public String functionType(int functionIndex) throws YAPI_Exception
    {
        YPEntry ypEntry = getYPFromIndex(functionIndex);
        return ypEntry.getClassname();
    }

    /**
     * Retrieves the base type of the <i>n</i>th function on the module.
     * For instance, the base type of all measuring functions is "Sensor".
     *
     *  @param functionIndex : the index of the function for which the information is desired, starting at
     * 0 for the first function.
     *
     * @return a string corresponding to the base type of the function
     *
     * @throws YAPI_Exception on error
     */
    public String functionBaseType(int functionIndex) throws YAPI_Exception
    {
        YPEntry ypEntry = getYPFromIndex(functionIndex);
        return ypEntry.getBaseType();
    }

    /**
     * Retrieves the logical name of the <i>n</i>th function on the module.
     *
     *  @param functionIndex : the index of the function for which the information is desired, starting at
     * 0 for the first function.
     *
     * @return a string corresponding to the logical name of the requested module function
     *
     * @throws YAPI_Exception on error
     */
    public String functionName(int functionIndex) throws YAPI_Exception
    {
        YPEntry ypEntry = getYPFromIndex(functionIndex);
        return ypEntry.getLogicalName();
    }

    /**
     * Retrieves the advertised value of the <i>n</i>th function on the module.
     *
     *  @param functionIndex : the index of the function for which the information is desired, starting at
     * 0 for the first function.
     *
     *  @return a short string (up to 6 characters) corresponding to the advertised value of the requested
     * module function
     *
     * @throws YAPI_Exception on error
     */
    public String functionValue(int functionIndex) throws YAPI_Exception
    {
        YPEntry ypEntry = getYPFromIndex(functionIndex);
        return ypEntry.getAdvertisedValue();
    }


    private byte[] _flattenJsonStruct_internal(byte[] actualSettings) throws YAPI_Exception
    {
        YJSONObject json = null;
        YJSONArray out = new YJSONArray();
        try {
            json = new YJSONObject(new String(actualSettings, _yapi._deviceCharset));
            json.parse();
        } catch (Exception ex) {
            throw new YAPI_Exception(YAPI.IO_ERROR, ex.getLocalizedMessage());
        }
        Set<String> functionList = json.getKeys();
        for (String fun_key : functionList) {
            if (!fun_key.equals("services")) {
                YJSONObject functionJson = json.getYJSONObject(fun_key);
                if (functionJson == null) {
                    continue;
                }
                Set<String> attr_keys = functionJson.getKeys();
                for (String attr_key : attr_keys) {
                    YJSONContent value = functionJson.get(attr_key);
                    if (value == null || value.getJSONType() == YJSONContent.YJSONType.OBJECT) {
                        continue;
                    }
                    String flat_attr = fun_key + "/" + attr_key + "=" + value.toString();
                    out.put(flat_attr);
                }
            }
        }
        return out.toJSON();
    }


    private ArrayList<String> get_subDevices_internal() throws YAPI_Exception
    {
        YDevice dev = _getDev();
        YGenericHub hub = dev.getHub();
        return hub.get_subDeviceOf(this.get_serialNumber());
    }


    private String get_parentHub_internal() throws YAPI_Exception
    {
        YDevice dev = _getDev();
        YGenericHub hub = dev.getHub();
        String hubSerial = hub.getSerialNumber();
        if (hubSerial.equals(getSerialNumber()))
            return "";
        return hubSerial;
    }


    private String get_url_internal() throws YAPI_Exception
    {
        YDevice dev = _getDev();
        YGenericHub hub = dev.getHub();
        return hub.get_urlOf(getSerialNumber());
    }

    public void _startStopDevLog_internal(String serial, boolean start) throws YAPI_Exception
    {
        YDevice ydev = _yapi._yHash.getDevice(serial);
        if (ydev != null) {
            ydev.registerLogCallback(_logCallback);
        }
    }


    // --- (generated code: YModule implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("productName")) {
            _productName = json_val.getString("productName");
        }
        if (json_val.has("serialNumber")) {
            _serialNumber = json_val.getString("serialNumber");
        }
        if (json_val.has("productId")) {
            _productId = json_val.getInt("productId");
        }
        if (json_val.has("productRelease")) {
            _productRelease = json_val.getInt("productRelease");
        }
        if (json_val.has("firmwareRelease")) {
            _firmwareRelease = json_val.getString("firmwareRelease");
        }
        if (json_val.has("persistentSettings")) {
            _persistentSettings = json_val.getInt("persistentSettings");
        }
        if (json_val.has("luminosity")) {
            _luminosity = json_val.getInt("luminosity");
        }
        if (json_val.has("beacon")) {
            _beacon = json_val.getInt("beacon") > 0 ? 1 : 0;
        }
        if (json_val.has("upTime")) {
            _upTime = json_val.getLong("upTime");
        }
        if (json_val.has("usbCurrent")) {
            _usbCurrent = json_val.getInt("usbCurrent");
        }
        if (json_val.has("rebootCountdown")) {
            _rebootCountdown = json_val.getInt("rebootCountdown");
        }
        if (json_val.has("userVar")) {
            _userVar = json_val.getInt("userVar");
        }
        super._parseAttr(json_val);
    }

    /**
     * Returns the commercial name of the module, as set by the factory.
     *
     * @return a string corresponding to the commercial name of the module, as set by the factory
     *
     * @throws YAPI_Exception on error
     */
    public String get_productName() throws YAPI_Exception
    {
        String res;
        YDevice dev;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                dev = this._getDev();
                if (!(dev == null)) {
                    return dev.getProductName();
                }
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PRODUCTNAME_INVALID;
                }
            }
            res = _productName;
        }
        return res;
    }

    /**
     * Returns the commercial name of the module, as set by the factory.
     *
     * @return a string corresponding to the commercial name of the module, as set by the factory
     *
     * @throws YAPI_Exception on error
     */
    public String getProductName() throws YAPI_Exception
    {
        return get_productName();
    }

    /**
     * Returns the serial number of the module, as set by the factory.
     *
     * @return a string corresponding to the serial number of the module, as set by the factory
     *
     * @throws YAPI_Exception on error
     */
    @Override
    public String get_serialNumber() throws YAPI_Exception
    {
        String res;
        YDevice dev;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                dev = this._getDev();
                if (!(dev == null)) {
                    return dev.getSerialNumber();
                }
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SERIALNUMBER_INVALID;
                }
            }
            res = _serialNumber;
        }
        return res;
    }

    /**
     * Returns the serial number of the module, as set by the factory.
     *
     * @return a string corresponding to the serial number of the module, as set by the factory
     *
     * @throws YAPI_Exception on error
     */
    public String getSerialNumber() throws YAPI_Exception
    {
        return get_serialNumber();
    }

    /**
     * Returns the USB device identifier of the module.
     *
     * @return an integer corresponding to the USB device identifier of the module
     *
     * @throws YAPI_Exception on error
     */
    public int get_productId() throws YAPI_Exception
    {
        int res;
        YDevice dev;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                dev = this._getDev();
                if (!(dev == null)) {
                    return dev.getProductId();
                }
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PRODUCTID_INVALID;
                }
            }
            res = _productId;
        }
        return res;
    }

    /**
     * Returns the USB device identifier of the module.
     *
     * @return an integer corresponding to the USB device identifier of the module
     *
     * @throws YAPI_Exception on error
     */
    public int getProductId() throws YAPI_Exception
    {
        return get_productId();
    }

    /**
     * Returns the release number of the module hardware, preprogrammed at the factory.
     * The original hardware release returns value 1, revision B returns value 2, etc.
     *
     * @return an integer corresponding to the release number of the module hardware, preprogrammed at the factory
     *
     * @throws YAPI_Exception on error
     */
    public int get_productRelease() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration == 0) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PRODUCTRELEASE_INVALID;
                }
            }
            res = _productRelease;
        }
        return res;
    }

    /**
     * Returns the release number of the module hardware, preprogrammed at the factory.
     * The original hardware release returns value 1, revision B returns value 2, etc.
     *
     * @return an integer corresponding to the release number of the module hardware, preprogrammed at the factory
     *
     * @throws YAPI_Exception on error
     */
    public int getProductRelease() throws YAPI_Exception
    {
        return get_productRelease();
    }

    /**
     * Returns the version of the firmware embedded in the module.
     *
     * @return a string corresponding to the version of the firmware embedded in the module
     *
     * @throws YAPI_Exception on error
     */
    public String get_firmwareRelease() throws YAPI_Exception
    {
        String res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return FIRMWARERELEASE_INVALID;
                }
            }
            res = _firmwareRelease;
        }
        return res;
    }

    /**
     * Returns the version of the firmware embedded in the module.
     *
     * @return a string corresponding to the version of the firmware embedded in the module
     *
     * @throws YAPI_Exception on error
     */
    public String getFirmwareRelease() throws YAPI_Exception
    {
        return get_firmwareRelease();
    }

    /**
     * Returns the current state of persistent module settings.
     *
     *  @return a value among YModule.PERSISTENTSETTINGS_LOADED, YModule.PERSISTENTSETTINGS_SAVED and
     * YModule.PERSISTENTSETTINGS_MODIFIED corresponding to the current state of persistent module settings
     *
     * @throws YAPI_Exception on error
     */
    public int get_persistentSettings() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PERSISTENTSETTINGS_INVALID;
                }
            }
            res = _persistentSettings;
        }
        return res;
    }

    /**
     * Returns the current state of persistent module settings.
     *
     *  @return a value among YModule.PERSISTENTSETTINGS_LOADED, YModule.PERSISTENTSETTINGS_SAVED and
     * YModule.PERSISTENTSETTINGS_MODIFIED corresponding to the current state of persistent module settings
     *
     * @throws YAPI_Exception on error
     */
    public int getPersistentSettings() throws YAPI_Exception
    {
        return get_persistentSettings();
    }

    public int set_persistentSettings(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("persistentSettings",rest_val);
        }
        return YAPI.SUCCESS;
    }


    /**
     * Returns the luminosity of the  module informative LEDs (from 0 to 100).
     *
     * @return an integer corresponding to the luminosity of the  module informative LEDs (from 0 to 100)
     *
     * @throws YAPI_Exception on error
     */
    public int get_luminosity() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return LUMINOSITY_INVALID;
                }
            }
            res = _luminosity;
        }
        return res;
    }

    /**
     * Returns the luminosity of the  module informative LEDs (from 0 to 100).
     *
     * @return an integer corresponding to the luminosity of the  module informative LEDs (from 0 to 100)
     *
     * @throws YAPI_Exception on error
     */
    public int getLuminosity() throws YAPI_Exception
    {
        return get_luminosity();
    }

    /**
     * Changes the luminosity of the module informative leds. The parameter is a
     * value between 0 and 100.
     * Remember to call the saveToFlash() method of the module if the
     * modification must be kept.
     *
     * @param newval : an integer corresponding to the luminosity of the module informative leds
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_luminosity(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("luminosity",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the luminosity of the module informative leds. The parameter is a
     * value between 0 and 100.
     * Remember to call the saveToFlash() method of the module if the
     * modification must be kept.
     *
     * @param newval : an integer corresponding to the luminosity of the module informative leds
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setLuminosity(int newval)  throws YAPI_Exception
    {
        return set_luminosity(newval);
    }

    /**
     * Returns the state of the localization beacon.
     *
     * @return either YModule.BEACON_OFF or YModule.BEACON_ON, according to the state of the localization beacon
     *
     * @throws YAPI_Exception on error
     */
    public int get_beacon() throws YAPI_Exception
    {
        int res;
        YDevice dev;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                dev = this._getDev();
                if (!(dev == null)) {
                    return dev.getBeacon();
                }
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return BEACON_INVALID;
                }
            }
            res = _beacon;
        }
        return res;
    }

    /**
     * Returns the state of the localization beacon.
     *
     * @return either YModule.BEACON_OFF or YModule.BEACON_ON, according to the state of the localization beacon
     *
     * @throws YAPI_Exception on error
     */
    public int getBeacon() throws YAPI_Exception
    {
        return get_beacon();
    }

    /**
     * Turns on or off the module localization beacon.
     *
     * @param newval : either YModule.BEACON_OFF or YModule.BEACON_ON
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_beacon(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = (newval > 0 ? "1" : "0");
            _setAttr("beacon",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Turns on or off the module localization beacon.
     *
     * @param newval : either YModule.BEACON_OFF or YModule.BEACON_ON
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setBeacon(int newval)  throws YAPI_Exception
    {
        return set_beacon(newval);
    }

    /**
     * Returns the number of milliseconds spent since the module was powered on.
     *
     * @return an integer corresponding to the number of milliseconds spent since the module was powered on
     *
     * @throws YAPI_Exception on error
     */
    public long get_upTime() throws YAPI_Exception
    {
        long res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return UPTIME_INVALID;
                }
            }
            res = _upTime;
        }
        return res;
    }

    /**
     * Returns the number of milliseconds spent since the module was powered on.
     *
     * @return an integer corresponding to the number of milliseconds spent since the module was powered on
     *
     * @throws YAPI_Exception on error
     */
    public long getUpTime() throws YAPI_Exception
    {
        return get_upTime();
    }

    /**
     * Returns the current consumed by the module on the USB bus, in milli-amps.
     *
     * @return an integer corresponding to the current consumed by the module on the USB bus, in milli-amps
     *
     * @throws YAPI_Exception on error
     */
    public int get_usbCurrent() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return USBCURRENT_INVALID;
                }
            }
            res = _usbCurrent;
        }
        return res;
    }

    /**
     * Returns the current consumed by the module on the USB bus, in milli-amps.
     *
     * @return an integer corresponding to the current consumed by the module on the USB bus, in milli-amps
     *
     * @throws YAPI_Exception on error
     */
    public int getUsbCurrent() throws YAPI_Exception
    {
        return get_usbCurrent();
    }

    /**
     * Returns the remaining number of seconds before the module restarts, or zero when no
     * reboot has been scheduled.
     *
     * @return an integer corresponding to the remaining number of seconds before the module restarts, or zero when no
     *         reboot has been scheduled
     *
     * @throws YAPI_Exception on error
     */
    public int get_rebootCountdown() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return REBOOTCOUNTDOWN_INVALID;
                }
            }
            res = _rebootCountdown;
        }
        return res;
    }

    /**
     * Returns the remaining number of seconds before the module restarts, or zero when no
     * reboot has been scheduled.
     *
     * @return an integer corresponding to the remaining number of seconds before the module restarts, or zero when no
     *         reboot has been scheduled
     *
     * @throws YAPI_Exception on error
     */
    public int getRebootCountdown() throws YAPI_Exception
    {
        return get_rebootCountdown();
    }

    public int set_rebootCountdown(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("rebootCountdown",rest_val);
        }
        return YAPI.SUCCESS;
    }


    /**
     * Returns the value previously stored in this attribute.
     * On startup and after a device reboot, the value is always reset to zero.
     *
     * @return an integer corresponding to the value previously stored in this attribute
     *
     * @throws YAPI_Exception on error
     */
    public int get_userVar() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return USERVAR_INVALID;
                }
            }
            res = _userVar;
        }
        return res;
    }

    /**
     * Returns the value previously stored in this attribute.
     * On startup and after a device reboot, the value is always reset to zero.
     *
     * @return an integer corresponding to the value previously stored in this attribute
     *
     * @throws YAPI_Exception on error
     */
    public int getUserVar() throws YAPI_Exception
    {
        return get_userVar();
    }

    /**
     * Stores a 32 bit value in the device RAM. This attribute is at programmer disposal,
     * should he need to store a state variable.
     * On startup and after a device reboot, the value is always reset to zero.
     *
     * @param newval : an integer
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_userVar(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("userVar",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Stores a 32 bit value in the device RAM. This attribute is at programmer disposal,
     * should he need to store a state variable.
     * On startup and after a device reboot, the value is always reset to zero.
     *
     * @param newval : an integer
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setUserVar(int newval)  throws YAPI_Exception
    {
        return set_userVar(newval);
    }

    /**
     * Allows you to find a module from its serial number or from its logical name.
     *
     * This function does not require that the module is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YModule.isOnline() to test if the module is
     * indeed online at a given time. In case of ambiguity when looking for
     * a module by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string containing either the serial number or
     *         the logical name of the desired module
     *
     * @return a YModule object allowing you to drive the module
     *         or get additional information on the module.
     */
    public static YModule FindModule(String func)
    {
        YModule obj;
        String cleanHwId;
        int modpos;
        cleanHwId = func;
        modpos = func.indexOf(".module");
        if (modpos != (func.length() - 7)) {
            cleanHwId = func + ".module";
        }
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YModule) YFunction._FindFromCache("Module", cleanHwId);
            if (obj == null) {
                obj = new YModule(cleanHwId);
                YFunction._AddToCache("Module", cleanHwId, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a module for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the module is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YModule.isOnline() to test if the module is
     * indeed online at a given time. In case of ambiguity when looking for
     * a module by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the module, for instance
     *         MyDevice.module.
     *
     * @return a YModule object allowing you to drive the module.
     */
    public static YModule FindModuleInContext(YAPIContext yctx,String func)
    {
        YModule obj;
        String cleanHwId;
        int modpos;
        synchronized (yctx._functionCacheLock) {
            cleanHwId = func;
            modpos = func.indexOf(".module");
            if (modpos != (func.length() - 7)) {
                cleanHwId = func + ".module";
            }
            obj = (YModule) YFunction._FindFromCacheInContext(yctx, "Module", cleanHwId);
            if (obj == null) {
                obj = new YModule(yctx, cleanHwId);
                YFunction._AddToCache("Module", cleanHwId, obj);
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
        _valueCallbackModule = callback;
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
        if (_valueCallbackModule != null) {
            _valueCallbackModule.yNewValue(this, value);
        } else {
            super._invokeValueCallback(value);
        }
        return 0;
    }

    public String get_productNameAndRevision() throws YAPI_Exception
    {
        String prodname;
        int prodrel;
        String fullname;

        prodname = get_productName();
        prodrel = get_productRelease();
        if (prodrel > 1) {
            fullname = String.format(Locale.US, "%s rev. %c",prodname,64 + prodrel);
        } else {
            fullname = prodname;
        }
        return fullname;
    }

    /**
     * Saves current settings in the nonvolatile memory of the module.
     * Warning: the number of allowed save operations during a module life is
     * limited (about 100000 cycles). Do not call this function within a loop.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int saveToFlash() throws YAPI_Exception
    {
        return set_persistentSettings(PERSISTENTSETTINGS_SAVED);
    }

    /**
     * Reloads the settings stored in the nonvolatile memory, as
     * when the module is powered on.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int revertFromFlash() throws YAPI_Exception
    {
        return set_persistentSettings(PERSISTENTSETTINGS_LOADED);
    }

    /**
     * Schedules a simple module reboot after the given number of seconds.
     *
     * @param secBeforeReboot : number of seconds before rebooting
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int reboot(int secBeforeReboot) throws YAPI_Exception
    {
        return set_rebootCountdown(secBeforeReboot);
    }

    /**
     * Schedules a module reboot into special firmware update mode.
     *
     * @param secBeforeReboot : number of seconds before rebooting
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int triggerFirmwareUpdate(int secBeforeReboot) throws YAPI_Exception
    {
        return set_rebootCountdown(-secBeforeReboot);
    }

    public void _startStopDevLog(String serial,boolean start) throws YAPI_Exception
    {
        _startStopDevLog_internal(serial, start);
    }

    //cannot be generated for Java:
    //public void _startStopDevLog_internal(String serial,boolean start) throws YAPI_Exception
    /**
     * Registers a device log callback function. This callback will be called each time
     * that a module sends a new log message. Mostly useful to debug a Yoctopuce module.
     *
     * @param callback : the callback function to call, or a null pointer.
     *         The callback function should take two
     *         arguments: the module object that emitted the log message,
     *         and the character string containing the log.
     * @throws YAPI_Exception on error
     */
    public int registerLogCallback(YModule.LogCallback callback) throws YAPI_Exception
    {
        String serial;

        serial = get_serialNumber();
        if (serial.equals(YAPI.INVALID_STRING)) {
            return YAPI.DEVICE_NOT_FOUND;
        }
        _logCallback = callback;
        _startStopDevLog(serial, callback != null);
        return 0;
    }

    public YModule.LogCallback get_logCallback()
    {
        return _logCallback;
    }

    /**
     * Register a callback function, to be called when a persistent settings in
     * a device configuration has been changed (e.g. change of unit, etc).
     *
     * @param callback : a procedure taking a YModule parameter, or null
     *         to unregister a previously registered  callback.
     */
    public int registerConfigChangeCallback(YModule.ConfigChangeCallback callback)
    {
        if (callback != null) {
            YModule._updateModuleCallbackList(this, true);
        } else {
            YModule._updateModuleCallbackList(this, false);
        }
        _confChangeCallback = callback;
        return 0;
    }

    public int _invokeConfigChangeCallback()
    {
        if (_confChangeCallback != null) {
            _confChangeCallback.configChangeCallback(this);
        }
        return 0;
    }

    /**
     * Register a callback function, to be called when the localization beacon of the module
     * has been changed. The callback function should take two arguments: the YModule object of
     * which the beacon has changed, and an integer describing the new beacon state.
     *
     * @param callback : The callback function to call, or null to unregister a
     *         previously registered callback.
     */
    public int registerBeaconCallback(YModule.BeaconCallback callback)
    {
        if (callback != null) {
            YModule._updateModuleCallbackList(this, true);
        } else {
            YModule._updateModuleCallbackList(this, false);
        }
        _beaconCallback = callback;
        return 0;
    }

    public int _invokeBeaconCallback(int beaconState)
    {
        if (_beaconCallback != null) {
            _beaconCallback.beaconCallback(this, beaconState);
        }
        return 0;
    }

    /**
     * Triggers a configuration change callback, to check if they are supported or not.
     */
    public int triggerConfigChangeCallback() throws YAPI_Exception
    {
        _setAttr("persistentSettings", "2");
        return 0;
    }

    /**
     *  Tests whether the byn file is valid for this module. This method is useful to test if the module
     * needs to be updated.
     *  It is possible to pass a directory as argument instead of a file. In this case, this method returns
     * the path of the most recent
     * appropriate .byn file. If the parameter onlynew is true, the function discards firmwares that are older or
     * equal to the installed firmware.
     *
     * @param path : the path of a byn file or a directory that contains byn files
     * @param onlynew : returns only files that are strictly newer
     *
     * @return the path of the byn file to use or a empty string if no byn files matches the requirement
     *
     * @throws YAPI_Exception on error
     */
    public String checkFirmware(String path,boolean onlynew) throws YAPI_Exception
    {
        String serial;
        int release;
        String tmp_res;
        if (onlynew) {
            release = YAPIContext._atoi(get_firmwareRelease());
        } else {
            release = 0;
        }
        //may throw an exception
        serial = get_serialNumber();
        tmp_res = YFirmwareUpdate.CheckFirmware(serial, path, release);
        if (tmp_res.indexOf("error:") == 0) {
            _throw(YAPI.INVALID_ARGUMENT, tmp_res);
        }
        return tmp_res;
    }

    /**
     * Prepares a firmware update of the module. This method returns a YFirmwareUpdate object which
     * handles the firmware update process.
     *
     * @param path : the path of the .byn file to use.
     * @param force : true to force the firmware update even if some prerequisites appear not to be met
     *
     * @return a YFirmwareUpdate object or NULL on error.
     */
    public YFirmwareUpdate updateFirmwareEx(String path,boolean force) throws YAPI_Exception
    {
        String serial;
        byte[] settings = new byte[0];

        serial = get_serialNumber();
        settings = get_allSettings();
        if ((settings).length == 0) {
            _throw(YAPI.IO_ERROR, "Unable to get device settings");
            settings = ("error:Unable to get device settings").getBytes(_yapi._deviceCharset);
        }
        return new YFirmwareUpdate(_yapi, serial, path, settings, force);
    }

    /**
     * Prepares a firmware update of the module. This method returns a YFirmwareUpdate object which
     * handles the firmware update process.
     *
     * @param path : the path of the .byn file to use.
     *
     * @return a YFirmwareUpdate object or NULL on error.
     */
    public YFirmwareUpdate updateFirmware(String path) throws YAPI_Exception
    {
        return updateFirmwareEx(path, false);
    }

    /**
     * Returns all the settings and uploaded files of the module. Useful to backup all the
     * logical names, calibrations parameters, and uploaded files of a device.
     *
     * @return a binary buffer with all the settings.
     *
     * @throws YAPI_Exception on error
     */
    public byte[] get_allSettings() throws YAPI_Exception
    {
        byte[] settings = new byte[0];
        byte[] json = new byte[0];
        byte[] res = new byte[0];
        String sep;
        String name;
        String item;
        String t_type;
        String pageid;
        String url;
        String file_data;
        byte[] file_data_bin = new byte[0];
        byte[] temp_data_bin = new byte[0];
        String ext_settings;
        ArrayList<byte[]> filelist = new ArrayList<>();
        ArrayList<String> templist = new ArrayList<>();

        settings = _download("api.json");
        if ((settings).length == 0) {
            return settings;
        }
        ext_settings = ", \"extras\":[";
        templist = get_functionIds("Temperature");
        sep = "";
        for (String ii_0:templist) {
            if (YAPIContext._atoi(get_firmwareRelease()) > 9000) {
                url = String.format(Locale.US, "api/%s/sensorType",ii_0);
                t_type = new String(_download(url), _yapi._deviceCharset);
                if (t_type.equals("RES_NTC") || t_type.equals("RES_LINEAR")) {
                    pageid = (ii_0).substring(11, 11 + ii_0.length() - 11);
                    if (pageid.equals("")) {
                        pageid = "1";
                    }
                    temp_data_bin = _download(String.format(Locale.US, "extra.json?page=%s",pageid));
                    if ((temp_data_bin).length > 0) {
                        item = String.format(Locale.US, "%s{\"fid\":\"%s\", \"json\":%s}\n",sep,ii_0,new String(temp_data_bin, _yapi._deviceCharset));
                        ext_settings = ext_settings + item;
                        sep = ",";
                    }
                }
            }
        }
        ext_settings = ext_settings + "],\n\"files\":[";
        if (hasFunction("files")) {
            json = _download("files.json?a=dir&d=1&f=");
            if ((json).length == 0) {
                return json;
            }
            filelist = _json_get_array(json);
            sep = "";
            for (byte[] ii_1:filelist) {
                name = _json_get_key(ii_1, "name");
                if ((name.length() > 0) && !(name.equals("startupConf.json"))) {
                    if ((name).substring(name.length()-1, name.length()-1 + 1).equals("/")) {
                        file_data = "";
                    } else {
                        file_data_bin = _download(_escapeAttr(name));
                        file_data = YAPIContext._bytesToHexStr(file_data_bin, 0, file_data_bin.length);
                    }
                    item = String.format(Locale.US, "%s{\"name\":\"%s\", \"data\":\"%s\"}\n",sep,name,file_data);
                    ext_settings = ext_settings + item;
                    sep = ",";
                }
            }
        }
        res = ("{ \"api\":" + new String(settings, _yapi._deviceCharset) + ext_settings + "]}").getBytes(_yapi._deviceCharset);
        return res;
    }

    public int loadThermistorExtra(String funcId,String jsonExtra) throws YAPI_Exception
    {
        ArrayList<byte[]> values = new ArrayList<>();
        String url;
        String curr;
        byte[] binCurr = new byte[0];
        String currTemp;
        byte[] binCurrTemp = new byte[0];
        int ofs;
        int size;
        url = "api/" + funcId + ".json?command=Z";

        _download(url);
        // add records in growing resistance value
        values = _json_get_array((jsonExtra).getBytes(_yapi._deviceCharset));
        ofs = 0;
        size = values.size();
        while (ofs + 1 < size) {
            binCurr = values.get(ofs);
            binCurrTemp = values.get(ofs + 1);
            curr = _json_get_string(binCurr);
            currTemp = _json_get_string(binCurrTemp);
            url = String.format(Locale.US, "api/%s.json?command=m%s:%s",funcId,curr,currTemp);
            _download(url);
            ofs = ofs + 2;
        }
        return YAPI.SUCCESS;
    }

    public int set_extraSettings(String jsonExtra) throws YAPI_Exception
    {
        ArrayList<byte[]> extras = new ArrayList<>();
        byte[] tmp = new byte[0];
        String functionId;
        byte[] data = new byte[0];
        extras = _json_get_array((jsonExtra).getBytes(_yapi._deviceCharset));
        for (byte[] ii_0:extras) {
            tmp = _get_json_path(ii_0, "fid");
            functionId = _json_get_string(tmp);
            data = _get_json_path(ii_0, "json");
            if (hasFunction(functionId)) {
                loadThermistorExtra(functionId, new String(data, _yapi._deviceCharset));
            }
        }
        return YAPI.SUCCESS;
    }

    /**
     * Restores all the settings and uploaded files to the module.
     * This method is useful to restore all the logical names and calibrations parameters,
     * uploaded files etc. of a device from a backup.
     * Remember to call the saveToFlash() method of the module if the
     * modifications must be kept.
     *
     * @param settings : a binary buffer with all the settings.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_allSettingsAndFiles(byte[] settings) throws YAPI_Exception
    {
        byte[] down = new byte[0];
        byte[] json_api = new byte[0];
        byte[] json_files = new byte[0];
        byte[] json_extra = new byte[0];
        int fuperror;
        int globalres;
        fuperror = 0;
        json_api = _get_json_path(settings, "api");
        if ((json_api).length == 0) {
            return set_allSettings(settings);
        }
        json_extra = _get_json_path(settings, "extras");
        if ((json_extra).length > 0) {
            set_extraSettings(new String(json_extra, _yapi._deviceCharset));
        }
        set_allSettings(json_api);
        if (hasFunction("files")) {
            ArrayList<byte[]> files = new ArrayList<>();
            String res;
            byte[] tmp = new byte[0];
            String name;
            String data;
            down = _download("files.json?a=format");
            down = _get_json_path(down, "res");
            res = _json_get_string(down);
            //noinspection DoubleNegation
            if (!(res.equals("ok"))) { throw new YAPI_Exception(YAPI.IO_ERROR, "format failed");}
            json_files = _get_json_path(settings, "files");
            files = _json_get_array(json_files);
            for (byte[] ii_0:files) {
                tmp = _get_json_path(ii_0, "name");
                name = _json_get_string(tmp);
                tmp = _get_json_path(ii_0, "data");
                data = _json_get_string(tmp);
                if (name.equals("")) {
                    fuperror = fuperror + 1;
                } else {
                    _upload(name, YAPIContext._hexStrToBin(data));
                }
            }
        }
        // Apply settings a second time for file-dependent settings and dynamic sensor nodes
        globalres = set_allSettings(json_api);
        //noinspection DoubleNegation
        if (!(fuperror == 0)) { throw new YAPI_Exception(YAPI.IO_ERROR, "Error during file upload");}
        return globalres;
    }

    /**
     * Tests if the device includes a specific function. This method takes a function identifier
     * and returns a boolean.
     *
     * @param funcId : the requested function identifier
     *
     * @return true if the device has the function identifier
     */
    public boolean hasFunction(String funcId) throws YAPI_Exception
    {
        int count;
        int i;
        String fid;

        count = functionCount();
        i = 0;
        while (i < count) {
            fid = functionId(i);
            if (fid.equals(funcId)) {
                return true;
            }
            i = i + 1;
        }
        return false;
    }

    /**
     * Retrieve all hardware identifier that match the type passed in argument.
     *
     * @param funType : The type of function (Relay, LightSensor, Voltage,...)
     *
     * @return an array of strings.
     */
    public ArrayList<String> get_functionIds(String funType) throws YAPI_Exception
    {
        int count;
        int i;
        String ftype;
        ArrayList<String> res = new ArrayList<>();

        count = functionCount();
        i = 0;
        while (i < count) {
            ftype = functionType(i);
            if (ftype.equals(funType)) {
                res.add(functionId(i));
            } else {
                ftype = functionBaseType(i);
                if (ftype.equals(funType)) {
                    res.add(functionId(i));
                }
            }
            i = i + 1;
        }
        return res;
    }

    public byte[] _flattenJsonStruct(byte[] jsoncomplex) throws YAPI_Exception
    {
        return _flattenJsonStruct_internal(jsoncomplex);
    }

    //cannot be generated for Java:
    //public byte[] _flattenJsonStruct_internal(byte[] jsoncomplex) throws YAPI_Exception
    public int calibVersion(String cparams)
    {
        if (cparams.equals("0,")) {
            return 3;
        }
        if (cparams.indexOf(",") >= 0) {
            if (cparams.indexOf(" ") > 0) {
                return 3;
            } else {
                return 1;
            }
        }
        if (cparams.equals("") || cparams.equals("0")) {
            return 1;
        }
        if ((cparams.length() < 2) || (cparams.indexOf(".") >= 0)) {
            return 0;
        } else {
            return 2;
        }
    }

    public int calibScale(String unit_name,String sensorType)
    {
        if (unit_name.equals("g") || unit_name.equals("gauss") || unit_name.equals("W")) {
            return 1000;
        }
        if (unit_name.equals("C")) {
            if (sensorType.equals("")) {
                return 16;
            }
            if (YAPIContext._atoi(sensorType) < 8) {
                return 16;
            } else {
                return 100;
            }
        }
        if (unit_name.equals("m") || unit_name.equals("deg")) {
            return 10;
        }
        return 1;
    }

    public int calibOffset(String unit_name)
    {
        if (unit_name.equals("% RH") || unit_name.equals("mbar") || unit_name.equals("lx")) {
            return 0;
        }
        return 32767;
    }

    public String calibConvert(String param,String currentFuncValue,String unit_name,String sensorType)
    {
        int paramVer;
        int funVer;
        int funScale;
        int funOffset;
        int paramScale;
        int paramOffset;
        ArrayList<Integer> words = new ArrayList<>();
        ArrayList<String> words_str = new ArrayList<>();
        ArrayList<Double> calibData = new ArrayList<>();
        ArrayList<Integer> iCalib = new ArrayList<>();
        int calibType;
        int i;
        int maxSize;
        double ratio;
        int nPoints;
        double wordVal;
        // Initial guess for parameter encoding
        paramVer = calibVersion(param);
        funVer = calibVersion(currentFuncValue);
        funScale = calibScale(unit_name, sensorType);
        funOffset = calibOffset(unit_name);
        paramScale = funScale;
        paramOffset = funOffset;
        if (funVer < 3) {
            // Read the effective device scale if available
            if (funVer == 2) {
                words = YAPIContext._decodeWords(currentFuncValue);
                if ((words.get(0).intValue() == 1366) && (words.get(1).intValue() == 12500)) {
                    // Yocto-3D RefFrame used a special encoding
                    funScale = 1;
                    funOffset = 0;
                } else {
                    funScale = words.get(1).intValue();
                    funOffset = words.get(0).intValue();
                }
            } else {
                if (funVer == 1) {
                    if (currentFuncValue.equals("") || (YAPIContext._atoi(currentFuncValue) > 10)) {
                        funScale = 0;
                    }
                }
            }
        }
        calibData.clear();
        calibType = 0;
        if (paramVer < 3) {
            // Handle old 16 bit parameters formats
            if (paramVer == 2) {
                words = YAPIContext._decodeWords(param);
                if ((words.get(0).intValue() == 1366) && (words.get(1).intValue() == 12500)) {
                    // Yocto-3D RefFrame used a special encoding
                    paramScale = 1;
                    paramOffset = 0;
                } else {
                    paramScale = words.get(1).intValue();
                    paramOffset = words.get(0).intValue();
                }
                if ((words.size() >= 3) && (words.get(2).intValue() > 0)) {
                    maxSize = 3 + 2 * ((words.get(2).intValue()) % (10));
                    if (maxSize > words.size()) {
                        maxSize = words.size();
                    }
                    i = 3;
                    while (i < maxSize) {
                        calibData.add((double) words.get(i).intValue());
                        i = i + 1;
                    }
                }
            } else {
                if (paramVer == 1) {
                    words_str = new ArrayList<>(Arrays.asList(param.split(",")));
                    for (String ii_0:words_str) {
                        words.add(YAPIContext._atoi(ii_0));
                    }
                    if (param.equals("") || (words.get(0).intValue() > 10)) {
                        paramScale = 0;
                    }
                    if ((words.size() > 0) && (words.get(0).intValue() > 0)) {
                        maxSize = 1 + 2 * ((words.get(0).intValue()) % (10));
                        if (maxSize > words.size()) {
                            maxSize = words.size();
                        }
                        i = 1;
                        while (i < maxSize) {
                            calibData.add((double) words.get(i).intValue());
                            i = i + 1;
                        }
                    }
                } else {
                    if (paramVer == 0) {
                        ratio = YAPI.ystr2float(param);
                        if (ratio > 0) {
                            calibData.add(0.0);
                            calibData.add(0.0);
                            calibData.add((double)Math.round(65535 / ratio));
                            calibData.add(65535.0);
                        }
                    }
                }
            }
            i = 0;
            while (i < calibData.size()) {
                if (paramScale > 0) {
                    // scalar decoding
                    calibData.set(i, (calibData.get(i).doubleValue() - paramOffset) / paramScale);
                } else {
                    // floating-point decoding
                    calibData.set(i, YAPIContext._decimalToDouble((int) (double)Math.round(calibData.get(i).doubleValue())));
                }
                i = i + 1;
            }
        } else {
            // Handle latest 32bit parameter format
            iCalib = YAPIContext._decodeFloats(param);
            calibType = (int) (double)Math.round(iCalib.get(0).doubleValue() / 1000.0);
            if (calibType >= 30) {
                calibType = calibType - 30;
            }
            i = 1;
            while (i < iCalib.size()) {
                calibData.add(iCalib.get(i).doubleValue() / 1000.0);
                i = i + 1;
            }
        }
        if (funVer >= 3) {
            // Encode parameters in new format
            if (calibData.size() == 0) {
                param = "0,";
            } else {
                param = Integer.toString(30 + calibType);
                i = 0;
                while (i < calibData.size()) {
                    if ((i & 1) > 0) {
                        param = param + ":";
                    } else {
                        param = param + " ";
                    }
                    param = param + Integer.toString((int) (double)Math.round(calibData.get(i).doubleValue() * 1000.0 / 1000.0));
                    i = i + 1;
                }
                param = param + ",";
            }
        } else {
            if (funVer >= 1) {
                // Encode parameters for older devices
                nPoints = (calibData.size() / 2);
                param = Integer.toString(nPoints);
                i = 0;
                while (i < 2 * nPoints) {
                    if (funScale == 0) {
                        wordVal = YAPIContext._doubleToDecimal((int) (double)Math.round(calibData.get(i).doubleValue()));
                    } else {
                        wordVal = calibData.get(i).doubleValue() * funScale + funOffset;
                    }
                    param = param + "," + Double.toString((double)Math.round(wordVal));
                    i = i + 1;
                }
            } else {
                // Initial V0 encoding used for old Yocto-Light
                if (calibData.size() == 4) {
                    param = Double.toString((double)Math.round(1000 * (calibData.get(3).doubleValue() - calibData.get(1).doubleValue()) / calibData.get(2).doubleValue() - calibData.get(0).doubleValue()));
                }
            }
        }
        return param;
    }

    public int _tryExec(String url)
    {
        int res;
        int done;
        res = YAPI.SUCCESS;
        done = 1;
        try {
            _download(url);
        } catch (Exception ex) {
            done = 0;
        }
        if (done == 0) {
            // retry silently after a short wait
            try {
                YAPI.Sleep(500);
                _download(url);
            } catch (Exception ex) {
                // second failure, return error code
                res = get_errorType();
            }
        }
        return res;
    }

    /**
     * Restores all the settings of the device. Useful to restore all the logical names and calibrations parameters
     * of a module from a backup.Remember to call the saveToFlash() method of the module if the
     * modifications must be kept.
     *
     * @param settings : a binary buffer with all the settings.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_allSettings(byte[] settings) throws YAPI_Exception
    {
        ArrayList<String> restoreLast = new ArrayList<>();
        byte[] old_json_flat = new byte[0];
        ArrayList<byte[]> old_dslist = new ArrayList<>();
        ArrayList<String> old_jpath = new ArrayList<>();
        ArrayList<Integer> old_jpath_len = new ArrayList<>();
        ArrayList<String> old_val_arr = new ArrayList<>();
        byte[] actualSettings = new byte[0];
        ArrayList<byte[]> new_dslist = new ArrayList<>();
        ArrayList<String> new_jpath = new ArrayList<>();
        ArrayList<Integer> new_jpath_len = new ArrayList<>();
        ArrayList<String> new_val_arr = new ArrayList<>();
        int cpos;
        int eqpos;
        int leng;
        int i;
        int j;
        int subres;
        int res;
        String njpath;
        String jpath;
        String fun;
        String attr;
        String value;
        String old_serial;
        String new_serial;
        String url;
        String tmp;
        byte[] binTmp = new byte[0];
        String sensorType;
        String unit_name;
        String newval;
        String oldval;
        String old_calib;
        String each_str;
        boolean do_update;
        boolean found;
        res = YAPI.SUCCESS;
        binTmp = _get_json_path(settings, "api");
        if ((binTmp).length > 0) {
            settings = binTmp;
        }
        old_serial = "";
        oldval = "";
        newval = "";
        old_json_flat = _flattenJsonStruct(settings);
        old_dslist = _json_get_array(old_json_flat);
        for (byte[] ii_0:old_dslist) {
            each_str = _json_get_string(ii_0);
            // split json path and attr
            leng = each_str.length();
            eqpos = each_str.indexOf("=");
            if ((eqpos < 0) || (leng == 0)) {
                _throw(YAPI.INVALID_ARGUMENT, "Invalid settings");
                return YAPI.INVALID_ARGUMENT;
            }
            jpath = (each_str).substring(0, eqpos);
            eqpos = eqpos + 1;
            value = (each_str).substring(eqpos, eqpos + leng - eqpos);
            old_jpath.add(jpath);
            old_jpath_len.add(jpath.length());
            old_val_arr.add(value);
            if (jpath.equals("module/serialNumber")) {
                old_serial = value;
            }
        }

        try {
            actualSettings = _download("api.json");
        } catch (Exception ex) {
            // retry silently after a short wait
            YAPI.Sleep(500);
            actualSettings = _download("api.json");
        }
        new_serial = get_serialNumber();
        if (old_serial.equals(new_serial) || old_serial.equals("")) {
            old_serial = "_NO_SERIAL_FILTER_";
        }
        actualSettings = _flattenJsonStruct(actualSettings);
        new_dslist = _json_get_array(actualSettings);
        for (byte[] ii_1:new_dslist) {
            // remove quotes
            each_str = _json_get_string(ii_1);
            // split json path and attr
            leng = each_str.length();
            eqpos = each_str.indexOf("=");
            if ((eqpos < 0) || (leng == 0)) {
                _throw(YAPI.INVALID_ARGUMENT, "Invalid settings");
                return YAPI.INVALID_ARGUMENT;
            }
            jpath = (each_str).substring(0, eqpos);
            eqpos = eqpos + 1;
            value = (each_str).substring(eqpos, eqpos + leng - eqpos);
            new_jpath.add(jpath);
            new_jpath_len.add(jpath.length());
            new_val_arr.add(value);
        }
        i = 0;
        while (i < new_jpath.size()) {
            njpath = new_jpath.get(i);
            leng = njpath.length();
            cpos = njpath.indexOf("/");
            if ((cpos < 0) || (leng == 0)) {
                continue;
            }
            fun = (njpath).substring(0, cpos);
            cpos = cpos + 1;
            attr = (njpath).substring(cpos, cpos + leng - cpos);
            do_update = true;
            if (fun.equals("services")) {
                do_update = false;
            }
            if (do_update && (attr.equals("firmwareRelease"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("usbCurrent"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("upTime"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("persistentSettings"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("adminPassword"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("userPassword"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("rebootCountdown"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("advertisedValue"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("poeCurrent"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("readiness"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("ipAddress"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("subnetMask"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("router"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("linkQuality"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("ssid"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("channel"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("security"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("message"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("signalValue"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("currentValue"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("currentRawValue"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("currentRunIndex"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("pulseTimer"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("lastTimePressed"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("lastTimeReleased"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("filesCount"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("freeSpace"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("timeUTC"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("rtcTime"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("unixTime"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("dateTime"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("rawValue"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("lastMsg"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("delayedPulseTimer"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("rxCount"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("txCount"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("msgCount"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("rxMsgCount"))) {
                do_update = false;
            }
            if (do_update && (attr.equals("txMsgCount"))) {
                do_update = false;
            }
            if (do_update) {
                do_update = false;
                j = 0;
                found = false;
                newval = new_val_arr.get(i);
                while ((j < old_jpath.size()) && !(found)) {
                    if ((new_jpath_len.get(i).intValue() == old_jpath_len.get(j).intValue()) && (new_jpath.get(i).equals(old_jpath.get(j)))) {
                        found = true;
                        oldval = old_val_arr.get(j);
                        if (!(newval.equals(oldval)) && !(oldval.equals(old_serial))) {
                            do_update = true;
                        }
                    }
                    j = j + 1;
                }
            }
            if (do_update) {
                if (attr.equals("calibrationParam")) {
                    old_calib = "";
                    unit_name = "";
                    sensorType = "";
                    j = 0;
                    found = false;
                    while ((j < old_jpath.size()) && !(found)) {
                        if ((new_jpath_len.get(i).intValue() == old_jpath_len.get(j).intValue()) && (new_jpath.get(i).equals(old_jpath.get(j)))) {
                            found = true;
                            old_calib = old_val_arr.get(j);
                        }
                        j = j + 1;
                    }
                    tmp = fun + "/unit";
                    j = 0;
                    found = false;
                    while ((j < new_jpath.size()) && !(found)) {
                        if (tmp.equals(new_jpath.get(j))) {
                            found = true;
                            unit_name = new_val_arr.get(j);
                        }
                        j = j + 1;
                    }
                    tmp = fun + "/sensorType";
                    j = 0;
                    found = false;
                    while ((j < new_jpath.size()) && !(found)) {
                        if (tmp.equals(new_jpath.get(j))) {
                            found = true;
                            sensorType = new_val_arr.get(j);
                        }
                        j = j + 1;
                    }
                    newval = calibConvert(old_calib, new_val_arr.get(i), unit_name, sensorType);
                    url = "api/" + fun + ".json?" + attr + "=" + _escapeAttr(newval);
                    subres = _tryExec(url);
                    if ((res == YAPI.SUCCESS) && (subres != YAPI.SUCCESS)) {
                        res = subres;
                    }
                } else {
                    url = "api/" + fun + ".json?" + attr + "=" + _escapeAttr(oldval);
                    if (attr.equals("resolution")) {
                        restoreLast.add(url);
                    } else {
                        subres = _tryExec(url);
                        if ((res == YAPI.SUCCESS) && (subres != YAPI.SUCCESS)) {
                            res = subres;
                        }
                    }
                }
            }
            i = i + 1;
        }
        for (String ii_2:restoreLast) {
            subres = _tryExec(ii_2);
            if ((res == YAPI.SUCCESS) && (subres != YAPI.SUCCESS)) {
                res = subres;
            }
        }
        clearCache();
        return res;
    }

    /**
     * Adds a file to the uploaded data at the next HTTP callback.
     * This function only affects the next HTTP callback and only works in
     * HTTP callback mode.
     *
     * @param filename : the name of the file to upload at the next HTTP callback
     *
     * @return nothing.
     */
    public int addFileToHTTPCallback(String filename) throws YAPI_Exception
    {
        byte[] content = new byte[0];

        content = _download("@YCB+" + filename);
        if ((content).length == 0) {
            return YAPI.NOT_SUPPORTED;
        }
        return YAPI.SUCCESS;
    }

    /**
     * Returns the unique hardware identifier of the module.
     * The unique hardware identifier is made of the device serial
     * number followed by string ".module".
     *
     * @return a string that uniquely identifies the module
     */
    @Override
    public String get_hardwareId() throws YAPI_Exception
    {
        String serial;

        serial = get_serialNumber();
        return serial + ".module";
    }

    /**
     * Downloads the specified built-in file and returns a binary buffer with its content.
     *
     * @param pathname : name of the new file to load
     *
     * @return a binary buffer with the file content
     *
     * @throws YAPI_Exception on error
     */
    public byte[] download(String pathname) throws YAPI_Exception
    {
        return _download(pathname);
    }

    /**
     * Returns the icon of the module. The icon is a PNG image and does not
     * exceeds 1536 bytes.
     *
     * @return a binary buffer with module icon, in png format.
     * @throws YAPI_Exception on error
     */
    public byte[] get_icon2d() throws YAPI_Exception
    {
        return _download("icon2d.png");
    }

    /**
     * Returns a string with last logs of the module. This method return only
     * logs that are still in the module.
     *
     * @return a string with last logs of the module.
     * @throws YAPI_Exception on error
     */
    public String get_lastLogs() throws YAPI_Exception
    {
        byte[] content = new byte[0];

        content = _download("logs.txt");
        if ((content).length == 0) {
            return YAPI.INVALID_STRING;
        }
        return new String(content, _yapi._deviceCharset);
    }

    /**
     * Adds a text message to the device logs. This function is useful in
     * particular to trace the execution of HTTP callbacks. If a newline
     * is desired after the message, it must be included in the string.
     *
     * @param text : the string to append to the logs.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int log(String text) throws YAPI_Exception
    {
        return _upload("logs.txt", (text).getBytes(_yapi._deviceCharset));
    }

    /**
     * Returns a list of all the modules that are plugged into the current module.
     * This method only makes sense when called for a YoctoHub/VirtualHub.
     * Otherwise, an empty array will be returned.
     *
     * @return an array of strings containing the sub modules.
     */
    public ArrayList<String> get_subDevices() throws YAPI_Exception
    {
        return get_subDevices_internal();
    }

    //cannot be generated for Java:
    //public ArrayList<String> get_subDevices_internal() throws YAPI_Exception
    /**
     * Returns the serial number of the YoctoHub on which this module is connected.
     * If the module is connected by USB, or if the module is the root YoctoHub, an
     * empty string is returned.
     *
     * @return a string with the serial number of the YoctoHub or an empty string
     */
    public String get_parentHub() throws YAPI_Exception
    {
        return get_parentHub_internal();
    }

    //cannot be generated for Java:
    //public String get_parentHub_internal() throws YAPI_Exception
    /**
     * Returns the URL used to access the module. If the module is connected by USB, the
     * string 'usb' is returned.
     *
     * @return a string with the URL of the module.
     */
    public String get_url() throws YAPI_Exception
    {
        return get_url_internal();
    }

    //cannot be generated for Java:
    //public String get_url_internal() throws YAPI_Exception
    /**
     * Continues the module enumeration started using yFirstModule().
     * Caution: You can't make any assumption about the returned modules order.
     * If you want to find a specific module, use Module.findModule()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YModule object, corresponding to
     *         the next module found, or a null pointer
     *         if there are no more modules to enumerate.
     */
    public YModule nextModule()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindModuleInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of modules currently accessible.
     * Use the method YModule.nextModule() to iterate on the
     * next modules.
     *
     * @return a pointer to a YModule object, corresponding to
     *         the first module currently online, or a null pointer
     *         if there are none.
     */
    public static YModule FirstModule()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("Module");
        if (next_hwid == null)  return null;
        return FindModuleInContext(yctx, next_hwid);
    }

    /**
     * comment from .yc definition
     */
    public static YModule FirstModuleInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("Module");
        if (next_hwid == null)  return null;
        return FindModuleInContext(yctx, next_hwid);
    }

    //--- (end of generated code: YModule implementation)


}
