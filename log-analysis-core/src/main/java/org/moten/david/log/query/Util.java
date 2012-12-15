package org.moten.david.log.query;

public class Util {

	public static String toJson(Buckets buckets, Metric metric) {
		StringBuilder s = new StringBuilder();
		for (Bucket bucket : buckets.getBuckets()) {
			Double value = bucket.get(metric);
			if (value != null) {
				if (s.length() > 0)
					s.append(",");
				s.append('[');
				s.append(bucket.getStart());
				s.append(',');
				s.append(value);
				s.append(']');
			}
		}
		s.insert(0, "{ \"data\": [");
		s.append("] }");

		return s.toString();
	}
}
