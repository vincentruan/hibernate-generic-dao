package junit.trg.search.hibernate;

import org.hibernate.SessionFactory;

import com.trg.search.hibernate.HibernateSearchFacade;


public class SearchResultTest extends test.trg.search.SearchResultTest {
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
		target = new HibernateSearchFacade(sessionFactory);
	}
}
