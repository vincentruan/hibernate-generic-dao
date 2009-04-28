package com.test.junit.original;

import java.util.HashMap;
import java.util.Map;

import com.test.base.TestBase;
import com.test.dao.original.PersonDAO;
import com.test.dao.original.PersonService;
import com.test.model.Person;
import com.trg.dao.dao.original.DAODispatcher;
import com.trg.dao.dao.original.GeneralDAO;
import com.trg.dao.search.Search;

public class GeneralDAOAndDispatcherTest extends TestBase {
	private GeneralDAO generalDAO;
	private DAODispatcher dispatcher;
	private PersonDAO personDAO;
	private PersonService personService;

	public void setOrigGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	public void setOrigDAODispatcher(DAODispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void setOrigPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setOrigPersonService(PersonService personService) {
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
		Person fred = new Person();
		fred.setFirstName("Fred");
		fred.setLastName("Smith");
		fred.setAge(35);
		setup(fred);

		dao.create(fred);

		Person bob = new Person();
		bob.setFirstName("Bob");
		bob.setLastName("Jones");
		bob.setAge(58);
		setup(bob);

		dao.create(bob);

		fred.setFather(bob);

		assertEquals(bob, dao.fetch(Person.class, bob.getId()));
		assertEquals(fred, dao.fetch(Person.class, fred.getId()));

		assertListEqual(new Person[] { bob, fred }, dao
				.fetchAll(Person.class));
		assertListEqual(new Person[] { bob, fred }, dao
				.search(new Search(Person.class)));

		assertEquals(2, dao.count(new Search(Person.class)));
		assertListEqual(new Person[] { bob, fred }, dao
				.searchAndCount(new Search(Person.class)).getResult());

		Search s = new Search(Person.class);
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, dao.searchUnique(s));

		dao.deleteEntity(bob);
		assertEquals(null, dao.fetch(Person.class, bob.getId()));

		dao.deleteById(Person.class, fred.getId());
		assertEquals(null, dao.fetch(Person.class, fred.getId()));

		assertEquals(0, dao.count(new Search(Person.class)));

		bob.setId(null);
		fred.setId(null);

		dao.createOrUpdate(bob);
		dao.create(fred);

		clearHibernate();

		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		dao.update(bob2);
		assertEquals("Bobby", (dao.fetch(bob.getClass(), bob.getId()))
				.getFirstName());
		
		
		dao.refresh(bob2);
		assertTrue(dao.isConnected(bob2));
		assertFalse(dao.isConnected(bob));
		
		if (dao == dispatcher) {
			try{
				dao.flush();
				fail("dispatcher should error on flush");
			} catch (Exception e) { }
			
			dispatcher.flush(Person.class);
		} else {
			dao.flush();
		}
		
	}

}