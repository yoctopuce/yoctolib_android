package com.yoctopuce.examples.yocto_graph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.Locale;

public class GraphListFragment extends ListFragment
{


    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = "GraphListFragment";
    private GraphAdapter _Adapter;
    private long _graphRange = 60000;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_activity_main);
        _Adapter = new GraphAdapter(SensorStorage.get().getSensorList());
        setListAdapter(_Adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        return inflater.inflate(R.layout.graph_list_fragment, container, false);
    }


    private final BroadcastReceiver mNeedUpdateScreen = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            _Adapter.setDataFromAnyThread(SensorStorage.get().getSensorList());
        }
    };

    private final BroadcastReceiver mNeedUpdateOneGraph = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            _Adapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onResume()
    {
        super.onResume();
        // register refresh
        IntentFilter filter = new IntentFilter(YoctopuceBgThread.ACTION_SENSOR_LIST_CHANGED);
        getActivity().registerReceiver(mNeedUpdateScreen, filter);
        filter = new IntentFilter(YoctopuceBgThread.ACTION_SENSOR_NEW_VALUE);
        getActivity().registerReceiver(mNeedUpdateOneGraph, filter);
        _Adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        getActivity().unregisterReceiver(mNeedUpdateScreen);
        getActivity().unregisterReceiver(mNeedUpdateOneGraph);
        super.onPause();
    }

    public void onRangeChange(int graphRange)
    {
        _graphRange = graphRange;
        _Adapter.notifyDataSetChanged();
    }


    private class GraphAdapter extends BaseAdapter
    {

        final Handler mHandler = new Handler();
        private ArrayList<ThreadSafeSensor> _sensors;
        private final SparseArray<SimpleLiveGraph> _liveGraphs = new SparseArray<>();

        GraphAdapter(ArrayList<ThreadSafeSensor> sensorList)
        {
            super();
            _sensors = sensorList;
        }

        @Override
        public int getCount()
        {
            return _sensors.size();
        }

        @Override
        public Object getItem(int position)
        {
            return _sensors.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.graph_list_item, parent, false);
                if (convertView == null) {
                    return null;
                }
            }
            ThreadSafeSensor sensor = (ThreadSafeSensor) getItem(position);
            // add the graphical view to the LinearLayout
            LinearLayout layout = convertView.findViewById(R.id.graph_list_item_graph);
            layout.removeAllViews();

            TextView nameView = convertView.findViewById(R.id.graph_list_item_friendlyname);

            if (nameView != null) {
                nameView.setText(sensor.getDisplayName());
            }

            if (sensor.isLoading()) {
                TextView currentValueView = convertView.findViewById(R.id.graph_list_item_currentvalue);
                String string = String.format(Locale.US, getString(R.string.loading_val), sensor.getLoading());
                currentValueView.setText(string);
            } else {
                TextView currentValueView = convertView.findViewById(R.id.graph_list_item_currentvalue);
                String value = Double.toString(sensor.getLastValue());
                currentValueView.setText(value + " " + sensor.getUnit());

                // allocate SimpleLiveGraph
                SimpleLiveGraph liveGraph = _liveGraphs.get(position, null);
                if (liveGraph == null) {
                    liveGraph = new SimpleLiveGraph(sensor.getHwId());
                    _liveGraphs.put(position, liveGraph);
                }
                // compute live Graph
                long to = System.currentTimeMillis();
                long from = to - _graphRange;
                liveGraph.updateFromSensor(sensor, from);
                int color;
                if ((position & 1) == 0) {
                    color = getActivity().getResources().
                            getColor(R.color.graph_item_serie_even);
                } else {
                    color = getActivity().getResources().
                            getColor(R.color.graph_item_serie_odd);
                }
                liveGraph.setColor(color);
                // get graphical view
                ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                GraphicalView graphicalView = liveGraph.getView(getActivity());
                graphicalView.setLayoutParams(param);

                layout.addView(graphicalView, 0);
            }
            return convertView;
        }

        void setDataFromAnyThread(final ArrayList<ThreadSafeSensor> newData)
        {
            // Enqueue work on mHandler to change the data on
            // the main thread.
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    _sensors = newData;
                    notifyDataSetChanged();
                }
            });
        }
    }


}
