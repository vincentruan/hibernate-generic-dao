package com.trg.dao.hibernate.original;

import java.io.Serializable;
import java.util.List;

import org.hibernate.NonUniqueResultException;

import com.trg.dao.hibernate.HibernateBaseDAO;
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
public class GeneralDAOImpl extends HibernateBaseDAO implements GeneralDAO {

	public void create(Object object) {
		_save(object);
	}

	public boolean createOrUpdate(Object object) {
		Serializable id = getMetaDataUtil().getId(object);
		if (id == null || (new Long(0)).equals(id)) {
			create(object);
			return true;
		} else {
			update(object);
			return false;
		}
	}

	public boolean deleteById(Class<?> klass, Serializable id) {
		return _deleteById(klass, id);
	}

	public boolean deleteEntity(Object object) {
		return _deleteEntity(object);
	}

	public <T> T fetch(Class<T> klass, Serializable id) {
		return _get(klass, id);
	}

	public <T> List<T> fetchAll(Class<T> klass) {
		return _all(klass);
	}

	public void update(Object object) {
		_update(object);
	}

	public List search(ISearch search) {
		return _search(search);
	}

	public int count(ISearch search) {
		return _count(search);
	}

	public SearchResult searchAndCount(ISearch search) {
		return _searchAndCount(search);
	}

	public void flush() {
		_flush();
	}

	public boolean isConnected(Object object) {
		return _sessionContains(object);
	}

	public Object searchUnique(ISearch search) throws NonUniqueResultException {
		return _searchUnique(search);
	}

	public void refresh(Object object) {
		_refresh(object);
	}
	
	public Filter getFilterFromExample(Object example) {
		return _getFilterFromExample(example);
	}

	public Filter getFilterFromExample(Object example, ExampleOptions options) {
		return _getFilterFromExample(example, options);
	}
}
