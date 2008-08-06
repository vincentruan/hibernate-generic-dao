package com.test.junit;

import java.util.List;
import java.util.Map;

import com.test.model.Person;
import com.trg.dao.GeneralDAO;
import com.trg.dao.GenericDAO;
import com.trg.search.Search;

public class TrickyIssueTest extends TestBase {
	private GeneralDAO generalDAO;
	
	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	/**
	 * The alias error occurs when using fetch mode FETCH_MAP. It occurs when
	 * there is a fetch that has a key with no "." in it and is the same as a
	 * property that is used in a filter.
	 */
	public void testAliasError() {
		initDB();
		
		List<Map<String, Object>> resultMap;
		
		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Joe");
		s.addFilterEqual("age", 10);
		s.addSort("lastName");
		s.setFetchMode(Search.FETCH_MAP);
		
		s.addFetch("firstName");
		
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		
		
		s.addFetch("lastName");
		
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Alpha", resultMap.get(0).get("lastName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		assertEquals("Beta", resultMap.get(1).get("lastName"));
		
		
		s.clearFetch();
		s.addFetch("firstName", "firstName");
		s.addFetch("age"); //this uses age for the property and key
		s.addFetch("lastName", "Last Name");
		s.addFetch("mother.lastName");
		
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals(10, resultMap.get(0).get("age"));
		assertEquals("Alpha", resultMap.get(0).get("Last Name"));
		assertEquals("Alpha", resultMap.get(0).get("mother.lastName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		assertEquals(10, resultMap.get(1).get("age"));
		assertEquals("Beta", resultMap.get(1).get("Last Name"));
		assertEquals("Beta", resultMap.get(1).get("mother.lastName"));
	}

	public void testEagerFetchingPagingError() {
		
	}
}
