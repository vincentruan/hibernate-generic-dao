package com.trg.dao.dao.original;

import java.io.Serializable;

import com.trg.dao.search.Fetch;
import com.trg.dao.search.Filter;
import com.trg.dao.search.Search;
import com.trg.dao.search.Sort;

/**
 * This simply extends <code>Search</code> by providing accessor methods for
 * the internal lists of search parameters so the search can be serialized and
 * sent over the network.
 * 
 * @author dwolverton
 * 
 */
public class FlexSearch extends Search implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public void setClassName(String className) throws ClassNotFoundException {
		searchClass = Class.forName(className);
	}

	public String getClassName() {
		if (searchClass == null)
			return null;
		return searchClass.getName();
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
			for (int i =  0; i < sorts.length; i++) {
				Object o = sorts[i];
				if (o != null && o instanceof Sort) {
					this.sorts.add(sorts[i]);
				}
			}
		}
	}

	public Fetch[] getFetches() {
		return fetches.toArray(new Fetch[0]);
	}

	public void setFetches(Fetch[] fetches) {
		this.fetches.clear();
		if (fetches != null) {
			for (int i =  0; i < fetches.length; i++) {
				Fetch f = fetches[i];
				if (f != null && f.getProperty() != null && f.getProperty().length() > 0) {
					if (f.getKey() == null) f.setKey(f.getProperty());
					this.fetches.add(f);
				}
			}
		}
	}
}
