package junit.trg.dao.dao.original;

import java.util.HashMap;

import test.trg.shared.TestBase;
import test.trg.shared.model.Person;

import com.trg.dao.dao.original.DAODispatcher;
import com.trg.dao.dao.original.FlexDAOAdapter;
import com.trg.dao.dao.original.GeneralDAO;
import com.trg.search.Filter;
import com.trg.search.flex.FlexSearch;

public class FlexDAOAdapterTest extends TestBase {
	private GeneralDAO generalDAO;
	private FlexDAOAdapter flexDAOAdapter;
	private DAODispatcher dispatcher;

	public void setOrigGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	public void setOrigFlexDAOAdapter(FlexDAOAdapter flexDAOAdapter) {
		this.flexDAOAdapter = flexDAOAdapter;
	}

	public void setOrigDAODispatcher(DAODispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	/**
	 * Just quickly check that all the methods basically work. We're relying on
	 * underlying implementation that is thoroughly tested elsewhere.
	 */
	@SuppressWarnings("unchecked")
	public void testFlexDAOAdapter() throws Exception {
		// use general DAO
		dispatcher.setSpecificDAOs(new HashMap<String, Object>());

		Person fred = new Person();
		fred.setFirstName("Fred");
		fred.setLastName("Smith");
		fred.setAge(35);
		setup(fred);

		flexDAOAdapter.create(fred);

		Person bob = new Person();
		bob.setFirstName("Bob");
		bob.setLastName("Jones");
		bob.setAge(58);
		setup(bob);

		flexDAOAdapter.create(bob);

		fred.setFather(bob);

		assertEquals(bob, flexDAOAdapter.fetch(bob.getId(), Person.class
				.getName()));
		assertEquals(fred, flexDAOAdapter.fetch(fred.getId(), Person.class
				.getName()));

		FlexSearch s = new FlexSearch();
		s.setSearchClassName(Person.class.getName());

		assertListEqual(new Person[] { bob, fred }, flexDAOAdapter
				.fetchAll(Person.class.getName()));
		assertListEqual(new Person[] { bob, fred }, flexDAOAdapter.search(s));

		assertEquals(2, flexDAOAdapter.searchLength(s));
		assertListEqual(new Person[] { bob, fred }, flexDAOAdapter
				.searchAndLength(s).getResult());

		s.setFilters(new Filter[] { Filter.equal("id", bob.getId()) });
		assertEquals(bob, flexDAOAdapter.searchUnique(s));

		flexDAOAdapter.deleteEntity(bob);
		assertEquals(null, flexDAOAdapter.fetch(bob.getId(), Person.class
				.getName()));

		flexDAOAdapter.deleteById(fred.getId(), Person.class.getName());
		assertEquals(null, flexDAOAdapter.fetch(fred.getId(), Person.class
				.getName()));

		assertEquals(0, flexDAOAdapter.searchLength(s));

		bob.setId(null);
		fred.setId(null);

		flexDAOAdapter.createOrUpdate(bob);
		flexDAOAdapter.create(fred);

		clearHibernate();

		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		flexDAOAdapter.update(bob2);
		assertEquals("Bobby", ((Person) generalDAO.fetch(bob.getClass(), bob
				.getId())).getFirstName());
	}
}