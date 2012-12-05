package org.moten.david.log;

public class Bucket {

	private long count = 0;
	private double total = 0;

	public void add(long timestamp, double value) {
		total += value;
		count += 1;
	}

	public Double mean() {
		if (count == 0)
			return null;
		else
			return total / count;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Bucket [count=");
		builder.append(count);
		builder.append(", total=");
		builder.append(total);
		builder.append("]");
		return builder.toString();
	}

}
