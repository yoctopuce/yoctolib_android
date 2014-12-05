package com.yoctopuce.yocto_firmware;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class CheckFirmwareOnlineThread<Token> extends HandlerThread
{
    private static final String TAG = "CheckFirmwareOnline";
    private static final int CHECK_FIRMWARE = 0;

    Handler _handler;
    Map<Token, Updatable> requestMap =
            Collections.synchronizedMap(new HashMap<Token, Updatable>());

    Handler _responseHandler;
    Listener<Token> mListener;


    public interface Listener<Token> {
        void onCheckFirmwareDone(Token token, String newFirmwareURL);
    }
    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }


    public CheckFirmwareOnlineThread(Handler responseHandler)
    {
        super(TAG);
        _responseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared()
    {
        _handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == CHECK_FIRMWARE) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token).getSerial());
                    handleRequest(token);
                }
            }
        };
    }


    public void queueThumbnail(Token token, Updatable updatable)
    {
        Log.i(TAG, "check firmware for : " + updatable.getSerial());
        requestMap.put(token, updatable);
        _handler.obtainMessage(CHECK_FIRMWARE, token)
                .sendToTarget();
    }


    private void handleRequest(final Token token)
    {
        final Updatable updatable = requestMap.get(token);
        if (updatable == null)
            return;
        final String latestFirmware = updatable.checkLatestFirmware();
        Log.i(TAG, "latest firmware for " + updatable.getSerial() + " is " + latestFirmware);

        _responseHandler.post(new Runnable() {
            public void run() {
                //ensure that the list view has not recyled the cell
                if (requestMap.get(token) != updatable)
                    return;
                requestMap.remove(token);
                mListener.onCheckFirmwareDone(token, updatable.getLatestFirmwareRev());
            }
        });
    }
}