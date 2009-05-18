package test.trg.dao.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import test.trg.PersistenceHelper;

public class HibernatePersistenceHelper implements PersistenceHelper {

	@SuppressWarnings("unchecked")
	public <T> T find(Class<T> type, Serializable id) {
		return (T) sessionFactory.getCurrentSession().get(type, id);
	}
	
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	public void persist(Object entity) {
		sessionFactory.getCurrentSession().persist(entity);
	}
	
	public void clear() {
		sessionFactory.getCurrentSession().clear();
	}
	
	private SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
