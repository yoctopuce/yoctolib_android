package com.yoctopuce.yocto_firmware;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;

public class AboutDialog extends DialogFragment {

    private static final String VERSION_UNAVAILABLE = "N/A";

    public static void showAbout(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_about");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new AboutDialog().show(ft, "dialog_about");
    }

    public AboutDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get app version
        Activity activity = getActivity();
        Resources resources = activity.getResources();
        PackageManager pm = activity.getPackageManager();
        String packageName = activity.getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = VERSION_UNAVAILABLE;
        }
        String app_name = resources.getString(R.string.app_name);
        // Build the about body view and append the link to see OSS licenses
        SpannableStringBuilder aboutBody = new SpannableStringBuilder();
        aboutBody.append(Html.fromHtml(getString(R.string.about_text, app_name,versionName, YAPI.GetAPIVersion())));

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") TextView aboutBodyView = (TextView) layoutInflater.inflate(R.layout.about, null);
        aboutBodyView.setText(aboutBody);
        aboutBodyView.setMovementMethod(new LinkMovementMethod());

        return new AlertDialog.Builder(activity)
                .setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(aboutBodyView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }
}
