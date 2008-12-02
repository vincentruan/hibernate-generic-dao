package com.trg.search;

import java.io.Serializable;

/**
 * Used to specify selection in <code>Search</code>.
 * 
 * @see Search
 */
public class Fetch implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The entity to fetch eagerly or the property to include in the result.
	 */
	public String property;

	/**
	 * The key to use for the property when using fetch mode
	 * <code>FETCH_MAP</code>.
	 */
	public String key;

	/**
	 * The operator to apply to the column: for example
	 * <code>OP_COUNT, OP_SUM, OP_MAX</code>. The default is
	 * <code>OP_PROPERTY</code>.
	 */
	public int operator = 0;

	/**
	 * Possible value for <code>operator</code>. This is the default value
	 * and does not apply any operator to the column. All the rows in the result
	 * set are returned.
	 */
	public static final int OP_PROPERTY = 0;
	
	/**
	 * Possible value for <code>operator</code>. This returns the number of
	 * rows in the result set where the given property is non-null.
	 */
	public static final int OP_COUNT = 1;
	
	/**
	 * Possible value for <code>operator</code>. This returns the number of
	 * distinct values of the given property in the result set.
	 */
	public static final int OP_COUNT_DISTINCT = 2;
	
	/**
	 * Possible value for <code>operator</code>. This returns the maximum
	 * value of the given property in the result set.
	 */
	public static final int OP_MAX = 3;
	
	/**
	 * Possible value for <code>operator</code>. This returns the minimum
	 * value of the given property in the result set.
	 */
	public static final int OP_MIN = 4;
	
	/**
	 * Possible value for <code>operator</code>. This returns the sum of the
	 * given property in all rows of the result set.
	 */
	public static final int OP_SUM = 5;
	
	/**
	 * Possible value for <code>operator</code>. This returns the average
	 * value of the given property in the result set.
	 */
	public static final int OP_AVG = 6;

	public Fetch() {
	}

	public Fetch(String property) {
		this.property = property;
	}

	public Fetch(String property, String key) {
		this.property = property;
		this.key = key;
	}

	public Fetch(String property, int operator) {
		this.property = property;
		this.operator = operator;
	}

	public Fetch(String property, int operator, String key) {
		this.property = property;
		this.operator = operator;
		this.key = key;
	}
}
