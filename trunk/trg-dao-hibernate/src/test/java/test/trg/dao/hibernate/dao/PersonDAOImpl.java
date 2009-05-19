package test.trg.dao.hibernate.dao;

import java.util.List;

import test.trg.model.Person;

import com.trg.search.Search;

public class PersonDAOImpl extends BaseDAOImpl<Person, Long> implements PersonDAO {

	public List<Person> findByName(String firstName, String lastName) {
		//If firstName or lastName are null, the corresponding filter will be ignored.
		return search(new Search().addFilterEqual("firstName", firstName).addFilterEqual("lastName", lastName));
	}

}
