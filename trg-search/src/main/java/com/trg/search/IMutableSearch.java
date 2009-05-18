package com.trg.search;

import java.util.List;

/**
 * <code>IMutableSearch</code> is an extension of <code>ISearch</code> that
 * provides setters for all of the properties.
 * 
 * @author dwolverton
 * 
 */
public interface IMutableSearch extends ISearch {
	public IMutableSearch setFirstResult(int firstResult);

	public IMutableSearch setMaxResults(int maxResults);

	public IMutableSearch setPage(int page);

	public IMutableSearch setSearchClass(Class<?> searchClass);

	public IMutableSearch setFilters(List<Filter> filters);

	public IMutableSearch setDisjunction(boolean disjunction);

	public IMutableSearch setSorts(List<Sort> sorts);

	public IMutableSearch setFields(List<Field> fields);
	
	public IMutableSearch setDistinct(boolean distinct);

	public IMutableSearch setFetches(List<String> fetches);

	public IMutableSearch setResultMode(int resultMode);
}
