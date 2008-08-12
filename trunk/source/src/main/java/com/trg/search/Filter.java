package com.trg.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * A <code>Filter</code> is used by the <code>Search</code> class to specify
 * a restriction on what results should be returned in the search. For example,
 * if a filter <code>Filter.equal("name","Paul")</code> were added to the
 * search, only objects with the property "name" equal to the string "Paul"
 * would be returned.
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
	public String property;

	/**
	 * The value to compare the property with. Should be of a compatible type
	 * with the property. Note that <code>null</code> is also valid for
	 * "equal" and "not equal" comparisons. Examples:
	 * <code>"Fred", new Date(), 45</code>
	 */
	public Object value;

	/**
	 * The type of comparison to do between the property and the value. The
	 * options are limited to the integer constants on this class:
	 * <code>OP_EQAUL, OP_LESS_THAN, OP_GREATER_THAN, LESS_OR_EQUAL, OP_GREATER_OR_EQUAL, OP_IN, OP_LIKE, OP_NOT_EQUAL, OP_NOT_IN, OP_AND, OP_OR, OP_NOT</code>.
	 */
	public int operator;

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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Filter))
			return false;
		Filter f = (Filter) o;
		return (property == null ? f.property == null : property
				.equals(f.property))
				&& (value == null ? f.value == null : value.equals(f.value))
				&& (operator == f.operator);
	}

	public static final int OP_EQUAL = 0, OP_LESS_THAN = 1,
			OP_GREATER_THAN = 2, OP_LESS_OR_EQUAL = 3, OP_GREATER_OR_EQUAL = 4,
			OP_IN = 5, OP_LIKE = 6, OP_NOT_EQUAL = 7, OP_NOT_IN = 8;
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
	 * <p>This takes a variable number of parameters. Any number of
	 * values can be specified.
	 */
	public static Filter in(String property, Collection<?> value) {
		return new Filter(property, value, OP_IN);
	}

	/**
	 * Create a new Filter using the IN operator.
	 * 
	 * <p>This takes a variable number of parameters. Any number of
	 * values can be specified.
	 */
	public static Filter in(String property, Object... value) {
		return new Filter(property, value, OP_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 * 
	 * <p>This takes a variable number of parameters. Any number of
	 * values can be specified.
	 */
	public static Filter notIn(String property, Collection<?> value) {
		return new Filter(property, value, OP_NOT_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 * 
	 * <p>This takes a variable number of parameters. Any number of
	 * values can be specified.
	 */
	public static Filter notIn(String property, Object... value) {
		return new Filter(property, value, OP_NOT_IN);
	}

	/**
	 * Create a new Filter using the LIKE operator.
	 */
	public static Filter like(String property, Object value) {
		return new Filter(property, value, OP_LIKE);
	}

	/**
	 * Create a new Filter using the != operator.
	 */
	public static Filter notEqual(String property, Object value) {
		return new Filter(property, value, OP_NOT_EQUAL);
	}

	/**
	 * Create a new Filter using the AND operator.
	 * 
	 * <p>This takes a variable number of parameters. Any number of
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
	 * <p>This takes a variable number of parameters. Any number of
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
}
