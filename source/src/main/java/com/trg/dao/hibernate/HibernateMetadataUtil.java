package com.trg.dao.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import com.trg.dao.Metadata;
import com.trg.dao.MetadataUtil;

public class HibernateMetadataUtil implements MetadataUtil {

	private static Map<SessionFactory, HibernateMetadataUtil> map = new HashMap<SessionFactory, HibernateMetadataUtil>();

	public static HibernateMetadataUtil getInstanceForSessionFactory(SessionFactory sessionFactory) {
		HibernateMetadataUtil instance = map.get(sessionFactory);
		if (instance == null) {
			instance = new HibernateMetadataUtil();
			instance.sessionFactory = sessionFactory;
			map.put(sessionFactory, instance);
		}
		return instance;
	}

	private SessionFactory sessionFactory;

	private HibernateMetadataUtil() {
	}

	// --- Public Methods ---

	public Serializable getId(Object object) {
		if (object == null)
			throw new NullPointerException("Cannot get ID from null object.");
		return get(object.getClass()).getIdValue(object);
	}

	public boolean isId(Class<?> rootClass, String propertyPath) {
		if (propertyPath == null || "".equals(propertyPath))
			return false;
		// with hibernate, "id" always refers to the id property, no matter what
		// that property is named. just make sure the segment before this "id"
		// refers to an entity since only entities have ids.
		if (propertyPath.equals("id")
				|| (propertyPath.endsWith(".id") && get(rootClass, propertyPath.substring(0,
						propertyPath.length() - 3)).isEntity()))
			return true;

		// see if the property is the identifier property of the entity it
		// belongs to.
		int pos = propertyPath.lastIndexOf(".");
		if (pos != -1) {
			Metadata parentType = get(rootClass, propertyPath.substring(0, pos));
			if (!parentType.isEntity())
				return false;
			return propertyPath.substring(pos + 1).equals(parentType.getIdProperty());
		} else {
			return propertyPath.equals(sessionFactory.getClassMetadata(rootClass).getIdentifierPropertyName());
		}
	}

	public Metadata get(Class<?> entityClass) {
		ClassMetadata cm = sessionFactory.getClassMetadata(entityClass);
		if (cm == null)
			throw new IllegalArgumentException("Unable to introspect " + entityClass.toString() + ". The class is not a registered Hibernate entity.");
		else
			return new HibernateEntityMetadata(sessionFactory, cm, null);
	}
	
	public Metadata get(Class<?> rootEntityClass, String propertyPath) {
		try {
			Metadata md = get(rootEntityClass);
			if (propertyPath == null || "".equals(propertyPath))
				return md;

			String[] chain = propertyPath.split("\\.");
		
			for (int i = 0; i < chain.length; i++) {
				md = md.getPropertyType(chain[i]);
			}
			
			return md;

		} catch (HibernateException ex) {
			throw new PropertyNotFoundException("Could not find property '" + propertyPath + "' on class " + rootEntityClass
					+ ".");
		}
	}

}
