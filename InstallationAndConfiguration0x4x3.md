# Installation #
## Maven ##
If your project uses Maven, then simply add the repository and dependency...
```
<repository>
        <id>trg-dao-repo</id>
        <name>Repository for The Revere Group's Hibernate Generic DAO framework</name>
        <url>http://hibernate-generic-dao.googlecode.com/svn/trunk/maven-repo/</url>
</repository>

<dependency>
        <groupId>com.trg</groupId>
        <artifactId>trg-dao</artifactId>
        <version>0.4.3</version> <!-- or whatever the latest version is -->
</dependency>
```

## Other ##
Download the jars from http://hibernate-generic-dao.googlecode.com/svn/trunk/maven-repo/com/trg/

The DAO requires both the trg-dao jars and the trg-search jars. Inside each of these folders, select the folder with the version you would like (the latest is generally the best). You only need the binary jar (trg-dao-X.X.X.jar), but you can also get the source jar to
help out your IDE with Javadocs and to help yourself out being able to see what's going
on (trg-dao-X.X.X-sources.jar).

You will also need the Hibernate 3.3 jars and their dependencies. I'll leave you to do all the hibernate configuration.

The project has one other dependency. That is Simple Logging Facade for Java (SLF4J). I think Hibernate 3.3 also requires this to be configured.


---

### Installing only the Search without the DAOs ###
Use this dependency instead:
```
<dependency>
        <groupId>com.trg</groupId>
        <artifactId>trg-dao</artifactId>
        <version>0.4.3</version> <!-- or whatever the latest version is -->
</dependency>
```

If not using Maven, simply don't download the trg-dao jars. Only get the trg-search ones.

---


# Configuration #
The Hibernate Generic DAO library is designed not to mandate any configuration. It's just a collection of POJOs that you can decide how to best to use. However, there are some things that the API requires in order to work.

### Generic DAOs ###
The Generic DAO classes will not work unless they are sub-classed with specific generic types. The idea of a generic DAO is to be a DAO for a specific domain object. The way to specify which domain object is to create a sub-class of GenericDAO that has the generic types of the domain object and its identifier. See GenericDAOExamples for examples.

### Hibernate Session and SessionFactory ###
Each DAO and the SearchFacade needs both a Session and a SessionFactory in order to work. By default they expose a `setSessionFactory()` method and use `SessionFactory.getCurrentSession()` to get a Session whenever they need it.

If using `getCurrentSession()` is what you want, all you have to do is make sure that each DAO has its SessionFactory set before it is used. If you need to do things differently, look at the "Hibernate session strategy" section in the "Details and Tips" on the UserGuide.


To see an example project configuration using Spring auto-wiring, have a look at the test portion of the source code. The spring configuration is in trunk/trg-shared/src/test/resources and trunk/trg-dao/src/test/resources. The Java packages of interest are trunk/trg-dao/src/test/java/`com.test.dao.standard` and trunk/trg-shared/src/test/java/`com.test.model`.