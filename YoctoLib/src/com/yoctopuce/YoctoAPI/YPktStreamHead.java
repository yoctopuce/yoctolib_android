package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
* Created by seb on 02.01.14.
*/
class YPktStreamHead
{
    protected static final int USB_PKT_STREAM_HEAD=2;
    // pkt type definitions
    protected static final int YPKT_STREAM=0;
    protected static final int YPKT_CONF=1;
    // pkt config type
    protected static final int USB_CONF_RESET=0;
    protected static final int USB_CONF_START=1;
    // stream type
    protected static final int YSTREAM_EMPTY=0;
    protected static final int YSTREAM_TCP=1;
    protected static final int YSTREAM_TCP_CLOSE=2;
    protected static final int YSTREAM_NOTICE=3;
    protected static final int YSTREAM_REPORT=4;
    protected static final int YSTREAM_META=5;

    private int  _pktNumber;
    private int  _streamType;
    private int  _pktType;
    private byte[] _data;


    int getPktNumber() {
        return _pktNumber;
    }
    int getStreamType() {
        return _streamType;
    }
    int getPktType() {
        return _pktType;
    }
    int getContentSize() {
        return _data.length;
    }

    int getFullSize(){
        return _data.length+ USB_PKT_STREAM_HEAD;
    }

    byte getDataByte(int ofs)
    {
        return _data[ofs];
    }


    byte[] getDataAsByteArray()
    {
        return _data;
    }


    // content of the stream is initialised with data_size byte of the bb (starting at current position)
    // note : data_size bytes in bb are consumed
    public YPktStreamHead(int pktNumber, int pktType, int streamType, byte[] data, int offset, int len)
    {
        _pktNumber = pktNumber;
        _streamType = streamType;
        _pktType = pktType;
        if(data==null){
            _data=new byte[0];
        } else {
            _data = Arrays.copyOfRange(data,offset, offset+len);
        }
    }


    public YPktStreamHead(int pktNumber, int pktType, int streamType, ByteBuffer pkt, int dataLen) {
        _pktNumber = pktNumber;
        _streamType = streamType;
        _pktType = pktType;
        _data = new byte[dataLen];
        pkt.get(_data);
    }



    @Override
    public String toString() {
        String type,stream;
        switch(_pktType){
        case YPKT_CONF:
            type = "CONF";
            switch(_streamType){
            case USB_CONF_RESET:
                stream="RESET";
                break;
            case USB_CONF_START:
                stream = "START";
                break;
            default:
                stream = "INVALID!";
                break;
            }
            break;
        case YPKT_STREAM:
            type = "STREAM";
            switch(_streamType){
            case YSTREAM_EMPTY:
                stream= "EMPTY";
                break;
            case YSTREAM_NOTICE:
                stream="NOTICE ";
                break;
            case YSTREAM_TCP:
                stream="TCP";
                break;
            case YSTREAM_TCP_CLOSE:
                stream="TCP_CLOSE";
                break;
            case YSTREAM_REPORT:
                stream="REPORT";
                break;
            case YSTREAM_META:
                stream="META";
                break;
            default:
                stream = "INVALID!";
                break;
            }
            break;
        default:
            type = "INVALID!";
            stream = "INVALID!";
            break;
        }
        return String.format("Stream: type=%d(%s) stream/cmd=%d(%s) size=%d (pktno=%d)\n",
                   this._pktType,type,this._streamType,stream,this._data.length,this._pktNumber);
    }

    //decode
    public static YPktStreamHead Decode(ByteBuffer pkt) throws YAPI_Exception {
        if (pkt.remaining() < USB_PKT_STREAM_HEAD ){
            return null;
        }
        int b = pkt.get() & 0xff;
        int pktNumber = b & 7;
        int streamType = (b >> 3);
        b = pkt.get() & 0xff;
        int pktType = b & 3;
        int dataLen = b >> 2;
        if (dataLen > pkt.remaining()){
            throw new YAPI_Exception(YAPI.IO_ERROR,String.format("invalid ystream header (invalid length %d>%d)",dataLen,pkt.remaining()));
        }
        return  new YPktStreamHead(pktNumber, pktType, streamType, pkt, dataLen);
    }



    public int getRawStream(byte[] res, int pos) {
        res[pos++] = (byte)((_pktNumber&7) | (( _streamType & 0x1f ) <<3));
        res[pos++] = (byte) (_pktType | ((_data.length & 0x3f ) <<2));
        if(_data.length>0) {
            System.arraycopy(_data, 0, res, pos, _data.length);
        }
        return _data.length + USB_PKT_STREAM_HEAD;
    }

    public static void PadWithEmpty(byte[] res, int pos, int usbPktSize) {
        int remaining = usbPktSize - pos -USB_PKT_STREAM_HEAD;
        if(remaining >= 0) {
            res[pos++] = (byte)( ( YSTREAM_EMPTY & 0x1f ) << 3);
            res[pos++] = (byte) (YPKT_STREAM | ((remaining & 0x3f ) <<2));
        }
    }

    public boolean isConfPktReset() {
        return (_pktType == YPKT_CONF && _streamType == USB_CONF_RESET );
    }

    public boolean isConfPktStart() {
        return (_pktType == YPKT_CONF && _streamType == USB_CONF_START );
    }


    NotificationStreams decodeAsNotification(YUSBDevice dev) throws YAPI_Exception {
        try {
        return new NotificationStreams(dev, _data);
        }catch (ArrayIndexOutOfBoundsException ex) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "Invlalid USB packet");
        }
    }

    static class NotificationStreams {

        //notifications type
        protected static final int NOTIFY_1STBYTE_MAXTINY = 63;
        protected static final int NOTIFY_1STBYTE_MINSMALL = 128;
        protected static final int NOTIFY_PKT_NAME = 0;
        protected static final int NOTIFY_PKT_PRODNAME = 1;
        protected static final int NOTIFY_PKT_CHILD = 2;
        protected static final int NOTIFY_PKT_FIRMWARE = 3;
        protected static final int NOTIFY_PKT_FUNCNAME = 4;
        protected static final int NOTIFY_PKT_FUNCVAL = 5;
        protected static final int NOTIFY_PKT_STREAMREADY = 6;
        protected static final int NOTIFY_PKT_LOG = 7;
        protected static final int NOTIFY_PKT_FUNCNAMEYDX = 8;



        public enum NotType{
            NAME,PRODNAME,CHILD,FIRMWARE,FUNCNAME,FUNCVAL,STREAMREADY,LOG,FUNCNAMEYDX
        }


        private final NotType _notType;
        private final String _serial;
        private String _functionId;
        private String _funcval;
        private String _logicalname;
        private byte _beacon;
        private String _product;
        private String _childserial;
        private String _firmware;
        private byte _onOff;
        private byte _devydy;
        private int _vendorid;
        private int _deviceid;
        private String _funcname;
        private byte _funydx;
        private byte _funclass;


        static String arrayToString(byte[] data, int ofs,int maxlen)
        {
            if(data==null)
                return "";
            int pos=ofs;
            int len=0;
            while(len < maxlen &&  ofs + len < data.length){
                if(data[pos + len]==0)
                    break;
                len++;
            }
            return new String(data, pos, len);
        }

        public YPEntry.BaseClass getFunclass()
        {
            if(_funclass >= YPEntry.BaseClass.values().length) {
                // Unknown subclass, use YFunction instead
                return YPEntry.BaseClass.Function;
            }
            return YPEntry.BaseClass.forByte(_funclass);

        }

        public NotificationStreams(YUSBDevice dev, byte[] data) throws YAPI_Exception {
            int firstByte = data[0];
            if(firstByte <= NOTIFY_1STBYTE_MAXTINY) {
                _notType = NotType.FUNCVAL;
                _serial = dev.getSerial();
                _functionId = dev.getFuncidFromYdx(_serial, firstByte);
                if (_functionId == null)
                    throw new YAPI_Exception(YAPI.IO_ERROR, "too early tiny notification");
                _funcval = new String(data, 1, data.length - 1);
            }else if (firstByte >= NOTIFY_1STBYTE_MINSMALL) {
                _notType = NotType.FUNCVAL;
                _serial = dev.getSerialFromYdx(data[1]);
                _functionId = dev.getFuncidFromYdx(_serial, firstByte - NOTIFY_1STBYTE_MINSMALL);
                if (_functionId == null || _serial ==null)
                    throw new YAPI_Exception(YAPI.IO_ERROR, "too early small notification");
                _funcval = new String(data, 2, data.length - 2);
            }else {
                _serial = arrayToString(data,0,YAPI.YOCTO_SERIAL_LEN);
                int p = YAPI.YOCTO_SERIAL_LEN;
                int type = data[p++];
                switch (type) {
                    case NOTIFY_PKT_NAME:
                        _notType = NotType.NAME;
                        _logicalname = arrayToString(data, p, YAPI.YOCTO_LOGICAL_LEN);
                        _beacon = data[p+YAPI.YOCTO_LOGICAL_LEN];
                        break;
                    case NOTIFY_PKT_PRODNAME:
                        _notType = NotType.PRODNAME;
                        _product = arrayToString(data, p, YAPI.YOCTO_PRODUCTNAME_LEN);
                        break;
                    case NOTIFY_PKT_CHILD:
                        _notType = NotType.CHILD;
                        _childserial = arrayToString(data, p, YAPI.YOCTO_SERIAL_LEN);
                        p += YAPI.YOCTO_SERIAL_LEN;
                        _onOff = data[p++];
                        _devydy = data[p];
                        break;
                    case NOTIFY_PKT_FIRMWARE:
                        _notType = NotType.FIRMWARE;
                        _firmware = arrayToString(data, p, YAPI.YOCTO_FIRMWARE_LEN);
                        p += YAPI.YOCTO_FIRMWARE_LEN;
                        _vendorid = data[p] + (data[p + 1]<<8);
                        p += 2;
                        _deviceid = data[p] + (data[p + 1]<<8);
                        break;
                    case NOTIFY_PKT_FUNCNAME:
                        _notType = NotType.FUNCNAME;
                        _functionId = arrayToString(data, p, YAPI.YOCTO_FUNCTION_LEN);
                        p += YAPI.YOCTO_FUNCTION_LEN;
                        _funcname = arrayToString(data, p, YAPI.YOCTO_LOGICAL_LEN);
                        break;
                    case NOTIFY_PKT_FUNCVAL:
                        _notType = NotType.FUNCVAL;
                        _functionId = arrayToString(data, p, YAPI.YOCTO_FUNCTION_LEN);
                        p += YAPI.YOCTO_FUNCTION_LEN;
                        _funcval = arrayToString(data, p, YAPI.YOCTO_PUBVAL_SIZE);
                        break;
                    case NOTIFY_PKT_STREAMREADY:
                        _notType = NotType.STREAMREADY;
                        break;
                    case NOTIFY_PKT_LOG:
                        _notType = NotType.LOG;
                        break;
                    case NOTIFY_PKT_FUNCNAMEYDX:
                        _notType = NotType.FUNCNAMEYDX;
                        _functionId = arrayToString(data, p, YAPI.YOCTO_FUNCTION_LEN-1);
                        p += YAPI.YOCTO_FUNCTION_LEN-1;
                        _funclass = data[p++];
                        _funcname = arrayToString(data, p, YAPI.YOCTO_LOGICAL_LEN);
                        p += YAPI.YOCTO_LOGICAL_LEN;
                        _funydx = data[p];
                        break;
                    default:
                        throw new YAPI_Exception(YAPI.IO_ERROR, "Invalid Notification");
                }
            }
        }

        public NotType getNotType() {
            return _notType;
        }

        public String getSerial() {
            return _serial;
        }

        public String getFuncval() {
            return _funcval;
        }

        public String getLogicalname() {
            return _logicalname;
        }

        public byte getBeacon() {
            return _beacon;
        }

        public String getProduct() {
            return _product;
        }

        public String getChildserial() {
            return _childserial;
        }

        public byte getDevydy() {
            return _devydy;
        }

        public int getDeviceid() {
            return _deviceid;
        }

        public String getFuncname() {
            return _funcname;
        }

        public byte getFunydx() {
            return _funydx;
        }

        public String getFunctionId() {
            return _functionId;
        }

        public String getHardwareId() {
            return _serial + "." + _functionId;
        }

    }




}
