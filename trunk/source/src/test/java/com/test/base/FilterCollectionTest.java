package com.test.base;

import java.util.List;

import com.test.misc.SearchTestInterface;
import com.test.model.Home;
import com.test.model.LimbedPet;
import com.test.model.Pet;
import com.trg.dao.search.Filter;
import com.trg.dao.search.Search;

public class FilterCollectionTest extends TestBase {
	protected SearchTestInterface target;

	public void testCollectionFilters() {
		initDB();
		
		Search s = new Search(Home.class);
		s.addFilterSome("residents", Filter.equal("lastName", "Beta"));
		
		List<Home> homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeB.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterSome("residents", Filter.and(Filter.equal("firstName", "Joe"), Filter.equal("lastName", "Alpha")));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeA.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterSome("residents", Filter.equal("father.father.lastName", "Alpha"));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeA.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterAll("residents", Filter.greaterThan("age", 15));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(grandpaA.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterAll("residents", Filter.like("home.address.street", "%Fulton"));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(grandpaA.getHome().getId(), homeResults.get(0).getId());
		

		// the null problem
		s.clearFilters();
		s.addFilterAll("residents", Filter.greaterThan("father.age", 50));
		assertEquals(0, target.search(s).size());
		
		
		s.clearFilters();
		s.addFilterNone("residents", Filter.equal("lastName", "Alpha"));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeB.getHome().getId(), homeResults.get(0).getId());
		
		//test nested collection filters
		
		
		//value collection
		
		s.clear();
		s.setSearchClass(LimbedPet.class);
		s.addFilterSome("limbs", Filter.equal("", "left front leg"));
		assertEquals(3, target.count(s));
		
		s.clear();
		s.setSearchClass(LimbedPet.class);
		s.addFilterSome("limbs", Filter.equal(null, "left frontish leg"));
		List<Pet> petResults = target.search(s);
		assertEquals(1, petResults.size());
		assertEquals(spiderJimmy.getId(), petResults.get(0).getId());
	}
	
	public void testEmpty() {
		
	}
	
	public void testSize() {
		
	}
	
	public void testId() {
		
	}
	
	public void testClass() {
		
	}
	
	
	//HQL functions that take collection-valued path expressions: size(),
	//minelement(), maxelement(), minindex(), maxindex(), along with the
	//special elements() and indices functions which may be quantified using
	//some, all, exists, any, in.
	//
	//.size
	//public void testIndexedCollection() {
		//initDB();
		
		//size() / .size
		
		//minelement() / maxelement() / minindex() / maxindex()
		
		//some / any - these are equivalent, true if condition is true for any element in the collection
		
		//exists - true if the collection is not empty
		
		//all - true if condition is true for all elements in the colleciton
		
		//
	//}
}
