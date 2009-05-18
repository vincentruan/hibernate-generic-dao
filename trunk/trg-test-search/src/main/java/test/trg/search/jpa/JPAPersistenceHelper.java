package test.trg.search.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import test.trg.PersistenceHelper;

public class JPAPersistenceHelper implements PersistenceHelper {

	public <T> T find(Class<T> type, Serializable id) {
		return (T) enityManager.find(type, id);
	}

	public void persist(Object entity) {
		enityManager.persist(entity);
	}
	
	private EntityManager enityManager;

	@PersistenceContext
	public void setEnityManager(EntityManager enityManager) {
		this.enityManager = enityManager;
	}

}
