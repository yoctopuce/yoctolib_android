package com.yoctopuce.yocto_firmware;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YFirmwareUpdate;
import com.yoctopuce.YoctoAPI.YModule;

import java.util.ArrayList;


public class CheckFirmwareActivity extends Activity implements CheckFirmwareFragment.OnFragmentInteractionListener, ConfirmUpdateFragment.ConfirmUpdateListener {

    private Handler _handler;
    private final ArrayList<Updatable> _updateables = new ArrayList<Updatable>();
    private YoctolibManager _yoctolibManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);
        if (savedInstanceState == null) {
            CheckFirmwareFragment fragment = CheckFirmwareFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        _yoctolibManager = YoctolibManager.Get(this);
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        try {
            _yoctolibManager.StartUsage();
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }
        _handler = new Handler();
        _handler.postDelayed(r, 500);
    }

    @Override
    protected void onStop()
    {
        _handler.removeCallbacks(r);
        _yoctolibManager.StopUsage();
        super.onStop();
    }

    private boolean RefreshList()
    {
        boolean changed = false;
        try {
            YAPI.UpdateDeviceList();
        } catch (YAPI_Exception e) {
            e.printStackTrace();
            return false;
        }
        YModule m = YModule.FirstModule();
        ArrayList<Updatable> toSweep = new ArrayList<Updatable>(_updateables);
        while (m != null) {
            try {
                String serialNumber = m.get_serialNumber();
                boolean add = true;
                for (Updatable u : toSweep) {
                    if (u.getSerial().equals(serialNumber)) {
                        add = false;
                        toSweep.remove(u);
                        break;
                    }
                }
                if (add) {
                    changed = true;
                    Updatable updatable = new Updatable(serialNumber, m.get_productName(), m.get_firmwareRelease(), m.get_icon2d());
                    _updateables.add(updatable);
                }
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
            m = m.nextModule();
        }
        ArrayList<String> allBootLoader = YFirmwareUpdate.GetAllBootLoaders();
        for (String bootloader : allBootLoader) {
            boolean add = true;
            for (Updatable u : toSweep) {
                if (u.getSerial().equals(bootloader)) {
                    add = false;
                    toSweep.remove(u);
                    break;
                }
            }
            if (add) {
                changed = true;
                Updatable updatable = new Updatable(bootloader);
                _updateables.add(updatable);
            }
        }

        if (toSweep.size() > 0) {
            for (Updatable u : toSweep) {
                _updateables.remove(u);
            }
            changed = true;
        }


        return changed;
    }

    final Runnable r = new Runnable()
    {
        public void run()
        {
            boolean changed = RefreshList();
            if (changed) {
                CheckFirmwareFragment fragment = (CheckFirmwareFragment) getFragmentManager().findFragmentById(R.id.container);
                fragment.refreshList();
            }
            _handler.postDelayed(this, 1000);
        }
    };


    @Override
    public ArrayList<Updatable> getUpdateableList()
    {
        return _updateables;
    }


    public void continueButtonPressed(View view)
    {

        CheckFirmwareFragment fragment = CheckFirmwareFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

    }

    @Override
    public void onDialogPositiveClick(ConfirmUpdateFragment dialog) {
        String serial = dialog.getSerial();
        String product = dialog.getProduct();
        String firmware = dialog.getFirmware();
        Intent intent = new Intent(this, DoUpdateActivity.class);
        intent.putExtra(DoUpdateActivity.ARG_SERIAL, serial);
        intent.putExtra(DoUpdateActivity.ARG_PRODUCT, product);
        intent.putExtra(DoUpdateActivity.ARG_FIRMWARE, firmware);
        startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick(ConfirmUpdateFragment dialog) {

    }
}
