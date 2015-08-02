package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class YUSBPktIn extends YUSBPkt
{

    YUSBPktIn(LinkedList<YPktStreamHead> streams)
    {
        super(streams.get(0).getPktNumber(), streams);
    }

    static YUSBPktIn Decode(ByteBuffer bb) throws YAPI_Exception
    {
        LinkedList<YPktStreamHead> streams = new LinkedList<>();
        while (bb.remaining() > 0) {
            YPktStreamHead s = YPktStreamHead.Decode(bb);
            if (s == null) {
                break;
            }
            streams.add(s);
        }
        return new YUSBPktIn(streams);
    }

    public boolean isConfPktReset()
    {
        if (_streams.size() < 1)
            return false;
        YPktStreamHead s = _streams.get(0);
        return s.isConfPktReset();
    }


    public ConfPktReset getConfPktReset()
    {
        if (!isConfPktReset())
            return null;
        byte[] data = _streams.get(0).getDataAsByteArray();
        return ConfPktReset.Decode(data);
    }


    public boolean isConfPktStart()
    {
        if (_streams.size() < 1)
            return false;
        YPktStreamHead s = _streams.get(0);
        return s.isConfPktStart();
    }

    public ConfPktStart getConfPktStart()
    {
        if (!isConfPktStart())
            return null;
        byte[] data = _streams.get(0).getDataAsByteArray();
        return ConfPktStart.Decode(data);
    }

}
