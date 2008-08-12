package com.trg.search;

import java.io.Serializable;
import java.util.List;

public class SearchResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The search used when generating this result.
	 */
	public Search search;
	/**
	 * The results of the search.
	 */
	public List<T> results;
	/**
	 * The total number of results that would have been returned if no
	 * maxResults had been specified. (-1 means unspecified.)
	 */
	public int totalLength = -1;
	/**
	 * The page used when generating this result. (-1 means unspecified.)
	 */
	public int page = -1;
	/**
	 * The first result used when generating this result. (-1 means unspecified.)
	 */
	public int firstResult = -1;
	/**
	 * The maximum number of results used when generating this result. (-1 means unspecified.)
	 */
	public int maxResults = -1;
}
