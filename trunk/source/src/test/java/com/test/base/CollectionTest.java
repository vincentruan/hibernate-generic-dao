package com.test.base;

import java.util.List;

import com.test.misc.SearchTestInterface;
import com.test.model.Pet;
import com.trg.dao.search.Search;

public class CollectionTest extends TestBase {
	protected SearchTestInterface target;

	//HQL functions that take collection-valued path expressions: size(),
	//minelement(), maxelement(), minindex(), maxindex(), along with the
	//special elements() and indices functions which may be quantified using
	//some, all, exists, any, in.
	//
	//.size
	
	public void testIndexedCollection() {
		initDB();
		
		Search s = new Search(Pet.class);
		List<Pet> result = target.search(s);
		assertEquals(4, result.size());
		
		//size() / .size
		
		//minelement() / maxelement() / minindex() / maxindex()
		
		//some / any - these are equivalent, true if condition is true for any element in the collection
		
		//exists - true if the collection is not empty
		
		//all - true if condition is true for all elements in the colleciton
		
		//
	}
}
