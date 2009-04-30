package com.trg.search;

import java.util.ArrayList;
import java.util.Collection;

public class ExampleOptions {
	public final static int EXACT = 0;
	public final static int START = 1;
	public final static int END = 2;
	public final static int ANYWHERE = 3;
	
	private Collection<String> excludeProps;
	private int likeMode = EXACT;
	private boolean excludeNulls = true;
	private boolean excludeZeros = false;
	private boolean ignoreCase = false;
	
	/**
	 * Add a property to the excludeProps collection
	 */
	public ExampleOptions excludeProp(String property) {
		if (excludeProps == null)
			excludeProps = new ArrayList<String>();
		excludeProps.add(property);
		return this;
	}

	public Collection<String> getExcludeProps() {
		return excludeProps;
	}

	public ExampleOptions setExcludeProps(Collection<String> excludeProps) {
		this.excludeProps = excludeProps;
		return this;
	}

	public boolean isExcludeNulls() {
		return excludeNulls;
	}

	public ExampleOptions setExcludeNulls(boolean excludeNulls) {
		this.excludeNulls = excludeNulls;
		return this;
	}

	public boolean isExcludeZeros() {
		return excludeZeros;
	}

	public ExampleOptions setExcludeZeros(boolean excludeZeros) {
		this.excludeZeros = excludeZeros;
		return this;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public ExampleOptions setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}

	public int getLikeMode() {
		return likeMode;
	}

	public ExampleOptions setLikeMode(int likeMode) {
		this.likeMode = likeMode;
		return this;
	}
}
