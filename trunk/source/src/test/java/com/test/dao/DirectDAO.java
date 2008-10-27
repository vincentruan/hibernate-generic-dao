package com.test.dao;

import java.util.List;

import com.trg.dao.HibernateDAOHQLImpl;

public class DirectDAO extends HibernateDAOHQLImpl {
	public List<?> executeQuery(String hql) {
		return getHibernateTemplate().find(hql);
	}
	
	public void disconnectSession() {
		getSession().disconnect();
	}
}
