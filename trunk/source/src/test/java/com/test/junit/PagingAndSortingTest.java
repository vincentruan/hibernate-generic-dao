package com.test.junit;

import com.test.TestBase;
import com.test.dao.PersonDAO;
import com.test.model.Person;
import com.trg.search.Search;

public class PagingAndSortingTest extends TestBase {

	private PersonDAO personDAO;

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void testBasicPaging() {
		initDB();

		Search s = new Search();
		s.addSort("lastName");
		s.addSort("firstName");

		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA,
				sallyA, joeB, mamaB, margretB, papaB }, personDAO.search(s));

		s.setMaxResults(3);
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA }, personDAO
				.search(s));

		s.setFirstResult(4);
		assertListEqual(new Person[] { papaA, sallyA, joeB }, personDAO
				.search(s));

		s.setMaxResults(-1);
		assertListEqual(new Person[] { papaA, sallyA, joeB, mamaB, margretB,
				papaB }, personDAO.search(s));

		s.setMaxResults(4);
		s.setPage(1);
		s.setFirstResult(2); // first result should override page
		assertListEqual(new Person[] { joeA, mamaA, papaA, sallyA }, personDAO
				.search(s));

		s.setFirstResult(-1);
		assertListEqual(new Person[] { papaA, sallyA, joeB, mamaB }, personDAO
				.search(s));

		s.setPage(0);
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA },
				personDAO.search(s));

		s.setPage(2);
		assertListEqual(new Person[] { margretB, papaB }, personDAO.search(s));

		s.clearPaging();
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA,
				sallyA, joeB, mamaB, margretB, papaB }, personDAO.search(s));

		s.setPage(1); // page should have no effect when max results is not
		// set
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA,
				sallyA, joeB, mamaB, margretB, papaB }, personDAO.search(s));
	}

	public void testSorting() {
		initDB();

		// test single sort
		Search s = new Search();
		s.addFilterNotIn("id", grandmaA.getId(), joeB.getId(), papaB
				.getId()); // remove duplicate ages for ease of testing

		s.addSort("age");
		assertListOrderEqual(new Person[] { sallyA, joeA, margretB, mamaB,
				papaA, mamaA, grandpaA }, personDAO.search(s));

		s.clearSorts();
		s.addSort("age", true);
		assertListOrderEqual(new Person[] { grandpaA, mamaA, papaA, mamaB,
				margretB, joeA, sallyA }, personDAO.search(s));

		s.removeSort("age");
		s.addSort("dob");
		assertListOrderEqual(new Person[] { grandpaA, mamaA, papaA, mamaB,
				margretB, joeA, sallyA }, personDAO.search(s));

		// Test nested sort
		s.clear();
		s.addFilterIn("id", sallyA.getId(), mamaB.getId(), grandmaA.getId());

		s.addSort("home.address.street");
		assertListOrderEqual(new Person[] { grandmaA, mamaB, sallyA },
				personDAO.search(s));

		// Test multiple sort
		s.clearFilters();
		s.addSort("firstName", false);
		assertListEqual(new Person[] { grandmaA, grandpaA, joeB, mamaB,
				margretB, papaB, joeA, mamaA, papaA, sallyA }, personDAO
				.search(s));
	}
}
