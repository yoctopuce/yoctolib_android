/*********************************************************************
 * $Id: yHTTPRequest.java 64027 2025-01-06 15:18:30Z seb $
 *
 * internal yHTTPRequest object
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
 *********************************************************************/

package com.yoctopuce.YoctoAPI;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Locale;


class yHTTPRequest implements Runnable
{
    public static final int MAX_REQUEST_MS = 5000;
    private static final int YIO_IDLE_TCP_TIMEOUT = 5000;

    private Object _context;

    private YGenericHub.RequestAsyncResult _resultCallback;
    private boolean _isChuckEncoded;

    public void kill()
    {
        _requestStop();
    }

    private enum State
    {
        AVAIL, IN_REQUEST, STOPPED
    }

    private final YHTTPHub _hub;

    private Socket _socket = null;
    private boolean _reuse_socket = false;
    private OutputStream _out = null;
    private InputStream _in = null;
    private State _state = State.AVAIL;
    private boolean _eof;
    private String _firstLine;
    private byte[] _rest_of_request;
    private final String _dbglabel;
    private final StringBuilder _header = new StringBuilder(1024);
    private Boolean _header_found;
    private final ByteArrayOutputStream _result = new ByteArrayOutputStream(4096);
    private long _startRequestTime;
    private long _lastReceiveTime;
    private long _requestTimeout;

    yHTTPRequest(YHTTPHub hub, String dbglabel)
    {
        _hub = hub;
        _dbglabel = dbglabel;
    }

    static byte[] yTcpDownloadEx(String host, int port, String path) throws YAPI_Exception
    {
        try {
            InetAddress addr = InetAddress.getByName(host);
            Socket socket = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(addr, port);
            socket.connect(sockaddr);
            String request = String.format("GET %s HTTP/1.1\r\n", path);
            request += String.format("Host: %s\r\nConnection: close\r\n", host);
            request += "Accept-Encoding:\r\nUser-Agent: Yoctopuce\r\n\r\n";
            socket.setTcpNoDelay(true);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            out.write(request.getBytes());
            ByteArrayOutputStream result = new ByteArrayOutputStream(8096);
            byte[] buffer = new byte[2048];
            int bytesRead;
            do {
                bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    result.write(buffer, 0, bytesRead);
                }
            } while (bytesRead >= 0);
            return result.toByteArray();
        } catch (IOException e) {
            throw new YAPI_Exception(YAPI.IO_ERROR, String.format("unable to contact %s:%d (%s)", host, port, e.getLocalizedMessage()), e);
        }
    }

    static byte[] yTcpDownload(YAPIContext ctx, String host, int port, String path) throws YAPI_Exception
    {
        byte[] raw = yTcpDownloadEx(host, port, path);
        String str_raw = new String(raw);
        int pos = str_raw.indexOf("\r\n\r\n");
        if (pos <= 0) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "Invalid HTTP response header");
        }
        pos += 4;
        String header = str_raw.substring(0, pos);
        byte[] content = Arrays.copyOfRange(raw, pos, raw.length);
        if (header.startsWith("0K\r\n") || header.startsWith("OK\r\n")) {
            return content;
        }
        int lpos = header.indexOf("\r\n");
        if (!header.startsWith("HTTP/1.1 "))
            throw new YAPI_Exception(YAPI.IO_ERROR, "Invalid HTTP response header");

        String[] parts = header.substring(9, lpos).split(" ");
        if (parts[0].equals("401")) {
            throw new YAPI_Exception(YAPI.UNAUTHORIZED, "Authentication required");
        }
        String header_low = header.toLowerCase();
        if (parts[0].equals("301") || parts[0].equals("302") || parts[0].equals("308")) {
            int t_ofs = header_low.indexOf("\r\nlocation:");
            if (t_ofs > 0) {
                t_ofs += 11;
                int t_endl = header_low.indexOf("\r\n", t_ofs);
                String new_url = header.substring(t_ofs, t_endl);
                new_url = new_url.trim();
                if (new_url.startsWith("http")) {
                    return ctx.BasicHTTPRequest(new_url,YHTTPHub.YIO_DEFAULT_TCP_TIMEOUT,0);
                } else {
                    return yTcpDownload(ctx,host, port, new_url);
                }
            }
        }
        if (!parts[0].equals("200") && !parts[0].equals("304")) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "Received HTTP status " + parts[0] + " (" + parts[1] + ")");
        } else {
            int t_ofs = header_low.indexOf("transfer-encoding");
            if (t_ofs > 0) {
                t_ofs += 17;
                int t_endl = header_low.indexOf("\r\n", t_ofs);
                int t_chunk = header_low.indexOf("chunked", t_ofs);
                if (t_chunk > 0 && t_chunk < t_endl) {
                    content = unpackHTTPRequest(content);
                }
            }
        }
        return content;
    }

    synchronized void _requestReserve() throws YAPI_Exception
    {
        long timeout = YAPI.GetTickCount() + MAX_REQUEST_MS + 1000;
        while (timeout > YAPI.GetTickCount() && _state != State.AVAIL) {
            try {
                long toWait = timeout - YAPI.GetTickCount();
                wait(toWait);
            } catch (InterruptedException ie) {
                throw new YAPI_Exception(YAPI.TIMEOUT, "Last Http request did not finished");
            }
        }
        if (_state != State.AVAIL)
            throw new YAPI_Exception(YAPI.TIMEOUT, "Last Http request did not finished");
        _state = State.IN_REQUEST;
    }

    synchronized void _requestRelease()
    {
        _state = State.AVAIL;
        notify();
    }


    void _requestStart(String firstLine, byte[] rest_of_request, long mstimeout, Object context,
                       YGenericHub.RequestAsyncResult resultCallback) throws YAPI_Exception
    {
        byte[] full_request;
        _firstLine = firstLine;
        _rest_of_request = rest_of_request;
        _context = context;
        _startRequestTime = System.currentTimeMillis();
        _requestTimeout = mstimeout;
        _resultCallback = resultCallback;
        boolean persistent = "&.".equals(firstLine.substring(firstLine.length() - 2));
        String header = "";

        int cur = 0;
        int ofs = firstLine.indexOf(" ");
        header += firstLine.substring(0, ofs + 1);
        cur = ofs + 1;
        header += _hub._runtime_http_params.getSubDomain();
        ofs = firstLine.indexOf(" ", cur);
        if (ofs < 0) {
            ofs = firstLine.indexOf("\r", cur);
            if (ofs < 0) {
                ofs = firstLine.length();
            }
        }
        header += firstLine.substring(cur, ofs);
        if (_hub._usePureHTTP) {
            header += " HTTP/1.1\r\n";
        } else {
            header += " \r\n";
        }
        header += _hub.getAuthorization(header);
        if (_hub._usePureHTTP) {
            header += "Host: " + _hub.getHost() + "\r\n";
        }
        if (!persistent || _hub._usePureHTTP) {
            header += "Connection: close\r\n";
        }
        if (rest_of_request == null) {
            header += "\r\n";
            full_request = header.getBytes(_hub._yctx._deviceCharset);
        } else {
            int len = header.length();
            full_request = new byte[len + rest_of_request.length];
            System.arraycopy(header.getBytes(_hub._yctx._deviceCharset), 0, full_request, 0, len);
            System.arraycopy(rest_of_request, 0, full_request, len, rest_of_request.length);
        }
        boolean retry;
        do {
            retry = false;
            try {
                if (!_reuse_socket) {
                    InetAddress addr = InetAddress.getByName(_hub.getHost());
                    // Creates an connected socket
                    _socket = _hub.OpenConnectedSocket(addr, _hub.getPort(), (int)mstimeout);
                    _socket.setTcpNoDelay(true);
                    _out = _socket.getOutputStream();
                    _in = _socket.getInputStream();
                }
                _result.reset();
                _header.setLength(0);
                _header_found = false;
                _isChuckEncoded = false;
                _eof = false;

            } catch (UnknownHostException e) {
                _requestStop();
                throw new YAPI_Exception(YAPI.INVALID_ARGUMENT, "Unknown host(" + _hub.getHost() + ")");
            } catch (IOException e) {
                _requestStop();
                throw new YAPI_Exception(YAPI.IO_ERROR, e.getLocalizedMessage());
            }

            // write request
            try {
                _out.write(full_request);
                _out.flush();
                _lastReceiveTime = -1;
                if (_reuse_socket) {
                    // it's a reusable socket read some data
                    // to ensure socket is not closed by remote host
                    _socket.setSoTimeout(1);
                    int b = _in.read();
                    if (b < 0) {
                        // end of connection
                        retry = true;
                    } else {
                        _header.append((char) b);
                    }
                }
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                if (_reuse_socket) {
                    retry = true;
                } else {
                    _requestStop();
                    throw new YAPI_Exception(YAPI.IO_ERROR, e.getLocalizedMessage());
                }
            }
            _reuse_socket = false;
        } while (retry);

    }


    void _requestStop()
    {
        if (!_reuse_socket) {
            if (_out != null) {
                try {
                    _out.close();
                } catch (IOException ignored) {
                }
                _out = null;
            }
            if (_in != null) {
                try {
                    _in.close();
                } catch (IOException ignored) {
                }
                _in = null;
            }
            if (_socket != null) {
                try {
                    _socket.close();
                } catch (IOException ignored) {
                }
                _socket = null;
            }
        }
    }

    private void _requestReset() throws YAPI_Exception
    {
        _requestStop();
        _requestStart(_firstLine, _rest_of_request, _requestTimeout, _context, _resultCallback);
    }


    int _requestProcesss() throws YAPI_Exception
    {
        boolean retry;
        int read = 0;


        if (_eof)
            return -1;

        do {
            retry = false;
            byte[] buffer = new byte[1024];
            try {
                if (_requestTimeout > 0) {
                    long read_timeout = _startRequestTime + _requestTimeout - System.currentTimeMillis();
                    if (read_timeout < 0) {
                        throw new YAPI_Exception(YAPI.TIMEOUT, String.format(Locale.US, "Hub did not send data during %dms", System.currentTimeMillis() - _lastReceiveTime));
                    }
                    if (read_timeout > YIO_IDLE_TCP_TIMEOUT) {
                        read_timeout = YIO_IDLE_TCP_TIMEOUT;
                    }
                    _socket.setSoTimeout((int) read_timeout);
                } else {
                    _socket.setSoTimeout(YIO_IDLE_TCP_TIMEOUT);
                }
                read = _in.read(buffer, 0, buffer.length);
            } catch (SocketTimeoutException e) {
                long nowTime = System.currentTimeMillis();
                if (_lastReceiveTime < 0 || nowTime - _lastReceiveTime < YIO_IDLE_TCP_TIMEOUT) {
                    retry = true;
                    continue;
                }
                long duration = nowTime - _startRequestTime;
                // global request timeout
                if (duration > _requestTimeout) {
                    throw new YAPI_Exception(YAPI.TIMEOUT, String.format(Locale.US, "TCP request on %s took too long (%dms)", _hub.getHost(), duration));
                } else if (duration > (_requestTimeout - _requestTimeout / 4)) {
                    _hub._yctx._Log(String.format(Locale.US, "Slow TCP request on %s (%dms)\n", _hub.getHost(), duration));
                }
                retry = true;
                continue;
            } catch (IOException e) {
                throw new YAPI_Exception(YAPI.IO_ERROR, e.getLocalizedMessage());
            }
            if (read < 0) {
                // end of connection
                _reuse_socket = false;
                _eof = true;
            } else if (read > 0) {
                _lastReceiveTime = System.currentTimeMillis();
                synchronized (_result) {
                    if (!_header_found) {
                        String partial_head = new String(buffer, 0, read, _hub._yctx._deviceCharset);
                        _header.append(partial_head);
                        int pos = _header.indexOf("\r\n\r\n");
                        if (pos > 0) {
                            pos += 4;
                            try {
                                _result.write(_header.substring(pos).getBytes(_hub._yctx._deviceCharset));
                            } catch (IOException ex) {
                                throw new YAPI_Exception(YAPI.IO_ERROR, ex.getLocalizedMessage());
                            }
                            _header_found = true;
                            _header.setLength(pos);
                            if (_header.indexOf("0K\r\n") == 0) {
                                _reuse_socket = true;
                            } else if (_header.indexOf("OK\r\n") != 0) {
                                int lpos = _header.indexOf("\r\n");
                                if (_header.indexOf("HTTP/1.1 ") != 0)
                                    throw new YAPI_Exception(YAPI.IO_ERROR, "Invalid HTTP response header");

                                String parts[] = _header.substring(9, lpos).split(" ");
                                if (parts[0].equals("401")) {
                                    if (!_hub.needRetryWithAuth()) {
                                        // No credential provided, give up immediately
                                        throw new YAPI_Exception(YAPI.UNAUTHORIZED, "Authentication required");
                                    } else {
                                        _hub.parseWWWAuthenticate(_header.toString());
                                        _requestReset();
                                        break;
                                    }
                                }
                                if (!parts[0].equals("200") && !parts[0].equals("304")) {
                                    throw new YAPI_Exception(YAPI.IO_ERROR, "Received HTTP status " + parts[0] + " (" + parts[1] + ")");
                                } else {
                                    String header_low = _header.toString().toLowerCase();
                                    int t_ofs = header_low.indexOf("transfer-encoding");
                                    if (t_ofs > 0) {
                                        t_ofs += 17;
                                        int t_endl = header_low.indexOf("\r\n", t_ofs);
                                        int t_chunk = header_low.indexOf("chunked", t_ofs);
                                        if (t_chunk > 0 && t_chunk < t_endl) {
                                            _isChuckEncoded = true;
                                        }
                                    }
                                }
                            }
                            _hub.authSucceded();
                        }
                    } else {
                        _result.write(buffer, 0, read);
                    }
                    if (_reuse_socket) {
                        if (_result.toString().endsWith("\r\n")) {
                            _eof = true;
                        }
                    }
                }
            }
        } while (retry);
        return read;
    }


    byte[] getPartialResult() throws YAPI_Exception
    {
        byte[] res;
        synchronized (_result) {
            if (!_header_found)
                return null;
            if (_result.size() == 0) {
                if (_eof)
                    throw new YAPI_Exception(YAPI.NO_MORE_DATA, "end of file reached");
                return null;
            }
            res = _result.toByteArray();
            _result.reset();
        }
        return res;
    }


    byte[] RequestSync(String req_first_line, byte[] req_head_and_body, int mstimeout) throws YAPI_Exception
    {
        byte[] res;
        _requestReserve();
        try {
            _requestStart(req_first_line, req_head_and_body, mstimeout, null, null);
            int read;
            do {
                read = _requestProcesss();
            } while (read >= 0);
            synchronized (_result) {
                res = _result.toByteArray();
                _result.reset();
                if (_isChuckEncoded) {
                    res = unpackHTTPRequest(res);
                }
            }
        } catch (YAPI_Exception ex) {
            _requestStop();
            _requestRelease();
            throw ex;

        }
        _requestStop();
        _requestRelease();
        return res;
    }

    private static byte[] unpackHTTPRequest(byte[] data)
    {
        ByteArrayOutputStream res = new ByteArrayOutputStream(data.length);
        int ofs = 0;
        do {
            StringBuilder hex_str = new StringBuilder();
            char c;
            while (ofs < data.length && (c = (char) data[ofs]) != '\n') {
                if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
                    hex_str.append(c);
                }
                ofs++;
            }
            if (ofs < data.length) {
                int len;
                try {
                    len = Integer.parseInt(hex_str.toString(), 16);
                } catch (NumberFormatException ex) {
                    len = 0;
                }
                if (ofs + 3 + len < data.length) {
                    ofs++;
                    res.write(data, ofs, len);
                    ofs += len + 2;// skip last \r\n
                } else {
                    ofs++;
                }
            }
        } while (ofs < data.length);
        return res.toByteArray();
    }

    public void run()
    {
        byte[] res = null;
        int errorType = YAPI.SUCCESS;
        String errmsg = null;
        try {
            _requestProcesss();
            int read;
            do {
                read = _requestProcesss();
            } while (read >= 0);
            synchronized (_result) {
                res = _result.toByteArray();
                _result.reset();
            }
        } catch (YAPI_Exception ex) {
            errorType = ex.errorType;
            errmsg = ex.getMessage();
            _hub._yctx._Log("ASYNC request " + _firstLine + "failed:" + errmsg + "\n");
        }
        _requestStop();
        if (_resultCallback != null) {
            _resultCallback.RequestAsyncDone(_context, res, errorType, errmsg);
        }
        _requestRelease();
    }


    void RequestAsync(String req_first_line, byte[] req_head_and_body, YGenericHub.RequestAsyncResult callback, Object context) throws YAPI_Exception
    {
        _requestReserve();
        try {
            _requestStart(req_first_line, req_head_and_body, _hub._networkTimeoutMs, context, callback);
            Thread t = new Thread(this);
            t.setName(_dbglabel);
            t.start();
        } catch (YAPI_Exception ex) {
            _requestRelease();
            throw ex;
        }
    }


    synchronized void WaitRequestEnd(long mstimeout) throws InterruptedException
    {
        long timeout = YAPI.GetTickCount() + mstimeout;
        while (timeout > YAPI.GetTickCount() && _state == State.IN_REQUEST) {
            long toWait = timeout - YAPI.GetTickCount();
            wait(toWait);
        }
        if (_state == State.IN_REQUEST)
            _hub._yctx._Log("WARNING: Last Http request did not finished");
        // ensure that we close all socket
        _reuse_socket = false;
        _requestStop();
        _state = State.STOPPED;
    }


}
