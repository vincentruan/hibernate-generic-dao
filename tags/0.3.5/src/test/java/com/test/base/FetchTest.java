package com.test.base;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.hibernate.proxy.HibernateProxy;

import com.test.base.TestBase;
import com.test.misc.SearchTestInterface;
import com.test.model.Person;
import com.trg.dao.search.Fetch;
import com.trg.dao.search.Search;


public class FetchTest extends TestBase {
	protected SearchTestInterface target;
	
	@SuppressWarnings("unchecked")
	public void testFetchEntity() throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		initDB();
		List<Person> results;
		
		Search s = new Search(Person.class);
		s.addSort("age");
		results = target.search(s);
		assertFalse(isEntityFetched(results.get(3).getHome()));
		
		s.addFetch("mother");
		results = target.search(s);
		assertFalse(isEntityFetched(results.get(3).getHome()));
		
		s.addFetch("father");
		
		s.addFetch("home.address");
		results = target.search(s);
		assertTrue(isEntityFetched(results.get(3).getHome()));
	}
	
	private boolean isEntityFetched(Object entity) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return !((HibernateProxy) entity).getHibernateLazyInitializer().isUninitialized();
	}
	
	@SuppressWarnings("unchecked")
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
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Alpha", resultArray.get(0)[1]);
		assertEquals(10, resultArray.get(0)[2]);
		assertEquals("Margret", resultArray.get(1)[0]);
		assertEquals("Beta", resultArray.get(1)[1]);
		assertEquals(14, resultArray.get(1)[2]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Alpha", resultList.get(0).get(1));
		assertEquals(10, resultList.get(0).get(2));
		assertEquals("Margret", resultList.get(1).get(0));
		assertEquals("Beta", resultList.get(1).get(1));
		assertEquals(14, resultList.get(1).get(2));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = target.search(s);
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
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Margret", resultArray.get(1)[0]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Margret", resultList.get(1).get(0));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Margret", resultMap.get(1).get("firstName"));
		
		s.setFetchMode(Search.FETCH_SINGLE);
		List<String> resultSingle = target.search(s);
		assertEquals(2, resultSingle.size());
		assertEquals("Joe", resultSingle.get(0));
		assertEquals("Margret", resultSingle.get(1));
		
		
		
		s.clearFetch();
		s.addFetch("home.type", "homeType");
		s.addFetch("father.mother.home.address.street");
		s.addFetch("firstName", "home.type");
		
		s.setFetchMode(Search.FETCH_ARRAY);
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("house", resultArray.get(0)[0]);
		assertEquals("3290 W Fulton", resultArray.get(0)[1]);
		assertEquals("Joe", resultArray.get(0)[2]);
		assertEquals("apartment", resultArray.get(1)[0]);
		assertEquals(null, resultArray.get(1)[1]);
		assertEquals("Margret", resultArray.get(1)[2]);
		
		s.setFetchMode(Search.FETCH_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("house", resultList.get(0).get(0));
		assertEquals("3290 W Fulton", resultList.get(0).get(1));
		assertEquals("Joe", resultList.get(0).get(2));
		assertEquals("apartment", resultList.get(1).get(0));
		assertEquals(null, resultList.get(1).get(1));
		assertEquals("Margret", resultList.get(1).get(2));
		
		s.setFetchMode(Search.FETCH_MAP);
		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("house", resultMap.get(0).get("homeType"));
		assertEquals("3290 W Fulton", resultMap.get(0).get("father.mother.home.address.street"));
		assertEquals("Joe", resultMap.get(0).get("home.type"));
		assertEquals("apartment", resultMap.get(1).get("homeType"));
		assertEquals(null, resultMap.get(1).get("father.mother.home.address.street"));
		assertEquals("Margret", resultMap.get(1).get("home.type"));
	}
	
	@SuppressWarnings("unchecked")
	public void testColumnOperators() {
		initDB();
		
		Search s = new Search(Person.class);
		
		s.setFetchMode(Search.FETCH_SINGLE);
		s.addFilterEqual("lastName", "Beta");
		
		//ages 10, 14, 38, 39
		
		s.addFetch("age", Fetch.OP_COUNT);
		assertEquals(4, ((Number) target.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_COUNT_DISTINCT);
		assertEquals(4, ((Number) target.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_MAX);
		assertEquals(39, target.searchUnique(s));
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_MIN);
		assertEquals(10, target.searchUnique(s));
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_SUM);
		assertEquals(101, ((Number) target.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("age", Fetch.OP_AVG);
		assertEquals(25, Math.round((Double) target.searchUnique(s)));
		
		
		//38, 38, 65
		
		s.clearFetch();
		s.addFetch("mother.age", Fetch.OP_COUNT);
		assertEquals(3, ((Number) target.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("mother.age", Fetch.OP_COUNT_DISTINCT);
		assertEquals(2, ((Number) target.searchUnique(s)).intValue());
		
		s.clearFetch();
		s.addFetch("mother.age", Fetch.OP_SUM);
		assertEquals(141, ((Number) target.searchUnique(s)).intValue());
		
		s.setFetchMode(Search.FETCH_ARRAY);
		s.clearFetch();
		s.addFetch("age", Fetch.OP_SUM);
		s.addFetch("mother.age", Fetch.OP_SUM);
		Object[] arrayResult = (Object[]) target.searchUnique(s);
		assertEquals(101, ((Number) arrayResult[0]).intValue());
		assertEquals(141, ((Number) arrayResult[1]).intValue());
		
		s.setFetchMode(Search.FETCH_LIST);
		List listResult = (List) target.searchUnique(s);
		assertEquals(101, ((Number) listResult.get(0)).intValue());
		assertEquals(141, ((Number) listResult.get(1)).intValue());
		
		s.setFetchMode(Search.FETCH_MAP);
		Map mapResult = (Map) target.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("age")).intValue());
		assertEquals(141, ((Number) mapResult.get("mother.age")).intValue());
		
		try {
			s.setFetchMode(Search.FETCH_ENTITY);
			target.searchUnique(s);
			fail("Searching with fetch operators and fetch mode = FETCH_ENTITY should throw an Error");
		} catch (Error err) {
			
		}
		
		s.setFetchMode(Search.FETCH_MAP);
		s.clearFetch();
		s.addFetch("age", Fetch.OP_SUM, "myAge");
		s.addFetch("mother.age", Fetch.OP_SUM, "myMomsAge");
		mapResult = (Map) target.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("myAge")).intValue());
		assertEquals(141, ((Number) mapResult.get("myMomsAge")).intValue());		
	}
}
