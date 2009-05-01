package com.trg.search.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.trg.search.ExampleOptions;
import com.trg.search.Filter;
import com.trg.search.ISearch;
import com.trg.search.SearchFacade;
import com.trg.search.SearchResult;

public class HibernateSearchFacade implements SearchFacade {
	private SessionFactory sessionFactory;
	private HibernateSearchProcessor processor;

	public HibernateSearchFacade() {
	}
	
	public HibernateSearchFacade(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.processor = HibernateSearchProcessor.getInstanceForSessionFactory(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	protected HibernateSearchProcessor getProcessor() {
		return processor;
	}
	
	
	@SuppressWarnings("unchecked")
	public List search(ISearch search) {
		return processor.search(getSession(), search);
	}
	
	@SuppressWarnings("unchecked")
	public List search(Class<?> searchClass, ISearch search) {
		return processor.search(getSession(), searchClass, search);
	}
	
	public int count(ISearch search) {
		return processor.count(getSession(), search);
	}
	
	public int count(Class<?> searchClass, ISearch search) {
		return processor.count(getSession(), searchClass, search);
	}
	
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(ISearch search) {
		return processor.searchAndCount(getSession(), search);
	}
	
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Class<?> searchClass, ISearch search) {
		return processor.searchAndCount(getSession(), searchClass, search);
	}
	
	public Object searchUnique(ISearch search) {
		return processor.searchUnique(getSession(), search);
	}
	
	public Object searchUnique(Class<?> searchClass, ISearch search) {
		return processor.searchUnique(getSession(), searchClass, search);
	}
	
	public Filter getFilterFromExample(Object example) {
		return processor.getFilterFromExample(example);
	}
	
	public Filter getFilterFromExample(Object example, ExampleOptions options) {
		return processor.getFilterFromExample(example, options);
	}
}
