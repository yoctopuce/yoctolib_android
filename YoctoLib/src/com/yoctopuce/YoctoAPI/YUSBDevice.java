package com.yoctopuce.YoctoAPI;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.yoctopuce.YoctoAPI.YUSBPkt.StreamHead;
import com.yoctopuce.YoctoAPI.YUSBRawDevice.PKTHandler;

public class YUSBDevice implements PKTHandler
{

    // USB communication data
    private int _lastpktno;
    private YUSBRawDevice _rawDev;

    // internal whites pages updated form notifications
    private final HashMap<String, WPEntry> _usbWP = new HashMap<String, WPEntry>();
    // internal yellowpage pages updated form notifications
    private final HashMap<String, YPEntry> _usbYP = new HashMap<String, YPEntry>();

    // state of the device
    private final Object _stateLock = new Object();
    private State _state = State.Detected;

    private enum State {
        Detected, ResetOk, ConfStarted, TCPSetuped, TCPWaiting, NotWorking
    }

    private enum TCP_State {
        Closed, Opened, Close_by_dev, Close_by_API
    }

    private TCP_State _tcp_state = TCP_State.Closed;
    private final ByteArrayOutputStream _req_result = new ByteArrayOutputStream(1024);
    private byte[] _currentRequest;
    //private ByteBuffer _currentRequest;
    private long _currentRequestTimeout;

    // mapping for ydx<serial> of potential subdevice for this USB device
    private ArrayList<String> _usbIdx2Serial = new ArrayList<String>();

    String getSerialFromYdx(int ydx)
    {
        return _usbIdx2Serial.get(ydx);
    }

    int getYdxFormSerial(String serial)
    {
        return _usbIdx2Serial.indexOf(serial);
    }

    private HashMap<String, String> _usbIdx2Funcid = new HashMap<String, String>();

    public String getFuncidFromYdx(String serial, int i)
    {
        String res = _usbIdx2Funcid.get(serial + i);
        return res;
    }

    private YPEntry getYPEntryForNotification(YUSBPkt.NotificationDecoder not)
    {
        YPEntry yp;
        synchronized (_usbYP) {
            if (!_usbYP.containsKey(not.getLongFunctionID())) {
                yp = new YPEntry(not.getSerial(), not.getShortFunctionID());
                _usbYP.put(not.getLongFunctionID(), yp);
            } else {
                yp = _usbYP.get(not.getLongFunctionID());
            }
        }
        return yp;
    }

    /*
     * Notification handler
     */
    private void handleNotifcation(StreamHead s) throws YAPI_Exception
    {
        YUSBPkt.NotificationDecoder not = new YUSBPkt.NotificationDecoder(s, this);
        // YAPI.Log(not.dump());
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
            yp = getYPEntryForNotification(not);
            yp.setLogicalName(not.getFuncname());
            break;
        case FUNCNAMEYDX:
            _usbIdx2Funcid.put(not.getSerial() + not.getFunydx(), not.getShortFunctionID());
            yp = getYPEntryForNotification(not);
            yp.setLogicalName(not.getFuncname());
            yp.setIndex(not.getFunydx());
            break;
        case FUNCVAL:
            yp = getYPEntryForNotification(not);
            yp.setAdvertisedValue(not.getFuncval());
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
                    if (_state == State.ConfStarted) {
                        _state = State.TCPSetuped;
                        _stateLock.notify();
                    } else {
                        YAPI.Log("Streamready to early! :" + _state);
                    }
                }
            }
            break;
        }
    }

    /*
     * new packet handler
     */
    public void newPKT(ByteBuffer rawpkt)
    {
        try {
            YUSBPkt pkt = new YUSBPkt();
            pkt.parse(rawpkt);
            if (pkt.isConfPkt()) {
                if (pkt.isConfPktReset()) {
                    YUSBPkt.ConfPktResetDecoder reset = pkt.getConfPktReset();
                    YUSBPkt.isCompatibe(reset.api, _rawDev.getSerial());
                    setNewState(State.ResetOk);
                } else if (pkt.isConfPktStart()) {
                    synchronized (_stateLock) {
                        if (_state == State.ResetOk) {
                            _lastpktno = pkt.getPktno();
                            _state = State.ConfStarted;
                            _stateLock.notify();
                        } else {
                            YAPI.Log("Drop late confpkt:" + pkt.dumpToString());
                        }
                    }
                } else {
                    YAPI.Log("Unknown configuration packet received:" + pkt.dumpToString());
                    return;
                }
            } else {
                int expectedPktno = (_lastpktno + 1) & 7;
                if (pkt.getPktno() != expectedPktno) {
                    YAPI.Log("Missing packet (look of pkt " + expectedPktno + " but get " + pkt.getPktno() + ")\n");
                }
                _lastpktno = pkt.getPktno();
                int nbstreams = pkt.getNbStreams();
                for (int i = 0; i < nbstreams; i++) {
                    StreamHead s = pkt.getStream(i);
                    switch (s.getStreamType()) {
                    case YUSBPkt.YSTREAM_NOTICE:
                        handleNotifcation(s);
                        break;
                    case YUSBPkt.YSTREAM_TCP:
                        synchronized (_req_result) {
                            _req_result.write(s.getDataAsByteArray());
                        }
                        // _currentRequest.append(s.getDataAsString());
                        break;
                    case YUSBPkt.YSTREAM_TCP_CLOSE:
                        synchronized (_req_result) {
                            _req_result.write(s.getDataAsByteArray());
                        }
                        // _currentRequest.append(s.getDataAsString());
                        remoteClose();
                        break;
                    case YUSBPkt.YSTREAM_EMPTY:
                        break;
                    }
                }
            }
        } catch (YAPI_Exception e) {
            YAPI.Log("Invalid packet received:" + e.getStackTraceToString());
        } catch (Exception e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            YAPI.Log("Invalid packet received:" + e.getLocalizedMessage());
            YAPI.Log(writer.toString());
        }
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
                YAPI.Log("Drop unexepected close from device\n");
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
        YUSBPkt ypkt = new YUSBPkt();
        ypkt.formatResetPkt();
        _rawDev.sendPkt(ypkt);
        // wait ack
        waitForState(State.ResetOk, null, 5000, " did not respond to reset pkt");
        // send start pkt
        ypkt.clear();
        ypkt.formatStartPkt();
        _rawDev.sendPkt(ypkt);
        // wait ack
        waitForState(State.ConfStarted, null, 5000, "unable to start connection to device");
        // wait notification for devices ok
        waitForState(State.TCPSetuped, null, 5000, "unable to start device");
    }

    private void finishLastRequest(boolean andOpenNewRequest) throws YAPI_Exception
    {
        YUSBPkt ypkt;
        synchronized (_stateLock) {
            if (_tcp_state == TCP_State.Closed) {
                // nothing to do
                if(andOpenNewRequest)
                    _tcp_state = TCP_State.Opened;
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
            ypkt = new YUSBPkt();
            ypkt.pushTCPClose();
            _rawDev.sendPkt(ypkt);
            if (_tcp_state == TCP_State.Close_by_dev) {
                if(andOpenNewRequest)
                    _tcp_state = TCP_State.Opened;
                else
                    _tcp_state = TCP_State.Closed;
                return;
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
                    YAPI.Log("USB Close without device ack\n");
                    _tcp_state = TCP_State.Closed;
                }
            }
            if(andOpenNewRequest)
                _tcp_state = TCP_State.Opened;
        }
    }

    public synchronized byte[] sendRequest(String firstLine, byte[] rest_of_request, boolean async) throws YAPI_Exception
    {
        byte[] result;
        // first enssure that last request has finished
        waitForState(State.TCPSetuped, null, 10, "Device not ready");
        finishLastRequest(true);
        synchronized (_req_result) {
            _req_result.reset();
        }
        
        
        if (rest_of_request == null) {
            _currentRequest =(firstLine+"\r\n\r\n").getBytes();
        } else {
            firstLine += "\r\n";
            int len = firstLine.length() + rest_of_request.length;
            _currentRequest = new byte[len];
            System.arraycopy(firstLine.getBytes(), 0, _currentRequest, 0, len);
            System.arraycopy(rest_of_request, 0, _currentRequest,firstLine.length(), rest_of_request.length);
        }
        _currentRequestTimeout = YAPI.GetTickCount() + 10000;// 10 sec
        int pos =0;
        YUSBPkt ypkt = new YUSBPkt();
        while (pos < _currentRequest.length) {
            pos += ypkt.pushTCP(_currentRequest, pos, _currentRequest.length -pos);
            _rawDev.sendPkt(ypkt);
            ypkt.clear();
        }
        if (!async) {
            finishLastRequest(false);
            synchronized (_req_result) {
                result = _req_result.toByteArray();
            }
            int hpos = YAPI._find_in_bytes(result, "\r\n\r\n".getBytes());
            if (hpos >= 0) {
                return Arrays.copyOfRange(result, hpos + 4, result.length);
            }
        } else {
            return new byte[0];
        }
        return result;
    }

    public String getSerial()
    {
        return _rawDev.getSerial();
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

    public void ioError()
    {
        setNewState(State.NotWorking);
    }

}
