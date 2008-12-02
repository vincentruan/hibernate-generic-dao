package com.trg.dao.hibernate;

import com.trg.dao.SearchToQLProcessor;

/**
 * Implementation of SearchToQLProcessor that generates HQL
 * @author dwolverton
 */
public class HibernateSearchToQLProcessor extends SearchToQLProcessor {

	protected HibernateSearchToQLProcessor() {
		super(QLTYPE_HQL);
	}
	
}
