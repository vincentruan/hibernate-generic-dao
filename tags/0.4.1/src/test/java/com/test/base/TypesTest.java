package com.test.base;

import java.util.List;

import com.test.misc.SearchTestInterface;
import com.test.model.LimbedPet;
import com.test.model.Pet;
import com.trg.dao.search.Search;

public class TypesTest extends TestBase {
	protected SearchTestInterface target;

	public void testComponent() {
		initDB();
		
		Search s = new Search(Pet.class);

		s.addFilterGreaterThan("ident.idNumber", 3333);
		s.addField("ident.name.first");
		s.setResultMode(Search.RESULT_SINGLE);
		assertEquals(spiderJimmy.getIdent().getName().getFirst(), target.searchUnique(s));
		
		s.clear();
		s.addFilterEqual("ident.name.first", "Miss");
		s.addField("ident.idNumber");
		s.setResultMode(Search.RESULT_SINGLE);
		assertEquals(catPrissy.getIdent().getIdNumber(), target.searchUnique(s));
		
		s.clear();
		s.addFilterEqual("favoritePlaymate.ident.name.first", "Jimmy");
		s.addFilterEqual("species", "cat");
		s.addField("ident.name.first");
		s.setResultMode(Search.RESULT_SINGLE);
		assertEquals(catNorman.getIdent().getName().getFirst(), target.searchUnique(s));
		
		//many-to-many ids
		//for example, querying on student.studentTeacher.id.teacher.firstName
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
	
	public void testPlymorphism() {
		initDB();
		
		Search s = new Search(Pet.class);
		List<Pet> result = target.search(s);
		assertEquals(4, result.size());
		
		s.setSearchClass(LimbedPet.class);
		result = target.search(s);
		assertEquals(3, result.size());
	}
}
