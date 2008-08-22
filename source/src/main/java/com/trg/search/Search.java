package com.trg.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The base search DTO (data transfer object). A Search object is passed into a
 * DAO to define the parameters for a search. There are five types of parameters
 * that can be set.
 * <ul>
 * <li>Class - The Class of the object(s) to search for. This is required. It
 * can be specified in the constructor or using <code>setSearchClass()</code>.
 * <li>Filters - Any number of filters may be specified for the search. Filters
 * specify a property and a condition that it must match for the object to be
 * included in the result. Filters are "ANDed" together by default, but
 * disjunction (OR) can be used instead by setting
 * <code>setDisjunction(true)</code>. Filters are set using
 * <code>addFilter()</code>. See also the <code>Filter</code> class.
 * <li>Sorts - Any number of sorts may be specified. Each sort consists of a
 * property and a flag for ascending or descending. The first sort added is the
 * primary sort, the second, secondary and so on. Sorts are set using
 * <code>addSort()</code>. See also the <code>Sort</code> class.
 * <li>Paging - The maximum number of results may be specified with
 * <code>setMaxResults()</code>. (This can also be thought of as results per
 * page.) The first result can be specified using either
 * <code>setFirstResult()</code> or <code>setPage()</code>.
 * <li>Fetch -
 * <ul>
 * <li>When <code>fetchMode == FETCH_ENTITY</code>, this determines which
 * attached objects to pull along with the base search object. With hibernate
 * this eagerly loads the specified properties. Otherwise they might be loaded
 * lazily. This is useful for performance and results that will be disconnected
 * from hibernate and copied to a remote client. Fetches are set using
 * <code>addFetch()</code>.
 * <li>For other values of <code>fetchMode</code>, instead of returning the
 * actual objects, a collection of specified properties can be returned for each
 * row in the result set. These properties can be returned in maps, lists or
 * arrays. Fetches are specified using <code>addFetch()</code>. The fetch
 * mode must also be set using <code>setFetchMode()</code>
 * </ul>
 * </ul>
 * 
 * @see Filter
 * @see Fetch
 * 
 * @author dwolverton
 * 
 */
@SuppressWarnings("unchecked")
public class Search implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Value for fetch mode. This is the default value.
	 * <code>FETCH_ENTITY</code> returns the actual objects of the specified
	 * <code>searchClass</code>
	 * 
	 * @see #setFetchMode(int)
	 */
	public static final int FETCH_ENTITY = 0;

	/**
	 * Value for fetch mode. This is the default value. <code>FETCH_ARRAY</code>
	 * returns each object as an Object array (<code>Object[]</code>) with
	 * the entries corresponding to the fetches added to the search. Here's an
	 * example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setFetchMode(Search.FETCH_ARRAY);
	 * s.addFetch(&quot;firstName&quot;);
	 * s.addFetch(&quot;lastName&quot;);
	 * for (Object[] array : dao.search(s)) {
	 * 	System.out.println(array[0] + &quot; &quot; + array[1]);
	 * }
	 * </pre>
	 * 
	 * @see #setFetchMode(int)
	 */
	public static final int FETCH_ARRAY = 1;

	/**
	 * Value for fetch mode. This is the default value. <code>FETCH_LIST</code>
	 * returns each object as a list of Objects (<code>List&lt;Object&gt;</Code>).
	 * Here's an example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setFetchMode(Search.FETCH_LIST);
	 * s.addFetch(&quot;firstName&quot;);
	 * s.addFetch(&quot;lastName&quot;);
	 * for (List&lt;Object&gt; list : dao.search(s)) {
	 * 	System.out.println(list.get(0) + &quot; &quot; + list.get(1));
	 * }
	 * </pre>
	 * 
	 * @see #setFetchMode(int)
	 */
	public static final int FETCH_LIST = 2;

	/**
	 * Value for fetch mode. This is the default value. <code>FETCH_MAP</code>
	 * returns each object as a map with properties' names or aliases for keys
	 * to the corresponding values. Here's an example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setFetchMode(Search.FETCH_MAP;
	 * s.addFetch(&quot;firstName&quot;);
	 * s.addFetch(&quot;lastName&quot;, &quot;ln&quot;);
	 * for (Map&lt;String, Object&gt; map : dao.search(s)) {
	 * 	System.out.println(map.get(&quot;firstName&quot;) + &quot; &quot; + map.get(&quot;ln&quot;));
	 * }
	 * </pre>
	 * 
	 * @see #setFetchMode(int)
	 */
	public static final int FETCH_MAP = 3;

	/**
	 * Value for fetch mode. This is the default value.
	 * <code>FETCH_SINGLE</code> - Exactly one fetch property must be
	 * specified to use this fetch mode. The result list contains just the value
	 * of that property for each element. Here's an example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setFetchMode(Search.FETCH_SINGLE);
	 * s.addFetch(&quot;firstName&quot;);
	 * for (String name : dao.search(s)) {
	 * 	System.out.println(name);
	 * }
	 * </pre>
	 * 
	 * @see #setFetchMode(int)
	 */
	public static final int FETCH_SINGLE = 4;

	protected int firstResult = -1; // -1 stands for unspecified

	protected int maxResults = -1; // -1 stands for unspecified

	protected int page = -1; // -1 stands for unspecified

	protected Class searchClass;

	protected List<Filter> filters = new ArrayList<Filter>();

	protected boolean disjunction;

	protected List<Sort> sorts = new ArrayList<Sort>();

	protected List<Fetch> fetches = new ArrayList<Fetch>();

	protected int fetchMode = FETCH_ENTITY;

	public Search() {

	}

	public Search(Class searchClass) {
		this.searchClass = searchClass;
	}

	public void setSearchClass(Class searchClass) {
		this.searchClass = searchClass;
	}

	public Class getSearchClass() {
		return searchClass;
	}

	// Filters
	public void addFilter(Filter filter) {
		if (filter.property == null)
			return; // null properties do nothing, don't bother to add them.
		filters.add(filter);
	}

	/**
	 * Add a filter that uses the == operator.
	 */
	public void addFilterEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_EQUAL));
	}

	/**
	 * Add a filter that uses the >= operator.
	 */
	public void addFilterGreaterOrEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_GREATER_OR_EQUAL));
	}

	/**
	 * Add a filter that uses the > operator.
	 */
	public void addFilterGreaterThan(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_GREATER_THAN));
	}

	/**
	 * Add a filter that uses the IN operator.
	 */
	public void addFilterIn(String property, Collection<?> value) {
		addFilter(new Filter(property, value, Filter.OP_IN));
	}

	/**
	 * Add a filter that uses the IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public void addFilterIn(String property, Object... value) {
		addFilter(new Filter(property, value, Filter.OP_IN));
	}

	/**
	 * Add a filter that uses the NOT IN operator.
	 */
	public void addFilterNotIn(String property, Collection<?> value) {
		addFilter(new Filter(property, value, Filter.OP_NOT_IN));
	}

	/**
	 * Add a filter that uses the NOT IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public void addFilterNotIn(String property, Object... value) {
		addFilter(new Filter(property, value, Filter.OP_NOT_IN));
	}

	/**
	 * Add a filter that uses the <= operator.
	 */
	public void addFilterLessOrEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_LESS_OR_EQUAL));
	}

	/**
	 * Add a filter that uses the < operator.
	 */
	public void addFilterLessThan(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_LESS_THAN));
	}

	/**
	 * Add a filter that uses the LIKE operator.
	 */
	public void addFilterLike(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_LIKE));
	}

	/**
	 * Add a filter that uses the != operator.
	 */
	public void addFilterNotEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_NOT_EQUAL));
	}

	/**
	 * Add a filter that uses the AND operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of
	 * <code>Filter</code>s can be specified.
	 */
	public void addFilterAnd(Filter... filters) {
		addFilter(Filter.and(filters));
	}

	/**
	 * Add a filter that uses the OR operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of
	 * <code>Filter</code>s can be specified.
	 */
	public void addFilterOr(Filter... filters) {
		addFilter(Filter.or(filters));
	}

	/**
	 * Add a filter that uses the NOT operator.
	 */
	public void addFilterNot(Filter filter) {
		addFilter(Filter.not(filter));
	}

	public void removeFilter(Filter filter) {
		filters.remove(filter);
	}

	/**
	 * Remove all filters on the given property.
	 */
	public void removeFiltersOnProperty(String property) {
		if (property == null)
			return;
		Iterator<Filter> itr = filters.iterator();
		while (itr.hasNext()) {
			if (property.equals(itr.next().property))
				itr.remove();
		}
	}

	public void clearFilters() {
		filters.clear();
	}

	public Iterator<Filter> filterIterator() {
		return filters.iterator();
	}

	public boolean isDisjunction() {
		return disjunction;
	}

	/**
	 * Filters added to a search are "ANDed" together if this is false (default)
	 * and "ORed" if it is set to true.
	 */
	public void setDisjunction(boolean disjunction) {
		this.disjunction = disjunction;
	}

	// Sorts
	public void addSort(Sort sort) {
		if (sort.property == null)
			return; // null properties do nothing, don't bother to add them.
		sorts.add(sort);
	}

	/**
	 * Add ascending sort by property
	 */
	public void addSort(String property) {
		if (property == null)
			return; // null properties do nothing, don't bother to add them.
		sorts.add(new Sort(property));
	}

	/**
	 * Add sort by property. Ascending if <code>desc == false</code>,
	 * descending if <code>desc == true</code>.
	 */
	public void addSort(String property, boolean desc) {
		if (property == null)
			return; // null properties do nothing, don't bother to add them.
		sorts.add(new Sort(property, desc));
	}

	public void removeSort(Sort sort) {
		sorts.remove(sort);
	}

	public void removeSort(String property) {
		if (property == null)
			return;
		Iterator<Sort> itr = sorts.iterator();
		while (itr.hasNext()) {
			if (property.equals(itr.next().property))
				itr.remove();
		}
	}

	public void clearSorts() {
		sorts.clear();
	}

	public Iterator<Sort> sortIterator() {
		return sorts.iterator();
	}

	// Fetch
	public void addFetch(Fetch fetch) {
		fetches.add(fetch);
	}

	/**
	 * If this fetch is used with <code>fetchMode == FETCH_MAP</code>, the
	 * <code>property</code> will also be used as the key for this value in
	 * the map.
	 */
	public void addFetch(String property) {
		if (property == null || "".equals(property))
			return; // null properties do nothing, don't bother to add them.
		fetches.add(new Fetch(property));
	}

	/**
	 * If this fetch is used with <code>fetchMode == FETCH_MAP</code>, the
	 * <code>key</code> will be used as the key for this value in the map.
	 */
	public void addFetch(String property, String key) {
		if (property == null || "".equals(property) || key == null
				|| "".equals(key))
			return; // null properties do nothing, don't bother to add them.
		fetches.add(new Fetch(property, key));
	}

	/**
	 * If this fetch is used with <code>fetchMode == FETCH_MAP</code>, the
	 * <code>property</code> will also be used as the key for this value in
	 * the map.
	 */
	public void addFetch(String property, int operator) {
		if (property == null || "".equals(property))
			return; // null properties do nothing, don't bother to add them.
		fetches.add(new Fetch(property, operator));
	}
	
	/**
	 * If this fetch is used with <code>fetchMode == FETCH_MAP</code>, the
	 * <code>key</code> will be used as the key for this value in the map.
	 */
	public void addFetch(String property, int operator, String key) {
		if (property == null || "".equals(property) || key == null
				|| "".equals(key))
			return; // null properties do nothing, don't bother to add them.
		fetches.add(new Fetch(property, operator, key));
	}
	
	public void removeFetch(Fetch fetch) {
		fetches.remove(fetch);
	}

	public void removeFetch(String property) {
		Iterator<Fetch> itr = fetches.iterator();
		while (itr.hasNext()) {
			if (itr.next().property.equals(property))
				itr.remove();
		}
	}

	public void removeFetch(String property, String key) {
		Iterator<Fetch> itr = fetches.iterator();
		while (itr.hasNext()) {
			Fetch fetch = itr.next();
			if (fetch.property.equals(property) && fetch.key.equals(key))
				itr.remove();
		}
	}

	public void clearFetch() {
		fetches.clear();
	}

	public Iterator<Fetch> fetchIterator() {
		return fetches.iterator();
	}

	public int getFetchMode() {
		return fetchMode;
	}

	/**
	 * Select modes tell the search what form to use for the results. Options
	 * include <code>FETCH_ENTITY</code>, <code>FETCH_ARRAY</code>,
	 * <code>FETCH_LIST</code>, <code>FETCH_MAP</code> and
	 * <code>FETCH_SINGLE</code>.
	 * 
	 * @see #FETCH_ENTITY
	 * @see #FETCH_ARRAY
	 * @see #FETCH_LIST
	 * @see #FETCH_MAP
	 * @see #FETCH_SINGLE
	 */
	public void setFetchMode(int fetchMode) {
		if (fetchMode < 0 || fetchMode > 4)
			throw new IllegalArgumentException("Fetch Mode ( " + fetchMode
					+ " ) is not a valid option.");
		this.fetchMode = fetchMode;
	}

	public void clear() {
		clearFilters();
		clearSorts();
		clearFetch();
		clearPaging();
		fetchMode = FETCH_ENTITY;
		disjunction = false;
	}

	// Paging
	public int getFirstResult() {
		return firstResult;
	}

	/**
	 * Zero based index of first result record to return.
	 */
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getPage() {
		return page;
	}

	/**
	 * Zero based index of the page of records to return. The size of a page is
	 * determined by <code>maxResults</code>. If page is specified first
	 * result is ignored and the first result returned is calculated by
	 * <code>page * maxResults</code>.
	 */
	public void setPage(int page) {
		this.page = page;
	}

	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * The maximum number of records to return. Also used as page size when
	 * calculating the first record to return based on <code>page</code>.
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @return Zero based index of the first record to return. Calculation is
	 *         based on <code>page * maxResults</code> or
	 *         <code>firstResult</code> if page is not specified.
	 */
	public int calcFirstResult() {
		return (firstResult > 0) ? firstResult
				: (page > 0 && maxResults > 0) ? page * maxResults : 0;
	}

	public void clearPaging() {
		firstResult = -1;
		page = -1;
		maxResults = -1;
	}

	/**
	 * Create a copy of this search. All collections are copied into new
	 * collections, but them items in those collections are not duplicated; they
	 * still point to the same objects.
	 */
	public Search copy() {
		Search dest = new Search();
		dest.searchClass = searchClass;
		dest.disjunction = disjunction;
		dest.fetchMode = fetchMode;
		dest.firstResult = firstResult;
		dest.page = page;
		dest.maxResults = maxResults;
		for (Fetch fetch : fetches)
			dest.addFetch(fetch);
		for (Filter filter : filters)
			dest.addFilter(filter);
		for (Sort sort : sorts)
			dest.addSort(sort);

		return dest;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Search))
			return false;
		Search s = (Search) obj;

		if (searchClass == null ? s.searchClass != null : !searchClass
				.equals(s.searchClass))
			return false;
		if (disjunction != s.disjunction || fetchMode != s.fetchMode
				|| firstResult != s.firstResult || page != s.page
				|| maxResults != s.maxResults)
			return false;
		if (!filters.equals(s.filters) || !sorts.equals(s.sorts)
				|| !fetches.equals(s.fetches))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
