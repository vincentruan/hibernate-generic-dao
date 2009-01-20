package com.test.junit.standard;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.ObjectNotFoundException;

import com.test.base.TestBase;
import com.test.dao.standard.PersonDAO;
import com.test.dao.standard.PersonService;
import com.test.model.Person;
import com.trg.dao.DAODispatcherException;
import com.trg.dao.dao.standard.DAODispatcher;
import com.trg.dao.dao.standard.GeneralDAO;
import com.trg.dao.search.Search;

public class GeneralDAOAndDispatcherTest extends TestBase {
	private GeneralDAO generalDAO;
	private DAODispatcher dispatcher;
	private PersonDAO personDAO;
	private PersonService personService;

	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	public void setDAODispatcher(DAODispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void testGeneralDAO() {
		testDAO(generalDAO);
	}

	public void testDispatcherWithGeneralDAO() {
		dispatcher.setSpecificDAOs(new HashMap<String, Object>());
		testDAO(dispatcher);
	}

	public void testDispatcherWithSpecificDAO() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Person.class.getName(), personDAO);
		dispatcher.setSpecificDAOs(map);

		testDAO(dispatcher);
	}

	public void testDispatcherWithSpecificDAONoInterface() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Person.class.getName(), personService);
		dispatcher.setSpecificDAOs(map);

		testDAO(dispatcher);
	}

	/**
	 * Just quickly check that all the methods basically work. The underlying
	 * implementation is more thoroughly tested in the
	 * <code>com.test.junit.hibernate</code> package
	 */
	@SuppressWarnings("unchecked")
	public void testDAO(GeneralDAO dao) {
		Person fred = setup(new Person("Fred", "Smith", 35));
		Person bob = setup(new Person("Bob", "Jones", 58));
		Person cyndi = setup(new Person("Cyndi", "Loo", 58));
		Person marty = setup(new Person("Marty", "McFly", 58));

		
		assertTrue(dao.save(fred));
		assertTrue(dao.save(bob));
		fred.setFather(bob);

		assertEquals(bob, dao.find(Person.class, bob.getId()));
		assertEquals(fred, dao.find(Person.class, fred.getId()));

		assertListEqual(new Person[] { bob, fred }, dao
				.findAll(Person.class));
		assertListEqual(new Person[] { bob, fred }, dao
				.search(new Search(Person.class)));

		assertEquals(2, dao.count(new Search(Person.class)));
		assertListEqual(new Person[] { bob, fred }, dao
				.searchAndCount(new Search(Person.class)).getResults());

		Search s = new Search(Person.class);
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, dao.searchUnique(s));

		assertTrue(dao.remove(bob));
		assertEquals(null, dao.find(Person.class, bob.getId()));

		assertTrue(dao.removeById(Person.class, fred.getId()));
		assertEquals(null, dao.find(Person.class, fred.getId()));

		assertEquals(0, dao.count(new Search(Person.class)));

		bob.setId(null);
		fred.setId(null);

		assertTrue(dao.save(bob));
		assertTrue(dao.save(fred));
		
		dao.save(cyndi, marty);
		for (Person p : dao.find(Person.class, cyndi.getId(), bob.getId(), fred.getId())) {
			assertNotNull(p);
		}
		
		dao.removeByIds(Person.class, cyndi.getId(), marty.getId());
		dao.remove(cyndi, fred);
		for (Person p : dao.find(Person.class, cyndi.getId(), marty.getId(), fred.getId())) {
			assertNull(p);
		}
		
		clearHibernate();
		
		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		assertFalse(dao.save(bob2));

		if (dao == dispatcher) {
			try{
				dao.flush();
				fail("dispatcher should error on flush");
			} catch (DAODispatcherException e) { }
			
			dispatcher.flush(Person.class);
		} else {
			dao.flush();
		}
		
		assertEquals("Bobby", dao.find(bob.getClass(), bob.getId()).getFirstName());
		
		
		dao.refresh(bob2);
		assertTrue(dao.isAttached(bob2));
		assertFalse(dao.isAttached(bob));
		
		Person a = dao.getReference(Person.class, bob2.getId());
		Person b = dao.getReference(Person.class, bob2.getId() + 10);
		
		Person[] pp = dao.getReferences(Person.class, bob2.getId(), bob2.getId() + 10);
		
		assertEquals("Bobby", a.getFirstName());
		assertEquals("Bobby", pp[0].getFirstName());
		
		try {
			b.getFirstName();
			fail("Entity does not exist, should throw error.");
		} catch (ObjectNotFoundException ex) { }
		try {
			pp[1].getFirstName();
			fail("Entity does not exist, should throw error.");
		} catch (ObjectNotFoundException ex) { }
	}

}