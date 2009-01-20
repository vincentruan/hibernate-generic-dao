package com.trg.dao.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * A <code>Filter</code> is used by the <code>Search</code> class to specify a
 * restriction on what results should be returned in the search. For example, if
 * a filter <code>Filter.equal("name","Paul")</code> were added to the search,
 * only objects with the property "name" equal to the string "Paul" would be
 * returned.
 * <p>
 * Nested properties can also be specified, for example
 * <code>Filter.greaterThan("employee.age",65)</code>.
 * 
 * @author dwolverton
 */
public class Filter implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of the property to filter on. It may be nested. Examples:
	 * <code>"name", "dateOfBirth", "employee.age", "employee.spouse.job.title"</code>
	 */
	protected String property;

	/**
	 * The value to compare the property with. Should be of a compatible type
	 * with the property. Note that <code>null</code> is also valid for "equal"
	 * and "not equal" comparisons. Examples:
	 * <code>"Fred", new Date(), 45</code>
	 */
	protected Object value;

	/**
	 * The type of comparison to do between the property and the value. The
	 * options are limited to the integer constants on this class:
	 * 
	 * 
	 * <code>OP_EQAUL, OP_LESS_THAN, OP_GREATER_THAN, LESS_OR_EQUAL, OP_GREATER_OR_EQUAL, OP_IN, OP_LIKE, OP_ILIKE, OP_NOT_EQUAL, OP_NOT_IN, OP_AND, OP_OR, OP_NOT</code>
	 * .
	 */
	protected int operator;

	public Filter() {

	}

	public Filter(String property, Object value, int operator) {
		this.property = property;
		this.value = value;
		this.operator = operator;
	}

	public Filter(String property, Object value) {
		this.property = property;
		this.value = value;
		this.operator = OP_EQUAL;
	}

	public static final int OP_EQUAL = 0, OP_NOT_EQUAL = 1, OP_LESS_THAN = 2, OP_GREATER_THAN = 3, OP_LESS_OR_EQUAL = 4,
			OP_GREATER_OR_EQUAL = 5, OP_IN = 6, OP_NOT_IN = 7, OP_LIKE = 8, OP_ILIKE = 9, OP_NULL = 10, OP_NOT_NULL = 11;
	public static final int OP_AND = 100, OP_OR = 101, OP_NOT = 102;

	/**
	 * Create a new Filter using the == operator.
	 */
	public static Filter equal(String property, Object value) {
		return new Filter(property, value, OP_EQUAL);
	}

	/**
	 * Create a new Filter using the < operator.
	 */
	public static Filter lessThan(String property, Object value) {
		return new Filter(property, value, OP_LESS_THAN);
	}

	/**
	 * Create a new Filter using the > operator.
	 */
	public static Filter greaterThan(String property, Object value) {
		return new Filter(property, value, OP_GREATER_THAN);
	}

	/**
	 * Create a new Filter using the <= operator.
	 */
	public static Filter lessOrEqual(String property, Object value) {
		return new Filter(property, value, OP_LESS_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the >= operator.
	 */
	public static Filter greaterOrEqual(String property, Object value) {
		return new Filter(property, value, OP_GREATER_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter in(String property, Collection<?> value) {
		return new Filter(property, value, OP_IN);
	}

	/**
	 * Create a new Filter using the IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter in(String property, Object... value) {
		return new Filter(property, value, OP_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter notIn(String property, Collection<?> value) {
		return new Filter(property, value, OP_NOT_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public static Filter notIn(String property, Object... value) {
		return new Filter(property, value, OP_NOT_IN);
	}

	/**
	 * Create a new Filter using the LIKE operator.
	 */
	public static Filter like(String property, String value) {
		return new Filter(property, value, OP_LIKE);
	}

	/**
	 * Create a new Filter using the ILIKE operator.
	 */
	public static Filter ilike(String property, String value) {
		return new Filter(property, value, OP_ILIKE);
	}

	/**
	 * Create a new Filter using the != operator.
	 */
	public static Filter notEqual(String property, Object value) {
		return new Filter(property, value, OP_NOT_EQUAL);
	}
	
	/**
	 * Create a new Filter using the IS NULL operator.
	 */
	public static Filter isNull(String property) {
		return new Filter(property, true, OP_NULL);
	}
	
	/**
	 * Create a new Filter using the IS NOT NULL operator.
	 */
	public static Filter isNotNull(String property) {
		return new Filter(property, true, OP_NOT_NULL);
	}

	/**
	 * Create a new Filter using the AND operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of
	 * <code>Filter</code>s can be specified.
	 */
	public static Filter and(Filter... filters) {
		Filter filter = new Filter("AND", null, OP_AND);
		for (Filter f : filters) {
			filter.add(f);
		}
		return filter;
	}

	/**
	 * Create a new Filter using the OR operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of
	 * <code>Filter</code>s can be specified.
	 */
	public static Filter or(Filter... filters) {
		Filter filter = and(filters);
		filter.property = "OR";
		filter.operator = OP_OR;
		return filter;
	}

	/**
	 * Create a new Filter using the NOT operator.
	 */
	public static Filter not(Filter filter) {
		return new Filter("NOT", filter, OP_NOT);
	}

	/**
	 * Used with OP_OR and OP_AND filters. These filters take a collection of
	 * filters as their value. This method adds a filter to that list.
	 */
	@SuppressWarnings("unchecked")
	public void add(Filter filter) {
		if (value == null || !(value instanceof List)) {
			value = new ArrayList();
		}
		((List) value).add(filter);
	}

	/**
	 * Used with OP_OR and OP_AND filters. These filters take a collection of
	 * filters as their value. This method removes a filter from that list.
	 */
	@SuppressWarnings("unchecked")
	public void remove(Filter filter) {
		if (value == null || !(value instanceof List)) {
			return;
		}
		((List) value).remove(filter);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + operator;
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Filter other = (Filter) obj;
		if (operator != other.operator)
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		switch (operator) {
		case Filter.OP_IN:
			return "`" + property + "` in (" + valueString(value) + ")";
		case Filter.OP_NOT_IN:
			return "`" + property + "` not in (" + valueString(value) + ")";
		case Filter.OP_EQUAL:
			return "`" + property + "` = " + valueString(value);
		case Filter.OP_NOT_EQUAL:
			return "`" + property + "` != " + valueString(value);
		case Filter.OP_GREATER_THAN:
			return "`" + property + "` > " + valueString(value);
		case Filter.OP_LESS_THAN:
			return "`" + property + "` < " + valueString(value);
		case Filter.OP_GREATER_OR_EQUAL:
			return "`" + property + "` >= " + valueString(value);
		case Filter.OP_LESS_OR_EQUAL:
			return "`" + property + "` <= " + valueString(value);
		case Filter.OP_LIKE:
			return "`" + property + "` LIKE " + valueString(value);
		case Filter.OP_ILIKE:
			return "`" + property + "` ILIKE " + valueString(value);
		case Filter.OP_AND:
		case Filter.OP_OR:
			if (!(value instanceof List)) {
				return (operator == Filter.OP_AND ? "AND: " : "OR: ") + "**INVALID VALUE - NOT A LIST: (" + value
						+ ") **";
			}

			String op = operator == Filter.OP_AND ? " and " : " or ";

			StringBuilder sb = new StringBuilder("(");
			boolean first = true;
			for (Object o : ((List) value)) {
				if (first) {
					first = false;
				} else {
					sb.append(op);
				}
				if (o instanceof Filter) {
					sb.append(o.toString());
				} else {
					sb.append("**INVALID VALUE - NOT A FILTER: (" + o + ") **");
				}
			}
			if (first)
				return (operator == Filter.OP_AND ? "AND: " : "OR: ") + "**EMPTY LIST**";

			sb.append(")");
			return sb.toString();
		case Filter.OP_NOT:
			if (!(value instanceof Filter)) {
				return "NOT: **INVALID VALUE - NOT A FILTER: (" + value + ") **";
			}

			return "not " + value.toString();
		default:
			return "**INVALID OPERATOR: (" + operator + ") - VALUE: " + valueString(value) + " **";
		}
	}
	
	private String valueString(Object val) {
		if (val == null) {
			return "null";
		} else if (val instanceof String) {
			return "\"" + val + "\"";
		} else if (val instanceof Collection) {
			StringBuilder sb = new StringBuilder();
			sb.append(val.getClass().getSimpleName());
			sb.append(" {");
			boolean first = true;
			for (Object o : (Collection<?>) val) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(valueString(o));
			}
			sb.append("}");
			return sb.toString();
		} else if (val instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			sb.append(val.getClass().getComponentType().getSimpleName());
			sb.append("[] {");
			boolean first = true;
			for (Object o : (Object[]) val) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(valueString(o));
			}
			sb.append("}");
			return sb.toString();
		} else {
			return val.toString();
		}
	}
}
