package com.yoctopuce.YoctoAPI;

class YUSBPktOut extends YUSBPkt
{

    private static final int USB_META_UTCTIME = 1;
    private static final int USB_META_DLFLUSH = 2;
    private static final int USB_META_ACK_D2H_PACKET = 3;
    private int _writeOfs;

    YUSBPktOut()
    {
        super();
    }


    public void open()
    {
        _streamCount = 0;
        _writeOfs = 0;
    }


    public byte[] close()
    {
        int remain = USB_PKT_SIZE - USB_PKT_STREAM_HEAD_SIZE - _writeOfs;
        if (remain >= 0) {
            // pad with empty
            writeStreamHead(0, YPKT_STREAM, YGenericHub.YSTREAM_EMPTY, remain);
        }
        return _raw;
    }


    private void writeStreamHead(int pktNumber, int pktType, int streamType, int contentSize)
    {
        _raw[_writeOfs++] = (byte) ((pktNumber & 7) | ((streamType & 0x1f) << 3));
        _raw[_writeOfs++] = (byte) (pktType | ((contentSize & 0x3f) << 2));
    }

    public int pushTCP(byte[] bytes, int pos, int len)
    {
        int usable = USB_PKT_SIZE - _writeOfs;
        if (usable <= USB_PKT_STREAM_HEAD_SIZE) {
            return 0;
        }
        usable -= USB_PKT_STREAM_HEAD_SIZE;
        if (len > usable) {
            len = usable;
        }
        writeStreamHead(0, YPKT_STREAM, YGenericHub.YSTREAM_TCP, len);
        if (len > 0) {
            System.arraycopy(bytes, pos, _raw, _writeOfs, len);
            _writeOfs += len;
        }
        return len;
    }


    public void pushTCPClose()
    {
        writeStreamHead(0, YPKT_STREAM, YGenericHub.YSTREAM_TCP_CLOSE, 0);
    }


    public void pushMetaUTC()
    {
        writeStreamHead(0, YPKT_STREAM, YGenericHub.YSTREAM_META, YGenericHub.USB_META_UTCTIME_SIZE);
        // Meta UTC packet are hand forged
        _raw[_writeOfs++] = USB_META_UTCTIME;
        long now = System.currentTimeMillis();
        long currUtcTime = now / 1000;
        _raw[_writeOfs++] = (byte) (currUtcTime & 0xff);
        _raw[_writeOfs++] = (byte) ((currUtcTime >> 8) & 0xff);
        _raw[_writeOfs++] = (byte) ((currUtcTime >> 16) & 0xff);
        _raw[_writeOfs++] = (byte) ((currUtcTime >> 24) & 0xff);
        _raw[_writeOfs++] = (byte) (((now % 1000) / 4) & 0xff);// 1/250 seconds
    }

    public void pushPktAck(int pktno)
    {
        writeStreamHead(0, YPKT_STREAM, YGenericHub.YSTREAM_META, 2);
        _raw[_writeOfs++] = USB_META_ACK_D2H_PACKET;
        _raw[_writeOfs++] = (byte) (pktno & 0xff);
    }

    public void push(ConfPktStart reset)
    {
        int contentSize = USB_PKT_SIZE - USB_PKT_STREAM_HEAD_SIZE;
        writeStreamHead(0, YPKT_CONF, USB_CONF_START, contentSize);
        reset.write(_raw, _writeOfs);
        _writeOfs += contentSize;
    }


    public void push(ConfPktReset reset)
    {
        int contentSize = USB_PKT_SIZE - USB_PKT_STREAM_HEAD_SIZE;
        writeStreamHead(0, YPKT_CONF, USB_CONF_RESET, contentSize);
        reset.write(_raw, _writeOfs);
        _writeOfs += contentSize;
    }


}
