package com.trg.dao;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utilities for TRG Generic DAO
 * 
 * @author dwolverton
 */
public class Util {
	/**
	 * <p>
	 * Return an instance of the given class type that has the given value. For
	 * example, if type is <code>Long</code> and <code>Integer</code> type with
	 * the value 13 is passed in, a new instance of <code>Long</code> will be
	 * returned with the value 13.
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

	/**
	 * This is a helper method to call a method on an Object with the given
	 * parameters. It is used for dispatching to specific DAOs that do not
	 * implement the GenericDAO interface.
	 */
	public static Object callMethod(Object object, String methodName,
			Object... args) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException  {
		for (Method method : object.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes.length == args.length) {
					if (method.isVarArgs()) {
						int i = args.length - 1;
						Object lastParam = Array.newInstance(
								args[i].getClass(), 1);
						Array.set(lastParam, 0, args[i]);
						args[i] = lastParam;
					}
				} else if (method.isVarArgs()
						&& paramTypes.length == args.length + 1) {
					Object[] temp = args;
					args = new Object[temp.length + 1];
					for (int i = 0; i < temp.length; i++) {
						args[i] = temp[i];
					}
					args[args.length - 1] = Array.newInstance(
							paramTypes[paramTypes.length - 1]
									.getComponentType(), 0);
				} else {
					continue;
				}

				for (int i = 0; i < paramTypes.length; i++) {
					if (!paramTypes[i].isInstance(args[i]))
						continue;
				}

				return method.invoke(object, args);
			}
		}

		throw new NoSuchMethodException("Method: "
				+ methodName + " not found on Class: " + object.getClass());
	}
}
