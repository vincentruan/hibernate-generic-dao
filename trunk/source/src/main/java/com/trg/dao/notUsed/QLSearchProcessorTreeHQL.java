package com.trg.dao.notUsed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.trg.dao.Util;
import com.trg.search.Fetch;
import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.search.Sort;

public class QLSearchProcessorTreeHQL {

	protected Node buildTree(Search search) {
		Node root = new Node();
		root.property = search.getSearchClass().getName();
		root.alias = "main";
		Node node;
		
		Iterator<Fetch> fetchItr = search.fetchIterator();
		while (fetchItr.hasNext()) {
			Fetch fetch = fetchItr.next();
			node = addToTree(root, fetch.property);
			node.fetch = fetch;
		}
		
		Iterator<Sort> sortItr = search.sortIterator();
		while (sortItr.hasNext()) {
			Sort sort = sortItr.next();
			node = addToTree(root, sort.property);
			node.sort = sort;
		}
		
		Iterator<Filter> filterItr = search.filterIterator();
		while (filterItr.hasNext()) {
			Filter filter = filterItr.next();
			node = addToTree(root, filter.property);
			node.filter = filter;
		}
		
		
		return root;
	}
	
	protected Node addToTree(Node root, String path) {
		if (path == null || path.trim().length() == 0)
			return root;
		return addToTree(root, path.split("\\."), 0);
	}
	
	protected Node addToTree(Node root, String[] path, int pathIndex) {
		Node node = root.children.get(path[pathIndex]);
		if (node == null) {
			node = new Node();
			node.property = path[pathIndex];
			node.parent = root;
			root.children.put(path[pathIndex], node);
		}
		
		if (pathIndex == path.length - 1) {
			return node;
		} else {
			return addToTree(node, path, pathIndex + 1);
		}
	}
	
	
	public String generateQL(Search search, List<Object> paramList) {
		Map<String, String> aliases = new HashMap<String, String>();

		String select, from, where, orderBy;

		select = generateSelectClause(search, aliases);
		where = generateWhereClause(search, paramList, aliases);
		from = generateFromClause(search, aliases);
		orderBy = generateOrderByClause(search);

		StringBuilder sb = new StringBuilder();
		sb.append(select);
		sb.append(" ");
		sb.append(from);
		sb.append(" ");
		sb.append(where);
		sb.append(" ");
		sb.append(orderBy);

		return sb.toString();
	}

	private String generateSelectClause(Search search,
			Map<String, String> aliases) {
		Iterator<Fetch> fetchItr = search.fetchIterator();
		if (search.getFetchMode() == Search.FETCH_ENTITY) {
			while (fetchItr.hasNext()) {
				if (fetchItr.next().operator != Fetch.OP_PROPERTY) {
					throw new Error(
							"A search with fetch mode FETCH_ENTITY cannot have fetches with operators. Change the fetch mode.");
				}
			}
			return "select main";
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

	private String generateFromClause(Search search, Map<String, String> aliases) {
		Map<String, Object> fetchProperties = new HashMap<String, Object>();
		
		if (search.getFetchMode() == Search.FETCH_ENTITY) {
			Iterator<Fetch> fetchItr = search.fetchIterator();
			while (fetchItr.hasNext()) {
				Fetch fetch = fetchItr.next();
				getAlias(aliases, fetch.property);
				fetchProperties.put(fetch.property, null);
			}
		}
		
		
		StringBuilder sb = new StringBuilder("from ");
		sb.append(search.getSearchClass().getName());
		sb.append(" main");

		Map<String, String> usedAliases = new HashMap<String, String>();
		int aliasCount = aliases.size();

		for (Map.Entry<String, String> entry : aliases.entrySet()) {
			String wholePath = entry.getKey();
			String alias = "main";
			int lastPos = -1;
			int pos = wholePath.indexOf('.');
			while (pos != -1) {
				String currentPath = wholePath.substring(0, pos);
				if (!usedAliases.containsKey(currentPath)) {
					String prop = wholePath.substring(lastPos + 1, pos);
					sb.append(" left join ");
					if (fetchProperties.containsKey(currentPath))
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
				if (fetchProperties.containsKey(wholePath))
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

	private String generateOrderByClause(Search search) {
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
			sb.append("main.");
			sb.append(sort.property);
			sb.append(sort.desc ? " desc" : " asc");
		}
		if (first) {
			return "";
		}
		return sb.toString();
	}

	private String generateWhereClause(Search search, List<Object> params,
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

	private String param(List<Object> params, Object value) {
		params.add(value);
		return Integer.toString(params.size());
	}

	private String filterToString(Search search, Filter filter,
			List<Object> params, Map<String, String> aliases) {
		Object value = filter.value;

		// convert numbers to the expected type if needed (ex: Integer to Long)
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

	private String getPath(Map<String, String> aliases, String property) {
		int pos = property.lastIndexOf('.');
		if (pos == -1) {
			return "main." + property;
		} else {
			String aliasPath = property.substring(0, pos);
			return getAlias(aliases, aliasPath) + "." + property.substring(pos + 1);
		}
	}
	
	private String getAlias(Map<String, String> aliases, String path) {
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

}
