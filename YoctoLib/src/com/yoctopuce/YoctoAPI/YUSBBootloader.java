package com.yoctopuce.YoctoAPI;


import java.nio.ByteBuffer;

public class YUSBBootloader implements YUSBRawDevice.IOHandler
{

    private static final long ERASE_TIMEOUT = 10000;
    private static final long BLOCK_FLASH_TIMEOUT = 5000;
    private static final long PROG_GET_INFO_TIMEOUT = 1000;
    private static final long ZONE_VERIF_TIMEOUT = 2000;
    private static final int PROGRESS_OFFSET = 5;
    private static final int PROGRESS_RANGE = 95;
    @SuppressWarnings("UnusedDeclaration")
    private static final int START_APPLICATION_SIGN = 0;
    @SuppressWarnings("UnusedDeclaration")
    private static final int START_BOOTLOADER_SIGN = ('b' | ('T' << 8));
    private static final int START_AUTOFLASHER_SIGN = ('b' | ('F' << 8));


    private String _serial;
    private YUSBRawDevice _rawdev = null;
    private int _ROM_nb_zone;
    private int _FLA_nb_zone;
    private YFirmwareFile _firmware;
    private YGenericHub.UpdateProgress _progress;
    private YFirmwareFile.byn_zone _bz;
    private int _flash_page_ofs;
    private int _file_ofs;
    private int _addr_page;
    private int _last_percent;
    private String _last_msg;


    private enum FLASH_STATE
    {
        GET_INFO,
        INFO_RECEIVED,
        ERASE,
        ERASE_CONFIRMED,
        FLASH,
        FLASH_CONFIRMED,
        GET_INFO_BFOR_REBOOT,
        REBOOT,
        FAILED,
        DONE
    }

    //device indentifications PIC24FJ256DA210 family
    private final static int FAMILY_PIC24FJ256DA210 = 0X41;
    private final static int PIC24FJ128DA206 = 0x08;
    private final static int PIC24FJ128DA106 = 0x09;
    private final static int PIC24FJ128DA210 = 0x0A;
    private final static int PIC24FJ128DA110 = 0x0B;
    private final static int PIC24FJ256DA206 = 0x0C;
    private final static int PIC24FJ256DA106 = 0x0D;
    private final static int PIC24FJ256DA210 = 0x0E;
    private final static int PIC24FJ256DA110 = 0x0F;

    //device indentifications PIC24FJ64GB004 family
    private final static int FAMILY_PIC24FJ64GB004 = 0x42;
    private final static int PIC24FJ32GB002 = 0x03;
    private final static int PIC24FJ64GB002 = 0x07;
    private final static int PIC24FJ32GB004 = 0x0B;
    private final static int PIC24FJ64GB004 = 0x0F;

    // Spansion Flash JEDEC id
    private final static int JEDEC_SPANSION_4MB = 0x15;


    private final Object _stateLock = new Object();
    private FLASH_STATE _flash_state;


    //pktinfo stuff
    private short _pr_blk_size;
    private int _devid_family;
    private int _devid_model;
    private int _last_addr;

    //pktinfo ext stuff
    private int _ext_jedec_id;
    private int _ext_page_size;
    private short _ext_total_pages;
    private int _first_code_page;
    private int _first_yfs3_page;


    public YUSBBootloader()
    {
        _flash_state = FLASH_STATE.DONE;
    }

    private void setNewState(FLASH_STATE new_state)
    {
        synchronized (_stateLock) {
            _flash_state = new_state;
            _stateLock.notify();
        }
    }

    private void setErrorState(String error)
    {
        uLogProgress(-1,error);
        synchronized (_stateLock) {
            _flash_state = FLASH_STATE.FAILED;
            _stateLock.notify();
        }
    }




    private void waitForState(FLASH_STATE wanted, FLASH_STATE next, long mswait, String message) throws YAPI_Exception
    {
        long timeout = YAPI.GetTickCount() + mswait;
        synchronized (_stateLock) {
            while (_flash_state != wanted && timeout > YAPI.GetTickCount()) {
                long millis = timeout - YAPI.GetTickCount();
                try {
                    _stateLock.wait(millis);
                } catch (InterruptedException e) {
                    throw new YAPI_Exception(YAPI.TIMEOUT, "Device " + _serial + " " + message, e);
                }
            }
            if (_flash_state != wanted) {
                throw new YAPI_Exception(YAPI.TIMEOUT, "Device " + _serial + " " + message + " (" + _flash_state + ")");
            }
            if (next != null) {
                _flash_state = next;
                _stateLock.notify();
            }
        }
    }


    @Override
    public void newPKT(ByteBuffer android_raw_pkt)
    {
        YUSBProgPkt pkt = new YUSBProgPkt(android_raw_pkt);
        int pkt_type = pkt.getType();
        switch (_flash_state) {
            case GET_INFO:
                if (pkt_type == YUSBProgPkt.PROG_INFO) {
                    _pr_blk_size = pkt.getPr_blk_size();
                    _last_addr = pkt.getLast_addr();
                    _devid_family = pkt.getDevid_family();
                    _devid_model = pkt.getDevid_model();
                    _ext_jedec_id = 0xffff;
                    _ext_page_size = 0xffff;
                    _ext_total_pages = 0;
                    _first_code_page = 0xffff;
                    _first_yfs3_page = 0xffff;
                    setNewState(FLASH_STATE.INFO_RECEIVED);
                } else if (pkt_type == YUSBProgPkt.PROG_INFO_EXT) {
                    _pr_blk_size = pkt.getPr_blk_size();
                    _last_addr = pkt.getLast_addr();
                    _devid_family = pkt.getDevid_family();
                    _devid_model = pkt.getDevid_model();
                    _ext_jedec_id = pkt.getExt_jedec_id();
                    _ext_page_size = pkt.getExt_page_size();
                    _ext_total_pages = pkt.getExt_total_pages();
                    _first_code_page = pkt.getFirst_code_page();
                    _first_yfs3_page = pkt.getFirst_yfs3_page();
                    setNewState(FLASH_STATE.INFO_RECEIVED);
                } else {
                    setErrorState("Not a PROG_INFO pkt");
                }
                break;
            case INFO_RECEIVED:
                break;
            case ERASE:
                if (pkt_type == YUSBProgPkt.PROG_INFO || pkt_type == YUSBProgPkt.PROG_INFO_EXT) {
                    setNewState(FLASH_STATE.ERASE_CONFIRMED);
                } else {
                    setErrorState("Not a PROG_INFO pkt");
                }
                break;
            case FLASH:
                if (pkt_type == YUSBProgPkt.PROG_PROG) {
                    setNewState(FLASH_STATE.FLASH_CONFIRMED);
                } else if (pkt_type == YUSBProgPkt.PROG_VERIF) {
                    int pageno = pkt.getPageNo();
                    int size = pkt.getSize() * 2;
                    int pos = pkt.getPos() * 4;
                    int addr = pageno * _ext_page_size + pos;
                    //Log.d(TAG, String.format("Verif page=0x%x:0x%x (%x bytes addr=0x%x)", pageno, pos, size, addr));
                    if (addr < _addr_page) {
                        setErrorState(String.format("Error page=0x%x pos=0x%x (up to %x bytes)",
                                pageno, pos, size));
                    }
                    if (addr < _addr_page + _flash_page_ofs) {
                        // packet is in verification range
                        int datasize = size;
                        if (addr + datasize >= _addr_page + _flash_page_ofs) {
                            datasize = _addr_page + _flash_page_ofs - addr;
                        }
                        byte[] firmware_data = _firmware.getData();
                        int firmware_ofs = _file_ofs + (addr - _addr_page);

                        byte[] pkt_data = pkt.getProgdata();
                        int pkt_ofs = pkt.getProgdata_ofs();
                        for (int i = 0; i < datasize; i++) {
                            short b = pkt_data[pkt_ofs + i];
                            short b1 = firmware_data[firmware_ofs + i];
                            if (b != b1) {
                                setErrorState(String.format("Flash verification failed at %x (%x:%x)", addr, pageno, pos));
                            }
                        }
                    }
                    //else {
                    //    Log.d(TAG, String.format("Skip verification for block at %x (block ends at %x)", addr, _addr_page + _flash_page_ofs));
                    //}
                    if ((addr & (_ext_page_size - 1)) + size >= _ext_page_size) {
                        setNewState(FLASH_STATE.FLASH_CONFIRMED);
                    }

                } else {
                    setErrorState("Not a PROG_INFO pkt");
                }

                break;
            case FLASH_CONFIRMED:
                break;
            case GET_INFO_BFOR_REBOOT:
                if (pkt_type == YUSBProgPkt.PROG_INFO || pkt_type == YUSBProgPkt.PROG_INFO_EXT) {
                    setNewState(FLASH_STATE.REBOOT);
                }
                break;
            case REBOOT:
                break;
            case DONE:
                break;
        }
    }


    private void uLogProgress(int percent, String msg)
    {

        if (_last_percent != percent || (msg != null && !msg.equals(_last_msg))) {
            if (_progress != null) {
                _progress.firmware_progress(percent * PROGRESS_RANGE / 100 + PROGRESS_OFFSET, msg);
            }
        }
        _last_percent = percent;
        if (msg != null) {
            _last_msg = msg;
        }
    }


    @Override
    public void ioError(String msg)
    {
        setErrorState(msg);
    }


    @Override
    public void rawDeviceUpdateState(YUSBRawDevice yusbRawDevice)
    {
        _rawdev = yusbRawDevice;
        _serial = yusbRawDevice.getSerial();
    }

    public String getSerial()
    {
        return _serial;
    }


    String getCPUName(int devid_family, int devid_model)
    {
        String res;
        switch (devid_family) {
            case FAMILY_PIC24FJ256DA210:
                switch (devid_model) {
                    case PIC24FJ128DA206:
                        return "PIC24FJ128DA206";
                    case PIC24FJ128DA106:
                        return "PIC24FJ128DA106";
                    case PIC24FJ128DA210:
                        return "PIC24FJ128DA210";
                    case PIC24FJ128DA110:
                        return "PIC24FJ128DA110";
                    case PIC24FJ256DA206:
                        return "PIC24FJ256DA206";
                    case PIC24FJ256DA106:
                        return "PIC24FJ256DA106";
                    case PIC24FJ256DA210:
                        return "PIC24FJ256DA210";
                    case PIC24FJ256DA110:
                        return "PIC24FJ256DA110";
                    default:
                        res = "Unknown CPU model(family PIC24FJ256DA210)";
                        break;
                }
                break;
            case FAMILY_PIC24FJ64GB004:
                switch (devid_model) {
                    case PIC24FJ32GB002:
                        return "PIC24FJ32GB002";
                    case PIC24FJ64GB002:
                        return "PIC24FJ64GB002";
                    case PIC24FJ32GB004:
                        return "PIC24FJ32GB004";
                    case PIC24FJ64GB004:
                        return "PIC24FJ64GB004";
                    default:
                        res = "Unknown CPU model(family PIC24FJ64GB004)";
                        break;
                }
                break;
            default:
                res = "Unknown CPU model";
        }
        return res;
    }


    public void firmwareUpdate(YFirmwareFile firmware, YGenericHub.UpdateProgress progress) throws YAPI_Exception
    {
        _firmware = firmware;
        _progress = progress;
        setNewState(FLASH_STATE.GET_INFO);
        uSendCmd(YUSBProgPkt.PROG_INFO);
        uLogProgress(1, "Get Info From bootloader");
        waitForState(FLASH_STATE.INFO_RECEIVED, null, PROG_GET_INFO_TIMEOUT, "Bootloader did not respond to GetInfo pkt");
        String baseSerial = _serial.substring(0, YAPI.YOCTO_BASE_SERIAL_LEN);
        if (!firmware.getSerial().startsWith(baseSerial)) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "This BYN file is not designed for your device");
        }
        String cpu = getCPUName(_devid_family, _devid_model);
        if (!cpu.equals(firmware.getPictype())) {
            throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "This BYN file is not designed for your device");
        }
        uLogProgress(2, "Firmware file validated");

        _ROM_nb_zone = firmware.getROM_nb_zone();
        _FLA_nb_zone = firmware.getFLA_nb_zone();
        int flashPage = _first_code_page;
        if (_ext_total_pages > 0) {
            // code on external flash -> erase page by pages
            int maxpages = (_ext_jedec_id == JEDEC_SPANSION_4MB ? 16 : 128);
            while (flashPage < _ext_total_pages) {
                setNewState(FLASH_STATE.ERASE);
                int npages = _ext_total_pages - flashPage;
                uLogProgress(3 + (7 * flashPage / _ext_total_pages), "Erasing flash");
                if (npages > maxpages) {
                    npages = maxpages;
                }
                uSendErase(flashPage, npages);
                // request info to ensure erase command as finished
                uSendCmd(YUSBProgPkt.PROG_INFO);
                waitForState(FLASH_STATE.ERASE_CONFIRMED, null, ERASE_TIMEOUT, "Timeout blanking flash");
                flashPage += npages;
            }
        } else {
            // code only on PIC -> erase all pages
            uLogProgress(3, "erase flash memory");
            setNewState(FLASH_STATE.ERASE);
            uSendCmd(YUSBProgPkt.PROG_ERASE);
            // request info to ensure erase command as finished
            uSendCmd(YUSBProgPkt.PROG_INFO);
        }
        waitForState(FLASH_STATE.ERASE_CONFIRMED, FLASH_STATE.FLASH, ERASE_TIMEOUT + (_last_addr >> 6), "Timeout blanking flash");
        _zst = FLASH_ZONE_STATE.FLASH_ZONE_START;
        if (_ext_total_pages > 0) {
            uFlashFlash();
        } else {
            uFlashZone();
        }
        setNewState(FLASH_STATE.GET_INFO_BFOR_REBOOT);
        uSendCmd(YUSBProgPkt.PROG_INFO);
        waitForState(FLASH_STATE.REBOOT, null, PROG_GET_INFO_TIMEOUT, "Last communication before reboot failed");
        YUSBProgPkt pkt;
        if (_ext_total_pages > 0) {
            pkt = new YUSBProgPkt(YUSBProgPkt.PROG_REBOOT, true);
            pkt.setBtSign(START_AUTOFLASHER_SIGN);

        } else {
            pkt = new YUSBProgPkt(YUSBProgPkt.PROG_REBOOT, false);
        }
        _rawdev.sendPkt(pkt.getRawPkt());
    }

    enum FLASH_ZONE_STATE
    {
        FLASH_ZONE_START,
        FLASH_ZONE_PROG,
        FLASH_ZONE_RECV_OK
    }

    FLASH_ZONE_STATE _zst;

    //progress 10 ->90
    private void uFlashZone() throws YAPI_Exception
    {
        int cur_zone = 0;
        int file_ofs = _firmware.getFirstZoneOfs();
        while (cur_zone < _ROM_nb_zone + _FLA_nb_zone) {
            _bz = _firmware.getBynZone(file_ofs);
            if ((_bz.addr_page % (_pr_blk_size * 2)) != 0) {
                throw new YAPI_Exception(YAPI.IO_ERROR, "ProgAlign");
            }
            file_ofs += YFirmwareFile.byn_zone.SIZE;
            int nbInstrInZone = _bz.len / 3;
            if (nbInstrInZone < _pr_blk_size) {
                throw new YAPI_Exception(YAPI.IO_ERROR, "ProgSmall");
            }
            int block_addr = _bz.addr_page;
            int inst_in_block = 0;
            while (nbInstrInZone > 0) {
                uLogProgress(10 + 80 * file_ofs / _firmware.getData().length, String.format("Write flash memory zone %d (0x%x)", cur_zone,_bz.addr_page));
                while (inst_in_block < _pr_blk_size) {
                    int nb_instructions = (nbInstrInZone < YUSBProgPkt.MAX_INSTR_IN_PACKET ? nbInstrInZone : YUSBProgPkt.MAX_INSTR_IN_PACKET);
                    //uLogProgress(4 + 92 * file_ofs / _firmware.getData().length,
                    //        String.format("Flash zone %d:0x%x 0x%x:(%d/%d)", cur_zone, file_ofs, block_addr, inst_in_block, _pr_blk_size));
                    YUSBProgPkt pkt = new YUSBProgPkt(YUSBProgPkt.PROG_PROG, block_addr, nb_instructions, _firmware.getData(), file_ofs);
                    _rawdev.sendPkt(pkt.getRawPkt());
                    inst_in_block += nb_instructions;
                    nbInstrInZone -= nb_instructions;
                    file_ofs += nb_instructions * 3;
                }
                waitForState(FLASH_STATE.FLASH_CONFIRMED, FLASH_STATE.FLASH, BLOCK_FLASH_TIMEOUT,
                        String.format("Bootlaoder did not send confirmation for Zone %x Block %x", cur_zone, _bz.addr_page));
                inst_in_block -= _pr_blk_size;
                block_addr += _pr_blk_size * 2;
            }
            cur_zone++;
        }
    }

    //progress 10 ->90
    private void uFlashFlash() throws YAPI_Exception
    {

        int curzone;
        _file_ofs = _firmware.getFirstZoneOfs();
        for (curzone = 0; curzone < _ROM_nb_zone + _FLA_nb_zone; curzone++) {
            _bz = _firmware.getBynZone(_file_ofs);
            // skip zone header
            _file_ofs += YFirmwareFile.byn_zone.SIZE;
            int page_len = _bz.len;
            if (curzone < _ROM_nb_zone) {
                _addr_page = _first_code_page * _ext_page_size + 3 * _bz.addr_page / 2;
            } else {
                _addr_page = _first_yfs3_page * _ext_page_size + _bz.addr_page;
            }
            if ((_addr_page & 1) != 0 || (page_len & 1) != 0) {
                throw new YAPI_Exception(YAPI.IO_ERROR, String.format("Prog block not on a word boundary (0x%x + 0x%x)", _addr_page, page_len));
            }
            int zone_ofs = 0;
            while (zone_ofs < _bz.len) {
                // Iterate on all pages
                if (curzone < _ROM_nb_zone && _addr_page >= (_first_yfs3_page * _ext_page_size)) {
                    //Log.d(TAG, String.format("Drop ROM data past firmware boundary (zone %d at offset %x)", curzone, _file_ofs));
                    _file_ofs += (_bz.len - zone_ofs);
                    break;
                }
                _flash_page_ofs = 0;
                int addr;
                int datasize;
                do {
                    addr = _addr_page + _flash_page_ofs;
                    datasize = _ext_page_size - (addr & (_ext_page_size - 1));

                    if (datasize > YUSBProgPkt.MAX_BYTE_IN_PACKET) {
                        datasize = YUSBProgPkt.MAX_BYTE_IN_PACKET;
                    }
                    if (zone_ofs + datasize > _bz.len) {
                        datasize = _bz.len - zone_ofs;
                    }
                    if ((datasize & 1) != 0) {
                        throw new YAPI_Exception(YAPI.IO_ERROR, String.format("Prog block not on a word boundary (%d+%d)", _addr_page, page_len));
                    }

                    int page = addr / _ext_page_size;
                    int pos = (addr % _ext_page_size) / 4;
                    String msg;
                    if (curzone < _ROM_nb_zone) {
                        msg = String.format("Write memory zone %d (0x%x)",curzone, page);
                    } else {
                        msg = String.format("Write memory zone %d (0x%x ext)",curzone, page);
                    }
                    //= String.format("Flash at 0x%x:0x%x (%x bytes) found at 0x%x (%x more in zone)", page, pos*4,
                    //        datasize, _file_ofs + _flash_page_ofs, _bz.len - zone_ofs);
                    uLogProgress(10 + 80 * (_file_ofs + _flash_page_ofs) / _firmware.getData().length, msg);

                    YUSBProgPkt pkt = new YUSBProgPkt(YUSBProgPkt.PROG_PROG, page, pos, datasize / 2, _firmware.getData(), _file_ofs + _flash_page_ofs);
                    _rawdev.sendPkt(pkt.getRawPkt());
                    _flash_page_ofs += datasize;
                    zone_ofs += datasize;
                    // verify each time we finish a page or a zone
                }
                while (((addr & (_ext_page_size - 1)) + datasize < _ext_page_size) && (zone_ofs < _bz.len));


                addr = _addr_page;
                int page = addr / _ext_page_size;
                int pos = (addr % _ext_page_size) / 4;
                YUSBProgPkt pkt = new YUSBProgPkt(YUSBProgPkt.PROG_VERIF, page, pos);
                _rawdev.sendPkt(pkt.getRawPkt());
                waitForState(FLASH_STATE.FLASH_CONFIRMED, FLASH_STATE.FLASH, ZONE_VERIF_TIMEOUT,
                        String.format("Bootlaoder did not send confirmation for Zone %x Block %x", curzone, _bz.addr_page));
                // go to next page
                _file_ofs += _flash_page_ofs;
                _addr_page += _flash_page_ofs;
            }

        }
    }

    private void uSendCmd(int type) throws YAPI_Exception
    {
        YUSBProgPkt pkt = new YUSBProgPkt(type, false);
        _rawdev.sendPkt(pkt.getRawPkt());
    }

    private void uSendErase(int firstPage, int nPages) throws YAPI_Exception
    {
        YUSBProgPkt pkt = new YUSBProgPkt(YUSBProgPkt.PROG_ERASE, firstPage, 0, nPages);
        _rawdev.sendPkt(pkt.getRawPkt());
    }

    public boolean isReady()
    {
        return _rawdev != null && _rawdev.isUsable();
    }
}
