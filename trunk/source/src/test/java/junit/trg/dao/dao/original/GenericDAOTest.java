package junit.trg.dao.dao.original;

import test.trg.dao.dao.original.PersonDAO;
import test.trg.shared.TestBase;
import test.trg.shared.model.Home;
import test.trg.shared.model.Person;

import com.trg.search.Search;

public class GenericDAOTest extends TestBase {

	private PersonDAO personDAO;

	public void setOrigPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	/**
	 * Just quickly check that all the methods basically work. The underlying
	 * implementation is more thoroughly tested in the
	 * <code>junit.trg.dao.hibernate</code> package
	 */
	public void testDAO() {
		Person fred = new Person();
		fred.setFirstName("Fred");
		fred.setLastName("Smith");
		fred.setAge(35);
		setup(fred);

		personDAO.create(fred);

		Person bob = new Person();
		bob.setFirstName("Bob");
		bob.setLastName("Jones");
		bob.setAge(58);
		setup(bob);

		personDAO.create(bob);

		fred.setFather(bob);

		assertEquals(bob, personDAO.fetch(bob.getId()));
		assertEquals(fred, personDAO.fetch(fred.getId()));

		//search
		assertListEqual(new Person[] { bob, fred }, personDAO.fetchAll());
		assertListEqual(new Person[] { bob, fred }, personDAO
				.search(new Search()));
		assertListEqual(new Person[] { bob, fred }, personDAO
				.search(new Search(Person.class)));

		//count
		assertEquals(2, personDAO.count(new Search()));
		assertEquals(2, personDAO.count(new Search(Person.class)));
		
		//searchAndCount
		assertListEqual(new Person[] { bob, fred }, personDAO
				.searchAndCount(new Search()).getResult());
		assertListEqual(new Person[] { bob, fred }, personDAO
				.searchAndCount(new Search(Person.class)).getResult());

		//searchUnique
		Search s = new Search();
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, personDAO.searchUnique(s));
		s = new Search(Person.class);
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, personDAO.searchUnique(s));
		
		//searchGeneric
		s = new Search();
		s.addFilterEqual("id", bob.getId());
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("firstName");
		assertEquals(bob.getFirstName(), personDAO.searchGeneric(s).get(0));
		s.setSearchClass(Person.class);
		assertEquals(bob.getFirstName(), personDAO.searchGeneric(s).get(0));
		
		try {
			personDAO.search(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.count(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.searchAndCount(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.searchUnique(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.searchGeneric(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		

		personDAO.deleteEntity(bob);
		assertEquals(null, personDAO.fetch(bob.getId()));

		personDAO.deleteById(fred.getId());
		assertEquals(null, personDAO.fetch(fred.getId()));

		assertEquals(0, personDAO.count(new Search(Person.class)));
		
		bob.setId(null);
		fred.setId(null);

		personDAO.createOrUpdate(bob);
		personDAO.create(fred);

		clearHibernate();

		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		personDAO.update(bob2);
		assertEquals("Bobby", (personDAO.fetch(bob.getId())).getFirstName());

		personDAO.refresh(bob2);
		assertTrue(personDAO.isConnected(bob2));
		assertFalse(personDAO.isConnected(bob));
	}

}
