package com.yoctopuce.examples.yocto_graph.hubs;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yoctopuce.examples.helpers.Hub;
import com.yoctopuce.examples.yocto_graph.R;

class HubViewHolder extends RecyclerView.ViewHolder
{
    private TextView _urlTextView;
    private Hub _hub;

    HubViewHolder(View itemView)
    {
        super(itemView);
        _urlTextView = itemView.findViewById(R.id.item_hub_url);

    }


    void bindHub(final Hub hub, final OnSelectListener selectListener)
    {
        _hub = hub;

        _urlTextView.setText(_hub.getUrl(false));

        if (selectListener != null) {
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    selectListener.onSelect(_hub);
                }
            });
        } else {
            itemView.setOnClickListener(null);
        }
    }


    public interface OnSelectListener
    {
        void onSelect(Hub hub);
    }


}
