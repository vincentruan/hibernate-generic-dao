package com.test.base;

import java.util.List;

import com.test.misc.SearchTestInterface;
import com.test.model.Ingredient;
import com.test.model.LimbedPet;
import com.test.model.Person;
import com.test.model.Pet;
import com.test.model.Recipe;
import com.test.model.RecipeIngredient;
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
	
	public void testIdProperty() {
		initDB();
		
		Recipe toffee = recipes.get(2);
		Ingredient sugar = (Ingredient) target.searchUnique(new Search(Ingredient.class).addFilterEqual("name", "Sugar"));
		
		Search s = new Search(RecipeIngredient.class);
		
		s.addField("id.recipe.id");
		s.addFilterEqual("id.ingredient.id", sugar.getId());
		assertListEqual(target.search(s), toffee.getId());
		
		s.clear();
		s.addField("id.recipe.title");
		s.addFilterEqual("id.ingredient.name", "Salt");
		assertListEqual(target.search(s), "Fried Chicken", "Bread");
		
		s.clear();
		s.setSearchClass(Person.class);
		s.addField("mother.father.id");
		s.addFilterEqual("father.id", papaB.getId());
		assertListEqual(target.search(s), grandpaA.getId(), grandpaA.getId());
	}
	
	public void testClassProperty() {
		initDB();
		
		Search s = new Search(Pet.class);
		s.addFilterEqual("class", LimbedPet.class);
		assertListEqual(target.search(s), catNorman, catPrissy, spiderJimmy);
		
		s.clear();
		s.addFilterNotEqual("class", LimbedPet.class.getName());
		assertListEqual(target.search(s), fishWiggles);
	}
	
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
