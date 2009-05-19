package com.trg.dao.jpa;

import java.io.Serializable;
import java.util.List;

import com.trg.search.ExampleOptions;
import com.trg.search.Filter;
import com.trg.search.ISearch;
import com.trg.search.SearchResult;

/**
 * Implementation of <code>GeneralDAO</code> using Hibernate.
 * The SessionFactory property is annotated for automatic resource injection.
 * 
 * @author dwolverton
 */
@SuppressWarnings("unchecked")
public class GeneralDAOImpl extends JPABaseDAO implements GeneralDAO {

	public int count(ISearch search) {
		return _count(search);
	}

	public <T> T find(Class<T> type, Serializable id) {
		return (T) _find(type, id);
	}

	public <T> T[] find(Class<T> type, Serializable... ids) {
		return _find(type, ids);
	}

	public <T> List<T> findAll(Class<T> type) {
		return _all(type);
	}

	public void flush() {
		_flush();
	}

	public <T> T getReference(Class<T> type, Serializable id) {
		return _getReference(type, id);
	}

	public <T> T[] getReferences(Class<T> type, Serializable... ids) {
		return _getReferences(type, ids);
	}

	public boolean isAttached(Object entity) {
		return _contains(entity);
	}

	public void refresh(Object... entities) {
		_refresh(entities);
	}

	public boolean remove(Object entity) {
		return _removeEntity(entity);
	}

	public void remove(Object... entities) {
		_removeEntity(entities);
	}

	public boolean removeById(Class<?> type, Serializable id) {
		return _removeById(type, id);
	}

	public void removeByIds(Class<?> type, Serializable... ids) {
		_removeByIds(type, ids);
	}

	public <T> T merge(T entity) {
		return _merge(entity);
	}

	public Object[] merge(Object... entities) {
		return _merge(Object.class, entities);
	}

	public void persist(Object... entities) {
		_persist(entities);
	}

	public <T> T save(T entity) {
		return _persistOrMerge(entity);
	}

	public Object[] save(Object... entities) {
		return _persistOrMerge(Object.class, entities);
	}

	public List search(ISearch search) {
		return _search(search);
	}

	public SearchResult searchAndCount(ISearch search) {
		return _searchAndCount(search);
	}

	public Object searchUnique(ISearch search) {
		return _searchUnique(search);
	}

	public Filter getFilterFromExample(Object example) {
		return _getFilterFromExample(example);
	}

	public Filter getFilterFromExample(Object example, ExampleOptions options) {
		return _getFilterFromExample(example, options);
	}
}
