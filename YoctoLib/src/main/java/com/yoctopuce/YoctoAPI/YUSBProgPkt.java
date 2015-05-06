package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class YUSBProgPkt {
    private static final int USB_PKT_SIZE = 64;
    //
    // PROG packets are only used in bootloader (USB DeviceID=0001/0002)
    //
    static final int PROG_INVALID = -1; // invalid packet
    static final int PROG_NOP = 0;      // nothing to do
    static final int PROG_REBOOT = 1;   // reset the device
    static final int PROG_ERASE = 2;    // erase completely the device
    static final int PROG_PROG = 3;     // program the device
    static final int PROG_VERIF = 4;    // program the device
    static final int PROG_INFO = 5;     // get device info
    static final int PROG_INFO_EXT = 6; // get extended device info (flash bootloader only)


    static final int MAX_BYTE_IN_PACKET = (60);
    static final int MAX_INSTR_IN_PACKET = (MAX_BYTE_IN_PACKET / 3);

    private final int _type;
    private final int _size;
    private int _progdata_ofs;

    // erase stuff
    private int _pos;
    private int _nPages;
    private int _pageNo;
    private final boolean _use_ext_pkt;

    //verif and prog stuff
    private int _address;

    //pktinfo stuff
    private short _pr_blk_size;
    private int _devid;
    private int _settings_addr;
    private int _last_addr;
    private int _config_start;
    private int _config_stop;
    private short _er_blk_size;

    //pktinfo ext stuff
    private byte _version;
    private short _ext_jedec_id;
    private short _ext_page_size;
    private short _ext_total_pages;
    private short _first_code_page;
    private short _first_yfs3_page;
    private byte _devid_;
    private byte[] _progdata;
    private short _sign;

    public byte[] getProgdata()
    {
        return _progdata;
    }

    public int getProgdata_ofs()
    {
        return _progdata_ofs;
    }

    public YUSBProgPkt(ByteBuffer pkt) {
        pkt.order(ByteOrder.LITTLE_ENDIAN);
        if (pkt.remaining() < USB_PKT_SIZE) {
            _type = PROG_INVALID;
            _size = 0;
            _use_ext_pkt = false;
            return;
        }

        int b = pkt.get() & 0xff;
        _type = b >> 5;
        _size = (b & 31);
        switch (_type) {
            case PROG_NOP:
            case PROG_REBOOT:
            case PROG_ERASE:
            default:
                _use_ext_pkt = false;
                break;
            case PROG_PROG:
            case PROG_VERIF:
                int dwordpos_lo = pkt.get() & 0xff;
                int pageno_lo = pkt.get() & 0xff;
                int misc_hi = pkt.get() & 0xff;
                _pos = dwordpos_lo + ((misc_hi << 2) & 0x300);
                _pageNo = pageno_lo + ((misc_hi & 0x3f) << 8);
                _address = (dwordpos_lo<<16)+ (misc_hi<<8)  + pageno_lo ;
                _use_ext_pkt = true;
                _progdata = new byte[_size*2];
                pkt.get(_progdata);
                _progdata_ofs = 0;
                break;
            case PROG_INFO:
                _use_ext_pkt = false;
                pkt.get(); // eat pad
                _pr_blk_size = pkt.getShort(); // eat  pr_blk_size;
                _devid = pkt.getInt();
                _settings_addr = pkt.getInt();
                _last_addr = pkt.getInt();
                _config_start = pkt.getInt();
                _config_stop = pkt.getInt();
                _er_blk_size = pkt.getShort();
                break;
            case PROG_INFO_EXT:
                _use_ext_pkt = true;
                _version = pkt.get();
                _pr_blk_size = pkt.getShort(); // eat  pr_blk_size;
                _devid = pkt.getInt();
                _settings_addr = pkt.getInt();
                _last_addr = pkt.getInt();
                _config_start = pkt.getInt();
                _config_stop = pkt.getInt();
                _er_blk_size = pkt.getShort();
                _ext_jedec_id = pkt.getShort();
                _ext_page_size = pkt.getShort();
                _ext_total_pages = pkt.getShort();
                _first_code_page = pkt.getShort();
                _first_yfs3_page = pkt.getShort();
                break;
        }
    }

    public YUSBProgPkt(int type, boolean ext_pkt) {
        _type = type;
        _size = 0;
        _use_ext_pkt = ext_pkt;
    }


    public YUSBProgPkt(int type, int page, int pos, int nPages) {
        _type = type;
        _size = 0;
        _use_ext_pkt = true;
        _pageNo = page;
        _pos = pos;
        _nPages = nPages;

    }

    public YUSBProgPkt(int type, int page, int pos)
    {
        _type = type;
        _size = 0;
        _use_ext_pkt = true;
        _pageNo = page;
        _pos = pos;
    }

    public YUSBProgPkt(int type, int page ,int pos ,int size, byte[] instr, int ofs) {
        _type = type;
        _size = size;
        _use_ext_pkt = true;
        _pageNo = page;
        _pos = pos;
        _progdata = instr;
        _progdata_ofs = ofs;
    }

    public YUSBProgPkt(int type, int address)
    {
        _type = type;
        _size = 0 ;
        _use_ext_pkt = false;
        _address = address;
    }

    public YUSBProgPkt(int type, int address, int nbinstr, byte[] instr, int ofs) {
        _type = type;
        _size = nbinstr;
        _use_ext_pkt = false;
        _address = address;
        _progdata = instr;
        _progdata_ofs = ofs;
    }


    public int getType() {
        return _type;
    }

    public int getSize() {
        return _size;
    }

    public short getPr_blk_size() {
        return _pr_blk_size;
    }

    public int getSettings_addr() {
        return _settings_addr;
    }

    public int getLast_addr() {
        return _last_addr;
    }

    public int getConfig_start() {
        return _config_start;
    }

    public int getConfig_stop() {
        return _config_stop;
    }

    public short getEr_blk_size() {
        return _er_blk_size;
    }

    public byte getVersion() {
        return _version;
    }

    public short getExt_jedec_id() {
        return _ext_jedec_id;
    }

    public short getExt_page_size() {
        return _ext_page_size;
    }

    public short getExt_total_pages() {
        return _ext_total_pages;
    }

    public short getFirst_code_page() {
        return _first_code_page;
    }

    public short getFirst_yfs3_page() {
        return _first_yfs3_page;
    }

    public byte[] getRawPkt() {

        byte[] pkt = new byte[USB_PKT_SIZE];
        final ByteBuffer bb = ByteBuffer.wrap(pkt);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        // java ensure all byte are initialized to 0
        bb.put((byte) (((_size & 31) + ((_type & 0x7) << 5)) & 0xff));
        switch (_type) {
            case PROG_NOP:
            case PROG_REBOOT:
                // not need to set fields for output packets
                if (_use_ext_pkt) {
                    bb.put((byte) 0);
                    bb.put((byte) 0);
                    bb.put((byte) 0);
                    bb.putShort(_sign);
                }
                break;
            case PROG_ERASE:
                if (_use_ext_pkt) {
                    bb.put((byte) (_pos & 0xff));
                    bb.put((byte) (_pageNo & 0xff));
                    bb.put((byte) (((_pageNo >> 8) & 0x3f) + ((_pos & 0x300) >> 2)));
                    bb.put((byte) (_nPages & 0xff));
                    bb.put((byte) ((_nPages >> 8) & 0xff));
                }
                break;
            case PROG_PROG:
            case PROG_VERIF:
                if (_use_ext_pkt) {
                    bb.put((byte) (_pos & 0xff));
                    bb.put((byte) (_pageNo & 0xff));
                    bb.put((byte) (((_pageNo >> 8) & 0x3f) + ((_pos & 0x300) >> 2)));
                    if (_type== PROG_PROG) {
                        bb.put(_progdata, _progdata_ofs, _size * 2);
                    }
                }else {
                    bb.put((byte) ((_address >> 16) & 0xff));
                    bb.put((byte) (_address & 0xff));
                    bb.put((byte) ((_address >> 8) & 0xff));
                    bb.put(_progdata, _progdata_ofs, _size * 3);
                }
                break;
            case PROG_INFO:
            case PROG_INFO_EXT:
            default:
                // not need to set fields for output packets
                break;
        }

        return pkt;

    }

    public int getDevid_family() {
        return (_devid >> 8) & 0xff;
    }

    public int getDevid_model() {
        return (_devid) & 0xff;
    }

    public int getDevid_rev() {
        return (_devid) >> 16 & 0xffff;
    }

    public int getPageNo()
    {
        return _pageNo;
    }
    public  int getPos()
    {
        return _pos;
    }

    public void setBtSign(int sign)
    {
        if (_use_ext_pkt) {
            _sign = (short) (sign & 0xffff);
        }
    }
}
