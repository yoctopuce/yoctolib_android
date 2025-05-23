/*********************************************************************
 *
 * $Id: YMessageBox.java 64027 2025-01-06 15:18:30Z seb $
 *
 * Implements FindMessageBox(), the high-level API for MessageBox functions
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
import java.util.ArrayList;
import java.util.Locale;


//--- (generated code: YMessageBox return codes)
//--- (end of generated code: YMessageBox return codes)
//--- (generated code: YMessageBox class start)
/**
 *  YMessageBox Class: SMS message box interface control interface, available for instance in the
 * YoctoHub-GSM-2G, the YoctoHub-GSM-3G-EU, the YoctoHub-GSM-3G-NA or the YoctoHub-GSM-4G
 *
 * The YMessageBox class provides SMS sending and receiving capability for
 * GSM-enabled Yoctopuce devices.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YMessageBox extends YFunction
{
//--- (end of generated code: YMessageBox class start)
//--- (generated code: YMessageBox definitions)
    /**
     * invalid slotsInUse value
     */
    public static final int SLOTSINUSE_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid slotsCount value
     */
    public static final int SLOTSCOUNT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid slotsBitmap value
     */
    public static final String SLOTSBITMAP_INVALID = YAPI.INVALID_STRING;
    /**
     * invalid pduSent value
     */
    public static final int PDUSENT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid pduReceived value
     */
    public static final int PDURECEIVED_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid obey value
     */
    public static final String OBEY_INVALID = YAPI.INVALID_STRING;
    /**
     * invalid command value
     */
    public static final String COMMAND_INVALID = YAPI.INVALID_STRING;
    protected int _slotsInUse = SLOTSINUSE_INVALID;
    protected int _slotsCount = SLOTSCOUNT_INVALID;
    protected String _slotsBitmap = SLOTSBITMAP_INVALID;
    protected int _pduSent = PDUSENT_INVALID;
    protected int _pduReceived = PDURECEIVED_INVALID;
    protected String _obey = OBEY_INVALID;
    protected String _command = COMMAND_INVALID;
    protected UpdateCallback _valueCallbackMessageBox = null;
    protected int _nextMsgRef = 0;
    protected String _prevBitmapStr = "";
    protected ArrayList<YSms> _pdus = new ArrayList<>();
    protected ArrayList<YSms> _messages = new ArrayList<>();
    protected boolean _gsm2unicodeReady;
    protected ArrayList<Integer> _gsm2unicode = new ArrayList<>();
    protected byte[] _iso2gsm = new byte[0];

    /**
     * Deprecated UpdateCallback for MessageBox
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YMessageBox function, String functionValue);
    }

    /**
     * TimedReportCallback for MessageBox
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YMessageBox  function, YMeasure measure);
    }
    //--- (end of generated code: YMessageBox definitions)


    /**
     *
     * @param func : functionid
     */
    protected YMessageBox(YAPIContext ctx, String func)
    {
        super(ctx, func);
        _className = "MessageBox";
        //--- (generated code: YMessageBox attributes initialization)
        //--- (end of generated code: YMessageBox attributes initialization)
    }

    /**
     *
     * @param func : functionid
     */
    protected YMessageBox(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }

    //--- (generated code: YMessageBox implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("slotsInUse")) {
            _slotsInUse = json_val.getInt("slotsInUse");
        }
        if (json_val.has("slotsCount")) {
            _slotsCount = json_val.getInt("slotsCount");
        }
        if (json_val.has("slotsBitmap")) {
            _slotsBitmap = json_val.getString("slotsBitmap");
        }
        if (json_val.has("pduSent")) {
            _pduSent = json_val.getInt("pduSent");
        }
        if (json_val.has("pduReceived")) {
            _pduReceived = json_val.getInt("pduReceived");
        }
        if (json_val.has("obey")) {
            _obey = json_val.getString("obey");
        }
        if (json_val.has("command")) {
            _command = json_val.getString("command");
        }
        super._parseAttr(json_val);
    }

    /**
     * Returns the number of message storage slots currently in use.
     *
     * @return an integer corresponding to the number of message storage slots currently in use
     *
     * @throws YAPI_Exception on error
     */
    public int get_slotsInUse() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SLOTSINUSE_INVALID;
                }
            }
            res = _slotsInUse;
        }
        return res;
    }

    /**
     * Returns the number of message storage slots currently in use.
     *
     * @return an integer corresponding to the number of message storage slots currently in use
     *
     * @throws YAPI_Exception on error
     */
    public int getSlotsInUse() throws YAPI_Exception
    {
        return get_slotsInUse();
    }

    /**
     * Returns the total number of message storage slots on the SIM card.
     *
     * @return an integer corresponding to the total number of message storage slots on the SIM card
     *
     * @throws YAPI_Exception on error
     */
    public int get_slotsCount() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SLOTSCOUNT_INVALID;
                }
            }
            res = _slotsCount;
        }
        return res;
    }

    /**
     * Returns the total number of message storage slots on the SIM card.
     *
     * @return an integer corresponding to the total number of message storage slots on the SIM card
     *
     * @throws YAPI_Exception on error
     */
    public int getSlotsCount() throws YAPI_Exception
    {
        return get_slotsCount();
    }

    public String get_slotsBitmap() throws YAPI_Exception
    {
        String res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return SLOTSBITMAP_INVALID;
                }
            }
            res = _slotsBitmap;
        }
        return res;
    }

    /**
     * Returns the number of SMS units sent so far.
     *
     * @return an integer corresponding to the number of SMS units sent so far
     *
     * @throws YAPI_Exception on error
     */
    public int get_pduSent() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PDUSENT_INVALID;
                }
            }
            res = _pduSent;
        }
        return res;
    }

    /**
     * Returns the number of SMS units sent so far.
     *
     * @return an integer corresponding to the number of SMS units sent so far
     *
     * @throws YAPI_Exception on error
     */
    public int getPduSent() throws YAPI_Exception
    {
        return get_pduSent();
    }

    /**
     * Changes the value of the outgoing SMS units counter.
     *
     * @param newval : an integer corresponding to the value of the outgoing SMS units counter
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_pduSent(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("pduSent",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the value of the outgoing SMS units counter.
     *
     * @param newval : an integer corresponding to the value of the outgoing SMS units counter
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setPduSent(int newval)  throws YAPI_Exception
    {
        return set_pduSent(newval);
    }

    /**
     * Returns the number of SMS units received so far.
     *
     * @return an integer corresponding to the number of SMS units received so far
     *
     * @throws YAPI_Exception on error
     */
    public int get_pduReceived() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return PDURECEIVED_INVALID;
                }
            }
            res = _pduReceived;
        }
        return res;
    }

    /**
     * Returns the number of SMS units received so far.
     *
     * @return an integer corresponding to the number of SMS units received so far
     *
     * @throws YAPI_Exception on error
     */
    public int getPduReceived() throws YAPI_Exception
    {
        return get_pduReceived();
    }

    /**
     * Changes the value of the incoming SMS units counter.
     *
     * @param newval : an integer corresponding to the value of the incoming SMS units counter
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_pduReceived(int  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = Integer.toString(newval);
            _setAttr("pduReceived",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the value of the incoming SMS units counter.
     *
     * @param newval : an integer corresponding to the value of the incoming SMS units counter
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setPduReceived(int newval)  throws YAPI_Exception
    {
        return set_pduReceived(newval);
    }

    /**
     * Returns the phone number authorized to send remote management commands.
     * When a phone number is specified, the hub will take contre of all incoming
     * SMS messages: it will execute commands coming from the authorized number,
     * and delete all messages once received (whether authorized or not).
     * If you need to receive SMS messages using your own software, leave this
     * attribute empty.
     *
     * @return a string corresponding to the phone number authorized to send remote management commands
     *
     * @throws YAPI_Exception on error
     */
    public String get_obey() throws YAPI_Exception
    {
        String res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return OBEY_INVALID;
                }
            }
            res = _obey;
        }
        return res;
    }

    /**
     * Returns the phone number authorized to send remote management commands.
     * When a phone number is specified, the hub will take contre of all incoming
     * SMS messages: it will execute commands coming from the authorized number,
     * and delete all messages once received (whether authorized or not).
     * If you need to receive SMS messages using your own software, leave this
     * attribute empty.
     *
     * @return a string corresponding to the phone number authorized to send remote management commands
     *
     * @throws YAPI_Exception on error
     */
    public String getObey() throws YAPI_Exception
    {
        return get_obey();
    }

    /**
     * Changes the phone number authorized to send remote management commands.
     * The phone number usually starts with a '+' and does not include spacers.
     * When a phone number is specified, the hub will take contre of all incoming
     * SMS messages: it will execute commands coming from the authorized number,
     * and delete all messages once received (whether authorized or not).
     * If you need to receive SMS messages using your own software, leave this
     * attribute empty. Remember to call the saveToFlash() method of the
     * module if the modification must be kept.
     *
     * This feature is only available since YoctoHub-GSM-4G.
     *
     * @param newval : a string corresponding to the phone number authorized to send remote management commands
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int set_obey(String  newval)  throws YAPI_Exception
    {
        String rest_val;
        synchronized (this) {
            rest_val = newval;
            _setAttr("obey",rest_val);
        }
        return YAPI.SUCCESS;
    }

    /**
     * Changes the phone number authorized to send remote management commands.
     * The phone number usually starts with a '+' and does not include spacers.
     * When a phone number is specified, the hub will take contre of all incoming
     * SMS messages: it will execute commands coming from the authorized number,
     * and delete all messages once received (whether authorized or not).
     * If you need to receive SMS messages using your own software, leave this
     * attribute empty. Remember to call the saveToFlash() method of the
     * module if the modification must be kept.
     *
     * This feature is only available since YoctoHub-GSM-4G.
     *
     * @param newval : a string corresponding to the phone number authorized to send remote management commands
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int setObey(String newval)  throws YAPI_Exception
    {
        return set_obey(newval);
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
     * Retrieves a SMS message box interface for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the SMS message box interface is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YMessageBox.isOnline() to test if the SMS message box interface is
     * indeed online at a given time. In case of ambiguity when looking for
     * a SMS message box interface by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the SMS message box interface, for instance
     *         YHUBGSM1.messageBox.
     *
     * @return a YMessageBox object allowing you to drive the SMS message box interface.
     */
    public static YMessageBox FindMessageBox(String func)
    {
        YMessageBox obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YMessageBox) YFunction._FindFromCache("MessageBox", func);
            if (obj == null) {
                obj = new YMessageBox(func);
                YFunction._AddToCache("MessageBox", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a SMS message box interface for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the SMS message box interface is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YMessageBox.isOnline() to test if the SMS message box interface is
     * indeed online at a given time. In case of ambiguity when looking for
     * a SMS message box interface by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the SMS message box interface, for instance
     *         YHUBGSM1.messageBox.
     *
     * @return a YMessageBox object allowing you to drive the SMS message box interface.
     */
    public static YMessageBox FindMessageBoxInContext(YAPIContext yctx,String func)
    {
        YMessageBox obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YMessageBox) YFunction._FindFromCacheInContext(yctx, "MessageBox", func);
            if (obj == null) {
                obj = new YMessageBox(yctx, func);
                YFunction._AddToCache("MessageBox", func, obj);
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
        _valueCallbackMessageBox = callback;
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
        if (_valueCallbackMessageBox != null) {
            _valueCallbackMessageBox.yNewValue(this, value);
        } else {
            super._invokeValueCallback(value);
        }
        return 0;
    }

    public int nextMsgRef()
    {
        _nextMsgRef = _nextMsgRef + 1;
        return _nextMsgRef;
    }

    public int clearSIMSlot(int slot) throws YAPI_Exception
    {
        int retry;
        int idx;
        String res;
        String bitmapStr;
        int int_res;
        byte[] newBitmap = new byte[0];
        int bitVal;

        retry = 5;
        while (retry > 0) {
            clearCache();
            bitmapStr = get_slotsBitmap();
            newBitmap = YAPIContext._hexStrToBin(bitmapStr);
            idx = (slot >> 3);
            if (idx < (newBitmap).length) {
                bitVal = (1 << ((slot & 7)));
                if ((((newBitmap[idx] & 0xff) & bitVal)) != 0) {
                    _prevBitmapStr = "";
                    int_res = set_command(String.format(Locale.US, "DS%d",slot));
                    if (int_res < 0) {
                        return int_res;
                    }
                } else {
                    return YAPI.SUCCESS;
                }
            } else {
                return YAPI.INVALID_ARGUMENT;
            }
            res = _AT("");
            retry = retry - 1;
        }
        return YAPI.IO_ERROR;
    }

    public String _AT(String cmd) throws YAPI_Exception
    {
        int chrPos;
        int cmdLen;
        int waitMore;
        String res;
        byte[] buff = new byte[0];
        int bufflen;
        String buffstr;
        int buffstrlen;
        int idx;
        int suffixlen;
        // copied form the YCellular class
        // quote dangerous characters used in AT commands
        cmdLen = cmd.length();
        chrPos = cmd.indexOf("#");
        while (chrPos >= 0) {
            cmd = String.format(Locale.US, "%s%c23%s",(cmd).substring(0, chrPos),37,(cmd).substring(chrPos+1, chrPos+1 + cmdLen-chrPos-1));
            cmdLen = cmdLen + 2;
            chrPos = cmd.indexOf("#");
        }
        chrPos = cmd.indexOf("+");
        while (chrPos >= 0) {
            cmd = String.format(Locale.US, "%s%c2B%s",(cmd).substring(0, chrPos),37,(cmd).substring(chrPos+1, chrPos+1 + cmdLen-chrPos-1));
            cmdLen = cmdLen + 2;
            chrPos = cmd.indexOf("+");
        }
        chrPos = cmd.indexOf("=");
        while (chrPos >= 0) {
            cmd = String.format(Locale.US, "%s%c3D%s",(cmd).substring(0, chrPos),37,(cmd).substring(chrPos+1, chrPos+1 + cmdLen-chrPos-1));
            cmdLen = cmdLen + 2;
            chrPos = cmd.indexOf("=");
        }
        cmd = String.format(Locale.US, "at.txt?cmd=%s",cmd);
        res = "";
        // max 2 minutes (each iteration may take up to 5 seconds if waiting)
        waitMore = 24;
        while (waitMore > 0) {
            buff = _download(cmd);
            bufflen = (buff).length;
            buffstr = new String(buff, _yapi._deviceCharset);
            buffstrlen = buffstr.length();
            idx = bufflen - 1;
            while ((idx > 0) && ((buff[idx] & 0xff) != 64) && ((buff[idx] & 0xff) != 10) && ((buff[idx] & 0xff) != 13)) {
                idx = idx - 1;
            }
            if ((buff[idx] & 0xff) == 64) {
                // continuation detected
                suffixlen = bufflen - idx;
                cmd = String.format(Locale.US, "at.txt?cmd=%s",(buffstr).substring(buffstrlen - suffixlen, buffstrlen - suffixlen + suffixlen));
                buffstr = (buffstr).substring(0, buffstrlen - suffixlen);
                waitMore = waitMore - 1;
            } else {
                // request complete
                waitMore = 0;
            }
            res = String.format(Locale.US, "%s%s",res,buffstr);
        }
        return res;
    }

    public YSms fetchPdu(int slot) throws YAPI_Exception
    {
        byte[] binPdu = new byte[0];
        ArrayList<byte[]> arrPdu = new ArrayList<>();
        String hexPdu;
        YSms sms;

        binPdu = _download(String.format(Locale.US, "sms.json?pos=%d&len=1",slot));
        arrPdu = _json_get_array(binPdu);
        hexPdu = _decode_json_string(arrPdu.get(0));
        sms = new YSms(this);
        sms.set_slot(slot);
        sms.parsePdu(YAPIContext._hexStrToBin(hexPdu));
        return sms;
    }

    public int initGsm2Unicode()
    {
        int i;
        int uni;
        _gsm2unicode.clear();
        // 00-07
        _gsm2unicode.add(64);
        _gsm2unicode.add(163);
        _gsm2unicode.add(36);
        _gsm2unicode.add(165);
        _gsm2unicode.add(232);
        _gsm2unicode.add(233);
        _gsm2unicode.add(249);
        _gsm2unicode.add(236);
        // 08-0F
        _gsm2unicode.add(242);
        _gsm2unicode.add(199);
        _gsm2unicode.add(10);
        _gsm2unicode.add(216);
        _gsm2unicode.add(248);
        _gsm2unicode.add(13);
        _gsm2unicode.add(197);
        _gsm2unicode.add(229);
        // 10-17
        _gsm2unicode.add(916);
        _gsm2unicode.add(95);
        _gsm2unicode.add(934);
        _gsm2unicode.add(915);
        _gsm2unicode.add(923);
        _gsm2unicode.add(937);
        _gsm2unicode.add(928);
        _gsm2unicode.add(936);
        // 18-1F
        _gsm2unicode.add(931);
        _gsm2unicode.add(920);
        _gsm2unicode.add(926);
        _gsm2unicode.add(27);
        _gsm2unicode.add(198);
        _gsm2unicode.add(230);
        _gsm2unicode.add(223);
        _gsm2unicode.add(201);
        // 20-7A
        i = 32;
        while (i <= 122) {
            _gsm2unicode.add(i);
            i = i + 1;
        }
        // exceptions in range 20-7A
        _gsm2unicode.set(36, 164);
        _gsm2unicode.set(64, 161);
        _gsm2unicode.set(91, 196);
        _gsm2unicode.set(92, 214);
        _gsm2unicode.set(93, 209);
        _gsm2unicode.set(94, 220);
        _gsm2unicode.set(95, 167);
        _gsm2unicode.set(96, 191);
        // 7B-7F
        _gsm2unicode.add(228);
        _gsm2unicode.add(246);
        _gsm2unicode.add(241);
        _gsm2unicode.add(252);
        _gsm2unicode.add(224);
        // Invert table as well wherever possible
        _iso2gsm = new byte[256];
        i = 0;
        while (i <= 127) {
            uni = _gsm2unicode.get(i).intValue();
            if (uni <= 255) {
                _iso2gsm[uni] = (byte)(i & 0xff);
            }
            i = i + 1;
        }
        i = 0;
        while (i < 4) {
            // mark escape sequences
            _iso2gsm[91+i] = (byte)(27 & 0xff);
            _iso2gsm[123+i] = (byte)(27 & 0xff);
            i = i + 1;
        }
        // Done
        _gsm2unicodeReady = true;
        return YAPI.SUCCESS;
    }

    public ArrayList<Integer> gsm2unicode(byte[] gsm)
    {
        int i;
        int gsmlen;
        int reslen;
        ArrayList<Integer> res = new ArrayList<>();
        int uni;
        if (!(_gsm2unicodeReady)) {
            initGsm2Unicode();
        }
        gsmlen = (gsm).length;
        reslen = gsmlen;
        i = 0;
        while (i < gsmlen) {
            if ((gsm[i] & 0xff) == 27) {
                reslen = reslen - 1;
            }
            i = i + 1;
        }
        res.clear();
        i = 0;
        while (i < gsmlen) {
            uni = _gsm2unicode.get((gsm[i] & 0xff)).intValue();
            if ((uni == 27) && (i+1 < gsmlen)) {
                i = i + 1;
                uni = (gsm[i] & 0xff);
                if (uni < 60) {
                    if (uni < 41) {
                        if (uni==20) {
                            uni=94;
                        } else {
                            if (uni==40) {
                                uni=123;
                            } else {
                                uni=0;
                            }
                        }
                    } else {
                        if (uni==41) {
                            uni=125;
                        } else {
                            if (uni==47) {
                                uni=92;
                            } else {
                                uni=0;
                            }
                        }
                    }
                } else {
                    if (uni < 62) {
                        if (uni==60) {
                            uni=91;
                        } else {
                            if (uni==61) {
                                uni=126;
                            } else {
                                uni=0;
                            }
                        }
                    } else {
                        if (uni==62) {
                            uni=93;
                        } else {
                            if (uni==64) {
                                uni=124;
                            } else {
                                if (uni==101) {
                                    uni=164;
                                } else {
                                    uni=0;
                                }
                            }
                        }
                    }
                }
            }
            if (uni > 0) {
                res.add(uni);
            }
            i = i + 1;
        }
        return res;
    }

    public String gsm2str(byte[] gsm)
    {
        int i;
        int gsmlen;
        int reslen;
        byte[] resbin = new byte[0];
        String resstr;
        int uni;
        if (!(_gsm2unicodeReady)) {
            initGsm2Unicode();
        }
        gsmlen = (gsm).length;
        reslen = gsmlen;
        i = 0;
        while (i < gsmlen) {
            if ((gsm[i] & 0xff) == 27) {
                reslen = reslen - 1;
            }
            i = i + 1;
        }
        resbin = new byte[reslen];
        i = 0;
        reslen = 0;
        while (i < gsmlen) {
            uni = _gsm2unicode.get((gsm[i] & 0xff)).intValue();
            if ((uni == 27) && (i+1 < gsmlen)) {
                i = i + 1;
                uni = (gsm[i] & 0xff);
                if (uni < 60) {
                    if (uni < 41) {
                        if (uni==20) {
                            uni=94;
                        } else {
                            if (uni==40) {
                                uni=123;
                            } else {
                                uni=0;
                            }
                        }
                    } else {
                        if (uni==41) {
                            uni=125;
                        } else {
                            if (uni==47) {
                                uni=92;
                            } else {
                                uni=0;
                            }
                        }
                    }
                } else {
                    if (uni < 62) {
                        if (uni==60) {
                            uni=91;
                        } else {
                            if (uni==61) {
                                uni=126;
                            } else {
                                uni=0;
                            }
                        }
                    } else {
                        if (uni==62) {
                            uni=93;
                        } else {
                            if (uni==64) {
                                uni=124;
                            } else {
                                if (uni==101) {
                                    uni=164;
                                } else {
                                    uni=0;
                                }
                            }
                        }
                    }
                }
            }
            if ((uni > 0) && (uni < 256)) {
                resbin[reslen] = (byte)(uni & 0xff);
                reslen = reslen + 1;
            }
            i = i + 1;
        }
        resstr = new String(resbin, _yapi._deviceCharset);
        if (resstr.length() > reslen) {
            resstr = (resstr).substring(0, reslen);
        }
        return resstr;
    }

    public byte[] str2gsm(String msg)
    {
        byte[] asc = new byte[0];
        int asclen;
        int i;
        int ch;
        int gsm7;
        int extra;
        byte[] res = new byte[0];
        int wpos;
        if (!(_gsm2unicodeReady)) {
            initGsm2Unicode();
        }
        asc = (msg).getBytes(_yapi._deviceCharset);
        asclen = (asc).length;
        extra = 0;
        i = 0;
        while (i < asclen) {
            ch = (asc[i] & 0xff);
            gsm7 = (_iso2gsm[ch] & 0xff);
            if (gsm7 == 27) {
                extra = extra + 1;
            }
            if (gsm7 == 0) {
                // cannot use standard GSM encoding
                res = new byte[0];
                return res;
            }
            i = i + 1;
        }
        res = new byte[asclen+extra];
        wpos = 0;
        i = 0;
        while (i < asclen) {
            ch = (asc[i] & 0xff);
            gsm7 = (_iso2gsm[ch] & 0xff);
            res[wpos] = (byte)(gsm7 & 0xff);
            wpos = wpos + 1;
            if (gsm7 == 27) {
                if (ch < 100) {
                    if (ch<93) {
                        if (ch<92) {
                            gsm7=60;
                        } else {
                            gsm7=47;
                        }
                    } else {
                        if (ch<94) {
                            gsm7=62;
                        } else {
                            gsm7=20;
                        }
                    }
                } else {
                    if (ch<125) {
                        if (ch<124) {
                            gsm7=40;
                        } else {
                            gsm7=64;
                        }
                    } else {
                        if (ch<126) {
                            gsm7=41;
                        } else {
                            gsm7=61;
                        }
                    }
                }
                res[wpos] = (byte)(gsm7 & 0xff);
                wpos = wpos + 1;
            }
            i = i + 1;
        }
        return res;
    }

    public int checkNewMessages() throws YAPI_Exception
    {
        String bitmapStr;
        byte[] prevBitmap = new byte[0];
        byte[] newBitmap = new byte[0];
        int slot;
        int nslots;
        int pduIdx;
        int idx;
        int bitVal;
        int prevBit;
        int i;
        int nsig;
        int cnt;
        String sig;
        ArrayList<YSms> newArr = new ArrayList<>();
        ArrayList<YSms> newMsg = new ArrayList<>();
        ArrayList<YSms> newAgg = new ArrayList<>();
        ArrayList<String> signatures = new ArrayList<>();
        YSms sms;

        bitmapStr = get_slotsBitmap();
        if (bitmapStr.equals(_prevBitmapStr)) {
            return YAPI.SUCCESS;
        }
        prevBitmap = YAPIContext._hexStrToBin(_prevBitmapStr);
        newBitmap = YAPIContext._hexStrToBin(bitmapStr);
        _prevBitmapStr = bitmapStr;
        nslots = 8*(newBitmap).length;
        newArr.clear();
        newMsg.clear();
        signatures.clear();
        nsig = 0;
        // copy known messages
        pduIdx = 0;
        while (pduIdx < _pdus.size()) {
            sms = _pdus.get(pduIdx);
            slot = sms.get_slot();
            idx = (slot >> 3);
            if (idx < (newBitmap).length) {
                bitVal = (1 << ((slot & 7)));
                if ((((newBitmap[idx] & 0xff) & bitVal)) != 0) {
                    newArr.add(sms);
                    if (sms.get_concatCount() == 0) {
                        newMsg.add(sms);
                    } else {
                        sig = sms.get_concatSignature();
                        i = 0;
                        while ((i < nsig) && (sig.length() > 0)) {
                            if (signatures.get(i).equals(sig)) {
                                sig = "";
                            }
                            i = i + 1;
                        }
                        if (sig.length() > 0) {
                            signatures.add(sig);
                            nsig = nsig + 1;
                        }
                    }
                }
            }
            pduIdx = pduIdx + 1;
        }
        // receive new messages
        slot = 0;
        while (slot < nslots) {
            idx = (slot >> 3);
            bitVal = (1 << ((slot & 7)));
            prevBit = 0;
            if (idx < (prevBitmap).length) {
                prevBit = ((prevBitmap[idx] & 0xff) & bitVal);
            }
            if ((((newBitmap[idx] & 0xff) & bitVal)) != 0) {
                if (prevBit == 0) {
                    sms = fetchPdu(slot);
                    newArr.add(sms);
                    if (sms.get_concatCount() == 0) {
                        newMsg.add(sms);
                    } else {
                        sig = sms.get_concatSignature();
                        i = 0;
                        while ((i < nsig) && (sig.length() > 0)) {
                            if (signatures.get(i).equals(sig)) {
                                sig = "";
                            }
                            i = i + 1;
                        }
                        if (sig.length() > 0) {
                            signatures.add(sig);
                            nsig = nsig + 1;
                        }
                    }
                }
            }
            slot = slot + 1;
        }
        _pdus = newArr;
        // append complete concatenated messages
        i = 0;
        while (i < nsig) {
            sig = signatures.get(i);
            cnt = 0;
            pduIdx = 0;
            while (pduIdx < _pdus.size()) {
                sms = _pdus.get(pduIdx);
                if (sms.get_concatCount() > 0) {
                    if (sms.get_concatSignature().equals(sig)) {
                        if (cnt == 0) {
                            cnt = sms.get_concatCount();
                            newAgg.clear();
                        }
                        newAgg.add(sms);
                    }
                }
                pduIdx = pduIdx + 1;
            }
            if ((cnt > 0) && (newAgg.size() == cnt)) {
                sms = new YSms(this);
                sms.set_parts(newAgg);
                newMsg.add(sms);
            }
            i = i + 1;
        }
        _messages = newMsg;
        return YAPI.SUCCESS;
    }

    public ArrayList<YSms> get_pdus() throws YAPI_Exception
    {
        checkNewMessages();
        return _pdus;
    }

    /**
     * Clear the SMS units counters.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int clearPduCounters() throws YAPI_Exception
    {
        int retcode;

        retcode = set_pduReceived(0);
        if (retcode != YAPI.SUCCESS) {
            return retcode;
        }
        retcode = set_pduSent(0);
        return retcode;
    }

    /**
     * Sends a regular text SMS, with standard parameters. This function can send messages
     * of more than 160 characters, using SMS concatenation. ISO-latin accented characters
     * are supported. For sending messages with special unicode characters such as asian
     * characters and emoticons, use newMessage to create a new message and define
     * the content of using methods addText and addUnicodeData.
     *
     * @param recipient : a text string with the recipient phone number, either as a
     *         national number, or in international format starting with a plus sign
     * @param message : the text to be sent in the message
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int sendTextMessage(String recipient,String message) throws YAPI_Exception
    {
        YSms sms;

        sms = new YSms(this);
        sms.set_recipient(recipient);
        sms.addText(message);
        return sms.send();
    }

    /**
     * Sends a Flash SMS (class 0 message). Flash messages are displayed on the handset
     * immediately and are usually not saved on the SIM card. This function can send messages
     * of more than 160 characters, using SMS concatenation. ISO-latin accented characters
     * are supported. For sending messages with special unicode characters such as asian
     * characters and emoticons, use newMessage to create a new message and define
     * the content of using methods addText et addUnicodeData.
     *
     * @param recipient : a text string with the recipient phone number, either as a
     *         national number, or in international format starting with a plus sign
     * @param message : the text to be sent in the message
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int sendFlashMessage(String recipient,String message) throws YAPI_Exception
    {
        YSms sms;

        sms = new YSms(this);
        sms.set_recipient(recipient);
        sms.set_msgClass(0);
        sms.addText(message);
        return sms.send();
    }

    /**
     * Creates a new empty SMS message, to be configured and sent later on.
     *
     * @param recipient : a text string with the recipient phone number, either as a
     *         national number, or in international format starting with a plus sign
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public YSms newMessage(String recipient) throws YAPI_Exception
    {
        YSms sms;
        sms = new YSms(this);
        sms.set_recipient(recipient);
        return sms;
    }

    /**
     * Returns the list of messages received and not deleted. This function
     * will automatically decode concatenated SMS.
     *
     * @return an YSms object list.
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<YSms> get_messages() throws YAPI_Exception
    {
        checkNewMessages();
        return _messages;
    }

    /**
     * Continues the enumeration of SMS message box interfaces started using yFirstMessageBox().
     * Caution: You can't make any assumption about the returned SMS message box interfaces order.
     * If you want to find a specific a SMS message box interface, use MessageBox.findMessageBox()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YMessageBox object, corresponding to
     *         a SMS message box interface currently online, or a null pointer
     *         if there are no more SMS message box interfaces to enumerate.
     */
    public YMessageBox nextMessageBox()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindMessageBoxInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of SMS message box interfaces currently accessible.
     * Use the method YMessageBox.nextMessageBox() to iterate on
     * next SMS message box interfaces.
     *
     * @return a pointer to a YMessageBox object, corresponding to
     *         the first SMS message box interface currently online, or a null pointer
     *         if there are none.
     */
    public static YMessageBox FirstMessageBox()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("MessageBox");
        if (next_hwid == null)  return null;
        return FindMessageBoxInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of SMS message box interfaces currently accessible.
     * Use the method YMessageBox.nextMessageBox() to iterate on
     * next SMS message box interfaces.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YMessageBox object, corresponding to
     *         the first SMS message box interface currently online, or a null pointer
     *         if there are none.
     */
    public static YMessageBox FirstMessageBoxInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("MessageBox");
        if (next_hwid == null)  return null;
        return FindMessageBoxInContext(yctx, next_hwid);
    }

    //--- (end of generated code: YMessageBox implementation)
}

