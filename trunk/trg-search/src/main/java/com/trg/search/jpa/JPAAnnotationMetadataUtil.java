package com.trg.search.jpa;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.proxy.HibernateProxyHelper;

import com.trg.search.Metadata;
import com.trg.search.MetadataUtil;

public class JPAAnnotationMetadataUtil implements MetadataUtil {

	public Metadata get(Class<?> klass) {
		return JPAAnnotationMetadata.getMetadata(klass);
	}

	public Metadata get(Class<?> rootEntityClass, String propertyPath) {
		Metadata md = get(rootEntityClass);
		if (propertyPath == null || propertyPath.equals("")) {
			return md;
		} else {
			for (String prop : propertyPath.split("\\.")) {
				md = md.getPropertyType(prop);
				if (md == null)
					throw new IllegalArgumentException("Property path '" + propertyPath + "' invalid for type " + rootEntityClass.getName());
			}
			return md;
		}
	}

	public Serializable getId(Object object) {
		Metadata md = get(object.getClass());
		return md.getIdValue(object);
	}

	public boolean isId(Class<?> rootClass, String propertyPath) {
		if (propertyPath == null || "".equals(propertyPath))
			return false;
		// with hibernate, "id" always refers to the id property, no matter what
		// that property is named. just make sure the segment before this "id"
		// refers to an entity since only entities have ids.
		if (propertyPath.equals("id")
				|| (propertyPath.endsWith(".id") && get(rootClass, propertyPath.substring(0, propertyPath.length() - 3))
						.isEntity()))
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
			return propertyPath.equals(get(rootClass).getIdProperty());
		}
	}

	public <T> Class<T> getUnproxiedClass(Class<?> klass) {
		throw new RuntimeException("Method not implemented");
	}
	
	public <T> Class<T> getUnproxiedClass(Object entity) {
		throw new RuntimeException("Method not implemented");
	}
}
