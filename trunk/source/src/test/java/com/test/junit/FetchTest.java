package com.test.junit;

import java.util.List;
import java.util.Map;

import com.test.dao.PersonDAO;
import com.test.model.Person;
import com.trg.dao.GeneralDAO;
import com.trg.search.Search;


public class FetchTest extends TestBase {
	private PersonDAO personDAO;
	private GeneralDAO generalDAO;

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}
	
	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}


	public void testFetchEntity() {
	}
	
	public void testFetchOther() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterEqual("lastName", "Beta");
		s.addFilterLessThan("age", 20);
		s.addSort("firstName");
		
		List<Object[]> resultArray;
		List<List<?>> resultList;
		List<Map<String, Object>> resultMap;
		
		s.addFetch("firstName", "first");
		s.addFetch("lastName");
		s.addFetch("age");
		
		s.setFetchMode(Search.FETCH_ARRAY);
		resultArray = generalDAO.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Beta", resultArray.get(0)[1]);
		assertEquals(10, resultArray.get(0)[2]);
		assertEquals("Margret", resultArray.get(1)[0]);
		assertEquals("Beta", resultArray.get(1)[1]);
		assertEquals(14, resultArray.get(1)[2]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = generalDAO.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Beta", resultList.get(0).get(1));
		assertEquals(10, resultList.get(0).get(2));
		assertEquals("Margret", resultList.get(1).get(0));
		assertEquals("Beta", resultList.get(1).get(1));
		assertEquals(14, resultList.get(1).get(2));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("first"));
		assertEquals("Beta", resultMap.get(0).get("lastName"));
		assertEquals(10, resultMap.get(0).get("age"));
		assertEquals("Margret", resultMap.get(1).get("first"));
		assertEquals("Beta", resultMap.get(1).get("lastName"));
		assertEquals(14, resultMap.get(1).get("age"));
		
		
		s.clearFetch();
		s.addFetch("firstName");
		
		s.setFetchMode(Search.FETCH_ARRAY);
		resultArray = generalDAO.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Margret", resultArray.get(1)[0]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = generalDAO.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Margret", resultList.get(1).get(0));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Margret", resultMap.get(1).get("firstName"));
	}
}
