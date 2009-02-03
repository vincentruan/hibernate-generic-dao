package com.trg.dao;

import java.io.Serializable;

public interface MetaDataUtil {
	/**
	 * Get the type of a property of a bean class. The property can be simple
	 * ("name") or nested ("organization.name").
	 * 
	 * @throws PropertyNotFoundException
	 *             if the class does not have the given bean property.
	 */
	public Class<?> getExpectedClass(Class<?> rootClass, String propertyPath);

	/**
	 * Get the value of the ID property of an entity.
	 */
	public Serializable getId(Object object);

	/**
	 * Return true if the property at the given property path is an entity. It
	 * could also, for example be a component or a value type.
	 */
	public boolean isEntity(Class<?> rootClass, String propertyPath);
	
	/**
	 * Return true if the property at the given property path is a collection. It
	 * could also, for example be a component or a value type.
	 */
	public boolean isCollection(Class<?> rootClass, String propertyPath);

	/**
	 * Return true if the property at the given property path is persisted as a
	 * string (char or varchar) type in the database.
	 */
	public boolean isSQLStringType(Class<?> rootClass, String propertyPath);
}
