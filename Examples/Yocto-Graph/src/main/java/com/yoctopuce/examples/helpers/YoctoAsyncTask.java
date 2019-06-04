package com.yoctopuce.examples.helpers;

import android.os.AsyncTask;

import com.yoctopuce.YoctoAPI.YAPI_Exception;

public class YoctoAsyncTask extends AsyncTask<YoctoAsyncTask.YoctoAsyncCode, Void, YAPI_Exception>
{

    public interface YoctoAsyncErr
    {
        void err(YAPI_Exception e);
    }

    public interface YoctoAsyncCode
    {
        void run() throws YAPI_Exception;
    }

    private YoctoAsyncErr _errorHandler;

    public YoctoAsyncTask(YoctoAsyncErr errHandler)
    {
        _errorHandler = errHandler;
    }

    @Override
    protected YAPI_Exception doInBackground(YoctoAsyncCode... runnables)
    {
        for (YoctoAsyncCode runnable : runnables) {
            try {
                runnable.run();
            } catch (YAPI_Exception ex) {
                return ex;
            }
            if (isCancelled()) break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(YAPI_Exception e)
    {
        if (e != null && _errorHandler != null) {
            _errorHandler.err(e);
        }
    }
}
