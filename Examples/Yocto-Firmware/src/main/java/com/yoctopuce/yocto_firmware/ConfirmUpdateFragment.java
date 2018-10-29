package com.yoctopuce.yocto_firmware;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ConfirmUpdateFragment extends DialogFragment {
    public static final String EXTRA_SERIAL = "SERIAL";
    private static final String EXTRA_PRODUCT = "PRODUCT";
    private static final String EXTRA_FIRMWARE = "FIRMWARE";
    private static final String EXTRA_FIRMWARE_REV = "FIRMWARE_REV";
    private String _serial;
    private String _product;
    private String _firmware;
    private String _firmwareRev;


    public String getSerial() {
        return _serial;
    }

    public String getProduct() {
        return _product;
    }

    public String getFirmware() {
        return _firmware;
    }

    /* The activity that creates an instance of this dialog fragment must
            * implement this interface in order to receive event callbacks.
            * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ConfirmUpdateListener {
        void onDialogPositiveClick(ConfirmUpdateFragment dialog);
        void onDialogNegativeClick(ConfirmUpdateFragment dialog);
    }



    // Use this instance of the interface to deliver action events
    ConfirmUpdateListener _listener;


    public static ConfirmUpdateFragment newInstance(String serial, String product, String firmware, String firmwareRev) {
        ConfirmUpdateFragment fragment = new ConfirmUpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SERIAL, serial);
        bundle.putString(EXTRA_PRODUCT, product);
        bundle.putString(EXTRA_FIRMWARE, firmware);
        bundle.putString(EXTRA_FIRMWARE_REV, firmwareRev);
        fragment.setArguments(bundle);
        return fragment;
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            _listener = (ConfirmUpdateListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ConfirmUpdateFragment");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        Resources resources = getResources();
        String format = resources.getString(R.string.title_dialog_confirm_update);
        _serial = arguments.getString(EXTRA_SERIAL);
        _product = arguments.getString(EXTRA_PRODUCT);
        _firmware = arguments.getString(EXTRA_FIRMWARE);
        _firmwareRev = arguments.getString(EXTRA_FIRMWARE_REV);
        String title = String.format(format, _serial);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_confirm_update, null);
        TextView paragraph = view.findViewById(R.id.paragraph1);
        format = resources.getString(R.string.dialog_confirm_update_text);
        paragraph.setText(String.format(format, _product,_firmwareRev));

        paragraph = view.findViewById(R.id.paragraph2);
        format = resources.getString(R.string.dialog_confirm_update_text2);
        paragraph.setText(String.format(format, resources.getString(R.string.app_name)));

        paragraph = view.findViewById(R.id.paragraph3);
        format = resources.getString(R.string.dialog_confirm_update_text3);
        paragraph.setText(String.format(format, resources.getString(R.string.app_name)));

        paragraph = view.findViewById(R.id.paragraph4);
        format = resources.getString(R.string.dialog_confirm_update_text4);
        paragraph.setText(String.format(format, resources.getString(R.string.app_name)));


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        _listener.onDialogPositiveClick(ConfirmUpdateFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        _listener.onDialogNegativeClick(ConfirmUpdateFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
