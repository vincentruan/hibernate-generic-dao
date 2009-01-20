package com.test.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "recipe_x_ingredient")
public class RecipeIngredient {
	private RecipeIngredientId id;
	private float amount;
	private String measure;

	public RecipeIngredient() {}
	
	public RecipeIngredient(Recipe recipe, Ingredient ingredient, float amount, String measure) {
		this.id = new RecipeIngredientId(recipe, ingredient);
		this.amount = amount;
		this.measure = measure;
	}
	
	@EmbeddedId
	public RecipeIngredientId getId() {
		return id;
	}

	public void setId(RecipeIngredientId id) {
		this.id = id;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		RecipeIngredient other = (RecipeIngredient) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toString() {
		return "RecipeIngredient::id:" + id + ",amount:" + amount + ",measure:" + measure;
	}

}
