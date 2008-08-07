package com.trg.dao;

import java.io.Serializable;
import java.util.List;

import com.trg.search.Search;
import com.trg.search.SearchResult;

@SuppressWarnings("unchecked")
public class GeneralDAOImpl extends HibernateDAOImpl implements GeneralDAO {

	public void create(Object object) {
		super._create(object);
	}
	
	public boolean createOrUpdate(Object object) {
		Serializable id = _getId(object);
		if (id == null || (new Long(0)).equals(id)) {
			create(object);
			return true;
		} else {
			update(object);
			return false;
		}
	}
	
	public void deleteById(Serializable id, Class klass) {
		super._deleteById(id, klass);
	}
	
	public void deleteEntity(Object object) {
		super._deleteEntity(object);
	}
	
	public Object fetch(Serializable id, Class klass) {
		return super._fetch(id, klass);
	}

	public List fetchAll(Class klass) {
		return super._fetchAll(klass);
	}
	
	public void update(Object object) {
		super._update(object);
	}
	
	public List search(Search search) {
		return super._search(search);
	}
	
	public int searchLength(Search search) {
		return super._searchLength(search);
	}
	
	public SearchResult searchAndLength(Search search) {
		return super._searchAndLength(search);
	}
	
	public void flush() {
		super._flush();
	}

	public boolean isConnected(Object object) {
		return super._isConnected(object);
	}
}
