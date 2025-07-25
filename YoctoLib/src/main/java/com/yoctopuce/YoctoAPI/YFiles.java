/*********************************************************************
 *
 * $Id: YFiles.java 67412 2025-06-12 09:01:59Z seb $
 *
 * Implements yFindFiles(), the high-level API for Files functions
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
import java.util.Locale;

//--- (generated code: YFiles class start)
/**
 *  YFiles Class: filesystem control interface, available for instance in the Yocto-Color-V2, the
 * Yocto-SPI, the YoctoHub-Ethernet or the YoctoHub-GSM-4G
 *
 * The YFiles class is used to access the filesystem embedded on
 * some Yoctopuce devices. This filesystem makes it
 * possible for instance to design a custom web UI
 * (for networked devices) or to add fonts (on display devices).
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YFiles extends YFunction
{
//--- (end of generated code: YFiles class start)

    //--- (generated code: YFiles definitions)
    /**
     * invalid filesCount value
     */
    public static final int FILESCOUNT_INVALID = YAPI.INVALID_UINT;
    /**
     * invalid freeSpace value
     */
    public static final int FREESPACE_INVALID = YAPI.INVALID_UINT;
    protected int _filesCount = FILESCOUNT_INVALID;
    protected int _freeSpace = FREESPACE_INVALID;
    protected UpdateCallback _valueCallbackFiles = null;
    protected int _ver = 0;

    /**
     * Deprecated UpdateCallback for Files
     */
    public interface UpdateCallback
    {
        /**
         *
         * @param function      : the function object of which the value has changed
         * @param functionValue : the character string describing the new advertised value
         */
        void yNewValue(YFiles function, String functionValue);
    }

    /**
     * TimedReportCallback for Files
     */
    public interface TimedReportCallback
    {
        /**
         *
         * @param function : the function object of which the value has changed
         * @param measure  : measure
         */
        void timedReportCallback(YFiles  function, YMeasure measure);
    }
    //--- (end of generated code: YFiles definitions)

    /**
     * @param func : functionid
     */
    protected YFiles(YAPIContext yctx, String func)
    {
        super(yctx, func);
        _className = "Files";
        //--- (generated code: YFiles attributes initialization)
        //--- (end of generated code: YFiles attributes initialization)
    }

    protected YFiles(String func)
    {
        this(YAPI.GetYCtx(true), func);
    }


    //--- (generated code: YFiles implementation)
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void  _parseAttr(YJSONObject json_val) throws Exception
    {
        if (json_val.has("filesCount")) {
            _filesCount = json_val.getInt("filesCount");
        }
        if (json_val.has("freeSpace")) {
            _freeSpace = json_val.getInt("freeSpace");
        }
        super._parseAttr(json_val);
    }

    /**
     * Returns the number of files currently loaded in the filesystem.
     *
     * @return an integer corresponding to the number of files currently loaded in the filesystem
     *
     * @throws YAPI_Exception on error
     */
    public int get_filesCount() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return FILESCOUNT_INVALID;
                }
            }
            res = _filesCount;
        }
        return res;
    }

    /**
     * Returns the number of files currently loaded in the filesystem.
     *
     * @return an integer corresponding to the number of files currently loaded in the filesystem
     *
     * @throws YAPI_Exception on error
     */
    public int getFilesCount() throws YAPI_Exception
    {
        return get_filesCount();
    }

    /**
     * Returns the free space for uploading new files to the filesystem, in bytes.
     *
     * @return an integer corresponding to the free space for uploading new files to the filesystem, in bytes
     *
     * @throws YAPI_Exception on error
     */
    public int get_freeSpace() throws YAPI_Exception
    {
        int res;
        synchronized (this) {
            if (_cacheExpiration <= YAPIContext.GetTickCount()) {
                if (load(_yapi._defaultCacheValidity) != YAPI.SUCCESS) {
                    return FREESPACE_INVALID;
                }
            }
            res = _freeSpace;
        }
        return res;
    }

    /**
     * Returns the free space for uploading new files to the filesystem, in bytes.
     *
     * @return an integer corresponding to the free space for uploading new files to the filesystem, in bytes
     *
     * @throws YAPI_Exception on error
     */
    public int getFreeSpace() throws YAPI_Exception
    {
        return get_freeSpace();
    }

    /**
     * Retrieves a filesystem for a given identifier.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the filesystem is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YFiles.isOnline() to test if the filesystem is
     * indeed online at a given time. In case of ambiguity when looking for
     * a filesystem by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * If a call to this object's is_online() method returns FALSE although
     * you are certain that the matching device is plugged, make sure that you did
     * call registerHub() at application initialization time.
     *
     * @param func : a string that uniquely characterizes the filesystem, for instance
     *         YRGBLED2.files.
     *
     * @return a YFiles object allowing you to drive the filesystem.
     */
    public static YFiles FindFiles(String func)
    {
        YFiles obj;
        YAPIContext ctx = YAPI.GetYCtx(true);
        synchronized (ctx._functionCacheLock) {
            obj = (YFiles) YFunction._FindFromCache("Files", func);
            if (obj == null) {
                obj = new YFiles(func);
                YFunction._AddToCache("Files", func, obj);
            }
        }
        return obj;
    }

    /**
     * Retrieves a filesystem for a given identifier in a YAPI context.
     * The identifier can be specified using several formats:
     * <ul>
     * <li>FunctionLogicalName</li>
     * <li>ModuleSerialNumber.FunctionIdentifier</li>
     * <li>ModuleSerialNumber.FunctionLogicalName</li>
     * <li>ModuleLogicalName.FunctionIdentifier</li>
     * <li>ModuleLogicalName.FunctionLogicalName</li>
     * </ul>
     *
     * This function does not require that the filesystem is online at the time
     * it is invoked. The returned object is nevertheless valid.
     * Use the method YFiles.isOnline() to test if the filesystem is
     * indeed online at a given time. In case of ambiguity when looking for
     * a filesystem by logical name, no error is notified: the first instance
     * found is returned. The search is performed first by hardware name,
     * then by logical name.
     *
     * @param yctx : a YAPI context
     * @param func : a string that uniquely characterizes the filesystem, for instance
     *         YRGBLED2.files.
     *
     * @return a YFiles object allowing you to drive the filesystem.
     */
    public static YFiles FindFilesInContext(YAPIContext yctx,String func)
    {
        YFiles obj;
        synchronized (yctx._functionCacheLock) {
            obj = (YFiles) YFunction._FindFromCacheInContext(yctx, "Files", func);
            if (obj == null) {
                obj = new YFiles(yctx, func);
                YFunction._AddToCache("Files", func, obj);
            }
        }
        return obj;
    }

    /**
     * Registers the callback function that is invoked on every change of advertised value.
     * The callback is invoked only during the execution of ySleep or yHandleEvents.
     * This provides control over the time when the callback is triggered. For good responsiveness, remember to call
     * one of these two functions periodically. To unregister a callback, pass a null pointer as argument.
     *
     * @param callback : the callback function to call, or a null pointer. The callback function should take two
     *         arguments: the function object of which the value has changed, and the character string describing
     *         the new advertised value.
     *
     */
    public int registerValueCallback(UpdateCallback callback)
    {
        String val;
        if (callback != null) {
            YFunction._UpdateValueCallbackList(this, true);
        } else {
            YFunction._UpdateValueCallbackList(this, false);
        }
        _valueCallbackFiles = callback;
        // Immediately invoke value callback with current value
        if (callback != null && isOnline()) {
            val = _advertisedValue;
            if (!(val.equals(""))) {
                _invokeValueCallback(val);
            }
        }
        return 0;
    }

    @Override
    public int _invokeValueCallback(String value)
    {
        if (_valueCallbackFiles != null) {
            _valueCallbackFiles.yNewValue(this, value);
        } else {
            super._invokeValueCallback(value);
        }
        return 0;
    }

    public byte[] sendCommand(String command) throws YAPI_Exception
    {
        String url;
        url = String.format(Locale.US, "files.json?a=%s",command);

        return _download(url);
    }

    public int _getVersion() throws YAPI_Exception
    {
        byte[] json = new byte[0];
        if (_ver > 0) {
            return _ver;
        }
        //may throw an exception
        json = sendCommand("info");
        if ((json[0] & 0xff) != 123) {
            // ascii code for '{'
            _ver = 30;
        } else {
            _ver = YAPIContext._atoi(_json_get_key(json, "ver"));
        }
        return _ver;
    }

    /**
     * Reinitialize the filesystem to its clean, unfragmented, empty state.
     * All files previously uploaded are permanently lost.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int format_fs() throws YAPI_Exception
    {
        byte[] json = new byte[0];
        String res;
        json = sendCommand("format");
        res = _json_get_key(json, "res");
        //noinspection DoubleNegation
        if (!(res.equals("ok"))) { throw new YAPI_Exception(YAPI.IO_ERROR, "format failed");}
        return YAPI.SUCCESS;
    }

    /**
     * Returns a list of YFileRecord objects that describe files currently loaded
     * in the filesystem.
     *
     * @param pattern : an optional filter pattern, using star and question marks
     *         as wild cards. When an empty pattern is provided, all file records
     *         are returned.
     *
     * @return a list of YFileRecord objects, containing the file path
     *         and name, byte size and 32-bit CRC of the file content.
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<YFileRecord> get_list(String pattern) throws YAPI_Exception
    {
        byte[] json = new byte[0];
        ArrayList<byte[]> filelist = new ArrayList<>();
        ArrayList<YFileRecord> res = new ArrayList<>();
        json = sendCommand(String.format(Locale.US, "dir&f=%s",pattern));
        filelist = _json_get_array(json);
        res.clear();
        for (byte[] ii_0:filelist) {
            res.add(new YFileRecord(new String(ii_0, _yapi._deviceCharset)));
        }
        return res;
    }

    /**
     * Tests if a file exists on the filesystem of the module.
     *
     * @param filename : the filename to test.
     *
     * @return true if the file exists, false otherwise.
     *
     * @throws YAPI_Exception on error
     */
    public boolean fileExist(String filename) throws YAPI_Exception
    {
        byte[] json = new byte[0];
        ArrayList<byte[]> filelist = new ArrayList<>();
        if (filename.length() == 0) {
            return false;
        }
        json = sendCommand(String.format(Locale.US, "dir&f=%s",filename));
        filelist = _json_get_array(json);
        if (filelist.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Downloads the requested file and returns a binary buffer with its content.
     *
     * @param pathname : path and name of the file to download
     *
     * @return a binary buffer with the file content
     *
     * @throws YAPI_Exception on error
     */
    public byte[] download(String pathname) throws YAPI_Exception
    {
        return _download(pathname);
    }

    /**
     * Uploads a file to the filesystem, to the specified full path name.
     * If a file already exists with the same path name, its content is overwritten.
     *
     * @param pathname : path and name of the new file to create
     * @param content : binary buffer with the content to set
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int upload(String pathname,byte[] content) throws YAPI_Exception
    {
        return _upload(pathname, content);
    }

    /**
     * Deletes a file, given by its full path name, from the filesystem.
     * Because of filesystem fragmentation, deleting a file may not always
     * free up the whole space used by the file. However, rewriting a file
     * with the same path name will always reuse any space not freed previously.
     * If you need to ensure that no space is taken by previously deleted files,
     * you can use format_fs to fully reinitialize the filesystem.
     *
     * @param pathname : path and name of the file to remove.
     *
     * @return YAPI.SUCCESS if the call succeeds.
     *
     * @throws YAPI_Exception on error
     */
    public int remove(String pathname) throws YAPI_Exception
    {
        byte[] json = new byte[0];
        String res;
        json = sendCommand(String.format(Locale.US, "del&f=%s",pathname));
        res  = _json_get_key(json, "res");
        //noinspection DoubleNegation
        if (!(res.equals("ok"))) { throw new YAPI_Exception(YAPI.IO_ERROR, "unable to remove file");}
        return YAPI.SUCCESS;
    }

    /**
     * Returns the expected file CRC for a given content.
     * Note that the CRC value may vary depending on the version
     * of the filesystem used by the hub, so it is important to
     * use this method if a reference value needs to be computed.
     *
     * @param content : a buffer representing a file content
     *
     * @return the 32-bit CRC summarizing the file content, as it would
     *         be returned by the get_crc() method of
     *         YFileRecord objects returned by get_list().
     */
    public int get_content_crc(byte[] content) throws YAPI_Exception
    {
        int fsver;
        int sz;
        int blkcnt;
        byte[] meta = new byte[0];
        int blkidx;
        int blksz;
        int part;
        int res;
        sz = (content).length;
        if (sz == 0) {
            res = YAPI._bincrc(content, 0, 0);
            return res;
        }

        fsver = _getVersion();
        if (fsver < 40) {
            res = YAPI._bincrc(content, 0, sz);
            return res;
        }
        blkcnt = ((sz + 255) / 256);
        meta = new byte[4 * blkcnt];
        blkidx = 0;
        while (blkidx < blkcnt) {
            blksz = sz - blkidx * 256;
            if (blksz > 256) {
                blksz = 256;
            }
            part = (YAPI._bincrc(content, blkidx * 256, blksz) ^ ((int) 0xffffffff));
            meta[4 * blkidx] = (byte)((part & 255) & 0xff);
            meta[4 * blkidx + 1] = (byte)(((part >> 8) & 255) & 0xff);
            meta[4 * blkidx + 2] = (byte)(((part >> 16) & 255) & 0xff);
            meta[4 * blkidx + 3] = (byte)(((part >> 24) & 255) & 0xff);
            blkidx = blkidx + 1;
        }
        res = (YAPI._bincrc(meta, 0, 4 * blkcnt) ^ ((int) 0xffffffff));
        return res;
    }

    /**
     * Continues the enumeration of filesystems started using yFirstFiles().
     * Caution: You can't make any assumption about the returned filesystems order.
     * If you want to find a specific a filesystem, use Files.findFiles()
     * and a hardwareID or a logical name.
     *
     * @return a pointer to a YFiles object, corresponding to
     *         a filesystem currently online, or a null pointer
     *         if there are no more filesystems to enumerate.
     */
    public YFiles nextFiles()
    {
        String next_hwid;
        try {
            String hwid = _yapi._yHash.resolveHwID(_className, _func);
            next_hwid = _yapi._yHash.getNextHardwareId(_className, hwid);
        } catch (YAPI_Exception ignored) {
            next_hwid = null;
        }
        if(next_hwid == null) return null;
        return FindFilesInContext(_yapi, next_hwid);
    }

    /**
     * Starts the enumeration of filesystems currently accessible.
     * Use the method YFiles.nextFiles() to iterate on
     * next filesystems.
     *
     * @return a pointer to a YFiles object, corresponding to
     *         the first filesystem currently online, or a null pointer
     *         if there are none.
     */
    public static YFiles FirstFiles()
    {
        YAPIContext yctx = YAPI.GetYCtx(false);
        if (yctx == null)  return null;
        String next_hwid = yctx._yHash.getFirstHardwareId("Files");
        if (next_hwid == null)  return null;
        return FindFilesInContext(yctx, next_hwid);
    }

    /**
     * Starts the enumeration of filesystems currently accessible.
     * Use the method YFiles.nextFiles() to iterate on
     * next filesystems.
     *
     * @param yctx : a YAPI context.
     *
     * @return a pointer to a YFiles object, corresponding to
     *         the first filesystem currently online, or a null pointer
     *         if there are none.
     */
    public static YFiles FirstFilesInContext(YAPIContext yctx)
    {
        String next_hwid = yctx._yHash.getFirstHardwareId("Files");
        if (next_hwid == null)  return null;
        return FindFilesInContext(yctx, next_hwid);
    }

    //--- (end of generated code: YFiles implementation)
}

