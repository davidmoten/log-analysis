package org.moten.david.log.query;

import java.util.List;

import com.google.common.collect.Lists;

public class Buckets {

	private final List<Bucket> buckets = Lists.newArrayList();
	private final Bucket allBucket;
	private final BucketQuery query;

	public Buckets(BucketQuery query) {
		this.query = query;
		for (int i = 0; i < query.getNumIntervals(); i++)
			buckets.add(new Bucket(query.getStartTime().getTime() + i
					* query.getIntervalSizeMs(), query.getIntervalSizeMs()));
		allBucket = new Bucket(query.getStartTime().getTime(),
				query.getIntervalSizeMs() * query.getNumIntervals());
	}

	public void add(long timestamp, double value) {
		int bucketIndex = (int) ((timestamp - query.getStartTime().getTime()) / query
				.getIntervalSizeMs());
		if (bucketIndex < buckets.size()) {
			buckets.get(bucketIndex).add(timestamp, value);
			allBucket.add(timestamp, value);
		}
	}

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public Bucket getBucketForAll() {
		return allBucket;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Buckets [buckets=");
		builder.append(buckets);
		builder.append(", allBucket=");
		builder.append(allBucket);
		builder.append(", query=");
		builder.append(query);
		builder.append("]");
		return builder.toString();
	}

}
