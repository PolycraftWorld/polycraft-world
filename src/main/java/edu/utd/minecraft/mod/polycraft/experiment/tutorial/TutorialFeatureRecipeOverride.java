package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration.Location;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration.PathType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.util.SetMap;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureRecipeOverride extends TutorialFeature{
	
	private List<PolycraftRecipe> recipeList;	// list of available recipes

	private final SetMap<RecipeComponent, PolycraftRecipe> shapedRecipes = new SetMap<RecipeComponent, PolycraftRecipe>();
	private final SetMap<String, PolycraftRecipe> shapelessRecipes= new SetMap<String, PolycraftRecipe>();
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField xPos2Field, yPos2Field, zPos2Field, metaField;
	@SideOnly(Side.CLIENT)
	protected GuiTextField blockNameField, slotDataField;
	
	public TutorialFeatureRecipeOverride() {}
	
	public TutorialFeatureRecipeOverride(String name, BlockPos pos){
		super(name, pos, Color.GREEN);
		this.featureType = TutorialFeatureType.RECIPE_OVERRIDE;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
//		for(PolycraftRecipe recipe: recipeList) {
//			// Add shaped recipes to the SetMap.  If there are no shaped inputs,
//			// then the recipe is added as an empty set into the shapemap
//			final Collection<Set<RecipeComponent>> shapedCombinations = recipe.getShapedCombinations();
//			if (shapedCombinations.size() != 0) {
//				for (final Set<RecipeComponent> inputs : shapedCombinations) {
//					shapedRecipes.add(inputs, recipe);
//				}
//			} else {
//				shapedRecipes.add(Collections.EMPTY_SET, recipe);
//			}
//		}
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		// this is a data-type feature, build recipe lists then end
		for(PolycraftRecipe recipe: recipeList) {
			// Add shapeless recipes to the SetMap.  If there are no shapeless inputs,
			// then the recipe is added as an empty set into the shapemap
			final Collection<Set<RecipeComponent>> shapelessCombinations = recipe.getShapelessCombinations();
			if (shapelessCombinations.size() != 0) {
				for (final Set<RecipeComponent> inputs : shapelessCombinations) {
					Set<String> itemSet = Sets.newLinkedHashSet();
					for (final RecipeComponent input : inputs) {
						itemSet.add(input.itemStack.getItem().toString());
					}
					shapelessRecipes.add(itemSet, recipe);
				}
			} else {
				shapelessRecipes.add(Collections.EMPTY_SET, recipe);
			}
			
			// Add shaped recipes to the SetMap.  If there are no shaped inputs,
			// then the recipe is added as an empty set into the shapemap
			final Collection<Set<RecipeComponent>> shapedCombinations = recipe.getShapedCombinations();
			if (shapedCombinations.size() != 0) {
				for (final Set<RecipeComponent> inputs : shapedCombinations) {
					shapedRecipes.add(inputs, recipe);
				}
			} else {
				shapedRecipes.add(Collections.EMPTY_SET, recipe);
			}
		}
		canProceed = true;
		this.complete(exp);
	}
	
	@Override
	public void updateValues() {
		recipeList = new LinkedList<PolycraftRecipe>();
		if(recipeList == null || recipeList.isEmpty())
		{
			recipeList = new LinkedList<PolycraftRecipe>();
			ContainerSlot[][] slotMatrix = PolycraftContainerType.CRAFTING_TABLE.getContainerSlotGrid(SlotType.INPUT);
			List<ContainerSlot> outputSlots = ImmutableList.copyOf(PolycraftContainerType.CRAFTING_TABLE.getSlots(SlotType.OUTPUT));
			// add default reciepes here
			List inputs = new LinkedList<RecipeInput>();
			List outputs = new LinkedList<RecipeComponent>();
			inputs.add(RecipeInput.shapedInput(slotMatrix[0][0], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
			inputs.add(RecipeInput.shapedInput(slotMatrix[1][0], new ItemStack(Item.getByNameOrId("polycraft:bag_polyisoprene_pellets"), 1).copy()));
			inputs.add(RecipeInput.shapedInput(slotMatrix[2][0], new ItemStack(Item.getByNameOrId("minecraft:stick"), 1).copy()));
			outputs.add(new RecipeComponent(outputSlots.get(0), new ItemStack(Item.getByNameOrId("polycraft:wooden_pogo_stick"), 1).copy()));
			addRecipe(true, PolycraftContainerType.CRAFTING_TABLE, inputs, outputs);
		}
		
		super.updateValues();
	}
	
	private void addRecipe(boolean isShaped, PolycraftContainerType containerType, List<RecipeInput> inputList, List<RecipeComponent> outputList) {
		PolycraftRecipe recipeToAdd = new PolycraftRecipe(PolycraftContainerType.CRAFTING_TABLE, inputList, outputList);
		recipeList.add(recipeToAdd);
	}
	
	public List<PolycraftRecipe> getRecipes(){
		return recipeList;
	}
	
	public SetMap<RecipeComponent, PolycraftRecipe> getShapedRecipes() {
		return shapedRecipes;
	}

	public SetMap<String, PolycraftRecipe> getShapelessRecipes() {
		return shapelessRecipes;
	}

	@Override
	public NBTTagCompound save()
	{
		super.save();
		NBTTagList recipeListConfig = new NBTTagList();
		for(PolycraftRecipe recipe: recipeList) {
			recipeListConfig.appendTag(recipe.toNBT());	// add recipe config to recipeListConfig
		}
		nbt.setTag("recipeListConfig", recipeListConfig);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		NBTTagList recipeListConfig = nbtFeat.getTagList("recipeListConfig", 10);
		recipeList = new LinkedList<PolycraftRecipe>();
		for(int i=0; i < recipeListConfig.tagCount(); i++) {
			recipeList.add(PolycraftRecipe.fromNBT(recipeListConfig.getCompoundTagAt(i)));
		}
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		JsonArray recipeListConfig = new JsonArray();
		for(PolycraftRecipe recipe: recipeList) {
			recipeListConfig.add(recipe.toJson());	// add recipe config to recipeListConfig
		}
		jobj.add("recipeListConfig", recipeListConfig);
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		JsonArray recipeListConfig = featJson.get("recipeListConfig").getAsJsonArray();
		recipeList = new LinkedList<PolycraftRecipe>();
		for(int i=0; i < recipeListConfig.size(); i++) {
			recipeList.add(PolycraftRecipe.fromJson(recipeListConfig.get(i).getAsJsonObject()));
		}
	}
	
	
}
