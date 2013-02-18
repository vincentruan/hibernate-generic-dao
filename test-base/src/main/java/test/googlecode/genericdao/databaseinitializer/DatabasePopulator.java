package test.googlecode.genericdao.databaseinitializer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test.googlecode.genericdao.model.Pet;

public class DatabasePopulator {

	private List<Object> entities;
	
	public DatabasePopulator(List<Object> entities) {
		this.entities = new ArrayList<Object>(entities);
	}
	
	public void persistEntitiesAndSetIds(Connection conn) throws SQLException {
		DatabaseRowInserter inserter = new DatabaseRowInserter(conn);
		for (Object entity : entities) {
			inserter.insert(entity);
		}
		
		for (Object entity : entities) {
			if (entity instanceof Pet)
				inserter.updateFavoritePlaymate((Pet) entity);
		}
	}
}
