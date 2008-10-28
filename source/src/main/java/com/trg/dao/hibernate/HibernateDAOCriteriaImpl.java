package com.trg.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.trg.dao.Util;
import com.trg.search.Fetch;
import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.search.SearchResult;
import com.trg.search.Sort;

@SuppressWarnings("unchecked")
public class HibernateDAOCriteriaImpl extends HibernateDaoSupport {

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

		Criteria crit = buildCriteria(search, true, true, true, true);
		List list = crit.list();

		return list;
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

		Criteria crit;
		crit = buildCriteria(search, true, false, false, false);
		crit.setProjection(Projections.rowCount());
		return ((Integer) crit.uniqueResult()).intValue();
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

		Criteria crit;
		crit = buildCriteria(search, true, false, false, true);
		return crit.uniqueResult();
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

	// ---- Search helpers ----
	/**
	 * The <code>parsePath</code> method returns an array of length two. These
	 * two values BASE and PROPERTY represent the two indexes into that array.
	 * <code>result[BASE]</code> is the base path of the object with the final
	 * property, and <code>result[PROPERTY]</code> is the name of the property
	 * specified on that object.
	 * 
	 * @see HibernateDAOCriteriaImpl#parsePath(String)
	 */
	protected static final int BASE = 0, PROPERTY = 1;

	/**
	 * Key for root criteria in the critMap that is passed around when building
	 * a criteria from a Search. The root criteria is the Criteria object that
	 * is actually used in the hibernate query.
	 */
	protected static final String ROOT_CRIT = "";

	/**
	 * Build a hibernate <code>Criteria</code> using the specified features of
	 * the given <code>Search</code>. The <code>Criteria</code> that is
	 * returned is ready to be executed.
	 */
	protected Criteria buildCriteria(Search search, boolean doFiltering,
			boolean doPaging, boolean doOrdering, boolean doFetching) {
		Map<String, Criteria> critMap = startCriteria(search);

		// if related collections are fetched eagerly (by join), this
		// DISTINCT_ROOT_ENTITY result transformer is needed to make sure each
		// result only shows up once. Note that when any of the fetch mode
		// options other than FETCH_ENTITY is used, this technique does not
		// work.
		critMap.get(ROOT_CRIT).setResultTransformer(
				Criteria.DISTINCT_ROOT_ENTITY);

		if (doFiltering)
			addFilters(critMap, search);
		if (doPaging)
			addPaging(critMap, search);
		if (doOrdering)
			addOrdering(critMap, search);
		if (doFetching)
			addFetching(critMap, search);

		return critMap.get(ROOT_CRIT);
	}

	/**
	 * As we build a Criteria we keep track of the tree of Criteria that are
	 * needed for all the nested properties (if any). This method initializes
	 * the map data structure that is used to hold that tree.
	 */
	protected Map<String, Criteria> startCriteria(Search search) {
		Map<String, Criteria> critMap = new HashMap<String, Criteria>();
		critMap.put(ROOT_CRIT, getSession().createCriteria(
				search.getSearchClass()));
		return critMap;
	}

	/**
	 * Apply the paging options (maxResults, page, firstResult) from the search
	 * to the Criteria.
	 */
	protected void addPaging(Map<String, Criteria> critMap, Search search) {
		addPaging(critMap.get(ROOT_CRIT), search);
	}

	/**
	 * This method adds the page and max results options to the criteria if
	 * specified by the search. It also deals with a tricky issue with database
	 * paging when fetching collections eagerly. Eager collection fetching is
	 * accomplished by joining the tables and having a row in the result set for
	 * each collection item. This messes with the paging options. So this method
	 * checks if paging is being used on an entity that has eagerly fetched
	 * collection properties. If it does it forces those to load by separate
	 * selects in this case.
	 * 
	 * @param crit
	 * @param search
	 */
	protected void addPaging(Criteria crit, Search search) {
		crit.setFirstResult(search.calcFirstResult());
		if (search.getMaxResults() > 0)
			crit.setMaxResults(search.getMaxResults());

		// The rest of this code deals with the eager collection fetching paging
		// conflict
		if (search.calcFirstResult() > 0 || search.getMaxResults() > 0) {
			initEagerCollections();
			// if any eager collections apply to the searchClass, set
			// FetchMode.SELECT for those
			// properties.
			List<String> myCollections = eagerCollections.get(search
					.getSearchClass().getName());
			if (myCollections != null) {
				for (String property : myCollections) {
					crit.setFetchMode(property, FetchMode.SELECT);
				}
			}
		}
	}

	/**
	 * Used in addPaging();
	 * 
	 * @see HibernateDAOCriteriaImpl#addPaging(Criteria, Search)
	 * @see HibernateDAOCriteriaImpl#initEagerCollections()
	 */
	protected static Map<String, List<String>> eagerCollections;

	/**
	 * This method initializes <code>eagerCollections</code>.
	 * <code>eagerCollections</code> should contain all collections that are
	 * to be loaded eagerly. Before we use it, we need to fill it using this
	 * method.
	 */
	protected void initEagerCollections() {
		if (eagerCollections == null) {
			// eagerCollections should contain all collections that are to
			// be loaded eagerly.
			// the first time we use it we need to fill it.
			eagerCollections = new HashMap<String, List<String>>();
			for (Object o : getSessionFactory().getAllCollectionMetadata()
					.entrySet()) {
				Map.Entry entry = (Map.Entry) o;
				if (!((CollectionMetadata) entry.getValue()).isLazy()) {
					String key = (String) entry.getKey();
					int pos = key.lastIndexOf('.');
					List<String> list = eagerCollections.get(key.substring(0,
							pos));
					if (list == null) {
						list = new ArrayList<String>(1);
						eagerCollections.put(key.substring(0, pos), list);
					}
					list.add(key.substring(pos + 1));
				}
			}
		}
	}

	/**
	 * Apply the ordering options (sorts) from the search to the Criteria.
	 */
	protected void addOrdering(Map<String, Criteria> critMap, Search search) {
		Iterator<Sort> sorts = search.sortIterator();
		while (sorts.hasNext()) {
			Sort sort = sorts.next();
			if (sort == null || sort.property == null
					|| "".equals(sort.property))
				continue;
			String[] parsed = parsePath(sort.property);
			Criteria crit = getCriteria(critMap, parsed[BASE]);

			if (sort.desc)
				crit.addOrder(Order.desc(parsed[PROPERTY]));
			else
				crit.addOrder(Order.asc(parsed[PROPERTY]));
		}
	}

	/**
	 * Apply the fetching options (fetches, fetchMode) from the search to the
	 * Criteria.
	 */
	protected void addFetching(Map<String, Criteria> critMap, Search search) {
		Iterator<Fetch> fetchItr = search.fetchIterator();
		boolean usesColumnOperators = false;
		boolean notUsesColumnOperators = false;
		while (fetchItr.hasNext()) {
			if (fetchItr.next().operator != Fetch.OP_PROPERTY) {
				usesColumnOperators = true;
				if (notUsesColumnOperators)
					throw new Error(
							"A search can not have a mix of fetches with operators and fetches without operators.");
			} else {
				notUsesColumnOperators = true;
				if (usesColumnOperators)
					throw new Error(
							"A search can not have a mix of fetches with operators and fetches without operators.");
			}
		}

		// FetchMode Entity
		if (search.getFetchMode() == Search.FETCH_ENTITY) {
			if (usesColumnOperators) {
				throw new Error(
						"A search with fetch mode FETCH_ENTITY cannot have fetches with operators. Change the fetch mode.");
			}
			fetchItr = search.fetchIterator();
			while (fetchItr.hasNext()) {
				String property = fetchItr.next().property;
				if (property == null || "".equals(property))
					continue;
				String[] parsed = parsePath(property);
				Criteria crit = getCriteria(critMap, parsed[BASE]);

				crit.setFetchMode(parsed[PROPERTY], FetchMode.JOIN);
			}

			// FetchMode Array, List, Map, Single
		} else {
			ProjectionList projectionList = Projections.projectionList();
			fetchItr = search.fetchIterator();
			int i = -1;
			while (fetchItr.hasNext()) {
				i++;
				Fetch select = fetchItr.next();
				if (select == null || select.property == null
						|| "".equals(select.property))
					continue;

				String[] parsed = parsePath(select.property);
				String path = select.property;
				if (!"".equals(parsed[BASE])) {
					path = getCriteria(critMap, parsed[BASE]).getAlias() + "."
							+ parsed[PROPERTY];
				}

				Projection projection;
				switch (select.operator) {
				case Fetch.OP_AVG:
					projection = Projections.avg(path);
					break;
				case Fetch.OP_COUNT:
					projection = Projections.count(path);
					break;
				case Fetch.OP_COUNT_DISTINCT:
					projection = Projections.countDistinct(path);
					break;
				case Fetch.OP_MAX:
					projection = Projections.max(path);
					break;
				case Fetch.OP_MIN:
					projection = Projections.min(path);
					break;
				case Fetch.OP_SUM:
					projection = Projections.sum(path);
					break;
				default:
					projection = Projections.property(path);
					break;
				}

				if (search.getFetchMode() == Search.FETCH_MAP) {
					String alias = select.key;
					if (alias == null | "".equals(alias))
						alias = select.property;
					alias = ALIAS_PREFIX + alias;

					projectionList.add(projection, alias);
				} else {
					projectionList.add(projection);
				}

				// with FETCH_SINGLE, only one fetch is allowed
				if (search.getFetchMode() == Search.FETCH_SINGLE)
					break;
			}
			critMap.get(ROOT_CRIT).setProjection(projectionList);
			if (search.getFetchMode() == Search.FETCH_MAP) {
				critMap.get(ROOT_CRIT).setResultTransformer(
						new AliasToMapResultTransformer());
			} else if (search.getFetchMode() == Search.FETCH_LIST) {
				critMap.get(ROOT_CRIT).setResultTransformer(
						Transformers.TO_LIST);
			} else if (search.getFetchMode() == Search.FETCH_ARRAY) {
				critMap.get(ROOT_CRIT).setResultTransformer(
						TO_ARRAY_RESULT_TRANSFORMER);
			} // else if FETCH_SINGLE use default result transformer
		}
	}

	/**
	 * This is a ResultTransformer is used for fetchMode == Search.FETCH_ARRAY.
	 * It transforms each result into an ordered array.
	 */
	protected static final ResultTransformer TO_ARRAY_RESULT_TRANSFORMER = new ResultTransformer() {
		private static final long serialVersionUID = 1L;

		public List transformList(List collection) {
			return collection;
		}

		public Object transformTuple(Object[] tuple, String[] aliases) {
			return tuple;
		}
	};

	/**
	 * @see HibernateDAOCriteriaImpl.AliasToMapResultTransformer
	 */
	protected static final String ALIAS_PREFIX = "@@";

	/**
	 * <p>
	 * There is an error with Hibernate (using a MySQL dialect).
	 * 
	 * <p>
	 * The problem occurs when using fetch mode FETCH_MAP when one or more of
	 * the aliases (a.k.a. keys) specified for the result map meets both of the
	 * following criteria:
	 * <ol>
	 * <li> The alias is exactly equal to a property that is used for filtering.
	 * <li> That property has no "."s in it. (i.e. it is a direct property of
	 * the base search type.)
	 * </ol>
	 * <p>
	 * To keep this from ever happening we simply add <code>ALIAS_PREFIX</code>
	 * to the beginning of all aliases when using fetch mode FETCH_MAP. Then in
	 * the result transformer (<code>AliasToMapResultTransformer</code>) we
	 * take the prefix off before passing the results back.
	 */
	protected static class AliasToMapResultTransformer implements
			ResultTransformer {
		private static final long serialVersionUID = 1L;

		private String[] correctedAliases;

		public List transformList(List collection) {
			return collection;
		}

		public Object transformTuple(Object[] tuple, String[] aliases) {
			if (correctedAliases == null) {
				correctedAliases = new String[aliases.length];
				for (int i = 0; i < aliases.length; i++) {
					String alias = aliases[i];
					if (alias != null && !"".equals(alias)) {
						correctedAliases[i] = alias.substring(ALIAS_PREFIX
								.length());
					}
				}
			}

			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < correctedAliases.length; i++) {
				String key = correctedAliases[i];
				if (key != null) {
					map.put(key, tuple[i]);
				}
			}

			return map;
		}
	}

	/**
	 * Apply the filtering options (filters, disjunction) from the search to the
	 * Criteria.
	 */
	protected void addFilters(Map<String, Criteria> critMap, Search search) {
		Junction junction = search.isDisjunction() ? Restrictions.disjunction()
				: Restrictions.conjunction();

		Iterator<Filter> filters = search.filterIterator();
		while (filters.hasNext()) {
			Filter filter = filters.next();
			Criterion criterion = getCriterionFromFilter(critMap, filter,
					search);
			if (criterion != null)
				junction.add(criterion);
		}
		critMap.get(ROOT_CRIT).add(junction);
	}

	/**
	 * Generate a single Criterion from a single Filter.
	 */
	protected Criterion getCriterionFromFilter(Map<String, Criteria> critMap,
			Filter filter, Search search) {
		if (filter == null
				|| filter.property == null
				|| "".equals(filter.property)
				|| (filter.value == null && filter.operator != Filter.OP_EQUAL && filter.operator != Filter.OP_NOT_EQUAL))
			return null;
		String[] parsed = parsePath(filter.property);
		String aliasedPath;
		if ("".equals(parsed[BASE])) {
			aliasedPath = parsed[PROPERTY];
		} else {
			aliasedPath = getCriteria(critMap, parsed[BASE]).getAlias() + "."
					+ parsed[PROPERTY];
		}

		// convert numbers to the expected type if needed (ex: Integer to Long)
		Object value = filter.value;
		if (filter.operator == Filter.OP_IN
				|| filter.operator == Filter.OP_NOT_IN) {
			// with IN & NOT IN, check each element in the collection.
			Class<?> expectedClass = Util.getExpectedClass(search
					.getSearchClass(), filter.property);

			Object[] val2;

			if (value instanceof Collection) {
				val2 = new Object[((Collection) value).size()];
				int i = 0;
				for (Object item : (Collection) value) {
					val2[i++] = Util.convertIfNeeded(item, expectedClass);
				}
			} else {
				val2 = new Object[((Object[]) value).length];
				int i = 0;
				for (Object item : (Object[]) value) {
					val2[i++] = Util.convertIfNeeded(item, expectedClass);
				}
			}
			value = val2;
		} else if (filter.operator != Filter.OP_AND
				&& filter.operator != Filter.OP_OR
				&& filter.operator != Filter.OP_NOT) {
			value = Util.convertIfNeeded(value, Util.getExpectedClass(search
					.getSearchClass(), filter.property));
		}

		switch (filter.operator) {
		case Filter.OP_IN:
			if (value instanceof Collection)
				return Restrictions.in(aliasedPath, (Collection) value);
			else
				return Restrictions.in(aliasedPath, (Object[]) value);
		case Filter.OP_NOT_IN:
			if (value instanceof Collection)
				return Restrictions.not(Restrictions.in(aliasedPath,
						(Collection) value));
			else
				return Restrictions.not(Restrictions.in(aliasedPath,
						(Object[]) value));
		case Filter.OP_EQUAL:
			if (value == null) {
				return Restrictions.isNull(aliasedPath);
			} else {
				return Restrictions.eq(aliasedPath, value);
			}
		case Filter.OP_NOT_EQUAL:
			if (value == null) {
				return Restrictions.isNotNull(aliasedPath);
			} else {
				return Restrictions.ne(aliasedPath, value);
			}
		case Filter.OP_GREATER_THAN:
			return Restrictions.gt(aliasedPath, value);
		case Filter.OP_LESS_THAN:
			return Restrictions.lt(aliasedPath, value);
		case Filter.OP_GREATER_OR_EQUAL:
			return Restrictions.ge(aliasedPath, value);
		case Filter.OP_LESS_OR_EQUAL:
			return Restrictions.le(aliasedPath, value);
		case Filter.OP_LIKE:
			return Restrictions.like(aliasedPath, value);
		case Filter.OP_AND:
		case Filter.OP_OR:
			if (!(value instanceof List)) {
				return null;
			}
			Junction junction = filter.operator == Filter.OP_AND ? Restrictions
					.conjunction() : Restrictions.disjunction();
			for (Object o : ((List) value)) {
				if (o instanceof Filter) {
					Criterion criterion = getCriterionFromFilter(critMap,
							(Filter) o, search);
					if (criterion != null)
						junction.add(criterion);
				}
			}
			return junction;
		case Filter.OP_NOT:
			if (!(value instanceof Filter)) {
				return null;
			}
			Criterion criterion = getCriterionFromFilter(critMap,
					(Filter) value, search);
			if (criterion == null)
				return null;
			return Restrictions.not(criterion);
		default:
			throw new IllegalArgumentException("Filter comparison ( "
					+ filter.operator + " ) is invalid.");
		}
	}

	/**
	 * <p>
	 * This method is the core of processing nested properties. It takes a path
	 * such as "firstKitten" or "parent.firstKitten" and returns the criteria or
	 * sub criteria based at that path. Once we have that base criteria we can
	 * add any of our specifications to it.
	 * <p>
	 * In order to get a sub criteria the method recursively adds criteria
	 * starting from the root. Once a sub criteria is created it is stored in
	 * the map with a key equal to its path. Subsequent references to the same
	 * path will retrieve the criteria from there.
	 * 
	 * @param path
	 *            This is the path to the base of the criteria. For example if
	 *            we are adding a filter for the property "city.name", we need
	 *            to get the criteria based at "city" and add a restriction for
	 *            "name" to it. So <code>getCriteria(critMap, "city")</code>
	 *            would be used.
	 */
	protected Criteria getCriteria(Map<String, Criteria> map, String path) {
		Criteria crit = map.get(path);
		if (crit != null) {
			return crit;
		} else {
			String[] parsed = parsePath(path);
			crit = getCriteria(map, parsed[BASE]).createCriteria(
					parsed[PROPERTY], generateAlias(path),
					CriteriaSpecification.LEFT_JOIN);
			map.put(path, crit);
			return crit;
		}
	}

	/**
	 * This is a helper function used in processing a Search. Parse a nested
	 * property path into the base path and property. For example passing in
	 * "Cat.firstKitten.name" would return the String array ["Cat.firstKitten",
	 * "name"]. Also passing simply "name" would return ["", "name"].
	 */
	protected String[] parsePath(String path) {
		int pos = path.lastIndexOf('.');
		if (pos == -1) {
			return new String[] { "", path };
		} else {
			return new String[] { path.substring(0, pos),
					path.substring(pos + 1) };
		}
	}

	/**
	 * We give each nested Criteria an alias. The alias is a function of the
	 * path to the criteria so that all aliases are unique.
	 */
	protected String generateAlias(String path) {
		return "/" + path.replace('.', '/');
	}

}
