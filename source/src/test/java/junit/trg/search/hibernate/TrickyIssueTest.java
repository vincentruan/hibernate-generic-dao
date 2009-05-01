package junit.trg.search.hibernate;

import org.hibernate.SessionFactory;

import com.trg.search.hibernate.HibernateSearchFacade;


public class TrickyIssueTest extends test.trg.search.TrickyIssueTest {
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
		target = new HibernateSearchFacade(sessionFactory);
	}
}
