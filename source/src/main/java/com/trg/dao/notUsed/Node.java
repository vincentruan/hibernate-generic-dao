package com.trg.dao.notUsed;

import java.util.HashMap;
import java.util.Map;

import com.trg.search.Fetch;
import com.trg.search.Filter;
import com.trg.search.Sort;

public class Node {
	public String property;
	public String alias;
	public Sort sort;
	public Filter filter;
	public Fetch fetch;
	public boolean eager;
	
	public Node parent;
	public Map<String,Node> children = new HashMap<String, Node>();
}
