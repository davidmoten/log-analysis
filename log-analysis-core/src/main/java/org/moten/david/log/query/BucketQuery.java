package org.moten.david.log.query;

import java.util.Date;
import java.util.logging.Logger;

public class BucketQuery {

	private static final Logger log = Logger.getLogger(BucketQuery.class
			.getName());

	private final Date startTime;
	private final double intervalSizeMs;
	private final long numIntervals;
	private final String sql;

	public BucketQuery(Date startTime, double intervalSizeMs,
			long numIntervals, String sql) {
		super();
		this.startTime = startTime;
		this.intervalSizeMs = intervalSizeMs;
		this.numIntervals = numIntervals;
		String timeClause = "logTimestamp between "
				+ startTime.getTime()
				+ " and "
				+ Math.ceil(startTime.getTime() + intervalSizeMs * numIntervals);
		Sql sq = new Sql(sql);
		this.sql = sq.and(timeClause).toString();
		log.info("sql = " + this.sql);
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
		builder.append(startTime.getTime());
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
