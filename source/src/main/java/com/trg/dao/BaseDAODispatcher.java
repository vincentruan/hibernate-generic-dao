package com.trg.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.annotation.Resource;

import com.trg.dao.dao.original.GeneralDAO;

public class BaseDAODispatcher {

	protected Map<String, Object> specificDAOs;

	@Resource
	protected GeneralDAO generalDAO;

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
