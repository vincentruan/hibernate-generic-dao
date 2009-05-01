package test.trg.dao.dao.standard;

import java.util.List;

import test.trg.shared.model.Person;

import com.trg.dao.dao.standard.GenericDAO;

public interface PersonDAO extends GenericDAO<Person, Long> {
	public List<Person> findByName(String firstName, String lastName);
}
