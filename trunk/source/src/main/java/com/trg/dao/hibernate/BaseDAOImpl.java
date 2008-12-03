package com.trg.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.trg.dao.MetaDataUtil;
import com.trg.search.Search;
import com.trg.search.SearchResult;

/**
 * Base class for DAOs that uses Hibernate SessionFactory and HQL for searches.
 * The SessionFactory property is annotated for automatic resource injection.
 * 
 * @author dwolverton
 * 
 */
@SuppressWarnings("unchecked")
public class BaseDAOImpl {

	private static HibernateSearchProcessor searchProcessor;

	private SessionFactory sessionFactory;
	
	private MetaDataUtil metaDataUtil;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		searchProcessor = HibernateSearchProcessor.getInstanceForSessionFactory(sessionFactory);
		metaDataUtil = HibernateMetaDataUtil.getInstanceForSessionFactory(sessionFactory);
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	protected MetaDataUtil getMetaDataUtil() {
		return this.metaDataUtil;
	}

	/**
	 * Add the specified object as a new entry in the datastore. NOTE: The Java
	 * object is also attached to the Hibernate session in persistent state.
	 */
	protected void _create(Object object) {
		getSession().save(object);
	}

	/**
	 * Remove the object of the specified class with the specified id from the
	 * datastore.
	 * 
	 * @return <code>true</code> if the object is found in the datastore and
	 *         deleted, <code>false</code> if the item is not found.
	 */
	protected boolean _deleteById(Serializable id, Class klass) {
		if (id != null) {
			Object object = getSession().get(klass, id);
			if (object != null) {
				getSession().delete(object);
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove the specified object from the datastore.
	 * 
	 * @return <code>true</code> if the object is found in the datastore and
	 *         removed, <code>false</code> if the item is not found.
	 */
	protected boolean _deleteEntity(Object object) {
		if (object != null) {
			Serializable id = getMetaDataUtil().getId(object);
			if (id != null) {
				object = getSession().get(object.getClass(), id);
				if (object != null) {
					getSession().delete(object);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the object of the specified class with the specified id from the
	 * datastore.
	 */
	protected <T> T _fetch(Serializable id, Class<T> klass) {
		return (T) getSession().get(klass, id);
	}

	/**
	 * Get a list of all the objects of the specified class.
	 */
	protected <T> List<T> _fetchAll(Class<T> klass) {
		return getSession().createCriteria(klass).setResultTransformer(
				Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Update the corresponding object in the datastore with the properties of
	 * the specified object. The corresponding object is determined by id. NOTE:
	 * The Java object becomes attached to the Hibernate session.
	 */
	protected void _update(Object object) {
		getSession().update(object);
	}

	/**
	 * Update the corresponding object in the datastore with the properties of
	 * the specified object. The corresponding object is determined by id. NOTE:
	 * The Java object does not become attached to the Hibernate session. It
	 * remains in its current state.
	 */
	protected void _merge(Object object) {
		getSession().merge(object);
	}

	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>Search</code> object.
	 * 
	 * @see Search
	 */
	protected List _search(Search search) {
		return searchProcessor.search(getSession(), search);
	}

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>Search</code> if there were no paging or maxResult limits.
	 * 
	 * @see Search
	 */
	protected int _searchLength(Search search) {
		return searchProcessor.count(getSession(), search);
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 * 
	 * @see Search
	 */
	protected SearchResult _searchAndLength(Search search) {
		return searchProcessor.searchAndCount(getSession(), search);
	}

	/**
	 * Search for a single result using the given parameters.
	 */
	public Object _searchUnique(Search search) throws NonUniqueResultException {
		return searchProcessor.searchUnique(getSession(), search);
	}

	/**
	 * Returns true if the object is connected to the current hibernate session.
	 */
	protected boolean _isConnected(Object o) {
		return getSession().contains(o);
	}

	/**
	 * Flushes changes in the hibernate cache to the datastore.
	 */
	protected void _flush() {
		getSession().flush();
	}

	/**
	 * Refresh the content of the given entity from the current datastore state.
	 */
	protected void _refresh(Object o) {
		getSession().refresh(o);
	}
}
