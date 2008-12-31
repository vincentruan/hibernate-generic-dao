package com.test.base;

import com.test.misc.SearchTestInterface;
import com.test.model.Person;
import com.trg.dao.search.Filter;
import com.trg.dao.search.Search;

public class FilterTest extends TestBase {
	protected SearchTestInterface target;

	@SuppressWarnings("unchecked")
	public void testGeneral() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Joe");
		assertListEqual(new Person[] { joeA, joeB }, target.search(s));

		s.addFilterEqual("lastName", "Alpha");
		assertListEqual(new Person[] { joeA }, target.search(s));

		s.removeFiltersOnProperty("firstName");
		s.addFilterLessThan("age", 13);
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));

		s.addFilterGreaterOrEqual("age", 10);
		assertListEqual(new Person[] { joeA }, target.search(s));
	}

	@SuppressWarnings("unchecked")
	public void testOperators() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterEqual("lastName", "Beta");
		assertListEqual(new Person[] { joeB, margretB, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterEqual("age", 10);
		assertListEqual(new Person[] { joeA, joeB }, target.search(s));

		s.clear();
		s.addFilterNotEqual("lastName", "Alpha");
		assertListEqual(new Person[] { joeB, margretB, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterNotEqual("age", 10);
		assertListEqual(new Person[] { sallyA, margretB, mamaA, papaA, mamaB, papaB, grandmaA, grandpaA }, target
				.search(s));

		s.clear();
		s.addFilterLike("firstName", "%pa");
		assertListEqual(new Person[] { papaA, papaB, grandpaA }, target.search(s));

		//-- LIKE is already case insensitive in some databases (MySQL)
		// while it is case sensitive in others (HSQLDB, ProsgresSQL) --
//		s.clear();
//		s.addFilterLike("firstName", "pA%");
//		assertEquals("none should match because of case", 0, target.search(s).size());

		s.clear();
		s.addFilterILike("firstName", "pA%");
		assertListEqual(new Person[] { papaA, papaB }, target.search(s));

		s.clear();
		s.addFilterLessThan("lastName", "Beta");
		assertListEqual(new Person[] { joeA, sallyA, papaA, mamaA, grandpaA, grandmaA }, target.search(s));

		s.clear();
		s.addFilterLessThan("lastName", "Alpha");
		assertListEqual(new Person[] {}, target.search(s));

		s.clear();
		s.addFilterLessThan("dob", mamaB.getDob());
		assertListEqual(new Person[] { papaA, papaB, mamaA, grandpaA, grandmaA }, target.search(s));

		s.clear();
		s.addFilterGreaterThan("lastName", "Beta");
		assertListEqual(new Person[] {}, target.search(s));

		s.clear();
		s.addFilterGreaterThan("lastName", "Alpha");
		assertListEqual(new Person[] { joeB, margretB, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterGreaterThan("dob", mamaB.getDob());
		assertListEqual(new Person[] { joeA, joeB, sallyA, margretB }, target.search(s));

		s.clear();
		s.addFilterLessOrEqual("age", 39);
		assertListEqual(new Person[] { joeA, joeB, sallyA, margretB, mamaB, papaA, papaB }, target.search(s));

		s.clear();
		s.addFilterGreaterOrEqual("age", 39);
		assertListEqual(new Person[] { papaA, papaB, mamaA, grandmaA, grandpaA }, target.search(s));

		s.clear();
		s.addFilterIn("age", 9, 10, 14, 65);
		assertListEqual(new Person[] { sallyA, joeA, joeB, margretB, grandmaA, grandpaA }, target.search(s));

		s.clear();
		s.addFilterIn("firstName", "Joe", "Papa");
		assertListEqual(new Person[] { joeA, joeB, papaA, papaB }, target.search(s));

		s.clear();
		s.addFilterNotIn("age", 9, 10, 14, 65);
		assertListEqual(new Person[] { papaA, mamaA, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterNotIn("firstName", "Joe", "Papa", "Mama");
		assertListEqual(new Person[] { sallyA, margretB, grandmaA, grandpaA }, target.search(s));

	}

	@SuppressWarnings("unchecked")
	public void testNesting() {
		initDB();

		Search s = new Search(Person.class);

		s.addFilterEqual("father.id", papaA.getId());
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));

		s.clear();
		s.addFilterEqual("father.firstName", "Papa");
		assertListEqual(new Person[] { joeA, sallyA, joeB, margretB }, target.search(s));

		s.clear();
		s.addFilterEqual("father.firstName", "Grandpa");
		assertListEqual(new Person[] { papaA, mamaB }, target.search(s));

		s.clear();
		s.addFilterEqual("father.father.firstName", "Grandpa");
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));

		s.addFilterEqual("mother.father.firstName", "Grandpa");
		s.setDisjunction(true);
		assertListEqual(new Person[] { joeA, sallyA, joeB, margretB }, target.search(s));
	}

	@SuppressWarnings("unchecked")
	public void testLogicOperators() {
		initDB();
		Search s = new Search(Person.class);

		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.greaterOrEqual("age", 10), Filter.lessThan("age", 20));
		assertListEqual(new Person[] { joeA }, target.search(s));

		s.clear();
		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.and(Filter.greaterOrEqual("age", 10), Filter.lessThan(
				"age", 20)));
		assertListEqual(new Person[] { joeA }, target.search(s));

		s.clear();
		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.or(Filter.lessOrEqual("age", 10), Filter.greaterThan(
				"age", 60)));
		assertListEqual(new Person[] { joeA, sallyA, grandmaA, grandpaA }, target.search(s));

		s.clear();
		s.addFilterNot(Filter.and(Filter.equal("lastName", "Alpha"), Filter.or(Filter.lessOrEqual("age", 10), Filter
				.greaterThan("age", 60))));
		assertListEqual(new Person[] { joeB, margretB, papaA, papaB, mamaA, mamaB }, target.search(s));

		s.clear();
		s.addFilterOr(Filter.not(Filter.or(Filter.equal("firstName", "Joe"), Filter.equal("lastName", "Alpha"))),
				Filter.and(Filter.equal("firstName", "Papa"), Filter.equal("lastName", "Alpha")));
		assertListEqual(new Person[] { margretB, papaB, mamaB, papaA }, target.search(s));
	}

}
