package com.test.junit;

import java.util.List;
import java.util.Map;

import com.test.TestBase;
import com.test.dao.DirectDAO;
import com.test.dao.PersonDAO;
import com.test.model.Person;
import com.trg.dao.GeneralDAO;
import com.trg.search.Fetch;
import com.trg.search.Search;


public class FetchTest extends TestBase {
	private GeneralDAO generalDAO;

	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}
	
	private PersonDAO personDAO;
	
	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}
	
	public void testFetchEntity() {
		initDB();
		List<Person> results;
		
		Search s = new Search(Person.class);
		results = personDAO.search(s);
		
		s.addFetch("mother");
		results = personDAO.search(s);
		s.addFetch("father");
		results = personDAO.search(s);
		s.addFetch("home.address");
		results = personDAO.search(s);
		
		//TODO how can we test which entities are being loaded lazily?
		fail("Test not implemented.");
	}
	
	public void testFetchOther() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterIn("id", joeA.getId(), margretB.getId());
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
		assertEquals("Alpha", resultArray.get(0)[1]);
		assertEquals(10, resultArray.get(0)[2]);
		assertEquals("Margret", resultArray.get(1)[0]);
		assertEquals("Beta", resultArray.get(1)[1]);
		assertEquals(14, resultArray.get(1)[2]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = generalDAO.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Alpha", resultList.get(0).get(1));
		assertEquals(10, resultList.get(0).get(2));
		assertEquals("Margret", resultList.get(1).get(0));
		assertEquals("Beta", resultList.get(1).get(1));
		assertEquals(14, resultList.get(1).get(2));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("first"));
		assertEquals("Alpha", resultMap.get(0).get("lastName"));
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
		
		s.setFetchMode(Search.FETCH_SINGLE);
		List<String> resultSingle = generalDAO.search(s);
		assertEquals(2, resultSingle.size());
		assertEquals("Joe", resultSingle.get(0));
		assertEquals("Margret", resultSingle.get(1));
		
		
		
		s.clearFetch();
		s.addFetch("home.type", "homeType");
		s.addFetch("father.mother.home.address.street");
		s.addFetch("firstName", "home.type");
		
		s.setFetchMode(Search.FETCH_ARRAY);
		resultArray = generalDAO.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("house", resultArray.get(0)[0]);
		assertEquals("3290 W Fulton", resultArray.get(0)[1]);
		assertEquals("Joe", resultArray.get(0)[2]);
		assertEquals("apartment", resultArray.get(1)[0]);
		assertEquals(null, resultArray.get(1)[1]);
		assertEquals("Margret", resultArray.get(1)[2]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = generalDAO.search(s);
		assertEquals(2, resultList.size());
		assertEquals("house", resultList.get(0).get(0));
		assertEquals("3290 W Fulton", resultList.get(0).get(1));
		assertEquals("Joe", resultList.get(0).get(2));
		assertEquals("apartment", resultList.get(1).get(0));
		assertEquals(null, resultList.get(1).get(1));
		assertEquals("Margret", resultList.get(1).get(2));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("house", resultMap.get(0).get("homeType"));
		assertEquals("3290 W Fulton", resultMap.get(0).get("father.mother.home.address.street"));
		assertEquals("Joe", resultMap.get(0).get("home.type"));
		assertEquals("apartment", resultMap.get(1).get("homeType"));
		assertEquals(null, resultMap.get(1).get("father.mother.home.address.street"));
		assertEquals("Margret", resultMap.get(1).get("home.type"));
	}
	
	public void testGenericSearch() {
		initDB();
		
		Search s = new Search();
		s.addFilterIn("id", joeA.getId(), margretB.getId());
		s.addSort("firstName");
		
		List<Object[]> resultArray;
		List<List<?>> resultList;
		List<Map<String, Object>> resultMap;
		
		s.addFetch("firstName", "first");
		s.addFetch("lastName");
		s.addFetch("age");
		
		s.setFetchMode(Search.FETCH_ARRAY);
		resultArray = personDAO.searchGeneric(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Alpha", resultArray.get(0)[1]);
		assertEquals(10, resultArray.get(0)[2]);
		assertEquals("Margret", resultArray.get(1)[0]);
		assertEquals("Beta", resultArray.get(1)[1]);
		assertEquals(14, resultArray.get(1)[2]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = personDAO.searchGeneric(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Alpha", resultList.get(0).get(1));
		assertEquals(10, resultList.get(0).get(2));
		assertEquals("Margret", resultList.get(1).get(0));
		assertEquals("Beta", resultList.get(1).get(1));
		assertEquals(14, resultList.get(1).get(2));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = personDAO.searchGeneric(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("first"));
		assertEquals("Alpha", resultMap.get(0).get("lastName"));
		assertEquals(10, resultMap.get(0).get("age"));
		assertEquals("Margret", resultMap.get(1).get("first"));
		assertEquals("Beta", resultMap.get(1).get("lastName"));
		assertEquals(14, resultMap.get(1).get("age"));
		
		
		s.clearFetch();
		s.addFetch("firstName");
		
		s.setFetchMode(Search.FETCH_ARRAY);
		resultArray = personDAO.searchGeneric(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Margret", resultArray.get(1)[0]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = personDAO.searchGeneric(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Margret", resultList.get(1).get(0));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = personDAO.searchGeneric(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Margret", resultMap.get(1).get("firstName"));
		
		s.setFetchMode(Search.FETCH_SINGLE);
		List<String> resultSingle = personDAO.searchGeneric(s);
		assertEquals(2, resultSingle.size());
		assertEquals("Joe", resultSingle.get(0));
		assertEquals("Margret", resultSingle.get(1));
		
		
		
		s.clearFetch();
		s.addFetch("home.type", "homeType");
		s.addFetch("father.mother.home.address.street");
		s.addFetch("firstName", "home.type");
		
		s.setFetchMode(Search.FETCH_ARRAY);
		resultArray = personDAO.searchGeneric(s);
		assertEquals(2, resultArray.size());
		assertEquals("house", resultArray.get(0)[0]);
		assertEquals("3290 W Fulton", resultArray.get(0)[1]);
		assertEquals("Joe", resultArray.get(0)[2]);
		assertEquals("apartment", resultArray.get(1)[0]);
		assertEquals(null, resultArray.get(1)[1]);
		assertEquals("Margret", resultArray.get(1)[2]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = personDAO.searchGeneric(s);
		assertEquals(2, resultList.size());
		assertEquals("house", resultList.get(0).get(0));
		assertEquals("3290 W Fulton", resultList.get(0).get(1));
		assertEquals("Joe", resultList.get(0).get(2));
		assertEquals("apartment", resultList.get(1).get(0));
		assertEquals(null, resultList.get(1).get(1));
		assertEquals("Margret", resultList.get(1).get(2));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = personDAO.searchGeneric(s);
		assertEquals(2, resultMap.size());
		assertEquals("house", resultMap.get(0).get("homeType"));
		assertEquals("3290 W Fulton", resultMap.get(0).get("father.mother.home.address.street"));
		assertEquals("Joe", resultMap.get(0).get("home.type"));
		assertEquals("apartment", resultMap.get(1).get("homeType"));
		assertEquals(null, resultMap.get(1).get("father.mother.home.address.street"));
		assertEquals("Margret", resultMap.get(1).get("home.type"));		
	}
	
	public void testColumnOperators() {
		initDB();
		
		Search s = new Search(Person.class);
		
		s.setFetchMode(Search.FETCH_SINGLE);
		s.addFilterEqual("lastName", "Beta");
		
		//ages 10, 14, 38, 39
		
		s.addFetch("age", Fetch.OP_COUNT);
		assertEquals(4, ((Number) generalDAO.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_COUNT_DISTINCT);
		assertEquals(4, ((Number) generalDAO.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_MAX);
		assertEquals(39, generalDAO.searchUnique(s));
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_MIN);
		assertEquals(10, generalDAO.searchUnique(s));
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_SUM);
		assertEquals(101, ((Number) generalDAO.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_AVG);
		assertEquals(25.0, generalDAO.searchUnique(s));
		
		
		//38, 38, 65
		
		s.clearFetch();
		s.addFetch("mother.age", Fetch.OP_COUNT);
		assertEquals(3, ((Number) generalDAO.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("mother.age", Fetch.OP_COUNT_DISTINCT);
		assertEquals(2, ((Number) generalDAO.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("mother.age", Fetch.OP_SUM);
		assertEquals(141, ((Number) generalDAO.searchUnique(s)).intValue());
		
		s.setFetchMode(Search.FETCH_ARRAY);
		s.clearFetch();
		s.addFetch("age", Fetch.OP_SUM);
		s.addFetch("mother.age", Fetch.OP_SUM);
		Object[] arrayResult = (Object[]) generalDAO.searchUnique(s);
		assertEquals(101, ((Number) arrayResult[0]).intValue());
		assertEquals(141, ((Number) arrayResult[1]).intValue());
		
		s.setFetchMode(Search.FETCH_LIST);
		List listResult = (List) generalDAO.searchUnique(s);
		assertEquals(101, ((Number) listResult.get(0)).intValue());
		assertEquals(141, ((Number) listResult.get(1)).intValue());
		
		s.setFetchMode(Search.FETCH_MAP);
		Map mapResult = (Map) generalDAO.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("age")).intValue());
		assertEquals(141, ((Number) mapResult.get("mother.age")).intValue());
		
		try {
			s.setFetchMode(Search.FETCH_ENTITY);
			generalDAO.searchUnique(s);
			fail("Searching with fetch operators and fetch mode = FETCH_ENTITY should throw an Error");
		} catch (Error err) {
			
		}
		
		s.setFetchMode(Search.FETCH_MAP);
		s.clearFetch();
		s.addFetch("age", Fetch.OP_SUM, "myAge");
		s.addFetch("mother.age", Fetch.OP_SUM, "myMomsAge");
		mapResult = (Map) generalDAO.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("myAge")).intValue());
		assertEquals(141, ((Number) mapResult.get("myMomsAge")).intValue());		
	}
}
