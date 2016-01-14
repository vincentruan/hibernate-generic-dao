
---

**NOTICE** Progress on this project has been halted for now. See notice on [Project Home](http://code.google.com/p/hibernate-generic-dao/).

---

## 1.2.0 (4/22/2013 current release) ##
  * [Issue 99](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=99) : Hibernate 4 Upgrade
  * [Issue 98](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=98) : search-1.1.0.jar is incompatible with java 5

## 1.1.0 (10/19/2011) ##
  * [Issue 73](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=73) : Custom filters, fields and sorts
  * [Issue 61](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=61) : Do not use PropertyNotFoundException in MetaDataUtil.get
  * [Issue 80](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=80) : Add paging to searchUnique()
  * [Issue 90](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=90) : Positional Parameters syntax in Query using JPA DAO

## 1.0.0  (2/24/2011) ##
  * Deployment to central maven repo.
  * Refactoring of maven architecture with parent pom.xml for whole framework.
  * [Issue 54](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=54) : Refactoring of package names.
  * [Issue 59](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=59) : Should use Entity Name in the JPQL in terms of JPA specification
  * [Issue 66](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=66) : JPQL can not support asterisk (`*`) in COUNT function
  * [Issue 68](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=68) : Alias missed in JPABaseDAO's queries

## 0.5.1 (3/11/2010) ##
  * Fixed [Issue 35](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=35) : Missing some operators in Filter.toString()
  * Fixed [Issue 36](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=36) : Removed left-over debug statement.
  * Fixed [Issue 40](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=40) : Sub-classing sub classes of GenericDAOImpl.
  * Fixed [Issue 44](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=44) : BUG: SearchUtil.shallowCopy is missing setDistinct()
  * Fixed [Issue 49](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=49) : Saving not working with proxy objects
  * Fixed [Issue 51](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=51) : Referencing the root object using addField()
  * Fixed [Issue 57](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=57) / [Issue 58](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=58) : HibernateSearchProcessor prints HQL to console
  * Completed [Issue 41](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=41) : Return type of searchGeneric
  * Default JPA Metadata implementation (still somewhat limited)

## 0.5.0 (05/27/2009) ##
  * Added implementation for JPA.
  * Fixed [Issue 34](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=34) HibernateMetadataUtil not working with proxy objects.
  * Added a couple example projects to demonstrate configuration.

## 0.4.3 (05/1/2009) ##
  * Added 'distinct' option to search.
  * Formalized separation between DAOs and search.
  * Added find by example functionality ([Issue 27](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=27))

## 0.4.2 (03/13/2009) ##
  * Added collection operators. (see blog)
  * Allowed use of special .id, .class, .size properties in Filters.
  * Addressed [Issue 25](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=25), handling nulls in sorts, filters, fields, etc.

## 0.4.1 (01/20/2009) ##
  * Added option to ignore case on sorts ([Issue 24](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=24)).
  * Implemented ignoring Filters with null values ([Issue 22](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=22)).
  * Implemented [Issue 20](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=20) (fields & fetches).
  * Refactored Search bean to use interfaces.
  * Provided a SearchUtil class to aid in working with search interfaces and creating implementations of search interfaces.
  * Added methods to SearchUtil for merging parts of Searches

## 0.4.0 (12/31/2008) ##
  * Addressed problems with Components (@Embedded) as per [Issue 19](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=19).
  * Tested Search w/ polymorphism.
  * Improved Util.callMethod to be more robust.
  * Created new default DAO with different methods. (see blog)
  * Created new default Flex remote DAO with different methods.
  * Added ILIKE operator ([Issue 21](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=21)).
  * Changed saveOrUpdate() to work with assigned as well as generated ids.
  * Added tests for many-to-many relationships and compound ids.
  * Refactored Search objects to be more bean-like

  * Known Issues
    * Javadocs for Search beans are out of date.

## 0.3.4 (12/16/2008) ##
  * Use Hibernate MetaData for Util.getExpectedClass()
  * Refactor RemoteDAO into a separate Remote adapter and DAO dispatcher
  * Make RemoteDAO, GeneralDAO and GenericDAO implementations thinner and more replacable. This way developers can customize their own DAO and Remote methods for the particular project and technology.
  * Separate RemoteDAO into separate remote adapter and DAO dispatcher
  * Use RemoteDAO adapters for specific technologies

## 0.3.3 (11/25/2008) ##
  * Cleaned up pom.xml ([Issue #14](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=#14)):
    * Removed unneeded dependencies and repositories
    * Moved most dependencies to `test` scope
  * Upgraded to Hibernate 3.3.1GA
  * `deleteById()` no longer throws an error if no matching object is found in the database. ([Issue #15](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=#15)))
  * `deleteById()` and `deleteEntity()` now return boolean: true if found and deleted, false if not found.



# Future #

## 1.2.1 ##
  * [Issue 93](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=93) : HibernateMetadataUtil causes memory leak when dealing with many session factories
  * [Issue 94](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=94) : Why setDistinct() return IMutableSearch instead of Search?

## 1.2.2 ##
  * [Issue 47](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=47) : Add load method with lock mode option
  * [Issue 50](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=50) / [Issue 64](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=64) : More extensible HibernateSearchFacade and HibernateSearchProcessor
  * Deprecate "original" daos?
  * See about removing Hibernate version dependency.

## Unknown ##
  * JPA 2.0 metadata support
  * Look at security with remote DAOs.
  * Possibly add SIZE and IS EMPTY for collections in fields.
  * Look at case sensitivity options. ([Issue 28](https://code.google.com/p/hibernate-generic-dao/issues/detail?id=28))
  * STARTS\_WITH, ENDS\_WITH, CONTAINS operators
  * Testing more data model configurations