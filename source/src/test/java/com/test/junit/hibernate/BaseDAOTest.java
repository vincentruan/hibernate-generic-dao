package com.test.junit.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;

import com.test.TestBase;
import com.test.misc.HibernateBaseDAOTester;
import com.test.model.Person;
import com.trg.dao.search.Fetch;
import com.trg.dao.search.Search;

public class BaseDAOTest extends TestBase {

	private HibernateBaseDAOTester target;

	public void setHibernateBaseDAOTester(HibernateBaseDAOTester dao) {
		this.target = dao;
	}

	public void testSave() {
		Serializable id = null;
		
		id = target.save(grandpaA.getHome().getAddress());
		assertEquals("returned ID should match assigned ID", grandpaA.getHome().getAddress().getId(), id);
		id = target.save(grandpaA.getHome());
		assertEquals("returned ID should match assigned ID", grandpaA.getHome().getId(), id);
		id = target.save(grandpaA);
		assertEquals("returned ID should match assigned ID", grandpaA.getId(), id);

		List<Person> list = target.all(Person.class);
		assertEquals(1, list.size());
		assertEquals(grandpaA, list.get(0));

		assertEquals(grandpaA, target.get(Person.class, grandpaA.getId()));

		target.save(papaA.getHome().getAddress());
		target.save(papaA.getHome());
		target.save(grandmaA);
		target.save(papaA);
		target.save(mamaA);
		target.save(joeA);

		grandpaA.setFirstName("Dean");

		assertEquals("Dean", target.get(Person.class, joeA.getId()).getFather()
				.getFather().getFirstName());
		
		grandpaA.setFirstName("Grandpa");
	}

	public void testUpdate() {
		initDB();
		Person fred = copy(papaA);
		fred.setFirstName("Fred");
		target.update(fred);
		
		assertTrue(target.isAttached(fred));

		assertEquals("The change should be made.", "Fred", target.get(Person.class, joeA.getId()).getFather()
				.getFirstName());
		
		fred.setLastName("Santos");
		Search s = new Search(Person.class);
		s.addFetch("father.lastName");
		s.setFetchMode(Search.FETCH_SINGLE);
		s.addFilterEqual("id", joeA.getId());
		
		assertEquals("The change should be made.", "Santos", target.searchUnique(s));
		
		Person otherFred = copy(papaA);
		try {
			target.update(otherFred);
			fail("Should throw exception when there is a persistent instance with the same identifier.");
		} catch (HibernateException e) {}
	}

	public void testPersist() {
		clearIds();
		
		target.persist(grandpaA.getHome().getAddress());
		target.persist(grandpaA.getHome());
		target.persist(grandpaA);

		List<Person> list = target.all(Person.class);
		assertEquals(1, list.size());
		assertEquals(grandpaA, list.get(0));

		assertEquals(grandpaA, target.load(Person.class, grandpaA.getId()));

		target.persist(papaA.getHome().getAddress());
		target.persist(papaA.getHome());
		target.persist(grandmaA);
		target.persist(papaA);
		target.persist(mamaA);
		target.persist(joeA);

		grandpaA.setFirstName("Dean");

		assertEquals("Dean", target.load(Person.class, joeA.getId()).getFather()
				.getFather().getFirstName());
		
		grandpaA.setFirstName("Grandpa");		
	}
	
	public void testMerge() {
		initDB();
		Person fred = copy(papaA);
		fred.setFirstName("Fred");
		Person attachedFred = target.merge(fred);

		assertEquals("The change should be made.", "Fred", target.get(Person.class, joeA.getId()).getFather()
				.getFirstName());
		
		assertFalse(target.isAttached(fred));
		assertTrue(target.isAttached(attachedFred));
		
		Search s = new Search(Person.class);
		s.addFetch("father.lastName");
		s.setFetchMode(Search.FETCH_SINGLE);
		s.addFilterEqual("id", joeA.getId());
		
		fred.setLastName("Santos");
		assertEquals("The change should not be made.", "Alpha", target.searchUnique(s));		
		
		attachedFred.setLastName("Santos");
		assertEquals("The change should be made.", "Santos", target.searchUnique(s));		
	}
	
	public void testDelete() {
		initDB();

		List<Person> list = target.all(Person.class);
		int sizeBefore = list.size();

		assertTrue("Should return true when successfully deleting", target.deleteById(Person.class, joeA.getId()) );
		assertTrue("Should return true when successfully deleting",  target.deleteEntity(sallyA) );

		list = target.all(Person.class);
		assertEquals(sizeBefore - 2, list.size());
		for (Person person : list) {
			if (person.getId().equals(joeA.getId())
					|| person.getId().equals(sallyA.getId()))
				fail("Neither Joe nor Sally should now be in the DB");
		}

		target.save(joeA);
		target.save(sallyA);

		list = target.all(Person.class);
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
		
		// Test deleting by non-existent ID.
		Search s = new Search(Person.class);
		s.setFetchMode(Search.FETCH_SINGLE);
		s.addFetch("id", Fetch.OP_MAX);
		Long unusedId = ((Long) target.searchUnique(s)).longValue() + 1;
		
		// deleteById should not throw an error
		assertFalse(target.deleteById( Person.class, unusedId ));
		
		Person fake = new Person();
		assertFalse("return false when no ID", target.deleteEntity(fake));
		fake.setId(unusedId);
		assertFalse("return false when ID not found", target.deleteEntity(fake));
	}
	
	public void testLoad() {
		initDB();
		
		Person joe = new Person();
		target.load(joe, joeA.getId());
		assertEquals(joe.getId(), joeA.getId());
		assertEquals(joe.getAge(), joeA.getAge());
	}

}
