# Introduction #
Hibernate Generic DAO frame work allows can be used with original Hibernate or the Hibernate JPA implementation. It can also (in theory) be used with other JPA providers (ex: OpenJPA, TopLink) with some tweaking. Here we lay out the different installation options.

# Installation #
## Maven ##
Hibernate Generic DAO is available from the Maven Central repository. Just add it as a dependency.

If you want to use standard hibernate without JPA, include the following dependency...
```
<dependency>
	<groupId>com.googlecode.genericdao</groupId>
	<artifactId>dao-hibernate</artifactId>
	<version>1.2.0</version> <!-- use current version -->
</dependency>
```

If you want to use JPA with the Hibernate JPA implementation, include these dependencies instead...
```
<!-- This first dependency includes all the JPA implementations for the DAOs -->
<dependency>
	<groupId>com.googlecode.genericdao</groupId>
	<artifactId>dao</artifactId>
	<version>1.2.0</version> <!-- use current version -->
</dependency>
<!-- This second one includes the Hibernate Entity Manager plugin for the framework -->
<dependency>
	<groupId>com.googlecode.genericdao</groupId>
	<artifactId>search-jpa-hibernate</artifactId>
	<version>1.2.0</version> <!-- use current version -->
</dependency>
```

#### Latest Maven Snapshot Build ####
We also release periodic development builds. These include the latest bug fixes and enhancements and passes all unit tests but may not be ready for a full release. These builds are available from the Sonatype Open Source snapshot repository.
```
<repository>
        <id>sonatype-oss-repo</id>
        <name>Sonatype Open Source maven snapshot repo</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>
```

Add this repository to your pom.xml and set the version number of the Hibernate Generic DAO dependencies to the snapshot version, (i.e. `"1.2.1-SNAPSHOT"`)

## Without Maven ##
If you're not using Maven, you can manually download the jars from the Maven repository at http://repo1.maven.org/maven2/com/googlecode/genericdao/

The DAO requires both the _search_ jar and the _dao_ jar.

http://repo1.maven.org/maven2/com/googlecode/genericdao/dao/1.2.0/dao-1.2.0.jar <br />
http://repo1.maven.org/maven2/com/googlecode/genericdao/search/1.2.0/search-1.2.0.jar

If you are using standard Hibernate without JPA, you will also need the _search-hibernate_ and _dao-hibernate_ jars.

http://repo1.maven.org/maven2/com/googlecode/genericdao/dao-hibernate/1.2.0/dao-hibernate-1.2.0.jar <br />
http://repo1.maven.org/maven2/com/googlecode/genericdao/search-hibernate/1.2.0/search-hibernate-1.2.0.jar

If you are using JPA with Hibernate Entity Manager, download just the _search-jpa-hibernate_ jar instead.

http://repo1.maven.org/maven2/com/googlecode/genericdao/search-jpa-hibernate/1.2.0/search-jpa-hibernate-1.2.0.jar

If you like, you can also get the source jars to help out your IDE with Javadocs and to help yourself out being able to see what's going on (XXX-X.X.X-sources.jar).

http://repo1.maven.org/maven2/com/googlecode/genericdao/dao/1.2.0/dao-1.2.0-sources.jar <br />
http://repo1.maven.org/maven2/com/googlecode/genericdao/search/1.2.0/search-1.2.0-sources.jar

http://repo1.maven.org/maven2/com/googlecode/genericdao/dao-hibernate/1.2.0/dao-hibernate-1.2.0-sources.jar <br />
http://repo1.maven.org/maven2/com/googlecode/genericdao/search-hibernate/1.2.0/search-hibernate-1.2.0-sources.jar

http://repo1.maven.org/maven2/com/googlecode/genericdao/search-jpa-hibernate/1.2.0/search-jpa-hibernate-1.2.0-sources.jar

You will also need the Hibernate 3.3 (or later) jars and their dependencies. I'll leave you to do all the hibernate configuration.

The project has one other dependency. That is Simple Logging Facade for Java (SLF4J). I think Hibernate 3.3 also requires this to be configured.


---

### Installing only the Search without the DAOs ###
For the first case, use this dependency instead:
```
<dependency>
	<groupId>com.googlecode.genericdao</groupId>
	<artifactId>search-hibernate</artifactId>
	<version>1.2.0</version>
</dependency>
```

For the second you only need:
```
<dependency>
	<groupId>com.googlecode.genericdao</groupId>
	<artifactId>search-jpa-hibernate</artifactId>
	<version>1.2.0</version>
</dependency>
```

If not using Maven, simply don't download the _dao..._ jars. Only get the _search..._ ones.

---


# Configuration #
The Hibernate Generic DAO library is designed not to mandate any configuration. It's just a collection of POJOs that you can decide how to best to use. However, there are some things that the API requires in order to work.

### Generic DAOs ###
The Generic DAO classes will not work unless they are sub-classed with specific generic types. The idea of a generic DAO is to be a DAO for a specific domain object. The way to specify which domain object is to create a sub-class of GenericDAO that has the generic types of the domain object and its identifier. See [GenericDAOExamples](GenericDAOExamples.md) for examples.

### Standard Hibernate: Session and SessionFactory ###
Each DAO and the SearchFacade needs both a Session and a SessionFactory in order to work. By default they expose a public `setSessionFactory()` method and use `SessionFactory.getCurrentSession()` to get a Session whenever they need it. With this configuration the SessionFactory is required and is generally set once when the object is initialized.

So if using `getCurrentSession()` is what you want, all you have to do is make sure that each DAO has its SessionFactory set before it is used. If you need to do things differently, look at the "Hibernate session strategy" section in the "Details and Tips" on the UserGuide.

To see an example project configuration using Spring auto-wiring, have a look at the web project in SVN at /trunk/sample/hibernate-maven-web.

### JPA using Hibernate Entity Manager ###
This configuration is somewhat more difficult because of ability to use different JPA providers.

Each **DAO** requires a JPASearchProcessor and an EntityManager. Generally, a single Search Processor will be associated with an instance of a DAO for the lifetime of the instance, while a new "current" EntityManager will be injected as needed. Make sure that any EntityManager that is used is associated with the same persistence unit (i.e. EntityManagerFactory) as the SearchProcessor.

The **JPASearchProcessor** is designed to be used as a singleton. The constructor requires a MetadataUtil instance. A JPASearchProcessor can only be used with EntityManagers that are associated with the same persistence unit as that MetadataUtil. If an application has multiple persistence units, it will need to have multiple corresponding Search Processors.

**MetadataUtil** is the layer of abstraction that allows the framework to work (in theory) with a variety of JPA providers. In order to use a particular JPA provider, an implementation of MetadataUtil must be provided. Currently the framework only has an implementation for Hibernate.

In theory, different implementations of MetadataUtil will require different configurations. The Hibernate implementation is **`com.googlecode.genericdao.search.hibernate.HibernateMetadataUtil`**. You will need one instance per persistence unit (i.e. EntityManagerFactory). Use `com.googlecode.genericdao.search.jpa.hibernate.HibernateMetadataUtil.getInstanceForEntityManagerFactory(HibernateEntityManagerFactory)` to get these instances.

To see an example project configuration using Spring auto-wiring, have a look at the project in SVN at /tags/1.2.0/sample/jpa-hibernate-maven.

**JPASearchFacade** is configured in basically the same way as the DAOs.