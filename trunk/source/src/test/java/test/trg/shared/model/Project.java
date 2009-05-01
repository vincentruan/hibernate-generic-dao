package test.trg.shared.model;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;


@Entity
public class Project {
	private Long id;
	private String name;
	private Integer inceptionYear;
	private Set<Person> members;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getInceptionYear() {
		return inceptionYear;
	}

	public void setInceptionYear(Integer inceptionYear) {
		this.inceptionYear = inceptionYear;
	}
	
	@ManyToMany
	public Set<Person> getMembers() {
		return members;
	}

	public void setMembers(Set<Person> members) {
		this.members = members;
	}

	/**
	 * Calculated property
	 */
	@Transient
	public int getDuration() {
		if (inceptionYear == null)
			return 0;
		return Calendar.getInstance().get(Calendar.YEAR) - inceptionYear;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inceptionYear == null) ? 0 : inceptionYear.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Project other = (Project) obj;
		if (inceptionYear == null) {
			if (other.inceptionYear != null)
				return false;
		} else if (!inceptionYear.equals(other.inceptionYear))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		return "Project::id:" + id + ",name:" + name + ",inceptionYear:" + inceptionYear + ",duration:" + getDuration();
	}
}
