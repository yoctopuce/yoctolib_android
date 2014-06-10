/*********************************************************************
 *
 * $Id: YUSBHub.java 16480 2014-06-10 08:28:21Z seb $
 *
 * YUSBHub Class: handle native USB acces
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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.yoctopuce.YoctoAPI.YAPI.SafeYAPI;

class YUSBHub extends YGenericHub
{
	private static final String ACTION_USB_PERMISSION = "com.yoctopuce.YoctoAPI.USB_PERMISSION";
	private static Context _appContext = null;
	private HashMap<String, YUSBDevice> _devsFromAndroidRef = new HashMap<String, YUSBDevice>();
	private HashMap<String, YUSBDevice>     _devsFromSerial = new HashMap<String, YUSBDevice>();
	private final ArrayList<String>  _requestedPermitions = new ArrayList<String>();
	private UsbManager _manager;
	private boolean permissionPending=false;

	private final BroadcastReceiver _usbBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
    		String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					permissionPending=false;
					UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (device != null) {
						if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            SafeYAPI()._Log("HUB_USB: permission granted for device " + device.getDeviceName() + "\n");
						} else {
                            SafeYAPI()._Log("HUB_USB: permission denied for device " + device.getDeviceName() + "\n");
						}
					}
				}		
				requestPermission();
			} else 	if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {       
    			/* If it was a USB device detach event, then get the USB device
    			 * that cause the event from the intent.
    			 */
    			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);      	        
    			if (device != null) {
                    SafeYAPI()._Log("HUB_USB: device " + device.getDeviceName() + "has been unplugeed (intent)");
					synchronized (_requestedPermitions) {
						_requestedPermitions.remove(device.getDeviceName());
					}
    			}
			}
		}
	};

	/*
	 * Constuctor
	 */

	YUSBHub(int idx) throws YAPI_Exception
	{
		super(idx);
		_manager = (UsbManager) _appContext.getSystemService(Context.USB_SERVICE);
		if (_manager == null) {
			throw new YAPI_Exception(YAPI.IO_ERROR, "Unable to get Android USB manager");
		}
		
		//Create a new filter to detect USB device events
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		_appContext.registerReceiver(_usbBroadcastReceiver, filter);
	}

	@Override
	void startNotifications()
	{
	}

	@Override
	void stopNotifications()
	{
	}

	
	@Override
	void release()
	{
	    //fixed by Lorenzeo of valarm.net 
		for(String devname: new ArrayList<String>(_devsFromAndroidRef.keySet())){
			_devsFromAndroidRef.get(devname).release();
			_devsFromAndroidRef.remove(devname);
		}
		_appContext.unregisterReceiver(_usbBroadcastReceiver);
	}

	
	
	synchronized void requestPermission()
	{
		HashMap<String, UsbDevice> connectedDevices = _manager.getDeviceList();
        if (connectedDevices == null) {
            return;
        }
        Iterator<UsbDevice>  deviceIterator = connectedDevices.values().iterator();

        if(permissionPending){
				return;
		}
		
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			if (device.getVendorId() != YAPI.YOCTO_VENDORID) {
				continue;
			}
			int deviceid = device.getProductId();
			if (deviceid == YAPI.YOCTO_DEVID_BOOTLOADER || deviceid == YAPI.YOCTO_DEVID_FACTORYBOOT) {
				continue;
			}
			
			if (!_manager.hasPermission(device)){
				synchronized (_requestedPermitions) {
					if(_requestedPermitions.contains(device.getDeviceName())){
						// we already have ask the user
						continue;
					}
					_requestedPermitions.add(device.getDeviceName());
				}
                SafeYAPI()._Log("HUB_USB: request permission for " + device.getDeviceName() + "\n");
				Intent intent = new Intent(ACTION_USB_PERMISSION);
				PendingIntent askPermissionIntent = PendingIntent.getBroadcast(_appContext, 0, intent, 0);
				permissionPending=true;
				_manager.requestPermission(device, askPermissionIntent);
				break;
			}
		}
	}

	
	void refreshUsableDeviceLsit()
	{
		HashMap<String, UsbDevice> connectedDevices = _manager.getDeviceList();
        if (connectedDevices == null) {
            return;
        }
		Iterator<UsbDevice> deviceIterator = connectedDevices.values().iterator();
		// mark all device as to remove
		ArrayList<String>  toRemove = new ArrayList<String>(_devsFromAndroidRef.keySet());
		
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			if (device.getVendorId() != YAPI.YOCTO_VENDORID) {
				continue;
			}
			int deviceid = device.getProductId();
			if (deviceid == YAPI.YOCTO_DEVID_BOOTLOADER || deviceid == YAPI.YOCTO_DEVID_FACTORYBOOT) {
                SafeYAPI()._Log("HUB_USB: drop yoctopuce bootloader for now\n");
				continue;
			}
			
			if (_manager.hasPermission(device)){
				if ( !_devsFromAndroidRef.containsKey(device.getDeviceName())) {
                    SafeYAPI()._Log("HUB_USB: use " + device.getDeviceName() + "\n");
					YUSBDevice newdev=new YUSBDevice(device, _manager);
					try {
						newdev.reset();
						String serial=newdev.getSerial();
						_devsFromAndroidRef.put(device.getDeviceName(), newdev);
						_devsFromSerial.put(serial, newdev);
                        SafeYAPI()._Log("HUB_USB: Device " + serial + " (" + device.getDeviceName() + ") started\n");
					} catch (YAPI_Exception e) {
                        newdev.release();
                        SafeYAPI()._Log(e.getStackTraceToString());
					}
				}
				toRemove.remove(device.getDeviceName());
			} 
		}
		
		for(String devname: toRemove){
			_devsFromAndroidRef.get(devname).release();
			_devsFromAndroidRef.remove(devname);
		}
	}

	
	@Override
	synchronized void updateDeviceList(boolean forceupdate) throws YAPI_Exception
	{
		requestPermission();
		refreshUsableDeviceLsit();
        HashMap<String, ArrayList<YPEntry>> yellowPages = new HashMap<String, ArrayList<YPEntry>>();
        ArrayList<WPEntry> whitePages = new ArrayList<WPEntry>();
		for(YUSBDevice d :_devsFromAndroidRef.values()) {
			d.updateWhitesPages(whitePages);
			d.updateYellowPages(yellowPages);
		}
        updateFromWpAndYp(whitePages, yellowPages);
	}

    @Override
    void devRequestAsync(YDevice device, String req_first_line, byte[] req_head_and_body, RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception
    {
        String serial = device.getSerialNumber();
        if(!_devsFromSerial.containsKey(serial))
            throw new YAPI_Exception(YAPI.NOT_SUPPORTED, "Device has been unpluged");
        int i = req_first_line.lastIndexOf("&.");
        if (i >=0) {
            req_first_line = req_first_line.substring(0,i);
        }
        YUSBDevice d=_devsFromSerial.get(serial);
        d.sendRequestAsync(req_first_line, req_head_and_body, asyncResult, asyncContext);
    }

    @Override
    byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body) throws YAPI_Exception
    {
        String serial = device.getSerialNumber();
        if(!_devsFromSerial.containsKey(serial))
            throw new YAPI_Exception(YAPI.NOT_SUPPORTED, "Device has been unpluged");
        YUSBDevice d=_devsFromSerial.get(serial);
        return  d.sendRequestSync(req_first_line, req_head_and_body);
    }

	static void SetContextType(Object ctx) throws YAPI_Exception
    {
        SafeYAPI()._Log("HUB_USB:context type=" + ctx.getClass().getName() + "\n");
        if (!(ctx instanceof Context)) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Object is not a valid Android Application Context");
        }
        Context app_ctx =((Context) ctx).getApplicationContext();
        if (_appContext != null && app_ctx!=_appContext) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Android Application Context allready set");
        }
        _appContext = app_ctx;
    }
	
	static void CheckUSBAcces() throws YAPI_Exception
	{
        if (_appContext == null) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "You must enable USB host mode before registering usb devices");
        }
	}

	@Override
	String getRootUrl()
	{
		return "usb";
	}

	@Override
	boolean isSameRootUrl(String url)
	{
		return url.equals("usb");
	}

    public static boolean RegisterLocalhost()
    {
        return false;
    }
}