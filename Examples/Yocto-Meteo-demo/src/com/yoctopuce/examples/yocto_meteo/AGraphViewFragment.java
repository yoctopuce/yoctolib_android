package com.yoctopuce.examples.yocto_meteo;


import org.achartengine.GraphicalView;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.yoctopuce.examples.yocto_meteo.LiveGraph.PositionChangeListener;

public class AGraphViewFragment extends Fragment
{
	private LiveGraph 			_tempGraph = new LiveGraph("Temperarture","Celcius",Color.YELLOW); 
	private GraphicalView 		_tempGraphView;
	private LiveGraph 			_humGraph = new LiveGraph("Humidity","%Rh",Color.CYAN); 
	private GraphicalView 		_humGraphView;
	private LiveGraph 			_pressGraph = new LiveGraph("Atmospheric pressure","mbar",Color.WHITE); 
	private GraphicalView 		_pressGraphView;

	private final PositionChangeListener _posChange = new PositionChangeListener()
	{
		
		@Override
		public void positionHasChanged(LiveGraph graph, long x_range, long x_max)
		{
			_tempGraph.setPos(x_range,x_max);
			_humGraph.setPos(x_range, x_max);
			_pressGraph.setPos(x_range, x_max);
		}
	};
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.agraph_view, container, false);
		LayoutParams param = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1.0f);
		_tempGraph.registerPostitionChangeListener(_posChange);
		_tempGraphView = _tempGraph.getView(getActivity());
		_tempGraphView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.addView(_tempGraphView,param);
	
		_humGraph.registerPostitionChangeListener(_posChange);
		_humGraphView = _humGraph.getView(getActivity());
		_humGraphView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.addView(_humGraphView,param);

		_pressGraph.registerPostitionChangeListener(_posChange);
		_pressGraphView = _pressGraph.getView(getActivity());
		_pressGraphView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.addView(_pressGraphView,param);
		
		return layout;	
	
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	
	public void AddValue(PointToPlot val)
	{
		_tempGraph.addNewPoints(val.getTime(),val.getTemperature());
		_humGraph.addNewPoints(val.getTime(), val.getHumidity());
		_pressGraph.addNewPoints(val.getTime(), val.getPressure());
		_tempGraphView.repaint();
		_humGraphView.repaint();
		_pressGraphView.repaint();
	}

	

}
