/* Copyright 2009 The Revere Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.googlecode.genericdao.search;

import java.util.List;

import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.search.BaseSearchTest;

import com.googlecode.genericdao.search.Search;

public class PagingAndSortingTest extends BaseSearchTest {
	@SuppressWarnings("unchecked")
	public void testBasicPaging() {
		initDB();

		Search s = new Search(Person.class);
		s.addSortAsc("lastName");
		s.addSortAsc("firstName");

		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA, sallyA, joeB, mamaB, margretB, papaB },
				target.search(s));

		s.setMaxResults(3);
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA }, target.search(s));

		s.setFirstResult(4);
		assertListEqual(new Person[] { papaA, sallyA, joeB }, target.search(s));

		s.setMaxResults(-1);
		assertListEqual(new Person[] { papaA, sallyA, joeB, mamaB, margretB, papaB }, target.search(s));

		s.setMaxResults(4);
		s.setPage(1);
		s.setFirstResult(2); // first result should override page
		assertListEqual(new Person[] { joeA, mamaA, papaA, sallyA }, target.search(s));

		s.setFirstResult(-1);
		assertListEqual(new Person[] { papaA, sallyA, joeB, mamaB }, target.search(s));

		s.setPage(0);
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA }, target.search(s));

		s.setPage(2);
		assertListEqual(new Person[] { margretB, papaB }, target.search(s));

		s.clearPaging();
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA, sallyA, joeB, mamaB, margretB, papaB },
				target.search(s));

		s.setPage(1); // page should have no effect when max results is not
		// set
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA, sallyA, joeB, mamaB, margretB, papaB },
				target.search(s));
	}

	@SuppressWarnings("unchecked")
	public void testSorting() {
		initDB();

		// test single sort
		Search s = new Search(Person.class);
		s.addFilterNotIn("id", grandmaA.getId(), joeB.getId(), papaB.getId()); // remove
		// duplicate
		// ages
		// for
		// ease
		// of
		// testing

		s.addSortAsc("age");
		assertListOrderEqual(new Person[] { sallyA, joeA, margretB, mamaB, papaA, mamaA, grandpaA }, target.search(s));

		s.clearSorts();
		s.addSortDesc("age");
		assertListOrderEqual(new Person[] { grandpaA, mamaA, papaA, mamaB, margretB, joeA, sallyA }, target.search(s));

		s.removeSort("age");
		s.addSortAsc("dob");
		assertListOrderEqual(new Person[] { grandpaA, mamaA, papaA, mamaB, margretB, joeA, sallyA }, target.search(s));

		// Test nested sort
		s.clear();
		s.addFilterIn("id", sallyA.getId(), mamaB.getId(), grandmaA.getId());

		s.addSortAsc("home.address.street");
		assertListOrderEqual(new Person[] { grandmaA, mamaB, sallyA }, target.search(s));

		// Test multiple sort
		s.clearFilters();
		s.addSort("firstName", false);
		assertListOrderEqual(new Person[] { grandmaA, grandpaA, joeB, mamaB, margretB, papaB, joeA, mamaA, papaA,
				sallyA }, target.search(s));

		// Test ignore case
		s.clear();
		find(Person.class, margretB.getId()).setFirstName("margret");
		s.addFilterIn("id", margretB.getId(), sallyA.getId());

		List<Person> results;
		// without ignore case
		// [some databases (i.e. MySQL) automatically ignore case for ordering,
		// so we won't include this test for those]
		if (!dbIgnoresCase) {
			s.addSortAsc("firstName");
			results = target.search(s);
			assertEquals(sallyA.getId(), results.get(0).getId());
			assertEquals(margretB.getId(), results.get(1).getId());
		}

		// with ignore case
		s.removeSort("firstName");
		s.addSortAsc("firstName", true);
		results = target.search(s);
		assertEquals(margretB.getId(), results.get(0).getId());
		assertEquals(sallyA.getId(), results.get(1).getId());

	}
}
