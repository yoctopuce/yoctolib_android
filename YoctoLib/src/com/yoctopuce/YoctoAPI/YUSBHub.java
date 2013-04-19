package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public class YUSBHub extends YGenericHub
{

	private static Context _appContext = null;
	private HashMap<String, YUSBDevice> _devsFromAndroidRef = new HashMap<String, YUSBDevice>();
	private HashMap<String, YUSBDevice>     _devsFromSerial = new HashMap<String, YUSBDevice>();
	private ArrayList<String>  _requestedPermitions = new ArrayList<String>();
	private UsbManager _manager;
	private PendingIntent _askPermissionIntent = null;
	private static final String _ACTION_USB_PERMISSION = "com.yoctopuce.YoctoAPI.USB_PERMISSION";

	private final BroadcastReceiver _usbBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
    		String action = intent.getAction();
			if (_ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (device != null) {
						if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
							YAPI.Log("HUB_USB: permission granted for device " + device.getDeviceName()+"\n");
							try {
								updateDeviceList(true);
							} catch (YAPI_Exception e) {
								YAPI.Log("Unable to register new plugged device ("+device.getDeviceName()+":"+e.getLocalizedMessage()+")\n");
								e.printStackTrace();
							}
						} else {
							YAPI.Log("HUB_USB: permission denied for device " + device.getDeviceName()+"\n");
						}
					}
				}
			} else 	if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {       
    			/* If it was a USB device detach event, then get the USB device
    			 * that cause the event from the intent.
    			 */
    			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);      	        
    			if (device != null) {
					YAPI.Log("HUB_USB: device " + device.getDeviceName()+"has been unplugeed (intent)");
					synchronized (_requestedPermitions) {
						_requestedPermitions.remove(device.getDeviceName());
					}
					try {
						updateDeviceList(true);
					} catch (YAPI_Exception e) {
						YAPI.Log("Unable to unregister unplugged device ("+device+":"+e.getLocalizedMessage()+")\n");
					}
    			}
			}
		}
	};

	/*
	 * Constuctor
	 */

	public YUSBHub(int idx) throws YAPI_Exception
	{
		super(idx);
		_manager = (UsbManager) _appContext.getSystemService(Context.USB_SERVICE);
		if (_manager == null) {
			throw new YAPI_Exception(YAPI.IO_ERROR, "Unable to get Android USB manager");
		}
		_askPermissionIntent = PendingIntent.getBroadcast(_appContext, 0, new Intent(_ACTION_USB_PERMISSION), 0);
		
		//Create a new filter to detect USB device events
		IntentFilter filter = new IntentFilter();
		filter.addAction(_ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		_appContext.registerReceiver(_usbBroadcastReceiver, filter);
	}

	@Override
	public void startNotifications()
	{
	}

	@Override
	public void stopNotifications()
	{
	}

	
	@Override
	public void release()
	{
	    //fixed by Lorenzeo of valarm.net 
		for(String devname: new ArrayList<String>(_devsFromAndroidRef.keySet())){
			_devsFromAndroidRef.get(devname).release();
			_devsFromAndroidRef.remove(devname);
		}
		_appContext.unregisterReceiver(_usbBroadcastReceiver);
	}

	private void refreshUsableDeviceLsit()
	{
		HashMap<String, UsbDevice> connectedDevices = _manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = connectedDevices.values().iterator();
		// mark all device as to remove
		ArrayList<String>  toRemove = new ArrayList<String>(_devsFromAndroidRef.keySet());
		boolean hasRequestedPermission=false;
		
		YAPI.Log(String.format(Locale.ENGLISH,"ANDROID: USB manager know %d devices\n",connectedDevices.size()));
		
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			YAPI.Log(String.format(Locale.ENGLISH,"ANDROID: %s (0x%04x:0x%04x)\n",device.getDeviceName(),device.getVendorId(),device.getProductId()));

			if (device.getVendorId() != YAPI.YOCTO_VENDORID) {
				continue;
			}
			int deviceid = device.getDeviceId();
			if (deviceid == YAPI.YOCTO_DEVID_BOOTLOADER || deviceid == YAPI.YOCTO_DEVID_FACTORYBOOT) {
				YAPI.Log("HUB_USB: drop yoctopuce bootloader for now\n");
				continue;
			}
			
			if (_manager.hasPermission(device)){
				if ( !_devsFromAndroidRef.containsKey(device.getDeviceName())) {
					YAPI.Log("HUB_USB: use "+device.getDeviceName()+"\n");				
					YUSBDevice newdev=new YUSBDevice(device, _manager);
					try {
						newdev.reset();
						String serial=newdev.getSerial();
						_devsFromAndroidRef.put(device.getDeviceName(), newdev);
						_devsFromSerial.put(serial, newdev);
						YAPI.Log("HUB_USB: Device "+serial+" ("+device.getDeviceName()+") started\n");
					} catch (YAPI_Exception e) {
						newdev.release();
						YAPI.Log(e.getStackTraceToString());
					}
				}
				toRemove.remove(device.getDeviceName());
			} else if (!hasRequestedPermission){
				synchronized (_requestedPermitions) {
					if(_requestedPermitions.contains(device.getDeviceName())){
						continue;
					}
					YAPI.Log("HUB_USB: request permition for "+device.getDeviceName()+"\n");
					_requestedPermitions.add(device.getDeviceName());
					_manager.requestPermission(device, _askPermissionIntent);	
				}
				hasRequestedPermission=true;
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
	 public byte[] devRequest(YDevice device, String req_first_line, byte[] req_head_and_body, Boolean async) throws YAPI_Exception
	{
		String serial = device.getSerialNumber();
		if(!_devsFromSerial.containsKey(serial))
			throw new YAPI_Exception(YAPI.NOT_SUPPORTED, "Device has been unpluged");
		YUSBDevice d=_devsFromSerial.get(serial);
		return  d.sendRequest(req_first_line,req_head_and_body,async);
	}
	
	
	public static void SetContextType(Object ctx) throws YAPI_Exception
    {
        YAPI.Log("HUB_USB:context type=" + ctx.getClass().getName() + "\n");
        if (!(ctx instanceof Context)) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Object is not a valid Android Application Context");
        }
        Context app_ctx =((Context) ctx).getApplicationContext();
        if (_appContext != null && app_ctx!=_appContext) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Android Application Context allready set");
        }
        _appContext = app_ctx;
    }
	
	public static void CheckUSBAcces() throws YAPI_Exception
	{
        if (_appContext == null) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "You must enable USB host mode before registering usb devices");
        }
	}

	@Override
	public String getRootUrl()
	{
		return "usb";
	}

	@Override
	public boolean isSameRootUrl(String url)
	{
		return url.equals("usb");
	}
	
}