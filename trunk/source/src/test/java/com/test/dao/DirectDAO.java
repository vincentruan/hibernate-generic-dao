package com.test.dao;

import java.util.List;

import com.trg.dao.hibernate.HibernateDAOHQLImpl;

public class DirectDAO extends HibernateDAOHQLImpl {
	public List<?> executeQuery(String hql) {
		return getSession().createQuery(hql).list();
	}
	
	public void disconnectSession() {
		getSession().disconnect();
	}
}
