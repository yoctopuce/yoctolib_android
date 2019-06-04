package com.yoctopuce.examples.yocto_graph.hubs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yoctopuce.examples.helpers.Hub;
import com.yoctopuce.examples.helpers.HubStorage;
import com.yoctopuce.examples.yocto_graph.PreferenceHubStorage;
import com.yoctopuce.examples.yocto_graph.R;

import java.util.HashMap;
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
        _adapter = new HubAdapter(_hubList);
        _hubRecyclerView.setAdapter(_adapter);
        _adapter.notifyDataSetChanged();
    }


    private class HubAdapter extends RecyclerView.Adapter<HubViewHolder> implements HubViewHolder.OnSelectListener
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
        public void onSelect(Hub hub)
        {
            /*
            Intent intent = HubDetailActivity.intentWithParams(getContext(), hub.getUuid());
            getActivity().startActivity(intent);
            */
        }

    }
}
