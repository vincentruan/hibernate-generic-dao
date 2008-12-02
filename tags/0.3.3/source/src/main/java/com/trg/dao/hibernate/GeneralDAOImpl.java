package com.trg.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.NonUniqueResultException;

import com.trg.dao.GeneralDAO;
import com.trg.search.Search;
import com.trg.search.SearchResult;

/**
 * Implementation of <code>GeneralDAO</code> using SpringHibernateSupport and
 * HQL for searches.
 * 
 * @author dwolverton
 */
@SuppressWarnings("unchecked")
public class GeneralDAOImpl extends HibernateDAOHQLImpl implements GeneralDAO {

	public void create(Object object) {
		_create(object);
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

	public boolean deleteById(Serializable id, Class<?> klass) {
		return _deleteById(id, klass);
	}

	public boolean deleteEntity(Object object) {
		return _deleteEntity(object);
	}

	public <T> T fetch(Serializable id, Class<T> klass) {
		return _fetch(id, klass);
	}

	public <T> List<T> fetchAll(Class<T> klass) {
		return _fetchAll(klass);
	}

	public void update(Object object) {
		_update(object);
	}

	public List search(Search search) {
		return _search(search);
	}

	public int searchLength(Search search) {
		return _searchLength(search);
	}

	public SearchResult searchAndLength(Search search) {
		return _searchAndLength(search);
	}

	public void flush() {
		_flush();
	}

	public boolean isConnected(Object object) {
		return _isConnected(object);
	}

	public Object searchUnique(Search search) throws NonUniqueResultException {
		return _searchUnique(search);
	}

	public void refresh(Object object) {
		_refresh(object);
	}
}
