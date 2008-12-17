package com.test.base;

import com.test.misc.SearchTestInterface;
import com.test.model.Pet;
import com.trg.dao.search.Search;

public class ComponentTest extends TestBase {
	protected SearchTestInterface target;

	public void testComponent() {
		initDB();
		
		Search s = new Search(Pet.class);

		s.addFilterGreaterThan("ident.idNumber", 3333);
		s.addFetch("ident.name.first");
		s.setFetchMode(Search.FETCH_SINGLE);
		assertEquals(spiderJimmy.getIdent().getName().getFirst(), target.searchUnique(s));
		
		s.clear();
		s.addFilterEqual("ident.name.first", "Miss");
		s.addFetch("ident.idNumber");
		s.setFetchMode(Search.FETCH_SINGLE);
		assertEquals(catPrissy.getIdent().getIdNumber(), target.searchUnique(s));
		
		s.clear();
		s.addFilterEqual("favoritePlaymate.ident.name.first", "Jimmy");
		s.addFilterEqual("species", "cat");
		s.addFetch("ident.name.first");
		s.setFetchMode(Search.FETCH_SINGLE);
		assertEquals(catNorman.getIdent().getName().getFirst(), target.searchUnique(s));
	}
}
