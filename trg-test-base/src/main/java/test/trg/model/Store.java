package test.trg.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Store {
	private long id;
	private String name;
	private Set<Ingredient> ingredientsCarried;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany
	public Set<Ingredient> getIngredientsCarried() {
		return ingredientsCarried;
	}

	public void setIngredientsCarried(Set<Ingredient> ingredientsCarried) {
		this.ingredientsCarried = ingredientsCarried;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Store other = (Store) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (ingredientsCarried == null) {
			sb.append("null");
		} else {
			sb.append("[");
			for (Ingredient i : ingredientsCarried) {
				sb.append(i);
			}
			sb.append("]");
		}
		return "Store::id:" + id + ",name:" + name + ",ingredientsCarried:" + sb.toString();
	}
}
