package test.trg.dao.jpa.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trg.dao.jpa.GenericDAOImpl;
import com.trg.search.jpa.JPASearchProcessor;

/**
 * Extension of GenericDAOImpl that is configured for Autowiring with Spring or
 * J2EE.
 */
@Repository
public class BaseDAOImpl<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	@PersistenceContext
	@Override
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}

	@Autowired
	@Override
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}

}
