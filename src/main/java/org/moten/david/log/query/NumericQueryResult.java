package org.moten.david.log.query;


public class NumericQueryResult {

	private final Iterable<Double> results;

	public NumericQueryResult(Iterable<Double> results) {
		this.results = results;
	}

	public Iterable<Double> getResults() {
		return results;
	}
}
