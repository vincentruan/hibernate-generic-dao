# hibernate-generic-dao
Automatically exported from code.google.com/p/hibernate-generic-dao

Hibernate Generic D.A.O. Framework
The motivation behind the framework
We had worked on a project where we hand-coded all of our DAOs. This produced four irksome difficulties: (1) Method names and implementations were not altogether consistent. (2) It was a pain to make additional columns sortable or filterable, and as a result, a lot of pages lacked good sorting and filtering. (3) Making additional DAOs was tedious and took a fair amount of time. (4) Writing tests for DAOs is tricky and tedious, so no one really did.

This framework aims to ease our troubles.

Why might you consider looking into this framework?
Generic DAO: With the sweetness of Java generics, the concept of generic DAOs is not new, and it’s not difficult. However, we’ve put a lot of work into making these easy to use and robust. So if you like the way we’ve done it, then this framework provides ready-made code for you. On the other hand if you’d rather make your own, then simply feel free to look at our source code for ideas and help on a few potentially tricky issues.
Search: Search is the most original and sophisticated part of this framework, and it can be used with or without the generic DAO portion of the framework. The search capability is designed around the use-case of a list page with sorting, filtering, column selection and paging. However, its use is certainly not limited to that. The value that the search adds is simpler, more robust querying with less coding and less testing. It is similar to Hibernate Criteria, but it is simpler to use and can easily move across layers of an application including view and even remote layers. Plus is works with both Hibernate and JPA*.
Remote DAO (for R.I.A.s?): If you you’re like us, you don’t want to write and configure an individual DAO style remote service for each entity in a R.I.A. or other client-heavy application. This framework may have the solution. We provide utilities and sample code to adapt our single general DAO to any remoting technology interface. Just configure this single remote access point and the client can do any basic CRUD or search operation on any entity. Again, if you don’t like our way of doing things, maybe you can at least get some ideas from our source code.
Remote Search (for R.I.A.s?): As mentioned above, the framework can provide a single point for client-server CRUD and search operations. The framework’s search is meant to be able to cross the client-server boundary. So lists and searches in the client application can take advantage of the same easy-to-use features and consistency that the search functionality provides in the server tier or single tier application. This consistency allowed us to create a reusable collection type in Adobe Flex 3 that is associated with a single search object and automatically updates itself from the server according to the search parameters.**
*A fairly simple adapter is required for each JPA provider. Right now we only have an adapter for Hibernate Entity Manager. If anyone would like to contribute an adapter for any other JPA provider (OpenJPA, TopLink, etc.), that would be great.

**If time permits, we would like to eventually post our corresponding Adobe Flex 3 framework and utilities.

More Information
Wiki Documentation: UserGuide

Javadoc: http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html

Blog: http://hibernategenericdao.wordpress.com/

Questions and Comments
Please post at http://groups.google.com/group/java-generic-dao.

Code Examples
Creating DAOs for individual model classes:
Simply extend the GenericDAO class with the specific type.
```java
public interface ProjectDAO extends GenericDAO<Project, Long> {

}

public class ProjectDAOImpl extends GenericDAOImpl<Project, Long> implements ProjectDAO {

}
```
The following methods (and several more) are now available on ProjectDAO
```java
Project project = projectDAO.find(projectId);

List<Project> list = projectDAO.findAll();

projectDAO.save(project);

projectDAO.remove(project);

projectDAO.removeById(project.getId());


Search search = new Search();
search.addFilterEqual("name", "hibernate-generic-dao");

List<Project> list = projectDAO.search(search);

int count = projectDAO.count(search);

SearchResult<Project> result = projectDAO.searchAndCount(search);
list = result.getResult();
count = result.getTotalCount();

search.clear();
search.addField("rating", Field.OP_AVG);
int avgProjectRating = (Integer) prjoectDAO.searchUnique(search);
```
A GeneralDAO is also provided with DAO methods for any entity:
```java
public interface GeneralDAO {

        public <T> T find(Class<T> type, Serializable id);

        public <T> T[] find(Class<T> type, Serializable... ids);

        public <T> T getReference(Class<T> type, Serializable id);

        public <T> T[] getReferences(Class<T> type, Serializable... ids);

        public boolean save(Object entity);

        public boolean[] save(Object... entities);

        public boolean remove(Object entity);

        public void remove(Object... entities);

        public boolean removeById(Class<?> type, Serializable id);

        public void removeByIds(Class<?> type, Serializable... ids);

        public <T> List<T> findAll(Class<T> type);

        public List search(ISearch search);

        public Object searchUnique(ISearch search);

        public int count(ISearch search);

        public SearchResult searchAndCount(ISearch search);

        public boolean isAttached(Object entity);

        public void refresh(Object... entities);

        public void flush();

        public Filter getFilterFromExample(Object example);

        public Filter getFilterFromExample(Object example, ExampleOptions options);
}
```
Search DTO usage examples
```java
Search search = new Search(Project.class);

//filtering
search.addFilterEqual("name", "hibernate-generic-dao");

search.addFilterLessThan("completionDate", new Date());

search.addFilterOr(
    Filter.equal("name", "Jack"),
    Filter.and(
        Filter.equal("name", "Jill"),
        Filter.like("location", "%Chicago%"),
        Filter.greaterThan("age", 5)
    )
);

search.addFilterIn("name", "Jack", "Jill", "Bob");

search.addFilterNot(Filter.in("name","Jack", "Jill", "Bob"));

//sorting
search.addSort("name");
search.addSort("age", true); //descending

//projection
search.addField("name");
search.addField("location");

//or with column operators
search.addField("rating", Field.OP_AVG);
search.addField("developerCount", Field.OP_MAX);

//paging
search.setMaxResults(15); //a.k.a. results per page
search.setPage(3);

//controlling eager fetching of relationships
serach.addFetch("owner");
Nested properties are also fully supported...
search.addFilterEqual("status.name", "active");
search.addFilterGreaterThan("workgroup.manager.salary", 75000.00);

search.addSort("status.name");
```


Introduction

The purpose of this framework is to save the time, tedium and possible inconsistency of hand coding DAO layer objects. This includes all of the basic CRUD methods as well as various "find" or "search" methods.

Javadoc: http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html

See also InstallationAndConfiguration.

Also note that it is possible to use the search functionality alone without the DAO portion of the framework. For more, see UsingSearchAlone.

Features
General and Generic DAOs
The foundation of this framework are the General and Generic DAOs.

The GeneralDAO is a single DAO class that can be used to access all classes of domain objects. GeneralDAO has methods that require the developer to specify which type of domain object to use. An application only needs a single instance of the GeneralDAO class to access all domain objects.

Javadoc: 

http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/GeneralDAO.html 

http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/original/GeneralDAO.html

GenericDAO is a class that can be extended to make individual DAOs. A basic domain-object-specific DAO is created by extending GenericDAO and specifying the domain object type with generic type parameters. The default implementation can be customized by adding and/or overriding methods.

GenericDAOExamples

Javadoc: 

http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/GenericDAO.html 

http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/original/GenericDAO.html

See: com.googlecode.genericdao.dao.hibernate.GeneralDAO, com.googlecode.genericdao.dao.hibernate.GeneralDAOImpl, com.googlecode.genericdao.dao.hibernate.GenericDAO, com.googlecode.genericdao.dao.hibernate.GenericDAOImpl, com.googlecode.genericdao.dao.hibernate.original.GeneralDAO, com.googlecode.genericdao.dao.hibernate.original.GeneralDAOImpl, com.googlecode.genericdao.dao.hibernate.original.GenericDAO, com.googlecode.genericdao.dao.hibernate.original.GenericDAOImpl

Search
The biggest impetus for this framework is actually in the area of searches. It is very common to have lists of business objects in an application, for example a list of Customers or a list of Invoices. To make these lists usable each one needed to be sortable and usually filterable as well. Unfortunately, in order to add a single sortable column or a single filter field, we had to make tedious changes to four different files, add several trivial lines of code to the DAO implementation and then (if we were feeling particularly responsible) add code to the test case. The way we did this, the UI code was also pretty tricky and the DAO implementation slightly error-prone.

The idea with this framework is to provide a Search object that consistently takes care of all the DAO implementation work for us and provides a uniform interface that we can take advantage of when writing reusable front-end components.

See: Search, com.googlecode.genericdao.dao.search.*

Remote DAO
The framework allows for creation of general DAO interfaces for local and remote clients. This makes for much less coding of the interface between the view layer and back-end. We used this functionality with Flex. It meant we didn't have to write an ActionScript delegate for every single DAO method for every single type of DTO. For most cases, we only needed the basic methods provided by the single general remote DAO service. This saved us a lot of time and eliminated a huge area of possible errors.

We were also able to write Flex components that integrated automatically with the Search objects, and updated themselves when users sorted by columns and selected filters. This greatly reduced the amount and complexity of view-layer code we needed to write.

See: com.googlecode.genericdao.dao.hibernate.FlexDAOAdapter, com.googlecode.genericdao.dao.hibernate.original.FlexDAOAdapter

Customizable Generic DAOs
The framework comes with two different generic DAO implementations (standard and original), each exposing different methods. But no one is limited to using these two, new generic DAOs can be created with only a little work and testing. Just extend BaseDAOImpl. It provides many protected methods to use in DAO implementations.

See: com.googlecode.genericdao.dao.hibernate.BaseDAOImpl

DAO Dispatcher
One use case is this: We have a single general remote DAO to expose to a Flex client. The GeneralDAO implementation works for almost all objects, but we have special code for handling saves for User objects. We can use a dispatcher. A dispatcher has the same interface as a GeneralDAO but when it receives a method call, it forwards to call to the correct DAO for the object type it is dealing with.

We provide a DAODispatcher implementation for both of the included DAOs. These default to use the GeneralDAO implementation unless another specific DAO has been configured for the object type in question. Implementations of GenericDAO can be specified for these specific DAOs, but any class that has a method matching the signature of the GenericDAO method may be used, even if it does not implement the GenericDAO interface. This last feature can be useful if the call should forward to a service layer class.

See: com.googlecode.genericdao.dao.hibernate.DAODispatcher, com.googlecode.genericdao.dao.hibernate.original.DAODispatcher

Sample Projects
As we all know, an example is worth 1000 wiki pages, so we have set up two sample projects in SVN.

/trunk/sample/hibernate-maven-web - a web project using standard Hibernate and Spring MVC
/trunk/sample/jpa-hibernate-maven - a project using JPA with Hibernate Entity Manager and Spring
Both projects use Maven and include Eclipse project files (which are only useful if you have all the right plugins). Both projects contain a README.TXT with instructions to build and run the project. Neither is particularly impressive in itself. The main purpose is to demonstrate basic configuration and usage of the framework.

Details and Tips
Hibernate session strategy
By default the DAOs and SearchFacade use SessionFactory.getCurrentSession() to get the session to use. However, you can change this behavior by overriding the protected getSession() method of a DAO. For example if you want to set the session manually, you could write something like this.

```java
public class MyBaseGenericDAOImpl<T, ID extends Serializable> extends GenericDAOImpl<T, ID> implements AddressDAO {
        private Session session;
        
        @Override
        protected Session getSession() {
                return session;
        }
        
        public void setSession(Session session) {
                this.session = session;
                setSessionFactory(session.getSessionFactory());
        }
}
```
