package com.trg.search.hibernate;

import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

import com.trg.search.Metadata;

public class HibernateNonEntityMetadata implements Metadata {

	private SessionFactory sessionFactory;
	private Type type;
	private Class<?> collectionType;
	
	public HibernateNonEntityMetadata(SessionFactory sessionFactory, Type type, Class<?> collectionType) {
		this.sessionFactory = sessionFactory;
		this.type = type;
		this.collectionType = collectionType;
	}
	
	public String getIdProperty() {
		return null;
	}

	public Metadata getIdType() {
		return null;
	}

	public Serializable getIdValue(Object object) {
		return null;
	}

	public Class<?> getJavaClass() {
		return type.getReturnedClass();
	}

	public String[] getProperties() {
		if (type.isComponentType())
			return ((ComponentType)type).getPropertyNames();
		else
			return null;
	}

	public Metadata getPropertyType(String property) {
		if (!type.isComponentType())
			return null;
		
		int i = getPropertyIndex(property);
		if (i == -1) {
			return null;
		} else {
			Type pType = ((ComponentType)type).getSubtypes()[i];
			Class<?> pCollectionType = null;
			if (pType.isCollectionType()) {
				pType = ((CollectionType)pType).getElementType((SessionFactoryImplementor) sessionFactory);
				pCollectionType = pType.getReturnedClass();
			}
			if (pType.isEntityType()) {
				return new HibernateEntityMetadata(sessionFactory, sessionFactory.getClassMetadata(((EntityType)pType).getName()), pCollectionType);
			} else {
				return new HibernateNonEntityMetadata(sessionFactory, pType, pCollectionType);
			}
		}
	}

	public Object getPropertyValue(Object object, String property) {
		if (!type.isComponentType())
			return null;
		int i = getPropertyIndex(property);
		if (i == -1) {
			return null;
		} else {
			return ((ComponentType)type).getPropertyValue(object, i, EntityMode.POJO);
		}		
	}

	public boolean isCollection() {
		return collectionType != null;
	}
	
	public Class<?> getCollectionClass() {
		return collectionType;
	}

	public boolean isEmeddable() {
		return type.isComponentType();
	}

	public boolean isEntity() {
		return false;
	}

	public boolean isNumeric() {
		return Number.class.isAssignableFrom(getJavaClass());
	}

	public boolean isString() {
		int[] types = type.sqlTypes((Mapping) sessionFactory);
		return types.length == 1 && (types[0] == java.sql.Types.VARCHAR || types[0] == java.sql.Types.CHAR);
	}
	
	
	private int getPropertyIndex(String property) {
		String[] properties = getProperties();
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].equals(property)) {
				return i;
			}
		}
		return -1;
	}
}
