package org.moten.david.log.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sql {
	// SELECT [<Projections>] FROM <Target> [LET <Assignment>*] [WHERE
	// <Condition>*] [GROUP BY <Field>] [ORDER BY <Fields>* [ASC|DESC]*] [SKIP
	// <SkipRecords>] [LIMIT <MaxRecords>]
	private static final Pattern pattern = Pattern
			.compile(
					"^.*(\\bSELECT\\b.*)(\\bFROM\\b.*)(\\bWHERE\\b.*)?(\\bGROUP BY\\b.*)?(\\bORDER BY\\b.*)?(\\bSKIP\\b.*)?(\\bLIMIT\\b.*)?$",
					Pattern.CASE_INSENSITIVE);
	private final String select;
	private final String from;
	private final String where;
	private final String groupBy;
	private final String orderBy;
	private final String skip;
	private final String limit;

	public Sql(String sql) {
		Matcher matcher = pattern.matcher(sql);
		if (!matcher.find())
			throw new RuntimeException("parse error, pattern not matched: "
					+ pattern.pattern());
		select = matcher.group(1);
		from = matcher.group(2);
		where = matcher.group(3);
		groupBy = matcher.group(4);
		orderBy = matcher.group(5);
		skip = matcher.group(6);
		limit = matcher.group(7);
	}

	public static Pattern getPattern() {
		return pattern;
	}

	public String getSelect() {
		return select;
	}

	public String getFrom() {
		return from;
	}

	public String getWhere() {
		return where;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public String getSkip() {
		return skip;
	}

	public String getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sql [select=");
		builder.append(select);
		builder.append(", from=");
		builder.append(from);
		builder.append(", where=");
		builder.append(where);
		builder.append(", groupBy=");
		builder.append(groupBy);
		builder.append(", orderBy=");
		builder.append(orderBy);
		builder.append(", skip=");
		builder.append(skip);
		builder.append(", limit=");
		builder.append(limit);
		builder.append("]");
		return builder.toString();
	}

}
