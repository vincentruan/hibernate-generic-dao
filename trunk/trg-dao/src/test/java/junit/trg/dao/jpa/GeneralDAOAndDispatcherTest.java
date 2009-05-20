package junit.trg.dao.jpa;

import java.util.HashMap;
import java.util.Map;

import test.trg.BaseTest;
import test.trg.dao.jpa.dao.PersonDAO;
import test.trg.dao.jpa.dao.PersonService;
import test.trg.model.Person;

import com.trg.dao.DAODispatcherException;
import com.trg.dao.jpa.DAODispatcher;
import com.trg.dao.jpa.GeneralDAO;
import com.trg.search.ExampleOptions;
import com.trg.search.Search;

public class GeneralDAOAndDispatcherTest extends BaseTest {
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
	 * implementation is more thoroughly tested elsewhere.
	 */
	@SuppressWarnings("unchecked")
	public void testDAO(GeneralDAO dao) {
		Person fred = setup(new Person("Fred", "Smith", 35));
		Person bob = setup(new Person("Bob", "Jones", 58));
		Person cyndi = setup(new Person("Cyndi", "Loo", 58));
		Person marty = setup(new Person("Marty", "McFly", 58));

		
		dao.persist(fred);
		dao.save(bob);
		fred.setFather(bob);

		assertEquals(bob, dao.find(Person.class, bob.getId()));
		assertEquals(fred, dao.find(Person.class, fred.getId()));

		//count
		assertEquals(2, dao.count(new Search()));
		assertEquals(2, dao.count(new Search(Person.class)));
		
		//searchAndCount
		assertListEqual(new Person[] { bob, fred }, dao
				.searchAndCount(new Search()).getResult());
		assertListEqual(new Person[] { bob, fred }, dao
				.searchAndCount(new Search(Person.class)).getResult());

		//searchUnique
		Search s = new Search();
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, dao.searchUnique(s));
		s = new Search(Person.class);
		s.addFilterEqual("father.id", bob.getId());
		assertEquals(fred, dao.searchUnique(s));
		
		//searchGeneric
		s = new Search();
		s.addFilterEqual("id", bob.getId());
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("firstName");
		assertEquals(bob.getFirstName(), dao.search(s).get(0));
		s.setSearchClass(Person.class);
		assertEquals(bob.getFirstName(), dao.search(s).get(0));

		//searchUniqueGeneric
		assertEquals(bob.getFirstName(), dao.searchUnique(s));
		s.setSearchClass(null);
		assertEquals(bob.getFirstName(), dao.searchUnique(s));
		
		//example
		Person example = new Person();
		example.setFirstName("Bob");
		example.setLastName("Jones");
		
		s = new Search(Person.class);
		s.addFilter(dao.getFilterFromExample(example));
		assertEquals(bob, dao.searchUnique(s));
		
		example.setAge(0);
		s.clear();
		s.addFilter(dao.getFilterFromExample(example));
		assertEquals(null, dao.searchUnique(s));
		
		s.clear();
		s.addFilter(dao.getFilterFromExample(example, new ExampleOptions().setExcludeZeros(true)));
		assertEquals(bob, dao.searchUnique(s));
		

		assertTrue(dao.removeById(Person.class, fred.getId()));
		assertEquals(null, dao.find(Person.class, fred.getId()));
		
		assertTrue(dao.remove(bob));
		assertEquals(null, dao.find(Person.class, bob.getId()));

		assertEquals(0, dao.count(new Search(Person.class)));

		if (dao instanceof DAODispatcher) {
			((DAODispatcher) dao).flush(Person.class);
		} else {
			dao.flush();
		}
		
		bob.setId(null);
		fred.setId(null);

		dao.save(bob);
		dao.save(fred);
		
		if (dao instanceof DAODispatcher) {
			((DAODispatcher) dao).flush(Person.class);
		} else {
			dao.flush();
		}
		
		dao.refresh(fred);
		
		for (Object p : dao.save(cyndi, marty)) {
			assertNotNull(p);
		}
		for (Person p : dao.find(Person.class, cyndi.getId(), bob.getId(), fred.getId())) {
			assertNotNull(p);
		}
		
		dao.removeByIds(Person.class, cyndi.getId(), marty.getId());
		dao.remove(cyndi, fred);
		for (Person p : dao.find(Person.class, cyndi.getId(), marty.getId(), fred.getId())) {
			assertNull(p);
		}
		
		flush();
		clear();
		
		fred.setId(null);
		dao.persist(fred);
		
		flush();
		clear();
		
		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		bob2 = dao.save(bob2);
		Person fred2 = copy(fred);
		fred2.setFirstName("Freddie");
		fred2 = dao.merge(fred2);

		flush();
		
		assertEquals("Bobby", dao.find(Person.class, bob.getId()).getFirstName());
		assertEquals("Freddie", dao.find(Person.class, fred.getId()).getFirstName());
		
		assertTrue(dao.isAttached(bob2));
		assertFalse(dao.isAttached(bob));
		
		Object[] oo = dao.merge(bob, fred);
		assertTrue(oo[0] == bob2);
		assertTrue(oo[1] == fred2);
		
		assertEquals("Bob", dao.find(Person.class, bob.getId()).getFirstName());
		assertEquals("Fred", dao.find(Person.class, fred.getId()).getFirstName());
		
		dao.persist(new Person("Andy", "Warhol"), new Person("Jimmy", "Hendrix"));
		
		assertEquals(2, dao.count(new Search().addFilterIn("firstName", "Andy", "Jimmy")));
		
		Person a = dao.getReference(Person.class, bob2.getId());
		Person b = dao.getReference(Person.class, bob2.getId() + 10);
		
		Person[] pp = dao.getReferences(Person.class, bob2.getId(), bob2.getId() + 10);
		
		assertEquals("Bob", a.getFirstName());
		assertEquals("Bob", pp[0].getFirstName());
		
		try {
			b.getFirstName();
			fail("Entity does not exist, should throw error.");
		} catch (Exception ex) { }
		try {
			pp[1].getFirstName();
			fail("Entity does not exist, should throw error.");
		} catch (Exception ex) { }
	}

}