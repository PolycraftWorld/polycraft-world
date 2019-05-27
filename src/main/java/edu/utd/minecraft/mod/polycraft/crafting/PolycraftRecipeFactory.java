package edu.utd.minecraft.mod.polycraft.crafting;

public interface PolycraftRecipeFactory {
	PolycraftRecipe createRecipe(final PolycraftContainerType containerType, final Iterable<RecipeInput> inputs,
			final Iterable<RecipeComponent> outputs, final double experience);
}
