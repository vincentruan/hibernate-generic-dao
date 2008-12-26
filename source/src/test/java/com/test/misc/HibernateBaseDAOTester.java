package com.test.misc;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.NonUniqueResultException;
import org.hibernate.SessionFactory;

import com.trg.dao.hibernate.BaseDAOImpl;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

public class HibernateBaseDAOTester extends BaseDAOImpl implements SearchTestInterface {

	@Override
	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	public <T> List<T> all(Class<T> klass) {
		return super._all(klass);
	}

	public int count(Class<?> klass) {
		return super._count(klass);
	}

	public int count(Search search) {
		return super._count(search);
	}

	public boolean deleteById(Class<?> klass, Serializable id) {
		return super._deleteById(klass, id);
	}

	public boolean deleteEntity(Object entity) {
		return super._deleteEntity(entity);
	}

	public void flush() {
		super._flush();
	}

	public <T> T get(Class<T> klass, Serializable id) {
		return super._get(klass, id);
	}

	public boolean isAttached(Object o) {
		return super._sessionContains(o);
	}

	public <T> T load(Class<T> klass, Serializable id) {
		return super._load(klass, id);
	}

	public void load(Object transientEntity, Serializable id) {
		super._load(transientEntity, id);
	}

	public <T> T merge(T entity) {
		return super._merge(entity);
	}

	public void persist(Object... entities) {
		super._persist(entities);
	}

	public void refresh(Object o) {
		super._refresh(o);
	}

	public Serializable save(Object object) {
		return super._save(object);
	}

	@SuppressWarnings("unchecked")
	public List search(Search search) {
		return super._search(search);
	}

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Search search) {
		return super._searchAndCount(search);
	}

	public Object searchUnique(Search search) throws NonUniqueResultException {
		return super._searchUnique(search);
	}

	public void update(Object... transientEntities) {
		super._update(transientEntities);
	}

	public int count(Search search, Class<?> forceClass) {
		return super._count(search, forceClass);
	}

	@SuppressWarnings("unchecked")
	public List search(Search search, Class<?> forceClass) {
		return super._search(search, forceClass);
	}

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Search search, Class<?> forceClass) {
		return super._searchAndCount(search, forceClass);
	}

	public Object searchUnique(Search search, Class<?> forceClass) {
		return super._searchUnique(search, forceClass);
	}
	

	public void deleteById(Class<?> type, Serializable... ids) {
		super._deleteById(type, ids);
	}

	public void deleteEntities(Object... entities) {
		super._deleteEntities(entities);
	}

	public <T> T[] get(Class<T> type, Serializable... ids) {
		return super._get(type, ids);
	}

	public <T> T[] load(Class<T> type, Serializable... ids) {
		return super._load(type, ids);
	}

	public void save(Object... entities) {
		super._save(entities);
	}
	public void saveOrUpdate(Object... entities) {
		super._saveOrUpdate(entities);
	}

	public void saveOrUpdate(Object entity) {
		super._hibernateSaveOrUpdate(entity);
	}

	public boolean saveOrUpdateIsNew(Object entity) {
		return super._saveOrUpdate(entity);
	}
	
}
