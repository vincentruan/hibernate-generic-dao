package com.trg.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.NonUniqueResultException;

import com.trg.search.Search;
import com.trg.search.SearchResult;

/**
 * Interface for general Data Access Object that can be used for any type domain
 * object. A single instance implementing this interface can be used for
 * multiple types of domain objects.
 * 
 * @author dwolverton
 */
@SuppressWarnings("unchecked")
public interface GeneralDAO {

	/**
	 * Add the specified object as a new entry in the database.
	 */
	public void create(Object object);

	/**
	 * If the id of the object is null or zero, create, otherwise update.
	 * 
	 * @return <code>true</code> if create; <code>false</code> if update.
	 */
	public boolean createOrUpdate(Object object);

	/**
	 * Delete the object with the specified id and class from the database.
	 * 
	 * @return <code>true</code> if the object is found in the database and
	 *         deleted, <code>false</code> if the item is not found.
	 */
	public boolean deleteById(Serializable id, Class<?> klass);

	/**
	 * Delete the specified object from the database.
	 * 
	 * @return <code>true</code> if the object is found in the database and
	 *         deleted, <code>false</code> if the item is not found.
	 */
	public boolean deleteEntity(Object object);

	/**
	 * Get the object with the specified id and class from the database.
	 */
	public <T> T fetch(Serializable id, Class<T> klass);

	/**
	 * Get a list of all the objects of the specified type.
	 */
	public <T> List<T> fetchAll(Class<T> klass);

	/**
	 * Update the corresponding object in the database with the properties of
	 * the specified object. The corresponding object is determined by id.
	 */
	public void update(Object object);

	/**
	 * Search for objects given the search parameters in the specified
	 * <code>Search</code> object.
	 */
	public List search(Search search);

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>Search</code> if there were no paging or maxResult limits.
	 */
	public int searchLength(Search search);

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 */
	public SearchResult searchAndLength(Search search);

	/**
	 * Search for a single result using the given parameters.
	 */
	public Object searchUnique(Search search) throws NonUniqueResultException;

	/**
	 * Returns true if the object is connected to the current Hibernate session.
	 */
	public void flush();

	/**
	 * Flushes changes in the Hibernate cache to the database.
	 */
	public boolean isConnected(Object object);

	/**
	 * Refresh the content of the given entity from the current database state.
	 */
	public void refresh(Object object);
}