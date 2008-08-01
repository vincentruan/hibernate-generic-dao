package com.test.junit;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.test.dao.PersonDAO;
import com.test.model.Person;
import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.test.TestCaseSpringAutoWire;

public class SomeTest extends TestCaseSpringAutoWire {

	private PersonDAO personDAO;

	private SessionFactory sessionFactory;

	private Person joeA, // 10
			sallyA, // 9
			papaA, // 39
			mamaA, // 40
			joeB, // 10
			margretB, // 13
			papaB, // 39
			mamaB, // 38
			grandpaA, // 65
			grandmaA; // 65

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void testCreate() {
		personDAO.create(grandpaA);

		List<Person> list = personDAO.fetchAll();
		assertEquals(1, list.size());
		assertEquals(grandpaA, list.get(0));

		assertEquals(grandpaA, personDAO.fetch(grandpaA.getId()));

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

	public void testSearch() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Joe");
		assertListEqual(new Person[] { joeA, joeB }, personDAO.search(s));

		s.addFilterEqual("lastName", "Alpha");
		assertListEqual(new Person[] { joeA }, personDAO.search(s));

		s.removeFiltersOnProperty("firstName");
		s.addFilterLessThan("age", 13);
		assertListEqual(new Person[] { joeA, sallyA }, personDAO.search(s));

		s.addFilterGreaterOrEqual("age", 10);
		assertListEqual(new Person[] { joeA }, personDAO.search(s));
	}

	public void testSearchOperators() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterEqual("lastName", "Beta");
		assertListEqual(new Person[] { joeB, margretB, papaB, mamaB }, personDAO.search(s));
		
		s.clear();
		s.addFilterEqual("age", 10);
		assertListEqual(new Person[] { joeA, joeB }, personDAO.search(s));
		
		s.clear();
		s.addFilterNotEqual("lastName", "Alpha");
		assertListEqual(new Person[] { joeB, margretB, papaB, mamaB }, personDAO.search(s));
		
		s.clear();
		s.addFilterNotEqual("age", 10);
		assertListEqual(new Person[] { sallyA, margretB, mamaA, papaA, mamaB, papaB, grandmaA, grandpaA }, personDAO.search(s));
		
		s.clear();
		s.addFilterLike("firstName", "%pa");
		assertListEqual(new Person[] { papaA, papaB, grandpaA }, personDAO.search(s));
		
		s.clear();
		s.addFilterLessThan("lastName", "Beta");
		assertListEqual(new Person[] { joeA, sallyA, papaA, mamaA, grandpaA, grandmaA }, personDAO.search(s));
		
		s.clear();
		s.addFilterLessThan("lastName", "Alpha");
		assertListEqual(new Person[] {  }, personDAO.search(s));
		
		s.clear();
		s.addFilterLessThan("dob", mamaB.getDob());
		assertListEqual(new Person[] { papaA, papaB, mamaA, grandpaA, grandmaA }, personDAO.search(s));
		
		s.clear();
		s.addFilterGreaterThan("lastName", "Beta");
		assertListEqual(new Person[] {  }, personDAO.search(s));
		
		s.clear();
		s.addFilterGreaterThan("lastName", "Alpha");
		assertListEqual(new Person[] { joeB, margretB, papaB, mamaB }, personDAO.search(s));
		
		s.clear();
		s.addFilterGreaterThan("dob", mamaB.getDob());
		assertListEqual(new Person[] { joeA, joeB, sallyA, margretB }, personDAO.search(s));
		
		s.clear();
		s.addFilterLessOrEqual("age", 39);
		assertListEqual(new Person[] { joeA, joeB, sallyA, margretB, mamaB, papaA, papaB }, personDAO.search(s));
		
		s.clear();
		s.addFilterGreaterOrEqual("age", 39);
		assertListEqual(new Person[] { papaA, papaB, mamaA, grandmaA, grandpaA }, personDAO.search(s));
		
	}

	public void testSearchNesting() {
		initDB();
		
		Search s = new Search(Person.class);
		
		s.addFilterEqual("father.id", papaA.getId());
		assertListEqual(new Person[] { joeA, sallyA }, personDAO.search(s));
		
		s.clear();
		s.addFilterEqual("father.firstName", "Papa");
		assertListEqual(new Person[] { joeA, sallyA, joeB, margretB }, personDAO.search(s));
		
		s.clear();
		s.addFilterEqual("father.firstName", "Grandpa");
		assertListEqual(new Person[] { papaA, mamaB }, personDAO.search(s));
		
		s.clear();
		s.addFilterEqual("father.father.firstName", "Grandpa");
		assertListEqual(new Person[] { joeA, sallyA }, personDAO.search(s));
		
		s.addFilterEqual("mother.father.firstName", "Grandpa");
		s.setDisjunction(true);
		assertListEqual(new Person[] { joeA, sallyA, joeB, margretB }, personDAO.search(s));
	}

	public void testSearchLogicOperators() {
		initDB();
		Search s = new Search(Person.class);
		
		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.greaterOrEqual("age", 10), Filter.lessThan("age", 20));
		assertListEqual(new Person[] { joeA }, personDAO.search(s));
		
		s.clear();
		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.and(Filter.greaterOrEqual("age", 10), Filter.lessThan("age", 20)));
		assertListEqual(new Person[] { joeA }, personDAO.search(s));
		
		s.clear();
		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.or(Filter.lessOrEqual("age", 10), Filter.greaterThan("age", 60)));
		assertListEqual(new Person[] { joeA, sallyA, grandmaA, grandpaA }, personDAO.search(s));
		
	}

	private void initDB() {
		Session session = sessionFactory.getCurrentSession();
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
		session.evict(grandpaA);
		session.evict(grandmaA);
		session.evict(papaA);
		session.evict(mamaA);
		session.evict(papaB);
		session.evict(mamaB);
		session.evict(joeA);
		session.evict(sallyA);
		session.evict(joeB);
		session.evict(margretB);
	}

	private Person copy(Person p) {
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

	private Person setup(Person p) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -p.getAge());
		p.setDob(cal.getTime());
		p.setWeight(100.0 + p.getAge() / 100.0);

		return p;
	}

	private void assertListEqual(Person[] expected, List<Person> actual) {
		assertEquals("The list did not have the expected length",
				expected.length, actual.size());

		HashMap<Long, Object> unmatched = new HashMap<Long, Object>();
		for (Person person : expected) {
			unmatched.put(person.getId(), "");
		}
		for (Person person : actual) {
			unmatched.remove(person.getId());
		}

		if (unmatched.size() != 0)
			fail("The list did not match the expected results.");
	}

	// --- Setters ---

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
}
