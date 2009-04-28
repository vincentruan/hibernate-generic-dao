package com.test.base;

import com.test.misc.SearchTestInterface;
import com.test.model.Person;
import com.trg.dao.search.Search;
import com.trg.dao.search.SearchResult;

public class SearchResultTest extends TestBase {
	protected SearchTestInterface target;

	@SuppressWarnings("unchecked")
	public void test() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterLessThan("lastName", "Balloons");
		
		assertEquals(6, target.count(s));
		
		SearchResult<Person> result = target.searchAndCount(s);
		assertEquals(6, result.getTotalCount());
		assertListEqual(new Person[] { joeA, sallyA, papaA, mamaA, grandpaA, grandmaA }, result.getResult());
	}
	
}
