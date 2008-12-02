package com.trg.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.NonUniqueResultException;

import com.trg.dao.GenericDAO;
import com.trg.search.Search;
import com.trg.search.SearchResult;

/**
 * Implementation of <code>GeneralDAO</code> using SpringHibernateSupport and
 * HQL for searches.
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
		HibernateDAOHQLImpl implements GenericDAO<T, ID> {

	protected Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];

	public void create(T object) {
		if (!persistentClass.isInstance(object))
			throw new IllegalArgumentException(
					"Object class does not match dao type.");
		_create(object);
	}

	public boolean createOrUpdate(T object) {
		Serializable id = _getId(object);
		if (id == null || (new Long(0)).equals(id)) {
			create(object);
			return true;
		} else {
			update(object);
			return false;
		}
	}

	public boolean deleteById(ID id) {
		return _deleteById(id, persistentClass);
	}

	public boolean deleteEntity(T object) {
		if (!persistentClass.isInstance(object))
			throw new IllegalArgumentException(
					"Object class does not match dao type.");
		return _deleteEntity(object);
	}

	public T fetch(ID id) {
		return (T) _fetch(id, persistentClass);
	}

	public List<T> fetchAll() {
		return _fetchAll(persistentClass);
	}

	public void update(T object) {
		if (!persistentClass.isInstance(object))
			throw new IllegalArgumentException(
					"Object class does not match dao type.");
		_update(object);
	}

	public List<T> search(Search search) {
		boolean classNull = false;
		if (search == null)
			return fetchAll();
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException(
					"Search class does not match dao type.");
		}
		List<T> result = _search(search);
		if (classNull)
			search.setSearchClass(null);
		return result;
	}

	public int searchLength(Search search) {
		boolean classNull = false;
		if (search == null)
			return 0;
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException(
					"Search class does not match dao type.");
		}
		int result = _searchLength(search);
		if (classNull)
			search.setSearchClass(null);
		return result;
	}

	public SearchResult<T> searchAndLength(Search search) {
		boolean classNull = false;
		if (search == null) {
			SearchResult<T> result = new SearchResult<T>();
			result.results = fetchAll();
			result.totalLength = result.results.size();
			return result;
		}
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException(
					"Search class does not match dao type.");
		}
		SearchResult<T> result = _searchAndLength(search);
		if (classNull)
			search.setSearchClass(null);
		return result;
	}

	public boolean isConnected(Object object) {
		return _isConnected(object);
	}

	public void flush() {
		_flush();
	}

	public void refresh(Object object) {
		_refresh(object);
	}

	public List searchGeneric(Search search) {
		boolean classNull = false;
		if (search == null)
			return fetchAll();
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException(
					"Search class does not match dao type.");
		}
		List result = _search(search);
		if (classNull)
			search.setSearchClass(null);
		return result;
	}

	public Object searchUnique(Search search) throws NonUniqueResultException {
		boolean classNull = false;
		if (search == null)
			return fetchAll();
		if (search.getSearchClass() == null) {
			search.setSearchClass(persistentClass);
			classNull = true;
		} else if (!persistentClass.equals(search.getSearchClass())) {
			throw new IllegalArgumentException(
					"Search class does not match dao type.");
		}
		Object result = _searchUnique(search);
		if (classNull)
			search.setSearchClass(null);
		return result;
	}

}