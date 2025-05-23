package com.yoctopuce.YoctoAPI;


import javax.net.ssl.*;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.*;
import java.util.*;

//--- (generated code: YAPIContext return codes)
//--- (end of generated code: YAPIContext return codes)
//--- (generated code: YAPIContext class start)
/**
 * YAPIContext Class: Yoctopuce I/O context configuration.
 *
 *
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YAPIContext
{
//--- (end of generated code: YAPIContext class start)


    static class DataEvent
    {

        private final YFunction _fun;
        private final String _value;
        private final ArrayList<Integer> _report;
        private final double _timestamp;
        private final double _duration;
        private final YModule _module;
        private final int _beacon;

        DataEvent(YFunction fun, String value)
        {
            _module = null;
            _fun = fun;
            _value = value;
            _report = null;
            _timestamp = 0;
            _duration = 0;
            _beacon = -1;
        }

        DataEvent(YModule module)
        {
            _module = module;
            _fun = null;
            _value = null;
            _report = null;
            _timestamp = 0;
            _duration = 0;
            _beacon = -1;
        }

        DataEvent(YModule module, int beacon)
        {
            _module = module;
            _fun = null;
            _value = null;
            _report = null;
            _timestamp = 0;
            _duration = 0;
            _beacon = beacon;
        }


        DataEvent(YFunction fun, double timestamp, double duration, ArrayList<Integer> report)
        {
            _module = null;
            _fun = fun;
            _value = null;
            _timestamp = timestamp;
            _duration = duration;
            _report = report;
            _beacon = -1;
        }

        public void invoke()
        {
            if (_module != null) {
                if (_beacon < 0) {
                    _module._invokeConfigChangeCallback();
                } else {
                    _module._invokeBeaconCallback(_beacon);
                }
            } else {
                if (_value == null) {
                    YSensor sensor = (YSensor) _fun;
                    assert sensor != null;
                    YMeasure mesure = sensor._decodeTimedReport(_timestamp, _duration, _report);
                    sensor._invokeTimedReportCallback(mesure);
                } else {
                    // new value
                    assert _fun != null;
                    _fun._invokeValueCallback(_value);
                }
            }
        }

    }

    static class PlugEvent
    {

        public enum Event
        {

            PLUG, UNPLUG, CHANGE
        }

        Event ev;
        public YModule module;

        PlugEvent(YAPIContext yctx, Event ev, String serial)
        {
            this.ev = ev;
            this.module = YModule.FindModuleInContext(yctx, serial + ".module");
        }
    }


    private final static double[] decExp = new double[]{
            1.0e-6, 1.0e-5, 1.0e-4, 1.0e-3, 1.0e-2, 1.0e-1, 1.0,
            1.0e1, 1.0e2, 1.0e3, 1.0e4, 1.0e5, 1.0e6, 1.0e7, 1.0e8, 1.0e9};

    // Convert Yoctopuce 16-bit decimal floats to standard double-precision floats
    //
    static double _decimalToDouble(int val)
    {
        boolean negate = false;
        double res;
        int mantis = val & 2047;

        if (mantis == 0) {
            return 0.0;
        }
        if (val > 32767) {
            negate = true;
            val = 65536 - val;
        } else if (val < 0) {
            negate = true;
            val = -val;
        }
        int exp = val >> 11;
        res = (double) mantis * decExp[exp];
        return (negate ? -res : res);
    }

    // Convert standard double-precision floats to Yoctopuce 16-bit decimal floats
    //
    static long _doubleToDecimal(double val)
    {
        int negate = 0;
        double comp, mant;
        int decpow;
        long res;

        if (val == 0.0) {
            return 0;
        }
        if (val < 0) {
            negate = 1;
            val = -val;
        }
        comp = val / 1999.0;
        decpow = 0;
        while (comp > decExp[decpow] && decpow < 15) {
            decpow++;
        }
        mant = val / decExp[decpow];
        if (decpow == 15 && mant > 2047.0) {
            res = (15 << 11) + 2047; // overflow
        } else {
            res = (decpow << 11) + Math.round(mant);
        }
        return (negate != 0 ? -res : res);
    }

    // Parse an array of u16 encoded in a base64-like string with memory-based compression
    static ArrayList<Integer> _decodeWords(String data)
    {
        ArrayList<Integer> udata = new ArrayList<>();
        int datalen = data.length();
        int p = 0;
        while (p < datalen) {
            int val;
            int c = data.charAt(p++);
            if (c == (int) '*') {
                val = 0;
            } else if (c == (int) 'X') {
                val = 0xffff;
            } else if (c == (int) 'Y') {
                val = 0x7fff;
            } else if (c >= (int) 'a') {
                int srcpos = udata.size() - 1 - (c - (int) 'a');
                if (srcpos < 0) {
                    val = 0;
                } else {
                    val = udata.get(srcpos);
                }
            } else {
                if (p + 2 > datalen) {
                    return udata;
                }
                val = c - (int) '0';
                c = data.charAt(p++);
                val += (c - (int) '0') << 5;
                c = data.charAt(p++);
                if (c == (int) 'z') {
                    c = '\\';
                }
                val += (c - (int) '0') << 10;
            }
            udata.add(val);
        }
        return udata;
    }

    // Parse an array of u16 encoded in a base64-like string with memory-based compression
    static ArrayList<Integer> _decodeFloats(String data)
    {
        ArrayList<Integer> idata = new ArrayList<>();
        int datalen = data.length();
        int p = 0;
        while (p < datalen) {
            int val = 0;
            int sign = 1;
            int dec = 0;
            int decInc = 0;
            int c = data.charAt(p++);
            while (c != (int) '-' && (c < (int) '0' || c > (int) '9')) {
                if (p >= datalen) {
                    return idata;
                }
                c = data.charAt(p++);
            }
            if (c == '-') {
                if (p >= datalen) {
                    return idata;
                }
                sign = -sign;
                c = data.charAt(p++);
            }
            while ((c >= '0' && c <= '9') || c == '.') {
                if (c == '.') {
                    decInc = 1;
                } else if (dec < 3) {
                    val = val * 10 + (c - '0');
                    dec += decInc;
                }
                if (p < datalen) {
                    c = data.charAt(p++);
                } else {
                    c = 0;
                }
            }
            if (dec < 3) {
                if (dec == 0) val *= 1000;
                else if (dec == 1) val *= 100;
                else val *= 10;
            }
            idata.add(sign * val);
        }
        return idata;
    }

    // helper function to find pattern in byte[]
    static int _find_in_bytes(byte[] source, byte[] match)
    {
        // sanity checks
        if (source == null || match == null) {
            return -1;
        }
        if (source.length == 0 || match.length == 0) {
            return -1;
        }
        int ret = -1;
        int mpos = 0;
        byte m = match[mpos];
        for (int spos = 0; spos < source.length; spos++) {
            if (m == source[spos]) {
                // starting match
                if (mpos == 0) {
                    ret = spos;
                } // finishing match
                else if (mpos == match.length - 1) {
                    return ret;
                }
                mpos++;
                m = match[mpos];
            } else {
                ret = -1;
                mpos = 0;
                m = match[mpos];
            }
        }
        return ret;
    }

    public static int _atoi(String str)
    {
        str = str.trim();
        if (str.length() == 0) {
            return 0;
        }
        int s = 0;
        if (str.charAt(s) == '+') {
            s++;
        }
        int i = s;
        if (str.charAt(i) == '-') {
            i++;
        }
        for (; i < str.length(); i++) {

            //If we find a non-digit character we return false.
            if (!Character.isDigit(str.charAt(i)))
                break;
        }
        if (i == 0) {
            return 0;
        }
        str = str.substring(s, i);
        return Integer.parseInt(str);
    }

    private final static char[] _hexArray = "0123456789ABCDEF".toCharArray();

    static String _bytesToHexStr(byte[] bytes, int offset, int len)
    {
        char[] hexChars = new char[len * 2];
        for (int j = 0; j < len; j++) {
            int v = bytes[offset + j] & 0xFF;
            hexChars[j * 2] = _hexArray[v >>> 4];
            hexChars[j * 2 + 1] = _hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] _hexStrToBin(String hex_str)
    {
        int len = hex_str.length() / 2;
        byte[] res = new byte[len];
        for (int i = 0; i < len; i++) {
            res[i] = (byte) ((Character.digit(hex_str.charAt(i * 2), 16) << 4)
                    + Character.digit(hex_str.charAt(i * 2 + 1), 16));
        }
        return res;
    }

    public static byte[] _bytesMerge(byte[] array_a, byte[] array_b)
    {
        byte[] res = new byte[array_a.length + array_b.length];
        System.arraycopy(array_a, 0, res, 0, array_a.length);
        System.arraycopy(array_b, 0, res, array_a.length, array_b.length);
        return res;
    }


    // Return the class name for a given function ID or full Hardware Id
    static String functionClass(String funcid)
    {
        int dotpos = funcid.indexOf('.');

        if (dotpos >= 0) {
            funcid = funcid.substring(dotpos + 1);
        }
        int classlen = funcid.length();

        while (funcid.charAt(classlen - 1) <= 57) {
            classlen--;
        }

        return funcid.substring(0, 1).toUpperCase(Locale.US)
                + funcid.substring(1, classlen);
    }

    String _defaultEncoding = YAPI.DefaultEncoding;
    final Charset _deviceCharset;
    private int _apiMode;
    private final ArrayList<YGenericHub> _hubs = new ArrayList<>(1); // array of root urls
    private final Queue<PlugEvent> _pendingCallbacks = new LinkedList<>();
    private final Queue<DataEvent> _data_events = new LinkedList<>();

    private final Object _regCbLock = new Object();
    private YAPI.DeviceArrivalCallback _arrivalCallback;
    private YAPI.DeviceChangeCallback _namechgCallback;
    private YAPI.DeviceRemovalCallback _removalCallback;
    private final Object _logCallbackLock = new Object();
    private YAPI.LogCallback _logCallback;

    private final Object _newHubCallbackLock = new Object();
    private YAPI.HubDiscoveryCallback _HubDiscoveryCallback;
    private final HashMap<Integer, YAPI.CalibrationHandlerCallback> _calibHandlers = new HashMap<>();
    private final YSSDP _ssdp;
    final YHash _yHash;
    private final ArrayList<YFunction> _ValueCallbackList = new ArrayList<>();
    private final ArrayList<YFunction> _TimedReportCallbackList = new ArrayList<>();
    private final Map<YModule, Integer> _moduleCallbackList = new HashMap<>();
    private final KeyStore _keyStore;
    private int _sslFlags = 0;


    private int _pktAckDelay = 0;

    long _deviceListValidityMs = 10000;
    int _networkTimeoutMs = YHTTPHub.YIO_DEFAULT_TCP_TIMEOUT;
    final Object _functionCacheLock;
    private final Map<Integer, YHub> _yhub_cache = new HashMap<>();


    //--- (generated code: YAPIContext definitions)
    protected long _defaultCacheValidity = 5;

    //--- (end of generated code: YAPIContext definitions)

    public boolean _checkForDuplicateHub(YGenericHub newhub)
    {
        String serial = newhub.getSerialNumber();
        YGenericHub previous = null;
        synchronized (_hubs) {
            for (YGenericHub hub : _hubs) {
                if (!hub.isEnabled()) {
                    continue;
                }
                String current = hub.getSerialNumber();
                if (serial.equals(current) && hub != newhub) {
                    previous = hub;
                    break;
                }
            }
            if (previous != null) {
                previous.merge(newhub);
                newhub.disable();
            }
        }
        if (previous != null) {
            newhub.requestStop();
            return true;
        }
        return false;
    }

    private final YSSDP.YSSDPReportInterface _ssdpCallback = new YSSDP.YSSDPReportInterface()
    {
        @Override
        public void HubDiscoveryCallback(String serial, String urlToRegister, String urlToUnregister)
        {
            if (urlToRegister != null) {
                synchronized (_newHubCallbackLock) {
                    if (_HubDiscoveryCallback != null)
                        _HubDiscoveryCallback.yHubDiscoveryCallback(serial, urlToRegister);
                }
            }
            if ((_apiMode & YAPI.DETECT_NET) != 0) {
                if (urlToRegister != null) {
                    if (urlToUnregister != null) {
                        UnregisterHub(urlToUnregister);
                    }
                    try {
                        PreregisterHub(urlToRegister);
                    } catch (YAPI_Exception ex) {
                        _Log("Unable to register hub " + urlToRegister + " detected by SSDP:" + ex.toString());
                    }
                }
            }
        }
    };


    private final static YAPI.CalibrationHandlerCallback linearCalibrationHandler = new YAPI.CalibrationHandlerCallback()
    {

        @Override
        public double yCalibrationHandler(double rawValue, int calibType, ArrayList<Integer> params, ArrayList<Double> rawValues, ArrayList<Double> refValues)
        {
            // calibration types n=1..10 and 11.20 are meant for linear calibration using n points
            int npt;
            double x = rawValues.get(0);
            double adj = refValues.get(0) - x;
            int i = 0;

            if (calibType < YAPI.YOCTO_CALIB_TYPE_OFS) {
                npt = calibType % 10;
                if (npt > rawValues.size()) npt = rawValues.size();
                if (npt > refValues.size()) npt = refValues.size();
            } else {
                npt = refValues.size();
            }
            while (rawValue > rawValues.get(i) && ++i < npt) {
                double x2 = x;
                double adj2 = adj;

                x = rawValues.get(i);
                adj = refValues.get(i) - x;

                if (rawValue < x && x > x2) {
                    adj = adj2 + (adj - adj2) * (rawValue - x2) / (x - x2);
                }
            }
            return rawValue + adj;
        }
    };

    //INTERNAL METHOD:

    public YAPIContext()
    {
        KeyStore keyStore;
        Charset charset;
        try {
            charset = Charset.forName(YAPI.DefaultEncoding);
        } catch (Exception dummy) {
            charset = Charset.defaultCharset();
        }
        _deviceCharset = charset;
        _yHash = new YHash(this);
        _ssdp = new YSSDP(this);
        _functionCacheLock = new Object();
        resetContext();

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            keyStore = null;
        }
        _keyStore = keyStore;


        //--- (generated code: YAPIContext attributes initialization)
        //--- (end of generated code: YAPIContext attributes initialization)

    }


    Socket CreateSSLSocket(int sslFlags) throws IOException
    {
        return this.getSocketFactory(sslFlags).createSocket();
    }


    private void resetContext()
    {
        _apiMode = 0;
        _pendingCallbacks.clear();
        _data_events.clear();
        _arrivalCallback = null;
        _namechgCallback = null;
        _removalCallback = null;
        _logCallback = null;
        _HubDiscoveryCallback = null;
        _hubs.clear();
        _yhub_cache.clear();
        _calibHandlers.clear();
        _ssdp.reset();
        _yHash.reset();
        _ValueCallbackList.clear();
        _TimedReportCallbackList.clear();
        _moduleCallbackList.clear();
        for (int i = 1; i <= 20; i++) {
            _calibHandlers.put(i, linearCalibrationHandler);
        }
        _calibHandlers.put(YAPI.YOCTO_CALIB_TYPE_OFS, linearCalibrationHandler);

    }

    void _pushPlugEvent(String serial, String productName, int productId)
    {
        if (_arrivalCallback != null) {
            synchronized (_pendingCallbacks) {
                _pendingCallbacks.add(new PlugEvent(this, PlugEvent.Event.PLUG, serial));
            }
        }
    }

    void _pushChangeEvent(String serial)
    {
        if (_namechgCallback != null) {
            synchronized (_pendingCallbacks) {
                _pendingCallbacks.add(new PlugEvent(this, YAPIContext.PlugEvent.Event.CHANGE, serial));
            }
        }
    }

    void _pushUnPlugEvent(String serial)
    {
        if (_removalCallback != null) {

            synchronized (_pendingCallbacks) {
                _pendingCallbacks.add(new PlugEvent(this, PlugEvent.Event.UNPLUG, serial));
            }
        }
    }


    // Queue a function data event (timed report of notification value)
    void _PushDataEvent(DataEvent ev)
    {
        synchronized (_data_events) {
            _data_events.add(ev);
        }
    }

    /*
     * Return a the calibration handler for a given type
     */
    YAPI.CalibrationHandlerCallback _getCalibrationHandler(int calibType)
    {
        if (!_calibHandlers.containsKey(calibType)) {
            return null;
        }
        return _calibHandlers.get(calibType);
    }


    YDevice funcGetDevice(String className, String func) throws YAPI_Exception
    {
        String resolved;
        try {
            resolved = _yHash.resolveSerial(className, func);
        } catch (YAPI_Exception ex) {
            if (ex.errorType == YAPI.DEVICE_NOT_FOUND && _hubs.isEmpty()) {
                throw new YAPI_Exception(ex.errorType,
                        "Impossible to contact any device because no hub has been registered");
            } else {
                _updateDeviceList_internal(true, false);
                resolved = _yHash.resolveSerial(className, func);
            }
        }
        YDevice dev = _yHash.getDevice(resolved);
        if (dev == null) {
            // try to force a device list update to check if the device arrived
            // in between
            _updateDeviceList_internal(true, false);
            dev = _yHash.getDevice(resolved);
            if (dev == null) {
                throw new YAPI_Exception(YAPI.DEVICE_NOT_FOUND, "Device [" + resolved + "] not online");
            }

        }
        return dev;
    }


    void _UpdateValueCallbackList(YFunction func, boolean add)
    {
        if (add) {
            func.isOnline();
            synchronized (_ValueCallbackList) {
                if (!_ValueCallbackList.contains(func)) {
                    _ValueCallbackList.add(func);
                }
            }
        } else {
            synchronized (_ValueCallbackList) {
                _ValueCallbackList.remove(func);
            }
        }
    }

    YFunction _GetValueCallback(String hwid)
    {
        synchronized (_ValueCallbackList) {
            for (YFunction func : _ValueCallbackList) {
                try {
                    if (func.getHardwareId().equals(hwid)) {
                        return func;
                    }
                } catch (YAPI_Exception ignore) {
                }
            }
        }
        return null;
    }


    void _UpdateTimedReportCallbackList(YFunction func, boolean add)
    {
        if (add) {
            func.isOnline();
            synchronized (_TimedReportCallbackList) {
                if (!_TimedReportCallbackList.contains(func)) {
                    _TimedReportCallbackList.add(func);
                }
            }
        } else {
            synchronized (_TimedReportCallbackList) {
                _TimedReportCallbackList.remove(func);
            }
        }
    }


    void _UpdateModuleCallbackList(YModule module, boolean add)
    {
        if (add) {
            module.isOnline();
            synchronized (_moduleCallbackList) {
                if (!_moduleCallbackList.containsKey(module)) {
                    _moduleCallbackList.put(module, 1);
                } else {
                    _moduleCallbackList.put(module, _moduleCallbackList.get(module) + 1);
                }
            }
        } else {
            synchronized (_moduleCallbackList) {
                if (_moduleCallbackList.containsKey(module) && _moduleCallbackList.get(module) > 1) {
                    _moduleCallbackList.put(module, _moduleCallbackList.get(module) - 1);
                }
            }
        }
    }


    YModule _GetModuleCallack(String serial)
    {
        YModule module = YModule.FindModuleInContext(this, serial + ".module");
        synchronized (_moduleCallbackList) {
            if (_moduleCallbackList.containsKey(module) && _moduleCallbackList.get(module) > 0) {
                return module;
            }
        }
        return null;
    }


    YFunction _GetTimedReportCallback(String hwid)
    {
        synchronized (_TimedReportCallbackList) {
            for (YFunction func : _TimedReportCallbackList) {
                try {
                    if (func.getHardwareId().equals(hwid)) {
                        return func;
                    }
                } catch (YAPI_Exception ignore) {
                }
            }
        }
        return null;
    }
    private String GetYAPISharedLibraryPath_internal()
    {
        return YUSBHub.getYAPISharedLibraryPath();
    }

    public String AddUdevRule_internal(boolean force)
    {
        return YUSBHub.addUdevRule(force);
    }

    private synchronized int _AddNewHub(String url, boolean reportConnnectionLost, InputStream request, OutputStream response, Object session) throws YAPI_Exception
    {
        synchronized (_hubs) {
            for (YGenericHub h : _hubs) {
                if (h.isEnabled() && h.isSameHub(url, request, response, session)) {
                    h.addKnownURL(url);
                    return YAPI.SUCCESS;
                }
            }
        }
        YGenericHub newhub;
        YGenericHub.HTTPParams parsedurl;
        parsedurl = new YGenericHub.HTTPParams(url);
        // Add hub to known list
        if (url.equals("usb")) {
            YUSBHub.CheckUSBAcces();
            newhub = new YUSBHub(this, true, _pktAckDelay);
        } else if (url.equals("usb_silent")) {
            YUSBHub.CheckUSBAcces();
            newhub = new YUSBHub(this, false, _pktAckDelay);
        } else if (url.equals("net")) {
            if ((_apiMode & YAPI.DETECT_NET) == 0) {
                _apiMode |= YAPI.DETECT_NET;
                _ssdp.addCallback(_ssdpCallback);
            }
            return YAPI.SUCCESS;
        } else if (parsedurl.getHost().equals("callback")) {
            if (session != null) {
                newhub = new YHTTPHub(this, parsedurl, reportConnnectionLost, session);
            } else {
                newhub = new YCallbackHub(this, parsedurl, request, response);
            }
        } else {
            newhub = new YHTTPHub(this, parsedurl, reportConnnectionLost, null);
        }
        newhub.startNotifications();
        synchronized (_hubs) {
            _hubs.add(newhub);
        }
        return YAPI.SUCCESS;
    }


    private void _updateDeviceList_internal(boolean forceupdate, boolean invokecallbacks) throws YAPI_Exception
    {
        synchronized (this) {
            // Rescan all hubs and update list of online devices
            for (YGenericHub h : _hubs) {
                if (!h.isEnabled()) {
                    continue;
                }
                try {
                    h.updateDeviceList(forceupdate);
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.IO_ERROR,
                            "Thread has been interrupted");
                }
            }
        }
        // after processing all hubs, invoke pending callbacks if required
        if (invokecallbacks) {
            while (true) {
                PlugEvent evt;
                synchronized (_pendingCallbacks) {
                    if (_pendingCallbacks.isEmpty()) {
                        break;
                    }
                    evt = _pendingCallbacks.poll();
                }
                synchronized (_regCbLock) {
                    if (evt != null) {
                        switch (evt.ev) {
                            case PLUG:
                                if (_arrivalCallback != null) {
                                    // force (re)loading the module object with up-to-date information
                                    // this will also ensure we have a valid serialNumber in cache on unplug
                                    evt.module.isOnline();
                                    _arrivalCallback.yDeviceArrival(evt.module);
                                }

                                break;
                            case CHANGE:
                                if (_namechgCallback != null) {
                                    _namechgCallback.yDeviceChange(evt.module);
                                }
                                break;
                            case UNPLUG:
                                if (_removalCallback != null) {
                                    _removalCallback.yDeviceRemoval(evt.module);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    void _Log(String message)
    {
        synchronized (_logCallbackLock) {
            if (_logCallback != null) {
                _logCallback.yLog(message);
            }
        }
    }

    byte[] BasicHTTPRequest(String url, int mstimout, int ssl_flags) throws YAPI_Exception
    {
        ByteArrayOutputStream result = new ByteArrayOutputStream(1024);
        URL u;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            throw new YAPI_Exception(YAPI.IO_ERROR, e.getLocalizedMessage());
        }
        if (url.startsWith("http://")) {
            String host = u.getHost();
            int port = u.getPort();
            if (port < 0) {
                port = 80;
            }
            String path = u.getFile();
            if (path.isEmpty()) {
                path = "/";
            }
            return yHTTPRequest.yTcpDownload(this, host, port, path);
        } else {
            BufferedInputStream in = null;
            try {
                URLConnection connection = u.openConnection();
                HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) connection;
                httpsUrlConnection.setConnectTimeout(mstimout);
                int flags = ssl_flags | this._sslFlags;
                httpsUrlConnection.setSSLSocketFactory(this.getSocketFactory(flags));
                if ((flags & YAPI.NO_HOSTNAME_CHECK) != 0) {
                    httpsUrlConnection.setHostnameVerifier(new HostnameVerifier()
                    {
                        @Override
                        public boolean verify(String hostname, SSLSession sslSession)
                        {
                            return true;
                        }
                    });
                }
                in = new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[1024];
                int readed = 0;
                while (true) {
                    readed = in.read(buffer, 0, buffer.length);
                    if (readed < 0) {
                        // end of connection
                        break;
                    } else {
                        result.write(buffer, 0, readed);
                    }
                }
            } catch (SSLHandshakeException e) {
                throw new YAPI_Exception(YAPI.SSL_UNK_CERT, "unable to contact " + url + " :" + e.getLocalizedMessage(), e);
            } catch (SSLException e) {
                throw new YAPI_Exception(YAPI.SSL_ERROR, "unable to contact " + url + " :" + e.getLocalizedMessage(), e);
            } catch (IOException e) {
                throw new YAPI_Exception(YAPI.IO_ERROR, "unable to contact " + url + " :" + e.getLocalizedMessage(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            return result.toByteArray();
        }
    }

    private SSLSocketFactory getSocketFactory(int sslFlags)
    {
        SSLContext sslContext = null;
        YTrustManager yTrustManager = null;
        try {
            TrustManager[] trustManagers = new TrustManager[1];
            trustManagers[0] = new YTrustManager(_keyStore, sslFlags | _sslFlags);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return null;
        }
    }


    public void SetNetworkTimeout_internal(int networkMsTimeout)
    {
        _networkTimeoutMs = networkMsTimeout;
    }

    public int GetNetworkTimeout_internal()
    {
        return _networkTimeoutMs;
    }

    public String DownloadHostCertificate_internal(String url, long mstimeout)
    {
        StringBuilder res = new StringBuilder();
        YGenericHub.HTTPParams parsedurl = new YGenericHub.HTTPParams(url);
        try {
            boolean https_req = parsedurl.useSecureSocket();
            if (parsedurl.getPort() == YAPI.YOCTO_DEFAULT_HTTPS_PORT) {
                https_req = true;
            }
            String clean_url = String.format("%s://%s:%d%s/info.json", parsedurl.useSecureSocket() ? "https" : "http", parsedurl.getHost(), parsedurl.getPort(), parsedurl.getSubDomain());
            URL destinationURL = new URL(clean_url);
            HttpsURLConnection conn = (HttpsURLConnection) destinationURL.openConnection();
            conn.setSSLSocketFactory(getSocketFactory(YAPI.NO_HOSTNAME_CHECK | YAPI.NO_TRUSTED_CA_CHECK |
                    YAPI.NO_EXPIRATION_CHECK));
            conn.connect();
            Certificate[] certs = conn.getServerCertificates();
            int i = 1;
            for (Certificate cert : certs) {
                if (cert instanceof X509Certificate) {
                    res.append("-----BEGIN CERTIFICATE-----\n");
                    byte[] encoded = cert.getEncoded();
                    res.append(YAPI.Base64Encode(encoded, 0, encoded.length));
                    res.append("-----END CERTIFICATE-----\n");
                }
            }
        } catch (CertificateEncodingException |
                 IOException e) {
            return "error:" + e.getLocalizedMessage();
        }
        return res.toString();
    }

    public String AddTrustedCertificates_internal(String certificate)
    {
        if (_keyStore != null) {
            ArrayList<X509Certificate> certs;
            try {
                certs = YTrustManager.parsePemCert(certificate);
            } catch (CertificateException e) {
                return "error:" + e.getLocalizedMessage();
            }
            for (X509Certificate c : certs) {
                try {
                    _keyStore.setCertificateEntry("cert" + c.toString(), c);
                } catch (KeyStoreException e) {
                    return "error:" + e.getLocalizedMessage();
                }
            }

            return "";
        } else {
            return "Error: Not supported";
        }
    }

    public String SetNetworkSecurityOptions_internal(int options)
    {
        this._sslFlags = options;
        return "";
    }

    private String SetTrustedCertificatesList_internal(String certificatePath)
    {
        return "error: Not supported in Java";
    }

    //PUBLIC METHOD:
    //--- (generated code: YAPIContext implementation)

    /**
     * Modifies the delay between each forced enumeration of the used YoctoHubs.
     * By default, the library performs a full enumeration every 10 seconds.
     * To reduce network traffic, you can increase this delay.
     * It's particularly useful when a YoctoHub is connected to the GSM network
     * where traffic is billed. This parameter doesn't impact modules connected by USB,
     * nor the working of module arrival/removal callbacks.
     * Note: you must call this function after yInitAPI.
     *
     * @param deviceListValidity : nubmer of seconds between each enumeration.
     *
     */
    public void SetDeviceListValidity(int deviceListValidity)
    {
        SetDeviceListValidity_internal(deviceListValidity);
    }

    //cannot be generated for Java:
    //public void SetDeviceListValidity_internal(int deviceListValidity)
    /**
     * Returns the delay between each forced enumeration of the used YoctoHubs.
     * Note: you must call this function after yInitAPI.
     *
     * @return the number of seconds between each enumeration.
     */
    public int GetDeviceListValidity()
    {
        return GetDeviceListValidity_internal();
    }

    //cannot be generated for Java:
    //public int GetDeviceListValidity_internal()
    /**
     * Returns the path to the dynamic YAPI library. This function is useful for debugging problems loading the
     *  dynamic library YAPI. This function is supported by the C#, Python and VB languages. The other
     * libraries return an
     * empty string.
     *
     * @return a string containing the path of the YAPI dynamic library.
     */
    public String GetYAPISharedLibraryPath()
    {
        return GetYAPISharedLibraryPath_internal();
    }

    //cannot be generated for Java:
    //public String GetYAPISharedLibraryPath_internal()
    /**
     * Adds a UDEV rule which authorizes all users to access Yoctopuce modules
     * connected to the USB ports. This function works only under Linux. The process that
     * calls this method must have root privileges because this method changes the Linux configuration.
     *
     * @param force : if true, overwrites any existing rule.
     *
     * @return an empty string if the rule has been added.
     *
     * On failure, returns a string that starts with "error:".
     */
    public String AddUdevRule(boolean force)
    {
        return AddUdevRule_internal(force);
    }

    //cannot be generated for Java:
    //public String AddUdevRule_internal(boolean force)
    /**
     * Download the TLS/SSL certificate from the hub. This function allows to download a TLS/SSL certificate to add it
     * to the list of trusted certificates using the AddTrustedCertificates method.
     *
     * @param url : the root URL of the VirtualHub V2 or HTTP server.
     * @param mstimeout : the number of milliseconds available to download the certificate.
     *
     * @return a string containing the certificate. In case of error, returns a string starting with "error:".
     */
    public String DownloadHostCertificate(String url,long mstimeout)
    {
        return DownloadHostCertificate_internal(url, mstimeout);
    }

    //cannot be generated for Java:
    //public String DownloadHostCertificate_internal(String url,long mstimeout)
    /**
     * Adds a TLS/SSL certificate to the list of trusted certificates. By default, the library
     * library will reject TLS/SSL connections to servers whose certificate is not known. This function
     * function allows to add a list of known certificates. It is also possible to disable the verification
     * using the SetNetworkSecurityOptions method.
     *
     * @param certificate : a string containing one or more certificates.
     *
     * @return an empty string if the certificate has been added correctly.
     *         In case of error, returns a string starting with "error:".
     */
    public String AddTrustedCertificates(String certificate)
    {
        return AddTrustedCertificates_internal(certificate);
    }

    //cannot be generated for Java:
    //public String AddTrustedCertificates_internal(String certificate)
    /**
     *  Set the path of Certificate Authority file on local filesystem. This method takes as a parameter
     * the path of a file containing all certificates in PEM format.
     *  For technical reasons, only one file can be specified. So if you need to connect to several Hubs
     * instances with self-signed certificates, you'll need to use
     *  a single file containing all the certificates end-to-end. Passing a empty string will restore the
     * default settings. This option is only supported by PHP library.
     *
     * @param certificatePath : the path of the file containing all certificates in PEM format.
     *
     * @return an empty string if the certificate has been added correctly.
     *         In case of error, returns a string starting with "error:".
     */
    public String SetTrustedCertificatesList(String certificatePath)
    {
        return SetTrustedCertificatesList_internal(certificatePath);
    }

    //cannot be generated for Java:
    //public String SetTrustedCertificatesList_internal(String certificatePath)
    /**
     * Enables or disables certain TLS/SSL certificate checks.
     *
     * @param opts : The options are YAPI.NO_TRUSTED_CA_CHECK,
     *         YAPI.NO_EXPIRATION_CHECK, YAPI.NO_HOSTNAME_CHECK.
     *
     * @return an empty string if the options are taken into account.
     *         On error, returns a string beginning with "error:".
     */
    public String SetNetworkSecurityOptions(int opts)
    {
        return SetNetworkSecurityOptions_internal(opts);
    }

    //cannot be generated for Java:
    //public String SetNetworkSecurityOptions_internal(int opts)
    /**
     * Modifies the network connection delay for yRegisterHub() and yUpdateDeviceList().
     * This delay impacts only the YoctoHubs and VirtualHub
     * which are accessible through the network. By default, this delay is of 20000 milliseconds,
     * but depending on your network you may want to change this delay,
     * gor example if your network infrastructure is based on a GSM connection.
     *
     * @param networkMsTimeout : the network connection delay in milliseconds.
     *
     */
    public void SetNetworkTimeout(int networkMsTimeout)
    {
        SetNetworkTimeout_internal(networkMsTimeout);
    }

    //cannot be generated for Java:
    //public void SetNetworkTimeout_internal(int networkMsTimeout)
    /**
     * Returns the network connection delay for yRegisterHub() and yUpdateDeviceList().
     * This delay impacts only the YoctoHubs and VirtualHub
     * which are accessible through the network. By default, this delay is of 20000 milliseconds,
     * but depending on your network you may want to change this delay,
     * for example if your network infrastructure is based on a GSM connection.
     *
     * @return the network connection delay in milliseconds.
     */
    public int GetNetworkTimeout()
    {
        return GetNetworkTimeout_internal();
    }

    //cannot be generated for Java:
    //public int GetNetworkTimeout_internal()
    /**
     * Change the validity period of the data loaded by the library.
     * By default, when accessing a module, all the attributes of the
     * module functions are automatically kept in cache for the standard
     * duration (5 ms). This method can be used to change this standard duration,
     * for example in order to reduce network or USB traffic. This parameter
     * does not affect value change callbacks
     * Note: This function must be called after yInitAPI.
     *
     * @param cacheValidityMs : an integer corresponding to the validity attributed to the
     *         loaded function parameters, in milliseconds.
     *
     */
    public void SetCacheValidity(long cacheValidityMs)
    {
        _defaultCacheValidity = cacheValidityMs;
    }

    /**
     * Returns the validity period of the data loaded by the library.
     * This method returns the cache validity of all attributes
     * module functions.
     * Note: This function must be called after yInitAPI .
     *
     * @return an integer corresponding to the validity attributed to the
     *         loaded function parameters, in milliseconds
     */
    public long GetCacheValidity()
    {
        return _defaultCacheValidity;
    }

    public YHub nextHubInUseInternal(int hubref)
    {
        return nextHubInUseInternal_internal(hubref);
    }

    //cannot be generated for Java:
    //public YHub nextHubInUseInternal_internal(int hubref)
    public YHub getYHubObj(int hubref)
    {
        YHub obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = _findYHubFromCache(hubref);
            if (obj == null) {
                obj = new YHub(this, hubref);
                _addYHubToCache(hubref, obj);
            }
        }
        return obj;
    }

    //--- (end of generated code: YAPIContext implementation)

    public YGenericHub getGenHub(int hubref)
    {
        for (YGenericHub h : _hubs) {
            if (h.get_hubid() == hubref) {
                return h;
            }
        }
        return null;
    }


    private void _addYHubToCache(int hubref, YHub obj)
    {
        _yhub_cache.put(hubref, obj);
    }

    private YHub _findYHubFromCache(int hubref)
    {
        return _yhub_cache.get(hubref);
    }


    private synchronized YHub nextHubInUseInternal_internal(int hubref)
    {

        int nextref = hubref < 0 ? 0 : hubref + 1;
        int next_avail_ref = Integer.MAX_VALUE;

        for (YGenericHub h : _hubs) {
            if (!h.isEnabled()) {
                continue;
            }
            int hubid = h.get_hubid();
            if (hubid == nextref) {
                return this.getYHubObj(nextref);
            } else {
                if (hubid > nextref && hubid < next_avail_ref) {
                    next_avail_ref = hubid;
                }
            }

        }
        if (next_avail_ref != Integer.MAX_VALUE) {
            return this.getYHubObj(next_avail_ref);
        }

        return null;
    }


    /**
     * Enables the HTTP callback cache. When enabled, this cache reduces the quantity of data sent to the
     * PHP script by 50% to 70%. To enable this cache, the method ySetHTTPCallbackCacheDir()
     * must be called before any call to yRegisterHub(). This method takes in parameter the path
     * of the directory used for saving data between each callback. This folder must exist and the
     * PHP script needs to have write access to it. It is recommended to use a folder that is not published
     * on the Web server since the library will save some data of Yoctopuce devices into this folder.
     *
     * Note: This feature is supported by YoctoHub and VirtualHub since version 27750.
     *
     * @param directory : the path of the folder that will be used as cache.
     *
     * @throws YAPI_Exception on error
     */
    public void SetHTTPCallbackCacheDir(String directory) throws YAPI_Exception
    {
        throw new YAPI_Exception(YAPI.NOT_SUPPORTED, "SetHTTPCallbackCacheDir is not supported by Java lib");
    }

    /**
     * Disables the HTTP callback cache. This method disables the HTTP callback cache, and
     * can additionally cleanup the cache directory.
     *
     * @param removeFiles : True to clear the content of the cache.
     * @throws YAPI_Exception on error
     */
    public void ClearHTTPCallbackCacheDir(boolean removeFiles)
    {
    }


    private void SetDeviceListValidity_internal(long deviceListValidity)
    {
        _deviceListValidityMs = deviceListValidity * 1000;
    }

    private int GetDeviceListValidity_internal()
    {
        return (int) (_deviceListValidityMs / 1000);
    }

    /**
     * Enables the acknowledge of every USB packet received by the Yoctopuce library.
     * This function allows the library to run on Android phones that tend to loose USB packets.
     * By default, this feature is disabled because it doubles the number of packets sent and slows
     * down the API considerably. Therefore, the acknowledge of incoming USB packets should only be
     * enabled on phones or tablets that loose USB packets. A delay of 50 milliseconds is generally
     * enough. In case of doubt, contact Yoctopuce support. To disable USB packets acknowledge,
     * call this function with the value 0. Note: this feature is only available on Android.
     *
     * @param pktAckDelay : then number of milliseconds before the module
     *         resend the last USB packet.
     */
    public void SetUSBPacketAckMs(int pktAckDelay)
    {
        this._pktAckDelay = pktAckDelay;
    }


    /**
     * Returns the version identifier for the Yoctopuce library in use.
     * The version is a string in the form "Major.Minor.Build",
     * for instance "1.01.5535". For languages using an external
     * DLL (for instance C#, VisualBasic or Delphi), the character string
     * includes as well the DLL version, for instance
     * "1.01.5535 (1.01.5439)".
     *
     * If you want to verify in your code that the library version is
     * compatible with the version that you have used during development,
     * verify that the major number is strictly equal and that the minor
     * number is greater or equal. The build number is not relevant
     * with respect to the library compatibility.
     *
     * @return a character string describing the library version.
     */
    public static String GetAPIVersion()
    {
        return YAPI.GetAPIVersion();
    }


    /**
     * Initializes the Yoctopuce programming library explicitly.
     * It is not strictly needed to call yInitAPI(), as the library is
     * automatically  initialized when calling yRegisterHub() for the
     * first time.
     *
     * When YAPI.DETECT_NONE is used as detection mode,
     * you must explicitly use yRegisterHub() to point the API to the
     * VirtualHub on which your devices are connected before trying to access them.
     *
     * @param mode : an integer corresponding to the type of automatic
     *         device detection to use. Possible values are
     *         YAPI.DETECT_NONE, YAPI.DETECT_USB, YAPI.DETECT_NET,
     *         and YAPI.DETECT_ALL.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * On failure returns a negative error code.
     */
    public int InitAPI(int mode) throws YAPI_Exception
    {
        if ((mode & YAPI.DETECT_NET) != 0) {
            RegisterHub("net");
        }
        if ((mode & YAPI.RESEND_MISSING_PKT) != 0) {
            _pktAckDelay = 50;
        }
        if ((mode & YAPI.DETECT_USB) != 0) {
            RegisterHub("usb");
        }
        return YAPI.SUCCESS;
    }

    /**
     * Waits for all pending communications with Yoctopuce devices to be
     * completed then frees dynamically allocated resources used by
     * the Yoctopuce library.
     *
     * From an operating system standpoint, it is generally not required to call
     * this function since the OS will automatically free allocated resources
     * once your program is completed. However, there are two situations when
     * you may really want to use that function:
     *
     * - Free all dynamically allocated memory blocks in order to
     * track a memory leak.
     *
     * - Send commands to devices right before the end
     * of the program. Since commands are sent in an asynchronous way
     * the program could exit before all commands are effectively sent.
     *
     * You should not call any other library function after calling
     * yFreeAPI(), or your program will crash.
     */
    public synchronized void FreeAPI()
    {
        if ((_apiMode & YAPI.DETECT_NET) != 0) {
            _ssdp.Stop();
        }
        for (YGenericHub h : _hubs) {
            h.stopNotifications();
            h.release();
        }
        resetContext();
    }


    /**
     * Set up the Yoctopuce library to use modules connected on a given machine. Idealy this
     * call will be made once at the begining of your application.  The
     * parameter will determine how the API will work. Use the following values:
     *
     * <b>usb</b>: When the usb keyword is used, the API will work with
     * devices connected directly to the USB bus. Some programming languages such a JavaScript,
     * PHP, and Java don't provide direct access to USB hardware, so usb will
     * not work with these. In this case, use a VirtualHub or a networked YoctoHub (see below).
     *
     * <b><i>x.x.x.x</i></b> or <b><i>hostname</i></b>: The API will use the devices connected to the
     * host with the given IP address or hostname. That host can be a regular computer
     * running a <i>native VirtualHub</i>, a <i>VirtualHub for web</i> hosted on a server,
     * or a networked YoctoHub such as YoctoHub-Ethernet or
     * YoctoHub-Wireless. If you want to use the VirtualHub running on you local
     * computer, use the IP address 127.0.0.1. If the given IP is unresponsive, yRegisterHub
     * will not return until a time-out defined by ySetNetworkTimeout has elapsed.
     * However, it is possible to preventively test a connection  with yTestHub.
     * If you cannot afford a network time-out, you can use the non-blocking yPregisterHub
     * function that will establish the connection as soon as it is available.
     *
     *
     * <b>callback</b>: that keyword make the API run in "<i>HTTP Callback</i>" mode.
     * This a special mode allowing to take control of Yoctopuce devices
     * through a NAT filter when using a VirtualHub or a networked YoctoHub. You only
     * need to configure your hub to call your server script on a regular basis.
     * This mode is currently available for PHP and Node.JS only.
     *
     * Be aware that only one application can use direct USB access at a
     * given time on a machine. Multiple access would cause conflicts
     * while trying to access the USB modules. In particular, this means
     * that you must stop the VirtualHub software before starting
     * an application that uses direct USB access. The workaround
     * for this limitation is to set up the library to use the VirtualHub
     * rather than direct USB access.
     *
     * If access control has been activated on the hub, virtual or not, you want to
     * reach, the URL parameter should look like:
     *
     * http://username:password@address:port
     *
     * You can call <i>RegisterHub</i> several times to connect to several machines. On
     * the other hand, it is useless and even counterproductive to call <i>RegisterHub</i>
     * with to same address multiple times during the life of the application.
     *
     * @param url : a string containing either "usb","callback" or the
     *         root URL of the hub to monitor
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * On failure returns a negative error code.
     */
    public int RegisterHub(String url) throws YAPI_Exception
    {
        _AddNewHub(url, true, null, null, null);
        try {
            // Register device list
            _updateDeviceList_internal(true, false);
        } catch (YAPI_Exception ex) {
            // remove hub from registred hub list
            unregisterHubEx(url, null, null, null);
            throw ex;
        }
        return YAPI.SUCCESS;
    }


    public int RegisterHub(String url, InputStream request, OutputStream response) throws YAPI_Exception
    {
        _AddNewHub(url, true, request, response, null);
        try {
            // Register device list
            _updateDeviceList_internal(true, false);
        } catch (YAPI_Exception ex) {
            // remove hub from registred hub list
            unregisterHubEx(url, request, response, null);
            throw ex;
        }
        return YAPI.SUCCESS;
    }

    /**
     *
     */
    public int RegisterHubHTTPCallback(InputStream request, OutputStream response) throws YAPI_Exception
    {
        _AddNewHub("http://callback", true, request, response, null);
        // Register device list
        _updateDeviceList_internal(true, false);
        return YAPI.SUCCESS;
    }


    /**
     *
     */
    public int PreregisterHubWebSocketCallback(Object session) throws YAPI_Exception
    {
        return PreregisterHubWebSocketCallback(session, null, null);
    }

    /**
     *
     */
    public int PreregisterHubWebSocketCallback(Object session, String user, String pass) throws YAPI_Exception
    {
        if (user == null) {
            user = "";
        }
        if (pass != null) {
            user += ":" + pass;
        }
        String url = "ws://" + user + "@callback";
        _AddNewHub(url, true, null, null, session);
        return YAPI.SUCCESS;
    }


    /**
     *
     */
    public void UnregisterHubWebSocketCallback(Object session)
    {
        unregisterHubEx("ws://callback", null, null, session);
    }


    /**
     * This function is used only on Android. Before calling yRegisterHub("usb")
     * you need to activate the USB host port of the system. This function takes as argument,
     * an object of class android.content.Context (or any subclass).
     * It is not necessary to call this function to reach modules through the network.
     *
     * @param osContext : an object of class android.content.Context (or any subclass).
     *
     * @throws YAPI_Exception on error
     */
    public void EnableUSBHost(Object osContext) throws YAPI_Exception
    {
        YUSBHub.SetContextType(osContext);
    }

    /**
     * Fault-tolerant alternative to yRegisterHub(). This function has the same
     * purpose and same arguments as yRegisterHub(), but does not trigger
     * an error when the selected hub is not available at the time of the function call.
     * If the connexion cannot be established immediately, a background task will automatically
     * perform periodic retries. This makes it possible to register a network hub independently of the current
     * connectivity, and to try to contact it only when a device is actively needed.
     *
     * @param url : a string containing either "usb","callback" or the
     *         root URL of the hub to monitor
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * On failure returns a negative error code.
     */
    public int PreregisterHub(String url) throws YAPI_Exception
    {
        _AddNewHub(url, false, null, null, null);
        return YAPI.SUCCESS;
    }

    /**
     * Set up the Yoctopuce library to no more use modules connected on a previously
     * registered machine with RegisterHub.
     *
     * @param url : a string containing either "usb" or the
     *         root URL of the hub to monitor
     */
    public void UnregisterHub(String url)
    {
        if (url.equals("net")) {
            _apiMode &= ~YAPI.DETECT_NET;
            return;
        }
        unregisterHubEx(url, null, null, null);
    }

    @SuppressWarnings("SameParameterValue")
    private void unregisterHubEx(String url, InputStream request, OutputStream response, Object session)
    {
        Iterator<YGenericHub> it = _hubs.iterator();
        while (it.hasNext()) {
            YGenericHub h = it.next();
            if (h.isSameHub(url, request, response, session)) {
                h.addKnownURL(url);
                h.stopNotifications();
                h.release();
                it.remove();
            }
        }
    }


    /**
     * Test if the hub is reachable. This method do not register the hub, it only test if the
     * hub is usable. The url parameter follow the same convention as the yRegisterHub
     * method. This method is useful to verify the authentication parameters for a hub. It
     * is possible to force this method to return after mstimeout milliseconds.
     *
     * @param url : a string containing either "usb","callback" or the
     *         root URL of the hub to monitor
     * @param mstimeout : the number of millisecond available to test the connection.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * On failure returns a negative error code.
     */
    public int TestHub(String url, int mstimeout) throws YAPI_Exception
    {
        if (url.equals("net")) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Invalid URL");
        }
        YGenericHub newhub;
        YGenericHub.HTTPParams parsedurl = new YGenericHub.HTTPParams(url);
        // Add hub to known list
        if (url.equals("usb")) {
            YUSBHub.CheckUSBAcces();
            newhub = new YUSBHub(this, true, _pktAckDelay);
        } else if (parsedurl.getHost().equals("callback")) {
            // fixme add TestHub function  for callback
            newhub = new YCallbackHub(this, parsedurl, null, null);
        } else {
            newhub = new YHTTPHub(this, parsedurl, true, null);
        }
        newhub.set_networkTimeout(mstimeout);
        return newhub.ping(mstimeout);
    }


    /**
     * Triggers a (re)detection of connected Yoctopuce modules.
     * The library searches the machines or USB ports previously registered using
     * yRegisterHub(), and invokes any user-defined callback function
     * in case a change in the list of connected devices is detected.
     *
     * This function can be called as frequently as desired to refresh the device list
     * and to make the application aware of hot-plug events. However, since device
     * detection is quite a heavy process, UpdateDeviceList shouldn't be called more
     * than once every two seconds.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * On failure returns a negative error code.
     */
    public int UpdateDeviceList() throws YAPI_Exception
    {
        _updateDeviceList_internal(false, true);
        return YAPI.SUCCESS;
    }

    /**
     * Maintains the device-to-library communication channel.
     * If your program includes significant loops, you may want to include
     * a call to this function to make sure that the library takes care of
     * the information pushed by the modules on the communication channels.
     * This is not strictly necessary, but it may improve the reactivity
     * of the library for the following commands.
     *
     * This function may signal an error in case there is a communication problem
     * while contacting a module.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * On failure returns a negative error code.
     */
    public int HandleEvents() throws YAPI_Exception
    {
        // handle pending events
        while (true) {
            DataEvent pv;
            synchronized (_data_events) {
                if (_data_events.isEmpty()) {
                    break;
                }
                pv = _data_events.poll();
            }
            if (pv != null) {
                pv.invoke();
            }
        }
        return YAPI.SUCCESS;
    }

    /**
     * Pauses the execution flow for a specified duration.
     * This function implements a passive waiting loop, meaning that it does not
     * consume CPU cycles significantly. The processor is left available for
     * other threads and processes. During the pause, the library nevertheless
     * reads from time to time information from the Yoctopuce modules by
     * calling yHandleEvents(), in order to stay up-to-date.
     *
     * This function may signal an error in case there is a communication problem
     * while contacting a module.
     *
     * @param ms_duration : an integer corresponding to the duration of the pause,
     *         in milliseconds.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *
     * On failure returns a negative error code.
     */
    public int Sleep(long ms_duration) throws YAPI_Exception
    {
        long end = GetTickCount() + ms_duration;

        do {
            HandleEvents();
            if (end > GetTickCount()) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    throw new YAPI_Exception(YAPI.IO_ERROR,
                            "Thread has been interrupted");
                }
            }
        } while (end > GetTickCount());
        return YAPI.SUCCESS;
    }

    /**
     * Force a hub discovery, if a callback as been registered with yRegisterHubDiscoveryCallback it
     * will be called for each net work hub that will respond to the discovery.
     *
     * @return YAPI.SUCCESS when the call succeeds.
     *         On failure returns a negative error code.
     */
    public int TriggerHubDiscovery() throws YAPI_Exception
    {
        // Register device list
        _ssdp.addCallback(_ssdpCallback);
        return YAPI.SUCCESS;
    }

    /**
     * Returns the current value of a monotone millisecond-based time counter.
     * This counter can be used to compute delays in relation with
     * Yoctopuce devices, which also uses the millisecond as timebase.
     *
     * @return a long integer corresponding to the millisecond counter.
     */
    public static long GetTickCount()
    {
        return System.currentTimeMillis();
    }

    /**
     * Checks if a given string is valid as logical name for a module or a function.
     * A valid logical name has a maximum of 19 characters, all among
     * A...Z, a...z, 0...9, _, and -.
     * If you try to configure a logical name with an incorrect string,
     * the invalid characters are ignored.
     *
     * @param name : a string containing the name to check.
     *
     * @return true if the name is valid, false otherwise.
     */
    public boolean CheckLogicalName(String name)
    {
        return YAPI.CheckLogicalName(name);
    }

    /**
     * Register a callback function, to be called each time
     * a device is plugged. This callback will be invoked while yUpdateDeviceList
     * is running. You will have to call this function on a regular basis.
     *
     * @param arrivalCallback : a procedure taking a YModule parameter, or null
     *         to unregister a previously registered  callback.
     */
    public void RegisterDeviceArrivalCallback(YAPI.DeviceArrivalCallback arrivalCallback)
    {
        synchronized (_regCbLock) {
            _arrivalCallback = arrivalCallback;
        }
    }

    public void RegisterDeviceChangeCallback(YAPI.DeviceChangeCallback changeCallback)
    {
        synchronized (_regCbLock) {
            _namechgCallback = changeCallback;
        }
    }

    /**
     * Register a callback function, to be called each time
     * a device is unplugged. This callback will be invoked while yUpdateDeviceList
     * is running. You will have to call this function on a regular basis.
     *
     * @param removalCallback : a procedure taking a YModule parameter, or null
     *         to unregister a previously registered  callback.
     */
    public void RegisterDeviceRemovalCallback(YAPI.DeviceRemovalCallback removalCallback)
    {
        synchronized (_regCbLock) {
            _removalCallback = removalCallback;
        }
    }

    /**
     * Register a callback function, to be called each time an Network Hub send
     * an SSDP message. The callback has two string parameter, the first one
     * contain the serial number of the hub and the second contain the URL of the
     * network hub (this URL can be passed to RegisterHub). This callback will be invoked
     * while yUpdateDeviceList is running. You will have to call this function on a regular basis.
     *
     * @param hubDiscoveryCallback : a procedure taking two string parameter, the serial
     *         number and the hub URL. Use null to unregister a previously registered  callback.
     */
    public void RegisterHubDiscoveryCallback(YAPI.HubDiscoveryCallback hubDiscoveryCallback)
    {
        synchronized (_newHubCallbackLock) {
            _HubDiscoveryCallback = hubDiscoveryCallback;
        }
        try {
            TriggerHubDiscovery();
        } catch (YAPI_Exception ignore) {
        }
    }

    /**
     * Registers a log callback function. This callback will be called each time
     * the API have something to say. Quite useful to debug the API.
     *
     * @param logfun : a procedure taking a string parameter, or null
     *         to unregister a previously registered  callback.
     */
    public void RegisterLogFunction(YAPI.LogCallback logfun)
    {
        synchronized (_logCallbackLock) {
            _logCallback = logfun;
        }
    }

    ArrayList<String> getAllBootLoaders()
    {
        ArrayList<String> res = new ArrayList<>();
        for (YGenericHub h : _hubs) {
            try {
                ArrayList<String> bootloaders;
                try {
                    bootloaders = h.getBootloaders();
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.IO_ERROR,
                            "Thread has been interrupted");
                }
                if (bootloaders != null) {
                    res.addAll(bootloaders);
                }
            } catch (YAPI_Exception e) {
                this._Log(e.getLocalizedMessage());
            }
        }
        return res;
    }

    public YGenericHub getHubWithBootloader(String serial) throws YAPI_Exception, InterruptedException
    {
        for (YGenericHub h : _hubs) {
            ArrayList<String> bootloaders = h.getBootloaders();
            if (bootloaders.contains(serial)) {
                return h;
            }
        }
        return null;
    }

}
