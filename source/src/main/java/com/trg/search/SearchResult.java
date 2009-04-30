package com.trg.search;

import java.io.Serializable;
import java.util.List;

public class SearchResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The results of the search.
	 */
	protected List<T> result;
	/**
	 * The total number of results that would have been returned if no
	 * maxResults had been specified. (-1 means unspecified.)
	 */
	protected int totalCount = -1;

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> results) {
		this.result = results;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
