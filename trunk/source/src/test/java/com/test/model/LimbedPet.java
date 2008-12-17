package com.test.model;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinTable;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;

@Entity
@DiscriminatorValue("1")
public class LimbedPet extends Pet {
	private List<String> limbs;
	private boolean hasPaws;
	
	@CollectionOfElements
    @JoinTable
	@IndexColumn(name="idx")
	public List<String> getLimbs() {
		return limbs;
	}
	public void setLimbs(List<String> limbs) {
		this.limbs = limbs;
	}
	public boolean isHasPaws() {
		return hasPaws;
	}
	public void setHasPaws(boolean hasPaws) {
		this.hasPaws = hasPaws;
	}
}
