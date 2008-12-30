package com.trg.dao.dao.standard;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;

import com.trg.dao.hibernate.BaseDAOImpl;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

/**
 * Implementation of <code>GenericDAO</code> using Hibernate.
 * The SessionFactory property is annotated for automatic resource injection.
 * 
 * @author dwolverton
 * 
 * @param <T>
 *            The type of the domain object for which this instance is to be
 *            used.
 * @param <ID>
 *            The type of the id of the domain object for which this instance is
 *            to be used.
 */
@SuppressWarnings("unchecked")
public class GenericDAOImpl<T, ID extends Serializable> extends
		BaseDAOImpl implements GenericDAO<T, ID> {

	@Override
	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	protected Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];

	public int count(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _count(search, persistentClass);
	}

	public T find(Serializable id) {
		return _get(persistentClass, id);
	}

	public T[] find(Serializable... ids) {
		return _get(persistentClass, ids);
	}

	public List<T> findAll() {
		return _all(persistentClass);
	}

	public void flush() {
		_flush();
	}

	public T getReference(Serializable id) {
		return _load(persistentClass, id);
	}

	public T[] getReferences(Serializable... ids) {
		return _load(persistentClass, ids);
	}

	public boolean isAttached(T entity) {
		return _sessionContains(entity);
	}

	public void refresh(T... entities) {
		_refresh(entities);
	}

	public boolean remove(T entity) {
		return _deleteEntity(entity);
	}

	public void remove(T... entities) {
		_deleteEntities(entities);
	}

	public boolean removeById(Serializable id) {
		return _deleteById(persistentClass, id);
	}

	public void removeByIds(Serializable... ids) {
		_deleteById(persistentClass, ids);
	}

	public boolean save(T entity) {
		return _saveOrUpdateIsNew(entity);
	}

	public boolean[] save(T... entities) {
		return _saveOrUpdateIsNew(entities);
	}

	public List<T> search(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _search(search, persistentClass);
	}

	public SearchResult<T> searchAndCount(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _searchAndCount(search, persistentClass);
	}

	public List searchGeneric(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _search(search, persistentClass);
	}

	public T searchUnique(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return (T) _searchUnique(search, persistentClass);
	}

	public Object searchUniqueGeneric(Search search) {
		if (search == null)
			throw new NullPointerException("Search cannot be null");
		
		return _searchUnique(search, persistentClass);
	}

	
}