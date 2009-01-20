package com.trg.dao.dao.standard;

import java.io.Serializable;
import java.util.List;

import com.trg.dao.dao.original.FlexSearch;
import com.trg.dao.dao.original.FlexSearchWrapper;
import com.trg.dao.search.SearchResult;

public class FlexDAOAdapter {
	
	private GeneralDAO dao;
	
	public void setDao(GeneralDAO dao) {
		this.dao = dao;
	}
	
	public Object find(String className, Serializable id) throws ClassNotFoundException {
		Class<?> type = Class.forName(className);
		return dao.find(type, id);
	}
	
	public Object[] findMulti(String className, Serializable[] ids) throws ClassNotFoundException {
		Class<?> type = Class.forName(className);
		return dao.find(type, ids);
	}
	
	public List<?> findAll(String className) throws ClassNotFoundException {
		Class<?> type = Class.forName(className);
		return dao.findAll(type);
	}
	
	 //create or update based on whether id already exist in datastore
	public Object save(Object entity) {
		dao.save(entity);
		return entity;
	}
	
	public Object[] saveMulti(Object[] entities) {
		dao.save(entities);
		return entities;
	}
	
	public void remove(Object entity) {
		dao.remove(entity);
	}
	
	public void removeById(String className, Serializable id) throws ClassNotFoundException {
		Class<?> type = Class.forName(className);
		dao.removeById(type, id);
	}
	
	public void removeMulti(Object[] entities) {
		dao.remove(entities);
	}
	
	public void removeMultiById(String className, Serializable[] ids) throws ClassNotFoundException {
		Class<?> type = Class.forName(className);
		dao.removeByIds(type, ids);
	}
	
	public List<?> search(FlexSearch flexSearch) throws ClassNotFoundException {
		return dao.search(new FlexSearchWrapper(flexSearch));
	}
	
	public int count(FlexSearch flexSearch) throws ClassNotFoundException {
		return dao.count(new FlexSearchWrapper(flexSearch));
	}
	
	public SearchResult<?> searchAndCount(FlexSearch flexSearch) throws ClassNotFoundException {
		return dao.searchAndCount(new FlexSearchWrapper(flexSearch));
	}
	
	public Object searchUnique(FlexSearch flexSearch) throws ClassNotFoundException {
		return dao.searchUnique(new FlexSearchWrapper(flexSearch));
	}
}
