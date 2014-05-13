/*********************************************************************
 *
 * $Id: YUSBDevice.java 16128 2014-05-09 09:18:06Z seb $
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

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.yoctopuce.YoctoAPI.YAPI.SafeYAPI;

public class YUSBDevice
{

    private static final long META_UTC_DELAY = 1800000;
    // USB communication data
    private int _lastpktno;
    private YUSBRawDevice _rawDev;
    private long _lastMetaUTC = -1;

    // internal whites pages updated form notifications
    private final HashMap<String, WPEntry> _usbWP = new HashMap<String, WPEntry>();
    // internal yellowpage pages updated form notifications
    private final HashMap<String, YPEntry> _usbYP = new HashMap<String, YPEntry>();

    // state of the device
    private final Object _stateLock = new Object();
    private State _state = State.Detected;
    private YGenericHub.RequestAsyncResult _asyncResult;
    private Object _asyncContext;


    private enum State {
        Detected, ResetReceived, StartReceived, StreamReadyReceived,  NotWorking
    }

    private enum TCP_State {
        Closed, Opened, Close_by_dev, Close_by_API
    }

    private TCP_State _tcp_state = TCP_State.Closed;
    private final ByteArrayOutputStream _req_result = new ByteArrayOutputStream(1024);
    private long _currentRequestTimeout;

    // mapping for ydx<serial> of potential subdevice for this USB device
    private ArrayList<String> _usbIdx2Serial = new ArrayList<String>();

    String getSerialFromYdx(int ydx)
    {
        return _usbIdx2Serial.get(ydx);
    }

    private HashMap<String, String> _usbIdx2Funcid = new HashMap<String, String>();

    public String getFuncidFromYdx(String serial, int i)
    {
        return  _usbIdx2Funcid.get(serial + i);
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

    private YPEntry getYPEntryFromYdx(String serial, int funIdx) throws YAPI_Exception
    {
        YPEntry yp;
        String functionId = getFuncidFromYdx(serial,funIdx);
        String hwid = serial+"."+ functionId;
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
            if (getSerial().equals(not.getSerial())) {
                synchronized (_stateLock) {
                    if (_state == State.StartReceived) {
                        _state = State.StreamReadyReceived;
                        _stateLock.notify();
                    } else {
                        SafeYAPI()._Log("Streamready to early! :" + _state);
                    }
                }
            }
            break;
        }
    }

    public void handleTimedNotification(byte[] data) throws YAPI_Exception {
        int pos = 0;
        String serial = getSerial();
        YDevice ydev = SafeYAPI().getDevice(serial);
        if (ydev==null) {
            // device has not been registered;
            return;
        }
        while (pos < data.length){
            int funYdx = data[pos] & 0xf;
            boolean isAvg = (data[pos] & 0x80)!=0;
            int len = 1 + ((data[pos]>>4) & 0x7);
            pos++;
            if (funYdx == 0xf) {
                Integer[] intData = new Integer[len];
                for (int i = 0; i < len; i++) {
                    intData[i] = data[pos + i] & 0xff;
                }
                ydev.setDeviceTime(intData);
            }else {
                YPEntry yp = getYPEntryFromYdx(getSerial(),funYdx);
                ArrayList<Integer> report =  new ArrayList<Integer>(len+1);
                report.add( isAvg ? 1 : 0);
                for (int i = 0 ; i < len ; i++) {
                    int b = data[pos + i] & 0xff;
                    report.add(b);
                }
                SafeYAPI().setTimedReport(yp.getHardwareId(), ydev.getDeviceTime(), report);
            }
            pos += len;
        }
    }





    /*
     * new packet handler
     */
    public void newPKT(ByteBuffer android_raw_pkt)  {
        try {
            YUSBPktIn newpkt = YUSBPktIn.Decode(this, android_raw_pkt);
            if (newpkt.isConfPktReset()) {
                YUSBPkt.ConfPktReset reset = newpkt.getConfPktReset();
                YUSBPkt.isCompatibe(reset.getApi(), _rawDev.getSerial());
                setNewState(State.ResetReceived);
            } else if (newpkt.isConfPktStart()) {
                synchronized (_stateLock) {
                    if (_state == State.ResetReceived) {
                        _lastpktno = newpkt.getPktno();
                        _state = State.StartReceived;
                        _stateLock.notify();
                    } else {
                        SafeYAPI()._Log("Drop late confpkt:" + newpkt.toString());
                    }
                }
            } else {

                int expectedPktno = (_lastpktno + 1) & 7;
                if (newpkt.getPktno() != expectedPktno) {
                    SafeYAPI()._Log("Missing packet (look of pkt " + expectedPktno + " but get " + newpkt.getPktno() + ")\n");
                }
                _lastpktno = newpkt.getPktno();
                ArrayList<YPktStreamHead> streams = newpkt.getStreams();
                for (YPktStreamHead s :streams) {
                    switch (s.getStreamType()) {
                    case YPktStreamHead.YSTREAM_NOTICE:
                        try {
                            YPktStreamHead.NotificationStreams not = s.decodeAsNotification(this);
                            handleNotifcation(not);
                        }catch (YAPI_Exception ignore){}
                        break;
                    case YPktStreamHead.YSTREAM_TCP:
                        if (checkDeviceState(false, newpkt)){
                            synchronized (_req_result) {
                                _req_result.write(s.getDataAsByteArray());
                            }
                        }
                        break;
                    case YPktStreamHead.YSTREAM_TCP_CLOSE:
                        if (checkDeviceState(false, newpkt)){
                            synchronized (_req_result) {
                                _req_result.write(s.getDataAsByteArray());
                                if (_asyncResult != null) {
                                    _asyncResult.RequestAsyncDone(_asyncContext,_req_result.toByteArray());
                                }
                            }
                            remoteClose();
                        }
                        break;
                    case YPktStreamHead.YSTREAM_EMPTY:
                        break;
                    case YPktStreamHead.YSTREAM_REPORT:
                        if (checkDeviceState(true, newpkt)){
                            handleTimedNotification(s.getDataAsByteArray());
                        }
                        break;
                    default:
                        SafeYAPI()._Log("drop unknown ystream:" + s.toString());
                        break;
                    }
                }
            }
        } catch (YAPI_Exception e) {
            ioError(e.toString());
        } catch (IOException e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            SafeYAPI()._Log("Io error during packet decoding:" + e.toString());
            SafeYAPI()._Log(writer.toString());
            ioError(e.toString());
        }
    }

    private boolean checkDeviceState(boolean isNotification, YUSBPktIn newpkt) {
        synchronized (_stateLock) {
            if (isNotification){
                if (_state != State.StreamReadyReceived && _state != State.StartReceived) {
                    SafeYAPI()._Log("Drop early notification packet:" + newpkt.toString());
                    return false;
                }
            }else{
                if (_state != State.StreamReadyReceived) {
                    SafeYAPI()._Log("Drop early tcp packet:" + newpkt.toString());
                    return false;
                }
            }
        }
        return true;
    }

    YUSBDevice(UsbDevice device, UsbManager manager)
    {
        super();
        _rawDev = new YUSBRawDevice(device, manager, this);
    }

    void release()
    {
        _rawDev.release();
    }

    private void setNewState(State newstate)
    {
        synchronized (_stateLock) {
            _state = newstate;
            _tcp_state = TCP_State.Closed;
            _stateLock.notify();
        }
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

    private void waitForState(State wanted, State next, long mswait, String message) throws YAPI_Exception
    {
        long timeout = YAPI.GetTickCount() + mswait;
        synchronized (_stateLock) {
            while (_state != State.NotWorking && _state != wanted && timeout > YAPI.GetTickCount()) {
                long millis = timeout - YAPI.GetTickCount();
                try {
                    _stateLock.wait(millis);
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.TIMEOUT, "Device " + _rawDev.getSerial() + " " + message, e);
                }
            }
            if (_state == State.NotWorking) {
                throw new YAPI_Exception(YAPI.IO_ERROR, "Device " + _rawDev.getSerial() + " " + message + " (io error)");
            }
            if (_state != wanted) {
                throw new YAPI_Exception(YAPI.TIMEOUT, "Device " + _rawDev.getSerial() + " " + message + " (" + _state + ")");
            }
            if (next != null) {
                _state = next;
                _stateLock.notify();
            }
        }
    }

    public void reset() throws YAPI_Exception
    {
        // ensure that the device is started
        _rawDev.start();
        // send reset packet
        YUSBPktOut ypkt_reset = YUSBPktOut.ResetPkt(this);
        _rawDev.sendPkt(ypkt_reset);
        // wait ack
        waitForState(State.ResetReceived, null, 5000, " did not respond to reset pkt");
        // send start pkt
        YUSBPktOut ypkt_start = YUSBPktOut.StartPkt(this);
        _rawDev.sendPkt(ypkt_start);
        // wait ack
        waitForState(State.StartReceived, null, 5000, "unable to start connection to device");
        // wait notification for devices ok
        waitForState(State.StreamReadyReceived, null, 5000, "unable to start device");
        // send Meta UTC to update device clock
        checkMetaUTC();
    }

    private void finishLastRequest(boolean andOpenNewRequest) throws YAPI_Exception
    {
        YUSBPktOut ypkt;
        synchronized (_stateLock) {
            if (_tcp_state == TCP_State.Closed) {
                // nothing to do
                if(andOpenNewRequest) {
                    _tcp_state = TCP_State.Opened;
                }
                return;
            }
            while (_tcp_state == TCP_State.Opened && _currentRequestTimeout > YAPI.GetTickCount()) {
                try {
                    _stateLock.wait(_currentRequestTimeout - YAPI.GetTickCount());
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.TIMEOUT, "HTTP request on " + _rawDev.getSerial() + " did not finished correctly", e);
                }
            }
            // send API close
            ypkt = new YUSBPktOut(this);
            ypkt.pushTCPClose();
            _rawDev.sendPkt(ypkt);
            if (_tcp_state == TCP_State.Close_by_dev) {
                _tcp_state = TCP_State.Closed;
            } else {
                _tcp_state = TCP_State.Close_by_API;
                long timeout = YAPI.GetTickCount() + 100;
                while (_tcp_state == TCP_State.Close_by_API && timeout > YAPI.GetTickCount()) {
                    try {
                        _stateLock.wait(timeout - YAPI.GetTickCount());
                    } catch (InterruptedException e) {
                        throw new YAPI_Exception(YAPI.TIMEOUT, "HTTP request on " + _rawDev.getSerial() + " did not finished correctly", e);
                    }
                }
                if (_tcp_state != TCP_State.Closed) {
                    SafeYAPI()._Log("USB Close without device ack\n");
                    _tcp_state = TCP_State.Closed;
                }
            }
            if(andOpenNewRequest) {
                _tcp_state = TCP_State.Opened;
            }
        }
    }

    private void sendRequest(String firstLine, byte[] rest_of_request, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception
    {
        // first enssure that last request has finished
        waitForState(State.StreamReadyReceived, null, 10, "Device not ready");
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
            System.arraycopy(rest_of_request, 0, currentRequest,firstLine.length(), rest_of_request.length);
        }
        _asyncResult = asyncResult;
        _asyncContext = asyncContext;
        _currentRequestTimeout = YAPI.GetTickCount() + 10000;// 10 sec
        int pos =0;
        while (pos < currentRequest.length) {
            YUSBPktOut ypkt = new YUSBPktOut(this);
            pos += ypkt.pushTCP(currentRequest, pos, currentRequest.length -pos);
            _rawDev.sendPkt(ypkt);
        }
        checkMetaUTC();
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


    public String getSerial()
    {
        return _rawDev.getSerial();
    }

    public void checkMetaUTC() throws YAPI_Exception {
        if (_lastMetaUTC + META_UTC_DELAY < YAPI.GetTickCount()){
            YUSBPktOut ypkt = new YUSBPktOut(this);
            ypkt.pushMetaUTC();
            _rawDev.sendPkt(ypkt);
            _lastMetaUTC = YAPI.GetTickCount();
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

    public void ioError(String errorMessage)
    {
        SafeYAPI()._Log("USB IO Error:" + errorMessage);
        setNewState(State.NotWorking);
    }

}
