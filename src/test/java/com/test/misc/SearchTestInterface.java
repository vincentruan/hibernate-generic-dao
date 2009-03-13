package com.test.misc;

import java.util.List;

import com.trg.dao.search.ISearch;
import com.trg.dao.search.SearchResult;

public interface SearchTestInterface {
	@SuppressWarnings("unchecked")
	public List search(ISearch search);

	public int count(ISearch search);

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(ISearch search);

	public Object searchUnique(ISearch search);
}
