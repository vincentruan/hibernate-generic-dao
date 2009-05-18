package test.trg.search.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import test.trg.PersistenceHelper;

public class JPAPersistenceHelper implements PersistenceHelper {

	public <T> T find(Class<T> type, Serializable id) {
		return (T) entityManager.find(type, id);
	}

	public void persist(Object entity) {
		entityManager.persist(entity);
	}
	
	public void flush() {
		entityManager.flush();
	}
	
	public void clear() {
		entityManager.clear();
	}
	
	private EntityManager entityManager;

	@PersistenceContext
	public void setEnityManager(EntityManager enityManager) {
		this.entityManager = enityManager;
	}

}
