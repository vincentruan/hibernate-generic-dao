package junit.trg.search.hibernate;

import org.hibernate.SessionFactory;

import com.trg.search.hibernate.HibernateMetadataUtil;


public class MetadataTest extends test.trg.search.MetadataTest {
	public void setSessionFactory(SessionFactory sessionFactory) {
		metadataUtil = HibernateMetadataUtil.getInstanceForSessionFactory(sessionFactory);
	}
}
