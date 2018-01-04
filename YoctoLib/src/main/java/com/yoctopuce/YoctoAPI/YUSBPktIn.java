package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.util.Locale;

class YUSBPktIn extends YUSBPkt
{

    YUSBPktIn()
    {
        super();
        for (int i = 0; i < _streams.length; i++) {
            _streams[i] = new YPktStreamHead();
        }

    }

    int getStreamType()
    {
        return _streams[0].getStreamType();
    }

    boolean isConfPkt()
    {
        return _streams[0].getPktType() == YPKT_CONF;
    }

    int getPktno()
    {
        return _streams[0].getPktNumber();
    }

    void decode(ByteBuffer pkt) throws YAPI_Exception
    {
        int stream_idx = 0;
        pkt.get(_raw, 0, USB_PKT_SIZE);
        int ofs = 0;
        while (ofs < USB_PKT_SIZE && stream_idx < _streams.length) {
            if (ofs + USB_PKT_STREAM_HEAD_SIZE > USB_PKT_SIZE) {
                break;
            }
            // decode stream head
            int b = _raw[ofs++] & 0xff;
            int pktNumber = b & 7;
            int streamType = (b >> 3);
            b = _raw[ofs++] & 0xff;
            int pktType = b & 3;
            int dataLen = b >> 2;
            if (dataLen + ofs > USB_PKT_SIZE) {
                throw new YAPI_Exception(YAPI.IO_ERROR, String.format(Locale.US, "invalid ystream header (invalid length %d+%d)", ofs, dataLen));
            }
            if (pktType == YPKT_STREAM && streamType == YGenericHub.YSTREAM_EMPTY)
                break;
            _streams[stream_idx].update(pktNumber, pktType, streamType, _raw, ofs, dataLen);
            ofs += dataLen;
            stream_idx++;
        }
        _streamCount = stream_idx;
    }


    ConfPktReset asConfPktReset()
    {
        return ConfPktReset.Decode(_streams[0]);
    }

    ConfPktStart asConfPktStart()
    {
        return ConfPktStart.Decode(_streams[0]);
    }


}
