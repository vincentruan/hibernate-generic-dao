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

	public Fetch() {
	}

	public Fetch(String property) {
		this.property = property;
	}

	public Fetch(String property, String key) {
		this.property = property;
		this.key = key;
	}
}
