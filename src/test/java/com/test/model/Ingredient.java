package com.test.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Ingredient {
	private long id;
	private String name;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Ingredient))
			return false;
		Ingredient i = (Ingredient) o;

		return (getName() == null) ? i.getName() == null : getName().equals(i.getName());
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (name == null ? 0 : name.hashCode());
		return hash;
	}

	public String toString() {
		return "Ingredient::id:" + id + ",name:" + name;
	}

}
