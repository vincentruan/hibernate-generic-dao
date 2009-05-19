package test.trg.dao.jpa.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trg.dao.jpa.GeneralDAOImpl;
import com.trg.search.jpa.JPASearchProcessor;

@Repository
public class MyGeneralDAOImpl extends GeneralDAOImpl {

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
