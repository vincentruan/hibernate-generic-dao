package com.test.dao.standard;

import java.util.List;

import com.test.model.Person;
import com.test.model.Project;
import com.trg.dao.dao.standard.GenericDAO;
import com.trg.dao.search.Search;

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
