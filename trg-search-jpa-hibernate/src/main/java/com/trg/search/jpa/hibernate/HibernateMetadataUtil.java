package com.trg.search.jpa.hibernate;

import org.hibernate.ejb.HibernateEntityManagerFactory;

import com.trg.search.MetadataUtil;

public class HibernateMetadataUtil {
	public static MetadataUtil getInstanceForEntityManagerFactory(HibernateEntityManagerFactory emf) {
		return com.trg.search.hibernate.HibernateMetadataUtil.getInstanceForSessionFactory(emf.getSessionFactory());
	}
}
