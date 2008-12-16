package com.test.junit.hibernate;

import com.test.misc.HibernateBaseDAOTester;

public class TrickyIssueTest extends com.test.TrickyIssueTest {
	public void setHibernateBaseDAOTester(HibernateBaseDAOTester target) {
		this.target = target;
	}
}
