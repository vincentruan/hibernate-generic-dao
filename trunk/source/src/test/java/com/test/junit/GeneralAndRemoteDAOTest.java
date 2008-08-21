package com.test.junit;

import java.util.HashMap;
import java.util.Map;

import com.test.dao.PersonDAO;
import com.test.model.Person;
import com.trg.dao.GeneralDAO;
import com.trg.remote.RemoteDAO;
import com.trg.remote.RemoteSearch;
import com.trg.search.Search;

public class GeneralAndRemoteDAOTest extends TestBase {
	private GeneralDAO generalDAO;
	private RemoteDAO remoteDAO;
	private PersonDAO personDAO;

	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	public void setRemoteDAO(RemoteDAO remoteDAO) {
		this.remoteDAO = remoteDAO;
	}

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	/**
	 * Just quickly check that all the methods basically work. General uses the
	 * same implementation as generic. So the thorough testing using generic is
	 * sufficient.
	 */
	public void testGeneralDAO() {
		Person fred = new Person();
		fred.setFirstName("Fred");
		fred.setLastName("Smith");
		fred.setAge(35);
		setup(fred);

		generalDAO.create(fred);

		Person bob = new Person();
		bob.setFirstName("Bob");
		bob.setLastName("Jones");
		bob.setAge(58);
		setup(bob);

		generalDAO.create(bob);

		fred.setFather(bob);

		assertEquals(bob, generalDAO.fetch(bob.getId(), Person.class));
		assertEquals(fred, generalDAO.fetch(fred.getId(), Person.class));

		assertListEqual(new Person[] { bob, fred }, generalDAO
				.fetchAll(Person.class));
		assertListEqual(new Person[] { bob, fred }, generalDAO
				.search(new Search(Person.class)));

		assertEquals(2, generalDAO.searchLength(new Search(Person.class)));
		assertListEqual(new Person[] { bob, fred }, generalDAO
				.searchAndLength(new Search(Person.class)).results);
		
		Search s = new Search(Person.class);
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, generalDAO.searchUnique(s));

		generalDAO.deleteEntity(bob);
		assertEquals(null, generalDAO.fetch(bob.getId(), Person.class));

		generalDAO.deleteById(fred.getId(), Person.class);
		assertEquals(null, generalDAO.fetch(fred.getId(), Person.class));

		assertEquals(0, generalDAO.searchLength(new Search(Person.class)));
		
		bob.setId(null);
		fred.setId(null);
		
		generalDAO.createOrUpdate(bob);
		generalDAO.create(fred);
		
		clearHibernate();
		
		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		generalDAO.update(bob2);
		assertEquals("Bobby", ((Person) generalDAO.fetch(bob.getId(), bob.getClass())).getFirstName() );
	}
	
	public void testRemoteDAOGeneral() throws Exception {
		Person fred = new Person();
		fred.setFirstName("Fred");
		fred.setLastName("Smith");
		fred.setAge(35);
		setup(fred);

		remoteDAO.create(fred);

		Person bob = new Person();
		bob.setFirstName("Bob");
		bob.setLastName("Jones");
		bob.setAge(58);
		setup(bob);

		remoteDAO.create(bob);

		fred.setFather(bob);

		assertEquals(bob, remoteDAO.fetch(bob.getId(), Person.class.getName()));
		assertEquals(fred, remoteDAO.fetch(fred.getId(), Person.class.getName()));

		RemoteSearch s = new RemoteSearch();
		s.setClassName(Person.class.getName());
		
		
		assertListEqual(new Person[] { bob, fred }, remoteDAO
				.fetchAll(Person.class.getName()));
		assertListEqual(new Person[] { bob, fred }, remoteDAO
				.search(s));

		assertEquals(2, remoteDAO.searchLength(s));
		assertListEqual(new Person[] { bob, fred }, remoteDAO
				.searchAndLength(s).results);
		
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, remoteDAO.searchUnique(s));

		remoteDAO.deleteEntity(bob);
		assertEquals(null, remoteDAO.fetch(bob.getId(), Person.class.getName()));

		remoteDAO.deleteById(fred.getId(), Person.class.getName());
		assertEquals(null, remoteDAO.fetch(fred.getId(), Person.class.getName()));

		assertEquals(0, remoteDAO.searchLength(s));
		
		bob.setId(null);
		fred.setId(null);
		
		remoteDAO.createOrUpdate(bob);
		remoteDAO.create(fred);
		
		clearHibernate();
		
		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		remoteDAO.update(bob2);
		assertEquals("Bobby", ((Person) generalDAO.fetch(bob.getId(), bob.getClass())).getFirstName());		
	}
	
	public void testRemoteDAOSpecific() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Person.class.getName(), personDAO);
		remoteDAO.setSpecificDAOs(map);
		
		testGeneralDAO();
	}
}