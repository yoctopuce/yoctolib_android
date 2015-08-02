package com.yoctopuce.yocto_firmware;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;

import java.util.ArrayList;

public class CheckFirmwareFragment extends ListFragment implements CheckFirmwareOnlineThread.Listener<TextView>
{

    private static final String TAG = "FirmwareUpdateListFragment";
    private OnFragmentInteractionListener mListener;
    private UpdatableAdapter _adapter;
    private CheckFirmwareOnlineThread<TextView> _checkFirmwareOnlineThread;

    public static CheckFirmwareFragment newInstance()
    {
        CheckFirmwareFragment fragment = new CheckFirmwareFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CheckFirmwareFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        _adapter = new UpdatableAdapter(mListener.getUpdateableList());
        setHasOptionsMenu(true);
        setListAdapter(_adapter);
        _checkFirmwareOnlineThread = new CheckFirmwareOnlineThread<TextView>(new Handler());
        _checkFirmwareOnlineThread.setListener(this);
        _checkFirmwareOnlineThread.start();
        _checkFirmwareOnlineThread.getLooper();
        Log.i(TAG, "Background thread started");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list,parent,false);
        ListView listView = (ListView)root.findViewById(android.R.id.list);
        registerForContextMenu(listView);
        return root;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_check_firmware, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about) {
            AboutDialog.showAbout(getActivity());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.fragment_check_firmware_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.menu_item_force_update) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            int position = info.position;
            Updatable u = _adapter.getItem(position);
            startFirmwareUpdate(u.getSerial(), u.getProduct(), "www.yoctopuce.com", "");
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onResume()
    {
        super.onResume();

    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        _checkFirmwareOnlineThread.quit();

    }


    public void refreshList()
    {
        _adapter.notifyDataSetChanged();
    }


    private class UpdatableAdapter extends ArrayAdapter<Updatable>
    {
        public UpdatableAdapter(ArrayList<Updatable> updatables)
        {
            super(getActivity(), 0, updatables);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_updatable, parent,false);
            }
            // Configure the view for this Crime
            Updatable u = getItem(position);
            TextView serialTextView = (TextView) convertView.findViewById(R.id.serial);
            Resources res = getResources();
            String text = String.format(res.getString(R.string.module_description), u.getProduct(), u.getSerial());
            serialTextView.setText(text);
            TextView firmwareTextView = (TextView) convertView.findViewById(R.id.firmware);
            firmwareTextView.setText(res.getString(R.string.checking));
            _checkFirmwareOnlineThread.queueThumbnail(firmwareTextView, u);
            ImageView icon2dImageView = (ImageView) convertView.findViewById(R.id.icon2d);
            byte[] icon2d = u.getIcon2d();
            if (icon2d != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(icon2d, 0, icon2d.length);
                if (bitmap != null) {
                    icon2dImageView.setImageBitmap(bitmap);
                }
            }
            return convertView;
        }
    }

    @Override
    public void onCheckFirmwareDone(TextView textView, String newFirmwareURL)
    {
        if (isVisible()) {
            Resources res = getResources();
            if (newFirmwareURL.length() > 0) {
                String format = res.getString(R.string.firmware_description);
                String text = String.format(format, newFirmwareURL);
                textView.setText(text);
            } else {
                textView.setText(res.getString(R.string.up_to_date));
            }
        }
    }


    public interface OnFragmentInteractionListener
    {
        public ArrayList<Updatable> getUpdateableList();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);


        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Updatable item = _adapter.getItem(position);

        if (item.getLatestFirmwarePath().equals("")) {
            // no need to update dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.title_dialog_no_need_update);
            builder.setNeutralButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            startFirmwareUpdate(item.getSerial(), item.getProduct(), item.getLatestFirmwarePath(), item.getLatestFirmwareRev());
        }
    }

    private void startFirmwareUpdate(String serial, String product, String latestFirmwarePath, String latestFirmwareRev)
    {
        // update dialog : warn the user of system popup
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Create and show the dialog.
        DialogFragment newFragment = ConfirmUpdateFragment.newInstance(serial,
                product, latestFirmwarePath, latestFirmwareRev);
        newFragment.show(ft, "dialog");
    }


}
