package com.yoctopuce.examples.helpers;

import java.util.UUID;

public class Hub
{
    private final UUID _uuid;
    private final boolean _isUSB;
    private String _host;
    private int _port;
    private String _user;
    private String _pass;
    private String _proto;


    private String _subDomain;


    public Hub(boolean isUSB)
    {
        _isUSB = isUSB;
        if (_isUSB) {
            _host = "usb";
        } else {
            _port = 4444;
            _proto = "ws";
            _host = "";
            _subDomain = "";
            _user = "";
            _pass = "";
        }
        _uuid = UUID.randomUUID();
    }


    void parseURL(String url)
    {
        int pos = 0;
        if (url.startsWith("http://")) {
            pos = 7;
            _proto = "http";
        } else if (url.startsWith("wss://")) {
            pos = 6;
            _proto = "wss";
        } else if (url.startsWith("usb://")) {
            pos = 6;
            _proto = "usb";
        } else {

            if (url.startsWith("ws://")) {
                pos = 5;
            }
            _proto = "ws";
        }
        int end_auth = url.indexOf('@', pos);
        int end_user = url.indexOf(':', pos);
        if (end_user >= 0 && end_user < end_auth) {
            _user = url.substring(pos, end_user);
            _pass = url.substring(end_user + 1, end_auth);
            pos = end_auth + 1;
        } else {
            _user = "";
            _pass = "";
        }
        if (url.length() > pos && url.charAt(pos) == '@') {
            pos++;
        }
        int end_url = url.indexOf('/', pos);
        if (end_url < 0) {
            end_url = url.length();
            _subDomain = "";
        } else {
            int next_slash = url.indexOf("/", end_url + 1);
            if (next_slash < 0) {
                next_slash = url.length();
            }
            _subDomain = url.substring(end_url, next_slash);
        }
        int portpos = url.indexOf(':', pos);
        if (portpos > 0) {
            if (portpos + 1 < end_url) {
                _host = url.substring(pos, portpos);
                _port = Integer.parseInt(url.substring(portpos + 1, end_url));
            } else {
                _host = url.substring(pos, portpos);
                _port = 4444;
            }
        } else {
            _host = url.substring(pos, end_url);
            _port = 4444;
        }
    }

    public Hub(String uuid_url)
    {
        int ofs = uuid_url.indexOf("/");
        String url;
        if (ofs > 0) {
            _uuid = UUID.fromString(uuid_url.substring(0, ofs));
            url = uuid_url.substring(ofs + 1);

        } else {
            _uuid = UUID.randomUUID();
            url = uuid_url;
        }

        if (url.toLowerCase().equals("usb")) {
            _isUSB = true;
            _host = "usb";
        } else {
            _isUSB = false;
            parseURL(url);
        }
    }

    @Override
    public String toString()
    {
        return _uuid.toString() + "/" + getUrl(true, true);
    }

    public UUID getUuid()
    {
        return _uuid;
    }

    public boolean isUSB()
    {
        return _isUSB;
    }

    public String getHost()
    {
        return _host;
    }

    public void setHost(String host)
    {
        _host = host;
    }

    public int getPort()
    {
        return _port;
    }

    public void setPort(int port)
    {
        _port = port;
    }

    public String getUser()
    {
        return _user;
    }

    public void setUser(String user)
    {
        _user = user;
    }

    public String getPass()
    {
        return _pass;
    }

    public void setPass(String pass)
    {
        _pass = pass;
    }

    public String getProto()
    {
        return _proto;
    }

    public void setProto(String proto)
    {
        _proto = proto.toLowerCase();
    }

    public String getSubDomain()
    {
        if (_subDomain.length() > 0 && _subDomain.charAt(0) == '/') {
            return _subDomain.substring(1);
        }
        return _subDomain;
    }

    public void setSubDomain(String subDomain)
    {
        if (subDomain.length() > 0 && subDomain.charAt(0) != '/') {
            subDomain = '/' + subDomain;
        }
        _subDomain = subDomain;
    }

    public String getUrl(boolean withProto, boolean withUserPass)
    {
        StringBuilder url = new StringBuilder();
        if (withProto) {
            url.append(_proto).append("://");
        }
        if (withUserPass && !_user.equals("")) {
            url.append(_user);
            if (!_pass.equals("")) {
                url.append(":");
                url.append(_pass);
            }
            url.append("@");
        }
        url.append(_host);
        url.append(":");
        url.append(_port);

        url.append(_subDomain);
        return url.toString();
    }


}
