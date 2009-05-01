package test.trg.shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import test.trg.shared.model.Address;
import test.trg.shared.model.Home;
import test.trg.shared.model.Ident;
import test.trg.shared.model.Ingredient;
import test.trg.shared.model.LimbedPet;
import test.trg.shared.model.Name;
import test.trg.shared.model.Person;
import test.trg.shared.model.Pet;
import test.trg.shared.model.Project;
import test.trg.shared.model.Recipe;
import test.trg.shared.model.RecipeIngredient;
import test.trg.shared.model.Store;

import com.trg.test.TestCaseSpringAutoWire;

public class TestBase extends TestCaseSpringAutoWire {

	protected SessionFactory sessionFactory;

	protected Person joeA, // 10
			sallyA, // 9
			papaA, // 39
			mamaA, // 40
			joeB, // 10
			margretB, // 13
			papaB, // 39
			mamaB, // 38
			grandpaA, // 65
			grandmaA; // 65

	protected Pet fishWiggles;
	protected LimbedPet catPrissy, catNorman, spiderJimmy;

	protected List<Store> stores;
	protected List<Recipe> recipes;
	
	protected List<Project> projects;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

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

	public void setFishWiggles(Pet fishWiggles) {
		this.fishWiggles = fishWiggles;
	}

	public void setCatPrissy(LimbedPet catPrissy) {
		this.catPrissy = catPrissy;
	}

	public void setCatNorman(LimbedPet catNorman) {
		this.catNorman = catNorman;
	}

	public void setSpiderJimmy(LimbedPet spiderJimmy) {
		this.spiderJimmy = spiderJimmy;
	}

	public void setStores(List<Store> stores) {
		this.stores = stores;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}
	
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	protected void initDB() {
		Session session = getSession();
		merge(session, papaA.getHome().getAddress());
		merge(session, papaA.getHome());
		merge(session, papaB.getHome().getAddress());
		merge(session, papaB.getHome());
		merge(session, grandpaA.getHome().getAddress());
		merge(session, grandpaA.getHome());

		merge(session, setup(grandpaA));
		merge(session, setup(grandmaA));
		merge(session, setup(papaA));
		merge(session, setup(mamaA));
		merge(session, setup(papaB));
		merge(session, setup(mamaB));
		merge(session, setup(joeA));
		merge(session, setup(sallyA));
		merge(session, setup(joeB));
		merge(session, setup(margretB));

		merge(session, spiderJimmy);
		merge(session, fishWiggles);
		merge(session, catPrissy);
		merge(session, catNorman);

		for (Ingredient i : stores.get(0).getIngredientsCarried()) {
			merge(session, i);
		}

		for (Store s : stores) {
			merge(session, s);
		}

		for (Recipe r : recipes) {
			Set<RecipeIngredient> ris = r.getIngredients();
			r.setIngredients(null);
			merge(session, r);

			for (RecipeIngredient ri : ris) {
				merge(session, ri);
			}

			r.setIngredients(ris);
		}

		for (Project p : projects) {
			merge(session, p);
		}

		// detatch all our Java copies of these from hibernate.
		session.flush();
		session.clear();
	}

	protected void clearIds() {
		for (Person p : new Person[] { grandpaA, grandmaA, papaA, papaB, mamaA, mamaB, joeA, joeB, sallyA, margretB }) {
			p.setId(null);
			p.getHome().setId(null);
			p.getHome().getAddress().setId(null);
		}
	}

	private void merge(Session session, Person p) {
		p.setId(((Person) session.merge(p)).getId());
	}

	private void merge(Session session, Home h) {
		h.setId(((Home) session.merge(h)).getId());
	}

	private void merge(Session session, Address a) {
		a.setId(((Address) session.merge(a)).getId());
	}

	private void merge(Session session, Pet p) {
		p.setId(((Pet) session.merge(p)).getId());
	}

	private void merge(Session session, Recipe r) {
		r.setId(((Recipe) session.merge(r)).getId());
	}

	private void merge(Session session, Ingredient i) {
		i.setIngredientId(((Ingredient) session.merge(i)).getIngredientId());
	}

	private void merge(Session session, Store s) {
		s.setId(((Store) session.merge(s)).getId());
	}

	private void merge(Session session, RecipeIngredient ri) {
		ri.setCompoundId(((RecipeIngredient) session.merge(ri)).getCompoundId());
	}
	
	private void merge(Session session, Project p) {
		p.setId(((Project) session.merge(p)).getId());
	}

	protected void clearHibernate() {
		Session session = sessionFactory.getCurrentSession();
		session.flush();
		session.clear();
	}

	protected Person copy(Person p) {
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

	protected Home copy(Home h) {
		Home cpy = new Home();
		cpy.setId(h.getId());
		cpy.setType(h.getType());
		return cpy;
	}

	protected Address copy(Address a) {
		Address cpy = new Address();
		cpy.setId(a.getId());
		cpy.setStreet(a.getStreet());
		cpy.setCity(a.getCity());
		cpy.setState(a.getState());
		cpy.setZip(a.getZip());
		return cpy;
	}

	protected LimbedPet copy(LimbedPet p) {
		LimbedPet cpy = new LimbedPet();
		cpy.setId(p.getId());
		cpy.setIdent(new Ident(p.getIdent().getIdNumber(), new Name(p.getIdent().getName().getFirst(), p.getIdent()
				.getName().getLast())));
		cpy.setSpecies(p.getSpecies());
		cpy.setHasPaws(p.isHasPaws());
		cpy.setFavoritePlaymate(p.getFavoritePlaymate());
		cpy.setLimbs(new ArrayList<String>(p.getLimbs().size()));
		cpy.getLimbs().addAll(p.getLimbs());
		return cpy;
	}

	protected Person setup(Person p) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -p.getAge());
		p.setDob(cal.getTime());
		p.setWeight(100.0 + p.getAge() / 100.0);

		return p;
	}

	protected void assertListEqual(Person[] expected, List<Person> actual) {
		Assert.assertEquals("The list did not have the expected length", expected.length, actual.size());

		HashMap<Long, Object> unmatched = new HashMap<Long, Object>();
		for (Person person : expected) {
			unmatched.put(person.getId(), "");
		}
		for (Person person : actual) {
			unmatched.remove(person.getId());
		}

		if (unmatched.size() != 0)
			Assert.fail("The list did not match the expected results.");
	}

	protected void assertListEqual(List<?> actual, Object... expected) {
		Assert.assertEquals("The list did not have the expected length", expected.length, actual.size());

		List<Object> remaining = new LinkedList<Object>();
		remaining.addAll(actual);
		
		for (Object o : expected) {
			if (!remaining.remove(o))
				Assert.fail("The list did not match the expected results.");
		}
	}
	
	protected void assertArrayEqual(Object[] actual, Object... expected) {
		Assert.assertEquals("The array did not have the expected length", expected.length, actual.length);

		List<Object> remaining = new LinkedList<Object>();
		for (Object o : actual) {
			remaining.add(o);
		}
		
		for (Object o : expected) {
			if (!remaining.remove(o))
				Assert.fail("The array did not match the expected results.");
		}
	}

	protected void assertListOrderEqual(Person[] expected, List<Person> actual) {
		Assert.assertEquals("The list did not have the expected length", expected.length, actual.size());

		for (int i = 0; i < expected.length; i++) {
			if (!expected[i].getId().equals(actual.get(i).getId()))
				Assert.fail("The list did not match the expected results.");
		}
	}
}
