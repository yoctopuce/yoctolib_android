package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class YUSBPktIn extends YUSBPkt {

    YUSBPktIn(YUSBDevice dev, ArrayList<YPktStreamHead> streams) {
        super(dev, streams.get(0).getPktNumber(), streams);
    }

    static YUSBPktIn Decode(YUSBDevice dev, ByteBuffer bb) throws YAPI_Exception
    {
        ArrayList<YPktStreamHead> streams = new ArrayList<YPktStreamHead>();
        while (bb.remaining() > 0) {
            YPktStreamHead s = YPktStreamHead.Decode(bb);
            if (s == null) {
                break;
            }
            streams.add(s);
        }
        return new YUSBPktIn(dev, streams);
    }

    public boolean isConfPktReset() {
        if (_streams.size() < 1)
            return false;
        YPktStreamHead s = _streams.get(0);
        return s.isConfPktReset();
    }


    public ConfPktReset getConfPktReset() {
        if (!isConfPktReset())
            return null;
        byte[] data = _streams.get(0).getDataAsByteArray();
        return ConfPktReset.Decode(data);
    }


    public boolean isConfPktStart() {
        if (_streams.size() < 1)
            return false;
        YPktStreamHead s = _streams.get(0);
        return s.isConfPktStart();
    }
}
