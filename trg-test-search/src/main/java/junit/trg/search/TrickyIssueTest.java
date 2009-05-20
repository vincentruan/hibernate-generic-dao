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
package junit.trg.search;

import java.util.List;
import java.util.Map;

import test.trg.model.Home;
import test.trg.model.Person;
import test.trg.search.BaseSearchTest;

import com.trg.search.Search;

public class TrickyIssueTest extends BaseSearchTest {
	/**
	 * The alias error occurs when using result mode FETCH_MAP. It occurs when
	 * there is a field that has a key with no "." in it and is the same as a
	 * property that is used in a filter.
	 */
	@SuppressWarnings("unchecked")
	public void testAliasError() {
		initDB();

		List<Map<String, Object>> resultMap;

		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Joe");
		s.addFilterEqual("age", 10);
		s.addSortAsc("lastName");
		s.setResultMode(Search.RESULT_MAP);

		s.addField("firstName");

		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));

		s.addField("lastName");

		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Alpha", resultMap.get(0).get("lastName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		assertEquals("Beta", resultMap.get(1).get("lastName"));

		s.clearFields();
		s.addField("firstName", "firstName");
		s.addField("age"); // this uses age for the property and key
		s.addField("lastName", "Last Name");
		s.addField("mother.lastName");

		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals(10, resultMap.get(0).get("age"));
		assertEquals("Alpha", resultMap.get(0).get("Last Name"));
		assertEquals("Alpha", resultMap.get(0).get("mother.lastName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		assertEquals(10, resultMap.get(1).get("age"));
		assertEquals("Beta", resultMap.get(1).get("Last Name"));
		assertEquals("Beta", resultMap.get(1).get("mother.lastName"));
	}

	/**
	 * The building of joins to eagerly fetch collections can mess with result
	 * counts. The latest version should be able to deal with this issue.
	 */
	public void testEagerFetchingPagingError() {
		initDB();

		Search s = new Search(Home.class);

		assertEquals(3, target.search(s).size());

		s.setMaxResults(3);
		assertEquals(3, target.search(s).size());

		s.setMaxResults(2);
		assertEquals(2, target.search(s).size());

		s.setMaxResults(1);
		assertEquals(1, target.search(s).size());

		s.setMaxResults(2);
		s.setPage(1);
		assertEquals(1, target.search(s).size());
	}

	/**
	 * If a property value is supposed to be of type Long but an Integer is
	 * passed in, an error would be thrown. This was a major issue when passing
	 * values in from Adobe Flex where we had no way to control whether a number
	 * is passed as an Integer or Long. The latest version should be able to
	 * deal with this issue.
	 */
	@SuppressWarnings("unchecked")
	public void testNumberClassCastError() {
		initDB();
		
		Search s = new Search(Person.class);
		
		s.addFilterGreaterThan("id", new Integer(0)); //id should be Long
		assertListEqual(new Person[] { sallyA, joeA, joeB, margretB, mamaB, papaA, mamaA, papaB, grandpaA, grandmaA }, target.search(s));
		
		s.clear();
		s.addFilterEqual("age", new Long(40L));
		assertListEqual(new Person[] { mamaA }, target.search(s));
		
		s.clear();
		s.addFilterEqual("age", new Double(10.0));
		assertListEqual(new Person[] { joeA, joeB }, target.search(s));
		
		s.clear();
		s.addFilterEqual("mother.age", new Double(40.0));
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));
		
		s.clear();
		s.addFilterEqual("mother.home.address", mamaA.getHome().getAddress());
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));
		
		s.clear();
		s.addFilterEqual("mother.home.address.id", new Float(mamaA.getHome().getAddress().getId().floatValue()));
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));
		
		s.clear();
		s.addFilterIn("id", new Object[] { new Integer(joeA.getId().intValue()), new Integer(joeB.getId().intValue()) });
		assertListEqual(new Person[] { joeA, joeB }, target.search(s));
		
		s.clear();
		s.addFilterIn("id", (Object[]) new Integer[] { new Integer(joeA.getId().intValue()), new Integer(joeB.getId().intValue()) });
		assertListEqual(new Person[] { joeA, joeB }, target.search(s));
	}
}
