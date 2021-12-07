package com.yoctopuce.examples.yocto_graph.hubs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yoctopuce.examples.helpers.Hub;
import com.yoctopuce.examples.helpers.HubStorage;
import com.yoctopuce.examples.yocto_graph.PreferenceHubStorage;
import com.yoctopuce.examples.yocto_graph.R;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HubListFragment extends Fragment
{
    private RecyclerView _hubRecyclerView;
    private HubAdapter _adapter;
    private HubStorage _hubStorage;
    private List<Hub> _hubList;
    private Map<UUID, Hub> _hubMap;
    private Switch _useUSBSwitch;
    private View _addButton;

    public HubListFragment()
    {
    }

    public static Fragment getInstance()
    {
        return new HubListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hub_list, container, false);
        FloatingActionButton addButton = view.findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent detailIntent = EditHubActivity.intentWithParams(getActivity());
                startActivity(detailIntent);
            }
        });
        _useUSBSwitch = view.findViewById(R.id.use_usb_switch);
        _useUSBSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                _hubStorage.setUseUSB(isChecked);
            }
        });
        _hubRecyclerView = view.findViewById(R.id.hub_list_recycler_view);
        _hubRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        _hubRecyclerView.addItemDecoration(itemDecoration);
        */
        setupUI();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void setupUI()
    {
        _hubStorage = PreferenceHubStorage.Get(getActivity());
        _hubList = _hubStorage.getHubs();
        _useUSBSwitch.setChecked(_hubStorage.useUSB());
        _adapter = new HubAdapter(_hubList);
        _hubRecyclerView.setAdapter(_adapter);
        _adapter.notifyDataSetChanged();
    }


    private class HubAdapter extends RecyclerView.Adapter<HubViewHolder> implements HubViewHolder.HubHolderListener
    {

        private List<Hub> _hubs;

        HubAdapter(List<Hub> hubs)
        {
            this._hubs = hubs;
        }

        @Override
        public HubViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_hub, parent, false);
            return new HubViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HubViewHolder holder, int position)
        {
            Hub hub = _hubs.get(position);
            holder.bindHub(hub, this);
        }

        @Override
        public int getItemCount()
        {
            return _hubs.size();
        }


        @Override
        public void onEdit(Hub hub)
        {
            Intent detailIntent = EditHubActivity.intentWithParams(getContext(), hub);
            startActivity(detailIntent);
        }

        @Override
        public void onDelete(final Hub hub)
        {
            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            _hubStorage.remove(hub.getUuid());
                            setupUI();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            FragmentActivity activity = getActivity();
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.confirmation);
                builder.setMessage(R.string.delete_confirm_msg);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, dialogClickListener);
                builder.setNegativeButton(R.string.cancel, dialogClickListener);
                builder.show();
            }
        }
    }
}
