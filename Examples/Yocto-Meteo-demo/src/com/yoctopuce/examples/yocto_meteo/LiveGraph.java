package com.yoctopuce.examples.yocto_meteo;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import android.content.Context;
import android.util.Log;

public class LiveGraph
{

	private TimeSeries dataset;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be
																// used to
																// customize
																// line 1
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds
																					// a
																					// collection
																					// of
																					// XYSeriesRenderer
																					// and
																					// customizes
																					// the
																					// graph
	private double mYAxisMin = Double.MAX_VALUE;
	private double mYAxisMax = Double.MIN_VALUE;

	private long x_range = 15*60*1000;

	
	public interface PositionChangeListener {
		void positionHasChanged(LiveGraph graph,long x_range,long x_max);
	};
	
	PositionChangeListener _posChangeListener=null;
	
	
	public void registerPostitionChangeListener(PositionChangeListener listener)
	{
		this._posChangeListener = listener;
	}

	public LiveGraph(String name, String unit, int color)
	{
		dataset = new TimeSeries(name);
		// Add single dataset to multiple dataset
		mDataset.addSeries(dataset);

		// Customization time for line 1!
		renderer.setColor(color);
		// mRenderer.setZoomButtonsVisible(true);
		mRenderer.setZoomEnabled(true, false);
		mRenderer.setPanEnabled(true, false);
		mRenderer.setXTitle(name);
		mRenderer.setYTitle(unit);

		// Add single renderer to multiple renderer
		mRenderer.addSeriesRenderer(renderer);
	}

	public GraphicalView getView(Context context)
	{
		GraphicalView view = ChartFactory.getTimeChartView(context, mDataset, mRenderer, null);
		view.addZoomListener(mZoomListener, true, true);
		view.addPanListener(mPanListener);
		scrollGraph(new Date().getTime()+3600000);
		return view;
	}

	
	
	private final ZoomListener mZoomListener = new ZoomListener() {
		@Override
		public void zoomReset() {
			Log.i("LiveGraph","zoomReset received");
			scrollGraph(new Date().getTime());
		}

		@Override
		public void zoomApplied(final ZoomEvent event) {
	 		long xmax = (long) mRenderer.getXAxisMax();
	 		long xmin = (long) mRenderer.getXAxisMin();
	 		x_range = (xmax-xmin);
	 		if(_posChangeListener!=null)
	 			_posChangeListener.positionHasChanged(LiveGraph.this, x_range, xmax);
		}
	};

	private final PanListener mPanListener = new PanListener()
	{
		
		@Override
		public void panApplied()
		{
	 		long xmax = (long) mRenderer.getXAxisMax();
	 		long xmin = (long) mRenderer.getXAxisMin();
	 		x_range = (xmax-xmin);
	 		if(_posChangeListener!=null)
	 			_posChangeListener.positionHasChanged(LiveGraph.this, x_range, xmax);
		}
	};
	
	
	private void scrollGraph(final long time)
	{
		double padding = (mYAxisMax- mYAxisMin) / 20 +1;
		long x_front_gap=x_range/30;
		long from = time -x_range +x_front_gap;
		long to   = time + x_front_gap;
 		final double[] limits = new double[] { from, to, mYAxisMin- padding, mYAxisMax +padding };
		mRenderer.setRange(limits);
	}

	public void addNewPoints(Date d, double value)
	{
		if (mYAxisMax < value)
			mYAxisMax = value;
		if (mYAxisMin > value)
			mYAxisMin = value;

		if (dataset.getItemCount() > 24*3600 * 2) {
			dataset.remove(0);
		}
		dataset.add(d, value);
 		long xmax = (long) mRenderer.getXAxisMax();
 		long newval= d.getTime();
 		if(xmax+x_range/2 >= newval) {
 			scrollGraph(d.getTime());
 		}
		
	}

	public void setPos(long x_range2, long x_max)
	{
		x_range = x_range2;
		double padding = (mYAxisMax- mYAxisMin) / 20 +1;
		long from = x_max-x_range;
		long to   = x_max;
 		final double[] limits = new double[] { from, to, mYAxisMin- padding, mYAxisMax +padding };
		mRenderer.setRange(limits);
	
	}

}
