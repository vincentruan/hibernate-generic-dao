package com.trg.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.NonUniqueResultException;

import com.trg.search.Search;
import com.trg.search.SearchResult;

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
				callMethod(specificDAO, "create", 0, Object.class, object);
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
				return (Boolean) callMethod(specificDAO, "createOrUpdate", 0,
						Object.class, object);
			}
		} else {
			return generalDAO.createOrUpdate(object);
		}
	}

	public boolean deleteById(Serializable id, Class<?> klass) {
		Object specificDAO = getSpecificDAO(klass.getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).deleteById(id);
			} else {
				return (Boolean) callMethod(specificDAO, "deleteById", 0,
						Serializable.class, id);
			}
		} else {
			return generalDAO.deleteById(id, klass);
		}
	}

	public boolean deleteEntity(Object object) {
		Object specificDAO = getSpecificDAO(object.getClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).deleteEntity(object);
			} else {
				return (Boolean) callMethod(specificDAO, "deleteEntity", 0,
						Object.class, object);
			}
		} else {
			return generalDAO.deleteEntity(object);
		}
	}

	public <T> T fetch(Serializable id, Class<T> klass) {
		Object specificDAO = getSpecificDAO(klass.getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return (T) ((GenericDAO) specificDAO).fetch(id);
			} else {
				return (T) callMethod(specificDAO, "fetch", 0,
						Serializable.class, id);
			}
		} else {
			return generalDAO.fetch(id, klass);
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
				callMethod(specificDAO, "flush", 0,
						Object.class);
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
				return (Boolean) callMethod(specificDAO, "isConnected", 0,
						Object.class, object);
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
				callMethod(specificDAO, "refresh", 0, Object.class, object);
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

	public SearchResult searchAndLength(Search search) {
		Object specificDAO = getSpecificDAO(search.getSearchClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).searchAndLength(search);
			} else {
				return (SearchResult) callMethod(specificDAO,
						"searchAndLength", search);
			}
		} else {
			return generalDAO.searchAndLength(search);
		}
	}

	public int searchLength(Search search) {
		Object specificDAO = getSpecificDAO(search.getSearchClass().getName());
		if (specificDAO != null) {
			if (specificDAO instanceof GenericDAO) {
				return ((GenericDAO) specificDAO).searchLength(search);
			} else {
				return (Integer) callMethod(specificDAO, "searchLength", search);
			}
		} else {
			return generalDAO.searchLength(search);
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
				callMethod(specificDAO, "update", 0, Object.class, object);
			}
		} else {
			generalDAO.update(object);
		}
	}

	/**
	 * This is a helper method to call a method on an Object with the given
	 * parameters. It is used for dispatching to DAOs that do not implement the
	 * GenericDAO interface.
	 * 
	 * @param specificDAO
	 * @param methodName
	 * @param flipIndex
	 * @param flipClass
	 * @param params
	 * @return
	 * @throws DAODispatcherException
	 */
	protected Object callMethod(Object specificDAO, String methodName,
			int flipIndex, Class<?> flipClass, Object... params)
			throws DAODispatcherException {
		Class<?>[] paramClasses = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			paramClasses[i] = params[i].getClass();
		}

		try {
			Method method = null;
			try {
				method = specificDAO.getClass().getMethod(methodName,
						paramClasses);
			} catch (NoSuchMethodException ex) {
				if (flipIndex >= 0)
					paramClasses[flipIndex] = flipClass;
				method = specificDAO.getClass().getMethod(methodName,
						paramClasses);
			}

			return method.invoke(specificDAO, params);
		} catch (SecurityException e) {
			throw new DAODispatcherException(e);
		} catch (NoSuchMethodException e) {
			throw new DAODispatcherException(e);
		} catch (IllegalArgumentException e) {
			throw new DAODispatcherException(e);
		} catch (IllegalAccessException e) {
			throw new DAODispatcherException(e);
		} catch (InvocationTargetException e) {
			throw new DAODispatcherException(e);
		}
	}

	protected Object callMethod(Object specificDAO, String methodName,
			Object... params) throws DAODispatcherException {
		return callMethod(specificDAO, methodName, -1, null, params);
	}
}
