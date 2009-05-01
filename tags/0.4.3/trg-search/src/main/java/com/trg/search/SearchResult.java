package com.trg.search;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to return the results of <code>searchAndCount()</code>
 * operations. It has just two properties: the results and the search and the
 * total (unpaged) count of the search.
 * 
 * @author dwolverton
 */
public class SearchResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected List<T> result;
	protected int totalCount = -1;

	/**
	 * The results of the search.
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * The results of the search.
	 */
	public void setResult(List<T> results) {
		this.result = results;
	}

	/**
	 * The total number of results that would have been returned if no
	 * maxResults had been specified. (-1 means unspecified.)
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * The total number of results that would have been returned if no
	 * maxResults had been specified. (-1 means unspecified.)
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
