package com.trg.search.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.trg.search.ISearch;
import com.trg.search.SearchFacade;
import com.trg.search.SearchResult;

public class HibernateSearchFacade implements SearchFacade {
	private Session session;
	private HibernateSearchProcessor processor;

	public void setSession(Session session) {
		this.session = session;
		this.processor = HibernateSearchProcessor.getInstanceForSessionFactory(session.getSessionFactory());
	}
	
	protected Session getSession() {
		return session;
	}
	
	protected HibernateSearchProcessor getProcessor() {
		return processor;
	}
	
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#search(com.trg.search.ISearch)
	 */
	@SuppressWarnings("unchecked")
	public List search(ISearch search) {
		return processor.search(getSession(), search);
	}
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#search(java.lang.Class, com.trg.search.ISearch)
	 */
	@SuppressWarnings("unchecked")
	public List search(Class<?> searchClass, ISearch search) {
		return processor.search(getSession(), searchClass, search);
	}
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#count(com.trg.search.ISearch)
	 */
	public int count(ISearch search) {
		return processor.count(getSession(), search);
	}
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#count(java.lang.Class, com.trg.search.ISearch)
	 */
	public int count(Class<?> searchClass, ISearch search) {
		return processor.count(getSession(), searchClass, search);
	}
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#searchAndCount(com.trg.search.ISearch)
	 */
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(ISearch search) {
		return processor.searchAndCount(getSession(), search);
	}
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#searchAndCount(java.lang.Class, com.trg.search.ISearch)
	 */
	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Class<?> searchClass, ISearch search) {
		return processor.searchAndCount(getSession(), searchClass, search);
	}
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#searchUnique(com.trg.search.ISearch)
	 */
	public Object searchUnique(ISearch search) {
		return processor.searchUnique(getSession(), search);
	}
	
	/* (non-Javadoc)
	 * @see com.trg.search.hibernate.SearchFacade#searchUnique(java.lang.Class, com.trg.search.ISearch)
	 */
	public Object searchUnique(Class<?> searchClass, ISearch search) {
		return processor.searchUnique(getSession(), searchClass, search);
	}
}
