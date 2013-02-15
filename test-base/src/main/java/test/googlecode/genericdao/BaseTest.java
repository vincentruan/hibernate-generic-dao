/* Copyright 2009 The Revere Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.googlecode.genericdao;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.googlecode.genericdao.model.Address;
import test.googlecode.genericdao.model.Home;
import test.googlecode.genericdao.model.Ident;
import test.googlecode.genericdao.model.Ingredient;
import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Name;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Pet;
import test.googlecode.genericdao.model.Project;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;
import test.googlecode.genericdao.model.Store;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:jUnit-applicationContext.xml" })
public abstract class BaseTest {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSourceForJdbcTemplate(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource, true);
	}
	
	PersistenceHelper persistenceHelper;
	
	@Autowired(required = false)
	public void setPersistenceHelper(PersistenceHelper persistenceHelper) {
		this.persistenceHelper = persistenceHelper;
	}

	public <T> T find(Class<T> type, Serializable id) {
		return persistenceHelper.find(type, id);
	}

	public <T> T getProxy(Class<T> type, Serializable id) {
		return persistenceHelper.getProxy(type, id);
	}

	public void persist(Object entity) {
		persistenceHelper.persist(entity);
	}

	public void flush() {
		persistenceHelper.flush();
	}

	public void clear() {
		persistenceHelper.clear();
	}

	// setDbIgnoresCase((Boolean) applicationContext.getBean("dbIgnoresCase"));

	protected boolean dbIgnoresCase;

	@Autowired(required = true)
	public void setDbIgnoresCase(Boolean dbIgnoresCase) {
		this.dbIgnoresCase = dbIgnoresCase;
	}

	protected Person joeA, // 10
			sallyA, // 9
			papaA, // 39
			mamaA, // 40
			joeB, // 10
			margaretB, // 13
			papaB, // 39
			mamaB, // 38
			grandpaA, // 65
			grandmaA; // 65

	protected Pet fishWiggles;
	protected LimbedPet catPrissy, catNorman, spiderJimmy;

	protected List<Store> stores;
	protected List<Recipe> recipes;

	protected List<Project> projects;

	@Autowired
	public void setJoeA(Person joeA) {
		this.joeA = joeA;
	}

	@Autowired
	public void setSallyA(Person sallyA) {
		this.sallyA = sallyA;
	}

	@Autowired
	public void setPapaA(Person papaA) {
		this.papaA = papaA;
	}

	@Autowired
	public void setMamaA(Person mamaA) {
		this.mamaA = mamaA;
	}

	@Autowired
	public void setJoeB(Person joeB) {
		this.joeB = joeB;
	}

	@Autowired
	public void setMargaretB(Person margaretB) {
		this.margaretB = margaretB;
	}

	@Autowired
	public void setPapaB(Person papaB) {
		this.papaB = papaB;
	}

	@Autowired
	public void setMamaB(Person mamaB) {
		this.mamaB = mamaB;
	}

	@Autowired
	public void setGrandpaA(Person grandpaA) {
		this.grandpaA = grandpaA;
	}

	@Autowired
	public void setGrandmaA(Person grandmaA) {
		this.grandmaA = grandmaA;
	}

	@Autowired
	public void setFishWiggles(Pet fishWiggles) {
		this.fishWiggles = fishWiggles;
	}

	@Autowired
	public void setCatPrissy(LimbedPet catPrissy) {
		this.catPrissy = catPrissy;
	}

	@Autowired
	public void setCatNorman(LimbedPet catNorman) {
		this.catNorman = catNorman;
	}

	@Autowired
	public void setSpiderJimmy(LimbedPet spiderJimmy) {
		this.spiderJimmy = spiderJimmy;
	}

	@Resource // wire by name -- @Autowired @Qualifier("stores") should work, but there seems to be a bug in Spring for lists
	public void setStores(List<Store> stores) {
		this.stores = stores;
	}

	@Resource // wire by name -- @Autowired @Qualifier("stores") should work, but there seems to be a bug in Spring for lists
	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	@Resource // wire by name -- @Autowired @Qualifier("stores") should work, but there seems to be a bug in Spring for lists
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	@Before
	public void onSetUp() throws Exception {
		reset();
	}

	protected void reset() {
		for (Person p : new Person[] { joeA, sallyA, joeB, margaretB, papaA, mamaA, papaB, mamaB, grandmaA, grandpaA }) {
			p.setId(null);
			p.getHome().setId(null);
			p.getHome().getAddress().setId(null);
			setup(p);
		}

		for (Pet p : new Pet[] { spiderJimmy, fishWiggles, catPrissy, catNorman }) {
			p.setId(null);
		}

		for (Ingredient i : stores.get(0).getIngredientsCarried()) {
			i.setIngredientId(0);
		}

		for (Store s : stores) {
			s.setId(0);
		}

		for (Recipe r : recipes) {
			r.setId(0);
		}

		for (Project p : projects) {
			p.setId(null);
		}
	}

	public void initDB() {
		insert(papaA.getHome().getAddress());
		insert(papaA.getHome());
		insert(papaB.getHome().getAddress());
		insert(papaB.getHome());
		insert(grandpaA.getHome().getAddress());
		insert(grandpaA.getHome());

		for (Person p : new Person[] { grandpaA, grandmaA, papaA, mamaA, papaB, mamaB, joeA, sallyA, joeB, margaretB }) {
			insert(p);
		}

		for (Pet p : new Pet[] { spiderJimmy, fishWiggles, catPrissy, catNorman }) {
			insert(p);
		}
		for (Pet p : new Pet[] { spiderJimmy, fishWiggles, catPrissy, catNorman }) {
			String sql = "update pet set favoritePlaymate_id = ?1 where id = ?2";
			jdbcTemplate.update(sql, new Object[] { p.getFavoritePlaymate().getId(), p.getId() });
		}

		for (Ingredient i : stores.get(0).getIngredientsCarried()) {
			insert(i);
		}

		for (Store s : stores) {
			insert(s);
		}

		for (Recipe r : recipes) {
			Set<RecipeIngredient> ris = r.getIngredients();
			r.setIngredients(null);
			insert(r);

			for (RecipeIngredient ri : ris) {
				insert(ri);
			}

			r.setIngredients(ris);
		}

		for (Project p : projects) {
			insert(p);
		}
		return;
	}

	private Number insert(String sql, Class<?>[] types, Object... args) {
		int[] stypes = new int[types.length];
		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			if (type.equals(Long.class)) {
				stypes[i] = Types.INTEGER;
			} else if (type.equals(Integer.class)) {
				stypes[i] = Types.INTEGER;
			} else if (type.equals(String.class)) {
				stypes[i] = Types.VARCHAR;
			} else if (type.equals(Date.class)) {
				stypes[i] = Types.DATE;
			} else if (type.equals(Float.class)) {
				stypes[i] = Types.FLOAT;
			} else if (type.equals(Double.class)) {
				stypes[i] = Types.DOUBLE;
			} else if (type.equals(Boolean.class)) {
				stypes[i] = Types.BOOLEAN;
			}
		}
		PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(sql, stypes);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(factory.newPreparedStatementCreator(args), keyHolder);
		return keyHolder.getKey();
	}

	protected void insert(Person p) {
		String sql = "INSERT INTO person (age, dob, first_name, last_name, weight, isMale, father_id, mother_id, home_id) values (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)";
		Class<?>[] types = new Class<?>[] { Integer.class, Date.class, String.class, String.class, Double.class,
				Boolean.class, Long.class, Long.class, Long.class };
		Number id = insert(sql, types, p.getAge(), p.getDob(), p.getFirstName(), p.getLastName(), p.getWeight(), p
				.getIsMale(), p.getFather() != null ? p.getFather().getId() : null, p.getMother() != null ? p
				.getMother().getId() : null, p.getHome().getId());
		p.setId(id.longValue());
	}

	protected void insert(Home h) {
		String sql = "INSERT INTO home (type, address_id) values (?1, ?2)";
		Class<?>[] types = new Class<?>[] { String.class, Long.class };
		Number id = insert(sql, types, h.getType(), h.getAddress().getId());
		h.setId(id.longValue());
	}

	protected void insert(Address a) {
		String sql = "INSERT INTO address (city, state, street, zip) values (?1, ?2, ?3, ?4)";
		Class<?>[] types = new Class<?>[] { String.class, String.class, String.class, String.class };
		Number id = insert(sql, types, a.getCity(), a.getState(), a.getStreet(), a.getZip());
		a.setId(id.longValue());
	}

	protected void insert(Pet p) {
		String sql = "INSERT INTO pet (idNumber, first, last, species, limbed, hasPaws) values (?1, ?2, ?3, ?4, ?5, ?6)";
		Class<?>[] types = new Class<?>[] { Integer.class, String.class, String.class, String.class, Boolean.class,
				Boolean.class };
		Boolean hasPaws = null;
		if (p instanceof LimbedPet) {
			hasPaws = ((LimbedPet) p).isHasPaws();
		}
		Number id = insert(sql, types, p.getIdent().getIdNumber(), p.getIdent().getName().getFirst(), p.getIdent()
				.getName().getLast(), p.getSpecies(), p instanceof LimbedPet, hasPaws);
		p.setId(id.longValue());

		if (p instanceof LimbedPet) {
			sql = "INSERT INTO LimbedPet_limbs (LimbedPet_id, limbs, idx) values (?1, ?2, ?3)";
			types = new Class<?>[] { Long.class, String.class, Integer.class };
			int i = 0;
			for (String s : ((LimbedPet) p).getLimbs()) {
				insert(sql, types, p.getId(), s, i++);
			}
		}
	}

	protected void insert(Recipe r) {
		String sql = "INSERT INTO recipe (title) values (?1)";
		Class<?>[] types = new Class<?>[] { String.class };
		Number id = insert(sql, types, r.getTitle());
		r.setId(id.longValue());
	}

	protected void insert(Ingredient i) {
		String sql = "INSERT INTO ingredient (name) values (?1)";
		Class<?>[] types = new Class<?>[] { String.class };
		Number id = insert(sql, types, i.getName());
		i.setIngredientId(id.longValue());
	}

	protected void insert(Store s) {
		String sql = "INSERT INTO store (name) values (?1)";
		Class<?>[] types = new Class<?>[] { String.class };
		Number id = insert(sql, types, s.getName());
		s.setId(id.longValue());

		sql = "INSERT INTO store_ingredient (Store_id, ingredientsCarried_ingredientId) values (?1, ?2)";
		types = new Class<?>[] { Long.class, Long.class };
		for (Ingredient i : s.getIngredientsCarried()) {
			insert(sql, types, s.getId(), i.getIngredientId());
		}
	}

	protected void insert(RecipeIngredient ri) {
		String sql = "INSERT INTO recipe_x_ingredient (amount, measure, ingredient_ingredientId, recipe_id) values (?1, ?2, ?3, ?4)";
		Class<?>[] types = new Class<?>[] { Float.class, String.class, Long.class, Long.class };
		insert(sql, types, ri.getAmount(), ri.getMeasure(), ri.getCompoundId().getIngredient().getIngredientId(), ri
				.getCompoundId().getRecipe().getId());
	}

	protected void insert(Project p) {
		String sql = "INSERT INTO project (id, inceptionYear, name) values (?1, ?2, ?3)";
		Class<?>[] types = new Class<?>[] { Long.class, Integer.class, String.class };
		Number id = insert(sql, types, p.getId(), p.getInceptionYear(), p.getName());
		p.setId(id.longValue());

		sql = "INSERT INTO project_person (Project_id, members_id) values (?1, ?2)";
		types = new Class<?>[] { Long.class, Long.class };
		for (Person m : p.getMembers()) {
			insert(sql, types, p.getId(), m.getId());
		}
	}

	protected Person setup(Person p) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -p.getAge());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		p.setDob(cal.getTime());
		p.setWeight(100.0 + p.getAge() / 100.0);

		return p;
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
		cpy.setIsMale(p.getIsMale());
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
