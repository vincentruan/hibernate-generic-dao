package com.trg.dao.search;

public class SearchUtil {
	public static int calcFirstResult(ISearch search) {
		return (search.getFirstResult() > 0) ? search.getFirstResult() : (search.getPage() > 0 && search
				.getMaxResults() > 0) ? search.getPage() * search.getMaxResults() : 0;
	}
}
