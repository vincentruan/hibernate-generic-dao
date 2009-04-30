package com.test.dao.original;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.NonUniqueResultException;

import com.test.model.Person;
import com.trg.search.Search;
import com.trg.search.SearchResult;

public class PersonService {
	PersonDAO dao;
	
	@Resource(name="origPersonDAO")
	public void setPersonDAO(PersonDAO dao) {
		this.dao = dao;
	}
	
	public void create(Person object) {
		dao.create(object);
	}

	public boolean createOrUpdate(Person object) {
		return dao.createOrUpdate(object);
	}

	public boolean deleteById(Long... id) {
		return dao.deleteById(id[0]);
	}

	//Test calling method with used varargs
	public boolean deleteEntity(Person object) {
		return dao.deleteEntity(object);
	}

	//Test calling method with unused varargs
	public Person fetch(Long id, String... foo) {
		return dao.fetch(id);
	}

	public List<Person> fetchAll() {
		return dao.fetchAll();
	}

	public void update(Person object) {
		dao.update(object);
	}

	public List<Person> search(Search search) {
		return dao.search(search);
	}

	public int searchLength(Search search) {
		return dao.count(search);
	}

	public SearchResult<Person> searchAndLength(Search search) {
		return dao.searchAndCount(search);
	}

	@SuppressWarnings("unchecked")
	public List searchGeneric(Search search) {
		return dao.searchGeneric(search);
	}

	public Object searchUnique(Search search) throws NonUniqueResultException {
		return dao.searchUnique(search);
	}

	public boolean isConnected(Object object) {
		return dao.isConnected(object);
	}

	public void flush() {
		dao.flush();
	}

	public void refresh(Object object) {
		dao.refresh(object);
	}
}
