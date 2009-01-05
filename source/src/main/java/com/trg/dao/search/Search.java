package com.trg.dao.search;

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
 * <code>addFilter()</code> . See also the <code>Filter</code> class.
 * <li>Sorts - Any number of sorts may be specified. Each sort consists of a
 * property and a flag for ascending or descending. The first sort added is the
 * primary sort, the second, secondary and so on. Sorts are set using
 * <code>addSort()</code>. See also the <code>Sort</code> class.
 * <li>Paging - The maximum number of results may be specified with
 * <code>setMaxResults()</code>. (This can also be thought of as results per
 * page.) The first result can be specified using either
 * <code>setFirstResult()</code> or <code>setPage()</code>.
 * <li>Fields - By default the entity specified in search class is returned as
 * the result for each row. However, by specifying fields, any combination of
 * individual properties can be returned for each row in the result set. These
 * properties can be returned as maps, lists or arrays depending on
 * <code>resultMode</code>. Fields are specified using <code>addField()</code>.
 * The result mode may also be set using <code>setResultMode()</code>.<br/><br/>
 * 
 * Additionally, fields can be specified using column operators: COUNT, COUNT
 * DISTINCT, SUM, AVG, MAX, MIN. Note that fields with column operators can not
 * be mixed with fields that do not use column operators.
 * </ul>
 * <li>Fetch - This determines which attached objects to pull along with the
 * base search object. With hibernate this eagerly loads the specified
 * properties. Otherwise they might be loaded lazily. This is useful for
 * performance and results that will be disconnected from hibernate and copied
 * to a remote client. Fetches are set using <code>addFetch()</code>. </ul>
 * 
 * @see Filter
 * @see Field
 * @see Sort
 * 
 * @author dwolverton
 * 
 */
@SuppressWarnings("unchecked")
public class Search implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Value for result mode. This is the default value. With
	 * <code>RESULT_AUTO</code> the result mode is automatically determined
	 * according to the following rules:
	 * <ul>
	 * <li>If any field is specified with a key, use <code>RESULT_MAP</code>.
	 * <li>Otherwise, if zero or one fields are specified, use <code>
	 * RESULT_SINGLE</code>. <li>Otherwise, use <code>RESULT_ARRAY</code>.
	 * </ul>
	 * 
	 * @see #setResultMode(int)
	 */
	public static final int RESULT_AUTO = 0;

	/**
	 * Value for result mode. <code>RESULT_ARRAY</code> returns each result as
	 * an Object array (<code>Object[]</code>) with the entries corresponding to
	 * the fields added to the search. Here's an example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setResultMode(Search.RESULT_ARRAY);
	 * s.addField(&quot;firstName&quot;);
	 * s.addField(&quot;lastName&quot;);
	 * for (Object[] array : dao.search(s)) {
	 * 	System.out.println(array[0] + &quot; &quot; + array[1]);
	 * }
	 * </pre>
	 * 
	 * @see #setResultMode(int)
	 */
	public static final int RESULT_ARRAY = 1;

	/**
	 * Value for result mode. <code>RESULT_LIST</code> returns each result as a
	 * list of Objects (<code>List&lt;Object&gt;</Code> ). Here's an example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setResultMode(Search.RESULT_LIST);
	 * s.addField(&quot;firstName&quot;);
	 * s.addField(&quot;lastName&quot;);
	 * for (List&lt;Object&gt; list : dao.search(s)) {
	 * 	System.out.println(list.get(0) + &quot; &quot; + list.get(1));
	 * }
	 * </pre>
	 * 
	 * @see #setResultMode(int)
	 */
	public static final int RESULT_LIST = 2;

	/**
	 * Value for result mode. <code>RESULT_MAP</code> returns each row as a
	 * map with properties' names or keys for keys to the corresponding
	 * values. Here's an example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setResultMode(Search.RESULT_MAP;
	 * s.addField(&quot;firstName&quot;);
	 * s.addField(&quot;lastName&quot;, &quot;ln&quot;);
	 * for (Map&lt;String, Object&gt; map : dao.search(s)) {
	 * 	System.out.println(map.get(&quot;firstName&quot;) + &quot; &quot; + map.get(&quot;ln&quot;));
	 * }
	 * </pre>
	 * 
	 * @see #setResultMode(int)
	 */
	public static final int RESULT_MAP = 3;

	/**
	 * Value for result mode.
	 * <code>RESULT_SINGLE</code> - Exactly one field or no fields must be specified
	 * to use this result mode. The result list contains just the value of that
	 * property for each element. Here's an example:
	 * 
	 * <pre>
	 * Search s = new Search(Person.class);
	 * s.setResultMode(Search.RESULT_SINGLE);
	 * s.addField(&quot;firstName&quot;);
	 * for (Object name : dao.search(s)) {
	 * 	System.out.println(name);
	 * }
	 * </pre>
	 * 
	 * @see #setResultMode(int)
	 */
	public static final int RESULT_SINGLE = 4;

	protected int firstResult = -1; // -1 stands for unspecified

	protected int maxResults = -1; // -1 stands for unspecified

	protected int page = -1; // -1 stands for unspecified

	protected Class searchClass;

	protected List<Filter> filters = new ArrayList<Filter>();

	protected boolean disjunction;

	protected List<Sort> sorts = new ArrayList<Sort>();

	protected List<Field> fields = new ArrayList<Field>();
	
	protected List<String> fetches = new ArrayList<String>();

	protected int resultMode = RESULT_AUTO;

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
	public Search addFilter(Filter filter) {
		filters.add(filter);
		return this;
	}

	/**
	 * Add a filter that uses the == operator.
	 */
	public Search addFilterEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_EQUAL));
		return this;
	}

	/**
	 * Add a filter that uses the >= operator.
	 */
	public Search addFilterGreaterOrEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_GREATER_OR_EQUAL));
		return this;
	}

	/**
	 * Add a filter that uses the > operator.
	 */
	public Search addFilterGreaterThan(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_GREATER_THAN));
		return this;
	}

	/**
	 * Add a filter that uses the IN operator.
	 */
	public Search addFilterIn(String property, Collection<?> value) {
		addFilter(new Filter(property, value, Filter.OP_IN));
		return this;
	}

	/**
	 * Add a filter that uses the IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public Search addFilterIn(String property, Object... value) {
		addFilter(new Filter(property, value, Filter.OP_IN));
		return this;
	}

	/**
	 * Add a filter that uses the NOT IN operator.
	 */
	public Search addFilterNotIn(String property, Collection<?> value) {
		addFilter(new Filter(property, value, Filter.OP_NOT_IN));
		return this;
	}

	/**
	 * Add a filter that uses the NOT IN operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of values can be
	 * specified.
	 */
	public Search addFilterNotIn(String property, Object... value) {
		addFilter(new Filter(property, value, Filter.OP_NOT_IN));
		return this;
	}

	/**
	 * Add a filter that uses the <= operator.
	 */
	public Search addFilterLessOrEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_LESS_OR_EQUAL));
		return this;
	}

	/**
	 * Add a filter that uses the < operator.
	 */
	public Search addFilterLessThan(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_LESS_THAN));
		return this;
	}

	/**
	 * Add a filter that uses the LIKE operator.
	 */
	public Search addFilterLike(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_LIKE));
		return this;
	}

	/**
	 * Add a filter that uses the ILIKE operator.
	 */
	public Search addFilterILike(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_ILIKE));
		return this;
	}

	/**
	 * Add a filter that uses the != operator.
	 */
	public Search addFilterNotEqual(String property, Object value) {
		addFilter(new Filter(property, value, Filter.OP_NOT_EQUAL));
		return this;
	}

	/**
	 * Add a filter that uses the IS NULL operator.
	 */
	public Search addFilterNull(String property) {
		addFilter(new Filter(property, true, Filter.OP_NULL));
		return this;
	}

	/**
	 * Add a filter that uses the IS NULL operator.
	 */
	public Search addFilterNotNull(String property) {
		addFilter(new Filter(property, true, Filter.OP_NOT_NULL));
		return this;
	}

	/**
	 * Add a filter that uses the AND operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of <code>Filter
	 * </code>s can be specified.
	 */
	public Search addFilterAnd(Filter... filters) {
		addFilter(Filter.and(filters));
		return this;
	}

	/**
	 * Add a filter that uses the OR operator.
	 * 
	 * <p>
	 * This takes a variable number of parameters. Any number of <code>Filter
	 * </code>s can be specified.
	 */
	public Search addFilterOr(Filter... filters) {
		addFilter(Filter.or(filters));
		return this;
	}

	/**
	 * Add a filter that uses the NOT operator.
	 */
	public Search addFilterNot(Filter filter) {
		addFilter(Filter.not(filter));
		return this;
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
			if (property.equals(itr.next().getProperty()))
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
	public Search addSort(Sort sort) {
		sorts.add(sort);
		return this;
	}

	/**
	 * Add ascending sort by property
	 */
	public Search addSortAsc(String property) {
		return addSort(property, false, false);
	}

	/**
	 * Add ascending sort by property
	 */
	public Search addSortAsc(String property, boolean ignoreCase) {
		return addSort(property, false, ignoreCase);
	}

	/**
	 * Add descending sort by property
	 */
	public Search addSortDesc(String property) {
		return addSort(property, true, false);
	}

	/**
	 * Add descending sort by property
	 */
	public Search addSortDesc(String property, boolean ignoreCase) {
		return addSort(property, true, ignoreCase);
	}

	/**
	 * Add sort by property. Ascending if <code>desc == false</code>, descending
	 * if <code>desc == true</code>.
	 */
	public Search addSort(String property, boolean desc) {
		return addSort(property, desc, false);
	}

	/**
	 * Add sort by property. Ascending if <code>desc == false</code>, descending
	 * if <code>desc == true</code>.
	 */
	public Search addSort(String property, boolean desc, boolean ignoreCase) {
		if (property == null)
			return this; // null properties do nothing, don't bother to add
		// them.
		sorts.add(new Sort(property, desc, ignoreCase));
		return this;
	}

	public void removeSort(Sort sort) {
		sorts.remove(sort);
	}

	public void removeSort(String property) {
		if (property == null)
			return;
		Iterator<Sort> itr = sorts.iterator();
		while (itr.hasNext()) {
			if (property.equals(itr.next().getProperty()))
				itr.remove();
		}
	}

	public void clearSorts() {
		sorts.clear();
	}

	public Iterator<Sort> sortIterator() {
		return sorts.iterator();
	}

	// Fields
	public Search addField(Field field) {
		fields.add(field);
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>property</code> will also be used as the key for this value in the
	 * map.
	 */
	public Search addField(String property) {
		if (property == null || "".equals(property))
			return this; // null properties do nothing, don't bother to add
		// them.
		fields.add(new Field(property));
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>key</code> will be used as the key for this value in the map.
	 */
	public Search addField(String property, String key) {
		if (property == null || "".equals(property) || key == null || "".equals(key))
			return this; // null properties do nothing, don't bother to add
		// them.
		fields.add(new Field(property, key));
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>property</code> will also be used as the key for this value in the
	 * map.
	 */
	public Search addField(String property, int operator) {
		if (property == null || "".equals(property))
			return this; // null properties do nothing, don't bother to add
		// them.
		fields.add(new Field(property, operator));
		return this;
	}

	/**
	 * If this field is used with <code>resultMode == RESULT_MAP</code>, the
	 * <code>key</code> will be used as the key for this value in the map.
	 */
	public Search addField(String property, int operator, String key) {
		if (property == null || "".equals(property) || key == null || "".equals(key))
			return this; // null properties do nothing, don't bother to add
		// them.
		fields.add(new Field(property, operator, key));
		return this;
	}

	public void removeField(Field field) {
		fields.remove(field);
	}

	public void removeField(String property) {
		Iterator<Field> itr = fields.iterator();
		while (itr.hasNext()) {
			if (itr.next().getProperty().equals(property))
				itr.remove();
		}
	}

	public void removeField(String property, String key) {
		Iterator<Field> itr = fields.iterator();
		while (itr.hasNext()) {
			Field field = itr.next();
			if (field.getProperty().equals(property) && field.getKey().equals(key))
				itr.remove();
		}
	}

	public void clearFields() {
		fields.clear();
	}

	public Iterator<Field> fieldIterator() {
		return fields.iterator();
	}

	public int getResultMode() {
		return resultMode;
	}

	/**
	 * Result modes tell the search what form to use for the results. Options
	 * include <code>RESULT_AUTO</code>, <code>RESULT_ARRAY</code>, <code>
	 * RESULT_LIST</code>, <code>RESULT_MAP</code> and <code>RESULT_SINGLE
	 * </code>.
	 * 
	 * @see #RESULT_AUTO
	 * @see #RESULT_ARRAY
	 * @see #RESULT_LIST
	 * @see #RESULT_MAP
	 * @see #RESULT_SINGLE
	 */
	public Search setResultMode(int resultMode) {
		if (resultMode < 0 || resultMode > 4)
			throw new IllegalArgumentException("Result Mode ( " + resultMode + " ) is not a valid option.");
		this.resultMode = resultMode;
		return this;
	}
	
	//Fetches
	public Search addFetch(String property) {
		fetches.add(property);
		return this;
	}
	
	public void removeFetch(String property) {
		fetches.remove(property);
	}
	
	public void clearFetches() {
		fetches.clear();
	}
	
	public Iterator<String> fetchIterator() {
		return fetches.iterator();
	}
	

	public void clear() {
		clearFilters();
		clearSorts();
		clearFields();
		clearPaging();
		clearFetches();
		resultMode = RESULT_AUTO;
		disjunction = false;
	}

	// Paging
	public int getFirstResult() {
		return firstResult;
	}

	/**
	 * Zero based index of first result record to return.
	 */
	public Search setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public int getPage() {
		return page;
	}

	/**
	 * Zero based index of the page of records to return. The size of a page is
	 * determined by <code>maxResults</code>. If page is specified first result
	 * is ignored and the first result returned is calculated by <code>page *
	 * maxResults</code>.
	 */
	public Search setPage(int page) {
		this.page = page;
		return this;
	}

	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * The maximum number of records to return. Also used as page size when
	 * calculating the first record to return based on <code>page</code>.
	 */
	public Search setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	/**
	 * @return Zero based index of the first record to return. Calculation is
	 *         based on <code>page * maxResults</code> or <code>firstResult
	 *         </code> if page is not specified.
	 */
	public int calcFirstResult() {
		return (firstResult > 0) ? firstResult : (page > 0 && maxResults > 0) ? page * maxResults : 0;
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
		dest.resultMode = resultMode;
		dest.firstResult = firstResult;
		dest.page = page;
		dest.maxResults = maxResults;
		for (Field field : fields)
			dest.addField(field);
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

		if (searchClass == null ? s.searchClass != null : !searchClass.equals(s.searchClass))
			return false;
		if (disjunction != s.disjunction || resultMode != s.resultMode || firstResult != s.firstResult
				|| page != s.page || maxResults != s.maxResults)
			return false;
		if (!filters.equals(s.filters) || !sorts.equals(s.sorts) || !fields.equals(s.fields))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (searchClass == null ? 0 : searchClass.hashCode());
		hash = hash * 31 + (fields == null ? 0 : fields.hashCode());
		hash = hash * 31 + (filters == null ? 0 : filters.hashCode());
		hash = hash * 31 + (sorts == null ? 0 : sorts.hashCode());
		hash = hash * 31 + (disjunction ? 1 : 0);
		hash = hash * 31 + (new Integer(resultMode).hashCode());
		hash = hash * 31 + (new Integer(firstResult).hashCode());
		hash = hash * 31 + (new Integer(maxResults).hashCode());
		hash = hash * 31 + (new Integer(page).hashCode());

		return hash;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Search(");
		sb.append(searchClass);
		sb.append(")[first: ").append(firstResult);
		sb.append(", page: ").append(page);
		sb.append(", max: ").append(maxResults);
		sb.append("] {\n resultMode: ");

		switch (resultMode) {
		case RESULT_AUTO:
			sb.append("AUTO");
			break;
		case RESULT_ARRAY:
			sb.append("ARRAY");
			break;
		case RESULT_LIST:
			sb.append("LIST");
			break;
		case RESULT_MAP:
			sb.append("MAP");
			break;
		case RESULT_SINGLE:
			sb.append("SINGLE");
			break;
		default:
			sb.append("**INVALID RESULT MODE: (" + resultMode + ")**");
			break;
		}

		sb.append(",\n disjunction: ").append(disjunction);
		sb.append(",\n fields: { ");
		appendList(sb, fields, ", ");
		sb.append(" },\n filters: {\n  ");
		appendList(sb, filters, ",\n  ");
		sb.append("\n },\n sorts: { ");
		appendList(sb, sorts, ", ");
		sb.append(" }\n}");

		return sb.toString();
	}

	private void appendList(StringBuilder sb, List<?> list, String separator) {
		boolean first = true;
		for (Object o : list) {
			if (first) {
				first = false;
			} else {
				sb.append(separator);
			}
			sb.append(o);
		}
	}

}
