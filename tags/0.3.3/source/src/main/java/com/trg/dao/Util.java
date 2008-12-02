package com.trg.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.PropertyNotFoundException;

/**
 * Utilities for TRG Generic DAO 
 * @author dwolverton
 */
public class Util {
	/**
	 * Get the type of a property of a bean class. The property can be simple
	 * ("name") or nested ("organization.name").
	 * 
	 * @throws PropertyNotFoundException
	 *             if the class does not have the given bean property.
	 */
	public static Class<?> getExpectedClass(Class<?> rootClass, String propertyPath)
			throws PropertyNotFoundException {
		if (propertyPath == null || "".equals("propertyPath"))
			return rootClass;
		String[] chain = propertyPath.split("\\.");

		Class<?> klass = rootClass;

		for (String property : chain) {
			String getMethod = "get" + property.substring(0, 1).toUpperCase()
					+ property.substring(1);
			String isMethod = "is" + property.substring(0, 1).toUpperCase()
					+ property.substring(1);
			boolean found = false;

			for (Method method : klass.getMethods()) {
				if (method.getParameterTypes().length == 0
						&& (method.getName().equals(getMethod) || method
								.getName().equals(isMethod))) {
					klass = method.getReturnType();
					found = true;
					break;
				}
			}
			if (!found)
				throw new PropertyNotFoundException("Could not find property '"
						+ propertyPath + "' on class " + rootClass + ".");
		}

		return klass;
	}

	/**
	 * <p>
	 * Return an instance of the given class type that has the given value. For
	 * example, if type is <code>Long</code> and <code>Integer</code> type
	 * with the value 13 is passed in, a new instance of <code>Long</code>
	 * will be returned with the value 13.
	 * 
	 * <p>
	 * If the value is already of the correct type, it is simply returned.
	 * 
	 * @throws ClassCastException
	 *             if the value cannot be converted to the given type.
	 */
	public static Object convertIfNeeded(Object value, Class<?> type)
			throws ClassCastException {
		if (value == null)
			return value;
		if (type.isInstance(value))
			return value;

		if (Number.class.isAssignableFrom(type) && value instanceof Number) {
			Number num = (Number) value;

			if (type.equals(Double.class)) {
				return new Double(num.doubleValue());
			} else if (type.equals(Float.class)) {
				return new Float(num.floatValue());
			} else if (type.equals(Long.class)) {
				return new Long(num.longValue());
			} else if (type.equals(Integer.class)) {
				return new Integer(num.intValue());
			} else if (type.equals(Short.class)) {
				return new Short(num.shortValue());
			} else {
				try {
					return type.getConstructor(String.class).newInstance(
							value.toString());
				} catch (IllegalArgumentException e) {
				} catch (SecurityException e) {
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				} catch (NoSuchMethodException e) {
				}
			}
		}

		throw new ClassCastException("Unable to convert value of type "
				+ value.getClass().getName() + " to type " + type.getName());
	}
}
