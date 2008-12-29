package com.test.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "home")
public class Home {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String type;
	@OneToOne
	@JoinColumn(name = "address_id")
	private Address address;

	@OneToMany(mappedBy = "home", fetch = FetchType.EAGER)
	private List<Person> residents;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Person> getResidents() {
		return residents;
	}

	public void setResidents(List<Person> residents) {
		this.residents = residents;
	}

	public String toString() {
		return "Home::id:" + id + ",type:" + type + ",address:" + address;
	}
}
