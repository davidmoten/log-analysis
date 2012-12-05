package org.moten.david.log;

public class Bucket {

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
		return latest();
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Bucket [count=");
		builder.append(count);
		builder.append(", total=");
		builder.append(sum);
		builder.append(", mean=");
		builder.append(mean());
		builder.append("]");
		return builder.toString();
	}

}
