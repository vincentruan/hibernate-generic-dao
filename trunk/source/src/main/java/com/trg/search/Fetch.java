package com.trg.search;

import java.io.Serializable;

/**
 * Used to specify selection in <code>Search</code>.
 * @see Search
 */
public class Fetch implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String property;
	public String key;
	public int operator = 0;
	
	public static final int OP_PROPERTY = 0;
	public static final int OP_COUNT = 1;
	public static final int OP_COUNT_DISTINCT = 2;
	public static final int OP_MAX = 3;
	public static final int OP_MIN = 4;
	public static final int OP_SUM = 5;
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
