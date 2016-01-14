# Introduction #

Since [revision 0](https://code.google.com/p/hibernate-generic-dao/source/detail?r=0).5.0, the structure has gotten pretty complicated on the framework backend. This is necessary in order to provide the flexibility of using Hibernate or JPA with any number of JPA providers. But the complexity makes it necessary now to document clearly how things are arranged.


# The Projects #

There are 7 separate projects that make up the framework. Each corresponds to a different Maven artifact.

  * _test-base_
    * This project has utilities, base classes, and configuration for testing the main projects. It is only referenced as a test dependency and will never be used by an end user.
  * _test-search_
    * This project has utilities, base classes, and configuration for testing the search in main projects. It is only referenced as a test dependency and will never be used by an end user.
    * **depends on:** _test-base_, _search_
  * _search_
    * The core project of the framework. This contains the Search Object interfaces, the Base Search Processor, and the `SearchFacade` interface. It also contains the JPA Search Processor and JPA Search Facade implementation.
    * **packages:** com.googlecode.genericdao.search, com.googlecode.genericdao.search.jpa, com.googlecode.genericdao.search.flex
    * **depends on:** NONE
  * _search-hibernate_
    * The standard Hibernate implementation of for search. Contains the Hibernate Search Processor and the HibernateMetadataUtil. Also includes Hibernate dependencies in the POM.
    * **packages:** com.googlecode.genericdao.search.hibernate
    * **depends on:** _search_ (and _test-search_ for test phase)
  * _search-jpa-hibernate_
    * The Hibernate Entity Manager implementation for use with JPA. Contains utility for getting HibernateMetadataUtil from an EntityManager and the Hibernate Entity Manager dependencies in the POM. Also used to test JPA Search.
    * **packages:** com.googlecode.genericdao.search.jpa.hibernate
    * **depends on:** _search_, _search-hibernate_ (and _test-search_ for test phase)
  * _dao_
    * Contains utilities for DAOs and the JPA DAO implementation.
    * **packages:** com.googlecode.genericdao.dao, com.googlecode.genericdao.dao.jpa
    * **depends on:** _search_ (and _test-base_ for test phase)
  * _dao-hibernate_
    * Standard Hibernate DAO implementations (both standard and original DAOs).
    * **packages:** com.googlecode.genericdao.dao.hibernate, com.googlecode.genericdao.dao.hibernate.original
    * **depends on:** _search_, _search-hibernate_, _dao_ (and _test-base_ for test phase)

# Building the Framework #
The framework should be built with Maven. All seven projects can be built together through the parent project (i.e. the trunk folder itself). Just use the `mvn package` to build a jar or `mvn install` to build the jar and install it in your local Maven repository.

If you have a need to alter and build the framework, you can check out just the project(s) you need to modify and rebuild them. If there are projects you don't need to modify, you can just get the original from the online Maven repository. Of course you are free to check out all the projects and rebuild all of them if you like.

The projects in SVN contain Eclipse project configuration files. If you are using Eclipse and you have all the right plugins and you're lucky, you may be able to check them out as Eclipse projects and hit the ground running. Each of the above projects is a separate Eclipse project. Each of the sample apps is also an Eclipse project. I'm mainly using the Sonatype, Inc. Maven Integration for Eclipse plugin. For the sample web project, I'm using the WST (Web Standard Tools) plugin and the Maven plugin to work with that. I'm using the Tigris.org SVN plugin for synchronization with the repository.