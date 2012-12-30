package org.moten.david.log.orientdb;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Parses and manipulates orientdb select statements.
 * 
 * @author dave
 * 
 */
public class SqlSelect {

	private final Map<String, String> clauses;

	// SELECT [<Projections>] FROM <Target> [LET <Assignment>*] [WHERE
	// <Condition>*] [GROUP BY <Field>] [ORDER BY <Fields>* [ASC|DESC]*] [SKIP
	// <SkipRecords>] [LIMIT <MaxRecords>]

	/**
	 * Constructor.
	 * 
	 * @param sql
	 */
	public SqlSelect(String sql) {
		sql = Pattern.compile("group by", Pattern.CASE_INSENSITIVE)
				.matcher(sql).replaceFirst("group_by");
		sql = Pattern.compile("order by", Pattern.CASE_INSENSITIVE)
				.matcher(sql).replaceFirst("order_by");
		Scanner scan = new Scanner(sql).useDelimiter(" |\\n|\\t");
		Set<String> keywords = Sets.newHashSet("SELECT", "FROM", "WHERE",
				"GROUP_BY", "ORDER_BY", "SKIP", "LIMIT", "LET");
		clauses = Maps.newHashMap();
		StringBuilder clause = new StringBuilder();

		String currentKeyword = null;
		while (scan.hasNext()) {
			String word = scan.next();
			boolean isKeyword = keywords.contains(word.toUpperCase());
			if (isKeyword) {
				if (clause.length() > 0)
					clauses.put(currentKeyword, clause.toString());
				clause = new StringBuilder();
				currentKeyword = word.toLowerCase();
			} else {
				if (clause.length() > 0)
					clause.append(' ');
				clause.append(word);
			}
		}
		if (clause.length() > 0)
			clauses.put(currentKeyword, clause.toString());
	}

	private SqlSelect(Map<String, String> clauses) {
		this.clauses = clauses;
	}

	/**
	 * Returns the select clause.
	 * 
	 * @return
	 */
	public String getSelect() {
		return clauses.get("select");
	}

	/**
	 * Returns the from clause.
	 * 
	 * @return
	 */
	public String getFrom() {
		return clauses.get("from");
	}

	/**
	 * Returns the where clause.
	 * 
	 * @return
	 */
	public String getWhere() {
		return clauses.get("where");
	}

	/**
	 * Returns the group by clause.
	 * 
	 * @return
	 */
	public String getGroupBy() {
		return clauses.get("group_by");
	}

	/**
	 * Returns the order by clause.
	 * 
	 * @return
	 */
	public String getOrderBy() {
		return clauses.get("order_by");
	}

	/**
	 * Returns the skip clause.
	 * 
	 * @return
	 */
	public String getSkip() {
		return clauses.get("skip");
	}

	/**
	 * Returns the limit clause.
	 * 
	 * @return
	 */
	public String getLimit() {
		return clauses.get("limit");
	}

	/**
	 * Returns a new instance of {@link SqlSelect} with a replaced where clause.
	 * 
	 * @param where
	 * @return
	 */
	public SqlSelect where(String where) {
		Map<String, String> m = Maps.newHashMap(clauses);
		m.put("where", where);
		return new SqlSelect(m);
	}

	/**
	 * Returns a new instance of {@link SqlSelect} with an additional where
	 * clause using boolean AND.
	 * 
	 * @param whereClause
	 * @return
	 */
	public SqlSelect and(String whereClause) {
		if (getWhere() == null)
			return where(whereClause);
		else {
			String newWhereClause = "(" + getWhere() + ") and (" + whereClause
					+ ")";
			return where(newWhereClause);
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("select ");
		s.append(clauses.get("select"));
		s.append(" from ");
		s.append(clauses.get("from"));
		appendClause(s, "let");
		appendClause(s, "where");
		appendClause(s, "group_by");
		appendClause(s, "order_by");
		appendClause(s, "skip");
		appendClause(s, "limit");
		return s.toString();

	}

	private void appendClause(StringBuilder s, String keyword) {
		if (clauses.get(keyword) != null) {
			s.append(" ");
			s.append(keyword.replaceAll("_", " "));
			s.append(" ");
			s.append(clauses.get(keyword));
		}
	}
}
