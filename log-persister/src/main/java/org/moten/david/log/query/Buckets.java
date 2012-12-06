package org.moten.david.log.query;

import java.util.List;


import com.google.common.collect.Lists;

public class Buckets {

	private final List<Bucket> buckets = Lists.newArrayList();
	private final Bucket allBuckets = new Bucket();
	private final BucketQuery query;

	public Buckets(BucketQuery query) {
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
