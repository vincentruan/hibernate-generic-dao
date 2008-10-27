package com.trg.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.trg.search.Fetch;
import com.trg.search.Search;
import com.trg.search.SearchResult;

@SuppressWarnings("unchecked")
public class HibernateDAOHQLImpl extends HibernateDaoSupport {

	SearchToQLProcessor searchToQLProcessor;
	
	@Autowired
	public void setSearchToQLProcessor(SearchToQLProcessor searchToQLProcessor) {
		this.searchToQLProcessor = searchToQLProcessor;
	}

	@Autowired
	public void setSessionFactoryAutowire(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	/**
	 * Get the value of the Hibernate-specified ID property of an object.
	 */
	protected Serializable _getId(Object object) {
		if (object == null)
			throw new NullPointerException("Cannot get ID from null object.");

		return getSessionFactory().getClassMetadata(object.getClass())
				.getIdentifier(object, EntityMode.POJO);
	}

	/**
	 * Add the specified object as a new entry in the database. NOTE: The Java
	 * object is also attached to the Hibernate session in persistent state.
	 */
	protected void _create(Object object) {
		getSession().save(object);
	}

	/**
	 * Delete the object of the specified class with the specified id from the
	 * database.
	 */
	protected void _deleteById(Serializable id, Class klass) {
		if (id == null)
			return;
		getSession().delete(getSession().get(klass, id));
	}

	/**
	 * Delete the specified object from the database.
	 */
	protected void _deleteEntity(Object object) {
		if (object == null)
			return;
		// getSession().delete(object);

		Serializable id = _getId(object);
		if (id != null) {
			object = getSession().get(object.getClass(), id);
			if (object != null)
				getSession().delete(object);
		}
	}

	/**
	 * Get the object of the specified class with the specified id from the
	 * database.
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
	 * Update the corresponding object in the database with the properties of
	 * the specified object. The corresponding object is determined by id. NOTE:
	 * The Java object becomes attached to the Hibernate session.
	 */
	protected void _update(Object object) {
		getSession().update(object);
	}

	/**
	 * Update the corresponding object in the database with the properties of
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
		if (search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String hql = searchToQLProcessor.generateQL(search, paramList);
		System.out.println(hql);
		Query query = getSession().createQuery(hql);
		addParams(query, paramList);
		addPaging(query, search);
		addFetchMode(query, search);

		return query.list();
	}

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>Search</code> if there were no paging or maxResult limits.
	 * 
	 * @see Search
	 */
	protected int _searchLength(Search search) {
		if (search == null)
			return 0;

		List<Object> paramList = new ArrayList<Object>();
		String hql = searchToQLProcessor.generateRowCountQL(search, paramList);
		System.out.println(hql);
		Query query = getSession().createQuery(hql);
		addParams(query, paramList);

		return ((Long) query.uniqueResult()).intValue();
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 * 
	 * @see Search
	 */
	protected SearchResult _searchAndLength(Search search) {
		if (search == null)
			return null;

		SearchResult result = new SearchResult();
		result.search = search;
		result.firstResult = search.getFirstResult();
		result.page = search.getPage();
		result.maxResults = search.getMaxResults();

		result.results = _search(search);

		if (search.getMaxResults() > 0) {
			result.totalLength = _searchLength(search);
		} else {
			result.totalLength = result.results.size()
					+ search.calcFirstResult();
		}

		return result;
	}

	/**
	 * Search for a single result using the given parameters.
	 */
	public Object _searchUnique(Search search) throws NonUniqueResultException {
		if (search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String hql = searchToQLProcessor.generateQL(search, paramList);
		System.out.println(hql);
		Query query = getSession().createQuery(hql);
		addParams(query, paramList);
		addFetchMode(query, search);

		return query.uniqueResult();
	}

	/**
	 * Returns true if the object is connected to the current hibernate session.
	 */
	protected boolean _isConnected(Object o) {
		return getSession().contains(o);
	}

	/**
	 * Flushes changes in the hibernate cache to the database.
	 */
	protected void _flush() {
		getSession().flush();
	}

	/**
	 * Refresh the content of the given entity from the current database state.
	 */
	protected void _refresh(Object o) {
		getSession().refresh(o);
	}

	// ---- SEARCH HELPERS ---- //

	@SuppressWarnings("unchecked")
	private void addParams(Query query, List<Object> params) {
		int i = 1;
		for (Object o : params) {
			if (o instanceof Collection) {
				query.setParameterList("p" + Integer.toString(i++),
						(Collection) o);
			} else if (o instanceof Object[]) {
				query.setParameterList("p" + Integer.toString(i++),
						(Object[]) o);
			} else {
				query.setParameter("p" + Integer.toString(i++), o);
			}
		}
	}

	private void addPaging(Query query, Search search) {
		if (search.calcFirstResult() > 0) {
			query.setFirstResult(search.calcFirstResult());
		}
		if (search.getMaxResults() > 0) {
			query.setMaxResults(search.getMaxResults());
		}
	}

	private void addFetchMode(Query query, Search search) {
		switch (search.getFetchMode()) {
		case Search.FETCH_ARRAY:
			query.setResultTransformer(ARRAY_RESULT_TRANSFORMER);
			break;
		case Search.FETCH_LIST:
			query.setResultTransformer(Transformers.TO_LIST);
			break;
		case Search.FETCH_MAP:
			List<String> keyList = new ArrayList<String>();
			Iterator<Fetch> fetchItr = search.fetchIterator();
			while (fetchItr.hasNext()) {
				Fetch fetch = fetchItr.next();
				if (fetch.key != null && !fetch.key.equals("")) {
					keyList.add(fetch.key);
				} else {
					keyList.add(fetch.property);
				}
			}
			query.setResultTransformer(new MapResultTransformer(keyList
					.toArray(new String[0])));
			break;
		default: // Search.FETCH_ENTITY / Search.FETCH_SINGLE
			break;
		}
	}

	private static final ResultTransformer ARRAY_RESULT_TRANSFORMER = new ResultTransformer() {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public List transformList(List collection) {
			return collection;
		}

		public Object transformTuple(Object[] tuple, String[] aliases) {
			return tuple;
		}
	};

	private static class MapResultTransformer implements ResultTransformer {
		private static final long serialVersionUID = 1L;

		private String[] keys;

		public MapResultTransformer(String[] keys) {
			this.keys = keys;
		}

		@SuppressWarnings("unchecked")
		public List transformList(List collection) {
			return collection;
		}

		public Object transformTuple(Object[] tuple, String[] aliases) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				if (key != null) {
					map.put(key, tuple[i]);
				}
			}

			return map;
		}
	}
}
