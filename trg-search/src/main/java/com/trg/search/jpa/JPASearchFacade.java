package com.trg.search.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.trg.search.ExampleOptions;
import com.trg.search.Filter;
import com.trg.search.ISearch;
import com.trg.search.SearchFacade;
import com.trg.search.SearchResult;

/**
 * <p>
 * Hibernate implementation of SearchFacade.
 * 
 * <p>
 * The SessionFactory must be set before an instance of this class can be used.
 * The <code>getCurrentSession()</code> method of the SessionFactory is used
 * when a session is needed.
 * 
 * <p>To change this default behavior, you can override the protected {@link #getSession()} method.
 * 
 * @author dwolverton
 */
public class JPASearchFacade implements SearchFacade {

	protected JPASearchProcessor processor;
	
	protected EntityManager entityManager; 
	
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		this.processor = searchProcessor;
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public List search(ISearch search) {
		return processor.search(entityManager, search);
	}

	@SuppressWarnings("unchecked")
	public List search(Class<?> searchClass, ISearch search) {
		return processor.search(entityManager, searchClass, search);
	}

	public int count(ISearch search) {
		return processor.count(entityManager, search);
	}

	public int count(Class<?> searchClass, ISearch search) {
		return processor.count(entityManager, searchClass, search);
	}

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(ISearch search) {
		return processor.searchAndCount(entityManager, search);
	}

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Class<?> searchClass, ISearch search) {
		return processor.searchAndCount(entityManager, searchClass, search);
	}

	public Object searchUnique(ISearch search) {
		return processor.searchUnique(entityManager, search);
	}

	public Object searchUnique(Class<?> searchClass, ISearch search) {
		return processor.searchUnique(entityManager, searchClass, search);
	}

	public Filter getFilterFromExample(Object example) {
		return processor.getFilterFromExample(example);
	}

	public Filter getFilterFromExample(Object example, ExampleOptions options) {
		return processor.getFilterFromExample(example, options);
	}
}
