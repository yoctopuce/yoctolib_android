package com.yoctopuce.examples.yocto_relay;

import com.getpebble.android.kit.util.PebbleDictionary;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YRelay;

public class Relay
{
    private final static int RELAY_STATE = 0;
    private final static int RELAY_NAME = 1;
    private final static int MODULE_NAME = 2;
    private final static int PRODUCT_NAME = 3;

    private String mHwId;
    private String mRelayName;
    private String mRelayId;
    private String mModuleName;
    private String mModuleSerial;
    private String mType;
    private boolean mOn;
    private YRelay  mYRelay;

    public Relay(YRelay yrelay) throws YAPI_Exception
    {
        mHwId = yrelay.get_hardwareId();
        mRelayName = yrelay.get_logicalName();
        mRelayId = yrelay.get_functionId();
        mType = yrelay.module().getProductName();
        mModuleSerial = yrelay.module().getSerialNumber();
        mModuleName = yrelay.module().get_logicalName();
        mOn = yrelay.get_output() == YRelay.OUTPUT_ON;
        mYRelay = yrelay;
               
    }

    public YRelay getYRelay()
    {
        return mYRelay;
    }

    public boolean updateFromYRelay(Relay newRelay)
    {
        if (mRelayName.equals(newRelay.mRelayName) && mOn == newRelay.mOn && mModuleName.equals(newRelay.mModuleName))
            return false;
        mRelayName = newRelay.mRelayName;
        mModuleName = newRelay.mModuleName;
        mOn = newRelay.mOn;
        return true;
    }

    public String getRelayInfo()
    {
        if(mRelayName.equals(""))
            return mRelayId;
        return mRelayName;
    }

    public String getModuleName()
    {
        if (mModuleName.equals(""))
            return mModuleSerial;
        return mModuleName;
    }

    public Boolean isOn() 
    {
        return mOn;
    }

    public void toggle()
    {
        mOn = !mOn;
    }

    public String getHwId()
    {
        return mHwId;
    }

    public String getType()
    {
        return mType;
    }

    @Override
    public String toString()
    {
        return mHwId;
    }

    public Object getSerial()
    {
        return mModuleSerial;
    }

    public PebbleDictionary toPebbleDictionary()
    {
        // Build up a Pebble dictionary containing the weather icon and the
        // current temperature in degrees celsius
        PebbleDictionary data = new PebbleDictionary();
        data.addString(RELAY_STATE, (mOn ? "ON" : "OFF"));
        data.addString(RELAY_NAME, getRelayInfo());
        data.addString(MODULE_NAME, getModuleName());
        data.addString(PRODUCT_NAME, mType);
        return data;
    }

    public static PebbleDictionary getEmptyPebbleDictionary()
    {
        PebbleDictionary data = new PebbleDictionary();
        data.addString(RELAY_STATE, "");
        data.addString(RELAY_NAME, "????????");
        data.addString(MODULE_NAME, "no module");
        data.addString(PRODUCT_NAME, "");
        return data;
    }

}
