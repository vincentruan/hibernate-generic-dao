package com.trg.dao.dao.original;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.NonUniqueResultException;

import com.trg.dao.hibernate.BaseDAOImpl;
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
		BaseDAOImpl implements GenericDAO<T, ID> {

	protected Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];

	public void create(T object) {
		if (!persistentClass.isInstance(object))
			throw new IllegalArgumentException(
					"Object class does not match dao type.");
		_save(object);
	}

	public boolean createOrUpdate(T object) {
		Serializable id = getMetaDataUtil().getId(object);
		if (id == null || (new Long(0)).equals(id)) {
			create(object);
			return true;
		} else {
			update(object);
			return false;
		}
	}

	public boolean deleteById(ID id) {
		return _deleteById(persistentClass, id);
	}

	public boolean deleteEntity(T object) {
		if (!persistentClass.isInstance(object))
			throw new IllegalArgumentException(
					"Object class does not match dao type.");
		return _deleteEntity(object);
	}

	public T fetch(ID id) {
		return (T) _get(persistentClass, id);
	}

	public List<T> fetchAll() {
		return _all(persistentClass);
	}

	public void update(T object) {
		if (!persistentClass.isInstance(object))
			throw new IllegalArgumentException(
					"Object class does not match dao type.");
		_update(object);
	}

	public List<T> search(ISearch search) {
		if (search == null)
			return fetchAll();
		
		return _search(persistentClass, search);
	}

	public int count(ISearch search) {
		if (search == null)
			return 0;
		return _count(persistentClass, search);
	}

	public SearchResult<T> searchAndCount(ISearch search) {
		if (search == null) {
			SearchResult<T> result = new SearchResult<T>();
			result.setResult(fetchAll());
			result.setTotalCount(result.getResult().size());
			return result;
		}
		
		return _searchAndCount(persistentClass, search);
	}

	public boolean isConnected(Object object) {
		return _sessionContains(object);
	}

	public void flush() {
		_flush();
	}

	public void refresh(Object object) {
		_refresh(object);
	}

	public List searchGeneric(ISearch search) {
		if (search == null)
			return fetchAll();
		
		return _search(persistentClass, search);
	}

	public Object searchUnique(ISearch search) throws NonUniqueResultException {
		if (search == null)
			return null;
		
		return _searchUnique(persistentClass, search);
	}

}