package com.test.junit;

import java.util.List;

import com.test.TestBase;
import com.test.dao.AddressDAO;
import com.test.dao.HomeDAO;
import com.test.dao.PersonDAO;
import com.test.model.Person;

public class BasicDAOTest extends TestBase {

	private PersonDAO personDAO;
	private AddressDAO addressDAO;
	private HomeDAO homeDAO;

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setAddressDAO(AddressDAO addressDAO) {
		this.addressDAO = addressDAO;
	}

	public void setHomeDAO(HomeDAO homeDAO) {
		this.homeDAO = homeDAO;
	}

	public void testCreate() {
		addressDAO.create(grandpaA.getHome().getAddress());
		homeDAO.create(grandpaA.getHome());
		personDAO.create(grandpaA);

		List<Person> list = personDAO.fetchAll();
		assertEquals(1, list.size());
		assertEquals(grandpaA, list.get(0));

		assertEquals(grandpaA, personDAO.fetch(grandpaA.getId()));

		addressDAO.create(papaA.getHome().getAddress());
		homeDAO.create(papaA.getHome());
		personDAO.create(grandmaA);
		personDAO.create(papaA);
		personDAO.create(mamaA);
		personDAO.create(joeA);

		grandpaA.setFirstName("Dean");

		assertEquals("Dean", personDAO.fetch(joeA.getId()).getFather()
				.getFather().getFirstName());
		
		grandpaA.setFirstName("Grandpa");
	}

	public void testUpdate() {
		initDB();
		Person fred = copy(papaA);
		fred.setFirstName("Fred");
		personDAO.update(fred);

		assertEquals("Fred", personDAO.fetch(joeA.getId()).getFather()
				.getFirstName());
	}

	public void testDelete() {
		initDB();

		List<Person> list = personDAO.fetchAll();
		int sizeBefore = list.size();

		personDAO.deleteById(joeA.getId());
		personDAO.deleteEntity(sallyA);

		list = personDAO.fetchAll();
		assertEquals(sizeBefore - 2, list.size());
		for (Person person : list) {
			if (person.getId().equals(joeA.getId())
					|| person.getId().equals(sallyA.getId()))
				fail("Neither Joe nor Sally should now be in the DB");
		}

		personDAO.create(joeA);
		personDAO.create(sallyA);

		list = personDAO.fetchAll();
		assertEquals(sizeBefore, list.size());
		boolean joeFound = false, sallyFound = false;
		for (Person person : list) {
			if (person.getFirstName().equals("Joe")
					&& person.getLastName().equals("Alpha"))
				joeFound = true;
			if (person.getFirstName().equals("Sally")
					&& person.getLastName().equals("Alpha"))
				sallyFound = true;
		}
		assertTrue("Joe and Sally should now be back in the DB", joeFound
				&& sallyFound);
	}

}
