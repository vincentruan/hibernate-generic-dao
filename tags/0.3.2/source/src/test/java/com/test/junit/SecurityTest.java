package com.test.junit;

import com.test.TestBase;
import com.test.model.Person;
import com.trg.dao.GeneralDAO;
import com.trg.search.Filter;
import com.trg.search.Search;

public class SecurityTest extends TestBase {
	private GeneralDAO generalDAO;

	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	public void testInjectionAttack() {
		Search s = new Search(Person.class);
		
		try {
			s.addFetch("address foo"); //spaces are not allowed
			generalDAO.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.setFetchMode(Search.FETCH_ARRAY);
			s.addFetch("firstName + lastName");
			generalDAO.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addSort("Mr. Friday");
			generalDAO.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addFilterGreaterThan("age-1", 44);
			generalDAO.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addFilterOr(Filter.equal("firstName", "Joe"), Filter.notEqual("age()", 44));
			generalDAO.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		//this shouldn't fail because property values are escaped
		s.clear();
		s.addFilterIn("firstName", "(select nonexistantProperty from Person)");
		generalDAO.search(s);
	}
}
