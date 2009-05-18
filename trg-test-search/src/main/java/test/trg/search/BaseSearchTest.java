package test.trg.search;

import com.trg.search.SearchFacade;

import test.trg.BaseTest;

public class BaseSearchTest extends BaseTest {
	protected SearchFacade target;
	
	public void setSearchFacade(SearchFacade target) {
		this.target = target;
	}
}
