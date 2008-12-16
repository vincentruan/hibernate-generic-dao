package com.trg.dao.dao.original;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.NonUniqueResultException;
import org.hibernate.SessionFactory;

import com.trg.dao.hibernate.BaseDAOImpl;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

/**
 * Implementation of <code>GeneralDAO</code> using SpringHibernateSupport and
 * HQL for searches.
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

	public List search(Search search) {
		return _search(search);
	}

	public int count(Search search) {
		return _count(search);
	}

	public SearchResult searchAndCount(Search search) {
		return _searchAndCount(search);
	}

	public void flush() {
		_flush();
	}

	public boolean isConnected(Object object) {
		return _isAttached(object);
	}

	public Object searchUnique(Search search) throws NonUniqueResultException {
		return _searchUnique(search);
	}

	public void refresh(Object object) {
		_refresh(object);
	}
}
