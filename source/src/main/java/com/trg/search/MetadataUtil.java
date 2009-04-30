package com.trg.search;

import java.io.Serializable;

import org.hibernate.PropertyNotFoundException;

/**
 * The framework uses an implementation of this interface to introspect the
 * objects and relationships maintained by the JPA provider.
 * 
 * This interface provides a layer of abstraction between the framework and the
 * underlying JPA provider (ex. Hibernate). By switching out the implementation
 * of this interface, the framework should be able to be used with different JPA
 * providers.
 * 
 * @author dwolverton
 */
public interface MetadataUtil {
	/**
	 * Get the value of the ID property of an entity.
	 */
	public Serializable getId(Object object);

	/**
	 * Return true if the property at the given property path is the id of some
	 * entity.
	 */
	public boolean isId(Class<?> rootClass, String propertyPath);

	/**
	 * Get the Metadata for an entity class.
	 * 
	 * @throws IllegalArgumentException
	 *             if the class is not a Hibernate entity.
	 */
	public Metadata get(Class<?> klass);

	/**
	 * Get the Metadata for a property of an entity class. The property can be
	 * simple ("name") or nested ("organization.name").
	 * 
	 * @throws IllegalArgumentException
	 *             if the root class is not a Hibernate entity.
	 * @throws PropertyNotFoundException
	 *             if the class does not have the given property.
	 */
	public Metadata get(Class<?> rootEntityClass, String propertyPath);
}
