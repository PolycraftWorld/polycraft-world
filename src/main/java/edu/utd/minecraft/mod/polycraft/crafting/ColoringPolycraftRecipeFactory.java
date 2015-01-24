package edu.utd.minecraft.mod.polycraft.crafting;

public class ColoringPolycraftRecipeFactory implements PolycraftRecipeFactory {

	@Override
	public PolycraftRecipe createRecipe(PolycraftContainerType containerType,
			Iterable<RecipeInput> inputs, Iterable<RecipeComponent> outputs,
			double experience) {
		return new ColoringPolycraftRecipe(containerType, inputs, outputs);
	}

}
