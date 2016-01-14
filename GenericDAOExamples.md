Here is all the code it usually takes to create a DAO interface and implementation for a `Project` domain object; all the content is inherited from the superclass:
```
public interface ProjectDAO extends GenericDAO<Project, Long> {

}

public class ProjectDAOImpl extends GenericDAOImpl<Project, Long> implements ProjectDAO {

}
```

This could be customized by adding and/or overriding methods if needed:
```
public interface ProjectDAO extends GenericDAO<Project, Long> {
	public Project findByName(String name);
	public Project findByStatus(String status);
}

public class ProjectDAOImpl extends GenericDAOImpl<Project, Long> implements ProjectDAO {
	//basic added method
	public Project findByName(String name) {
		Criteria crit = getSession().createCriteria(Project.class);
		crit.add(Restrictions.eq("name", name));
		return (Project) crit.uniqueResult();
	}

	//basic added method using search
	public List<Project> searchByStatus(String status) {
		return search(new Search().addFilterEqual("status", status));
	}

	//overriding a method
	@Override
	public void save(Project project) {
		if (project.getId() == null)
			project.setStatus("NEW");
		super.save(project);
	}
}
```

A more complex example can be found in the test code at:
http://code.google.com/p/hibernate-generic-dao/source/browse/tags/1.0.0/dao-hibernate/src/test/java/test/googlecode/genericdao/dao/hibernate/dao/ProjectDAOImpl.java