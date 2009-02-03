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

		SearchContext ctx = new SearchContext(entityClass, rootAlias, paramList);

		String select = generateSelectClause(ctx, search);
		String where = generateWhereClause(ctx, search.getFilters(), search.isDisjunction());
		String orderBy = generateOrderByClause(ctx, search);
		String from = generateFromClause(ctx, search.getFetches(), search.getFields(), true);

		StringBuilder sb = new StringBuilder();
		sb.append(select);
		sb.append(from);
		sb.append(where);
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

		SearchContext ctx = new SearchContext(entityClass, rootAlias, paramList);

		String where = generateWhereClause(ctx, search.getFilters(), search.isDisjunction());
		String from = generateFromClause(ctx, search.getFetches(), search.getFields(), false);

		StringBuilder sb = new StringBuilder();
		sb.append("select count(distinct ").append(rootAlias).append(".id)");
		sb.append(from);
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
	protected String generateSelectClause(SearchContext ctx, ISearch search) {

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
					prop = getPathRef(ctx, field.getProperty());
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
			return "select " + ctx.getRootAlias();
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
	protected String generateFromClause(SearchContext ctx, List<String> fetches, List<Field> fields,
			boolean doEagerFetching) {
		StringBuilder sb = new StringBuilder(" from ");
		sb.append(ctx.rootClass.getName());
		sb.append(" ");
		sb.append(ctx.getRootAlias());
		sb.append(generateJoins(ctx, fetches, fields, doEagerFetching));
		return sb.toString();
	}

	/**
	 * Internal method for generating the join portion of the from clause. This
	 * method should be called after generating other clauses because it relies
	 * on the aliases they create. This method takes every path that is called
	 * for in the other clauses and makes it available as an alias using left
	 * joins. It also adds join fetching for properties specified by
	 * <code>fetches</code> if <code>doEagerFetching</code> is <code>true</code>
	 */
	protected String generateJoins(SearchContext ctx, List<String> fetches, List<Field> fields, boolean doEagerFetching) {
		if (doEagerFetching && fetches != null) {
			// apply fetches
			boolean hasFetches = false, hasFields = false;
			for (String fetch : fetches) {
				getAlias(ctx, fetch, true);
				hasFetches = true;
			}
			if (hasFetches && fields != null) {
				// don't fetch nodes whose ancestors aren't found in the select
				// clause
				List<String> fieldProps = new ArrayList<String>();
				for (Field field : fields) {
					if (field.getOperator() == Field.OP_PROPERTY) {
						fieldProps.add(field.getProperty() + ".");
					}
					hasFields = true;
				}
				if (hasFields) {
					for (AliasNode node : ctx.aliases.values()) {
						if (node.fetch) {
							// make sure it has an ancestor in the select clause
							boolean hasAncestor = false;
							for (String field : fieldProps) {
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

		StringBuilder sb = new StringBuilder();

		// traverse alias graph breadth-first
		Queue<AliasNode> queue = new LinkedList<AliasNode>();
		queue.offer(ctx.aliases.get(ROOT_PATH));
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
			for (AliasNode child : node.children) {
				queue.offer(child);
			}
		}

		return sb.toString();
	}

	/**
	 * Internal method for generating order by clause. Uses sort options from
	 * search.
	 */
	protected String generateOrderByClause(SearchContext ctx, ISearch search) {
		if (search.getSorts() == null)
			return "";

		StringBuilder sb = null;
		boolean first = true;
		for (Sort sort : search.getSorts()) {
			if (first) {
				sb = new StringBuilder(" order by ");
				first = false;
			} else {
				sb.append(", ");
			}
			if (sort.isIgnoreCase() && metaDataUtil.isSQLStringType(ctx.rootClass, sort.getProperty())) {
				sb.append("lower(");
				sb.append(getPathRef(ctx, sort.getProperty()));
				sb.append(")");
			} else {
				sb.append(getPathRef(ctx, sort.getProperty()));
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
	protected String generateWhereClause(SearchContext ctx, List<Filter> filters, boolean isDisjunction) {
		String content = null;
		if (filters == null || filters.size() == 0) {
			return "";
		} else if (filters.size() == 1) {
			content = filterToQL(ctx, filters.get(0));
		} else {
			Filter junction = new Filter(null, filters, isDisjunction ? Filter.OP_OR : Filter.OP_AND);
			content = filterToQL(ctx, junction);
		}

		return (content == null) ? "" : " where " + content;
	}

	/**
	 * Add value to paramList and return the named parameter string ":pX".
	 */
	protected String param(SearchContext ctx, Object value) {
		if (value instanceof Class) {
			return ((Class<?>) value).getName();
		}

		ctx.paramList.add(value);
		return ":p" + Integer.toString(ctx.paramList.size());
	}

	/**
	 * Recursively generate the QL fragment for a given search filter option.
	 */
	@SuppressWarnings("unchecked")
	protected String filterToQL(SearchContext ctx, Filter filter) {
		String property = filter.getProperty();
		Object value = filter.getValue();
		int operator = filter.getOperator();

		// If the operator needs a value and no value is specified, ignore this
		// filter.
		// Only NULL, NOT_NULL, EMPTY and NOT_EMPTY do not need a value.
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
			Class<?> expectedClass = metaDataUtil.getExpectedClass(ctx.rootClass, property);
			if ("class".equals(property) || property.endsWith(".class")) {
				expectedClass = Class.class;
			}

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
				&& operator != Filter.OP_NULL && operator != Filter.OP_NOT_NULL && operator != Filter.OP_EMPTY
				&& operator != Filter.OP_NOT_EMPTY && operator != Filter.OP_SOME && operator != Filter.OP_ALL
				&& operator != Filter.OP_NONE) {
			Class<?> expectedClass = metaDataUtil.getExpectedClass(ctx.rootClass, property);
			if ("class".equals(property) || property.endsWith(".class")) {
				expectedClass = Class.class;
			}
			value = Util.convertIfNeeded(value, expectedClass);
		}

		switch (operator) {
		case Filter.OP_NULL:
			return getPathRef(ctx, property) + " is null";
		case Filter.OP_NOT_NULL:
			return getPathRef(ctx, property) + " is not null";
		case Filter.OP_IN:
			return getPathRef(ctx, property) + " in (" + param(ctx, value) + ")";
		case Filter.OP_NOT_IN:
			return getPathRef(ctx, property) + " not in (" + param(ctx, value) + ")";
		case Filter.OP_EQUAL:
			return getPathRef(ctx, property) + " = " + param(ctx, value);
		case Filter.OP_NOT_EQUAL:
			return getPathRef(ctx, property) + " != " + param(ctx, value);
		case Filter.OP_GREATER_THAN:
			return getPathRef(ctx, property) + " > " + param(ctx, value);
		case Filter.OP_LESS_THAN:
			return getPathRef(ctx, property) + " < " + param(ctx, value);
		case Filter.OP_GREATER_OR_EQUAL:
			return getPathRef(ctx, property) + " >= " + param(ctx, value);
		case Filter.OP_LESS_OR_EQUAL:
			return getPathRef(ctx, property) + " <= " + param(ctx, value);
		case Filter.OP_LIKE:
			return getPathRef(ctx, property) + " like " + param(ctx, value.toString());
		case Filter.OP_ILIKE:
			return "lower(" + getPathRef(ctx, property) + ") like lower(" + param(ctx, value.toString()) + ")";
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
					String filterStr = filterToQL(ctx, (Filter) o);
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
			String filterStr = filterToQL(ctx, (Filter) value);
			if (filterStr == null)
				return null;

			return "not " + filterStr;
		case Filter.OP_EMPTY:
			if (metaDataUtil.isSQLStringType(ctx.rootClass, property)) {
				String pathRef = getPathRef(ctx, property);
				return "(" + pathRef + " is null or " + pathRef + " = '')";
			} else if (metaDataUtil.isCollection(ctx.rootClass, property)) {
				return "not exists elements(" + getPathRef(ctx, property) + ")";
			} else {
				return getPathRef(ctx, property) + " is null";
			}
		case Filter.OP_NOT_EMPTY:
			if (metaDataUtil.isSQLStringType(ctx.rootClass, property)) {
				String pathRef = getPathRef(ctx, property);
				return "(" + pathRef + " is not null and " + pathRef + " != '')";
			} else if (metaDataUtil.isCollection(ctx.rootClass, property)) {
				return "exists elements(" + getPathRef(ctx, property) + ")";
			} else {
				return getPathRef(ctx, property) + " is not null";
			}
		case Filter.OP_SOME:
			if (!(value instanceof Filter)) {
				return null;
			} else if (value instanceof Filter) {
				return "exists " + generateSubquery(ctx, property, (Filter) value);
			}
		case Filter.OP_ALL:
			if (!(value instanceof Filter)) {
				return null;
			} else if (value instanceof Filter) {
				return "not exists " + generateSubquery(ctx, property, negate((Filter) value));
			}
		case Filter.OP_NONE:
			if (!(value instanceof Filter)) {
				return null;
			} else if (value instanceof Filter) {
				return "not exists " + generateSubquery(ctx, property, (Filter) value);
			}
		default:
			throw new IllegalArgumentException("Filter comparison ( " + operator + " ) is invalid.");
		}
	}

	/**
	 * Generate a QL string for a subquery on the given property that uses the
	 * given filter. This is used by SOME, ALL and NONE filters.
	 */
	protected String generateSubquery(SearchContext ctx, String property, Filter filter) {
		SearchContext ctx2 = new SearchContext();
		ctx2.rootClass = metaDataUtil.getCollectionElementClass(ctx.rootClass, property);
		ctx2.setRootAlias(rootAlias + (ctx.nextSubqueryNum++));
		ctx2.paramList = ctx.paramList;
		ctx2.nextAliasNum = ctx.nextAliasNum;
		ctx2.nextSubqueryNum = ctx.nextSubqueryNum;

		List<Filter> filters = new ArrayList<Filter>(1);
		filters.add(filter);
		String where = generateWhereClause(ctx2, filters, false);
		String joins = generateJoins(ctx2, null, null, false);
		ctx.nextAliasNum = ctx2.nextAliasNum;
		ctx.nextSubqueryNum = ctx2.nextSubqueryNum;

		StringBuilder sb = new StringBuilder();
		sb.append("(from ");
		sb.append(getPathRef(ctx, property));
		sb.append(" ");
		sb.append(ctx2.getRootAlias());
		sb.append(joins);
		sb.append(where);
		sb.append(")");

		return sb.toString();
	}

	/**
	 * Return a filter that negates the given filter.
	 */
	protected Filter negate(Filter filter) {
		return Filter.not(addExplicitNullChecks(filter));
	}

	/**
	 * Used by {@link #negate(Filter)}. There's a complication with null values
	 * in the database so that !(x == 1) is not the opposite of (x == 1). Rather
	 * !(x == 1 and x != null) is the same as (x == 1). This method applies the
	 * null check explicitly to all filters included in the given filter tree.
	 */
	protected Filter addExplicitNullChecks(Filter filter) {
		switch (filter.getOperator()) {
		case Filter.OP_AND:
		case Filter.OP_OR:
			Filter result = (filter.getOperator() == Filter.OP_AND ? Filter.and() : Filter.or());
			if (filter.getValue() instanceof List) {
				for (Filter f : (List<Filter>) filter.getValue()) {
					result.add(addExplicitNullChecks(f));
				}
			}
			return result;
		case Filter.OP_NOT:
			return Filter.not((filter.getValue() instanceof Filter) ? addExplicitNullChecks((Filter) filter.getValue())
					: null);
		case Filter.OP_EMPTY:
		case Filter.OP_NOT_EMPTY:
		case Filter.OP_NULL:
		case Filter.OP_NOT_NULL:
		case Filter.OP_ALL:
		case Filter.OP_SOME:
		case Filter.OP_NONE:
			return filter;
		default:
			return Filter.and(filter, Filter.isNotNull(filter.getProperty()));
		}
	}

	/**
	 * Given a full path to a property (ex. department.manager.salary), return
	 * the reference to that property that uses the appropriate alias (ex.
	 * a4_manager.salary).
	 */
	protected String getPathRef(SearchContext ctx, String path) {
		if (path == null || "".equals(path)) {
			return ctx.getRootAlias();
		}

		String[] parts = splitPath(ctx, path);

		return getAlias(ctx, parts[0], false).alias + "." + parts[1];
	}

	/**
	 * Split a path into two parts. The first part will need to be aliased. The
	 * second part will be a property of that alias. For example:
	 * (department.manager.salary) would return [department.manager, salary].
	 */
	protected String[] splitPath(SearchContext ctx, String path) {
		if (path == null || "".equals(path))
			return null;

		int pos = path.lastIndexOf('.');

		if (pos == -1) {
			return new String[] { "", path };
		} else {
			String lastSegment = path.substring(pos + 1);
			String currentPath = path;
			boolean first = true;

			// Basically gobble up as many segments as possible until we come to
			// an entity. Entities must become aliases so we can use our left
			// join feature.
			// The exception is that if a segment is an id, we want to skip the
			// entity preceding it because (entity.id) is actually stored in the
			// same table as the foreign key.
			while (true) {
				if (lastSegment.equals("id")) { // TODO: more rubust checking
					// for whether it's an id property
					// skip one segment
					if (pos == -1) {
						return new String[] { "", path };
					}
					pos = currentPath.lastIndexOf('.', pos - 1);
				} else if (!first && metaDataUtil.isEntity(ctx.rootClass, currentPath)) {
					// when we reach an entity (excluding the very last
					// segment), we're done
					return new String[] { currentPath, path.substring(currentPath.length() + 1) };
				}
				first = false;

				// for size, we need to go back to the 'first' behavior
				// for the next segment
				if (lastSegment.equals("size")) { // TODO make sure the previous
					// is a collection
					first = true;
				}

				// if that was the last segment, we're done
				if (pos == -1) {
					return new String[] { "", path };
				}
				// proceed to the next segment
				currentPath = currentPath.substring(0, pos);
				pos = currentPath.lastIndexOf('.');
				if (pos == -1) {
					lastSegment = currentPath;
				} else {
					lastSegment = currentPath.substring(pos + 1);
				}
			}

		}

		// 1st
		// if "id", go 2; try again
		// if component, go 1; try again
		// if entity, go 1; try again
		// if size, go 1; try 1st again

		// successive
		// if "id", go 2; try again
		// if component, go 1; try again
		// if entity, stop
	}

	/**
	 * Given a full path to an entity (ex. department.manager), return the alias
	 * to reference that entity (ex. a4_manager). If there is no alias for the
	 * given path, one will be created.
	 */
	protected AliasNode getAlias(SearchContext ctx, String path, boolean setFetch) {
		if (path == null || path.equals("")) {
			return ctx.aliases.get(ROOT_PATH);
		} else if (ctx.aliases.containsKey(path)) {
			AliasNode node = ctx.aliases.get(path);
			if (setFetch) {
				while (node.parent != null) {
					node.fetch = true;
					node = node.parent;
				}
			}

			return node;
		} else {
			String[] parts = splitPath(ctx, path);

			int pos = parts[1].lastIndexOf('.');

			String alias = "a" + (ctx.nextAliasNum++) + "_" + (pos == -1 ? parts[1] : parts[1].substring(pos + 1));

			AliasNode node = new AliasNode(parts[1], alias);

			// set up path recursively
			getAlias(ctx, parts[0], setFetch).addChild(node);

			node.fetch = setFetch;
			ctx.aliases.put(path, node);

			return node;
		}
	}

	protected static final class AliasNode {
		String property;
		String alias;
		boolean fetch;
		AliasNode parent;
		List<AliasNode> children = new ArrayList<AliasNode>();

		AliasNode(String property, String alias) {
			this.property = property;
			this.alias = alias;
		}

		void addChild(AliasNode node) {
			children.add(node);
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

	protected static final class SearchContext {
		Class<?> rootClass;
		Map<String, AliasNode> aliases = new HashMap<String, AliasNode>();
		List<Object> paramList;

		int nextAliasNum = 1;
		int nextSubqueryNum = 1;

		public SearchContext() {
		}

		public SearchContext(Class<?> rootClass, String rootAlias, List<Object> paramList) {
			this.rootClass = rootClass;
			setRootAlias(rootAlias);
			this.paramList = paramList;
		}

		public void setRootAlias(String rootAlias) {
			this.aliases.put(ROOT_PATH, new AliasNode(ROOT_PATH, rootAlias));
		}

		public String getRootAlias() {
			return this.aliases.get(ROOT_PATH).alias;
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
				if (field != null && field.getProperty() != null && !field.getProperty().equals(""))
					securityCheckProperty(field.getProperty());
			}
		}

		if (search.getFetches() != null) {
			for (String fetch : search.getFetches()) {
				if (fetch == null) {
					throw new IllegalArgumentException("The search contains a null fetch.");
				}
				securityCheckProperty(fetch);
			}
		}

		if (search.getSorts() != null) {
			for (Sort sort : search.getSorts()) {
				if (sort == null) {
					throw new IllegalArgumentException("The search contains a null Sort.");
				}
				if (sort.getProperty() == null) {
					throw new IllegalArgumentException("There is a Sort in the search that has no property specified: "
							+ sort.toString());
				}
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
		if (filter == null) {
			throw new IllegalArgumentException("The search contains a null Filter.");
		}
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
			if (filter.getProperty() != null && !filter.getProperty().equals("")) {
				securityCheckProperty(filter.getProperty());
			}
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
