package com.test.dao.standard;

import java.util.List;

import com.test.model.Person;
import com.trg.search.Search;

public class PersonDAOImpl extends BaseGenericDAOImpl<Person, Long> implements PersonDAO {

	public List<Person> findByName(String firstName, String lastName) {
		//If firstName or lastName are null, the corresponding filter will be ignored.
		return search(new Search().addFilterEqual("firstName", firstName).addFilterEqual("lastName", lastName));
	}

}
