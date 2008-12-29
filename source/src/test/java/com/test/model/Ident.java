package com.test.model;

import javax.persistence.Embeddable;

@Embeddable
public class Ident {
	private Integer idNumber;
	private Name name;

	public Integer getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(Integer idNumber) {
		this.idNumber = idNumber;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String toString() {
		return "Ident::idNumber:" + idNumber + ",name:" + name;
	}

}
