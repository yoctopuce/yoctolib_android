package com.yoctopuce.YoctoAPI;

import java.util.LinkedList;

import static com.yoctopuce.YoctoAPI.YAPI.SafeYAPI;


public class YUSBPktOut extends YUSBPkt {

    private static final int USB_META_UTCTIME = 1;
    private static final int USB_META_DLFLUSH = 2;
    private static final int USB_META_ACK_D2H_PACKET = 3;

    YUSBPktOut() {
        super(0, new LinkedList<YPktStreamHead>());
    }

    public byte[] getRawPkt() throws YAPI_Exception
    {
        byte[] res = new byte[USB_PKT_SIZE];
        int pos = 0;
        for (YPktStreamHead s : _streams) {
            pos += s.getRawStream(res, pos);
        }
        YPktStreamHead.PadWithEmpty(res, pos, USB_PKT_SIZE);
        return res;
    }


    private void addStream(YPktStreamHead stream) {
        if (getFreeSize() < stream.getFullSize()) {
            SafeYAPI()._Log("USB Out packet overflow");
            return;
        }
        _streams.add(stream);
    }

    private int getFreeSize() {
        int used = 0;
        for (YPktStreamHead s : _streams) {
            used += s.getFullSize();
        }
        return USB_PKT_SIZE - used;
    }

    public int pushTCP(byte[] bytes, int pos, int len) throws YAPI_Exception {
        int avail = getFreeSize() - YPktStreamHead.USB_PKT_STREAM_HEAD;
        if (avail <= 0)
            return 0;

        if (avail < len) {
            len = avail;
        }
        YPktStreamHead s = new YPktStreamHead(0, YPktStreamHead.YPKT_STREAM, YPktStreamHead.YSTREAM_TCP, bytes, pos, len);
        addStream(s);
        return len;
    }

    public void pushTCPClose() throws YAPI_Exception {
        YPktStreamHead s = new YPktStreamHead(0, YPktStreamHead.YPKT_STREAM, YPktStreamHead.YSTREAM_TCP_CLOSE, null, 0, 0);
        addStream(s);
    }


    public void pushMetaUTC() {
        // Meta UTC packet are hand forged 
        byte[] pktdata = new byte[5];
        pktdata[0] = USB_META_UTCTIME;
        long currUtcTime = System.currentTimeMillis() / 1000;
        pktdata[1] = (byte) (currUtcTime & 0xff);
        pktdata[2] = (byte) ((currUtcTime >> 8) & 0xff);
        pktdata[3] = (byte) ((currUtcTime >> 16) & 0xff);
        pktdata[4] = (byte) ((currUtcTime >> 24) & 0xff);
        YPktStreamHead s = new YPktStreamHead(0, YPktStreamHead.YPKT_STREAM, YPktStreamHead.YSTREAM_META, pktdata, 0, pktdata.length);
        addStream(s);
    }

    public void pushPktAck(int pktno) {
        byte[] pktdata = new byte[2];
        pktdata[0] = USB_META_ACK_D2H_PACKET;
        pktdata[1] = (byte) (pktno & 0xff);
        YPktStreamHead s = new YPktStreamHead(0, YPktStreamHead.YPKT_STREAM, YPktStreamHead.YSTREAM_META, pktdata, 0, pktdata.length);
        addStream(s);
    }


    public static YUSBPktOut ResetPkt() {
        ConfPktReset reset = new ConfPktReset(YPKT_USB_VERSION_BCD, 1, 0, 0);
        YUSBPktOut pkt = new YUSBPktOut();
        pkt.addStream(reset.getAsStream());
        return pkt;
    }

    public static YUSBPktOut StartPkt(int pktAckDelay) {
        ConfPktStart start = new ConfPktStart(1, pktAckDelay);
        YUSBPktOut pkt = new YUSBPktOut();
        pkt.addStream(start.getAsStream());
        return pkt;
    }

    public static YUSBPktOut AckPkt(int pktno) {
        YUSBPktOut pkt = new YUSBPktOut();
        pkt.pushPktAck(pktno);
        return pkt;
    }
}
