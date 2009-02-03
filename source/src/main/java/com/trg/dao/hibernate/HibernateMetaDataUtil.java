package com.trg.dao.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

import com.trg.dao.MetaDataUtil;

public class HibernateMetaDataUtil implements MetaDataUtil {

	private static Map<SessionFactory, HibernateMetaDataUtil> map = new HashMap<SessionFactory, HibernateMetaDataUtil>();

	public static HibernateMetaDataUtil getInstanceForSessionFactory(SessionFactory sessionFactory) {
		HibernateMetaDataUtil instance = map.get(sessionFactory);
		if (instance == null) {
			instance = new HibernateMetaDataUtil();
			instance.sessionFactory = sessionFactory;
			map.put(sessionFactory, instance);
		}
		return instance;
	}

	private SessionFactory sessionFactory;

	private HibernateMetaDataUtil() {
	}

	// --- Public Methods ---

	public boolean isSQLStringType(Class<?> rootClass, String propertyPath) {
		Type type = getPathType(rootClass, propertyPath);
		if (type == null) {
			return false;
		} else {
			int[] types = type.sqlTypes((Mapping) sessionFactory);
			return types.length == 1 && (types[0] == java.sql.Types.VARCHAR || types[0] == java.sql.Types.CHAR);
		}
	}

	public Class<?> getExpectedClass(Class<?> rootClass, String propertyPath) {
		Type type = getPathType(rootClass, propertyPath);
		if (type == null) {
			return rootClass;
		} else {
			return type.getReturnedClass();
		}
	}

	public Class<?> getCollectionElementClass(Class<?> rootClass, String propertyPath) {
		Type type = getPathType(rootClass, propertyPath);
		if (type == null || !type.isCollectionType()) {
			throw new IllegalArgumentException("The property '" + propertyPath + "' on class " + rootClass
					+ " is not a collection.");
		} else {
			return ((CollectionType) type).getElementType((SessionFactoryImplementor) sessionFactory).getReturnedClass();
		}
	}

	public boolean isEntity(Class<?> rootClass, String propertyPath) {
		Type type = getPathType(rootClass, propertyPath);
		if (type == null) {
			try {
				sessionFactory.getClassMetadata(rootClass);
				return true;
			} catch (HibernateException ex) {
				return false;
			}
		} else {
			return type.isEntityType();
		}
	}

	public boolean isCollection(Class<?> rootClass, String propertyPath) {
		Type type = getPathType(rootClass, propertyPath);
		if (type == null) {
			return false;
		} else {
			return type.isCollectionType();
		}
	}

	public Serializable getId(Object object) {
		if (object == null)
			throw new NullPointerException("Cannot get ID from null object.");

		return sessionFactory.getClassMetadata(object.getClass()).getIdentifier(object, EntityMode.POJO);
	}

	private Type getPathType(Class<?> rootClass, String propertyPath) {
		if (propertyPath == null || "".equals(propertyPath))
			return null;

		String[] chain = propertyPath.split("\\.");

		try {
			Type type = sessionFactory.getClassMetadata(rootClass).getPropertyType(chain[0]);

			outer: for (int i = 1; i < chain.length; i++) {
				String property = chain[i];
				if (type.isEntityType()) {
					type = sessionFactory.getClassMetadata(((EntityType) type).getName()).getPropertyType(property);
				} else if (type.isComponentType()) {
					String[] propNames = ((AbstractComponentType) type).getPropertyNames();
					for (int j = 0; j < propNames.length; j++) {
						if (propNames[j].equals(property)) {
							type = ((AbstractComponentType) type).getSubtypes()[j];
							continue outer;
						}
					}
					throw new HibernateException("");
				} else {
					throw new RuntimeException("The property path contains an unexpected property type: "
							+ type.toString());
				}
			}

			return type;

		} catch (HibernateException ex) {
			throw new PropertyNotFoundException("Could not find property '" + propertyPath + "' on class " + rootClass
					+ ".");
		}
	}

}
