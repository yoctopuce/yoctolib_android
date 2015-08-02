package com.yoctopuce.examples.yocto_relay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yoctopuce.YoctoAPI.YAPI;

public class RelayListFragment extends ListFragment
{
    private static final String TAG = "com.yoctopuce.examples.yocto_relay.RelayListFragment";
    //private ArrayList<Relay> mRelays;
    private RelayAdapter mAdapter;
    private RelayListStorage mYoctoSingleton;

    private BroadcastReceiver mNeedUpdateScreen = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ((RelayAdapter) getListAdapter()).notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.relay_list_title);
        mYoctoSingleton = RelayListStorage.get(getActivity());
        mAdapter = new RelayAdapter(mYoctoSingleton);
        setListAdapter(mAdapter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
        case R.id.menu_set_hub_addr:
            getHubDiallog();
            return true;
        case R.id.menu_service_control:
            boolean startService = !YoctoService.isServiceAlarmOn(getActivity());
            YoctoService.setServiceAlarm(getActivity(), startService);
            getActivity().invalidateOptionsMenu();
            return true;
        case R.id.menu_about:
            AboutDialog.showAbout(getActivity());
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggleItem = menu.findItem(R.id.menu_service_control);
        if(toggleItem==null)
            return;
        if (YoctoService.isServiceAlarmOn(getActivity()))
            toggleItem.setTitle(R.string.stop_service);
        else
            toggleItem.setTitle(R.string.start_service);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Relay r = ((RelayAdapter) getListAdapter()).getItem(position);
        r.toggle();
        Log.d(TAG, "clicked on relay " + r.toString());
        //send message to the service to toggle the relay
        Intent i = new Intent(getActivity(), YoctoService.class);
        i.putExtra(YoctoService.EXTRA_TOGGLE, r.getHwId());
        getActivity().startService(i);
        //notify everybody that something has changed
        mYoctoSingleton.notifyChanges();
        //((RelayAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class RelayAdapter extends BaseAdapter
    {
        private RelayListStorage mYoctoSingleton;


        public RelayAdapter(RelayListStorage mYoctoSingleton)
        {
            super();
            this.mYoctoSingleton = mYoctoSingleton;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Log.i(TAG, "getView for " + position);
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_relay, parent, false);
            }
            Relay r = mYoctoSingleton.getRelay(position);
            if(r!=null){
                TextView name = (TextView) convertView.findViewById(R.id.name);
                if(name!=null)
                    name.setText(r.getRelayInfo());
                TextView serial = (TextView) convertView.findViewById(R.id.serial);
                if(serial!=null)
                    serial.setText(r.getModuleName());
                CheckBox onoff = (CheckBox) convertView.findViewById(R.id.onoff_checkbox);
                if(onoff!=null)
                    onoff.setChecked(r.isOn());
            }
            return convertView;
        }

        @Override
        public int getCount()
        {
            return mYoctoSingleton.getRelayCount();
        }

        @Override
        public Relay getItem(int position)
        {
            return mYoctoSingleton.getRelay(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent i = new Intent(getActivity(), YoctoService.class);
        // register refresh
        IntentFilter filter = new IntentFilter(RelayListStorage.ACTION_RELAY_LIST_CHANGED);
        getActivity().registerReceiver(mNeedUpdateScreen, filter);
        // send a update device list to the service
        i.putExtra(YoctoService.EXTRA_REFRESH, "REFRESH");
        getActivity().startService(i);
        ((RelayAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getActivity().unregisterReceiver(mNeedUpdateScreen);
    }
    
    private void getHubDiallog()
    {
        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hub Hostname");
        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        String currentHostName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(RelayListStorage.PREF_HUB_HOSTNAME, "usb");
        input.setText(currentHostName);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { 
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newHubHostname = input.getText().toString();
                Toast.makeText(getActivity(), "Change Hub address to"+newHubHostname,   Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), YoctoService.class);
                i.putExtra(YoctoService.EXTRA_NEWHUB, newHubHostname);
                getActivity().startService(i);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
