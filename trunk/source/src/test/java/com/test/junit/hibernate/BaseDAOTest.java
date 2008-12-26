package com.test.junit.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;

import com.test.base.TestBase;
import com.test.misc.HibernateBaseDAOTester;
import com.test.model.Home;
import com.test.model.Person;
import com.test.model.Pet;
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
	
	@SuppressWarnings("unchecked")
	public void testForceClass() {
		Person bob = copy(grandpaA);
		Person fred = copy(grandmaA);
		target.save(bob);
		target.save(fred);
		
		Search s = new Search();
		Search sP = new Search(Person.class);
		Search sH = new Search(Home.class);
		//search
		assertListEqual(new Person[] { bob, fred }, target
				.search(s, Person.class));
		assertListEqual(new Person[] { bob, fred }, target
				.search(sP, Person.class));
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());

		//count
		assertEquals(2, target.count(s, Person.class));
		assertEquals(2, target.count(sP, Person.class));
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());
		
		//searchAndCount
		assertListEqual(new Person[] { bob, fred }, target
				.searchAndCount(s, Person.class).results);
		assertListEqual(new Person[] { bob, fred }, target
				.searchAndCount(sP, Person.class).results);
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());

		//searchUnique
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, target.searchUnique(s, Person.class));
		sP.addFilterEqual("id", bob.getId());
		assertEquals(bob, target.searchUnique(sP, Person.class));
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());
		
		try {
			target.search(sH, Person.class);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
		try {
			target.count(sH, Person.class);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
		try {
			target.searchAndCount(sH, Person.class);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
		try {
			target.searchUnique(sH, Person.class);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
	}

	public void testSaveMulti() {
		Serializable id = null;
		
		target.save(grandpaA.getHome().getAddress(), grandpaA.getHome(), grandpaA);
		
		List<Person> list = target.all(Person.class);
		assertEquals(1, list.size());
		assertEquals(grandpaA, list.get(0));
		
		assertEquals(grandpaA, target.get(Person.class, grandpaA.getId()));
		
		target.save(papaA.getHome().getAddress(), papaA.getHome(), grandmaA, papaA, mamaA, joeA);
		
		grandpaA.setFirstName("Dean");
		try {
			assertEquals("Dean", target.get(Person.class, joeA.getId()).getFather()
					.getFather().getFirstName());
		} finally {
			grandpaA.setFirstName("Grandpa");
		}
	}
	
	public void testSaveOrUpdate() {
		initDB();
		
		String[] orig = new String[] { grandpaA.getFirstName(), grandmaA.getFirstName() };
		
		try {
			grandpaA.setFirstName("GGG1");
			grandmaA.setFirstName("GGG2");
			
			target.saveOrUpdate(grandpaA.getHome().getAddress(), grandpaA.getHome(), grandpaA);
			
			target.saveOrUpdate(grandmaA);
			
			assertFalse(target.saveOrUpdateIsNew(papaA));
			
			Person bob = new Person();
			bob.setFirstName("Bob");
			bob.setLastName("Loblaw");
			
			assertTrue(target.saveOrUpdateIsNew(bob));
			
			Person[] people = new Person[2];
			people[0] = new Person();
			people[0].setFirstName("First");
			people[0].setLastName("Person");
			people[1] = new Person();
			people[1].setFirstName("Second");
			people[1].setLastName("Person");
			
			target.saveOrUpdate((Object[]) people);
			
			Search s = new Search(Person.class);
			s.addFilterIn("firstName", "GGG1", "GGG2", "Bob", "First", "Second");
			assertListEqual(new Person[] { grandpaA, grandmaA, bob, people[0], people[1] }, target.search(s));
			
			grandpaA.setFirstName("GGG3");
			grandmaA.setFirstName("GGG4");
			bob.setFirstName("Bobby");
			people[0].setFirstName("Firstly");
			people[1].setFirstName("Secondly");
			
			s.clear();
			s.addFilterIn("firstName", "GGG3", "GGG4", "Bobby", "Firstly", "Secondly");
			assertListEqual(new Person[] { grandpaA, grandmaA, bob, people[0], people[1] }, target.search(s));
		
		} finally {
			grandpaA.setFirstName(orig[0]);
			grandmaA.setFirstName(orig[1]);
		}
	}
	
	public void testGetLoadMulti() {
		initDB();
		
		Search s = new Search(Person.class);
		s.setFetchMode(Search.FETCH_SINGLE);
		s.addFetch("id", Fetch.OP_MAX);
		long maxId = (Long) target.searchUnique(s);
		
		Person[] people = target.get(Person.class, papaA.getId(), maxId + 1, papaB.getId());
		assertEquals(3, people.length);
		assertEquals(papaA.getId(), people[0].getId());
		assertEquals(papaA.getAge(), people[0].getAge());
		assertNull(people[1]);
		assertEquals(papaB.getId(), people[2].getId());
		assertEquals(papaB.getAge(), people[2].getAge());
		
		
		people = target.load(Person.class, mamaA.getId(), maxId + 1, mamaB.getId());
		assertEquals(3, people.length);
		assertEquals(mamaA.getId(), people[0].getId());
		assertEquals(mamaA.getAge(), people[0].getAge());
		assertEquals(mamaB.getId(), people[2].getId());
		assertEquals(mamaB.getAge(), people[2].getAge());
		
		try {
			people[1].getAge();
			fail("Entity does not exist, should throw error.");
		} catch (ObjectNotFoundException ex) { }
		
	}

	public void testDeleteMulti() {
		initDB();
		
		Search s = new Search(Person.class);
		s.setFetchMode(Search.FETCH_SINGLE);
		s.addFetch("id", Fetch.OP_MAX);
		long maxId = (Long) target.searchUnique(s);
		
		target.update(papaA);
		target.update(papaB);
		target.update(mamaA);
		target.update(mamaB);
		
		
		//delete unattached
		assertFalse(target.isAttached(joeA));
		assertFalse(target.isAttached(joeB));
		assertFalse(target.isAttached(sallyA));
		assertFalse(target.isAttached(margretB));
		
		target.deleteById(Person.class, joeA.getId(), null, joeB.getId(), maxId + 1);
		
		assertNull(target.get(Person.class, joeA.getId()));
		assertFalse(target.isAttached(joeA));
		assertNull(target.get(Person.class, joeB.getId()));
		assertFalse(target.isAttached(joeB));
		
		target.deleteEntities(sallyA, null, margretB, spiderJimmy);
		
		assertNull(target.get(Person.class, sallyA.getId()));
		assertFalse(target.isAttached(sallyA));
		assertNull(target.get(Person.class, margretB.getId()));
		assertFalse(target.isAttached(margretB));
		assertNull(target.get(Pet.class, spiderJimmy.getId()));
		assertFalse(target.isAttached(spiderJimmy));
		
		//delete attached
		assertTrue(target.isAttached(papaA));
		assertTrue(target.isAttached(papaB));
		assertTrue(target.isAttached(mamaA));
		assertTrue(target.isAttached(mamaB));
		
		target.deleteById(Person.class, papaA.getId(), null, papaB.getId(), maxId + 1);
		
		assertNull(target.get(Person.class, papaA.getId()));
		assertFalse(target.isAttached(papaA));
		assertNull(target.get(Person.class, papaB.getId()));
		assertFalse(target.isAttached(papaB));
		
		target.deleteEntities(mamaA, mamaB);
		
		assertNull(target.get(Person.class, mamaA.getId()));
		assertFalse(target.isAttached(mamaA));
		assertNull(target.get(Person.class, mamaB.getId()));
		assertFalse(target.isAttached(mamaB));
		
	}
}
