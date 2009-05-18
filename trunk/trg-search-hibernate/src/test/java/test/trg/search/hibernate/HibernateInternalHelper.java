package test.trg.search.hibernate;

import org.hibernate.proxy.HibernateProxy;

import test.trg.search.InternalHelper;


public class HibernateInternalHelper implements InternalHelper {
	
	public boolean isEntityFetched(Object entity) {
		 return !((HibernateProxy)entity).getHibernateLazyInitializer().isUninitialized();
	}
}
