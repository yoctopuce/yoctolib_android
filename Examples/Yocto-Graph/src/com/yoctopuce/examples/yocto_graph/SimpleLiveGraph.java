package com.yoctopuce.examples.yocto_graph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class SimpleLiveGraph
{

	private XYSeries _serie;
    private XYSeriesRenderer _serieRenderer;
    private XYMultipleSeriesDataset _multipleDataset;
	private XYMultipleSeriesRenderer _multipleRenderer;

    private long _lastUpdateFrom=-1;
    private long _lastUpdateTo=-1;

    // The gesture threshold expressed in dp
    private static final float X_LABEL_SIZE_IN_DP = 16.0f;

    public SimpleLiveGraph(String name)
	{
		_serie = new TimeSeries(name);
        _serieRenderer = new XYSeriesRenderer();
        _serieRenderer.setLineWidth(3);
		// Add single dataset to multiple dataset
        _multipleDataset = new XYMultipleSeriesDataset();
        _multipleDataset.addSeries(_serie);
        _multipleRenderer = new XYMultipleSeriesRenderer();
        _multipleRenderer.setZoomButtonsVisible(false);
        _multipleRenderer.addSeriesRenderer(_serieRenderer);
        _multipleRenderer.setShowLegend(false);
        _multipleRenderer.setZoomEnabled(false);
        _multipleRenderer.setPanEnabled(false);
        _multipleRenderer.setYLabels(3);
        _multipleRenderer.setShowGridX(true);
        _multipleRenderer.setAxesColor(Color.TRANSPARENT);
        _multipleRenderer.setGridColor(Color.GRAY);
        _multipleRenderer.setYAxisAlign(Paint.Align.RIGHT,0);

    }


    public void updateFromSensor(ThreadSafeSensor sensor, long fromMs, long toMs)
    {
        if (_lastUpdateTo < 0) {
            _lastUpdateTo = fromMs;
        }
        double fromS = (_lastUpdateTo / 1000);
        double toS = (toMs / 1000);
        sensor.fillGraphSerie(_serie, fromS, toS);
        _lastUpdateTo = toMs;
        _lastUpdateFrom = fromMs;
        _multipleRenderer.setXAxisMin(fromMs);
        _multipleRenderer.setXAxisMax(toMs);
    }


    public GraphicalView getView(Context context)
    {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        int textsize = (int) (X_LABEL_SIZE_IN_DP * scale + 0.5f);
        _multipleRenderer.setLabelsTextSize(textsize);
        return ChartFactory.getTimeChartView(context, _multipleDataset, _multipleRenderer, "k:mm");
    }

    public void setColor(int color)
    {
        _serieRenderer.setColor(color);
    }


}
