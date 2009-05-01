package test.trg.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.proxy.HibernateProxy;

import test.trg.shared.TestBase;
import test.trg.shared.model.Person;

import com.trg.search.Field;
import com.trg.search.Search;
import com.trg.search.SearchFacade;

public class FieldAndFetchTest extends TestBase {
	protected SearchFacade target;

	@SuppressWarnings("unchecked")
	public void testFetches() {
		initDB();
		List<Person> results;

		Search s = new Search(Person.class);
		s.addSortAsc("age");
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

	private boolean isEntityFetched(Object entity) {
		return !((HibernateProxy) entity).getHibernateLazyInitializer().isUninitialized();
	}

	@SuppressWarnings("unchecked")
	public void testFields() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterIn("id", joeA.getId(), margretB.getId());
		s.addSortAsc("firstName");

		List<Object[]> resultArray;
		List<List<?>> resultList;
		List<Map<String, Object>> resultMap;

		s.addField("firstName", "first");
		s.addField("lastName");
		s.addField("age");

		s.setResultMode(Search.RESULT_ARRAY);
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Alpha", resultArray.get(0)[1]);
		assertEquals(10, resultArray.get(0)[2]);
		assertEquals("Margret", resultArray.get(1)[0]);
		assertEquals("Beta", resultArray.get(1)[1]);
		assertEquals(14, resultArray.get(1)[2]);

		s.setResultMode(Search.RESULT_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Alpha", resultList.get(0).get(1));
		assertEquals(10, resultList.get(0).get(2));
		assertEquals("Margret", resultList.get(1).get(0));
		assertEquals("Beta", resultList.get(1).get(1));
		assertEquals(14, resultList.get(1).get(2));

		s.setResultMode(Search.RESULT_MAP);
		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("first"));
		assertEquals("Alpha", resultMap.get(0).get("lastName"));
		assertEquals(10, resultMap.get(0).get("age"));
		assertEquals("Margret", resultMap.get(1).get("first"));
		assertEquals("Beta", resultMap.get(1).get("lastName"));
		assertEquals(14, resultMap.get(1).get("age"));

		s.clearFields();
		s.addField("firstName");

		s.setResultMode(Search.RESULT_ARRAY);
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Margret", resultArray.get(1)[0]);

		s.setResultMode(Search.RESULT_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Margret", resultList.get(1).get(0));

		s.setResultMode(Search.RESULT_MAP);
		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Margret", resultMap.get(1).get("firstName"));

		s.setResultMode(Search.RESULT_SINGLE);
		List<String> resultSingle = target.search(s);
		assertEquals(2, resultSingle.size());
		assertEquals("Joe", resultSingle.get(0));
		assertEquals("Margret", resultSingle.get(1));

		s.clearFields();
		s.addField("home.type", "homeType");
		s.addField("father.mother.home.address.street");
		s.addField("firstName", "home.type");

		s.setResultMode(Search.RESULT_ARRAY);
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("house", resultArray.get(0)[0]);
		assertEquals("3290 W Fulton", resultArray.get(0)[1]);
		assertEquals("Joe", resultArray.get(0)[2]);
		assertEquals("apartment", resultArray.get(1)[0]);
		assertEquals(null, resultArray.get(1)[1]);
		assertEquals("Margret", resultArray.get(1)[2]);

		s.setResultMode(Search.RESULT_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("house", resultList.get(0).get(0));
		assertEquals("3290 W Fulton", resultList.get(0).get(1));
		assertEquals("Joe", resultList.get(0).get(2));
		assertEquals("apartment", resultList.get(1).get(0));
		assertEquals(null, resultList.get(1).get(1));
		assertEquals("Margret", resultList.get(1).get(2));

		s.setResultMode(Search.RESULT_MAP);
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

		s.setResultMode(Search.RESULT_SINGLE);
		s.addFilterEqual("lastName", "Beta");

		// ages 10, 14, 38, 39

		s.addField("age", Field.OP_COUNT);
		assertEquals(4, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("age", Field.OP_COUNT_DISTINCT);
		assertEquals(4, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("age", Field.OP_MAX);
		assertEquals(39, target.searchUnique(s));

		s.clearFields();
		s.addField("age", Field.OP_MIN);
		assertEquals(10, target.searchUnique(s));

		s.clearFields();
		s.addField("age", Field.OP_SUM);
		assertEquals(101, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("age", Field.OP_AVG);
		assertEquals(25, Math.round((Double) target.searchUnique(s)));

		// 38, 38, 65

		s.clearFields();
		s.addField("mother.age", Field.OP_COUNT);
		assertEquals(3, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("mother.age", Field.OP_COUNT_DISTINCT);
		assertEquals(2, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("mother.age", Field.OP_SUM);
		assertEquals(141, ((Number) target.searchUnique(s)).intValue());

		s.setResultMode(Search.RESULT_ARRAY);
		s.clearFields();
		s.addField("age", Field.OP_SUM);
		s.addField("mother.age", Field.OP_SUM);
		Object[] arrayResult = (Object[]) target.searchUnique(s);
		assertEquals(101, ((Number) arrayResult[0]).intValue());
		assertEquals(141, ((Number) arrayResult[1]).intValue());

		s.setResultMode(Search.RESULT_LIST);
		List listResult = (List) target.searchUnique(s);
		assertEquals(101, ((Number) listResult.get(0)).intValue());
		assertEquals(141, ((Number) listResult.get(1)).intValue());

		s.setResultMode(Search.RESULT_MAP);
		Map mapResult = (Map) target.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("age")).intValue());
		assertEquals(141, ((Number) mapResult.get("mother.age")).intValue());

		s.setResultMode(Search.RESULT_MAP);
		s.clearFields();
		s.addField("age", Field.OP_SUM, "myAge");
		s.addField("mother.age", Field.OP_SUM, "myMomsAge");
		mapResult = (Map) target.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("myAge")).intValue());
		assertEquals(141, ((Number) mapResult.get("myMomsAge")).intValue());
	}
	
	public void testDistinct() {
		initDB();
		
		Search s = new Search(Person.class);
		s.setDistinct(true);
		
		s.addFilterLessOrEqual("age", 15);
		assertEquals(4, target.count(s));
		assertListEqual(target.search(s), joeA, joeB, sallyA, margretB);
		
		s.clear();
		s.setDistinct(true);
		s.addField("lastName");
		assertEquals(2, target.count(s));
		assertListEqual(target.search(s), "Alpha", "Beta");
		
		s.clear();
		s.setDistinct(true);
		s.addField("mother");
		assertEquals(3, target.count(s));
		assertListEqual(target.search(s), mamaA, mamaB, grandmaA);
		
		s.clearFields();
		s.addField("mother.firstName");
		s.addField("mother.lastName");
		try {
			assertEquals(4, target.count(s));
			fail();
		} catch (IllegalArgumentException ex) {
			//We don't support distinct counts with multiple fields at this time
		}
		List<Object[]> results = target.search(s);
		List<String> results2 = new ArrayList<String>(results.size());
		for (Object[] a : results) {
			results2.add((String)a[0] + " " + (String)a[1]);
		}
		assertListEqual(results2, "null null", "Mama Alpha", "Mama Beta", "Grandma Alpha");
		
		//This is a miscellaneous test. When column operators are defined, the count should always be 1.
		s.clear();
		s.addField("age", Field.OP_COUNT);
		assertEquals(1, target.count(s));
	}

	public void testResultModeAuto() {
		initDB();

		Search s = new Search(Person.class).addFilterEqual("firstName", "Margret");

		// SINGLE
		assertEquals(margretB, target.searchUnique(s));

		s.addField("firstName");
		assertEquals("Margret", target.searchUnique(s));

		// ARRAY
		s.addField("lastName");
		Object[] strResults = (Object[]) target.searchUnique(s);
		assertEquals("Margret", strResults[0]);
		assertEquals("Beta", strResults[1]);

		// MAP
		s.clearFields();
		s.addField("firstName", "fn");
		Map<String, Object> mapResults = (Map<String, Object>) target.searchUnique(s);
		assertEquals("Margret", mapResults.get("fn"));

		s.addField("lastName");
		mapResults = (Map<String, Object>) target.searchUnique(s);
		assertEquals("Margret", mapResults.get("fn"));
		assertEquals("Beta", mapResults.get("lastName"));
	}

	public void testCombineFieldAndFetch() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Sally");
		s.addField("mother");

		Person p = (Person) target.searchUnique(s);
		assertFalse(isEntityFetched(p.getHome()));

		s.addFetch("mother.home");
		p = (Person) target.searchUnique(s);
		assertTrue(isEntityFetched(p.getHome()));

		// with another field
		s.addField("mother.firstName");
		p = (Person) ((Object[]) target.searchUnique(s))[0];
		assertTrue(isEntityFetched(p.getHome()));

		// fetch with no applicable fields (it should ignore these. just make
		// sure no error is thrown)
		s.removeField("mother");
		target.searchUnique(s);

		s.clearFields();
		s.addField("age", Field.OP_MAX);
	}
}
