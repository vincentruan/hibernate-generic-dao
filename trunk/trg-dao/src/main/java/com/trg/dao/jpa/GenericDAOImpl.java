package com.trg.dao.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.trg.search.ExampleOptions;
import com.trg.search.Filter;
import com.trg.search.ISearch;
import com.trg.search.SearchResult;

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
		JPABaseDAO implements GenericDAO<T, ID> {

	protected Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];

	public int count(ISearch search) {
		return _count(persistentClass, search);
	}

	public T find(ID id) {
		return _find(persistentClass, id);
	}

	public T[] find(ID... ids) {
		return _find(persistentClass, ids);
	}

	public List<T> findAll() {
		return _all(persistentClass);
	}

	public void flush() {
		_flush();
	}

	public T getReference(ID id) {
		return _getReference(persistentClass, id);
	}

	public T[] getReferences(ID... ids) {
		return _getReferences(persistentClass, ids);
	}

	public boolean isAttached(T entity) {
		return _contains(entity);
	}

	public void refresh(T... entities) {
		_refresh(entities);
	}

	public boolean remove(T entity) {
		return _removeEntity(entity);
	}

	public void remove(T... entities) {
		_removeEntity(entities);
	}

	public boolean removeById(ID id) {
		return _removeById(persistentClass, id);
	}

	public void removeByIds(ID... ids) {
		_removeByIds(persistentClass, ids);
	}

	public T merge(T entity) {
		return _merge(entity);
	}

	public T[] merge(T... entities) {
		return _merge(persistentClass, entities);
	}

	public void persist(T... entities) {
		_persist(entities);
	}
	
	public T save(T entity) {
		return _persistOrMerge(entity);
	}

	public T[] save(T... entities) {
		return _persistOrMerge(persistentClass, entities);
	}

	public List<T> search(ISearch search) {
		return _search(persistentClass, search);
	}

	public SearchResult<T> searchAndCount(ISearch search) {
		return _searchAndCount(persistentClass, search);
	}

	public List searchGeneric(ISearch search) {
		return _search(persistentClass, search);
	}

	public T searchUnique(ISearch search) {
		return (T) _searchUnique(persistentClass, search);
	}

	public Object searchUniqueGeneric(ISearch search) {
		return _searchUnique(persistentClass, search);
	}

	public Filter getFilterFromExample(T example) {
		return _getFilterFromExample(example);
	}

	public Filter getFilterFromExample(T example, ExampleOptions options) {
		return _getFilterFromExample(example, options);
	}
}