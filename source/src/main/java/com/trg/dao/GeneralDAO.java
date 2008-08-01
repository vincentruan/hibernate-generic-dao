package com.trg.dao;

import java.io.Serializable;
import java.util.List;

import com.trg.search.Search;
import com.trg.search.SearchResult;

@SuppressWarnings("unchecked")
public interface GeneralDAO {

	public void create(Object object);

	public void deleteById(Serializable id, Class klass);

	public void deleteEntity(Object object);

	public Object fetch(Serializable id, Class klass);
	
	public List fetchAll(Class klass);

	public void update(Object object);

	public List search(Search search);
	
	public int searchLength(Search search);
	
	public SearchResult searchAndLength(Search search);

	public void flush();
	
	public boolean isConnected(Object object);

}