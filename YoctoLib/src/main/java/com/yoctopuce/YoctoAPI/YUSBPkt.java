/**
 * ******************************************************************
 *
 * $Id: YUSBPkt.java 26580 2017-02-08 09:59:57Z seb $
 *
 * YUSBPkt Class: USB packet definitions
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
 *
 * *******************************************************************
 */

package com.yoctopuce.YoctoAPI;

import java.util.Locale;


abstract class YUSBPkt
{
    // generic pkt definitions
    protected static final int YPKT_USB_LEGACY_VERSION_BCD = 0x0207;
    static final int YPKT_USB_VERSION_BCD = 0x0208;
    public static final int USB_PKT_SIZE = 64;


    static final int USB_PKT_STREAM_HEAD_SIZE = 2;
    // pkt type definitions
    static final int YPKT_STREAM = 0;
    static final int YPKT_CONF = 1;
    // pkt config type
    static final int USB_CONF_RESET = 0;
    static final int USB_CONF_START = 1;


    int _streamCount = 0;
    YPktStreamHead[] _streams = new YPktStreamHead[32];
    byte[] _raw = new byte[USB_PKT_SIZE];

    YUSBPkt()
    {
    }

    public String toString()
    {
        String dump = String.format(Locale.US, "YUSBPkt with %d streams:", _streamCount);
        for (int i = 0; i < _streamCount && i < _streams.length; i++) {
            dump += "\n" + _streams[i].toString();
        }
        return dump;
    }

    public int getStreamCount()
    {
        return _streamCount;
    }

    public YPktStreamHead getStream(int i)
    {
        return _streams[i];
    }


    protected static class ConfPktReset
    {
        private int _api;
        private int _ok;
        private int _ifaceNo;
        private int _nbIface;

        public ConfPktReset(int api, int ok, int ifaceno, int nbiface)
        {
            this._api = api;
            this._ok = ok;
            this._ifaceNo = ifaceno;
            this._nbIface = nbiface;
        }

        public int getApi()
        {
            return _api;
        }

        public int getOk()
        {
            return _ok;
        }

        public int getIfaceNo()
        {
            return _ifaceNo;
        }

        public int getNbIface()
        {
            return _nbIface;
        }


        static public ConfPktReset Decode(YPktStreamHead data)
        {
            int api = data.getByte(0) + ((int) data.getByte(1) << 8);
            return new ConfPktReset(api, data.getByte(2), data.getByte(3), data.getByte(4));
        }

        public void write(byte[] data, int ofs)
        {
            data[ofs] = (byte) (_api & 0xff);
            data[ofs + 1] = (byte) ((_api >> 8) & 0xff);
            data[ofs + 2] = 1;
        }
    }

    protected static class ConfPktStart
    {

        private final int _nbIface;
        private final int _ack_delay;

        public ConfPktStart(int nbiface, int ack_delay)
        {
            _nbIface = nbiface;
            _ack_delay = ack_delay;
        }


        static public ConfPktStart Decode(YPktStreamHead data)
        {
            int nbiface = data.getByte(0) & 0xff;
            int ackDelay;
            if (data.getContentSize() >= 2) {
                ackDelay = data.getByte(1) & 0xff;
            } else {
                ackDelay = 0;
            }
            return new ConfPktStart(nbiface, ackDelay);
        }

        public void write(byte[] data, int ofs)
        {
            data[ofs] = (byte) _nbIface;
            data[ofs + 1] = (byte) _ack_delay;
        }

        public int getAckDelay()
        {
            return _ack_delay;
        }
    }

}
