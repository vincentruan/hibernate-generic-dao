package com.trg.dao.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import com.trg.dao.BaseSearchProcessor;
import com.trg.dao.search.Field;
import com.trg.dao.search.ISearch;
import com.trg.dao.search.SearchResult;
import com.trg.dao.search.SearchUtil;

/**
 * Implementation of SearchToQLProcessor that generates HQL
 * @author dwolverton
 */
public class HibernateSearchProcessor extends BaseSearchProcessor {
	
	private static Map<SessionFactory, HibernateSearchProcessor> map = new HashMap<SessionFactory, HibernateSearchProcessor>();

	public static HibernateSearchProcessor getInstanceForSessionFactory(
			SessionFactory sessionFactory) {
		HibernateSearchProcessor instance = map.get(sessionFactory);
		if (instance == null) {
			instance = new HibernateSearchProcessor(HibernateMetaDataUtil.getInstanceForSessionFactory(sessionFactory));
			map.put(sessionFactory, instance);
		}
		return instance;
	}

	private HibernateSearchProcessor(HibernateMetaDataUtil mdu) {
		super(QLTYPE_HQL, mdu);
	}
	
	// --- Public Methods ---
	
	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>ISearch</code> object.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public List search(Session session, ISearch search) {
		if (search == null)
			return null;
		
		return search(session, search.getSearchClass(), search);
	}
	
	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>ISearch</code> object.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public List search(Session session, Class<?> searchClass, ISearch search) {
		if (searchClass == null || search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String hql = generateQL(searchClass, search, paramList);
		Query query = session.createQuery(hql);
		addParams(query, paramList);
		addPaging(query, search);
		addResultMode(query, search);

		return query.list();
	}

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>ISearch</code> if there were no paging or maxResult limits.
	 * 
	 * @see ISearch
	 */
	public int count(Session session, ISearch search) {
		if (search == null)
			return 0;
		return count(session, search.getSearchClass(), search);
	}
	
	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>ISearch</code> if there were no paging or maxResult limits.
	 * 
	 * @see ISearch
	 */
	public int count(Session session, Class<?> searchClass, ISearch search) {
		if (searchClass == null || search == null)
			return 0;

		List<Object> paramList = new ArrayList<Object>();
		String hql = generateRowCountQL(searchClass, search, paramList);
		if (hql == null) { //special case where the query uses column operators
			return 1;
		}
		Query query = session.createQuery(hql);
		addParams(query, paramList);

		return ((Long) query.uniqueResult()).intValue();
	}
	
	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Session session, ISearch search) {
		if (search == null)
			return null;
		return searchAndCount(session, search.getSearchClass(), search);
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 * 
	 * @see ISearch
	 */
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Session session, Class<?> searchClass, ISearch search) {
		if (searchClass == null || search == null)
			return null;

		SearchResult result = new SearchResult();
		result.setSearch(search);
		result.setFirstResult(search.getFirstResult());
		result.setPage(search.getPage());
		result.setMaxResults(search.getMaxResults());

		result.setResults(search(session, searchClass, search));

		if (search.getMaxResults() > 0) {
			result.setTotalCount(count(session, searchClass, search));
		} else {
			result.setTotalCount(result.getResults().size()
					+ SearchUtil.calcFirstResult(search));
		}

		return result;
	}
	
	/**
	 * Search for a single result using the given parameters.
	 */
	public Object searchUnique(Session session, ISearch search) throws NonUniqueResultException {
		if (search == null)
			return null;
		return searchUnique(session, search.getSearchClass(), search);
	}
	
	/**
	 * Search for a single result using the given parameters.
	 */
	public Object searchUnique(Session session, Class<?> entityClass, ISearch search) throws NonUniqueResultException {
		if (search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String hql = generateQL(entityClass, search, paramList);
		Query query = session.createQuery(hql);
		addParams(query, paramList);
		addResultMode(query, search);

		return query.uniqueResult();
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

	private void addPaging(Query query, ISearch search) {
		int firstResult = SearchUtil.calcFirstResult(search);
		if (firstResult > 0) {
			query.setFirstResult(firstResult);
		}
		if (search.getMaxResults() > 0) {
			query.setMaxResults(search.getMaxResults());
		}
	}

	private void addResultMode(Query query, ISearch search) {
		int resultMode = search.getResultMode();
		if (resultMode == ISearch.RESULT_AUTO) {
			int count = 0;
			Iterator<Field> fieldItr = search.getFields().iterator();
			while (fieldItr.hasNext()) {
				Field field = fieldItr.next();
				if (field.getKey() != null && !field.getKey().equals("")) {
					resultMode = ISearch.RESULT_MAP;
					break;
				}
				count++;
			}
			if (resultMode == ISearch.RESULT_AUTO) {
				if (count > 1)
					resultMode = ISearch.RESULT_ARRAY;
				else
					resultMode = ISearch.RESULT_SINGLE;
			}
		}
		
		switch (resultMode) {
		case ISearch.RESULT_ARRAY:
			query.setResultTransformer(ARRAY_RESULT_TRANSFORMER);
			break;
		case ISearch.RESULT_LIST:
			query.setResultTransformer(Transformers.TO_LIST);
			break;
		case ISearch.RESULT_MAP:
			List<String> keyList = new ArrayList<String>();
			Iterator<Field> fieldItr = search.getFields().iterator();
			while (fieldItr.hasNext()) {
				Field field = fieldItr.next();
				if (field.getKey() != null && !field.getKey().equals("")) {
					keyList.add(field.getKey());
				} else {
					keyList.add(field.getProperty());
				}
			}
			query.setResultTransformer(new MapResultTransformer(keyList
					.toArray(new String[0])));
			break;
		default: // ISearch.RESULT_SINGLE
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private static final ResultTransformer ARRAY_RESULT_TRANSFORMER = new ResultTransformer() {
		private static final long serialVersionUID = 1L;

		public List transformList(List collection) {
			return collection;
		}

		public Object transformTuple(Object[] tuple, String[] aliases) {
			return tuple;
		}
	};

	@SuppressWarnings("unchecked")
	private static class MapResultTransformer implements ResultTransformer {
		private static final long serialVersionUID = 1L;

		private String[] keys;

		public MapResultTransformer(String[] keys) {
			this.keys = keys;
		}

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
