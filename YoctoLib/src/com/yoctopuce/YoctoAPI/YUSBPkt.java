/*********************************************************************
 *
 * $Id: YUSBPkt.java 14929 2014-02-12 17:55:52Z seb $
 *
 * YUSBPkt Class: USB packet definitions
 *
 * - - - - - - - - - License information: - - - - - - - - -
 *
 *  Copyright (C) 2011 and beyond by Yoctopuce Sarl, Switzerland.
 *
 *  Yoctopuce Sarl (hereafter Licensor) grants to you a perpetual
 *  non-exclusive license to use, modify, copy and integrate this
 *  file into your software for the sole purpose of interfacing 
 *  with Yoctopuce products. 
 *
 *  You may reproduce and distribute copies of this file in 
 *  source or object form, as long as the sole purpose of this
 *  code is to interface with Yoctopuce products. You must retain 
 *  this notice in the distributed source file.
 *
 *  You should refer to Yoctopuce General Terms and Conditions
 *  for additional information regarding your rights and 
 *  obligations.
 *
 *  THE SOFTWARE AND DOCUMENTATION ARE PROVIDED 'AS IS' WITHOUT
 *  WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING 
 *  WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, FITNESS 
 *  FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO
 *  EVENT SHALL LICENSOR BE LIABLE FOR ANY INCIDENTAL, SPECIAL,
 *  INDIRECT OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, 
 *  COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY OR 
 *  SERVICES, ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT 
 *  LIMITED TO ANY DEFENSE THEREOF), ANY CLAIMS FOR INDEMNITY OR
 *  CONTRIBUTION, OR OTHER SIMILAR COSTS, WHETHER ASSERTED ON THE
 *  BASIS OF CONTRACT, TORT (INCLUDING NEGLIGENCE), BREACH OF
 *  WARRANTY, OR OTHERWISE.
 *
 *********************************************************************/

package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;

import static com.yoctopuce.YoctoAPI.YAPI.SafeYAPI;


public class YUSBPkt  {
	

	// generic pkt definitions
    protected static final int YPKT_USB_VERSION_BCD=0x0206;
    public static final int USB_PKT_SIZE=64;
    private final YUSBDevice _dev;
    protected int _pktno = 0;
	protected ArrayList<YPktStreamHead> _streams;


    YUSBPkt(YUSBDevice dev, int pktno, ArrayList<YPktStreamHead>streams)
    {
        _dev = dev;
        _streams = streams;
        _pktno = pktno;
    }


    int getPktno() {
        return _pktno;
    }

    public ArrayList<YPktStreamHead> getStreams() {
        return _streams;
    }

    protected static boolean isCompatibe(int version,String serial) throws YAPI_Exception
	{

	    if((version & 0xff00) != (YPKT_USB_VERSION_BCD & 0xff00)){
	        // major version change
	        if((version & 0xff00) > (YPKT_USB_VERSION_BCD & 0xff00)){
                SafeYAPI()._Log(String.format("Yoctopuce library is too old (using 0x%x need 0x%x) to handle device %s, please upgrade your Yoctopuce library\n", YPKT_USB_VERSION_BCD, version, serial));
	            throw new YAPI_Exception(YAPI.IO_ERROR,"Library is too old to handle this device");
	        } else {
	            // implement backward compatibility when implementing a new protocol
	            throw new YAPI_Exception(YAPI.IO_ERROR,"implement backward compatibility when implementing a new protocol");
	        }
	    } else if(version  != YPKT_USB_VERSION_BCD ){
	        if(version > YPKT_USB_VERSION_BCD){
                SafeYAPI()._Log(String.format("Device %s is using an newer protocol, consider upgrading your Yoctopuce library\n",serial));
	        }else{
                SafeYAPI()._Log(String.format("Device %s is using an older protocol, consider upgrading the device firmware\n",serial));
	        }
	        return false;
	    }
	    return true;
	}

    public String toString() {
        String dump = String.format("pktno:%d with %d ystream",_pktno, _streams.size());
        for (YPktStreamHead s :_streams) {
            dump += "\n" + s.toString();
        }
        return dump;
    }


    protected static class ConfPktReset
	{
        private int _api;
        private int _ok;
        private int _ifaceNo;
        private int _nbIface;

        public ConfPktReset(int api, int ok, int ifaceno, int nbiface) {
            this._api = api;
            this._ok = ok;
            this._ifaceNo = ifaceno;
            this._nbIface = nbiface;
        }

        public int getApi() {
            return _api;
        }

        public int getOk() {
            return _ok;
        }

        public int getIfaceNo() {
            return _ifaceNo;
        }

        public int getNbIface() {
            return _nbIface;
        }

        static public ConfPktReset Decode(byte[] data)
		{
            int api = data[0] + ((int) data[1] << 8);
            return new ConfPktReset(api, data[2],data[3],data[4]);
		}

        public YPktStreamHead getAsStream()
        {
            byte[] data = new byte[USB_PKT_SIZE - YPktStreamHead.USB_PKT_STREAM_HEAD];
            data[0] = (byte) (_api & 0xff);
            data[1] = (byte) ((_api >> 8) & 0xff);
            data[2] = 1;
            return new YPktStreamHead(0, YPktStreamHead.YPKT_CONF,YPktStreamHead.USB_CONF_RESET, data,0,data.length);
        }
	}
	
	protected static class ConfPktStart
	{
        private int _nbIface;

        public ConfPktStart(int nbiface) {
            this._nbIface = nbiface;
        }

        public int getNbIface() {
            return _nbIface;
        }

        static public ConfPktStart Decode(byte[] data)
        {
            return new ConfPktStart(data[0]);
        }

        public YPktStreamHead getAsStream()
        {
            byte[] data = new byte[USB_PKT_SIZE - YPktStreamHead.USB_PKT_STREAM_HEAD];
            data[0] = (byte) _nbIface;
            return new YPktStreamHead(0, YPktStreamHead.YPKT_CONF,YPktStreamHead.USB_CONF_START, data,0,data.length);
        }

    }

}
