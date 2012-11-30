package org.moten.david.log.query;

import java.util.Date;

public class NumericQuery {

	private final Date startTime;
	private final double intervalSizeMs;
	private final long numIntervals;
	private final Metric metric;
	private final String propertyName;

	public NumericQuery(Date startTime, double intervalSizeMs,
			long numIntervals, Metric metric, String propertyName) {
		super();
		this.startTime = startTime;
		this.intervalSizeMs = intervalSizeMs;
		this.numIntervals = numIntervals;
		this.metric = metric;
		this.propertyName = propertyName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public double getIntervalSizeMs() {
		return intervalSizeMs;
	}

	public long getNumIntervals() {
		return numIntervals;
	}

	public Metric getMetric() {
		return metric;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NumericQuery [startTime=");
		builder.append(startTime);
		builder.append(", intervalSizeMs=");
		builder.append(intervalSizeMs);
		builder.append(", numIntervals=");
		builder.append(numIntervals);
		builder.append(", metric=");
		builder.append(metric);
		builder.append(", propertyName=");
		builder.append(propertyName);
		builder.append("]");
		return builder.toString();
	}

}
