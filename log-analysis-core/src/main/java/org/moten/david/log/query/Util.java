package org.moten.david.log.query;

import com.google.common.base.CaseFormat;

public class Util {

	public static String toJson(Buckets buckets, Metric metric) {
		StringBuilder s = new StringBuilder();
		for (Bucket bucket : buckets.getBuckets()) {
			Double value = bucket.get(metric);
			if (value != null) {
				if (s.length() > 0)
					s.append(",");
				add(s, bucket, value);
			}
		}
		s.insert(0, "{ \"data\": [");
		s.append("]");
		StringBuilder s2 = new StringBuilder();
		Bucket b = buckets.getBucketForAll();
		for (Metric m : Metric.values()) {
			try {
				Double value = b.get(m);
				if (s2.length() > 0)
					s2.append(",\n\t");
				s2.append("\t\""
						+ CaseFormat.LOWER_UNDERSCORE.to(
								CaseFormat.LOWER_CAMEL, m.toString()) + "\": "
						+ value);
			} catch (RuntimeException e) {
				// ignore unimplemented metric
			}
		}
		s.append(",\n");
		s.append("\"stats\": {\n");
		s.append(s2);
		s.append("    }\n");
		s.append("}");
		return s.toString();
	}

	private static void add(StringBuilder s, Bucket bucket, Double value) {
		s.append('[');
		s.append(bucket.getStart());
		s.append(',');
		s.append(value);
		s.append(']');
	}
}
