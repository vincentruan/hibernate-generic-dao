package com.trg.dao.dao.standard;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;

import com.trg.dao.hibernate.BaseDAOImpl;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

/**
 * Implementation of <code>GeneralDAO</code> using Hibernate.
 * The SessionFactory property is annotated for automatic resource injection.
 * 
 * @author dwolverton
 */
@SuppressWarnings("unchecked")
public class GeneralDAOImpl extends BaseDAOImpl implements GeneralDAO {

	@Override
	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	public int count(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _count(search);
	}

	public <T> T find(Class<T> type, Serializable id) {
		return (T) _get(type, id);
	}

	public <T> T[] find(Class<T> type, Serializable... ids) {
		return _get(type, ids);
	}

	public <T> List<T> findAll(Class<T> type) {
		return _all(type);
	}

	public void flush() {
		_flush();
	}

	public <T> T getReference(Class<T> type, Serializable id) {
		return _load(type, id);
	}

	public <T> T[] getReferences(Class<T> type, Serializable... ids) {
		return _load(type, ids);
	}

	public boolean isAttached(Object entity) {
		return _sessionContains(entity);
	}

	public void refresh(Object entity) {
		_refresh(entity);
	}

	public boolean remove(Object entity) {
		return _deleteEntity(entity);
	}

	public void remove(Object... entities) {
		_deleteEntities(entities);
	}

	public boolean removeById(Class<?> type, Serializable id) {
		return _deleteById(type, id);
	}

	public void removeByIds(Class<?> type, Serializable... ids) {
		_deleteById(type, ids);
	}

	public boolean save(Object entity) {
		return _saveOrUpdateIsNew(entity);
	}

	public void save(Object... entities) {
		_saveOrUpdate(entities);
	}

	public List search(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _search(search);
	}

	public SearchResult searchAndCount(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _searchAndCount(search);
	}

	public Object searchUnique(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _searchUnique(search);
	}
}
