package com.trg.dao.hibernate;

import com.trg.dao.SearchToQLProcessor;

public class HibernateSearchToQLProcessor extends SearchToQLProcessor {

	protected HibernateSearchToQLProcessor() {
		super(QLTYPE_HQL);
	}
	
}
