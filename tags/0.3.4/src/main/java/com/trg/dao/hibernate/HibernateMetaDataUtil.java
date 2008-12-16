package com.trg.dao.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.SessionFactory;

import com.trg.dao.MetaDataUtil;

public class HibernateMetaDataUtil implements MetaDataUtil {

	private static Map<SessionFactory, HibernateMetaDataUtil> map = new HashMap<SessionFactory, HibernateMetaDataUtil>();

	public static HibernateMetaDataUtil getInstanceForSessionFactory(
			SessionFactory sessionFactory) {
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

	public Class<?> getExpectedClass(Class<?> rootClass, String propertyPath) {
		if (propertyPath == null || "".equals("propertyPath"))
			return rootClass;

		String[] chain = propertyPath.split("\\.");
		Class<?> klass = rootClass;

		for (String property : chain) {
			try {
				klass = sessionFactory.getClassMetadata(klass).getPropertyType(
						property).getReturnedClass();
			} catch (HibernateException ex) {
				throw new PropertyNotFoundException("Could not find property '"
						+ propertyPath + "' on class " + rootClass + ".");
			}
		}

		return klass;
	}
	
	public Serializable getId(Object object) {
		if (object == null)
			throw new NullPointerException("Cannot get ID from null object.");

		return sessionFactory.getClassMetadata(object.getClass())
				.getIdentifier(object, EntityMode.POJO);
	}

}
