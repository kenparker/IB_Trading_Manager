package com.amaggioni.indicators;

/**
 * @author: Humberto Rocha Loureiro (humbertorocha@gmail.com)
 * @modify: 
 */

public class IndicatorValue {
	private final String date;
	private final double value;

	public IndicatorValue(String date, double value) {
		this.date = date;
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public double getValue() {
		return value;
	}

}
