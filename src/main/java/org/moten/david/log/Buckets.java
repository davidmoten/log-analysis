package org.moten.david.log;

import java.util.List;

import org.moten.david.log.query.NumericQuery;

import com.google.common.collect.Lists;

public class Buckets {

	private final List<Bucket> buckets = Lists.newArrayList();
	private final Bucket allBuckets = new Bucket();
	private final NumericQuery query;

	public Buckets(NumericQuery query) {
		this.query = query;
		for (int i = 0; i < query.getNumIntervals(); i++)
			buckets.add(new Bucket());
	}

	public void add(long timestamp, double value) {
		int bucketIndex = (int) ((timestamp - query.getStartTime().getTime()) / query
				.getIntervalSizeMs());
		buckets.get(bucketIndex).add(timestamp, value);
		allBuckets.add(timestamp, value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Buckets [buckets=");
		builder.append(buckets);
		builder.append(", allBuckets=");
		builder.append(allBuckets);
		builder.append(", query=");
		builder.append(query);
		builder.append("]");
		return builder.toString();
	}

}
