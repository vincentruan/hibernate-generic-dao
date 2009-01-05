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

import com.trg.dao.AbstractSearchProcessor;
import com.trg.dao.search.Field;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

/**
 * Implementation of SearchToQLProcessor that generates HQL
 * @author dwolverton
 */
public class HibernateSearchProcessor extends AbstractSearchProcessor {
	
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
	 * <code>Search</code> object.
	 * 
	 * @see Search
	 */
	@SuppressWarnings("unchecked")
	public List search(Session session, Search search) {
		if (search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String hql = generateQL(search, paramList);
		Query query = session.createQuery(hql);
		addParams(query, paramList);
		addPaging(query, search);
		addResultMode(query, search);

		return query.list();
	}

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>Search</code> if there were no paging or maxResult limits.
	 * 
	 * @see Search
	 */
	public int count(Session session, Search search) {
		if (search == null)
			return 0;

		List<Object> paramList = new ArrayList<Object>();
		String hql = generateRowCountQL(search, paramList);
		Query query = session.createQuery(hql);
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
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Session session, Search search) {
		if (search == null)
			return null;

		SearchResult result = new SearchResult();
		result.setSearch(search);
		result.setFirstResult(search.getFirstResult());
		result.setPage(search.getPage());
		result.setMaxResults(search.getMaxResults());

		result.setResults(search(session, search));

		if (search.getMaxResults() > 0) {
			result.setTotalCount(count(session, search));
		} else {
			result.setTotalCount(result.getResults().size()
					+ search.calcFirstResult());
		}

		return result;
	}

	/**
	 * Search for a single result using the given parameters.
	 */
	public Object searchUnique(Session session, Search search) throws NonUniqueResultException {
		if (search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String hql = generateQL(search, paramList);
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

	private void addPaging(Query query, Search search) {
		if (search.calcFirstResult() > 0) {
			query.setFirstResult(search.calcFirstResult());
		}
		if (search.getMaxResults() > 0) {
			query.setMaxResults(search.getMaxResults());
		}
	}

	private void addResultMode(Query query, Search search) {
		int resultMode = search.getResultMode();
		if (resultMode == Search.RESULT_AUTO) {
			int count = 0;
			Iterator<Field> fieldItr = search.fieldIterator();
			while (fieldItr.hasNext()) {
				Field field = fieldItr.next();
				if (field.getKey() != null && !field.getKey().equals("")) {
					resultMode = Search.RESULT_MAP;
					break;
				}
				count++;
			}
			if (resultMode == Search.RESULT_AUTO) {
				if (count > 1)
					resultMode = Search.RESULT_ARRAY;
				else
					resultMode = Search.RESULT_SINGLE;
			}
		}
		
		switch (resultMode) {
		case Search.RESULT_ARRAY:
			query.setResultTransformer(ARRAY_RESULT_TRANSFORMER);
			break;
		case Search.RESULT_LIST:
			query.setResultTransformer(Transformers.TO_LIST);
			break;
		case Search.RESULT_MAP:
			List<String> keyList = new ArrayList<String>();
			Iterator<Field> fieldItr = search.fieldIterator();
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
		default: // Search.RESULT_SINGLE
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
