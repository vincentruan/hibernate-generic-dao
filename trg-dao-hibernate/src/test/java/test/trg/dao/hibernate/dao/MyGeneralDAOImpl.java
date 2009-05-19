package test.trg.dao.hibernate.dao;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.trg.dao.hibernate.GeneralDAOImpl;

/**
 * Extension of GeneralDAOImpl that is configured for Autowiring with Spring or J2EE.
 */
@Repository
public class MyGeneralDAOImpl extends GeneralDAOImpl {
	@Override
	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
}
