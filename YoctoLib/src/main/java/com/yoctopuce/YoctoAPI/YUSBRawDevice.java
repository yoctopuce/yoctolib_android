/**
 * ******************************************************************
 *
 * $Id: YUSBRawDevice.java 20772 2015-06-26 17:07:25Z seb $
 *
 * YUSBRawDevice Class: low level USB code
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

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class YUSBRawDevice implements Runnable
{
    private static final String TAG = "YPKT";
    private State _state;
    private YUSBHub _usbHub;
    private String _serial;

    public String getSerial()
    {
        return _serial;
    }

    public boolean isUsable()
    {
        return _state == State.ACCEPTED;
    }

    enum State
    {
        UNPLUGGED,
        PLUGGED,
        ACCEPTED,
        REJECTED
    }

    final private UsbDevice _device;
    final private UsbManager _manager;
    final private IOHandler _ioHandler;
    private volatile UsbDeviceConnection _connection;
    private volatile UsbInterface _intf;
    private boolean _muststop = false;
    private final Object _threadLock = new Object();
    private Thread thread;
    private boolean _ioStarted;


    private boolean mustBgThreadStop()
    {
        boolean b;
        synchronized (_threadLock) {
            b = _muststop;
        }
        return b;
    }

    private void stopBgThread()
    {
        synchronized (_threadLock) {
            _muststop = true;
        }
    }

    public UsbDevice getUsbDevice()
    {
        return _device;
    }


    public interface IOHandler
    {
        void newPKT(ByteBuffer android_raw_pkt) throws InterruptedException;

        void ioError(String msg);

        void rawDeviceUpdateState(YUSBRawDevice yusbRawDevice);
    }

    YUSBRawDevice(YUSBHub yusbHub, UsbDevice device, UsbManager manager, IOHandler handler)
    {
        _usbHub = yusbHub;
        _device = device;
        _manager = manager;
        _ioHandler = handler;
        _ioStarted = false;
        _state = State.PLUGGED;
    }


    public synchronized void ensureIOStarted()
    {
        if (_ioStarted)
            return;
        _intf = _device.getInterface(0);
        if (!_manager.hasPermission(_device)) {
            if (_state == State.REJECTED && _device.getProductId() != YAPI.YOCTO_DEVID_BOOTLOADER) {
                // if the user has rejected the authorisation we do not ask it again
                // (except for bootloader)
                return;
            }
            _usbHub.requestUSBPermission(this);
        } else {
            permissionAccepted();
        }
    }

    boolean permissionAccepted()
    {
        _state = State.ACCEPTED;
        /* Open a connection to the USB device */
        _connection = _manager.openDevice(_device);
        if (_connection == null) {
            YAPI.SafeYAPI()._Log("unable to open connection to device " + _device.getDeviceName());
            release();
            return false;
        }

		/* Claim the required interface to gain access to it */
        if (!_connection.claimInterface(_intf, true)) {
            YAPI.SafeYAPI()._Log("unable to claim interface 0 for device " + _device.getDeviceName());
            release();
            return false;
        }
        _serial = _connection.getSerial();
        thread = new Thread(this);
        thread.setName("IOusb_" + _serial);
        thread.start();

        synchronized (this) {
            if (!_ioStarted) {
                try {
                    this.wait(500);
                } catch (InterruptedException e) {
                    YAPI.SafeYAPI()._Log("unable to start IOhTread: " + e.getLocalizedMessage());
                    release();
                    return false;
                }
            }
            if (!_ioStarted) {
                YAPI.SafeYAPI()._Log("unable to start IOhTread ");
                release();
                return false;
            }
        }
        _ioHandler.rawDeviceUpdateState(this);
        return true;
    }

    public void permissionRejected()
    {
        _state = State.REJECTED;

    }

    public void unplug()
    {
        _state = State.UNPLUGGED;
    }


    public synchronized void release()
    {
        stopBgThread();
        if (_connection != null) {
            _connection.releaseInterface(_intf);
            _connection.close();
        }
        if (thread != null) {
            try {
                thread.join(20);
            } catch (InterruptedException e) {
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            }
            thread = null;
        }
        /* Clear up all of the locals */
        _connection = null;
        _intf = null;
    }

    public synchronized void sendPkt(byte[] outPkt) throws YAPI_Exception
    {
        if (_intf == null) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "Device is gone");
        }
        UsbEndpoint endpointOUT = null;
        for (int e = 0; e < _intf.getEndpointCount(); e++) {
            UsbEndpoint endp = _intf.getEndpoint(e);
            // out and in meaning is a bit strange
            // USB_DIR_OUT Used to signify direction of data for a UsbEndpoint
            // is OUT (host to device)
            // USB_DIR_IN Used to signify direction of data for a UsbEndpoint is
            // IN (device to host)
            if (endp.getDirection() == UsbConstants.USB_DIR_OUT) {
                endpointOUT = endp;
            }
        }
        if (endpointOUT == null) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "Unable to get USB Out endpoint");
        }

        int result = 0;
        int attempt = 0;
        do {
            if (result < 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.IO_ERROR, "InterruptedException ont pkt sent");
                }
            }
            result = _connection.bulkTransfer(endpointOUT, outPkt,
                    outPkt.length, 1000);
            attempt++;
        } while (result < 0 && attempt < 15);
        if (result < 0) {
            throw new YAPI_Exception(YAPI.IO_ERROR,
                    String.format("Unable to send USB packet after %d attempt (res=%d)",
                            attempt, result));

        }
    }

    public void run()
    {
        int nbSuccessiveError = 0;
        UsbEndpoint endpointIN = null;
        for (int e = 0; e < _intf.getEndpointCount(); e++) {
            UsbEndpoint endp = _intf.getEndpoint(e);
            // out and in meaning is a bit strange
            // USB_DIR_OUT Used to signify direction of data for a UsbEndpoint
            // is OUT (host to device)
            // USB_DIR_IN Used to signify direction of data for a UsbEndpoint is
            // IN (device to host)
            if (endp.getDirection() == UsbConstants.USB_DIR_IN) {
                endpointIN = endp;
            }
        }
        if (endpointIN == null) {
            _ioHandler.ioError("Unable to get USB In endpoint");
            return;
        }
        // initialise both directions requests
        UsbRequest d2h_r = new UsbRequest();
        boolean initializeed = d2h_r.initialize(_connection, endpointIN);
        if (!initializeed) {
            String msg = "Unable to initialize USB request for " + _device.toString();
            _ioHandler.ioError(msg);
            return;
        }

        // init the packet with some garbage to detect errors
        ByteBuffer d2h_pkt = ByteBuffer.allocateDirect(YUSBPkt.USB_PKT_SIZE);
        d2h_pkt.order(ByteOrder.LITTLE_ENDIAN);
        boolean d2h_request_queued = d2h_r.queue(d2h_pkt, YUSBPkt.USB_PKT_SIZE);
        synchronized (YUSBRawDevice.this) {
            _ioStarted = true;
            YUSBRawDevice.this.notifyAll();
        }

        while (!mustBgThreadStop()) {
            //If the connection was closed, destroy the connections and
            //variables and exit this thread.
            if (_connection == null) {
                break;
            }
            UsbRequest finished = null;
            if (d2h_request_queued) {
                try {
                    finished = _connection.requestWait();
                } catch (Exception e) {
                    _ioHandler.ioError("USB requestWait():" + e.getLocalizedMessage());
                    break;
                }
            }
            if (finished != null) {
                UsbEndpoint endp = finished.getEndpoint();
                nbSuccessiveError = 0;
                if (endp != null && endp.getDirection() == UsbConstants.USB_DIR_IN) {
                    d2h_pkt.rewind();
                    if (d2h_pkt.limit() == YUSBPkt.USB_PKT_SIZE) {
                        if (!mustBgThreadStop()) {
                            try {
                                _ioHandler.newPKT(d2h_pkt);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    } else {
                        //Log.d(TAG, "drop invalid packet" + d2h_pkt.toString());
                    }
                    d2h_pkt.clear();
                    d2h_request_queued = d2h_r.queue(d2h_pkt, YUSBPkt.USB_PKT_SIZE);

                }
            } else {
                if (nbSuccessiveError > 5) {
                    _ioHandler.ioError("Too may successive USB error");
                    break;
                }
                nbSuccessiveError++;
                // let Android breath a bit
                try {
                    Thread.sleep(500, 0);
                } catch (InterruptedException e) {
                    break;
                }
            }

        }
    }

}
