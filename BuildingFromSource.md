See also FrameworkArchitecture for a basic understanding of how the sources and modules are structured. Thanks Tercio for your contribution to this (see discussion http://groups.google.com/group/java-generic-dao/browse_thread/thread/cae3cf17bb866fa9).

# Compiling from Source #

## Requisites ##

To compile yourself **hibernate-generic-dao** you will need a Subversion
client(SVN) and Maven 2.

Subversion client:
  * http://subversion.tigris.org/
Maven 2:
  * http://maven.apache.org/

## Checking Out the Source from Subversion ##

Checking out the **hibernate-generic-dao** source is useful if you plan
to compile it yourself.<br />
**hibernate-generic-dao** is hosted on Google Code project hosting, so
you check out the source using a Subversion client as you would for
any other project hosted on Google Code:

```
svn co http://hibernate-generic-dao.googlecode.com/svn/trunk hibernate-
generic-dao
```

## Compiling ##

Simply navigate to the hibernate-generic-dao directory where the code was checked out and run a maven build...
```
mvn clean install
```

This will compile and install all seven maven components into your local Maven repository.