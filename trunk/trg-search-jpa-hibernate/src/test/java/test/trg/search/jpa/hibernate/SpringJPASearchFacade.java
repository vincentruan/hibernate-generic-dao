package test.trg.search.jpa.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.trg.search.jpa.JPASearchFacade;

public class SpringJPASearchFacade extends JPASearchFacade {
	@Override
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}
}
