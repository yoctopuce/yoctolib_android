/**
 * ******************************************************************
 *
 * $Id: YUSBDevice.java 28106 2017-07-24 13:10:56Z seb $
 *
 * YUSBDevice Class:
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
 *
 * *******************************************************************
 */
package com.yoctopuce.YoctoAPI;


import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;


class YUSBDevice implements YUSBRawDevice.IOHandler
{

    private static final long META_UTC_DELAY = 60000;
    private final String _android_dev_name;
    private int _pktAckDelay;
    private int _devVersion;
    private int _retry = 0;
    // USB communication data
    private String _serial = null;
    private String _logicalname;
    private byte _beacon;
    private String _productName;
    private int _deviceid;


    private final YUSBHub _usbHub;
    private WPEntry _wp;
    private final HashMap<String, YPEntry> _usbYP = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> _usbIdx2Funcid = new HashMap<>();

    boolean isAllowed()
    {
        return _rawDev != null && _rawDev.isUsable();
    }

    public String getSerial()
    {
        return _serial;
    }

    private enum PKT_State
    {
        Plugged,
        Authorized,
        ResetSend,
        ResetReceived,
        StartSend,
        StartReceived,
        StreamReadyReceived,
        IOError
    }


    private enum TCP_State
    {
        Closed, Opened, Close_by_dev, Close_by_API
    }


    private int _lastpktno;
    private YUSBRawDevice _rawDev;
    private long _lastMetaUTC = -1;


    private final Object _stateLock = new Object();
    private volatile PKT_State _pkt_state = PKT_State.Plugged;
    private volatile TCP_State _tcp_state = TCP_State.Closed;
    private String _ioErrorMessage = null;

    // async request related
    private YGenericHub.RequestAsyncResult _asyncResult;
    private Object _asyncContext;
    private final ByteArrayOutputStream _req_result = new ByteArrayOutputStream(1024);
    private long _currentRequestTimeout;


    private YPEntry getYPEntryFromYdx(int funIdx)
    {
        String functionId = _usbIdx2Funcid.get(funIdx);
        if (functionId == null)
            return null;
        if (_usbYP.containsKey(functionId)) {
            return _usbYP.get(functionId);
        }
        return null;
    }


    boolean waitEndOfInit()
    {
        boolean ready = false;
        while (!ready && _retry < 5) {

            try {
                waitForTcpState(PKT_State.StreamReadyReceived, null, 1000, "Device not ready");
                _retry = 0;
                ready = true;
            } catch (YAPI_Exception e) {
                e.printStackTrace();
                _retry++;
                sendConfReset();
            }
        }
        return ready;
    }


    YUSBDevice(YUSBHub yusbHub, int pktAckDelay, String android_dev_name)
    {
        _usbHub = yusbHub;
        _pktAckDelay = pktAckDelay;
        _android_dev_name = android_dev_name;

    }


    private void setNewState(PKT_State new_pkt_state, TCP_State new_tcp_State)
    {
        synchronized (_stateLock) {
            _pkt_state = new_pkt_state;
            if (new_tcp_State != null) {
                _tcp_state = TCP_State.Closed;
            }
            _ioErrorMessage = null;
            _stateLock.notify();
        }
    }

    private boolean testState(PKT_State pkt_state, TCP_State tcp_State)
    {
        synchronized (_stateLock) {
            if (_pkt_state != pkt_state) {
                return false;
            }
            if (tcp_State != null && _tcp_state != tcp_State)
                return false;
        }
        return true;
    }


    private void remoteClose()
    {
        synchronized (_stateLock) {
            switch (_tcp_state) {
                case Close_by_API:
                    _tcp_state = TCP_State.Closed;
                    _stateLock.notify();
                    break;
                case Close_by_dev:
                case Closed:
                    _usbHub._yctx._Log("Drop unexpected close from device\n");
                    break;
                case Opened:
                    _tcp_state = TCP_State.Close_by_dev;
                    _stateLock.notify();
                    break;
            }

        }
    }

    private void waitForTcpState(PKT_State wanted, PKT_State next, long mswait, String message) throws YAPI_Exception
    {
        long timeout = YAPI.GetTickCount() + mswait;
        synchronized (_stateLock) {
            while (_pkt_state != PKT_State.IOError && _pkt_state != wanted && timeout > YAPI.GetTickCount()) {
                long millis = timeout - YAPI.GetTickCount();
                try {
                    _stateLock.wait(millis);
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.TIMEOUT, "Device " + _serial + " " + message, e);
                }
            }
            if (_pkt_state == PKT_State.IOError) {
                throw new YAPI_Exception(YAPI.IO_ERROR, "Device " + _serial + " " + _ioErrorMessage);
            }
            if (_pkt_state != wanted) {
                throw new YAPI_Exception(YAPI.TIMEOUT, "Device " + _serial + " " + message + " (" + _pkt_state + ")");
            }
            if (next != null) {
                _pkt_state = next;
                _stateLock.notify();
            }
        }
    }

    private void sendConfReset()
    {
        // ensure that the device is started
        // send reset packet

        YUSBPktOut pkt = new YUSBPktOut();
        pkt.open();
        YUSBPkt.ConfPktReset reset = new YUSBPkt.ConfPktReset(YUSBPkt.YPKT_USB_VERSION_BCD, 1, 0, 0);
        pkt.push(reset);
        byte[] bytes = pkt.close();
        try {
            setNewState(PKT_State.ResetSend, TCP_State.Closed);
            _rawDev.sendPkt(bytes);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
            ioError("Unable to send reset pkt:" + e.getLocalizedMessage());
        }
    }

    private void receiveConfReset(YUSBPktIn newpkt)
    {

        if (testState(PKT_State.ResetSend, null)) {
            YUSBPkt.ConfPktReset reset = newpkt.asConfPktReset();
            _devVersion = reset.getApi();
            if (_devVersion != YUSBPkt.YPKT_USB_VERSION_BCD) {
                if ((_devVersion & 0xff00) != (YUSBPkt.YPKT_USB_VERSION_BCD & 0xff00)) {
                    // major dev_version change
                    if ((_devVersion & 0xff00) > (YUSBPkt.YPKT_USB_VERSION_BCD & 0xff00)) {
                        _usbHub._yctx._Log(String.format("Yoctopuce library is too old (using 0x%x need 0x%x) to handle device %s, please upgrade your Yoctopuce library\n", YUSBPkt.YPKT_USB_VERSION_BCD, _devVersion, _serial));
                        ioError("Library is too old to handle this device");
                    } else {
                        // implement backward compatibility when implementing a new protocol
                        ioError("implement backward compatibility when implementing a new protocol");
                    }
                    return;
                } else {
                    if (_devVersion > YUSBPkt.YPKT_USB_VERSION_BCD) {
                        _usbHub._yctx._Log(String.format("Device %s is using an newer protocol, consider upgrading your Yoctopuce library\n", _serial));
                    } else {
                        _usbHub._yctx._Log(String.format("Device %s is using an older protocol, consider upgrading the device firmware\n", _serial));
                    }
                }
            }

            YUSBPkt.ConfPktStart start = new YUSBPkt.ConfPktStart(1, _pktAckDelay);
            YUSBPktOut pkt = new YUSBPktOut();
            pkt.open();
            pkt.push(start);
            byte[] data = pkt.close();
            try {
                setNewState(PKT_State.StartSend, null);
                _rawDev.sendPkt(data);
            } catch (YAPI_Exception e) {
                e.printStackTrace();
                ioError("Unable to send start pkt:" + e.getLocalizedMessage());
            }
        } else {
            _usbHub._yctx._Log("Drop late reset packet for " + _rawDev.getUsbDevice().getDeviceName());
        }
    }

    private void ackInPkt(int pktno)
    {
        if (_pktAckDelay > 0) {
            YUSBPktOut pkt = new YUSBPktOut();
            pkt.open();
            pkt.pushPktAck(pktno);
            byte[] data = pkt.close();
            try {
                if (_rawDev != null) {
                    _rawDev.sendPkt(data);
                }
            } catch (YAPI_Exception e) {
                ioError("Unable to send start pkt:" + e.getLocalizedMessage());
            }
        }

    }

    private void receiveConfStart(YUSBPktIn newpkt)
    {
        if (testState(PKT_State.StartSend, null)) {
            YUSBPkt.ConfPktStart pktStart = newpkt.asConfPktStart();
            if (_devVersion >= YUSBPkt.YPKT_USB_VERSION_BCD) {
                _pktAckDelay = pktStart.getAckDelay();
            } else {
                _pktAckDelay = 0;
            }
            _lastpktno = newpkt.getPktno();
            setNewState(PKT_State.StartReceived, null);
        } else {
            _usbHub._yctx._Log("Drop late start packet for" + _rawDev.getUsbDevice().getDeviceName());
        }
    }

    private void setStreamReady()
    {
        synchronized (_stateLock) {
            if (_pkt_state == PKT_State.StartReceived) {
                _wp = new WPEntry(_logicalname, _productName, _deviceid, "", _beacon, _serial);
                _pkt_state = PKT_State.StreamReadyReceived;
                _stateLock.notify();
            } else {
                _usbHub._yctx._Log("Streamready to early! :" + _pkt_state);
            }
        }
    }


    /**
     * request related stuff
     */

    private void finishLastRequest(boolean andOpenNewRequest) throws YAPI_Exception
    {
        YUSBPktOut ypkt;
        synchronized (_stateLock) {
            if (_tcp_state == TCP_State.Closed) {
                // nothing to do
                if (andOpenNewRequest) {
                    _tcp_state = TCP_State.Opened;
                }
                return;
            }
            while (_pkt_state != PKT_State.IOError && _tcp_state == TCP_State.Opened && _currentRequestTimeout > YAPI.GetTickCount()) {
                try {
                    _stateLock.wait(_currentRequestTimeout - YAPI.GetTickCount());
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.TIMEOUT, "HTTP request on " + _serial + " did not finished correctly", e);
                }
            }
            if (_pkt_state == PKT_State.IOError) {
                throw new YAPI_Exception(YAPI.IO_ERROR, "IO error: " + _ioErrorMessage);
            }

            // send API close
            ypkt = new YUSBPktOut();
            ypkt.open();
            ypkt.pushTCPClose();
            byte[] data = ypkt.close();
            _rawDev.sendPkt(data);
            if (_tcp_state == TCP_State.Close_by_dev) {
                _tcp_state = TCP_State.Closed;
            } else {
                _tcp_state = TCP_State.Close_by_API;
                long timeout = YAPI.GetTickCount() + 100;
                while (_tcp_state == TCP_State.Close_by_API && timeout > YAPI.GetTickCount()) {
                    try {
                        _stateLock.wait(timeout - YAPI.GetTickCount());
                    } catch (InterruptedException e) {
                        throw new YAPI_Exception(YAPI.TIMEOUT, "HTTP request on " + _serial + " did not finished correctly", e);
                    }
                }
                if (_tcp_state != TCP_State.Closed) {
                    _usbHub._yctx._Log("USB Close without device ack\n");
                    _tcp_state = TCP_State.Closed;
                }
            }
            if (andOpenNewRequest) {
                _tcp_state = TCP_State.Opened;
            }
        }
    }

    private void sendRequest(String firstLine, byte[] rest_of_request, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception
    {
        // first ensure that last request has finished
        waitForTcpState(PKT_State.StreamReadyReceived, null, 100, "Device not ready");
        finishLastRequest(true);
        synchronized (_req_result) {
            _req_result.reset();
        }
        byte[] currentRequest;
        if (rest_of_request == null) {
            currentRequest = (firstLine + "\r\n\r\n").getBytes();
        } else {
            firstLine += "\r\n";
            int len = firstLine.length() + rest_of_request.length;
            currentRequest = new byte[len];
            System.arraycopy(firstLine.getBytes(), 0, currentRequest, 0, firstLine.length());
            System.arraycopy(rest_of_request, 0, currentRequest, firstLine.length(), rest_of_request.length);
        }
        _asyncResult = asyncResult;
        _asyncContext = asyncContext;
        _currentRequestTimeout = YAPI.GetTickCount() + 10000;// 10 sec
        YUSBPktOut ypkt = new YUSBPktOut();
        int pos = 0;
        while (pos < currentRequest.length) {
            ypkt.open();
            pos += ypkt.pushTCP(currentRequest, pos, currentRequest.length - pos);
            byte[] data = ypkt.close();
            _rawDev.sendPkt(data);
        }
    }

    synchronized void sendRequestAsync(String firstLine, byte[] rest_of_request, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception
    {
        sendRequest(firstLine, rest_of_request, asyncResult, asyncContext);
    }

    synchronized byte[] sendRequestSync(String firstLine, byte[] rest_of_request) throws YAPI_Exception
    {
        byte[] result;
        sendRequest(firstLine, rest_of_request, null, null);
        finishLastRequest(false);
        synchronized (_req_result) {
            result = _req_result.toByteArray();
        }
        int hpos = YAPIContext._find_in_bytes(result, "\r\n\r\n".getBytes());
        if (hpos >= 0) {
            return Arrays.copyOfRange(result, hpos + 4, result.length);
        }
        return result;
    }


    private void checkMetaUTC()
    {
        if (_lastMetaUTC + META_UTC_DELAY < YAPI.GetTickCount()) {
            YUSBPktOut ypkt = new YUSBPktOut();
            ypkt.open();
            ypkt.pushMetaUTC();
            byte[] bytes = ypkt.close();
            try {
                _rawDev.sendPkt(bytes);
                _lastMetaUTC = YAPI.GetTickCount();
            } catch (YAPI_Exception e) {
                e.printStackTrace();
                ioError("Unable to send meta UTC pkt:" + e.getLocalizedMessage());
            }
        }
    }


    WPEntry getWhitesPagesEntry()
    {
        return _wp;
    }

    void updateYellowPages(HashMap<String, ArrayList<YPEntry>> publicYP)
    {
        for (YPEntry yp : _usbYP.values()) {
            String classname = yp.getClassname();
            if (!publicYP.containsKey(classname))
                publicYP.put(classname, new ArrayList<YPEntry>());
            publicYP.get(classname).add(yp);
        }
    }


    /*
     * methods related incoming data handler
     */


    /*
     * Notification handler
     */
    private void handleNotifcation(YPktStreamHead.NotificationStreams not)
    {
        YPEntry yp;
        if (_serial != null && !_serial.equals(not.getSerial())) {
            return;
        }

        String functionId = not.getFunctionId();

        switch (not.getNotType()) {
            case CHILD:
                break;
            case FIRMWARE:
                _deviceid = not.getDeviceid();
                break;
            case FUNCNAME:
                yp = _usbYP.get(functionId);
                if (yp == null) {
                    yp = new YPEntry(_serial, functionId, not.getFunclass());
                    _usbYP.put(functionId, yp);
                }
                yp.setLogicalName(not.getFuncname());
                break;
            case FUNCNAMEYDX:
                yp = _usbYP.get(functionId);
                if (yp == null) {
                    yp = new YPEntry(_serial, functionId, not.getFunclass());
                    _usbYP.put(functionId, yp);
                }
                int funydx = not.getFunydx();
                // update ydx
                _usbIdx2Funcid.put(funydx, functionId);
                yp.setIndex(not.getFunydx());
                yp.setLogicalName(not.getFuncname());
                break;
            case FUNCVAL:
                yp = _usbYP.get(functionId);
                if (yp != null) {
                    _usbHub.handleValueNotification(not.getSerial(), functionId, not.getFuncval());
                }
                break;
            case FUNCVAL_TINY:
                yp = getYPEntryFromYdx(not.getFunydx());
                if (yp != null) {
                    _usbHub.handleValueNotification(not.getSerial(), yp.getFuncId(), not.getFuncval());
                }
                break;
            case FUNCVALFLUSH:
                // To be implemented later
                break;
            case LOG:
                break;
            case NAME:
                _logicalname = not.getLogicalname();
                _beacon = not.getBeacon();
                break;
            case PRODNAME:
                _productName = not.getProduct();
                break;
            case STREAMREADY:
                setStreamReady();
                break;
        }
    }

    private void handleTimedNotification(YPktStreamHead data)
    {
        int pos = 0;
        YDevice ydev = _usbHub._yctx._yHash.getDevice(_serial);
        if (ydev == null) {
            // device has not been registered;
            return;
        }
        while (pos < data.getContentSize()) {
            int funYdx = data.getByte(pos) & 0xf;
            boolean isAvg = (data.getByte(pos) & 0x80) != 0;
            int len = 1 + ((data.getByte(pos) >> 4) & 0x7);
            pos++;
            if (data.getContentSize() < pos + len) {
                _usbHub._yctx._Log("drop invalid timedNotification");
                return;
            }
            if (funYdx == 0xf) {
                Integer[] intData = new Integer[len];
                for (int i = 0; i < len; i++) {
                    intData[i] = data.getByte(pos + i) & 0xff;
                }
                ydev.setDeviceTime(intData);
            } else {
                YPEntry yp = getYPEntryFromYdx(funYdx);
                if (yp != null) {
                    ArrayList<Integer> report = new ArrayList<>(len + 1);
                    report.add(isAvg ? 1 : 0);
                    for (int i = 0; i < len; i++) {
                        int b = data.getByte(pos + i) & 0xff;
                        report.add(b);
                    }
                    _usbHub.handleTimedNotification(yp.getSerial(), yp.getFuncId(), ydev.getDeviceTime(), report);
                }
            }
            pos += len;
        }
    }


    private void handleTimedNotificationV2(YPktStreamHead data)
    {
        int pos = 0;
        YDevice ydev = _usbHub._yctx._yHash.getDevice(_serial);
        if (ydev == null) {
            // device has not been registered;
            return;
        }
        while (pos < data.getContentSize()) {
            int funYdx = data.getByte(pos) & 0xf;
            int extralen = (data.getByte(pos) >> 4) & 0xf;
            int len = extralen + 1;
            pos++; // consume generic header
            if (funYdx == 0xf) {
                Integer[] intData = new Integer[len];
                for (int i = 0; i < len; i++) {
                    intData[i] = data.getByte(pos + i) & 0xff;
                }
                ydev.setDeviceTime(intData);
            } else {
                YPEntry yp = getYPEntryFromYdx(funYdx);
                if (yp != null) {
                    ArrayList<Integer> report = new ArrayList<>(len + 1);
                    report.add(2);
                    for (int i = 0; i < len; i++) {
                        int b = data.getByte(pos + i) & 0xff;
                        report.add(b);
                    }
                    _usbHub.handleTimedNotification(yp.getSerial(), yp.getFuncId(), ydev.getDeviceTime(), report);
                }
            }
            pos += len;
        }
    }


    private void streamHandler(YUSBPktIn yusbPkt)
    {

        for (int i = 0; i < yusbPkt.getStreamCount(); i++) {
            YPktStreamHead s = yusbPkt.getStream(i);
            final int streamType = s.getStreamType();
            switch (streamType) {
                case YGenericHub.YSTREAM_NOTICE:
                case YGenericHub.YSTREAM_NOTICE_V2:
                    try {
                        YPktStreamHead.NotificationStreams not = s.decodeAsNotification(_serial, streamType == YGenericHub.YSTREAM_NOTICE_V2);
                        handleNotifcation(not);
                    } catch (YAPI_Exception ignore) {
                        _usbHub._yctx._Log("drop invalid notification" + ignore.getLocalizedMessage());
                    }
                    break;
                case YGenericHub.YSTREAM_TCP:
                    if (_pkt_state == PKT_State.StreamReadyReceived) {
                        s.writeTo(_req_result);
                    }
                    break;
                case YGenericHub.YSTREAM_TCP_CLOSE:
                    if (testState(PKT_State.StreamReadyReceived, null)) {
                        s.writeTo(_req_result);
                        if (_asyncResult != null) {
                            _asyncResult.RequestAsyncDone(_asyncContext, _req_result.toByteArray(), YAPI.SUCCESS, null);
                        }
                        remoteClose();
                    }
                    break;
                case YGenericHub.YSTREAM_EMPTY:
                    break;
                case YGenericHub.YSTREAM_REPORT:
                    if (testState(PKT_State.StreamReadyReceived, null)) {
                        handleTimedNotification(s);
                    }
                    break;
                case YGenericHub.YSTREAM_REPORT_V2:
                    if (testState(PKT_State.StreamReadyReceived, null)) {
                        handleTimedNotificationV2(s);
                    }
                    break;
                default:
                    _usbHub._yctx._Log("drop unknown ystream:" + s.toString());
                    break;
            }
        }
    }


    /**
     * methods related incoming data handler
     */


    private YUSBPktIn newpkt = new YUSBPktIn();


    /*
     * new packet handler
     */
    @Override
    public void newPKT(ByteBuffer android_raw_pkt)
    {
        try {
            newpkt.decode(android_raw_pkt);
        } catch (YAPI_Exception e) {
            _usbHub._yctx._Log("Drop invalid packet:" + e.getLocalizedMessage());
            e.printStackTrace();
            return;
        }
        ackInPkt(newpkt.getPktno());
        if (newpkt.isConfPkt()) {
            int streamType = newpkt.getStreamType();
            if (streamType == YUSBPkt.USB_CONF_RESET) {
                receiveConfReset(newpkt);
            } else if (streamType == YUSBPkt.USB_CONF_START) {
                receiveConfStart(newpkt);
            }
        } else {
            boolean use = false;
            synchronized (_stateLock) {
                if (_pkt_state == PKT_State.StreamReadyReceived || _pkt_state == PKT_State.StartReceived) {
                    use = true;
                }
            }
            if (use) {
                if (_pktAckDelay > 0 && _lastpktno == newpkt.getPktno()) {
                    //late retry : drop it since we already have the packet.
                    return;
                }
                int expectedPktNo = (_lastpktno + 1) & 7;
                if (newpkt.getPktno() != expectedPktNo) {
                    String message = "Missing packet (look of pkt " + expectedPktNo + " but get " + newpkt.getPktno() + ")";
                    _usbHub._yctx._Log(message + "\n");
                    _usbHub._yctx._Log("Set YAPI.RESEND_MISSING_PKT on YAPI.InitAPI()\n");
                    ioError(message);
                    return;
                }
                _lastpktno = newpkt.getPktno();
                streamHandler(newpkt);
                checkMetaUTC();
            } else {
                _usbHub._yctx._Log("Drop non-config pkt:");
                String dump = newpkt.toString();
                String[] lines = dump.split("\n");
                for (String s : lines) {
                    _usbHub._yctx._Log(s);
                }
            }
        }
    }


    @Override
    public void ioError(String errorMessage)
    {
        String msg = "USB IO error:" + errorMessage;
        _usbHub._yctx._Log(msg);
        synchronized (_stateLock) {
            _pkt_state = PKT_State.IOError;
            _tcp_state = TCP_State.Closed;
            _ioErrorMessage = errorMessage;
            _stateLock.notify();
        }
    }

    @Override
    public void rawDeviceUpdateState(YUSBRawDevice yusbRawDevice)
    {
        _rawDev = yusbRawDevice;
        if (_serial == null) {
            _serial = yusbRawDevice.getSerial();
        }

        if (_rawDev.isUsable()) {
            sendConfReset();
        }
    }
}
