package com.trg.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trg.search.Fetch;
import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.search.Sort;

/**
 * This class provides two methods for generating query language to fulfill a
 * <code>Search</code>.
 * <ol>
 * <li><code>generateQL()</code> - is used for getting the actual search
 * results.</li>
 * <li><code>generateRowCountQL()</code> - is used for getting just the
 * number of results.</li>
 * </ol>
 * Both methods return a query language sting and a list of values for filling
 * named parameters. For example the following query and parameter list might be
 * returned:
 * 
 * <pre>
 * select _it from com.example.Cat _it
 *   where _it.age &gt; :p1 and _it.name != :p2
 *   
 * parameter list: [3, 'Mittens']
 * </pre>
 * 
 * This is an abstract class. A subclass must be used to implement individual
 * query languages. Currently only HQL query language is supported (<code>com.trg.dao.hibernate.HibernateSearchToQLProcessor</code>).
 * The that implementation could be used for EQL query language as well with no
 * or minor modifications.
 */
public abstract class AbstractSearchProcessor {

	private static Logger logger = LoggerFactory.getLogger(AbstractSearchProcessor.class);
	
	protected static int QLTYPE_HQL = 0;
	protected static int QLTYPE_EQL = 1;

	protected int qlType;
	
	protected MetaDataUtil metaDataUtil;

	protected String rootAlias = "_it";

	protected AbstractSearchProcessor(int qlType, MetaDataUtil metaDataUtil) {
		this.qlType = qlType;
		this.metaDataUtil = metaDataUtil;
	}

	/**
	 * This is the string used to represent the root entity of the search within
	 * the query. The default value is <code>"_it"</code>. It may be
	 * necessary to use a different alias if there are entities in the data
	 * model with the name or property "_it".
	 */
	public void setRootAlias(String alias) {
		this.rootAlias = alias;
	}

	/**
	 * Generate the QL string for a given search. Fill paramList with the values
	 * to be used for the query. All parameters within the query string are
	 * specified as named parameters ":pX", where X is the index of the
	 * parameter value in paramList.
	 */
	public String generateQL(Search search, List<Object> paramList) {
		securityCheck(search);

		Map<String, String> aliases = new HashMap<String, String>();

		String select = generateSelectClause(search, aliases);
		String where = generateWhereClause(search, paramList, aliases);
		String orderBy = generateOrderByClause(search, aliases);
		String from = generateFromClause(search, aliases);

		StringBuilder sb = new StringBuilder();
		sb.append(select);
		sb.append(" ");
		sb.append(from);
		sb.append(" ");
		sb.append(where);
		sb.append(" ");
		sb.append(orderBy);

		String query = sb.toString();
		if (logger.isDebugEnabled()) logger.debug("generateQL:\n  " + query);
		return query;
	}

	/**
	 * Generate the QL string that will query the total number of results from a
	 * given search (paging is ignored). Fill paramList with the values to be
	 * used for the query. All parameters within the query string are specified
	 * as named parameters ":pX", where X is the index of the parameter value in
	 * paramList.
	 */
	public String generateRowCountQL(Search search, List<Object> paramList) {
		securityCheck(search);

		Map<String, String> aliases = new HashMap<String, String>();

		String where = generateWhereClause(search, paramList, aliases);
		String from = generateFromClause(search, aliases);

		StringBuilder sb = new StringBuilder();
		sb.append("select count(distinct ");
		sb.append(rootAlias);
		sb.append(".id) ");
		sb.append(from);
		sb.append(" ");
		sb.append(where);

		String query = sb.toString();
		if (logger.isDebugEnabled()) logger.debug("generateRowCountQL:\n  " + query);
		return query;
	}

	/**
	 * Internal method for generating the select clause based on the fetches of
	 * the given search (if <code>fetchMode != FETCH_ENTITY</code>)
	 */
	protected String generateSelectClause(Search search,
			Map<String, String> aliases) {
		Iterator<Fetch> fetchItr = search.fetchIterator();
		if (search.getFetchMode() == Search.FETCH_ENTITY) {
			while (fetchItr.hasNext()) {
				if (fetchItr.next().operator != Fetch.OP_PROPERTY) {
					throw new Error(
							"A search with fetch mode FETCH_ENTITY cannot have fetches with operators. Change the fetch mode.");
				}
			}
			return "select " + rootAlias;
		} else {
			StringBuilder sb = null;
			boolean useOperator = false, notUseOperator = false;

			boolean first = true;
			while (fetchItr.hasNext()) {
				Fetch fetch = fetchItr.next();
				if (first) {
					sb = new StringBuilder("select ");
					first = false;
				} else {
					sb.append(", ");
				}

				String prop;
				if (fetch.property == null || "".equals(fetch.property)) {
					prop = "*";
				} else {
					prop = getPath(aliases, fetch.property);
				}

				switch (fetch.operator) {
				case Fetch.OP_AVG:
					sb.append("avg(");
					useOperator = true;
					break;
				case Fetch.OP_COUNT:
					sb.append("count(");
					useOperator = true;
					break;
				case Fetch.OP_COUNT_DISTINCT:
					sb.append("count(distinct ");
					useOperator = true;
					break;
				case Fetch.OP_MAX:
					sb.append("max(");
					useOperator = true;
					break;
				case Fetch.OP_MIN:
					sb.append("min(");
					useOperator = true;
					break;
				case Fetch.OP_SUM:
					sb.append("sum(");
					useOperator = true;
					break;
				default:
					notUseOperator = true;
					break;
				}
				sb.append(prop);
				if (useOperator) {
					sb.append(")");
				}
			}
			if (first) {
				throw new Error(
						"Search has no fetch entries, yet fetch mode is not FETCH_ENTITY. This is not valid.");
			}
			if (useOperator && notUseOperator) {
				throw new Error(
						"A search can not have a mix of fetches with operators and fetches without operators.");
			}
			return sb.toString();
		}
	}

	/**
	 * Internal method for generating from clause. This method should be called
	 * after generating other clauses because it relies on the aliases they
	 * create. This method takes every path that is called for in the other
	 * clauses and makes it available as an alias using left joins. It also adds
	 * entities that should be fetched with
	 * <code>fetchMode == FETCH_ENTITY</code>.
	 */
	protected String generateFromClause(Search search,
			Map<String, String> aliases) {
		Map<String, Object> fetchPaths = new HashMap<String, Object>();

		if (search.getFetchMode() == Search.FETCH_ENTITY) {
			// Mark all the paths that should be fetched eagerly by adding them
			// to fetchPaths
			Iterator<Fetch> fetchItr = search.fetchIterator();
			while (fetchItr.hasNext()) {
				Fetch fetch = fetchItr.next();
				getAlias(aliases, fetch.property);
				fetchPaths.put(fetch.property, null);
				int pos = fetch.property.lastIndexOf('.');
				while (pos > -1) {
					fetchPaths.put(fetch.property.substring(0, pos), null);
					pos = fetch.property.lastIndexOf('.', pos - 1);
				}
			}
		}

		StringBuilder sb = new StringBuilder("from ");
		sb.append(search.getSearchClass().getName());
		sb.append(" ");
		sb.append(rootAlias);

		Map<String, String> usedAliases = new HashMap<String, String>();
		int aliasCount = aliases.size();

		for (Map.Entry<String, String> entry : aliases.entrySet()) {
			String wholePath = entry.getKey();
			String alias = rootAlias;
			int lastPos = -1;
			int pos = wholePath.indexOf('.');
			while (pos != -1) {
				String currentPath = wholePath.substring(0, pos);
				if (!usedAliases.containsKey(currentPath)) {
					String prop = wholePath.substring(lastPos + 1, pos);
					sb.append(" left join ");
					if (fetchPaths.containsKey(currentPath))
						sb.append("fetch ");
					sb.append(alias);
					sb.append(".");
					sb.append(prop);
					sb.append(" as ");
					if (aliases.containsKey(currentPath)) {
						alias = aliases.get(currentPath);
					} else {
						alias = "a" + Integer.toString(++aliasCount) + "_"
								+ prop;
					}
					sb.append(alias);
				}
				lastPos = pos;
				pos = wholePath.indexOf('.', pos + 1);
			}

			if (!usedAliases.containsKey(wholePath)) {
				String prop = wholePath.substring(lastPos + 1);
				sb.append(" left join ");
				if (fetchPaths.containsKey(wholePath))
					sb.append("fetch ");
				sb.append(alias);
				sb.append(".");
				sb.append(prop);
				sb.append(" as ");
				alias = entry.getValue();
				sb.append(alias);
			}
		}

		return sb.toString();
	}

	/**
	 * Internal method for generating order by clause. Uses sort options from
	 * search.
	 */
	protected String generateOrderByClause(Search search,
			Map<String, String> aliases) {
		Iterator<Sort> sortItr = search.sortIterator();
		StringBuilder sb = null;
		boolean first = true;
		while (sortItr.hasNext()) {
			Sort sort = sortItr.next();
			if (first) {
				sb = new StringBuilder("order by ");
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(getPath(aliases, sort.property));
			sb.append(sort.desc ? " desc" : " asc");
		}
		if (first) {
			return "";
		}
		return sb.toString();
	}

	/**
	 * Internal method for generating where clause for given search. Uses filter
	 * options from search.
	 */
	protected String generateWhereClause(Search search, List<Object> params,
			Map<String, String> aliases) {
		List<Filter> filters = new ArrayList<Filter>();
		Iterator<Filter> filterItr = search.filterIterator();
		while (filterItr.hasNext()) {
			filters.add(filterItr.next());
		}
		if (filters.size() == 0) {
			return "";
		} else if (filters.size() == 1) {
			return "where "
					+ filterToString(search, filters.get(0), params, aliases);
		} else {
			Filter junction = new Filter(null, filters,
					search.isDisjunction() ? Filter.OP_OR : Filter.OP_AND);
			return "where " + filterToString(search, junction, params, aliases);
		}
	}

	/**
	 * Add value to paramList and return the "X" part of the named parameter
	 * string ":pX".
	 */
	protected String param(List<Object> params, Object value) {
		params.add(value);
		return Integer.toString(params.size());
	}

	/**
	 * Recursively generate the QL fragment for a given search filter option.
	 */
	@SuppressWarnings("unchecked")
	protected String filterToString(Search search, Filter filter,
			List<Object> params, Map<String, String> aliases) {
		Object value = filter.value;

		// convert numbers to the expected type if needed (ex: Integer to Long)
		if (filter.operator == Filter.OP_IN
				|| filter.operator == Filter.OP_NOT_IN) {
			// with IN & NOT IN, check each element in the collection.
			Class<?> expectedClass = metaDataUtil.getExpectedClass(search
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
			value = Util.convertIfNeeded(value, metaDataUtil.getExpectedClass(search
					.getSearchClass(), filter.property));
		}

		switch (filter.operator) {
		case Filter.OP_IN:
			return getPath(aliases, filter.property) + " in (:p"
					+ param(params, value) + ")";
		case Filter.OP_NOT_IN:
			return getPath(aliases, filter.property) + " not in (:p"
					+ param(params, value) + ")";
		case Filter.OP_EQUAL:
			if (value == null) {
				return getPath(aliases, filter.property) + " is null";
			} else {
				return getPath(aliases, filter.property) + " = :p"
						+ param(params, value);
			}
		case Filter.OP_NOT_EQUAL:
			if (value == null) {
				return getPath(aliases, filter.property) + " is not null";
			} else {
				return getPath(aliases, filter.property) + " != :p"
						+ param(params, value);
			}
		case Filter.OP_GREATER_THAN:
			return getPath(aliases, filter.property) + " > :p"
					+ param(params, value);
		case Filter.OP_LESS_THAN:
			return getPath(aliases, filter.property) + " < :p"
					+ param(params, value);
		case Filter.OP_GREATER_OR_EQUAL:
			return getPath(aliases, filter.property) + " >= :p"
					+ param(params, value);
		case Filter.OP_LESS_OR_EQUAL:
			return getPath(aliases, filter.property) + " <= :p"
					+ param(params, value);
		case Filter.OP_LIKE:
			return getPath(aliases, filter.property) + " like :p"
					+ param(params, value);
		case Filter.OP_AND:
		case Filter.OP_OR:
			if (!(value instanceof List)) {
				return null;
			}

			String op = filter.operator == Filter.OP_AND ? " and " : " or ";

			StringBuilder sb = new StringBuilder("(");
			boolean first = true;
			for (Object o : ((List) value)) {
				if (o instanceof Filter) {
					String filterStr = filterToString(search, (Filter) o,
							params, aliases);
					if (filterStr != null) {
						if (first) {
							first = false;
						} else {
							sb.append(op);
						}
						sb.append(filterStr);
					}
				}
			}
			if (first)
				return null;

			sb.append(")");
			return sb.toString();
		case Filter.OP_NOT:
			if (!(value instanceof Filter)) {
				return null;
			}
			String filterStr = filterToString(search, (Filter) value, params,
					aliases);
			if (filterStr == null)
				return null;

			return "not " + filterStr;
		default:
			throw new IllegalArgumentException("Filter comparison ( "
					+ filter.operator + " ) is invalid.");
		}
	}

	/**
	 * Given a full path to a property (ex. department.manager.salary), return
	 * the reference to that property that uses the appropriate alias (ex.
	 * a4_manager.salary).
	 */
	protected String getPath(Map<String, String> aliases, String property) {
		int pos = property.lastIndexOf('.');
		if (pos == -1) {
			return rootAlias + "." + property;
		} else {
			String aliasPath = property.substring(0, pos);
			return getAlias(aliases, aliasPath) + "."
					+ property.substring(pos + 1);
		}
	}

	/**
	 * Given a full path to an entity (ex. department.manager), return the alias
	 * to reference that entity (ex. a4_manager). If there is no alias for the
	 * given path, one will be created.
	 */
	protected String getAlias(Map<String, String> aliases, String path) {
		if (aliases.containsKey(path)) {
			return aliases.get(path);
		} else {
			int pos = path.lastIndexOf('.');
			String alias = "a" + Integer.toString(aliases.size() + 1) + "_"
					+ path.substring(pos + 1);
			aliases.put(path, alias);
			return alias;
		}
	}

	// ---- SECURITY CHECK ---- //

	/**
	 * Regex pattern for a valid property name/path.
	 */
	protected Pattern INJECTION_CHECK = Pattern.compile("^[\\w\\.]+$");

	/**
	 * Checks to make sure the search will not violate security.<br/><br/>
	 * 
	 * Current checks:
	 * <ul>
	 * <li>INJECTION - We check each property sepcified in the search to make
	 * sure it only contains valid Java identifier characters</li>
	 * </ul>
	 */
	protected void securityCheck(Search search) {
		Iterator<Fetch> fetchItr = search.fetchIterator();
		while (fetchItr.hasNext()) {
			securityCheckProperty(fetchItr.next().property);
		}

		Iterator<Sort> sortItr = search.sortIterator();
		while (sortItr.hasNext()) {
			securityCheckProperty(sortItr.next().property);
		}

		Iterator<Filter> filterItr = search.filterIterator();
		while (filterItr.hasNext()) {
			securityCheckFilter(filterItr.next());
		}
	}

	/**
	 * Used by <code>securityCheck()</code> to recursively check filters.
	 */
	@SuppressWarnings("unchecked")
	protected void securityCheckFilter(Filter filter) {
		if (filter.operator == Filter.OP_AND || filter.operator == Filter.OP_OR) {
			if (filter.value instanceof List) {
				for (Filter f : (List<Filter>) filter.value) {
					securityCheckFilter(f);
				}
			}
		} else if (filter.operator == Filter.OP_NOT) {
			if (filter.value instanceof Filter) {
				securityCheckFilter((Filter) filter.value);
			}
		} else {
			securityCheckProperty(filter.property);
		}
	}

	/**
	 * Used by <code>securityCheck()</code> to check a property string for
	 * injection attack.
	 */
	protected void securityCheckProperty(String property) {
		if (!INJECTION_CHECK.matcher(property).matches())
			throw new IllegalArgumentException(
					"A property used in a Search may only contain word characters (alphabetic, numberic and underscore \"_\") and dot \".\" seperators. This constraint was violated: "
							+ property);
	}
}
