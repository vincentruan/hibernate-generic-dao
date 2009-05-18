package test.trg.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "recipe_x_ingredient")
public class RecipeIngredient {
	private RecipeIngredientId compoundId;
	private float amount;
	private String measure;

	public RecipeIngredient() {
	}

	public RecipeIngredient(Recipe recipe, Ingredient ingredient, float amount, String measure) {
		this.compoundId = new RecipeIngredientId(recipe, ingredient);
		this.amount = amount;
		this.measure = measure;
	}

	/**
	 * Ordinarily I would just call this "id", but for testing I needed some
	 * identifiers that were not "id".
	 */
	@EmbeddedId
	public RecipeIngredientId getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(RecipeIngredientId compoundId) {
		this.compoundId = compoundId;
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
		result = prime * result + ((compoundId == null) ? 0 : compoundId.hashCode());
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
		if (compoundId == null) {
			if (other.compoundId != null)
				return false;
		} else if (!compoundId.equals(other.compoundId))
			return false;
		return true;
	}

	public String toString() {
		return "RecipeIngredient::compoundId:" + compoundId + ",amount:" + amount + ",measure:" + measure;
	}

}
