package junit.trg.search;

import test.trg.model.Person;
import test.trg.search.BaseSearchTest;

import com.trg.search.Search;
import com.trg.search.SearchResult;

public class SearchResultTest extends BaseSearchTest {
	@SuppressWarnings("unchecked")
	public void test() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterLessThan("lastName", "Balloons");
		
		assertEquals(6, target.count(s));
		
		SearchResult<Person> result = target.searchAndCount(s);
		assertEquals(6, result.getTotalCount());
		assertListEqual(new Person[] { joeA, sallyA, papaA, mamaA, grandpaA, grandmaA }, result.getResult());
	}
	
}
