package com.trg.dao.dao.original;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.NonUniqueResultException;

import com.trg.dao.DAODispatcherException;
import com.trg.dao.Util;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

@SuppressWarnings("unchecked")
public class DAODispatcher implements GeneralDAO {

	private Map<String, Object> specificDAOs;

	@Resource
	private GeneralDAO generalDAO;

	/**
	 * In practice some DAOs could be put into this map using Spring. If a DAO
	 * is in this map, it will be used instead of the general DAO. This provides
	 * a way to override the default implementation for objects with special
	 * considerations.
	 */
	public void setSpecificDAOs(Map<String, Object> specificDAOs) {
		this.specificDAOs = specificDAOs;
	}

	/**
	 * GeneralDAO has default implementations for the standard DAO methods.
	 * Which model class it uses is specified when calling the particular
	 * method.
	 */
	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	protected Object getSpecificDAO(String className) {
		return specificDAOs == null ? null : specificDAOs.get(className);
	}

	public void create(Object object) {
		Object specificDAO = getSpecificDAO(object.getClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				((GenericDAO) specificDAO).create(object);
			} else {
				callMethod(specificDAO, "create", object);
			}
		} else {
			generalDAO.create(object);
		}
	}

	public boolean createOrUpdate(Object object) {
		Object specificDAO = getSpecificDAO(object.getClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).createOrUpdate(object);
			} else {
				return (Boolean) callMethod(specificDAO, "createOrUpdate", object);
			}
		} else {
			return generalDAO.createOrUpdate(object);
		}
	}

	public boolean deleteById(Class<?> klass, Serializable id) {
		Object specificDAO = getSpecificDAO(klass.getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).deleteById(id);
			} else {
				return (Boolean) callMethod(specificDAO, "deleteById", id);
			}
		} else {
			return generalDAO.deleteById(klass, id);
		}
	}

	public boolean deleteEntity(Object object) {
		Object specificDAO = getSpecificDAO(object.getClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).deleteEntity(object);
			} else {
				return (Boolean) callMethod(specificDAO, "deleteEntity", object);
			}
		} else {
			return generalDAO.deleteEntity(object);
		}
	}

	public <T> T fetch(Class<T> klass, Serializable id) {
		Object specificDAO = getSpecificDAO(klass.getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return (T) ((GenericDAO) specificDAO).fetch(id);
			} else {
				return (T) callMethod(specificDAO, "fetch", id);
			}
		} else {
			return generalDAO.fetch(klass, id);
		}
	}

	public <T> List<T> fetchAll(Class<T> klass) {
		Object specificDAO = getSpecificDAO(klass.getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).fetchAll();
			} else {
				return (List<T>) callMethod(specificDAO, "fetchAll");
			}
		} else {
			return generalDAO.fetchAll(klass);
		}
	}

	/**
	 * @deprecated use flush(Class<?>)
	 */
	public void flush() {
		throw new DAODispatcherException(
				"The flush() method cannot be used with DAODispatcher because it could does not include a Class type to dispatch to. Use flush(Class<?>).");
	}

	public void flush(Class<?> klass) {
		Object specificDAO = getSpecificDAO(klass.getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				((GenericDAO) specificDAO).flush();
			} else {
				callMethod(specificDAO, "flush");
			}
		} else {
			generalDAO.flush();
		}
	}

	public boolean isConnected(Object object) {
		Object specificDAO = getSpecificDAO(object.getClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).isConnected(object);
			} else {
				return (Boolean) callMethod(specificDAO, "isConnected", object);
			}
		} else {
			return generalDAO.isConnected(object);
		}
	}

	public void refresh(Object object) {
		Object specificDAO = getSpecificDAO(object.getClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				((GenericDAO) specificDAO).refresh(object);
			} else {
				callMethod(specificDAO, "refresh", object);
			}
		} else {
			generalDAO.refresh(object);
		}
	}

	public List search(Search search) {
		Object specificDAO = getSpecificDAO(search.getSearchClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).search(search);
			} else {
				return (List) callMethod(specificDAO, "search", search);
			}
		} else {
			return generalDAO.search(search);
		}
	}

	public SearchResult searchAndCount(Search search) {
		Object specificDAO = getSpecificDAO(search.getSearchClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).searchAndCount(search);
			} else {
				return (SearchResult) callMethod(specificDAO,
						"searchAndLength", search);
			}
		} else {
			return generalDAO.searchAndCount(search);
		}
	}

	public int count(Search search) {
		Object specificDAO = getSpecificDAO(search.getSearchClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).count(search);
			} else {
				return (Integer) callMethod(specificDAO, "searchLength", search);
			}
		} else {
			return generalDAO.count(search);
		}
	}

	public Object searchUnique(Search search) throws NonUniqueResultException {
		Object specificDAO = getSpecificDAO(search.getSearchClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).searchUnique(search);
			} else {
				return callMethod(specificDAO, "searchUnique", search);
			}
		} else {
			return generalDAO.searchUnique(search);
		}
	}

	public void update(Object object) {
		Object specificDAO = getSpecificDAO(object.getClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				((GenericDAO) specificDAO).update(object);
			} else {
				callMethod(specificDAO, "update", object);
			}
		} else {
			generalDAO.update(object);
		}
	}
	
	protected Object callMethod(Object specificDAO, String methodName, Object... args) {
		try {
			return Util.callMethod(specificDAO, methodName, args);
		} catch (IllegalArgumentException e) {
			throw new DAODispatcherException(e);
		} catch (NoSuchMethodException e) {
			throw new DAODispatcherException(e);
		} catch (IllegalAccessException e) {
			throw new DAODispatcherException(e);
		} catch (InvocationTargetException e) {
			throw new DAODispatcherException(e);
		}
	}
}