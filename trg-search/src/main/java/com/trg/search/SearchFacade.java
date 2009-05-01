package com.trg.search;

import java.util.List;

public interface SearchFacade {

	@SuppressWarnings("unchecked")
	public List search(ISearch search);

	@SuppressWarnings("unchecked")
	public List search(Class<?> searchClass, ISearch search);

	public int count(ISearch search);

	public int count(Class<?> searchClass, ISearch search);

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(ISearch search);

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Class<?> searchClass, ISearch search);

	public Object searchUnique(ISearch search);

	public Object searchUnique(Class<?> searchClass, ISearch search);
	
	public Filter getFilterFromExample(Object example);
	
	public Filter getFilterFromExample(Object example, ExampleOptions options);

}