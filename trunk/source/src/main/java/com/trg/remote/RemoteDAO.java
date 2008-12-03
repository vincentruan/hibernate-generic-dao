package com.trg.remote;

import java.io.Serializable;
import java.util.List;

import com.trg.dao.GeneralDAO;
import com.trg.search.SearchResult;

/**
 * Example of what a general remote DAO might look like.
 * 
 * @author dwolverton
 */
@SuppressWarnings("unchecked")
public class RemoteDAO {
	private static long mockDelay = 0;
	
	private GeneralDAO dao;

	public void setDAO(GeneralDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * <p>
	 * Here is an example of one DAO method that could be exposed remotely. The
	 * ID of the object and the class of the object to get are passed in and the
	 * object from the datastore is returned.
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
		
		return dao.fetch(id, klass);
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

		return dao.fetchAll(klass);
	}

	public Object create(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		dao.create(object);
		return object;
	}

	public Object update(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		dao.update(object);
		return object;
	}
	
	public Object createOrUpdate(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		dao.createOrUpdate(object);
		return object;
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

		dao.deleteById(id, klass);
	}

	public void deleteEntity(Object object) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		dao.deleteEntity(object);
	}
	
	public void deleteList(Object[] list) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		
		for(int i = 0; i < list.length; i++) {
			deleteEntity(list[i]);
		}
	}
	
	public void deleteListById(Serializable[] ids, String className) throws Exception {
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
		
		return dao.search(search);
	}

	public int searchLength(RemoteSearch search) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		try {
			search.setSearchClass(Class.forName(search.getClassName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return dao.searchLength(search);
	}

	public SearchResult searchAndLength(RemoteSearch search) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		try {
			search.setSearchClass(Class.forName(search.getClassName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return dao.searchAndLength(search);
	}
	
	public Object searchUnique(RemoteSearch search) throws Exception {
		if (mockDelay != 0) Thread.sleep(mockDelay);
		try {
			search.setSearchClass(Class.forName(search.getClassName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return dao.searchUnique(search);
	}
}
