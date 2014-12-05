/*********************************************************************
 *
 * $Id: YUSBDevice.java 18451 2014-11-20 16:17:56Z seb $
 *
 * YUSBDevice Class: 
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.yoctopuce.YoctoAPI.YAPI.SafeYAPI;

public class YUSBDevice implements YUSBRawDevice.IOHandler {

    private static final long META_UTC_DELAY = 1800000;

    public boolean isAllowed()
    {
        return _rawDev!=null && _rawDev.isUsable();
    }

    public String getSerial()
    {
        return _serial;
    }

    private enum PKT_State {
        Plugged,
        Authorize,
        ResetSend,
        ResetReceived,
        StartSend,
        StartReceived,
        StreamReadyReceived,
        IOError
    }

    private enum TCP_State {
        Closed, Opened, Close_by_dev, Close_by_API
    }

    // USB communication data
    private String _serial = null;
    private int _lastpktno;
    private YUSBRawDevice _rawDev;
    private long _lastMetaUTC = -1;
    // internal whites pages updated form notifications
    private final HashMap<String, WPEntry> _usbWP = new HashMap<String, WPEntry>();
    // internal yellow  pages updated form notifications
    private final HashMap<String, YPEntry> _usbYP = new HashMap<String, YPEntry>();
    private HashMap<String, String> _usbIdx2Funcid = new HashMap<String, String>();
    // mapping for ydx<serial> of potential subdevice for this USB device
    private ArrayList<String> _usbIdx2Serial = new ArrayList<String>();

    private final Object _stateLock = new Object();
    private PKT_State _pkt_state = PKT_State.Plugged;
    private TCP_State _tcp_state = TCP_State.Closed;

    // async request related
    private YGenericHub.RequestAsyncResult _asyncResult;
    private Object _asyncContext;
    private final ByteArrayOutputStream _req_result = new ByteArrayOutputStream(1024);
    private long _currentRequestTimeout;


    String getSerialFromYdx(int ydx)
    {
        return _usbIdx2Serial.get(ydx);
    }

    public String getFuncidFromYdx(String serial, int i)
    {
        return _usbIdx2Funcid.get(serial + i);
    }

    private YPEntry getYPEntryFromNotification(YPktStreamHead.NotificationStreams not) throws YAPI_Exception
    {
        YPEntry yp;
        synchronized (_usbYP) {
            if (!_usbYP.containsKey(not.getHardwareId())) {
                yp = new YPEntry(not.getSerial(), not.getFunctionId());
                _usbYP.put(not.getHardwareId(), yp);
            } else {
                yp = _usbYP.get(not.getHardwareId());
            }
        }
        return yp;
    }

    private YPEntry getYPEntryFromYdx(String serial, int funIdx)
    {
        YPEntry yp;
        String functionId = getFuncidFromYdx(serial, funIdx);
        String hwid = serial + "." + functionId;
        synchronized (_usbYP) {
            if (!_usbYP.containsKey(hwid)) {
                yp = new YPEntry(serial, functionId);
                _usbYP.put(hwid, yp);
            } else {
                yp = _usbYP.get(hwid);
            }
        }
        return yp;
    }

    public boolean waitEndOfInit(int wait)
    {
        try {
            waitForTcpState(PKT_State.StreamReadyReceived, null, wait, "Device not ready");
        } catch (YAPI_Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public YUSBDevice()
    {
    }


    private void setNewState(PKT_State new_pkt_state, TCP_State new_tcp_State)
    {
        synchronized (_stateLock) {
            _pkt_state = new_pkt_state;
            if (new_tcp_State != null) {
                _tcp_state = TCP_State.Closed;
            }
            _stateLock.notify();
        }
    }

    private boolean testState(PKT_State pkt_state, TCP_State tcp_State)
    {
        synchronized (_stateLock) {
            if (_pkt_state != pkt_state)
                return false;
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
                    SafeYAPI()._Log("Drop unexpected close from device\n");
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
                throw new YAPI_Exception(YAPI.IO_ERROR, "Device " + _serial + " " + message + " (io error)");
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
        YUSBPktOut ypkt_reset = YUSBPktOut.ResetPkt(this);
        try {
            _rawDev.sendPkt(ypkt_reset.getRawPkt());
            setNewState(PKT_State.ResetSend, TCP_State.Closed);
        } catch (YAPI_Exception e) {
            e.printStackTrace();
            ioError("Unable to send reset pkt:" + e.getLocalizedMessage());
        }
    }

    private void receiveConfReset(YUSBPktIn newpkt)
    {

        if (testState(PKT_State.ResetSend, null)) {
            YUSBPkt.ConfPktReset reset = newpkt.getConfPktReset();
            try {
                YUSBPkt.isCompatibe(reset.getApi(), _serial);
            } catch (YAPI_Exception e) {
                ioError(e.getLocalizedMessage());
                return;
            }
            // send startIO pkt
            YUSBPktOut ypkt_start = YUSBPktOut.StartPkt(this);
            try {
                _rawDev.sendPkt(ypkt_start.getRawPkt());
                setNewState(PKT_State.StartSend, null);
            } catch (YAPI_Exception e) {
                e.printStackTrace();
                ioError("Unable to send start pkt:" + e.getLocalizedMessage());
            }
        } else {
            SafeYAPI()._Log("Drop late reset packet:" + newpkt.toString());
        }
    }

    private void receiveConfStart(YUSBPktIn newpkt)
    {
        if (testState(PKT_State.StartSend, null)) {
            _lastpktno = newpkt.getPktno();
            setNewState(PKT_State.StartReceived, null);
        } else {
            SafeYAPI()._Log("Drop late start packet:" + newpkt.toString());
        }
    }

    private void setStreamReady()
    {
        synchronized (_stateLock) {
            if (_pkt_state == PKT_State.StartReceived) {
                _pkt_state = PKT_State.StreamReadyReceived;
                _stateLock.notify();
            } else {
                SafeYAPI()._Log("Streamready to early! :" + _pkt_state);
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
            while (_tcp_state == TCP_State.Opened && _currentRequestTimeout > YAPI.GetTickCount()) {
                try {
                    _stateLock.wait(_currentRequestTimeout - YAPI.GetTickCount());
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.TIMEOUT, "HTTP request on " + _serial + " did not finished correctly", e);
                }
            }
            // send API close
            ypkt = new YUSBPktOut(this);
            ypkt.pushTCPClose();
            _rawDev.sendPkt(ypkt.getRawPkt());
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
                    SafeYAPI()._Log("USB Close without device ack\n");
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
        // first enssure that last request has finished
        waitForTcpState(PKT_State.StreamReadyReceived, null, 10, "Device not ready");
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
            System.arraycopy(firstLine.getBytes(), 0, currentRequest, 0, len);
            System.arraycopy(rest_of_request, 0, currentRequest, firstLine.length(), rest_of_request.length);
        }
        _asyncResult = asyncResult;
        _asyncContext = asyncContext;
        _currentRequestTimeout = YAPI.GetTickCount() + 10000;// 10 sec
        int pos = 0;
        while (pos < currentRequest.length) {
            YUSBPktOut ypkt = new YUSBPktOut(this);
            pos += ypkt.pushTCP(currentRequest, pos, currentRequest.length - pos);
            _rawDev.sendPkt(ypkt.getRawPkt());
        }
    }

    public synchronized void sendRequestAsync(String firstLine, byte[] rest_of_request, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception
    {
        sendRequest(firstLine, rest_of_request, asyncResult, asyncContext);
    }

    public synchronized byte[] sendRequestSync(String firstLine, byte[] rest_of_request) throws YAPI_Exception
    {
        byte[] result;
        sendRequest(firstLine, rest_of_request, null, null);
        finishLastRequest(false);
        synchronized (_req_result) {
            result = _req_result.toByteArray();
        }
        int hpos = YAPI._find_in_bytes(result, "\r\n\r\n".getBytes());
        if (hpos >= 0) {
            return Arrays.copyOfRange(result, hpos + 4, result.length);
        }
        return result;
    }


    public void checkMetaUTC()
    {
        if (_lastMetaUTC + META_UTC_DELAY < YAPI.GetTickCount()) {
            YUSBPktOut ypkt = new YUSBPktOut(this);
            ypkt.pushMetaUTC();
            try {
                _rawDev.sendPkt(ypkt.getRawPkt());
                _lastMetaUTC = YAPI.GetTickCount();
            } catch (YAPI_Exception e) {
                e.printStackTrace();
                ioError("Unable to send meta UTC pkt:" + e.getLocalizedMessage());
            }
        }
    }


    public void updateWhitesPages(ArrayList<WPEntry> publicWP)
    {
        synchronized (_usbWP) {
            for (WPEntry wp : _usbWP.values()) {
                if (wp.isValid())
                    publicWP.add(wp);
            }
        }
    }

    public void updateYellowPages(HashMap<String, ArrayList<YPEntry>> publicYP)
    {
        synchronized (_usbYP) {
            for (YPEntry yp : _usbYP.values()) {
                if (!publicYP.containsKey(yp.getCateg()))
                    publicYP.put(yp.getCateg(), new ArrayList<YPEntry>());
                publicYP.get(yp.getCateg()).add(yp);
            }
        }
    }


    /**
     * methods related incoming data handler
     */


    /*
     * Notification handler
     */
    private void handleNotifcation(YPktStreamHead.NotificationStreams not) throws YAPI_Exception
    {
        WPEntry wp;
        YPEntry yp;
        synchronized (_usbWP) {
            if (!_usbWP.containsKey(not.getSerial())) {
                wp = new WPEntry(_usbWP.size(), not.getSerial(), "");
                _usbWP.put(not.getSerial(), wp);
            } else {
                wp = _usbWP.get(not.getSerial());
            }
        }

        switch (not.getNotType()) {
            case CHILD:
                _usbIdx2Serial.add(not.getDevydy(), not.getChildserial());
                break;
            case FIRMWARE:
                wp.setProductId(not.getDeviceid());
                break;
            case FUNCNAME:
                yp = getYPEntryFromNotification(not);
                yp.setLogicalName(not.getFuncname());
                break;
            case FUNCNAMEYDX:
                _usbIdx2Funcid.put(not.getSerial() + not.getFunydx(), not.getFunctionId());
                yp = getYPEntryFromNotification(not);
                yp.setLogicalName(not.getFuncname());
                yp.setIndex(not.getFunydx());
                yp.setBaseclass(not.getFunclass());
                break;
            case FUNCVAL:
                yp = getYPEntryFromNotification(not);
                SafeYAPI().setFunctionValue(yp.getHardwareId(), not.getFuncval());
                break;
            case LOG:
                break;
            case NAME:
                wp.setLogicalName(not.getLogicalname());
                wp.setBeacon(not.getBeacon());
                break;
            case PRODNAME:
                wp.setProductName(not.getProduct());
                break;
            case STREAMREADY:
                wp.validate();
                if (_serial.equals(not.getSerial())) {
                    setStreamReady();
                }
                break;
        }
    }

    public void handleTimedNotification(byte[] data)
    {
        int pos = 0;
        YDevice ydev = SafeYAPI().getDevice(_serial);
        if (ydev == null) {
            // device has not been registered;
            return;
        }
        while (pos < data.length) {
            int funYdx = data[pos] & 0xf;
            boolean isAvg = (data[pos] & 0x80) != 0;
            int len = 1 + ((data[pos] >> 4) & 0x7);
            pos++;
            if (data.length < pos + len) {
                SafeYAPI()._Log("drop invalid timedNotification");
                return;
            }
            if (funYdx == 0xf) {
                Integer[] intData = new Integer[len];
                for (int i = 0; i < len; i++) {
                    intData[i] = data[pos + i] & 0xff;
                }
                ydev.setDeviceTime(intData);
            } else {
                YPEntry yp = getYPEntryFromYdx(_serial, funYdx);
                ArrayList<Integer> report = new ArrayList<Integer>(len + 1);
                report.add(isAvg ? 1 : 0);
                for (int i = 0; i < len; i++) {
                    int b = data[pos + i] & 0xff;
                    report.add(b);
                }
                SafeYAPI().setTimedReport(yp.getHardwareId(), ydev.getDeviceTime(), report);
            }
            pos += len;
        }
    }


    public void handleTimedNotificationV2(byte[] data)
    {
        int pos = 0;
        YDevice ydev = SafeYAPI().getDevice(_serial);
        if (ydev == null) {
            // device has not been registered;
            return;
        }
        while (pos < data.length) {
            int funYdx = data[pos] & 0xf;
            int extralen = (data[pos] >> 4);
            int len = extralen + 1;
            pos++; // consume generic header
            if (funYdx == 0xf) {
                Integer[] intData = new Integer[len];
                for (int i = 0; i < len; i++) {
                    intData[i] = data[pos + i] & 0xff;
                }
                ydev.setDeviceTime(intData);
            } else {
                YPEntry yp = getYPEntryFromYdx(_serial, funYdx);
                ArrayList<Integer> report = new ArrayList<Integer>(len + 1);
                report.add(2);
                for (int i = 0; i < len; i++) {
                    int b = data[pos + i] & 0xff;
                    report.add(b);
                }
                SafeYAPI().setTimedReport(yp.getHardwareId(), ydev.getDeviceTime(), report);
            }
            pos += len;
        }
    }


    private void streamHandler(ArrayList<YPktStreamHead> streams)
    {
        for (YPktStreamHead s : streams) {
            switch (s.getStreamType()) {
                case YPktStreamHead.YSTREAM_NOTICE:
                    try {
                        YPktStreamHead.NotificationStreams not = s.decodeAsNotification(this);
                        handleNotifcation(not);
                    } catch (YAPI_Exception ignore) {
                        YAPI.SafeYAPI()._Log("drop invalid notification");
                    }
                    break;
                case YPktStreamHead.YSTREAM_TCP:
                    if (testState(PKT_State.StreamReadyReceived, null)) {
                        synchronized (_req_result) {
                            try {
                                _req_result.write(s.getDataAsByteArray());
                            } catch (IOException e) {
                                ioError(e.getLocalizedMessage());
                            }
                        }
                    }
                    break;
                case YPktStreamHead.YSTREAM_TCP_CLOSE:
                    if (testState(PKT_State.StreamReadyReceived, null)) {
                        synchronized (_req_result) {
                            try {
                                _req_result.write(s.getDataAsByteArray());
                            } catch (IOException e) {
                                ioError(e.getLocalizedMessage());
                            }
                            if (_asyncResult != null) {
                                _asyncResult.RequestAsyncDone(_asyncContext, _req_result.toByteArray(), YAPI.SUCCESS, null);
                            }
                        }
                        remoteClose();
                    }
                    break;
                case YPktStreamHead.YSTREAM_EMPTY:
                    break;
                case YPktStreamHead.YSTREAM_REPORT:
                    if (testState(PKT_State.StreamReadyReceived, null)) {
                        handleTimedNotification(s.getDataAsByteArray());
                    }
                    break;
                case YPktStreamHead.YSTREAM_REPORT_V2:
                    if (testState(PKT_State.StreamReadyReceived, null)) {
                        handleTimedNotificationV2(s.getDataAsByteArray());
                    }
                    break;
                default:
                    SafeYAPI()._Log("drop unknown ystream:" + s.toString());
                    break;
            }
        }
    }


    /**
     * methods related incoming data handler
     */


    /*
     * new packet handler
     */
    @Override
    public void newPKT(ByteBuffer android_raw_pkt)
    {
        YUSBPktIn newpkt;
        try {
            newpkt = YUSBPktIn.Decode(this, android_raw_pkt);
        } catch (YAPI_Exception e) {
            SafeYAPI()._Log("Drop invalid packet:" + e.getLocalizedMessage());
            e.printStackTrace();
            return;
        }
        if (newpkt.isConfPktReset()) {
            receiveConfReset(newpkt);
        } else if (newpkt.isConfPktStart()) {
            receiveConfStart(newpkt);
        } else {
            boolean use = false;
            synchronized (_stateLock) {
                if (_pkt_state == PKT_State.StreamReadyReceived || _pkt_state == PKT_State.StartReceived) {
                    use = true;
                }
            }
            if (use) {
                int expectedPktNo = (_lastpktno + 1) & 7;
                if (newpkt.getPktno() != expectedPktNo) {
                    String message = "Missing packet (look of pkt " + expectedPktNo + " but get " + newpkt.getPktno() + ")";
                    SafeYAPI()._Log(message + "\n");
                    ioError(message);
                    return;
                }
                _lastpktno = newpkt.getPktno();
                ArrayList<YPktStreamHead> streams = newpkt.getStreams();
                streamHandler(streams);
            } else {
                SafeYAPI()._Log("Drop non-config pkt:" + newpkt.toString());
            }
        }
    }


    @Override
    public void ioError(String errorMessage)
    {
        SafeYAPI()._Log("USB IO Error:" + errorMessage);
        setNewState(PKT_State.IOError, TCP_State.Closed);
    }

    @Override
    public void rawDeviceUpdateState(YUSBRawDevice yusbRawDevice)
    {
        _rawDev = yusbRawDevice;
        _serial = yusbRawDevice.getSerial();
        if (_rawDev.isUsable())
        sendConfReset();
    }


}
