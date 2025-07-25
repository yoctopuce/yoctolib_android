/*********************************************************************
 * $Id: YGenericHub.java 67272 2025-06-04 10:18:51Z seb $
 *
 * Internal YGenericHub object
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


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;


abstract class YGenericHub
{

    static final int NOTIFY_V2_LEGACY = 0;       // unused (reserved for compatibility with legacy notifications)
    static final int NOTIFY_V2_6RAWBYTES = 1;       // largest type: data is always 6 bytes
    static final int NOTIFY_V2_TYPEDDATA = 2;       // other types: first data byte holds the decoding format
    static final int NOTIFY_V2_FLUSHGROUP = 3;       // no data associated

    // stream type
    static final int YSTREAM_EMPTY = 0;
    static final int YSTREAM_TCP = 1;
    static final int YSTREAM_TCP_CLOSE = 2;
    static final int YSTREAM_NOTICE = 3;
    static final int YSTREAM_REPORT = 4;
    static final int YSTREAM_META = 5;
    static final int YSTREAM_REPORT_V2 = 6;
    static final int YSTREAM_NOTICE_V2 = 7;
    static final int YSTREAM_TCP_NOTIF = 8;
    static final int YSTREAM_TCP_ASYNCCLOSE = 9;


    static final int USB_META_UTCTIME = 1;
    static final int USB_META_DLFLUSH = 2;
    static final int USB_META_ACK_D2H_PACKET = 3;
    static final int USB_META_WS_ANNOUNCE = 4;
    static final int USB_META_WS_AUTHENTICATION = 5;
    static final int USB_META_WS_ERROR = 6;
    static final int USB_META_ACK_UPLOAD = 7;

    static final int USB_META_UTCTIME_SIZE = 6;
    static final int USB_META_DLFLUSH_SIZE = 1;
    static final int USB_META_ACK_D2H_PACKET_SIZE = 2;
    static final int USB_META_WS_ANNOUNCE_SIZE = 8 + YAPI.YOCTO_SERIAL_LEN;
    static final int USB_META_WS_AUTHENTICATION_SIZE = 28;
    static final int USB_META_WS_ERROR_SIZE = 6;
    static final int USB_META_ACK_UPLOAD_SIZE = 6;

    static final int USB_META_WS_PROTO_V1 = 1;  // adding authentication support
    static final int USB_META_WS_PROTO_V2 = 2;  // adding API packets throttling
    static final int VERSION_SUPPORT_ASYNC_CLOSE = 1;


    static final int USB_META_WS_VALID_SHA1 = 1;
    static final int USB_META_WS_AUTH_FLAGS_RW = 2;


    private static final int PUBVAL_LEGACY = 0;   // 0-6 ASCII characters (normally sent as YSTREAM_NOTICE)
    private static final int PUBVAL_1RAWBYTE = 1;   // 1 raw byte  (=2 characters)
    private static final int PUBVAL_2RAWBYTES = 2;   // 2 raw bytes (=4 characters)
    private static final int PUBVAL_3RAWBYTES = 3;   // 3 raw bytes (=6 characters)
    private static final int PUBVAL_4RAWBYTES = 4;   // 4 raw bytes (=8 characters)
    private static final int PUBVAL_5RAWBYTES = 5;   // 5 raw bytes (=10 characters)
    private static final int PUBVAL_6RAWBYTES = 6;   // 6 hex bytes (=12 characters) (sent as V2_6RAWBYTES)
    private static final int PUBVAL_C_LONG = 7;   // 32-bit C signed integer
    private static final int PUBVAL_C_FLOAT = 8;   // 32-bit C float
    private static final int PUBVAL_YOCTO_FLOAT_E3 = 9;   // 32-bit Yocto fixed-point format (e-3)
    private static final int PUBVAL_YOCTO_FLOAT_E6 = 10;   // 32-bit Yocto fixed-point format (e-6)

    static final long YPROG_BOOTLOADER_TIMEOUT = 20000;
    final YAPIContext _yctx;
    /**
     * The URL used for the creation of the GenericHub objet
     */
    final HTTPParams _URL_params;
    protected long _notifyTrigger = 0;
    protected Object _notifyHandle = null;
    volatile boolean _isNotifWorking = false;
    long _devListExpires = 0;
    final ConcurrentHashMap<Integer, String> _serialByYdx = new ConcurrentHashMap<>();
    private HashMap<String, YDevice> _devices = new HashMap<>();
    boolean _reportConnnectionLost;
    protected String _hubSerialNumber = null;
    private HashMap<String, Integer> _beaconss = new HashMap<>();
    protected ArrayList<String> _knownUrls = new ArrayList<>();

    protected String _lastErrorMessage = "";
    protected int _lastErrorType = YAPI.SUCCESS;
    protected int _networkTimeoutMs;
    private boolean _enabled = true;
    private final long _creation_time;
    private static int _global_hub_id = 0;
    private final int _hubid;

    YGenericHub(YAPIContext yctx, HTTPParams httpParams, boolean reportConnnectionLost)
    {
        _yctx = yctx;
        _networkTimeoutMs = _yctx._networkTimeoutMs;
        _reportConnnectionLost = reportConnnectionLost;
        _URL_params = httpParams;
        _knownUrls.add(httpParams._originalURL);
        _creation_time = System.currentTimeMillis();
        _hubid = _global_hub_id++;
    }

    abstract void release();

    abstract String getRootUrl();

    @SuppressWarnings("UnusedParameters")
    boolean isSameHub(String url, Object request, Object response, Object session)
    {
        for (String ku : _knownUrls) {
            if (url.equals(ku)) {
                return true;
            }
        }
        HTTPParams params = new HTTPParams(url);
        String paramsUrl = params.getUrl(false, false, false);
        return paramsUrl.equals(_URL_params.getUrl(false, false, false));
    }

    abstract void startNotifications() throws YAPI_Exception;

    abstract void stopNotifications();

    boolean updateHubSerial(String serial) throws YAPI_Exception
    {
        if (_hubSerialNumber == null) {
            _hubSerialNumber = serial;
            return _yctx._checkForDuplicateHub(this);
        }
        return false;
    }


    static String decodePubVal(int typeV2, byte[] funcval, int ofs, int funcvallen)
    {
        String buffer = "";

        if (typeV2 == NOTIFY_V2_6RAWBYTES || typeV2 == NOTIFY_V2_TYPEDDATA) {
            int funcValType;

            if (typeV2 == NOTIFY_V2_6RAWBYTES) {
                funcValType = PUBVAL_6RAWBYTES;
            } else {
                funcValType = funcval[ofs++] & 0xff;
            }
            switch (funcValType) {
                case PUBVAL_LEGACY:
                    // fallback to legacy handling, just in case
                    break;
                case PUBVAL_1RAWBYTE:
                case PUBVAL_2RAWBYTES:
                case PUBVAL_3RAWBYTES:
                case PUBVAL_4RAWBYTES:
                case PUBVAL_5RAWBYTES:
                case PUBVAL_6RAWBYTES:
                    // 1..5 hex bytes
                    for (int i = 0; i < funcValType; i++) {
                        int c = funcval[ofs++] & 0xff;
                        int b = c >> 4;
                        buffer += (b > 9) ? b + 'a' - 10 : b + '0';
                        b = c & 0xf;
                        buffer += (b > 9) ? b + 'a' - 10 : b + '0';
                    }
                    return buffer;
                case PUBVAL_C_LONG:
                case PUBVAL_YOCTO_FLOAT_E3:
                    // 32bit integer in little endian format or Yoctopuce 10-3 format
                    int numVal = funcval[ofs++] & 0xff;
                    numVal += (int) (funcval[ofs++] & 0xff) << 8;
                    numVal += (int) (funcval[ofs++] & 0xff) << 16;
                    numVal += (int) (funcval[ofs++] & 0xff) << 24;
                    if (funcValType == PUBVAL_C_LONG) {
                        return String.format(Locale.US, "%d", numVal);
                    } else {
                        buffer = String.format(Locale.US, "%.3f", numVal / 1000.0);
                        int endp = buffer.length();
                        while (endp > 0 && buffer.charAt(endp - 1) == '0') {
                            --endp;
                        }
                        if (endp > 0 && buffer.charAt(endp - 1) == '.') {
                            --endp;
                            buffer = buffer.substring(0, endp);
                        }
                        return buffer;
                    }
                case PUBVAL_C_FLOAT:
                    // 32bit (short) float
                    float floatVal = ByteBuffer.wrap(funcval).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    buffer = String.format(Locale.US, "%.6f", floatVal);
                    int endp = buffer.length();
                    while (endp > 0 && buffer.charAt(endp - 1) == '0') {
                        --endp;
                    }
                    if (endp > 0 && buffer.charAt(endp - 1) == '.') {
                        --endp;
                        buffer = buffer.substring(0, endp);
                    }
                    return buffer;
                default:
                    return "?";
            }
        }

        // Legacy handling: just pad with NUL up to 7 chars
        int len = 0;
        while (len < YAPI.YOCTO_PUBVAL_SIZE && len < funcvallen) {
            if (funcval[len + ofs] == 0)
                break;
            len++;
        }
        return new String(funcval, ofs, len);
    }

    void removeAllDevices()
    {
        HashMap<String, ArrayList<YPEntry>> yellowPages = new HashMap<>();
        ArrayList<WPEntry> whitePages = new ArrayList<>();
        try {
            updateFromWpAndYp(whitePages, yellowPages);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    void updateFromWpAndYp(ArrayList<WPEntry> whitePages, HashMap<String, ArrayList<YPEntry>> yellowPages) throws YAPI_Exception
    {

        // by default consider all known device as unplugged
        ArrayList<YDevice> toRemove = new ArrayList<>(_devices.values());

        for (WPEntry wp : whitePages) {
            String serial = wp.getSerialNumber();
            if (_devices.containsKey(serial)) {
                // already there
                YDevice currdev = _devices.get(serial);
                if (!currdev.getLogicalName().equals(wp.getLogicalName())) {
                    // Reindex device from its own data
                    currdev.refresh();
                    _yctx._pushChangeEvent(serial);
                } else if (currdev.getBeacon() > 0 != wp.getBeacon() > 0) {
                    currdev.refresh();
                }
                toRemove.remove(currdev);
            } else {
                YDevice dev = new YDevice(this, wp, yellowPages);
                _yctx._yHash.reindexDevice(dev);
                _devices.put(serial, dev);
                _yctx._pushPlugEvent(serial, wp.getProductName(), wp.getProductId());
                _yctx._Log("HUB: device " + serial + " has been plugged\n");
            }
        }

        for (YDevice dev : toRemove) {
            String serial = dev.getSerialNumber();
            _yctx._pushUnPlugEvent(serial);
            _yctx._Log("HUB: device " + serial + " has been unplugged\n");
            _devices.remove(serial);
            _yctx._yHash.forgetDevice(serial);
        }
        synchronized (this) {
            if (_hubSerialNumber == null) {
                for (WPEntry wp : whitePages) {
                    if (wp.getNetworkUrl().equals("")) {
                        _hubSerialNumber = wp.getSerialNumber();
                    }
                }
            }
        }
        _yctx._yHash.reindexYellowPages(yellowPages);

    }

    String getSerialNumber()
    {
        return _hubSerialNumber;
    }

    public String get_urlOf(String serialNumber)
    {
        for (YDevice dev : _devices.values()) {
            String devSerialNumber = dev.getSerialNumber();
            if (devSerialNumber.equals(serialNumber)) {
                return _URL_params.getUrl(true, false, false) + dev.getNetworkUrl() + '/';
            }
        }
        return _URL_params.getUrl(true, false, true);
    }

    public ArrayList<String> get_subDeviceOf(String serialNumber)
    {
        ArrayList<String> res = new ArrayList<>();
        for (YDevice dev : _devices.values()) {
            String devSerialNumber = dev.getSerialNumber();
            if (devSerialNumber.equals(serialNumber)) {
                if (!dev.getNetworkUrl().equals("")) {
                    //
                    res.clear();
                    return res;
                } else {
                    continue;
                }
            }
            res.add(devSerialNumber);
        }
        return res;
    }

    void handleValueNotification(String serial, String funcid, String value)
    {
        String hwid = serial + "." + funcid;

        _yctx._yHash.setFunctionValue(hwid, value);
        YFunction conn_fn = _yctx._GetValueCallback(hwid);
        if (conn_fn != null) {
            _yctx._PushDataEvent(new YAPIContext.DataEvent(conn_fn, value));
        }

    }

    void handleConfigChangeNotification(String serial)
    {
        YModule module = _yctx._GetModuleCallack(serial);
        if (module != null) {
            _yctx._PushDataEvent(new YAPIContext.DataEvent(module));
        }
    }

    void handleBeaconNotification(String serial, String logicalName, int beacon)
    {
        if (!_beaconss.containsKey(serial) || _beaconss.get(serial) != beacon) {
            _beaconss.put(serial, beacon);

            YModule module = _yctx._GetModuleCallack(serial);
            if (module != null) {
                _yctx._PushDataEvent(new YAPIContext.DataEvent(module, beacon));
            }
        }
    }


    //called from Jni
    protected void handleTimedNotification(String serial, String funcid, double time, double duration, byte[] report)
    {
        ArrayList<Integer> arrayList = new ArrayList<>(report.length);
        for (byte b : report) {
            int i = b & 0xff;
            arrayList.add(i);
        }
        handleTimedNotification(serial, funcid, time, duration, arrayList);
    }


    void handleTimedNotification(String serial, String funcid, double time, double duration, ArrayList<Integer> report)
    {
        String hwid = serial + "." + funcid;
        YFunction func = _yctx._GetTimedReportCallback(hwid);
        if (func != null) {
            _yctx._PushDataEvent(new YAPIContext.DataEvent(func, time, duration, report));
        }
    }

    abstract void updateDeviceList(boolean forceupdate) throws YAPI_Exception, InterruptedException;

    public abstract ArrayList<String> getBootloaders() throws YAPI_Exception, InterruptedException;

    abstract int ping(int mstimeout) throws YAPI_Exception;

    public static String getAPIVersion()
    {
        return "";
    }

    abstract boolean isCallbackMode();

    abstract boolean isReadOnly();


    public void set_networkTimeout(int networkMsTimeout)
    {
        _networkTimeoutMs = networkMsTimeout;
    }

    public int get_networkTimeout()
    {
        return _networkTimeoutMs;
    }

    public String getLastErrorMessage()
    {
        return _lastErrorMessage;
    }

    public int getLastErrorType()
    {
        return _lastErrorType;
    }

    abstract public boolean isOnline();

    synchronized public void merge(YGenericHub newhub)
    {
        this.addKnownURL(newhub._URL_params._originalURL);
        if (_creation_time < newhub._creation_time) {
            _reportConnnectionLost = newhub._reportConnnectionLost;
        }
    }

    public void disable()
    {
        _enabled = false;
    }

    public boolean isEnabled()
    {
        return _enabled;
    }

    synchronized public void addKnownURL(String url)
    {
        if (!_knownUrls.contains(url)) {
            _knownUrls.add(url);
        }
    }

    public void requestStop()
    {
    }

    public abstract String getConnectionUrl();

    public int get_hubid()
    {
        return _hubid;
    }

    interface UpdateProgress
    {
        void firmware_progress(int percent, String message);
    }

    abstract ArrayList<String> firmwareUpdate(String serial, YFirmwareFile firmware, byte[] settings, UpdateProgress progress) throws YAPI_Exception, InterruptedException;

    interface RequestAsyncResult
    {
        @SuppressWarnings("UnusedParameters")
        void RequestAsyncDone(Object context, byte[] result, int error, String errmsg);
    }

    interface RequestProgress
    {
        void requestProgressUpdate(Object context, int acked, int total);
    }

    abstract void devRequestAsync(YDevice device, String req_first_line, byte[] req_head_and_body, RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception, InterruptedException;

    abstract byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body, RequestProgress progress, Object context) throws YAPI_Exception, InterruptedException;

    private final BlockingQueue<YDevice> _logPullList = new LinkedBlockingDeque<>();

    void addDevForLogPull(YDevice yDevice)
    {
        _logPullList.offer(yDevice);
    }

    void testLogPull() throws InterruptedException
    {
        YDevice device = _logPullList.poll();
        if (device == null) {
            return;
        }
        try {
            String logRequest = device.getLogRequest();
            if (logRequest == null) {
                return;
            }
            RequestAsyncResult logCallbackHandler = device.getLogCallbackHandler();
            this.devRequestAsync(device, logRequest, null, logCallbackHandler, null);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
    }

    static class HTTPParams
    {

        private String _host;
        private int _port;
        private String _user;
        private String _pass;
        private String _proto;
        private final String _subDomain;
        private final String _originalURL;

        public String getOriginalURL()
        {
            return _originalURL;
        }

        public HTTPParams(HTTPParams org)
        {
            this._host = org._host;
            this._port = org._port;
            this._user = org._user;
            this._pass = org._pass;
            this._proto = org._proto;
            this._subDomain = org._subDomain;
            this._originalURL = org._originalURL;
        }

        public HTTPParams(String url)
        {
            int defaultPort = YAPI.YOCTO_DEFAULT_PORT;
            _originalURL = url;
            int pos = 0;
            if (url.startsWith("auto://")) {
                pos = 7;
                _proto = "auto";
            } else if (url.startsWith("secure://")) {
                pos = 9;
                _proto = "secure";
                defaultPort = YAPI.YOCTO_DEFAULT_HTTPS_PORT;
            } else if (url.startsWith("http://")) {
                pos = 7;
                _proto = "http";
            } else if (url.startsWith("https://")) {
                pos = 8;
                _proto = "https";
                defaultPort = YAPI.YOCTO_DEFAULT_HTTPS_PORT;
            } else if (url.startsWith("wss://")) {
                pos = 6;
                _proto = "wss";
                defaultPort = YAPI.YOCTO_DEFAULT_HTTPS_PORT;
            } else if (url.equals("usb")) {
                _proto = "usb";
                _user = "";
                _pass = "";
                _subDomain = "";
                _host = "";
                _port = -1;
                return;
            } else if (url.startsWith("ws://")) {
                pos = 5;
                _proto = "ws";
            } else {
                _proto = "auto";
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            int end_auth = url.indexOf('@', pos);
            if (end_auth > 0) {
                int end_user = url.indexOf(':', pos);
                if (end_user >= 0 && end_user < end_auth) {
                    _user = url.substring(pos, end_user);
                    _pass = url.substring(end_user + 1, end_auth);
                } else {
                    _user = url.substring(pos, end_auth);
                    _pass = "";
                }
                pos = end_auth + 1;
            } else {
                _user = "";
                _pass = "";
            }
            if (url.length() > pos && url.charAt(pos) == '@') {
                pos++;
            }
            int end_host = url.indexOf('/', pos);
            if (end_host < 0) {
                end_host = url.length();
                _subDomain = "";
            } else {
                _subDomain = url.substring(end_host);
            }
            int endv6 = url.indexOf(']', pos);
            int portpos = url.indexOf(':', pos);
            if (portpos > 0 && endv6 < end_host && portpos < endv6) {
                // ipv6 URL
                portpos = url.indexOf(':', endv6);
            }

            if (portpos > 0) {
                if (portpos + 1 < end_host) {
                    _host = url.substring(pos, portpos);
                    _port = Integer.parseInt(url.substring(portpos + 1, end_host));
                } else {
                    _host = url.substring(pos, portpos);
                    _port = defaultPort;
                }
            } else {
                _host = url.substring(pos, end_host);
                if (_subDomain.length() > 0) {
                    // override default port if there is a subdomain (VHub4web)
                    if (_proto.equals("http")) {
                        defaultPort = 80;
                    } else if (_proto.equals("https")) {
                        defaultPort = 443;
                    }
                }
                _port = defaultPort;
            }
        }

        public HTTPParams(HTTPParams http_params_org, String proto, int port)
        {
            _originalURL = http_params_org._originalURL;
            _host = http_params_org._host;
            _port = port;
            _user = http_params_org._user;
            _pass = http_params_org._pass;
            if (!http_params_org.getProto().equals("http") && !http_params_org.getProto().equals("https")) {
                _proto = proto;
            } else {
                _proto = http_params_org.getProto();
            }
            _subDomain = http_params_org._subDomain;
        }

        String getHost()
        {
            return _host;
        }

        String getPass()
        {
            return _pass;
        }

        int getPort()
        {
            return _port;
        }

        String getUser()
        {
            return _user;
        }

        String getUrl()
        {
            return getUrl(false, true, false);
        }

        String getUrl(boolean withProto, boolean withUserPass, boolean withEndSlash)
        {
            if (_proto.equals("usb")) {
                return "usb";
            }
            StringBuilder url = new StringBuilder();
            if (withProto) {
                url.append(_proto).append("://");
            }
            if (withUserPass && !_user.equals("")) {
                url.append(_user);
                if (!_pass.equals("")) {
                    url.append(":");
                    url.append(_pass);
                }
                url.append("@");
            }
            url.append(_host);
            url.append(":");
            url.append(_port);
            url.append(_subDomain);
            if (withEndSlash) {
                url.append('/');
            }
            return url.toString();
        }

        public String getProto()
        {
            return _proto;
        }

        boolean useWebSocket()
        {
            return _proto.startsWith("ws");
        }

        /**
         * @return subdomain (starting with a /
         */
        String getSubDomain()
        {
            return _subDomain;
        }

        boolean hasAuthParam()
        {
            return !_user.equals("");
        }

        boolean useSecureSocket()
        {
            return "wss".equals(_proto) || "https".equals(_proto) || "secure".equals(_proto);
        }

        @Override
        public String toString()
        {
            return _proto + "://" + _host + ':' + _port + _subDomain;
        }

        public boolean testInfoJson()
        {
            return _proto.equals("auto") || _proto.equals("secure") || _proto.equals("http") || _proto.equals("https");
        }

        public void updateForRedirect(String host, int port, boolean is_secure)
        {
            _host = host;
            _port = port;
            if (useWebSocket()) {
                _proto = is_secure ? "wss" : "ws";
            } else {
                _proto = is_secure ? "https" : "http";
            }
        }

        public void updateAuth(String user, String pass)
        {
            _user = user;
            _pass = pass;
        }

    }
}
