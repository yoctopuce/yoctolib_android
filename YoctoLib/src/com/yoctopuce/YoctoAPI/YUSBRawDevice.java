/*********************************************************************
 *
 * $Id: YUSBRawDevice.java 14271 2014-01-09 10:03:04Z seb $
 *
 * YUSBRawDevice Class: low level USB code
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

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import java.nio.ByteBuffer;

public class YUSBRawDevice implements Runnable {
	protected UsbDevice _device = null;
	private UsbManager _manager = null;
	private UsbDeviceConnection _connection;
	private UsbInterface _intf;
	private String _serial = null;
	private YUSBDevice _yUsbDevice;
	private boolean _muststop = false;
	private final Object _threadLock = new Object();
	private Thread thread;


	private boolean mustBgThreadStop() {
		boolean b;
		synchronized (_threadLock) {
			b = _muststop;
		}
		return b;
	}

	private void stopBgThread() {
		synchronized (_threadLock) {
			_muststop = true;
		}
	}

	String getSerial() {
		return _serial;
	}


	YUSBRawDevice(UsbDevice device, UsbManager manager, YUSBDevice yUsbDevice) {
		_device = device;
		_manager = manager;
		_yUsbDevice = yUsbDevice;

	}

	public synchronized void start() throws YAPI_Exception {
		_intf = _device.getInterface(0);
		/* Open a connection to the USB device */
		_connection = _manager.openDevice(_device);

		if (_connection == null) {
			throw new YAPI_Exception(YAPI.IO_ERROR,
					"unable to open connection to device "
							+ _device.getDeviceName());
		}

		/* Claim the required interface to gain access to it */
		if (!_connection.claimInterface(_intf, true)) {
			throw new YAPI_Exception(YAPI.IO_ERROR,
					"unable to claim interface 0 for device "
							+ _device.getDeviceName());
		}
		_serial = _connection.getSerial();
		thread = new Thread(this);
		thread.setName("IOusb_" + _serial);
		thread.start();
	}

	public synchronized void release() {
		stopBgThread();
		if (_connection!=null) {
			_connection.releaseInterface(_intf);
			_connection.close();
		}
		if (thread != null) {
			try {

				thread.join(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			thread = null;
		}
		/* Clear up all of the locals */
		_device = null;
		_manager = null;
		_connection = null;
		_intf = null;
	}

	public synchronized void sendPkt(YUSBPktOut ypkt) throws YAPI_Exception {
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

		int result;
		byte outPkt[] = ypkt.getRawPkt();
		int retry = 0;
		do {
            result = _connection.bulkTransfer(endpointOUT, outPkt,
					outPkt.length, 1000);
			retry++;
		} while (result < 0 && retry < 15);
	}

	public void run() {
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
            _yUsbDevice.ioError("Unable to get USB In endpoint");
            return;
        }
		// initialise both directions requests
		UsbRequest d2h_r = new UsbRequest();
		d2h_r.initialize(_connection, endpointIN);
        byte[] data = new byte[YUSBPkt.USB_PKT_SIZE];
        data[0]= (byte) 0xde;
        data[1]= (byte) 0xad;
        data[2]= (byte) 0xbe;
        data[3]= (byte) 0xef;
        d2h_r.setClientData(data);
        ByteBuffer d2h_pkt = ByteBuffer.wrap(data);
		d2h_r.queue(d2h_pkt, YUSBPkt.USB_PKT_SIZE);
		while (!mustBgThreadStop()) {
			/*
			 * If the connection was closed, destroy the connections and
			 * variables and exit this thread.
			 */
			if (_connection == null) {
				break;
			}
			UsbRequest finished;
			try {
				finished = _connection.requestWait();
			} catch (Exception e) {
				finished = null;
			}
			if (finished != null) {
                UsbEndpoint endp = finished.getEndpoint();
				nbSuccessiveError = 0;
				if (endp != null && endp.getDirection() == UsbConstants.USB_DIR_IN) {
                    // d2h request
					_yUsbDevice.newPKT(ByteBuffer.wrap(data));
                    d2h_pkt.clear();
                    d2h_r.queue(d2h_pkt, YUSBPkt.USB_PKT_SIZE);
				}
			} else {
				if (nbSuccessiveError > 5) {
					_yUsbDevice.ioError("Too may successive USB error");
					break;
				}
			}
            try {
                _yUsbDevice.checkMetaUTC();
            } catch (YAPI_Exception e) {
                _yUsbDevice.ioError(e.getMessage());
            }

        }
	}

}
