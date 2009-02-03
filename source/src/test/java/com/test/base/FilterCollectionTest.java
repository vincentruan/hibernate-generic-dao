package com.test.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.test.misc.SearchTestInterface;
import com.test.model.Home;
import com.test.model.LimbedPet;
import com.test.model.Person;
import com.test.model.Pet;
import com.test.model.Recipe;
import com.test.model.RecipeIngredient;
import com.test.model.Store;
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
		//TODO fails
		
		s.clearFilters();
		s.addFilterNone("residents", Filter.equal("lastName", "Alpha"));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeB.getHome().getId(), homeResults.get(0).getId());
		
		//TODO test nested collection filters
		
		
		//value collection
		if (false) {
			//It appears HQL does not support any way to do this with value collections.
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
	}
	
	public void testEmpty() {
		initDB();
		
		//with entity
		Search s = new Search(Person.class);
		s.addFilterEmpty("father");
		assertListEqual(target.search(s), grandpaA, grandmaA, mamaA, papaB);

		s.clear();
		s.addFilterEmpty("father.id");
		assertListEqual(target.search(s), grandpaA, grandmaA, mamaA, papaB);
		
		s.clear();
		s.addFilterNotEmpty("father");
		assertListEqual(target.search(s), mamaB, papaA, joeA, joeB, margretB, sallyA);
		
		//with int and string value
		Person pete = (Person) sessionFactory.getCurrentSession().get(Person.class, papaA.getId());
		pete.setAge(null);
		pete.setFirstName(null);
		pete.setLastName("");
		
		s.clear();
		s.addFilterEmpty("age");
		assertListEqual(target.search(s), pete);
		
		s.clear();
		s.addFilterEmpty("firstName");
		assertListEqual(target.search(s), pete);

		s.clear();
		s.addFilterEmpty("lastName");
		assertListEqual(target.search(s), pete);
		
		s.clear();
		s.addFilterNotEmpty("lastName");
		assertListEqual(target.search(s), grandmaA, grandpaA, papaB, mamaA, mamaB, joeA, joeB, sallyA, margretB);
		
		pete.setAge(0);
		s.clear();
		s.addFilterEmpty("age");
		assertListEqual(target.search(s)); //no matches, 0 is not empty
		
		//with value collection
		s.clear();
		s.setSearchClass(LimbedPet.class);
		s.addFilterEmpty("limbs");
		assertListEqual(target.search(s)); //no results
		
		LimbedPet cat = copy(catPrissy);
		cat.setLimbs(new ArrayList<String>(0));
		getSession().update(cat);
		
		assertListEqual(target.search(s), cat);
		
		cat.setLimbs(null);
		
		assertListEqual(target.search(s), cat);
		
		//with one-to-many
		
		Recipe air = new Recipe();
		air.setTitle("Air");
		air.setIngredients(new HashSet<RecipeIngredient>());
		getSession().save(air);
		
		s.clear();
		s.setSearchClass(Recipe.class);
		s.addFilterEmpty("ingredients");
		assertListEqual(target.search(s), air);
		
		s.clear();
		s.addFilterNotEmpty("ingredients");
		assertListEqual(target.search(s), recipes.toArray());
		
		//with many-to-many
		
		Store emptyStore = new Store();
		emptyStore.setName("Empty Store");
		getSession().save(emptyStore);
		
		s.clear();
		s.setSearchClass(Store.class);
		s.addFilterEmpty("ingredientsCarried");
		assertListEqual(target.search(s), emptyStore);
		
		s.clear();
		s.addFilterNotEmpty("ingredientsCarried");
		assertListEqual(target.search(s), stores.toArray());		
	}
	
	public void testSizeProperty() {
		initDB();
		
		Search s = new Search(LimbedPet.class);
		s.addFilterEqual("limbs.size", 8);
		assertListEqual(target.search(s), spiderJimmy);
		
		s.clear();
		s.addFilterLessThan("limbs.size", 6);
		assertListEqual(target.search(s), catNorman, catPrissy);
		
		s.clear();
		s.setSearchClass(Person.class);
		s.addFilterGreaterThan("home.residents.size", 2);
		assertListEqual(target.search(s), papaA, papaB, mamaA, mamaB, sallyA, margretB, joeA, joeB);
	}

}
