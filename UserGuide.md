# Introduction #

The purpose of this framework is to save the time, tedium and possible inconsistency of hand coding DAO layer objects. This includes all of the basic CRUD methods as well as various "find" or "search" methods.

Javadoc: http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html

See also InstallationAndConfiguration.

Also note that it is possible to use the search functionality alone without the DAO portion of the framework. For more, see UsingSearchAlone.

# Features #

## General and Generic DAOs ##
The foundation of this framework are the General and Generic DAOs.

The **GeneralDAO** is a single DAO class that can be used to access all classes of domain objects. GeneralDAO has methods that require the developer to specify which type of domain object to use. An application only needs a single instance of the GeneralDAO class to access all domain objects.

Javadoc: <br />
http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/GeneralDAO.html <br />
http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/original/GeneralDAO.html

**GenericDAO** is a class that can be extended to make individual DAOs. A basic domain-object-specific DAO is created by extending GenericDAO and specifying the domain object type with generic type parameters. The default implementation can be customized by adding and/or overriding methods.

[GenericDAOExamples](GenericDAOExamples.md)

Javadoc: <br />
http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/GenericDAO.html <br />
http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/dao/hibernate/original/GenericDAO.html

See:
`com.googlecode.genericdao.dao.hibernate.GeneralDAO, com.googlecode.genericdao.dao.hibernate.GeneralDAOImpl, com.googlecode.genericdao.dao.hibernate.GenericDAO, com.googlecode.genericdao.dao.hibernate.GenericDAOImpl, com.googlecode.genericdao.dao.hibernate.original.GeneralDAO, com.googlecode.genericdao.dao.hibernate.original.GeneralDAOImpl, com.googlecode.genericdao.dao.hibernate.original.GenericDAO, com.googlecode.genericdao.dao.hibernate.original.GenericDAOImpl`

## Search ##
The biggest impetus for this framework is actually in the area of searches. It is very common to have lists of business objects in an application, for example a list of Customers or a list of Invoices. To make these lists usable each one needed to be sortable and usually filterable as well. Unfortunately, in order to add a single sortable column or a single filter field, we had to make tedious changes to four different files, add several trivial lines of code to the DAO implementation and then (if we were feeling particularly responsible) add code to the test case. The way we did this, the UI code was also pretty tricky and the DAO implementation slightly error-prone.

The idea with this framework is to provide a Search object that consistently takes care of all the DAO implementation work for us and provides a uniform interface that we can take advantage of when writing reusable front-end components.

See:
[Search](Search.md), `com.googlecode.genericdao.dao.search.*`

## Remote DAO ##
The framework allows for creation of general DAO interfaces for local and remote clients. This makes for much less coding of the interface between the view layer and back-end. We used this functionality with Flex. It meant we didn't have to write an ActionScript delegate for every single DAO method for every single type of DTO. For most cases, we only needed the basic methods provided by the single general remote DAO service. This saved us a lot of time and eliminated a huge area of possible errors.

We were also able to write Flex components that integrated automatically with the Search objects, and updated themselves when users sorted by columns and selected filters. This greatly reduced the amount and complexity of view-layer code we needed to write.

See:
`com.googlecode.genericdao.dao.hibernate.FlexDAOAdapter, com.googlecode.genericdao.dao.hibernate.original.FlexDAOAdapter`

## Customizable Generic DAOs ##
The framework comes with two different generic DAO implementations (standard and original), each exposing different methods. But no one is limited to using these two, new generic DAOs can be created with only a little work and testing. Just extend `BaseDAOImpl`. It provides many protected methods to use in DAO implementations.

See:
`com.googlecode.genericdao.dao.hibernate.BaseDAOImpl`

## DAO Dispatcher ##
One use case is this: We have a single general remote DAO to expose to a Flex client. The GeneralDAO implementation works for almost all objects, but we have special code for handling saves for User objects. We can use a dispatcher. A dispatcher has the same interface as a GeneralDAO but when it receives a method call, it forwards to call to the correct DAO for the object type it is dealing with.

We provide a `DAODispatcher` implementation for both of the included DAOs. These default to use the `GeneralDAO` implementation unless another specific DAO has been configured for the object type in question. Implementations of GenericDAO can be specified for these specific DAOs, but any class that has a method matching the signature of the GenericDAO method may be used, even if it does not implement the GenericDAO interface. This last feature can be useful if the call should forward to a service layer class.

See:
`com.googlecode.genericdao.dao.hibernate.DAODispatcher, com.googlecode.genericdao.dao.hibernate.original.DAODispatcher`

# Sample Projects #
As we all know, an example is worth 1000 wiki pages, so we have set up two sample projects in SVN.
  * _/trunk/sample/hibernate-maven-web_ - a web project using standard Hibernate and Spring MVC
  * _/trunk/sample/jpa-hibernate-maven_ - a project using JPA with Hibernate Entity Manager and Spring

Both projects use Maven and include Eclipse project files (which are only useful if you have all the right plugins). Both projects contain a README.TXT with instructions to build and run the project. Neither is particularly impressive in itself. The main purpose is to demonstrate basic configuration and usage of the framework.

# Details and Tips #
### Hibernate session strategy ###
By default the DAOs and SearchFacade use `SessionFactory.getCurrentSession()` to get the session to use. However, you can change this behavior by overriding the protected getSession() method of a DAO. For example if you want to set the session manually, you could write something like this.

```
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

	...
}
```