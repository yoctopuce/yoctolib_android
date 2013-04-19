package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class YUSBPkt  {
	

	// generic pkt definitions
	public static final int USB_PKT_SIZE=64;
	protected static final int USB_PKT_STREAM_HEAD=2;
	protected static final int YPKT_USB_VERSION_BCD=0x0205;
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
	protected int      		_pktno	= 0;
	protected ArrayList<StreamHead> _streams = new ArrayList<YUSBPkt.StreamHead>();
	
	

	protected static boolean isCompatibe(int version,String serial) throws YAPI_Exception
	{

	    if((version & 0xff00) != (YPKT_USB_VERSION_BCD & 0xff00)){
	        // major version change
	        if((version & 0xff00) > (YPKT_USB_VERSION_BCD & 0xff00)){
	            YAPI.Log(String.format("Yoctopuce library is too old (using 0x%x need 0x%x) to handle device %s, please upgrade your Yoctopuce library\n",YPKT_USB_VERSION_BCD,version,serial));
	            throw new YAPI_Exception(YAPI.IO_ERROR,"Library is too old to handle this device");
	        } else {
	            // implement backward compatibility when implementing a new protocol
	            throw new YAPI_Exception(YAPI.IO_ERROR,"implement backward compatibility when implementing a new protocol");
	        }
	    } else if(version  != YPKT_USB_VERSION_BCD ){
	        if(version > YPKT_USB_VERSION_BCD){
	        	YAPI.Log(String.format("Device %s is using an newer protocol, consider upgrading your Yoctopuce library\n",serial));
	        }else{
	        	YAPI.Log(String.format("Device %s is using an older protocol, consider upgrading the device firmware\n",serial));
	        }
	        return false;
	    }
	    return true;
	}

	
	
	protected static class StreamHead
	{
	    private int  _pktNumber;
	    private int  _streamType;
	    private int  _pktType;
	    private byte[] _data;
	    
	    int getPktNumber() {
			return _pktNumber;
		}
		void setPktNumber(int pktNumber) {
			this._pktNumber = pktNumber;
		}
		int getStreamType() {
			return _streamType;
		}
		void setStreamType(int streamType) {
			this._streamType = streamType;
		}
		int getPktType() {
			return _pktType;
		}
		void setPktType(int pktType) {
			this._pktType = pktType;
		}
		int getContentSize() {
			return _data.length;
		}
		
		int getSize(){
		    return _data.length+USB_PKT_STREAM_HEAD;
		}
		
		byte getData(int ofs)
		{
			if(_data==null)
				return (byte) 0xff;
			return _data[ofs];
		}
		
		String getNullTerminedString(int ofs,int maxlen)
		{
			if(_data==null)
				return "";
			int pos=ofs;
			int len=0;
			while(len<maxlen){
				if(_data[pos+len]==0)
					break;
				len++;
			}
			return new String(_data,pos,len);
		}
		
		String getDataAsString_dtc()
		{
		    byte[] data =this.getDataAsByteArray();
		    return new String(data);//FIXME : handle encoding
		}

	    byte[] getDataAsByteArray()
        {
            return _data;
        }

	    
	    //buffer must point to the begining of the contenent of the stream
		public StreamHead(int pktNumber, int pktType, int streamType, int size,ByteBuffer buffer) 
		{
        
			this._pktNumber = pktNumber;
			this._streamType = streamType;
			this._pktType = pktType;
		    if(buffer==null){
			    this._data=new byte[0];
			} else {
    			this._data = new byte[size];
    			buffer.get(this._data);
			}
		}
		
		public StreamHead(int pktNumber, int pktType, int streamType, int size) 
		{
			this._pktNumber = pktNumber;
			this._streamType = streamType;
			this._pktType = pktType;
            this._data = new byte[size];
		}

		
		public StreamHead(ByteBuffer pkt) {
			super();
			int b = pkt.get()&0xff;			
			this._pktNumber = b&7;
			this._streamType = (b>>3);
			b = pkt.get()&0xff;			
			this._pktType = b&3;
            this._data = new byte[b>>2];
            pkt.get(this._data);
		}
		
	

	    public int popFromPkt(byte[] res, int pos)
        {
	        res[pos++] = (byte)((_pktNumber&7) | (( _streamType & 0x1f ) <<3));
	        res[pos++] = (byte) (_pktType | ((_data.length & 0x3f ) <<2));
            if(_data.length>0) {
                System.arraycopy(_data, 0, res, pos, _data.length);
            }
            return _data.length+USB_PKT_STREAM_HEAD;
        }
		
		String dump()
		{
			return String.format("Stream: type=%d stream/cmd=%d size=%d (pktno=%d)\n",
			           this._pktType,this._streamType,this._data.length,this._pktNumber);
		}
    
	}
	
	protected static class ConfPktResetDecoder
	{
		public ConfPktResetDecoder(StreamHead s)
		{
			api= s.getData(0) + ((int)s.getData(1)<<8);
			ok = s.getData(2);
			ifaceno = s.getData(3);
			nbiface = s.getData(4);
		}
		public int api;
		public int ok;
		public int ifaceno;
		public int nbiface;
	}
	
	protected static class ConfPktStartDecoder
	{
		public ConfPktStartDecoder(StreamHead s)
		{
			nbiface = s.getData(0);
		}

		public int nbiface;
		
	}

	protected static class NotificationDecoder
	{
		//notifications type
		protected static final int NOTIFY_1STBYTE_MAXTINY=63  ;
		protected static final int NOTIFY_1STBYTE_MINSMALL=128;
		protected static final int NOTIFY_PKT_NAME=0;
		protected static final int NOTIFY_PKT_PRODNAME=1;
		protected static final int NOTIFY_PKT_CHILD=2;
		protected static final int NOTIFY_PKT_FIRMWARE=3;
		protected static final int NOTIFY_PKT_FUNCNAME=4;
		protected static final int NOTIFY_PKT_FUNCVAL=5;
		protected static final int NOTIFY_PKT_STREAMREADY=6;
		protected static final int NOTIFY_PKT_LOG=7;
		protected static final int NOTIFY_PKT_FUNCNAMEYDX=8;

		public enum NotType{
			NAME,PRODNAME,CHILD,FIRMWARE,FUNCNAME,FUNCVAL,STREAMREADY,LOG,FUNCNAMEYDX
		}
		
		
		private NotType notType;
		private String  serial=null;
		private String  logicalname=null;
		private int     beacon;
		private String  product=null;
		private String  childserial=null;
		private int     onOff;
		private int     devydy;
		private String  firmware=null;
		private int     vendorid;
		private int     deviceid;
		private String  funcid=null;
		private String  funcname=null;
		private String  funcval=null;
		private int     funydx;

		public NotificationDecoder(StreamHead s,YUSBDevice dev) throws YAPI_Exception
		{
			int firstByte = s.getData(0);
		    if(firstByte <= NOTIFY_1STBYTE_MAXTINY) {
		    	notType =NotType.FUNCVAL;
		    	serial=dev.getSerial();
		    	funcid= dev.getFuncidFromYdx(serial,firstByte);
		    	funcval=s.getNullTerminedString(1,s.getContentSize()-1);
		    }else if (firstByte >= NOTIFY_1STBYTE_MINSMALL) {
		    	notType = NotType.FUNCVAL;
		    	serial = dev.getSerialFromYdx(s.getData(1));
		    	funcid= dev.getFuncidFromYdx(serial,firstByte-NOTIFY_1STBYTE_MINSMALL);
		    	funcval=s.getNullTerminedString(2,s.getContentSize()-2);		    	
		    }else {
		    	serial = s.getNullTerminedString(0,	YAPI.YOCTO_SERIAL_LEN);
		    	int p=YAPI.YOCTO_SERIAL_LEN+1;
		    	switch(s.getData(YAPI.YOCTO_SERIAL_LEN)){
				case NOTIFY_PKT_NAME:
					notType=NotType.NAME;
					logicalname= s.getNullTerminedString(p, YAPI.YOCTO_LOGICAL_LEN);
					p+= YAPI.YOCTO_LOGICAL_LEN;
					beacon = s.getData(p);
					break;
				case NOTIFY_PKT_PRODNAME:
					notType=NotType.PRODNAME;
					product= s.getNullTerminedString(p, YAPI.YOCTO_PRODUCTNAME_LEN);
					break;
				case NOTIFY_PKT_CHILD:
					notType=NotType.CHILD;
					childserial=s.getNullTerminedString(p,	YAPI.YOCTO_SERIAL_LEN);
					p+=YAPI.YOCTO_SERIAL_LEN;
					onOff =s.getData(p++);
					devydy =s.getData(p);
					break;
				case NOTIFY_PKT_FIRMWARE:
					notType=NotType.FIRMWARE;
					firmware=s.getNullTerminedString(p,	YAPI.YOCTO_FIRMWARE_LEN);
					p+=YAPI.YOCTO_FIRMWARE_LEN;
					vendorid =s.getData(p) + (s.getData(p+1)<<8);
					p+=2;
					deviceid =s.getData(p) + (s.getData(p+1)<<8);
					break;
				case NOTIFY_PKT_FUNCNAME:
					notType=NotType.FUNCNAME;
					funcid=s.getNullTerminedString(p,	YAPI.YOCTO_FUNCTION_LEN);
					p+=YAPI.YOCTO_FUNCTION_LEN;
					funcname=s.getNullTerminedString(p,	YAPI.YOCTO_LOGICAL_LEN);
					break;
				case NOTIFY_PKT_FUNCVAL:
					notType=NotType.FUNCVAL;
					funcid=s.getNullTerminedString(p,	YAPI.YOCTO_FUNCTION_LEN);
					p+=YAPI.YOCTO_FUNCTION_LEN;
					funcval=s.getNullTerminedString(p,	YAPI.YOCTO_PUBVAL_SIZE);
					break;
				case NOTIFY_PKT_STREAMREADY:
					notType=NotType.STREAMREADY;
					break;
				case NOTIFY_PKT_LOG:
					notType=NotType.LOG;
					break;
				case NOTIFY_PKT_FUNCNAMEYDX:
					notType=NotType.FUNCNAMEYDX;
					funcid=s.getNullTerminedString(p,	YAPI.YOCTO_FUNCTION_LEN);
					p+=YAPI.YOCTO_FUNCTION_LEN;
					funcname=s.getNullTerminedString(p,	YAPI.YOCTO_LOGICAL_LEN);
					p+=YAPI.YOCTO_LOGICAL_LEN;
					funydx=s.getData(p);
					break;
				default:
					throw new YAPI_Exception(YAPI.IO_ERROR, "Invalid Notification");
		    	}
		    }
		}

		
		public String dump()
		{
			String res="Not("+serial+"): ";
			switch(notType) {
			case CHILD:
				res+= String.format("child %s %d %d",childserial,onOff,devydy);
				break;
			case FIRMWARE:
				res+= String.format("firmware %s %x %x",firmware,vendorid,deviceid);
				break;
			case FUNCNAME:
				res+= String.format("FuncName %s %s",funcid,funcname);
				break;
			case FUNCNAMEYDX:
				res+= String.format("FuncName %s %s %x",funcid,funcname,funydx);
				break;
			case FUNCVAL:
				res+= String.format("FuncName %s %s",funcid,funcval);
				break;
			case LOG:
				res+= "Log";
				break;
			case NAME:
				res+= String.format("Name %s %x",logicalname,beacon);
				break;
			case PRODNAME:
				res+= String.format("product %s",product);
				break;
			case STREAMREADY:
				res+="StreamReady";
				break;
			default:
				res+="unknown";
				break;
			
			}
			return res+="\n";
		}

		public String getSerial()
		{
			return serial;
		}


		public NotType getNotType()
		{
			return notType;
		}


		public String getLogicalname()
		{
			return logicalname;
		}


		public int getBeacon()
		{
			return beacon;
		}


		public String getProduct()
		{
			return product;
		}


		public String getChildserial()
		{
			return childserial;
		}


		public int getOnOff()
		{
			return onOff;
		}


		public int getDevydy()
		{
			return devydy;
		}


		public String getFirmware()
		{
			return firmware;
		}


		public int getVendorid()
		{
			return vendorid;
		}


		public int getDeviceid()
		{
			return deviceid;
		}


		public String getShortFunctionID()
		{
			return funcid;
		}

		public String getLongFunctionID()
		{
			return serial+"."+funcid;
		}


		public String getFuncname()
		{
			return funcname;
		}


		public String getFuncval()
		{
			return funcval;
		}


		public int getFunydx()
		{
			return funydx;
		}
		
	}
	

	
	// create new pkt with his own buffer
	public YUSBPkt() 
	{
	}
	
	public void clear()
	{
	    _streams.clear();
		_pktno =0;
	}
	
	int getPktno() {
		return _pktno;
	}

	void setPktno(int _pktno) {
		this._pktno = _pktno&0x7;
	}

	
	
	public byte[] getRawPkt() throws YAPI_Exception
	{
	    byte[] res = new byte[USB_PKT_SIZE];
	    int pos=0;
	    //pad end of packet with empty data
	    this.FillPktWithEmpty();
		for(StreamHead s : _streams) {
			pos += s.popFromPkt(res,pos);
		}
		return res;
	}
	

	void parse(ByteBuffer raw_in_pkt) throws YAPI_Exception
	{
		raw_in_pkt.rewind();
		if(raw_in_pkt.limit()!=USB_PKT_SIZE) {
			throw new YAPI_Exception(YAPI.IO_ERROR,"invalid packet size");
		}
		_streams.clear();
		while(raw_in_pkt.position() <raw_in_pkt.limit()){
			StreamHead header=new StreamHead(raw_in_pkt);
			_pktno = header.getPktNumber();
			_streams.add(header);
		}
	}
	
	public boolean isConfPkt()
	{
		return _streams.get(0).getPktType()== YPKT_CONF;
	}
	
	public static void DebugDumpPkt(ByteBuffer pkt)
	{
		YAPI.Log("dump packet :"+pkt+"\n");
		byte[] tmp = pkt.array();
		YAPI.Log("byte[] len "+tmp.length+" ->"+tmp+"\n");
		for (int i =0 ; i <8;i++) {
			String line = String.format("0x02x:", i*8);  
			for (int j =0 ; j <8;j++) {
				int pos =i*8+j;
				line += String.format(" 0x%02x", tmp[pos]);  
			}
			YAPI.Log(line+"\n");

		}
		
	}

	public boolean isConfPktReset()
	{
		if(isConfPkt() && _streams.get(0).getStreamType()==USB_CONF_RESET)
			return true;
		return false;
	}

	public ConfPktResetDecoder getConfPktReset()
	{
		if(!isConfPktReset())
			return null;
		ConfPktResetDecoder reset = new ConfPktResetDecoder(_streams.get(0));
		return reset;
				
	}

	public boolean isConfPktStart()
	{
		if(isConfPkt() && _streams.get(0).getStreamType()==USB_CONF_START)
			return true;
		return false;
	}

	public ConfPktStartDecoder getConfPktStart()
	{
		if(!isConfPktStart())
			return null;
		ConfPktStartDecoder start = new ConfPktStartDecoder(_streams.get(0));
		return start;
	}

	public String dumpToString()
	{
		StringBuilder res =new StringBuilder();
		for(StreamHead s:_streams){
			res.append(s.dump());
		}
		return res.toString();
	}

	public StreamHead getStream(int i)
	{
		return _streams.get(i);
	}

	public int getNbStreams()
	{
		return _streams.size();
	}

	protected void  pushStream(int type, int size, ByteBuffer data) throws YAPI_Exception
	{
		StreamHead header = new StreamHead(_pktno, YPKT_STREAM, type, size,data);
		_streams.add(header);
	}

	protected void  pushEmptyStream(int size) throws YAPI_Exception
	{
		
		StreamHead header = new StreamHead(_pktno, YPKT_STREAM, YSTREAM_EMPTY, size);
		_streams.add(header);
	}

	
	protected void  pushConf(int type, ByteBuffer data) throws YAPI_Exception
	{
		int size = data.remaining();
		if(_streams.size()!=0){
			throw new YAPI_Exception(YAPI.IO_ERROR,"Conf packet can only be sent on empty packet");			
		}			
		StreamHead header = new StreamHead(_pktno, YPKT_CONF, type, size,data);
		_streams.add(header);
		this.FillPktWithEmpty();
	}
	
	
	public int getMaxTcpSize_slow()
	{
	    
		int remain = USB_PKT_SIZE-USB_PKT_STREAM_HEAD;
		for (StreamHead stream : _streams) {
            int size = stream.getSize();
            if (size >= remain)
                return 0;
            remain-=size;
        }
		return remain;
	}
	
	public void formatResetPkt() throws YAPI_Exception
	{
		setPktno(0);
		ByteBuffer confpkt = ByteBuffer.allocate(4);
		confpkt.putShort((short) YPKT_USB_VERSION_BCD);
		confpkt.put((byte) 1);
		confpkt.flip();
		pushConf(USB_CONF_RESET, confpkt);
	}

	public void formatStartPkt() throws YAPI_Exception
	{
		setPktno(0);
		ByteBuffer confpkt = ByteBuffer.allocate(4);
		confpkt.put((byte) 1);
        confpkt.flip();
		pushConf(USB_CONF_START, confpkt);
	}

	public int pushTCP(byte[] tcp,int start, int len) throws YAPI_Exception
	{
	    int avail =getMaxTcpSize_slow();
		int contentlen =avail< len ? avail: len;
		pushStream(YSTREAM_TCP, contentlen,ByteBuffer.wrap(tcp,start,contentlen));
		return contentlen;
	}

	
	
   public void pushTCPClose() throws YAPI_Exception
    {
        pushStream(YSTREAM_TCP_CLOSE, 0,null);
    }

	
	private void FillPktWithEmpty() throws YAPI_Exception
	{
	    int avail =getMaxTcpSize_slow();
		if(avail> USB_PKT_STREAM_HEAD){
			pushEmptyStream(avail-USB_PKT_STREAM_HEAD);
		}
	}
   
	

}
