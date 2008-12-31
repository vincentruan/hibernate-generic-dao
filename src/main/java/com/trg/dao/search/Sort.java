package com.trg.dao.search;

import java.io.Serializable;

public class Sort implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String property;
	protected boolean desc;

	public Sort() {

	}

	public Sort(String property, boolean desc) {
		this.property = property;
		this.desc = desc;
	}

	public Sort(String property) {
		this.property = property;
	}

	public static Sort asc(String property) {
		return new Sort(property);
	}

	public static Sort desc(String property) {
		return new Sort(property, true);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (desc ? 1231 : 1237);
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sort other = (Sort) obj;
		if (desc != other.desc)
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (property == null) {
			sb.append("null");
		} else {
			sb.append("`");
			sb.append(property);
			sb.append("`");
		}
		sb.append(desc ? " desc" : " asc");
		return sb.toString();
	}
}
