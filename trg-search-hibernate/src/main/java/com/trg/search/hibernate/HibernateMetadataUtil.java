/* Copyright 2009 The Revere Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trg.search.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import com.trg.search.Metadata;
import com.trg.search.MetadataUtil;

/**
 * Implementation of MetadataUtil for Hibernate
 * 
 * A singleton instance of this class is maintained for each SessionFactory.
 * This should be accessed using
 * {@link HibernateMetadataUtil#getInstanceForSessionFactory(SessionFactory)}.
 * 
 * @author dwolverton
 */
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

	protected HibernateMetadataUtil() {
	}

	// --- Public Methods ---

	public Serializable getId(Object entity) {
		if (entity == null)
			throw new NullPointerException("Cannot get ID from null object.");
		return get(entity.getClass()).getIdValue(entity);
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
			return propertyPath.equals(sessionFactory.getClassMetadata(rootClass).getIdentifierPropertyName());
		}
	}

	public Metadata get(Class<?> entityClass) {
		ClassMetadata cm = sessionFactory.getClassMetadata(entityClass);
		if (cm == null) {
			//cm will be null if entityClass is not registered with Hibernate. However
			//there are cases where this will return a false negative. For example when
			//we have a Hibernate proxy class (e.x. test.trg.model.Person_$$_javassist_5),
			//we need that to be recognized, but it is not. So if a class is not recognized,
			//we will loop through all recognized classes and do a less strict comparison...
			Map<String, ClassMetadata> m = sessionFactory.getAllClassMetadata();
			List<ClassMetadata> candidates = new ArrayList<ClassMetadata>();
			for (ClassMetadata md : m.values()) {
				if (md.getMappedClass(EntityMode.POJO).isAssignableFrom(entityClass)) {
					candidates.add(md);
				}
			}
			
			if (candidates.size() == 1) {
				return new HibernateEntityMetadata(sessionFactory, candidates.get(0), null);
			} else if (candidates.size() > 1) {
				for (int i = candidates.size() - 1; i >= 0; i--) {
					for (int j = 0; j < candidates.size(); j++) {
						//if i is a superclass of j, drop i and keep j. we want the most specific class.
						if (i != j && candidates.get(i).getMappedClass(EntityMode.POJO).isAssignableFrom(candidates.get(j).getMappedClass(EntityMode.POJO))) {
							candidates.remove(i);
							break;
						}
					}
				}
				return new HibernateEntityMetadata(sessionFactory, candidates.get(0), null);
			}
			
			throw new IllegalArgumentException("Unable to introspect " + entityClass.toString()
					+ ". The class is not a registered Hibernate entity.");
		} else {
			return new HibernateEntityMetadata(sessionFactory, cm, null);
		}
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
			throw new PropertyNotFoundException("Could not find property '" + propertyPath + "' on class "
					+ rootEntityClass + ".");
		}
	}
}
