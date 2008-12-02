package com.trg.remote;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.trg.dao.GeneralDAO;
import com.trg.dao.Util;
import com.trg.search.Search;
import com.trg.search.SearchResult;

/**
 * Example of what a general remote DAO might look like.
 * 
 * @author dwolverton
 */
@SuppressWarnings("unchecked")
public class RemoteDAO {
	private static long mockDelay = 0;
	
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
	 * GeneralDAO has default implementations for the standard DAO methods. Which
	 * model class it uses is specified when calling the particular method.
	 */
	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}
	
	private Object getSpecificDAO(String className) {
		return specificDAOs == null ? null : specificDAOs.get(className);
	}

	/**
	 * <p>
	 * Here is an example of one DAO method that could be exposed remotely. The
	 * ID of the object and the class of the object to get are passed in and the
	 * object from the database is returned.
	 * <p>
	 * Notice that the implementation first checks for a specific DAO for the
	 * class, and if none is found defaults to the general DAO.
	 */
	public Object fetch(Serializable id, String className) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		Class<?> klass;
		try {
			klass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		
		Class<?> idClass = klass.getMethod("getId").getReturnType();
		id = (Serializable) Util.convertIfNeeded(id, idClass);

		Object specificDao = getSpecificDAO(klass.getName());
		if (specificDao != null) {
			try {
				return specificDao.getClass().getMethod("fetch", idClass)
						.invoke(specificDao, id);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			return generalDAO.fetch(id, klass);
		}
	}

	public List fetchAll(String className) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		Class<?> klass;
		try {
			klass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		Object specificDao = getSpecificDAO(klass.getName());
		if (specificDao != null) {
			try {
				return (List) specificDao.getClass().getMethod("fetchAll")
						.invoke(specificDao);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			return generalDAO.fetchAll(klass);
		}
	}

	public Object create(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		Object specificDao = getSpecificDAO(object.getClass().getName());
		if (specificDao != null) {
			try {
				Method method = null;
				try {
					method = specificDao.getClass().getMethod("create", object.getClass());
				} catch (NoSuchMethodException ex) {
					method = specificDao.getClass().getMethod("create", Object.class);
				}
				method.invoke(specificDao, object);
				return object;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			generalDAO.create(object);
			return object;
		}
	}

	public Object update(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		Object specificDao = getSpecificDAO(object.getClass().getName());
		if (specificDao != null) {
			try {
				Method method = null;
				try {
					method = specificDao.getClass().getMethod("update", object.getClass());
				} catch (NoSuchMethodException ex) {
					method = specificDao.getClass().getMethod("update", Object.class);
				}
				method.invoke(specificDao, object);
				return object;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			generalDAO.update(object);
			return object;
		}
	}
	
	public Object createOrUpdate(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		Object specificDao = getSpecificDAO(object.getClass().getName());
		if (specificDao != null) {
			try {
				Method method = null;
				try {
					method = specificDao.getClass().getMethod("createOrUpdate", object.getClass());
				} catch (NoSuchMethodException ex) {
					method = specificDao.getClass().getMethod("createOrUpdate", Object.class);
				}
				method.invoke(specificDao, object);
				return object;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			generalDAO.createOrUpdate(object);
			return object;
		}
	}	

	public void deleteById(Serializable id, String className) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		Class<?> klass;
		try {
			klass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		Class<?> idClass = klass.getMethod("getId").getReturnType();
		id = (Serializable) Util.convertIfNeeded(id, idClass);

		Object specificDao = getSpecificDAO(klass.getName());
		if (specificDao != null) {
			try {
				specificDao.getClass().getMethod("deleteById", Serializable.class).invoke(
						specificDao, id);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			generalDAO.deleteById(id, klass);
		}
	}

	public void deleteEntity(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		Object specificDao = getSpecificDAO(object.getClass().getName());
		if (specificDao != null) {
			try {
				Method method = null;
				try {
					method = specificDao.getClass().getMethod("deleteEntity", object.getClass());
				} catch (NoSuchMethodException ex) {
					method = specificDao.getClass().getMethod("deleteEntity", Object.class);
				}
				method.invoke(specificDao, object);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			generalDAO.deleteEntity(object);
		}
	}
	
	public void deleteList(Object[] list) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		
		for(int i = 0; i < list.length; i++) {
			deleteEntity(list[i]);
		}
	}
	
	public void deleteListById(Long[] ids, String className) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		
		for(int i = 0; i < ids.length; i++) {
			deleteById(ids[i], className);
		}		
	}

	public List search(RemoteSearch search) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		try {
			search.setSearchClass(Class.forName(search.getClassName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		Object specificDao = getSpecificDAO(search.getSearchClass().getName());
		if (specificDao != null) {
			try {
				return (List) specificDao.getClass().getMethod("search",
						Search.class).invoke(specificDao, search);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			return generalDAO.search(search);
		}
	}

	public int searchLength(RemoteSearch search) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		try {
			search.setSearchClass(Class.forName(search.getClassName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		Object specificDao = getSpecificDAO(search.getSearchClass().getName());
		if (specificDao != null) {
			try {
				return (Integer) specificDao.getClass().getMethod(
						"searchLength", Search.class).invoke(specificDao,
						search);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			return generalDAO.searchLength(search);
		}
	}

	public SearchResult searchAndLength(RemoteSearch search) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		try {
			search.setSearchClass(Class.forName(search.getClassName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		Object specificDao = getSpecificDAO(search.getSearchClass().getName());
		if (specificDao != null) {
			try {
				return (SearchResult) specificDao.getClass().getMethod(
						"searchAndLength", Search.class).invoke(specificDao,
						search);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			return generalDAO.searchAndLength(search);
		}
	}
	
	public Object searchUnique(RemoteSearch search) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		try {
			search.setSearchClass(Class.forName(search.getClassName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		Object specificDao = getSpecificDAO(search.getSearchClass().getName());
		if (specificDao != null) {
			try {
				return specificDao.getClass().getMethod(
						"searchUnique", Search.class).invoke(specificDao,
						search);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			return generalDAO.searchUnique(search);
		}
	}
}
