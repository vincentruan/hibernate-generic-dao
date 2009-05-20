package test.trg.dao.jpa.dao;

import java.util.List;

import test.trg.model.Person;
import test.trg.model.Project;

import com.trg.dao.jpa.GenericDAO;
import com.trg.search.Search;

public interface ProjectDAO extends GenericDAO<Project, Long> {
	/**
	 * Returns a list of all projects of which the given person is a member.
	 */
	public List<Project> findProjectsForMember(Person member);
	
	/**
	 * Returns a search that will find all the projects for which the given
	 * person is a member.
	 */
	public Search getProjectsForMemberSearch(Person member);
}
