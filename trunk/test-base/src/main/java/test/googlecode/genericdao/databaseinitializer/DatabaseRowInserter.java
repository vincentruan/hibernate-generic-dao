package test.googlecode.genericdao.databaseinitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.springframework.jdbc.core.StatementCreatorUtils;

import test.googlecode.genericdao.model.Address;
import test.googlecode.genericdao.model.Home;
import test.googlecode.genericdao.model.Ingredient;
import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Pet;
import test.googlecode.genericdao.model.Project;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;
import test.googlecode.genericdao.model.Store;

import com.mysql.jdbc.Statement;

public class DatabaseRowInserter {
	
	Connection connection;
	PreparedStatement statement = null;
	ResultSet generatedKeys = null;
	long generatedKey;
	
	public DatabaseRowInserter(Connection connection) {
		this.connection = connection;
	}
	
	public void insert(Object entity) throws SQLException {
		if (entity instanceof Person)
			insert((Person) entity);
		else if (entity instanceof Pet)
			insert((Pet) entity);
		else if (entity instanceof Store)
			insert((Store) entity);
		else if (entity instanceof Recipe)
			insert((Recipe) entity);
		else if (entity instanceof Project)
			insert((Project) entity);
		else
			throw new RuntimeException("DatabaseRowInserter is not set up to handle entities of type " + entity.getClass().getName());
	}
	
	protected void insert(Person p) throws SQLException {
		if (p.getHome() != null && p.getHome().getId() == null) {
			insert(p.getHome());
		}
		
		String sql = "INSERT INTO person (age, dob, first_name, last_name, weight, isMale, father_id, mother_id, home_id) values (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)";
		Class<?>[] types = new Class<?>[] { Integer.class, Date.class, String.class, String.class, Double.class,
				Boolean.class, Long.class, Long.class, Long.class };
		executeSqlInsertWithGeneratedId(sql, types, p.getAge(), p.getDob(), p.getFirstName(), p.getLastName(), p.getWeight(), p
				.getIsMale(), p.getFather() != null ? p.getFather().getId() : null, p.getMother() != null ? p
				.getMother().getId() : null, p.getHome().getId());
		p.setId(getGeneratedKey());
	}

	protected void insert(Home h) throws SQLException {
		if (h.getAddress() != null && h.getAddress().getId() == null) {
			insert(h.getAddress());
		}
		
		String sql = "INSERT INTO home (type, address_id) values (?1, ?2)";
		Class<?>[] types = new Class<?>[] { String.class, Long.class };
		executeSqlInsertWithGeneratedId(sql, types, h.getType(), h.getAddress().getId());
		h.setId(getGeneratedKey());
	}

	protected void insert(Address a) throws SQLException {
		String sql = "INSERT INTO address (city, state, street, zip) values (?1, ?2, ?3, ?4)";
		Class<?>[] types = new Class<?>[] { String.class, String.class, String.class, String.class };
		executeSqlInsertWithGeneratedId(sql, types, a.getCity(), a.getState(), a.getStreet(), a.getZip());
		a.setId(getGeneratedKey());
	}

	protected void insert(Pet p) throws SQLException {
		String sql = "INSERT INTO pet (idNumber, first, last, species, limbed, hasPaws) values (?1, ?2, ?3, ?4, ?5, ?6)";
		Class<?>[] types = new Class<?>[] { Integer.class, String.class, String.class, String.class, Boolean.class,
				Boolean.class };
		Boolean hasPaws = null;
		if (p instanceof LimbedPet) {
			hasPaws = ((LimbedPet) p).isHasPaws();
		}
		executeSqlInsertWithGeneratedId(sql, types, p.getIdent().getIdNumber(), p.getIdent().getName().getFirst(), p.getIdent()
				.getName().getLast(), p.getSpecies(), p instanceof LimbedPet, hasPaws);
		p.setId(getGeneratedKey());

		if (p instanceof LimbedPet) {
			sql = "INSERT INTO LimbedPet_limbs (LimbedPet_id, limbs, idx) values (?1, ?2, ?3)";
			types = new Class<?>[] { Long.class, String.class, Integer.class };
			int i = 0;
			for (String s : ((LimbedPet) p).getLimbs()) {
				executeSqlWithoutGeneratedId(sql, types, p.getId(), s, i++);
			}
		}
	}

	protected void insert(Recipe r) throws SQLException {
		String sql = "INSERT INTO recipe (title) values (?1)";
		Class<?>[] types = new Class<?>[] { String.class };
		executeSqlInsertWithGeneratedId(sql, types, r.getTitle());
		r.setId(getGeneratedKey());
		
		for (RecipeIngredient ri : r.getIngredients()) {
			insert(ri);
		}
	}

	protected void insert(Ingredient i) throws SQLException {
		String sql = "INSERT INTO ingredient (name) values (?1)";
		Class<?>[] types = new Class<?>[] { String.class };
		executeSqlInsertWithGeneratedId(sql, types, i.getName());
		i.setIngredientId(getGeneratedKey());
	}

	protected void insert(Store s) throws SQLException {
		String sql = "INSERT INTO store (name) values (?1)";
		Class<?>[] types = new Class<?>[] { String.class };
		executeSqlInsertWithGeneratedId(sql, types, s.getName());
		s.setId(getGeneratedKey());

		sql = "INSERT INTO store_ingredient (Store_id, ingredientsCarried_ingredientId) values (?1, ?2)";
		types = new Class<?>[] { Long.class, Long.class };
		for (Ingredient i : s.getIngredientsCarried()) {
			if (i.getIngredientId() == 0) {
				insert(i);
			}
			
			executeSqlWithoutGeneratedId(sql, types, s.getId(), i.getIngredientId());
		}
	}

	protected void insert(RecipeIngredient ri) throws SQLException {
		if (ri.getCompoundId().getIngredient().getIngredientId() == 0) {
			insert(ri.getCompoundId().getIngredient());
		}
		
		String sql = "INSERT INTO recipe_x_ingredient (amount, measure, ingredient_ingredientId, recipe_id) values (?1, ?2, ?3, ?4)";
		Class<?>[] types = new Class<?>[] { Float.class, String.class, Long.class, Long.class };
		executeSqlWithoutGeneratedId(sql, types, ri.getAmount(), ri.getMeasure(), ri.getCompoundId().getIngredient().getIngredientId(), ri
				.getCompoundId().getRecipe().getId());
	}

	protected void insert(Project p) throws SQLException {
		String sql = "INSERT INTO project (id, inceptionYear, name) values (?1, ?2, ?3)";
		Class<?>[] types = new Class<?>[] { Long.class, Integer.class, String.class };
		executeSqlInsertWithGeneratedId(sql, types, p.getId(), p.getInceptionYear(), p.getName());
		p.setId(getGeneratedKey());

		sql = "INSERT INTO project_person (Project_id, members_id) values (?1, ?2)";
		types = new Class<?>[] { Long.class, Long.class };
		for (Person m : p.getMembers()) {
			executeSqlWithoutGeneratedId(sql, types, p.getId(), m.getId());
		}
	}
	
	private void executeSqlInsertWithGeneratedId(String sql, Class<?>[] argTypes, Object... args) throws SQLException {
	    try {
	    	doExecuteSqlInsertWithGeneratedId(sql, argTypes, args);
	    } finally {
    		if (statement != null) statement.close();
	    }
	}
	
	private void doExecuteSqlInsertWithGeneratedId(String sql, Class<?>[] argTypes, Object... args) throws SQLException {
		executeSql(sql, argTypes, args, true);
		updateGeneratedKey();
	}
	
	private void executeSql(String sql, Class<?>[] argTypes, Object[] args, boolean returnGeneratedKeys) throws SQLException {
		int flag = returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
		statement = connection.prepareStatement(sql, flag);
		
		for (int i = 0; i < args.length; i++) {
			int sqlType = getSQLTypeFromJavaType(argTypes[i]);
			StatementCreatorUtils.setParameterValue(statement, i + 1, sqlType, args[i]);
		}
		
		statement.executeUpdate();
	}
	
	private int getSQLTypeFromJavaType(Class<?> javaType) {
		if (javaType.equals(Long.class)) {
			return Types.INTEGER;
		} else if (javaType.equals(Integer.class)) {
			return Types.INTEGER;
		} else if (javaType.equals(String.class)) {
			return Types.VARCHAR;
		} else if (javaType.equals(Date.class)) {
			return Types.DATE;
		} else if (javaType.equals(Float.class)) {
			return Types.FLOAT;
		} else if (javaType.equals(Double.class)) {
			return Types.DOUBLE;
		} else if (javaType.equals(Boolean.class)) {
			return Types.BOOLEAN;
		} else {
			throw new RuntimeException("Unexpected Java Type for Argument");
		}
	}
	
	private void updateGeneratedKey() throws SQLException {
		try {
			doUpdateGeneratedKey();
		} finally {
			if (generatedKeys != null) generatedKeys.close();
		}
	}
	
	private void doUpdateGeneratedKey() throws SQLException {
		generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            generatedKey = generatedKeys.getLong(1);
        } else {
            throw new SQLException("Inserting entity failed, no generated key obtained.");
        }
	}
	
	private long getGeneratedKey() {
		return generatedKey;
	}
	
	public void updateFavoritePlaymate(Pet pet) throws SQLException {
		String sql = "update pet set favoritePlaymate_id = ?1 where id = ?2";
		Class<?>[] types = new Class<?>[] { Long.class, Long.class };
		executeSqlWithoutGeneratedId(sql, types, pet.getFavoritePlaymate().getId(), pet.getId());
	}
	
	private void executeSqlWithoutGeneratedId(String sql, Class<?>[] argTypes, Object... args) throws SQLException {
	    try {
	    	executeSql(sql, argTypes, args, false);
	    } finally {
    		if (statement != null) statement.close();
	    }
	}

}
