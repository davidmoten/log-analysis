package org.moten.david.log.query;

import com.google.common.base.Preconditions;

public class Bucket {

	private final double start;
	private final double width;
	private long count = 0;
	private double sum = 0;
	private double sumSquares = 0;
	private Double first;
	private Double last;
	private Double earliest;
	private Long earliestTimestamp;
	private Double latest;
	private Long latestTimestamp;
	private Double max;
	private Double min;

	public Bucket(double start, double width) {
		Preconditions.checkArgument(width > 0, "width must be > 0");
		this.start = start;
		this.width = width;
	}

	public void add(long timestamp, double value) {
		sum += value;
		sumSquares += value * value;
		count += 1;
		if (first == null)
			first = value;
		last = value;
		if (earliestTimestamp == null || timestamp < earliestTimestamp) {
			earliestTimestamp = timestamp;
			earliest = value;
		}
		if (latestTimestamp == null || timestamp > latestTimestamp) {
			latestTimestamp = timestamp;
			latest = value;
		}
		if (max == null || value > max) {
			max = value;
		}
		if (min == null || value < min) {
			min = value;
		}
	}

	public Double first() {
		return first;
	}

	public Double sumSquares() {
		return sumSquares;
	}

	public Double mean() {
		if (count == 0)
			return null;
		else
			return sum / count;
	}

	public Double standardDeviation() {
		if (count == 0)
			return null;
		else
			return Math.sqrt(sumSquares / count - mean() * mean());
	}

	public Double variance() {
		if (count == 0)
			return null;
		else
			return sumSquares / count - mean() * mean();
	}

	public Double last() {
		return last;
	}

	public Double earliest() {
		return earliest;
	}

	public Long earliestTimestamp() {
		return earliestTimestamp;
	}

	public Double latest() {
		return latest;
	}

	public Long latestTimestamp() {
		return latestTimestamp;
	}

	public Double max() {
		return max;
	}

	public Double min() {
		return min;
	}

	public Double sum() {
		return sum;
	}

	public Double get(Metric metric) {
		if (metric == Metric.EARLIEST)
			return earliest();
		else if (metric == Metric.FIRST)
			return first();
		else if (metric == Metric.LAST)
			return last();
		else if (metric == Metric.LATEST)
			return latest();
		else if (metric == Metric.MAX)
			return max();
		else if (metric == Metric.MEAN)
			return mean();
		else if (metric == Metric.MEDIAN)
			throw new RuntimeException("not implemented " + metric);
		else if (metric == Metric.MIN)
			return min();
		else if (metric == Metric.MODE)
			throw new RuntimeException("not implemented " + metric);
		else if (metric == Metric.STANDARD_DEVIATION)
			return standardDeviation();
		else if (metric == Metric.SUM)
			return sum();
		else if (metric == Metric.SUM_SQUARES)
			return sumSquares();
		else if (metric == Metric.VARIANCE)
			return variance();
		else if (metric == Metric.VARIANCE_POPULATION)
			throw new RuntimeException("not implemented " + metric);
		else
			throw new RuntimeException("not implemented " + metric);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Bucket [count=");
		builder.append(count);
		builder.append(", sum=");
		builder.append(sum());
		builder.append(", mean=");
		builder.append(mean());
		builder.append(", sd=");
		builder.append(standardDeviation());
		builder.append(", start=");
		builder.append(getStart());
		builder.append(", width=");
		builder.append(getWidth());

		builder.append("]");
		return builder.toString();
	}

	public double getStart() {
		return start;
	}

	public double getWidth() {
		return width;
	}

}