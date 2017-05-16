package edu.utd.minecraft.mod.polycraft.crafting;

public class GenericPolycraftRecipeFactory implements PolycraftRecipeFactory {
	@Override
	public PolycraftRecipe createRecipe(PolycraftContainerType containerType,
			Iterable<RecipeInput> inputs, Iterable<RecipeComponent> outputs,
			double experience) {
		return new PolycraftRecipe(containerType, inputs, outputs, experience);
	}
}
