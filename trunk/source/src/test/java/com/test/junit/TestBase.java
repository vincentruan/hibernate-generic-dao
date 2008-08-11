package com.test.junit;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.test.model.Person;
import com.trg.test.TestCaseSpringAutoWire;

public class TestBase extends TestCaseSpringAutoWire {
	
	protected SessionFactory sessionFactory;

	protected Person joeA, // 10
			sallyA, // 9
			papaA, // 39
			mamaA, // 40
			joeB, // 10
			margretB, // 13
			papaB, // 39
			mamaB, // 38
			grandpaA, // 65
			grandmaA; // 65

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setJoeA(Person joeA) {
		this.joeA = joeA;
	}

	public void setSallyA(Person sallyA) {
		this.sallyA = sallyA;
	}

	public void setPapaA(Person papaA) {
		this.papaA = papaA;
	}

	public void setMamaA(Person mamaA) {
		this.mamaA = mamaA;
	}

	public void setJoeB(Person joeB) {
		this.joeB = joeB;
	}

	public void setMargretB(Person margretB) {
		this.margretB = margretB;
	}

	public void setPapaB(Person papaB) {
		this.papaB = papaB;
	}

	public void setMamaB(Person mamaB) {
		this.mamaB = mamaB;
	}

	public void setGrandpaA(Person grandpaA) {
		this.grandpaA = grandpaA;
	}

	public void setGrandmaA(Person grandmaA) {
		this.grandmaA = grandmaA;
	}
	
	
	protected void initDB() {
		Session session = sessionFactory.getCurrentSession();
		session.save(papaA.getHome().getAddress());
		session.save(papaA.getHome());
		session.save(papaB.getHome().getAddress());
		session.save(papaB.getHome());
		session.save(grandpaA.getHome().getAddress());
		session.save(grandpaA.getHome());
		
		session.save(setup(grandpaA));
		session.save(setup(grandmaA));
		session.save(setup(papaA));
		session.save(setup(mamaA));
		session.save(setup(papaB));
		session.save(setup(mamaB));
		session.save(setup(joeA));
		session.save(setup(sallyA));
		session.save(setup(joeB));
		session.save(setup(margretB));

		// detatch all our Java copies of these from hibernate.
		session.flush();
		session.clear();
	}

	protected Person copy(Person p) {
		Person cpy = new Person();
		cpy.setId(p.getId());
		cpy.setFather(p.getFather());
		cpy.setFirstName(p.getFirstName());
		cpy.setLastName(p.getLastName());
		cpy.setMother(p.getMother());
		cpy.setAge(p.getAge());
		cpy.setDob(p.getDob());
		cpy.setWeight(p.getWeight());
		return cpy;
	}

	protected Person setup(Person p) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -p.getAge());
		p.setDob(cal.getTime());
		p.setWeight(100.0 + p.getAge() / 100.0);

		return p;
	}
	
	protected void assertListEqual(Person[] expected, List<Person> actual) {
		Assert.assertEquals("The list did not have the expected length",
				expected.length, actual.size());

		HashMap<Long, Object> unmatched = new HashMap<Long, Object>();
		for (Person person : expected) {
			unmatched.put(person.getId(), "");
		}
		for (Person person : actual) {
			unmatched.remove(person.getId());
		}

		if (unmatched.size() != 0)
			Assert.fail("The list did not match the expected results.");
	}
	
	protected void assertListOrderEqual(Person[] expected, List<Person> actual) {
		Assert.assertEquals("The list did not have the expected length",
				expected.length, actual.size());
		
		for (int i = 0; i < expected.length; i++) {
			if (!expected[i].getId().equals(actual.get(i).getId()))
				Assert.fail("The list did not match the expected results.");
		}
	}
}
