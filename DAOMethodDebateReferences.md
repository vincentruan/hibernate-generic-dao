# Introduction #

These are some methods that are used on API that are comparable to our DAO APIs.

The order in which they are listed here is not meant to be significant.

# Without Descriptions #

## (A) hibernate-generic-dao (as of 0.3.3) ##

```
void create(Object object)
void update(Object object)
boolean createOrUpdate(Object object)
boolean deleteById(Serializable id, Class<?> klass)
boolean deleteEntity(Object object)
<T> T fetch(Serializable id, Class<T> klass)
<T> List<T> fetchAll(Class<T> klass)
List search(Search search)
int searchLength(Search search)
SearchResult searchAndLength(Search search)
Object searchUnique(Search search) throws NonUniqueResultException
boolean isConnected(Object object)
void flush()
void refresh(Object object)
```

## (B) Based on Hibernate Tools ##
```
void save(Object instance)
void delete(Object instance)
Object findById(Long id)
Object merge(Object instance)
void attachClean(Object instance)
void attachDirty(Object instance)
```

## (C) Based on JPA EntityManager ##
```
boolean contains(Object entity) 
<T> T find(Class<T> entityClass, Object primaryKey) 
void flush() 
void lock(Object entity, LockModeType lockMode) 
<T> T merge(T entity) 
void persist(Object entity) 
void refresh(Object entity)
void remove(Object entity)
```

## (D) Based on Hibernate Session API ##
```
boolean contains(Object object) 
void delete(Object object) 
void evict(Object object) 
void flush()
Object get(Class clazz, Serializable id)
Object load(Class theClass, Serializable id) 
Object merge(Object object) 
void persist(Object object)
void lock(Object object, LockMode lockMode) 
void refresh(Object object)
void replicate(Object object, ReplicationMode replicationMode)
Serializable save(Object object) 
void saveOrUpdate(Object object) 
void update(Object object) 
void setReadOnly(Object entity, boolean readOnly) 
```

## (E) Jukes HibernateDao ##

http://melloware.com/products/jukes/apidocs/com/melloware/jukes/db/HibernateDao.html

## (F) Hibernate Generic Data Access Objects ##
http://www.hibernate.org/328.html

```
T findById(ID id, boolean lock) //load()
List<T> findAll()
List<T> findByExample(T exampleInstance)
T makePersistent(T entity) //saveOrUpdate()
void makeTransient(T entity) //delete()
```

## (G) Don't repeat the DAO! article ##
http://www.ibm.com/developerworks/java/library/j-genericdao.html

```
PK create(T newInstance); //save()
T read(PK id); //get()
void update(T transientObject); //update()
void delete(T persistentObject); //delete()
```

## (H) Core J2EE Patterns - Data Access Object article ##
http://java.sun.com/blueprints/corej2eepatterns/Patterns/DataAccessObject.html

```
insert()
delete()
find()
update()
select() //search
```

# Descriptions #
## (A) Current (as of 0.3.3) ##

```
void create(Object object)
```
Add the specified object as a new entry in the database.


```
void update(Object object)
```
Update the corresponding object in the database with the properties of
the specified object. The corresponding object is determined by id. If there is a persistent instance with the same identifier, an exception is thrown.

```
boolean createOrUpdate(Object object)
```
If the id of the object is null or zero, create, otherwise update.
Return true if create; false if update.

```
boolean deleteById(Serializable id, Class<?> klass)
```
Delete the object with the specified id and class from the database.
Return true if the object is found in the database and deleted, false if the item is not found.

```
boolean deleteEntity(Object object)
```
elete the specified object from the database.
Return true if the object is found in the database and deleted, false if the item is not found.

```
<T> T fetch(Serializable id, Class<T> klass)
```
Get the object with the specified id and class from the database.

```
<T> List<T> fetchAll(Class<T> klass)
```
Get a list of all the objects of the specified type.

```
List search(Search search)
```
Search for objects given the search parameters in the specified
Search object.

```
int searchLength(Search search)
```
Returns the total number of results that would be returned using the
given Search if there were no paging or maxResult limits.

```
SearchResult searchAndLength(Search search)
```
Returns a SearchResult object that includes the list of
results like search() and the total length like
searchLength.

```
Object searchUnique(Search search) throws NonUniqueResultException
```
Search for a single result using the given parameters.

```
boolean isConnected(Object object)
```
Returns true if the object is connected to the current Hibernate session.

```
void flush()
```
Flushes changes in the Hibernate cache to the database.

```
void refresh(Object object)
```
Refresh the content of the given entity from the current database state.

## (B) Based on Hibernate Tools ##
```
void save(Object instance)
```
Uses `org.hibernate.Session.saveOrUpdate()`

```
void delete(Object instance)
```
Remove a persistent instance from the datastore. Uses `org.hibernate.Session.delete()`.

```
Object findById(Long id)
```
Return the persistent instance of the given entity class with the given identifier, or null if there is no such persistent instance. Uses `org.hibernate.Session.get()`

```
Object merge(Object instance)
```
Copy the state of the given object onto the persistent object with the same identifier. If there is no persistent instance currently associated with the session, it will be loaded. Return the persistent instance. If the given instance is unsaved, save a copy of and return it as a newly persistent instance. The given instance does not become associated with the session.
Uses `org.hibernate.Session.merge()`

```
void attachClean(Object instance)
```
Reassociate an object with a new session. The detached instance has to be unmodified!
Uses `org.hibernate.Session.lock()`, `LockMode.NONE`

```
void attachDirty(Object instance)
```
Same as save().


## (C) Based on JPA EntityManager ##

```
boolean contains(Object entity) 
```
Check if the instance belongs to the current persistence context.

```
<T> T find(Class<T> entityClass, Object primaryKey) 
```
Find by primary key.

```
void flush() 
```
Synchronize the persistence context to the underlying database.

```
void lock(Object entity, LockModeType lockMode) 
```
Set the lock mode for an entity object contained in the persistence context.

```
<T> T merge(T entity) 
```
Merge the state of the given entity into the current persistence context.

```
void persist(Object entity) 
```
Make an entity instance managed and persistent.

```
void refresh(Object entity)
```
Refresh the state of the instance from the database, overwriting changes made to the entity, if any.

```
void remove(Object entity)
```
Remove the entity instance.

## (D) Based on Hibernate Session API ##
```
boolean contains(Object object) 
```
Check if this instance is associated with this Session.

```
void delete(Object object) 
```
Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
Session or a transient instance with an identifier associated with existing persistent state.

```
void evict(Object object) 
```
Remove this instance from the session cache. Changes to the instance will not be synchronized with the database.

```
void flush()
```
Force this session to flush.

```
Object get(Class clazz, Serializable id)
```
Return the persistent instance of the given entity class with the given identifier, or null if there is no such persistent instance.

```
Object load(Class theClass, Serializable id) 
```
Return the persistent instance of the given entity class with the given identifier, assuming that the instance exists.
If it does not exist, throw Error.

```
Object merge(Object object) 
```
Copy the state of the given object onto the persistent object with the same identifier. If there is no persistent
instance currently associated with the session, it will be loaded. Return the persistent instance. If the given
instance is unsaved, save a copy of and return it as a newly persistent instance. The given instance does not
become associated with the session.

```
void persist(Object object)
```
void Make a transient instance persistent.  _TODO read JSR-220 for details_

```
void lock(Object object, LockMode lockMode) 
```
Obtain the specified lock level upon the given object. This may be used to perform a version check (LockMode.READ),
to upgrade to a pessimistic lock (LockMode.UPGRADE), or to simply reassociate a transient instance with a session (LockMode.NONE)

```
void refresh(Object object)
```
Re-read the state of the given instance from the underlying database, with the given LockMode.

```
void replicate(Object object, ReplicationMode replicationMode)
```
Persist the state of the given detached instance, reusing the current identifier value.

```
Serializable save(Object object) 
```
Persist the given transient instance, first assigning a generated identifier. Return the generated identifier.

```
void saveOrUpdate(Object object) 
```
Either save(Object) or update(Object) the given instance, depending upon resolution of the unsaved-value checks.

```
void update(Object object) 
```
Update the persistent instance with the identifier of the given detached instance. If there is a persistent instance with the same identifier, an exception is thrown.

```
void setReadOnly(Object entity, boolean readOnly) 
```
Set an unmodified persistent object to read only mode, or a read only object to modifiable mode.