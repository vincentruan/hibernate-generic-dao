package junit.trg.search.hibernate;

import org.hibernate.SessionFactory;

import com.trg.search.hibernate.HibernateSearchFacade;


public class FilterCollectionTest extends test.trg.search.FilterCollectionTest {
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
		target = new HibernateSearchFacade(sessionFactory);
	}
}
