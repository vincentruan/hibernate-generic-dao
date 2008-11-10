package com.trg.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.NonUniqueResultException;

import com.trg.search.Search;
import com.trg.search.SearchResult;

/**
 * Interface for a Data Access Object that can be used for a single specified
 * type domain object. A single instance implementing this interface can be used
 * only for the type of domain object specified in the type parameters.
 * 
 * @author dwolverton
 * 
 * @param <T>
 *            The type of the domain object for which this instance is to be
 *            used.
 * @param <ID>
 *            The type of the id of the domain object for which this instance is
 *            to be used.
 */
public interface GenericDAO<T, ID extends Serializable> {

	/**
	 * Add the specified object as a new entry in the database.
	 */
	public void create(T object);

	/**
	 * If the id of the object is null or zero, create, otherwise update.
	 * 
	 * @return true if create; false if update.
	 */
	public boolean createOrUpdate(T object);

	/**
	 * Delete the object of this type with the specified id from the database.
	 */
	public void deleteById(ID id);

	/**
	 * Delete the specified object from the database.
	 */
	public void deleteEntity(T object);

	/**
	 * Get the object of this type with the specified id from the database.
	 */
	public T fetch(ID id);

	/**
	 * Get a list of all the objects of this type.
	 */
	public List<T> fetchAll();

	/**
	 * Update the corresponding object in the database with the properties of
	 * the specified object. The corresponding object is determined by id.
	 */
	public void update(T object);

	/**
	 * Search for objects of this type given the search parameters in the
	 * specified <code>Search</code> object.
	 */
	public List<T> search(Search search);

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
	public SearchResult<T> searchAndLength(Search search);

	/**
	 * Search for objects given the search parameters in the specified
	 * <code>Search</code> object. Return an untyped result list. The result
	 * type can be determined by fetch mode and fetches on the search.
	 */
	@SuppressWarnings("unchecked")
	public List searchGeneric(Search search);

	/**
	 * Search for a single result using the given parameters.
	 */
	public Object searchUnique(Search search) throws NonUniqueResultException;

	/**
	 * Returns true if the object is connected to the current Hibernate session.
	 */
	public boolean isConnected(Object object);

	/**
	 * Flushes changes in the Hibernate cache to the database.
	 */
	public void flush();

	/**
	 * Refresh the content of the given entity from the current database state.
	 */
	public void refresh(Object object);
}