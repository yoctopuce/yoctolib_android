package com.yoctopuce.examples.helpers;

import java.util.Locale;
import java.util.UUID;

public class Hub
{
    private final boolean _isUSB;
    private String _host;
    private int _port;
    private String _user;
    private String _pass;


    public Hub(boolean isUSB)
    {
        _isUSB = isUSB;
        if (_isUSB) {
            _host = "usb";
        } else {
            _port = 4444;
            _host = "";
            _user = "";
            _pass = "";
        }
    }


    public Hub(String url)
    {
        if (url.toLowerCase().equals("usb")) {
            _isUSB = true;
            _host = "usb";
        } else {
            _isUSB = false;

            if (url.startsWith("http://")) {
                url = url.substring(7);
            } else if (url.startsWith("ws://")) {
                url = url.substring(5);
            }
            int pos = url.indexOf('/');
            if (pos >= 0) {
                url = url.substring(0, pos);
            }
            pos = url.indexOf('@');
            if (pos <= 0) {
                _user = "";
                _pass = "";
            } else {
                int pass_pos = url.indexOf(':');
                if (pass_pos < 0 || pass_pos > pos) {
                    _user = url.substring(0, pos);
                    _pass = "";
                } else {
                    _user = url.substring(0, pass_pos);
                    _pass = "";
                }
            }
            pos = url.indexOf(':');
            if (pos < 0) {
                _host = url;
            } else {
                _host = url.substring(0, pos);
                String substring = url.substring(pos + 1);
                try {
                    _port = Integer.parseInt(substring);
                } catch (NumberFormatException ex) {
                    _port = 4444;
                }
            }
        }
    }

    public Hub(String host, int port, String user, String pass)
    {
        _host = host;
        _port = port;
        _user = user;
        _pass = pass;
        _isUSB = false;
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


    public String getUrl(boolean withAuth)
    {
        if (_isUSB) {
            return "usb";
        }
        if (withAuth && _user.length() > 0) {
            return String.format(Locale.US, "%s:%s@%s:%d", _user, _pass, _host, _port);
        } else {
            return String.format(Locale.US, "%s:%d", _host, _port);

        }
    }

}
