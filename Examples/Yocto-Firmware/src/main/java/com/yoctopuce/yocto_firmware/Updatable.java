package com.yoctopuce.yocto_firmware;

import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YFirmwareUpdate;

import java.util.Arrays;

public class Updatable
{
    private final String _serial;
    private final  String _firmware;
    private final byte[] _icon2d;
    private final String _product;
    private String _latestFirmwarePath = "www.yoctopuce.com";
    private String _latestFirmwareRev="";


    public Updatable(String _serial, String product, String firmware, byte[] icon2d)
    {
        this._serial = _serial;
        this._firmware = firmware;
        this._product = product;
        this._icon2d = icon2d;
    }

    public Updatable(String serial)
    {
        _serial = serial;
        _product = "Bootloader";
        _firmware = "";
        _icon2d = null;

    }

    public String getSerial()
    {
        return _serial;
    }

    public String getfirmware()
    {
        return _firmware;
    }

    public String checkLatestFirmware()
    {
        if (_latestFirmwarePath.equals("www.yoctopuce.com")) {
            int minrelease;
            try {
                minrelease = Integer.parseInt(_firmware);
            } catch (NumberFormatException ignore) {
                minrelease = 0;
            }
            _latestFirmwarePath = YFirmwareUpdate.CheckFirmware(_serial, "www.yoctopuce.com", minrelease);
            _latestFirmwareRev = "";
            if (_latestFirmwarePath.startsWith("error:")) {
                return "";
            }
            if (_latestFirmwarePath.length()>0) {
                String[] parts = _latestFirmwarePath.split("\\.");
                if (parts.length > 3) {
                    _latestFirmwareRev = parts[parts.length - 2];
                }
            }
        }
        return _latestFirmwarePath;
    }



    public byte[] getIcon2d()
    {
        return _icon2d;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Updatable updatable = (Updatable) o;

        if (_firmware != null ? !_firmware.equals(updatable._firmware) : updatable._firmware != null)
            return false;
        if (!Arrays.equals(_icon2d, updatable._icon2d)) return false;
        if (_serial != null ? !_serial.equals(updatable._serial) : updatable._serial != null)
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _serial != null ? _serial.hashCode() : 0;
        result = 31 * result + (_firmware != null ? _firmware.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(_icon2d);
        return result;
    }

    public String getProduct()
    {
        return _product;
    }

    public String getLatestFirmwareRev() {
        return _latestFirmwareRev;
    }

    public String getLatestFirmwarePath() {
        return _latestFirmwarePath;
    }
}
