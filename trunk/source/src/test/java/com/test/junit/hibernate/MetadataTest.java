package com.test.junit.hibernate;

import org.hibernate.SessionFactory;

import com.trg.search.hibernate.HibernateMetadataUtil;


public class MetadataTest extends com.test.base.MetadataTest {
	public void setSessionFactory(SessionFactory sessionFactory) {
		metadataUtil = HibernateMetadataUtil.getInstanceForSessionFactory(sessionFactory);
	}
}
