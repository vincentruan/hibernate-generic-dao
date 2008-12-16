package com.test.junit.search;

import com.test.TestBase;
import com.test.dao.PersonDAO;
import com.test.model.Person;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

public class SearchResultTest extends TestBase {
	private PersonDAO personDAO;

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}
	
	public void testGeneric() {
		initDB();
		
		Search s = new Search();
		s.addFilterLessThan("lastName", "Balloons");
		
		assertEquals(6, personDAO.count(s));
		
		SearchResult<Person> result = personDAO.searchAndCount(s);
		assertEquals(s, result.search);
		assertEquals(s.getFirstResult(), result.firstResult);
		assertEquals(s.getMaxResults(), result.maxResults);
		assertEquals(s.getPage(), result.page);
		assertEquals(6, result.totalLength);
		assertListEqual(new Person[] { joeA, sallyA, papaA, mamaA, grandpaA, grandmaA }, result.results);
	}
	
}
