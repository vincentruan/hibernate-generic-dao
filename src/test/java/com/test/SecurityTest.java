package com.test;

import com.test.misc.SearchTestInterface;
import com.test.model.Person;
import com.trg.dao.search.Filter;
import com.trg.dao.search.Search;

public class SecurityTest extends TestBase {
	protected SearchTestInterface target;

	public void testInjectionAttack() {
		Search s = new Search(Person.class);
		
		try {
			s.addFetch("address foo"); //spaces are not allowed
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.setFetchMode(Search.FETCH_ARRAY);
			s.addFetch("firstName + lastName");
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addSort("Mr. Friday");
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addFilterGreaterThan("age-1", 44);
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addFilterOr(Filter.equal("firstName", "Joe"), Filter.notEqual("age()", 44));
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		//this shouldn't fail because property values are escaped
		s.clear();
		s.addFilterIn("firstName", "(select nonexistantProperty from Person)");
		target.search(s);
	}
}
