package com.trg.search;

import java.io.Serializable;
import java.util.List;

public class SearchResult<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public Search search;
	public List<T> result;
	public int totalLength = -1;  //-1 stands for unspecified
	public int page = -1;  //-1 stands for unspecified
	public int firstResult = -1;  //-1 stands for unspecified
	public int maxResults = -1;  //-1 stands for unspecified
}
