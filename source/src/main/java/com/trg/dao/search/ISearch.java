package com.trg.dao.search;

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
public interface ISearch {
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
	 * Value for result mode. <code>RESULT_MAP</code> returns each row as a map
	 * with properties' names or keys for keys to the corresponding values.
	 * Here's an example:
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
	 * Value for result mode. <code>RESULT_SINGLE</code> - Exactly one field or
	 * no fields must be specified to use this result mode. The result list
	 * contains just the value of that property for each element. Here's an
	 * example:
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

	public int getFirstResult();

	// public ISearch setFirstResult(int firstResult);

	public int getMaxResults();

	// public ISearch setMaxResults(int maxResults);

	public int getPage();

	// public ISearch setPage(int page);

	public Class<?> getSearchClass();

	// public ISearch setSearchClass(Class<?> searchClass);

	public List<Filter> getFilters();

	// public ISearch setFilters(List<Filter> filters);

	public boolean isDisjunction();

	// public ISearch setDisjunction(boolean disjunction);

	public List<Sort> getSorts();

	// public ISearch setSorts(List<Sort> sorts);

	public List<Field> getFields();

	// public ISearch setFields(List<Field> fields);

	public List<String> getFetches();

	// public ISearch setFetches(List<String> fetches);

	public int getResultMode();

	// public ISearch setResultMode(int resultMode);
}
