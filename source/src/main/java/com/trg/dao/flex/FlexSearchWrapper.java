package com.trg.dao.flex;

import java.util.List;

import com.trg.dao.search.Field;
import com.trg.dao.search.Filter;
import com.trg.dao.search.ISearch;
import com.trg.dao.search.Sort;

/**
 * This provides a wrapper around a FlexSearch so that is can be passed into DAO
 * methods requiring the ISearch interface.
 * 
 * @author dwolverton
 * 
 */
public class FlexSearchWrapper implements ISearch {

	FlexSearch search;

	public FlexSearchWrapper(FlexSearch flexSearch) {
		search = flexSearch;
	}

	public List<String> getFetches() {
		return search.fetches;
	}

	public List<Field> getFields() {
		return search.fields;
	}

	public List<Filter> getFilters() {
		return search.filters;
	}

	public int getFirstResult() {
		return search.firstResult;
	}

	public int getMaxResults() {
		return search.maxResults;
	}

	public int getPage() {
		return search.page;
	}

	public int getResultMode() {
		return search.resultMode;
	}

	public Class<?> getSearchClass() {
		if (search.searchClassName == null || "".equals(search.searchClassName)) {
			return null;
		} else {
			try {
				return Class.forName(search.searchClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public List<Sort> getSorts() {
		return search.sorts;
	}

	public boolean isDisjunction() {
		return search.disjunction;
	}

	public boolean isDistinct() {
		return search.distinct;
	}
}
