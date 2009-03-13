package com.trg.dao.search;

import java.io.Serializable;
import java.util.List;

public class SearchResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The search used when generating this result.
	 */
	protected ISearch search;
	/**
	 * The results of the search.
	 */
	protected List<T> results;
	/**
	 * The total number of results that would have been returned if no
	 * maxResults had been specified. (-1 means unspecified.)
	 */
	protected int totalCount = -1;
	/**
	 * The page used when generating this result. (-1 means unspecified.)
	 */
	protected int page = -1;
	/**
	 * The first result used when generating this result. (-1 means
	 * unspecified.)
	 */
	protected int firstResult = -1;
	/**
	 * The maximum number of results used when generating this result. (-1 means
	 * unspecified.)
	 */
	protected int maxResults = -1;

	public ISearch getSearch() {
		return search;
	}

	public void setSearch(ISearch search) {
		this.search = search;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
}
