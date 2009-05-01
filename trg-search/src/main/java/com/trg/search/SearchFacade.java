package com.trg.search;

import java.util.List;

/**
 * SearchFacade provides a clean interface to the Search APIs.
 * 
 * @author dwolverton
 */
public interface SearchFacade {

	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>ISearch</code> object.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public List search(ISearch search);

	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>ISearch</code> object. Uses the specified searchClass, ignoring the
	 * searchClass specified on the search itself.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public List search(Class<?> searchClass, ISearch search);

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>ISearch</code> if there were no paging or maxResult limits.
	 * 
	 * @see ISearch
	 */
	public int count(ISearch search);

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>ISearch</code> if there were no paging or maxResult limits.
	 * Uses the specified searchClass, ignoring the searchClass specified on the
	 * search itself.
	 * 
	 * @see ISearch
	 */
	public int count(Class<?> searchClass, ISearch search);

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(ISearch search);

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>. Uses the specified searchClass, ignoring the
	 * searchClass specified on the search itself.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Class<?> searchClass, ISearch search);

	/**
	 * Search for a single result using the given parameters.
	 */
	public Object searchUnique(ISearch search);

	/**
	 * Search for a single result using the given parameters. Uses the specified
	 * searchClass, ignoring the searchClass specified on the search itself.
	 */
	public Object searchUnique(Class<?> searchClass, ISearch search);

	/**
	 * Generates a search filter from the given example using default options. 
	 */
	public Filter getFilterFromExample(Object example);

	/**
	 * Generates a search filter from the given example using the specified options. 
	 */
	public Filter getFilterFromExample(Object example, ExampleOptions options);

}