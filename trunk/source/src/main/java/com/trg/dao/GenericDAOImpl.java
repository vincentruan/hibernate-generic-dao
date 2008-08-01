package com.trg.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.trg.search.Search;
import com.trg.search.SearchResult;

@SuppressWarnings("unchecked")
public class GenericDAOImpl<T> extends HibernateDAOImpl implements GenericDAO<T> {

	private Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0];

	public void create(T object) {
		if (!persistentClass.isInstance(object)) throw new IllegalArgumentException("Object class does not match dao type.");
		super._create(object);
	}

	public void deleteById(Serializable id) {
		super._deleteById(id, persistentClass);
	}

	public void deleteEntity(T object) {
		if (!persistentClass.isInstance(object)) throw new IllegalArgumentException("Object class does not match dao type.");
		super._deleteEntity(object);
	}

	public T fetch(Serializable id) {
		return (T) super._fetch(id, persistentClass);
	}

	public List<T> fetchAll() {
		return super._fetchAll(persistentClass);
	}

	public void update(T object) {
		if (!persistentClass.isInstance(object)) throw new IllegalArgumentException("Object class does not match dao type.");
		super._update(object);
	}

	public List<T> search(Search search) {
		boolean classNull = false;
		if (search == null) return fetchAll();
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException("Search class does not match dao type.");
		}
		List<T> result = super._search(search);
		if (classNull) search.setSearchClass(null);
		return result;
	}
	
	public int searchLength(Search search) {
		boolean classNull = false;
		if (search == null) return 0;
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException("Search class does not match dao type.");
		}
		int result = super._searchLength(search);
		if (classNull) search.setSearchClass(null);
		return result;
	}
	
	public SearchResult<T> searchAndLength(Search search) {
		boolean classNull = false;
		if (search == null) {
			SearchResult<T> result = new SearchResult<T>();
			result.result = fetchAll();
			result.totalLength = result.result.size();
			return result;
		}
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException("Search class does not match dao type.");
		}
		SearchResult<T> result = super._searchAndLength(search);
		if (classNull) search.setSearchClass(null);
		return result;
	}

	public boolean isConnected(Object object) {
		return super._isConnected(object);
	}
	
	public void flush() {
		super._flush();
	}

}