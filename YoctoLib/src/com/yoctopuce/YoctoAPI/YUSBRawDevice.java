/*********************************************************************
 *
 * $Id: YUSBRawDevice.java 12427 2013-08-20 16:00:19Z seb $
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

import java.nio.ByteBuffer;

import com.yoctopuce.YoctoAPI.YAPI;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

public class YUSBRawDevice implements Runnable {
	protected UsbDevice _device = null;
	private UsbManager _manager = null;
	private UsbDeviceConnection _connection;
	private UsbInterface _intf;
	private String _serial = null;
	private PKTHandler _pktHandler;
	private boolean _muststop = false;
	private final Object _threadLock = new Object();
	private Thread thread;

	public interface PKTHandler {

		void newPKT(ByteBuffer pkt);
		void ioError();
	}

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

	int getVendorId() {
		return _device.getVendorId();
	}

	int getDeviceId() {
		return _device.getDeviceId();
	}

	String getSerial() {
		return _serial;
	}

	/**
	 * Constructor - creates connection to device and launches the thread that
	 * runs the actual demo.
	 * 
	 * @param context
	 *            Context requesting to run the demo.
	 * @param device
	 *            The USB device to attach to.
	 * @param handler
	 *            The Handler where demo Messages should be sent.
	 */
	YUSBRawDevice(UsbDevice device, UsbManager manager, PKTHandler pktHandler) {
		_device = device;
		_manager = manager;
		_pktHandler = pktHandler;

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
		if (_connection.claimInterface(_intf, true) != true) {
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
		_connection.releaseInterface(_intf);
		_connection.close();
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

	public synchronized void sendPkt(YUSBPkt ypkt) throws YAPI_Exception {
		if (_intf == null) {
			throw new YAPI_Exception(YAPI.IO_ERROR, "Device is gone");
		}
		//ByteBuffer pkt = ypkt.getRawPkt();
	
		//pkt.rewind();

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

		/* Send the request to get the push button status */
		int result;
		byte outpkt[] = ypkt.getRawPkt();
		int retry = 15;
		do {
			result = _connection.bulkTransfer(endpointOUT, outpkt,
					outpkt.length, 1000);
			retry--;
		} while (result < 0 && retry >= 0);

	}

	public void run() {
		int nbSuccesiveError = 0;
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

		// initalise both directions requests
		UsbRequest d2h_r = new UsbRequest();
		d2h_r.initialize(_connection, endpointIN);
		ByteBuffer d2h_pkt = ByteBuffer.allocate(YUSBPkt.USB_PKT_SIZE);
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
				nbSuccesiveError = 0;
				if (finished.getEndpoint().getDirection() == UsbConstants.USB_DIR_IN) {
					// d2h request
					_pktHandler.newPKT(d2h_pkt);
					d2h_pkt = ByteBuffer.allocate(YUSBPkt.USB_PKT_SIZE);
					d2h_r.queue(d2h_pkt, YUSBPkt.USB_PKT_SIZE);
				}
			} else {
				if (nbSuccesiveError > 5) {
					_pktHandler.ioError();
					break;
				}
			}
		}
	}

}
