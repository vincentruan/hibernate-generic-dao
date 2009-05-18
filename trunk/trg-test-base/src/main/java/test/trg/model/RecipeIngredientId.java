package test.trg.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class RecipeIngredientId implements Serializable {
	private static final long serialVersionUID = 0L;

	private Recipe recipe;
	private Ingredient ingredient;

	public RecipeIngredientId() {
	}

	public RecipeIngredientId(Recipe recipe, Ingredient ingredient) {
		super();
		this.recipe = recipe;
		this.ingredient = ingredient;
	}

	@ManyToOne
	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	@ManyToOne
	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ingredient == null) ? 0 : ingredient.hashCode());
		result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
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
		RecipeIngredientId other = (RecipeIngredientId) obj;
		if (ingredient == null) {
			if (other.ingredient != null)
				return false;
		} else if (!ingredient.equals(other.ingredient))
			return false;
		if (recipe == null) {
			if (other.recipe != null)
				return false;
		} else if (!recipe.equals(other.recipe))
			return false;
		return true;
	}

	public String toString() {
		return "RecipeIngredientId::recipeId:" + (recipe == null ? "(none)" : recipe.getId()) + ",ingredientId:" + (ingredient == null ? "(none)" : ingredient.getIngredientId());
	}
}
