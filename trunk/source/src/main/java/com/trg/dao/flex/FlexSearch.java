package com.trg.dao.flex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.trg.dao.search.Field;
import com.trg.dao.search.Filter;
import com.trg.dao.search.ISearch;
import com.trg.dao.search.Sort;

/**
 * This is a search DTO that is suitable for both Flex and Java. Is can be used
 * to pass search parameters to and from a remote Flex client.
 * 
 * @author dwolverton
 * 
 */
public class FlexSearch implements Serializable {

	private static final long serialVersionUID = 1L;

	protected int firstResult = -1; // -1 stands for unspecified

	protected int maxResults = -1; // -1 stands for unspecified

	protected int page = -1; // -1 stands for unspecified

	protected String searchClassName;

	protected List<Filter> filters = new ArrayList<Filter>();

	protected boolean disjunction;

	protected List<Sort> sorts = new ArrayList<Sort>();

	protected List<Field> fields = new ArrayList<Field>();
	
	protected boolean distinct;

	protected List<String> fetches = new ArrayList<String>();

	protected int resultMode = ISearch.RESULT_AUTO;

	public void setSearchClassName(String searchClassName) throws ClassNotFoundException {
		this.searchClassName = searchClassName;
	}

	public String getSearchClassName() {
		return searchClassName;
	}

	public Filter[] getFilters() {
		return filters.toArray(new Filter[0]);
	}

	public void setFilters(Filter[] filters) {
		this.filters.clear();
		if (filters != null) {
			for (int i = 0; i < filters.length; i++) {
				Object o = filters[i];
				if (o != null && o instanceof Filter) {
					this.filters.add(filters[i]);
				}
			}
		}
	}

	public Sort[] getSorts() {
		return sorts.toArray(new Sort[0]);
	}

	public void setSorts(Sort[] sorts) {
		this.sorts.clear();
		if (sorts != null) {
			for (int i = 0; i < sorts.length; i++) {
				Object o = sorts[i];
				if (o != null && o instanceof Sort) {
					this.sorts.add(sorts[i]);
				}
			}
		}
	}

	public Field[] getFields() {
		return fields.toArray(new Field[0]);
	}

	public void setFields(Field[] fields) {
		this.fields.clear();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				if (f != null && f.getProperty() != null && f.getProperty().length() > 0) {
					if (f.getKey() == null)
						f.setKey(f.getProperty());
					this.fields.add(f);
				}
			}
		}
	}

	public String[] getFetches() {
		return fetches.toArray(new String[0]);
	}

	public void setFetches(String[] fetches) {
		this.fetches.clear();
		if (fetches != null) {
			for (int i = 0; i < fetches.length; i++) {
				if (fetches[i] != null && !"".equals(fetches[i]))
					this.fetches.add(fetches[i]);
			}
		}
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

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isDisjunction() {
		return disjunction;
	}

	public void setDisjunction(boolean disjunction) {
		this.disjunction = disjunction;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public int getResultMode() {
		return resultMode;
	}

	public void setResultMode(int resultMode) {
		this.resultMode = resultMode;
	}
}
