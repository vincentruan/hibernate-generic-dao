package com.test.misc;

import java.util.List;

import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

public interface SearchTestInterface {
	@SuppressWarnings("unchecked")
	public List search(Search search);

	public int count(Search search);

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Search search);

	public Object searchUnique(Search search);
}
