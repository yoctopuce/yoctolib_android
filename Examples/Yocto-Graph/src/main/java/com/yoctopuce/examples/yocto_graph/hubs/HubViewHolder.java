package com.yoctopuce.examples.yocto_graph.hubs;


import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yoctopuce.examples.helpers.Hub;
import com.yoctopuce.examples.yocto_graph.R;

class HubViewHolder extends RecyclerView.ViewHolder
{
    private TextView _urlTextView;
    private ImageView _editButton;
    private ImageView _deleteButton;
    private Hub _hub;

    HubViewHolder(View itemView)
    {
        super(itemView);
        _urlTextView = itemView.findViewById(R.id.item_hub_url);
        _editButton = itemView.findViewById(R.id.item_hub_edit);
        _deleteButton = itemView.findViewById(R.id.item_hub_delete);

    }


    void bindHub(final Hub hub, final HubHolderListener listener)
    {
        _hub = hub;

        _urlTextView.setText(_hub.getUrl(true,false));
        _editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onEdit(_hub);
            }
        });
        _deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onDelete(_hub);
            }
        });
    }


    public interface HubHolderListener
    {
        void onEdit(Hub hub);

        void onDelete(Hub hub);
    }


}
