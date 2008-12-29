package com.test.model;

import javax.persistence.Embeddable;

/**
 * Name component used for Pets (note, this is not used for Persons)
 */
@Embeddable
public class Name {
	private String first;
	private String last;

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String toString() {
		return "Name::first:" + first + ",last:" + last;
	}
}
