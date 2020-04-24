package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeSlot;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureRecipeOverride;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class RecipeTransform extends ElementTransform{
	
	private enum TransformType{
		ADD_RECIPE,
		ADD_RECIPE_2,
		ADD_RECIPE_3,
		ROTATE_PERCEPTION,
		FLIP_HORIZONTAL_PERCEPTION,
		FLIP_VERTICAL_PERCEPTION,
		REMOVE_PERCEIVED_INPUT_ITEM,
		ADD_PERCEIVED_INPUT_ITEM
	}
	
	private TransformType transformType;
	
	public RecipeTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.RECIPE_OVERRIDE);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seed) {
		if(shouldApplyTransform(feature)) {
			if(this.seedOverride == -1)
				seedOverride = seed;
			Random rand1 = new Random(seedOverride);
			List<PolycraftRecipe> recipes = ((TutorialFeatureRecipeOverride)feature).getRecipes();
			
			// working veriables
			ContainerSlot[][] slotMatrix;
			List<ContainerSlot> outputSlots;
			List inputs;
			List outputs;
			
			switch(transformType) {
			case ADD_PERCEIVED_INPUT_ITEM:
				recipes = ((TutorialFeatureRecipeOverride)feature).getRecipesForClient();
				int slotToAdd = rand1.nextInt(9); // rotations value can be from 1-3	
				for(int i=0; i < recipes.size(); i++) {
					recipes.get(i).getShapedInputs().put(new RecipeSlot(slotToAdd), RecipeInput.shapedInput(new RecipeSlot(slotToAdd), 
							new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				}

				System.out.println("Added item to perceived recipes in slot " + slotToAdd);
				((TutorialFeatureRecipeOverride)feature).setPerceivedRecipes(recipes);
				break;
			case ADD_RECIPE:
				// add new recipe to feature
				slotMatrix = PolycraftContainerType.CRAFTING_TABLE.getContainerSlotGrid(SlotType.INPUT);
				outputSlots = ImmutableList.copyOf(PolycraftContainerType.CRAFTING_TABLE.getSlots(SlotType.OUTPUT));
				// add default reciepes here
				inputs = new LinkedList<RecipeInput>();
				outputs = new LinkedList<RecipeComponent>();
				inputs.add(RecipeInput.shapedInput(slotMatrix[0][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[0][1], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][1], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][2], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				outputs.add(new RecipeComponent(outputSlots.get(0), new ItemStack(Item.getByNameOrId("minecraft:wooden_axe"), 1).copy()));
				
				((TutorialFeatureRecipeOverride)feature).addRecipe(true, PolycraftContainerType.CRAFTING_TABLE, inputs, outputs);
				break;
			case ADD_RECIPE_2:
				// add new recipe to feature
				slotMatrix = PolycraftContainerType.CRAFTING_TABLE.getContainerSlotGrid(SlotType.INPUT);
				outputSlots = ImmutableList.copyOf(PolycraftContainerType.CRAFTING_TABLE.getSlots(SlotType.OUTPUT));
				// add wooden axe recipe
				inputs = new LinkedList<RecipeInput>();
				outputs = new LinkedList<RecipeComponent>();
				inputs.add(RecipeInput.shapedInput(slotMatrix[0][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[0][1], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][1], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][2], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				outputs.add(new RecipeComponent(outputSlots.get(0), new ItemStack(Item.getByNameOrId("minecraft:wooden_axe"), 1).copy()));
				
				((TutorialFeatureRecipeOverride)feature).addRecipe(true, PolycraftContainerType.CRAFTING_TABLE, inputs, outputs);
				
				// add wooden sword recipe
				inputs = new LinkedList<RecipeInput>();
				outputs = new LinkedList<RecipeComponent>();
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][1], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][2], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				outputs.add(new RecipeComponent(outputSlots.get(0), new ItemStack(Item.getByNameOrId("minecraft:wooden_sword"), 1).copy()));
				
				((TutorialFeatureRecipeOverride)feature).addRecipe(true, PolycraftContainerType.CRAFTING_TABLE, inputs, outputs);
				break;
			case ADD_RECIPE_3:
				// add new recipe to feature
				slotMatrix = PolycraftContainerType.CRAFTING_TABLE.getContainerSlotGrid(SlotType.INPUT);
				outputSlots = ImmutableList.copyOf(PolycraftContainerType.CRAFTING_TABLE.getSlots(SlotType.OUTPUT));
				// add wooden axe recipe
				inputs = new LinkedList<RecipeInput>();
				outputs = new LinkedList<RecipeComponent>();
				inputs.add(RecipeInput.shapedInput(slotMatrix[0][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[0][1], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][1], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][2], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				outputs.add(new RecipeComponent(outputSlots.get(0), new ItemStack(Item.getByNameOrId("minecraft:wooden_axe"), 1).copy()));
				
				((TutorialFeatureRecipeOverride)feature).addRecipe(true, PolycraftContainerType.CRAFTING_TABLE, inputs, outputs);
				
				// add wooden sword recipe
				inputs = new LinkedList<RecipeInput>();
				outputs = new LinkedList<RecipeComponent>();
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][1], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][2], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				outputs.add(new RecipeComponent(outputSlots.get(0), new ItemStack(Item.getByNameOrId("minecraft:wooden_sword"), 1).copy()));
				
				((TutorialFeatureRecipeOverride)feature).addRecipe(true, PolycraftContainerType.CRAFTING_TABLE, inputs, outputs);
				
				// add wooden pick axe recipe
				inputs = new LinkedList<RecipeInput>();
				outputs = new LinkedList<RecipeComponent>();
				inputs.add(RecipeInput.shapedInput(slotMatrix[0][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[2][0], new ItemStack(Item.getByNameOrId("minecraft:planks"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][1], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				inputs.add(RecipeInput.shapedInput(slotMatrix[1][2], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
				outputs.add(new RecipeComponent(outputSlots.get(0), new ItemStack(Item.getByNameOrId("minecraft:wooden_pickaxe"), 1).copy()));
				
				((TutorialFeatureRecipeOverride)feature).addRecipe(true, PolycraftContainerType.CRAFTING_TABLE, inputs, outputs);
				break;
			case FLIP_HORIZONTAL_PERCEPTION:
				break;
			case FLIP_VERTICAL_PERCEPTION:
				break;
			case REMOVE_PERCEIVED_INPUT_ITEM:
				recipes = ((TutorialFeatureRecipeOverride)feature).getRecipesForClient();
				int slotToRemove = rand1.nextInt(9); // rotations value can be from 1-3	
				for(int i=0; i < recipes.size(); i++) {
					recipes.get(i).removeInput(slotToRemove);
				}

				System.out.println("removed item to perceived recipes in slot " + slotToRemove);
				((TutorialFeatureRecipeOverride)feature).setPerceivedRecipes(recipes);
				break;
			case ROTATE_PERCEPTION:
				recipes = ((TutorialFeatureRecipeOverride)feature).getRecipesForClient();
				int rotations = rand1.nextInt(3) + 1; // rotations value can be from 1-3	
				for(int i=0; i < recipes.size(); i++) {
					recipes.get(i).rotateInputsCW(rotations);
				}

				System.out.println("Rotated perceived recipes by " + rotations * 90);
				((TutorialFeatureRecipeOverride)feature).setPerceivedRecipes(recipes);
				break;
			default:
				break;
			
			}
			
		}
		return feature;
	}
	
	@Override
	public void loadJson(JsonObject transformJson) {
		super.loadJson(transformJson);
		transformType = TransformType.valueOf(transformJson.get("transformType").getAsString());
//		this.blockName = transformJson.get("blockName").getAsString();
	}
	
	@Override
	public JsonObject saveJson() {
		super.saveJson();
		jobj.addProperty("transformType", transformType.name());
//		jobj.addProperty("blockName", blockName);
		return jobj;
	}
}
