/*********************************************************************
 * $Id: YUSBHub.java 44027 2021-02-25 10:06:43Z web $
 *
 * YUSBHub Class: handle native USB acces
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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


class YUSBHub extends YGenericHub
{
    private static final String ACTION_USB_PERMISSION = "com.yoctopuce.YoctoAPI.USB_PERMISSION";
    private static final long YPROG_BOOTLOADER_TIMEOUT = 3600000;// 1 hour
    private static final int USB_ENUMERATION_DELAY = 1000;
    private static Context sAppContext = null;
    private final HashMap<String, YUSBRawDevice> _usbDevices = new HashMap<>(1);
    private final HashMap<String, YUSBDevice> _devsFromAndroidRef = new HashMap<>(2);
    private final HashMap<String, YUSBBootloader> _bootloadersFromAndroidRef = new HashMap<>(2);
    private final UsbManager _manager;
    private final int _pktAckDelay;
    private final boolean _requestPermission;


    private final BroadcastReceiver _usbBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device == null) {
                return;
            }
            String deviceName = device.getDeviceName();
            YUSBRawDevice yusbRawDevice = _usbDevices.get(deviceName);

            if (ACTION_USB_PERMISSION.equals(action)) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    _yctx._Log("HUB_USB: permission granted for device " + deviceName + "\n");
                    if (yusbRawDevice != null) {
                        yusbRawDevice.permissionAccepted();
                    }
                } else {
                    _yctx._Log("HUB_USB: permission denied for device " + deviceName + "\n");
                    if (yusbRawDevice != null) {
                        yusbRawDevice.permissionRejected();
                    }
                }
                synchronized (_permissionPending) {
                    YUSBRawDevice current = _permissionPending.poll();
                    if (current==null){
                        return;
                    }
                    if (!current.getUsbDevice().equals(device)) {
                        return;
                    }
                    YUSBRawDevice next = _permissionPending.peek();
                    if (next != null) {
                        doPermissionRequest(next.getUsbDevice());
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                _yctx._Log("HUB_USB: unplug of device " + deviceName + "\n");
                if (yusbRawDevice != null) {
                    yusbRawDevice.unplug();
                }
            }
        }
    };

    @Override
    boolean isCallbackMode()
    {
        return false;
    }

    @Override
    boolean isReadOnly()
    {
        return false;
    }

    @Override
    String getSerialNumber()
    {
        return "";
    }

    @Override
    public String get_urlOf(String serialNumber)
    {
        return "usb";
    }

    @Override
    public ArrayList<String> get_subDeviceOf(String serialNumber)
    {
        return new ArrayList<>();
    }

    YUSBHub(YAPIContext yctx, int idx, boolean requestPermission, int pktAckDelay) throws YAPI_Exception
    {
        super(yctx, new HTTPParams("usb://"), idx, true);
        _pktAckDelay = pktAckDelay;
        _manager = (UsbManager) sAppContext.getSystemService(Context.USB_SERVICE);
        if (_manager == null) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "Unable to get Android USB manager");
        }
        _requestPermission = requestPermission;
        //Create a new filter to detect USB device events
        IntentFilter filter = new IntentFilter();
        if (_requestPermission) {
            filter.addAction(ACTION_USB_PERMISSION);
        }
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        sAppContext.registerReceiver(_usbBroadcastReceiver, filter);
    }

    @Override
    void startNotifications()
    {
    }

    @Override
    void stopNotifications()
    {
        removeAllDevices();
    }


    @Override
    void release()
    {
        sAppContext.unregisterReceiver(_usbBroadcastReceiver);
        for (String devname : _usbDevices.keySet()) {
            _usbDevices.get(devname).release();
        }
        _usbDevices.clear();
        _devsFromAndroidRef.clear();
        _bootloadersFromAndroidRef.clear();
    }


    private final Queue<YUSBRawDevice> _permissionPending = new LinkedList<>();

    void requestUSBPermission(YUSBRawDevice device)
    {
        if (_requestPermission) {
            boolean doRequest = false;
            synchronized (_permissionPending) {
                if (_permissionPending.contains(device)) {
                    return;
                }
                if (_permissionPending.size() == 0) {
                    doRequest = true;
                }
                _permissionPending.add(device);
            }
            _yctx._Log("HUB_USB: trigger request permission for " + device.getUsbDevice().getDeviceName() + "\n");
            if (doRequest) {
                UsbDevice usbDevice = device.getUsbDevice();
                doPermissionRequest(usbDevice);
            }
        } else {
            _yctx._Log("HUB_USB: hub is working in silent mode. Ignore request permission for " + device.getUsbDevice().getDeviceName() + "\n");
            device.permissionIngore();
        }
    }

    private void doPermissionRequest(UsbDevice device)
    {
        Intent intent = new Intent(ACTION_USB_PERMISSION);
        _yctx._Log("HUB_USB: request permission for " + device.getDeviceName() + "\n");
        PendingIntent askPermissionIntent = PendingIntent.getBroadcast(sAppContext, 0, intent, 0);
        _manager.requestPermission(device, askPermissionIntent);
    }


    private void refreshUsableDeviceList()
    {
        HashMap<String, UsbDevice> connectedDevices;
        try {
            connectedDevices = _manager.getDeviceList();
        } catch (Exception ignore) {
            return;
        }
        if (connectedDevices == null) {
            return;
        }
        //todo: trap unplug event instead of active pooling,
        // mark all device as to remove
        ArrayList<String> toRemove = new ArrayList<>(_usbDevices.keySet());
        for (Map.Entry<String, UsbDevice> entry : connectedDevices.entrySet()) {
            String android_dev_name = entry.getKey();
            UsbDevice usbdevice = entry.getValue();
            if (usbdevice.getInterfaceCount() < 1)
                continue;
            int deviceid = usbdevice.getProductId();
            if (usbdevice.getVendorId() == YAPI.YOCTO_VENDORID && deviceid != YAPI.YOCTO_DEVID_FACTORYBOOT) {
                if (_usbDevices.containsKey(android_dev_name)) {
                    toRemove.remove(android_dev_name);
                    _usbDevices.get(android_dev_name).ensureIOStarted();
                    continue;
                }
                YUSBRawDevice rawDevice;
                if (deviceid == YAPI.YOCTO_DEVID_BOOTLOADER) {
                    YUSBBootloader bootloader = new YUSBBootloader(android_dev_name);
                    rawDevice = new YUSBRawDevice(this, usbdevice, _manager, bootloader);
                    _bootloadersFromAndroidRef.put(android_dev_name, bootloader);
                } else {
                    YUSBDevice device = new YUSBDevice(this, _pktAckDelay, android_dev_name);
                    rawDevice = new YUSBRawDevice(this, usbdevice, _manager, device);
                    _devsFromAndroidRef.put(android_dev_name, device);
                }
                rawDevice.ensureIOStarted();
                _usbDevices.put(android_dev_name, rawDevice);
                //rawDevice.checkAndroidPermission();
            }
        }
        for (String devname : toRemove) {
            YUSBRawDevice yusbRawDevice = _usbDevices.get(devname);
            if (yusbRawDevice != null) {
                yusbRawDevice.release();
            }
            if (_devsFromAndroidRef.containsKey(devname)) {
                _devsFromAndroidRef.remove(devname);
            }
            if (_bootloadersFromAndroidRef.containsKey(devname)) {
                _bootloadersFromAndroidRef.remove(devname);
            }
            _usbDevices.remove(devname);
        }
    }


    @Override
    synchronized void updateDeviceList(boolean forceupdate) throws YAPI_Exception
    {

        long now = YAPI.GetTickCount();
        if (forceupdate) {
            _devListExpires = 0;
        }
        if (_devListExpires > now) {
            return;
        }
        refreshUsableDeviceList();
        HashMap<String, ArrayList<YPEntry>> yellowPages = new HashMap<>();
        ArrayList<WPEntry> whitePages = new ArrayList<>();
        for (YUSBDevice d : _devsFromAndroidRef.values()) {
            if (d.isAllowed() && d.waitEndOfInit()) {
                whitePages.add(d.getWhitesPagesEntry());
                d.updateYellowPages(yellowPages);
            }
        }
        updateFromWpAndYp(whitePages, yellowPages);
        // reset device list cache timeout for this hub
        now = YAPI.GetTickCount();
        _devListExpires = now + _yctx._deviceListValidityMs;
    }

    @Override
    public ArrayList<String> getBootloaders()
    {
        ArrayList<String> res = new ArrayList<>(_bootloadersFromAndroidRef.size());
        refreshUsableDeviceList();
        for (YUSBBootloader bootloader : _bootloadersFromAndroidRef.values()) {
            if (bootloader.isReady())
                res.add(bootloader.getSerial());
        }
        return res;
    }

    @Override
    public int ping(int mstimeout) throws YAPI_Exception
    {
        return YAPI.SUCCESS;
    }

    @Override
    java.util.ArrayList<String> firmwareUpdate(String serial, YFirmwareFile firmware, byte[] settings, UpdateProgress progress) throws YAPI_Exception, InterruptedException
    {
        String reboot_req = "GET /api/module/rebootCountdown?rebootCountdown=-3";

        //1% -> 5%
        progress.firmware_progress(1, "Wait bootloader to be detected");
        // USB connected device -> reboot it in bootloader
        YUSBBootloader bootloader = null;
        long timeout = YAPI.GetTickCount() + YPROG_BOOTLOADER_TIMEOUT;
        boolean rebootsent = false;
        do {
            refreshUsableDeviceList();
            for (YUSBBootloader b : _bootloadersFromAndroidRef.values()) {
                if (b.isReady() && serial.equals(b.getSerial())) {
                    bootloader = b;
                    break;
                }
            }
            if (bootloader == null) {
                if (!rebootsent) {
                    YUSBDevice d = devFromSerial(serial);
                    d.sendRequestSync(reboot_req, null);
                    rebootsent = true;
                } else {
                    Thread.sleep(500, 0);
                }
            }
            refreshUsableDeviceList();
        } while (bootloader == null && timeout > YAPI.GetTickCount());
        if (bootloader == null) {
            throw new YAPI_Exception(YAPI.DEVICE_NOT_FOUND, "bootloader is not detected");
        }
        //5% ->100
        progress.firmware_progress(5, "Start usb firmware update");
        bootloader.firmwareUpdate(firmware, progress);
        //todo: detect when the user has rejected the access to the device
        return null;
    }


    private YUSBDevice devFromSerial(String serial) throws YAPI_Exception
    {
        //todo: test if we spent too much time here (alternatively use two hashmap)
        for (Map.Entry<String, YUSBDevice> entry : _devsFromAndroidRef.entrySet()) {
            YUSBDevice yusbDevice = entry.getValue();
            if (serial.equals(yusbDevice.getSerial())) {
                return yusbDevice;
            }
        }
        throw new YAPI_Exception(YAPI.DEVICE_NOT_FOUND, "Device has been unplugged");
    }

    @Override
    void devRequestAsync(YDevice device, String req_first_line, byte[] req_head_and_body, RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception
    {
        String serial = device.getSerialNumber();
        int i = req_first_line.lastIndexOf("&.");
        if (i >= 0) {
            req_first_line = req_first_line.substring(0, i);
        }
        YUSBDevice d = devFromSerial(serial);
        d.sendRequestAsync(req_first_line, req_head_and_body, asyncResult, asyncContext);
    }

    @Override
    byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body, RequestProgress progress, Object context) throws YAPI_Exception
    {
        String serial = device.getSerialNumber();
        YUSBDevice d = devFromSerial(serial);
        return d.sendRequestSync(req_first_line, req_head_and_body);
    }

    static void SetContextType(Object ctx) throws YAPI_Exception
    {
        if (!(ctx instanceof Context)) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Object is not a valid Android Application Context");
        }
        Context app_ctx = ((Context) ctx).getApplicationContext();
        if (sAppContext != null && app_ctx != sAppContext) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Android Application Context already set");
        }
        sAppContext = app_ctx;
    }

    static void CheckUSBAcces() throws YAPI_Exception
    {
        if (sAppContext == null) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "You must enable USB host mode before registering usb devices");
        }
    }

    @Override
    String getRootUrl()
    {
        return "usb";
    }

    @Override
    boolean isSameHub(String url, Object request, Object response, Object session)
    {
        return url.startsWith("usb");
    }

    static boolean RegisterLocalhost()
    {
        return false;
    }

    public static String addUdevRule(boolean force)
    {
        return "error: Not supported in Android";
    }



}