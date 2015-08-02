package com.yoctopuce.yocto_firmware;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YFirmwareUpdate;
import com.yoctopuce.YoctoAPI.YModule;


public class DoUpdateActivity extends Activity {

    private static final String TAG = "FirmwareUpdateActivity";
    public static final String ARG_SERIAL = "serial";
    public static final String ARG_PRODUCT = "product";
    public static final String ARG_FIRMWARE = "firmware";

    private String _serial;
    private String _product;
    private String _firmware;
    private ProgressBar _progressBar;
    private TextView _messageTextView;
    private Button _continueButton;
    private YoctolibManager _yoctolibManager;
    private TextView _warningTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setContentView(R.layout.activity_do_update);

        Intent intent = getIntent();
        _serial = intent.getStringExtra(ARG_SERIAL);
        _product = intent.getStringExtra(ARG_PRODUCT);
        _firmware = intent.getStringExtra(ARG_FIRMWARE);

        // get widget references
        TextView serialTextView = (TextView) findViewById(R.id.serial);
        serialTextView.setText(_serial);
        TextView productTextView = (TextView) findViewById(R.id.product);
        productTextView.setText(_product);
        _warningTextView = (TextView) findViewById(R.id.progress_headline);
        _progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        _messageTextView = (TextView) findViewById(R.id.progress_msg);
        _continueButton = (Button) findViewById(R.id.continue_button);
        _yoctolibManager = YoctolibManager.Get(this);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        //todo: check if it better to move that to onstart or to a service
        try {
            _yoctolibManager.StartUsage();
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        USBDevicePluggedActivity.SetActivity(null);
        new DoFirmwareUpdate().execute(_serial);

    }


    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        _yoctolibManager.StopUsage();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    public void continueButtonPressed(View view) {
        finish();
    }


    public class DoFirmwareUpdate extends AsyncTask<String, FirmwareUpdateProgress, FirmwareUpdateProgress> {


        protected FirmwareUpdateProgress doInBackground(String... serials) {
            try {
                // check if firmware is valid
                String newfirm = YFirmwareUpdate.CheckFirmware(_serial, _firmware, 0);
                if (newfirm.equals("")) {
                    return new FirmwareUpdateProgress(0, "up to date");
                } else {
                    // execute the firmware upgrade
                    YFirmwareUpdate update;
                    if (_product.equals("Bootloader")) {
                        update = new YFirmwareUpdate(_serial, _firmware, null);
                    } else {
                        YModule module = YModule.FindModule(_serial + ".module");
                        update = module.updateFirmware(newfirm);
                    }
                    int status = update.startUpdate();
                    do {
                        int new_status = update.get_progress();
                        if (new_status != status)
                            publishProgress(new FirmwareUpdateProgress(status, update.get_progressMessage()));
                        YAPI.Sleep(500);
                        status = new_status;
                    } while (status < 100 && status >= 0);
                    return new FirmwareUpdateProgress(status, update.get_progressMessage());
                }
            } catch (YAPI_Exception e) {
                e.printStackTrace();
                return new FirmwareUpdateProgress(e.errorType, e.getLocalizedMessage());
            }
        }

        protected void onProgressUpdate(FirmwareUpdateProgress... progress) {
            for (FirmwareUpdateProgress p : progress) {
                if (p._progress >= 0) {
                    _progressBar.setProgress(p._progress);
                }
                _messageTextView.setText(p._message);
            }
        }

        protected void onPostExecute(FirmwareUpdateProgress result) {
            if (result._progress >= 100) {
                _progressBar.setProgress(100);
                _messageTextView.setText("");
                _warningTextView.setText(getString(R.string.update_success));
                _warningTextView.setTextColor(getResources().getColor(R.color.success));
            } else {
                _warningTextView.setText(R.string.error);
                _warningTextView.setTextColor(getResources().getColor(R.color.success));
                _messageTextView.setText(result._message);
            }
            _continueButton.setEnabled(true);
        }
    }


    public class FirmwareUpdateProgress {
        public final int _progress;
        public final String _message;

        public FirmwareUpdateProgress(int progress, String message) {
            _progress = progress;
            _message = message;
        }
    }

}
