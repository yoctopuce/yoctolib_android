package com.yoctopuce.YoctoAPI;


import java.io.ByteArrayOutputStream;
import java.util.Locale;


class YPktStreamHead
{
    private int _pktNumber;
    private int _streamType;
    private int _pktType;
    private int _contentSize;
    private byte[] _content;
    private int _contentOfs;


    int getPktNumber()
    {
        return _pktNumber;
    }

    int getStreamType()
    {
        return _streamType;
    }

    int getPktType()
    {
        return _pktType;
    }

    int getContentSize()
    {
        return _contentSize;
    }

    @Override
    public String toString()
    {
        String type, stream;
        switch (_pktType) {
            case YUSBPkt.YPKT_CONF:
                type = "CONF";
                switch (_streamType) {
                    case YUSBPkt.USB_CONF_RESET:
                        stream = "RESET";
                        break;
                    case YUSBPkt.USB_CONF_START:
                        stream = "START";
                        break;
                    default:
                        stream = "INVALID!";
                        break;
                }
                break;
            case YUSBPkt.YPKT_STREAM:
                type = "STREAM";
                switch (_streamType) {
                    case YGenericHub.YSTREAM_EMPTY:
                        stream = "EMPTY";
                        break;
                    case YGenericHub.YSTREAM_NOTICE:
                        stream = "NOTICE ";
                        break;
                    case YGenericHub.YSTREAM_TCP:
                        stream = "TCP";
                        break;
                    case YGenericHub.YSTREAM_TCP_CLOSE:
                        stream = "TCP_CLOSE";
                        break;
                    case YGenericHub.YSTREAM_REPORT:
                        stream = "REPORT";
                        break;
                    case YGenericHub.YSTREAM_META:
                        stream = "META";
                        break;
                    case YGenericHub.YSTREAM_REPORT_V2:
                        stream = "REPORT_V2";
                        break;
                    case YGenericHub.YSTREAM_NOTICE_V2:
                        stream = "NOTICE_v2 ";
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
        return String.format(Locale.US, "Stream: type=%d(%s) stream/cmd=%d(%s) size=%d (pktno=%d)\n",
                this._pktType, type, this._streamType, stream, this._contentSize, this._pktNumber);
    }




    NotificationStreams decodeAsNotification(String serial, boolean isV2) throws YAPI_Exception
    {
        try {
            return new NotificationStreams(serial, _content, _contentOfs, _contentSize, isV2);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "Invalid USB packet");
        }
    }

    public void update(int pktNumber, int pktType, int streamType, byte[] raw, int ofs, int dataLen)
    {
        _pktNumber = pktNumber;
        _pktType = pktType;
        _streamType = streamType;
        _content = raw;
        _contentOfs = ofs;
        _contentSize = dataLen;
    }

    public void writeTo(ByteArrayOutputStream outputStream)
    {
        outputStream.write(_content, _contentOfs, _contentSize);
    }

    public byte getByte(int i)
    {
        return _content[_contentOfs + i];
    }



    /**
     * Created by seb on 27.02.2015.
     */
    static class NotificationStreams
    {

        //notifications type
        private static final int NOTIFY_1STBYTE_MAXTINY = 63;
        private static final int NOTIFY_1STBYTE_MINSMALL = 128;

        private static final int NOTIFY_V2_FUNYDX_MASK = 0xF;
        private static final int NOTIFY_V2_TYPE_MASK = 0X3;
        private static final int NOTIFY_V2_TYPE_OFS = 4;
        private static final int NOTIFY_V2_IS_SMALL_FLAG = 0x80;


        protected static final int NOTIFY_PKT_NAME = 0;
        protected static final int NOTIFY_PKT_PRODNAME = 1;
        protected static final int NOTIFY_PKT_CHILD = 2;
        protected static final int NOTIFY_PKT_FIRMWARE = 3;
        protected static final int NOTIFY_PKT_FUNCNAME = 4;
        protected static final int NOTIFY_PKT_FUNCVAL = 5;
        protected static final int NOTIFY_PKT_STREAMREADY = 6;
        protected static final int NOTIFY_PKT_LOG = 7;
        protected static final int NOTIFY_PKT_FUNCNAMEYDX = 8;


        public enum NotType
        {
            NAME, PRODNAME, CHILD, FIRMWARE, FUNCNAME, FUNCVAL, FUNCVAL_TINY, FUNCVALFLUSH, STREAMREADY, LOG, FUNCNAMEYDX
        }

        private final NotType _notType;
        private final String _serial;
        private String _functionId;
        private String _funcval;
        private int _funcvalType;
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
        private int _funydx;
        private byte _funclass;


        static String arrayToString(byte[] data, int ofs, int maxlen)
        {
            if (data == null)
                return "";
            int len = 0;
            while (len < maxlen && ofs + len < data.length) {
                if (data[ofs + len] == 0)
                    break;
                len++;
            }
            return new String(data, ofs, len);
        }

        public YPEntry.BaseClass getFunclass()
        {
            if (_funclass >= YPEntry.BaseClass.values().length) {
                // Unknown subclass, use YFunction instead
                return YPEntry.BaseClass.Function;
            }
            return YPEntry.BaseClass.forByte(_funclass);

        }

        public NotificationStreams(String serial, byte[] data, int ofs, int size, boolean isV2) throws YAPI_Exception
        {
            int firstByte = data[ofs];
            if (isV2 || firstByte <= NOTIFY_1STBYTE_MAXTINY || firstByte >= NOTIFY_1STBYTE_MINSMALL) {
                _funcvalType = (firstByte >> NOTIFY_V2_TYPE_OFS) & NOTIFY_V2_TYPE_MASK;
                _serial = serial;
                _funydx = firstByte & NOTIFY_V2_FUNYDX_MASK;
                if (_funcvalType == YGenericHub.NOTIFY_V2_FLUSHGROUP) {
                    _notType = NotType.FUNCVALFLUSH;
                } else {
                    _notType = NotType.FUNCVAL_TINY;
                    if ((firstByte & NOTIFY_V2_IS_SMALL_FLAG) != 0) {
                        // added on 2015-02-25, remove code below when confirmed dead code
                        throw new YAPI_Exception(YAPI.IO_ERROR, "Hub Should not fwd notification");
                    }
                    _funcval = YGenericHub.decodePubVal(_funcvalType, data, ofs + 1, size - 1);
                }
            } else {
                _serial = arrayToString(data, ofs, YAPI.YOCTO_SERIAL_LEN);
                int p = ofs + YAPI.YOCTO_SERIAL_LEN;
                int type = data[p++];
                switch (type) {
                    case NOTIFY_PKT_NAME:
                        _notType = NotType.NAME;
                        _logicalname = arrayToString(data, p, YAPI.YOCTO_LOGICAL_LEN);
                        _beacon = data[p + YAPI.YOCTO_LOGICAL_LEN];
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
                        _vendorid = data[p] + (data[p + 1] << 8);
                        p += 2;
                        _deviceid = data[p] + (data[p + 1] << 8);
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
                        _functionId = arrayToString(data, p, YAPI.YOCTO_FUNCTION_LEN - 1);
                        p += YAPI.YOCTO_FUNCTION_LEN - 1;
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

        public NotType getNotType()
        {
            return _notType;
        }

        public String getSerial()
        {
            return _serial;
        }

        public String getFuncval()
        {
            return _funcval;
        }

        public String getLogicalname()
        {
            return _logicalname;
        }

        public byte getBeacon()
        {
            return _beacon;
        }

        public String getProduct()
        {
            return _product;
        }

        public byte getDevydy()
        {
            return _devydy;
        }

        public int getDeviceid()
        {
            return _deviceid;
        }

        public String getFuncname()
        {
            return _funcname;
        }

        public int getFunydx()
        {
            return _funydx;
        }

        public String getFunctionId()
        {
            return _functionId;
        }

        public String getHardwareId()
        {
            return _serial + "." + _functionId;
        }

    }
}
