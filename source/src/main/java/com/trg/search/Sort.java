package com.trg.search;

import java.io.Serializable;

public class Sort implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String property;
	public boolean desc;

	public Sort() {
		
	}
	
	public Sort(String property, boolean desc) {
		this.property = property;
		this.desc = desc;
	}
	
	public Sort(String property) {
		this.property = property;
	}
	
	public static Sort asc(String property) {
		return new Sort(property);
	}
	
	public static Sort desc(String property) {
		return new Sort(property, true);
	}
}
