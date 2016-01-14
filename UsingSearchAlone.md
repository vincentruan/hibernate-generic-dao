# Introduction #

The framework's search functionality is now separated into a separate stand-alone module that can be used with or without the DAOs. (The DAOs, however, cannot be used without the search.)

# Configuration #

See InstallationAndConfiguration for the basics on how to include the search library in your project.

# Using the API #

The API consists of one main interface `SearchFacade` (http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/com/googlecode/genericdao/search/SearchFacade.html). All of the methods for accessing the search functionality are here. Just create `ISearch` objects and pass them in. For more information on using `ISearch`, see [Search](Search.md).

The tricky part is getting an instance of `SearchFacade`. At this point the only implementation available is for Hibernate. It is `HibernateSearchFacade`.

The `HibernateSearchFacade` needs a Hibernate `Session` on which to perform it's operations and also a `SessionFactory` for some Metadata. By default `HibernateSearchFacade` requires only that a `SessionFactory` be passed in (either through the constructor or through `setSessionFactory`. It then uses `SessionFactory.getCurrentSession()` to get a session whenever it needs one.

Here's an example of how this might be set up with Spring framework:

**applicationContext.xml**
```
...
<bean id="searchFacade" class="com.googlecode.genericdao.search.hibernate.HibernateSearchFacade">
    <!-- The Hibernate sessionFactory bean is defined elsewhere -->
    <property name="sessionFactory" ref="sessionFactory"/>
</bean>
...
```

**Class where facade is used (must be defined as spring bean)**
```
...
private SearchFacade searchFacade;

@Resource //Spring will automatically fill in properties marked with
          //this annotation
public setSearchFacade(SearchFacade searchFacade) {
    this.searchFacade = searchFacade;
}
...
    //Use search facade anywhere in class
    searchFacade.search(search);
...
```

If using `getCurrentSession()` is not desired, you must create a sub-class of `HibernateSessionFacade` and override the protected `getSession()` method. See the "Hibernate session strategy" section in the "Details and Tips" on the UserGuide.