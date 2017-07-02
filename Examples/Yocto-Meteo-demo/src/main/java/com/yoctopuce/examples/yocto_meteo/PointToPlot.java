package com.yoctopuce.examples.yocto_meteo;

import java.util.Date;

public class PointToPlot
{
	private Date   time;
	private double temperature;
	private double humidity;
	private double pressure;
	
	
	public PointToPlot(Date time,double temp, double humiditiy, double pressure)
	{
		this.time = time;
		this.temperature = temp;
		this.humidity = humiditiy;
		this.pressure = pressure;
	}

	public double getTemperature()
	{
		return temperature;
	}
	
	public double getHumidity()
	{
		return humidity;
	}
 
	public double getPressure()
	{
		return pressure;
	}

	public Date getTime()
	{
		return time;
	}
	
}
