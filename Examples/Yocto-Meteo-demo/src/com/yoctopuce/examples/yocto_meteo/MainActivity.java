package com.yoctopuce.examples.yocto_meteo;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI.LogCallback;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YHumidity;
import com.yoctopuce.YoctoAPI.YPressure;
import com.yoctopuce.YoctoAPI.YTemperature;

public class MainActivity extends Activity implements LogCallback
{

    private Thread thread;
    private PointToPlot lastVal = null;
    private boolean thread_must_stop;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart()
    {
        try {
            // register log function (for debug)
            // YAPI.RegisterLogFunction(this);
            // Pass the application Context to the Yoctopuce Library
            YAPI.EnableUSBHost(this);
            // use only directly connected devices
            YAPI.RegisterHub("usb");
        } catch (YAPI_Exception e) {
            yLog(e.getLocalizedMessage());
            e.printStackTrace();
        }

        thread = new Thread()
        {
            /*
             * Background thread that will pool value form the Yocto-Meteo twice
             * per second and create a new PointToPlot with all three value
             * (temperature humidity and pressure)
             * 
             * We have to do this job in background to prevent inteface to be
             * unresponsive (see strict mode on android developer web site)
             */
            public void run()
            {
                while (true) {
                    synchronized (this) {
                        if(thread_must_stop)
                            return;                        
                    }                    
                    try {
                        // wait half a second
                        sleep(500);
                    } catch (InterruptedException e) {
                        return;
                    }
                    
                    PointToPlot val = null;
                    try {
                        // refresh device list in case of the device
                        // has been unpluged
                        YAPI.UpdateDeviceList();
                        // get temperature
                        YTemperature temp_sensor = YTemperature.FirstTemperature();
                        if (temp_sensor == null)
                            throw new YAPI_Exception(YAPI.DEVICE_NOT_FOUND, "no temp sensor");
                        double temp = temp_sensor.getCurrentValue();
                        // get humidity
                        YHumidity hum_sensor = YHumidity.FirstHumidity();
                        if (hum_sensor == null)
                            throw new YAPI_Exception(YAPI.DEVICE_NOT_FOUND, "no humidity sensor");
                        double humiditiy = hum_sensor.getCurrentValue();
                        // get pressure
                        YPressure pres_sensor = YPressure.FirstPressure();
                        if (pres_sensor == null)
                            throw new YAPI_Exception(YAPI.DEVICE_NOT_FOUND, "no pressure sensor");
                        double pressure = pres_sensor.getCurrentValue();
                        // if we succesfully get the tree value create a new
                        // PointToPlot
                        val = new PointToPlot(new Date(), temp, humiditiy, pressure);
                    } catch (YAPI_Exception e) {
                        if (e.errorType != YAPI.DEVICE_NOT_FOUND) {
                            // real error log it and retry (this should never
                            // occure)
                            Log.e("YRUN", "Exception in backgound thread " + e.getLocalizedMessage());
                            e.printStackTrace();
                            continue;
                        }
                    }
                    // update global Variable with last mesured value...
                    lastVal = val;
                    // then update Interface and graphs form UI thread
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            AGraphViewFragment graphFrag = (AGraphViewFragment) getFragmentManager().findFragmentById(R.id.agrap_fragement);
                            if (graphFrag == null)
                                return; // fragment is not displayed -> nothing
                                        // to do
                            if (lastVal != null) {
                                // we have a valid value -> refresh interface
                                graphFrag.AddValue(lastVal);
                                TextView textfield = (TextView) findViewById(R.id.Temp);
                                textfield.setText(String.format("%.1f %s", lastVal.getTemperature(),getResources().getString(R.string.tempunit)));
                                textfield = (TextView) findViewById(R.id.Hum);
                                textfield.setText(String.format("%.1f %s", lastVal.getHumidity(),getResources().getString(R.string.humunit)));
                                textfield = (TextView) findViewById(R.id.Pres);
                                textfield.setText(String.format("%.1f %s", lastVal.getPressure(),getResources().getString(R.string.presunit)));
                            } else {
                                // we do not have a valid value -> change value
                                // to "unpluged" string
                                String unpluged = getResources().getString(R.string.unplugged);
                                TextView textfield = (TextView) findViewById(R.id.Temp);
                                textfield.setText(unpluged);
                                textfield = (TextView) findViewById(R.id.Hum);
                                textfield.setText(unpluged);
                                textfield = (TextView) findViewById(R.id.Pres);
                                textfield.setText(unpluged);

                            }
                        }
                    });
                }
            }
        };
        thread_must_stop=false;
        thread.start();
        super.onStart();

    }

    @Override
    protected void onStop()
    {
        synchronized (thread) {
            thread_must_stop=true;
        }
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        YAPI.FreeAPI();

        super.onStop();
    }

   
    public void yLog(String line)
    {
        Log.d("YOCTO", line);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_generic_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        Resources resources = getResources();
        // set application message
        String app_name = resources.getString(R.string.app_name);
        TextView textView = (TextView) messageView.findViewById(R.id.about_message);
        String format = resources.getString(R.string.about_intro);
        textView.setText(String.format(format, app_name));
        // set

        String version = "0";
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textView = (TextView) messageView.findViewById(R.id.app_version);
        textView.setText(version);

        textView = (TextView) messageView.findViewById(R.id.yapi_version);
        textView.setText(YAPI.GetAPIVersion());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }


}
