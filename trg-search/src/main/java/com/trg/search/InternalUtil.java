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
package com.trg.search;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Utilities for TRG Generic DAO Search
 * 
 * @author dwolverton
 */
public class InternalUtil {
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
	public static Object convertIfNeeded(Object value, Class<?> type) throws ClassCastException {
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
					return type.getConstructor(String.class).newInstance(value.toString());
				} catch (IllegalArgumentException e) {
				} catch (SecurityException e) {
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				} catch (NoSuchMethodException e) {
				}
			}
		} else if (Class.class.equals(type)) {
			try {
				return Class.forName(value.toString());
			} catch (ClassNotFoundException e) {
				throw new ClassCastException("Unable to convert value " + value.toString() + " to type Class");
			}
		}

		throw new ClassCastException("Unable to convert value of type " + value.getClass().getName() + " to type "
				+ type.getName());
	}

	public static String paramDisplayString(Object val) {
		if (val == null) {
			return "null";
		} else if (val instanceof String) {
			return "\"" + val + "\"";
		} else if (val instanceof Collection) {
			StringBuilder sb = new StringBuilder();
			sb.append(val.getClass().getSimpleName());
			sb.append(" {");
			boolean first = true;
			for (Object o : (Collection<?>) val) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(paramDisplayString(o));
			}
			sb.append("}");
			return sb.toString();
		} else if (val instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			sb.append(val.getClass().getComponentType().getSimpleName());
			sb.append("[] {");
			boolean first = true;
			for (Object o : (Object[]) val) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(paramDisplayString(o));
			}
			sb.append("}");
			return sb.toString();
		} else {
			return val.toString();
		}
	}
}
