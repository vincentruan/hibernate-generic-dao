package com.test.junit.standard;

import java.util.List;

import org.hibernate.ObjectNotFoundException;

import com.test.base.TestBase;
import com.test.dao.standard.PersonDAO;
import com.test.dao.standard.ProjectDAO;
import com.test.model.Person;
import com.test.model.Project;
import com.trg.search.Search;
import com.trg.search.Sort;

public class GenericDAOTest extends TestBase {

	private PersonDAO dao;
	
	private ProjectDAO projectDAO;

	public void setPersonDAO(PersonDAO dao) {
		this.dao = dao;
	}
	
	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}


	/**
	 * Just quickly check that all the methods basically work. The underlying
	 * implementation is more thoroughly tested in the
	 * <code>com.test.junit.hibernate</code> package
	 */
	public void testDAO() {
		Person fred = setup(new Person("Fred", "Smith", 35));
		Person bob = setup(new Person("Bob", "Jones", 58));
		Person cyndi = setup(new Person("Cyndi", "Loo", 58));
		Person marty = setup(new Person("Marty", "McFly", 58));

		
		assertTrue(dao.save(fred));
		assertTrue(dao.save(bob));
		fred.setFather(bob);

		assertEquals(bob, dao.find(bob.getId()));
		assertEquals(fred, dao.find(fred.getId()));

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
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, dao.searchUnique(s));
		
		//searchGeneric
		s = new Search();
		s.addFilterEqual("id", bob.getId());
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("firstName");
		assertEquals(bob.getFirstName(), dao.searchGeneric(s).get(0));
		s.setSearchClass(Person.class);
		assertEquals(bob.getFirstName(), dao.searchGeneric(s).get(0));

		//searchUniqueGeneric
		assertEquals(bob.getFirstName(), dao.searchUniqueGeneric(s));
		s.setSearchClass(null);
		assertEquals(bob.getFirstName(), dao.searchUniqueGeneric(s));
		
		assertTrue(dao.remove(bob));
		assertEquals(null, dao.find(bob.getId()));

		assertTrue(dao.removeById(fred.getId()));
		assertEquals(null, dao.find(fred.getId()));

		assertEquals(0, dao.count(new Search(Person.class)));

		bob.setId(null);
		fred.setId(null);

		assertTrue(dao.save(bob));
		assertTrue(dao.save(fred));
		
		dao.save(cyndi, marty);
		for (Person p : dao.find(cyndi.getId(), bob.getId(), fred.getId())) {
			assertNotNull(p);
		}
		
		dao.removeByIds(cyndi.getId(), marty.getId());
		dao.remove(cyndi, fred);
		for (Person p : dao.find(cyndi.getId(), marty.getId(), fred.getId())) {
			assertNull(p);
		}
		
		clearHibernate();
		
		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		assertFalse(dao.save(bob2));

		dao.flush();
		
		assertEquals("Bobby", dao.find(bob.getId()).getFirstName());
		
		
		dao.refresh(bob2);
		assertTrue(dao.isAttached(bob2));
		assertFalse(dao.isAttached(bob));
		
		Person a = dao.getReference(bob2.getId());
		Person b = dao.getReference(bob2.getId() + 10);
		
		Person[] pp = dao.getReferences(bob2.getId(), bob2.getId() + 10);
		
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
	
	/**
	 * Test an example of adding and overriding DAO methods.
	 */
	public void testExtendingDAO() {
		initDB();
		
		//two added methods...
		
		List<Project> expected = projectDAO.search(new Search().addFilterIn("name", "First", "Second"));
		assertListEqual(projectDAO.findProjectsForMember(joeA), expected.toArray());
		
		assertListEqual(projectDAO.search(projectDAO.getProjectsForMemberSearch(joeA).addField("name")), "First", "Second");
		
		//overridden search method to deal with "duration"...
		
		Search s = new Search();
		s.addFilterGreaterThan("duration", 50);
		s.addSort(Sort.asc("duration"));
		List<Project> results = projectDAO.search(s);
		assertTrue(results.size() == 2);
		assertEquals("Second", results.get(0).getName());
		assertEquals("First", results.get(1).getName());
		
		s.clear();
		s.addFilterLessThan("duration", 100);
		s.addSort(Sort.desc("duration"));
		results = projectDAO.search(s);
		assertTrue(results.size() == 2);
		assertEquals("Second", results.get(0).getName());
		assertEquals("Third", results.get(1).getName());
	}

}
