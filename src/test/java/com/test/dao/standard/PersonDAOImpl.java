package com.test.dao.standard;

import java.util.List;

import com.test.model.Person;
import com.trg.dao.dao.standard.GenericDAOImpl;
import com.trg.dao.search.Search;

public class PersonDAOImpl extends GenericDAOImpl<Person, Long> implements PersonDAO {

	public List<Person> findByName(String firstName, String lastName) {
		return search(new Search().addFilterEqual("firstName", firstName).addFilterEqual("lastName", lastName));
	}

}
