package com.trg.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trg.dao.search.Field;
import com.trg.dao.search.Filter;
import com.trg.dao.search.ISearch;
import com.trg.dao.search.Sort;

/**
 * This class provides two methods for generating query language to fulfill a
 * <code>ISearch</code>.
 * <ol>
 * <li><code>generateQL()</code> - is used for getting the actual search
 * results.</li>
 * <li><code>generateRowCountQL()</code> - is used for getting just the number
 * of results.</li>
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
 * query languages. Currently only HQL query language is supported (
 * <code>com.trg.dao.hibernate.HibernateSearchToQLProcessor</code>). The that
 * implementation could be used for EQL query language as well with no or minor
 * modifications.
 */
public abstract class BaseSearchProcessor {

	private static Logger logger = LoggerFactory.getLogger(BaseSearchProcessor.class);

	protected static int QLTYPE_HQL = 0;
	protected static int QLTYPE_EQL = 1;

	protected int qlType;

	protected MetaDataUtil metaDataUtil;

	protected String rootAlias = "_it";

	protected static final String ROOT_PATH = "";

	protected BaseSearchProcessor(int qlType, MetaDataUtil metaDataUtil) {
		this.qlType = qlType;
		this.metaDataUtil = metaDataUtil;
	}

	/**
	 * This is the string used to represent the root entity of the search within
	 * the query. The default value is <code>"_it"</code>. It may be necessary
	 * to use a different alias if there are entities in the data model with the
	 * name or property "_it".
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
	public String generateQL(Class<?> entityClass, ISearch search, List<Object> paramList) {
		if (entityClass == null)
			throw new NullPointerException("The entity class for a search cannot be null");

		securityCheck(entityClass, search);

		Map<String, AliasNode> aliases = new HashMap<String, AliasNode>();
		aliases.put(ROOT_PATH, new AliasNode(ROOT_PATH, rootAlias));

		String select = generateSelectClause(entityClass, search, aliases);
		String where = generateWhereClause(entityClass, search, paramList, aliases);
		String orderBy = generateOrderByClause(entityClass, search, aliases);
		String from = generateFromClause(entityClass, search, aliases, true);

		StringBuilder sb = new StringBuilder();
		sb.append(select);
		sb.append(" ");
		sb.append(from);
		sb.append(" ");
		sb.append(where);
		sb.append(" ");
		sb.append(orderBy);

		String query = sb.toString();
		if (logger.isDebugEnabled())
			logger.debug("generateQL:\n  " + query);
		return query;
	}

	/**
	 * Generate the QL string that will query the total number of results from a
	 * given search (paging is ignored). Fill paramList with the values to be
	 * used for the query. All parameters within the query string are specified
	 * as named parameters ":pX", where X is the index of the parameter value in
	 * paramList.
	 */
	public String generateRowCountQL(Class<?> entityClass, ISearch search, List<Object> paramList) {
		if (entityClass == null)
			throw new NullPointerException("The entity class for a search cannot be null");

		securityCheck(entityClass, search);

		Map<String, AliasNode> aliases = new HashMap<String, AliasNode>();
		aliases.put(ROOT_PATH, new AliasNode(ROOT_PATH, rootAlias));

		String where = generateWhereClause(entityClass, search, paramList, aliases);
		String from = generateFromClause(entityClass, search, aliases, false);

		StringBuilder sb = new StringBuilder();
		sb.append("select count(distinct ").append(rootAlias).append(".id) ");
		sb.append(from);
		sb.append(" ");
		sb.append(where);

		String query = sb.toString();
		if (logger.isDebugEnabled())
			logger.debug("generateRowCountQL:\n  " + query);
		return query;
	}

	/**
	 * Internal method for generating the select clause based on the fields of
	 * the given search.
	 */
	protected String generateSelectClause(Class<?> entityClass, ISearch search, Map<String, AliasNode> aliases) {

		StringBuilder sb = null;
		boolean useOperator = false, notUseOperator = false;
		boolean first = true;

		if (search.getFields() != null) {
			for (Field field : search.getFields()) {
				if (first) {
					sb = new StringBuilder("select ");
					first = false;
				} else {
					sb.append(", ");
				}

				String prop;
				if (field.getProperty() == null || "".equals(field.getProperty())) {
					prop = "*";
				} else {
					prop = getPath(entityClass, aliases, field.getProperty());
				}

				switch (field.getOperator()) {
				case Field.OP_AVG:
					sb.append("avg(");
					useOperator = true;
					break;
				case Field.OP_COUNT:
					sb.append("count(");
					useOperator = true;
					break;
				case Field.OP_COUNT_DISTINCT:
					sb.append("count(distinct ");
					useOperator = true;
					break;
				case Field.OP_MAX:
					sb.append("max(");
					useOperator = true;
					break;
				case Field.OP_MIN:
					sb.append("min(");
					useOperator = true;
					break;
				case Field.OP_SUM:
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
		}
		if (first) {
			// there are no fields
			return "select " + rootAlias;
		}
		if (useOperator && notUseOperator) {
			throw new Error("A search can not have a mix of fields with operators and fields without operators.");
		}
		return sb.toString();
	}

	/**
	 * Internal method for generating from clause. This method should be called
	 * after generating other clauses because it relies on the aliases they
	 * create. This method takes every path that is called for in the other
	 * clauses and makes it available as an alias using left joins. It also adds
	 * join fetching for properties specified by <code>fetches</code>
	 */
	protected String generateFromClause(Class<?> entityClass, ISearch search, Map<String, AliasNode> aliases,
			boolean doEagerFetching) {
		if (search.getFetches() != null) {
			// apply fetches
			boolean hasFetches = false, hasFields = false;
			for (String fetch : search.getFetches()) {
				getAlias(entityClass, aliases, fetch, true);
				hasFetches = true;
			}
			if (hasFetches) {
				// don't fetch nodes whose ancestors aren't found in the select
				// clause
				List<String> fields = new ArrayList<String>();
				for (Field field : search.getFields()) {
					if (field.getOperator() == Field.OP_PROPERTY) {
						fields.add(field.getProperty() + ".");
					}
					hasFields = true;
				}
				if (hasFields) {
					for (AliasNode node : aliases.values()) {
						if (node.fetch) {
							// make sure it has an ancestor in the select clause
							boolean hasAncestor = false;
							for (String field : fields) {
								if (node.getFullPath().startsWith(field)) {
									hasAncestor = true;
									break;
								}
							}
							if (!hasAncestor)
								node.fetch = false;
						}
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder("from ");
		sb.append(entityClass.getName());
		sb.append(" ");
		sb.append(rootAlias);

		// traverse alias graph breadth-first
		Queue<AliasNode> queue = new LinkedList<AliasNode>();
		queue.offer(aliases.get(ROOT_PATH));
		while (!queue.isEmpty()) {
			AliasNode node = queue.poll();
			if (node.parent != null) {
				sb.append(" left join ");
				if (doEagerFetching && node.fetch)
					sb.append("fetch ");
				sb.append(node.parent.alias);
				sb.append(".");
				sb.append(node.property);
				sb.append(" as ");
				sb.append(node.alias);
			}
			for (AliasNode child : node.children.values()) {
				queue.offer(child);
			}
		}

		return sb.toString();
	}

	/**
	 * Internal method for generating order by clause. Uses sort options from
	 * search.
	 */
	protected String generateOrderByClause(Class<?> entityClass, ISearch search, Map<String, AliasNode> aliases) {
		if (search.getSorts() == null)
			return "";

		StringBuilder sb = null;
		boolean first = true;
		for (Sort sort : search.getSorts()) {
			if (first) {
				sb = new StringBuilder("order by ");
				first = false;
			} else {
				sb.append(", ");
			}
			if (sort.isIgnoreCase() && metaDataUtil.isSQLStringType(entityClass, sort.getProperty())) {
				sb.append("lower(");
				sb.append(getPath(entityClass, aliases, sort.getProperty()));
				sb.append(")");
			} else {
				sb.append(getPath(entityClass, aliases, sort.getProperty()));
			}
			sb.append(sort.isDesc() ? " desc" : " asc");
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
	protected String generateWhereClause(Class<?> entityClass, ISearch search, List<Object> params,
			Map<String, AliasNode> aliases) {
		List<Filter> filters = search.getFilters();
		String content = null;
		if (filters == null || filters.size() == 0) {
			return "";
		} else if (filters.size() == 1) {
			content = filterToString(entityClass, filters.get(0), params, aliases);
		} else {
			Filter junction = new Filter(null, filters, search.isDisjunction() ? Filter.OP_OR : Filter.OP_AND);
			content = filterToString(entityClass, junction, params, aliases);
		}

		return (content == null) ? "" : "where " + content;
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
	protected String filterToString(Class<?> entityClass, Filter filter, List<Object> params,
			Map<String, AliasNode> aliases) {
		Object value = filter.getValue();
		int operator = filter.getOperator();

		// If the operator needs a value and no value is specified, ignore this
		// filter.
		// Only NULL and NOT_NULL do not need a value.
		if (value == null && operator != Filter.OP_NULL && operator != Filter.OP_NOT_NULL
				&& operator != Filter.OP_EMPTY && operator != Filter.OP_NOT_EMPTY) {
			return null;
		}

		// for IN and NOT IN, if value is empty list, return false, and true
		// respectively
		if (operator == Filter.OP_IN || operator == Filter.OP_NOT_IN) {
			if (value instanceof Collection && ((Collection) value).size() == 0) {
				return operator == Filter.OP_IN ? "1 = 2" : "1 = 1";
			}
			if (value instanceof Object[] && ((Object[]) value).length == 0) {
				return operator == Filter.OP_IN ? "1 = 2" : "1 = 1";
			}
		}

		// convert numbers to the expected type if needed (ex: Integer to Long)
		if (operator == Filter.OP_IN || operator == Filter.OP_NOT_IN) {
			// with IN & NOT IN, check each element in the collection.
			Class<?> expectedClass = metaDataUtil.getExpectedClass(entityClass, filter.getProperty());

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
		} else if (operator != Filter.OP_AND && operator != Filter.OP_OR && operator != Filter.OP_NOT
				&& operator != Filter.OP_NULL && operator != Filter.OP_NOT_NULL) {
			value = Util.convertIfNeeded(value, metaDataUtil.getExpectedClass(entityClass, filter.getProperty()));
		}

		switch (operator) {
		case Filter.OP_NULL:
			return getPath(entityClass, aliases, filter.getProperty()) + " is null";
		case Filter.OP_NOT_NULL:
			return getPath(entityClass, aliases, filter.getProperty()) + " is not null";
		case Filter.OP_IN:
			return getPath(entityClass, aliases, filter.getProperty()) + " in (:p" + param(params, value) + ")";
		case Filter.OP_NOT_IN:
			return getPath(entityClass, aliases, filter.getProperty()) + " not in (:p" + param(params, value) + ")";
		case Filter.OP_EQUAL:
			return getPath(entityClass, aliases, filter.getProperty()) + " = :p" + param(params, value);
		case Filter.OP_NOT_EQUAL:
			return getPath(entityClass, aliases, filter.getProperty()) + " != :p" + param(params, value);
		case Filter.OP_GREATER_THAN:
			return getPath(entityClass, aliases, filter.getProperty()) + " > :p" + param(params, value);
		case Filter.OP_LESS_THAN:
			return getPath(entityClass, aliases, filter.getProperty()) + " < :p" + param(params, value);
		case Filter.OP_GREATER_OR_EQUAL:
			return getPath(entityClass, aliases, filter.getProperty()) + " >= :p" + param(params, value);
		case Filter.OP_LESS_OR_EQUAL:
			return getPath(entityClass, aliases, filter.getProperty()) + " <= :p" + param(params, value);
		case Filter.OP_LIKE:
			return getPath(entityClass, aliases, filter.getProperty()) + " like :p" + param(params, value.toString());
		case Filter.OP_ILIKE:
			return "lower(" + getPath(entityClass, aliases, filter.getProperty()) + ") like lower(:p"
					+ param(params, value.toString()) + ")";
		case Filter.OP_AND:
		case Filter.OP_OR:
			if (!(value instanceof List)) {
				return null;
			}

			String op = filter.getOperator() == Filter.OP_AND ? " and " : " or ";

			StringBuilder sb = new StringBuilder("(");
			boolean first = true;
			for (Object o : ((List) value)) {
				if (o instanceof Filter) {
					String filterStr = filterToString(entityClass, (Filter) o, params, aliases);
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
			String filterStr = filterToString(entityClass, (Filter) value, params, aliases);
			if (filterStr == null)
				return null;

			return "not " + filterStr;
		default:
			throw new IllegalArgumentException("Filter comparison ( " + operator + " ) is invalid.");
		}
	}

	/**
	 * Given a full path to a property (ex. department.manager.salary), return
	 * the reference to that property that uses the appropriate alias (ex.
	 * a4_manager.salary).
	 */
	protected String getPath(Class<?> rootClass, Map<String, AliasNode> aliases, String property) {
		int pos = property.lastIndexOf('.');
		if (pos == -1) {
			return rootAlias + "." + property;
		} else {
			String aliasPath = property.substring(0, pos);
			return getAlias(rootClass, aliases, aliasPath, false).alias + "." + property.substring(pos + 1);
		}
	}

	/**
	 * Given a full path to an entity (ex. department.manager), return the alias
	 * to reference that entity (ex. a4_manager). If there is no alias for the
	 * given path, one will be created.
	 */
	protected AliasNode getAlias(Class<?> rootClass, Map<String, AliasNode> aliases, String path, boolean setFetch) {
		if (aliases.containsKey(path)) {
			AliasNode node = aliases.get(path);
			if (setFetch) {
				while (node.parent != null) {
					node.fetch = true;
					node = node.parent;
				}
			}

			return node;
		} else {
			AliasNode node;
			int pos = path.lastIndexOf('.');
			String property = path.substring(pos + 1);
			String subpath = (pos == -1) ? ROOT_PATH : path.substring(0, pos);

			if (metaDataUtil.isEntity(rootClass, path)) {
				String alias = "a" + Integer.toString(aliases.size() + 1) + "_" + property;

				node = new AliasNode(property, alias);

				// set up path recursively
				getAlias(rootClass, aliases, subpath, setFetch).addChild(property, node);

			} else {
				String alias = getAlias(rootClass, aliases, subpath, setFetch).alias + "." + property;

				node = new AliasNode(null, alias);
			}

			node.fetch = setFetch;
			aliases.put(path, node);

			return node;
		}
	}

	protected static final class AliasNode {
		String property;
		String alias;
		boolean fetch;
		AliasNode parent;
		Map<String, AliasNode> children = new HashMap<String, AliasNode>();

		AliasNode(String property, String alias) {
			this.property = property;
			this.alias = alias;
		}

		void addChild(String prop, AliasNode node) {
			children.put(prop, node);
			node.parent = this;
		}

		public String getFullPath() {
			if (parent == null)
				return "";
			else if (parent.parent == null)
				return property;
			else
				return parent.getFullPath() + "." + property;
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
	protected void securityCheck(Class<?> entityClass, ISearch search) {
		if (search.getFields() != null) {
			for (Field field : search.getFields()) {
				securityCheckProperty(field.getProperty());
			}
		}

		if (search.getFetches() != null) {
			for (String fetch : search.getFetches()) {
				securityCheckProperty(fetch);
			}
		}

		if (search.getSorts() != null) {
			for (Sort sort : search.getSorts()) {
				securityCheckProperty(sort.getProperty());
			}
		}

		if (search.getFilters() != null) {
			for (Filter filter : search.getFilters()) {
				securityCheckFilter(filter);
			}
		}
	}

	/**
	 * Used by <code>securityCheck()</code> to recursively check filters.
	 */
	@SuppressWarnings("unchecked")
	protected void securityCheckFilter(Filter filter) {
		if (filter.getOperator() == Filter.OP_AND || filter.getOperator() == Filter.OP_OR) {
			if (filter.getValue() instanceof List) {
				for (Filter f : (List<Filter>) filter.getValue()) {
					securityCheckFilter(f);
				}
			}
		} else if (filter.getOperator() == Filter.OP_NOT) {
			if (filter.getValue() instanceof Filter) {
				securityCheckFilter((Filter) filter.getValue());
			}
		} else {
			securityCheckProperty(filter.getProperty());
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
