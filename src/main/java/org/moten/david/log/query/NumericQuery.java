package org.moten.david.log.query;

import java.util.Date;

public class NumericQuery {

	private final Date startTime;
	private final double intervalSizeMs;
	private final long numIntervals;
	private final String sql;

	public NumericQuery(Date startTime, double intervalSizeMs,
			long numIntervals, String sql) {
		super();
		this.startTime = startTime;
		this.intervalSizeMs = intervalSizeMs;
		this.numIntervals = numIntervals;
		this.sql = sql;
		if (true) {
			sql += " WHERE";
		} else
			// TODO put brackets around existing condition
			sql += " AND";

		sql += " logTimestamp between "
				+ startTime.getTime()
				+ " and "
				+ Math.ceil(startTime.getTime() + intervalSizeMs * numIntervals);
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

	public String getSql() {
		return sql;
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
		builder.append(", sql=");
		builder.append(sql);

		builder.append("]");
		return builder.toString();
	}

}
