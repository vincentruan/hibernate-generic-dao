package com.test.junit;

import junit.framework.TestCase;

import com.test.model.Person;
import com.trg.dao.search.Fetch;
import com.trg.dao.search.Filter;
import com.trg.dao.search.Search;
import com.trg.dao.search.Sort;

public class SearchTest extends TestCase {
	public void testToString() {
		Search s = new Search();
		System.out.println(s);
		
		s.setSearchClass(Person.class);
		s.setDisjunction(true);
		s.setFirstResult(-44);
		s.setMaxResults(19);
		s.setPage(0);
		s.setFetchMode(Search.FETCH_ARRAY);
		
		s.addFetch("home.type");
		s.addFilterEqual("father.firstName", "ABC");
		s.addSort("home.address.type");
		
		System.out.println(s);
		
		s.setSearchClass(Search.class);
		
		s.addFetch("home", Fetch.OP_AVG);
		s.addFetch("sally's home", Fetch.OP_COUNT);
		s.addFetch("pork", Fetch.OP_COUNT_DISTINCT);
		s.addFetch("some pig", Fetch.OP_MAX);
		s.addFetch(new Fetch("", Fetch.OP_MIN));
		s.addFetch(new Fetch(null, Fetch.OP_SUM));
		s.addFetch("4th limb", 6000);
		s.addFetch((Fetch) null);
		
		s.addFilterGreaterThan("gt", "nine");
		s.addFilterLessThan("lt", 9);
		s.addFilterGreaterOrEqual("ge", 8.2293);
		s.addFilterLessOrEqual("le", null);
		s.addFilterAnd(Filter.notEqual("ne", 11), Filter.in("mine.in", 22, 23, 24, 25, "Cartons"), Filter.or(Filter.not(Filter.notIn("marm.not.in", 33, 34, 35)), Filter.like("dog.like", "mant*s"), Filter.ilike("cat.ilike", "Mon%")));
		s.addFilter(new Filter(null, null, -3));
		s.addFilter((Filter) null);
		
		s.addSort("more.sorts", true);
		s.addSort(new Sort(null, false));
		s.addSort((Sort) null);
		System.out.println(s);
	}
}
