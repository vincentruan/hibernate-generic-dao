package com.test.junit;

import junit.framework.TestCase;

import com.test.model.Person;
import com.trg.dao.search.Field;
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
		s.setResultMode(Search.RESULT_ARRAY);
		
		s.addField("home.type");
		s.addFilterEqual("father.firstName", "ABC");
		s.addSortAsc("home.address.type");
		
		System.out.println(s);
		
		s.setSearchClass(Search.class);
		
		s.addField("home", Field.OP_AVG);
		s.addField("sally's home", Field.OP_COUNT);
		s.addField("pork", Field.OP_COUNT_DISTINCT);
		s.addField("some pig", Field.OP_MAX);
		s.addField(new Field("", Field.OP_MIN));
		s.addField(new Field(null, Field.OP_SUM));
		s.addField("4th limb", 6000);
		s.addField((Field) null);
		
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
