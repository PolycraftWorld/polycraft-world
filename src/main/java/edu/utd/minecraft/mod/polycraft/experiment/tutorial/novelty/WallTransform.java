package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.Collections;
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
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWall;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration.PathType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class WallTransform extends ElementTransform{
	
	private enum TransformType{
		ADD_DOORS,
		MOVE_DOORS
	}
	
	private TransformType transformType;
	
	public WallTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.WALL);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seed) {
		if(shouldApplyTransform(feature)) {
			if(this.seedOverride == -1) {
				seedOverride = seed;
				rand = new Random(seedOverride);
			}
			List<Integer> slots;
			
			switch(transformType) {
			case ADD_DOORS:
				slots = new LinkedList<Integer>();
				for(Integer slot: ((TutorialFeatureWall)feature).getWallConfiguration().keySet()) {
					slots.add(slot);
				}
				
				Collections.shuffle(slots, rand);
				
				if(!slots.isEmpty()) {
					((TutorialFeatureWall)feature).getWallConfiguration().get(slots.remove(0)).setType(PathType.DOOR);
					for(int slot: slots) {
						((TutorialFeatureWall)feature).getWallConfiguration().get(slot).setType(PathType.WALL);
					}
				}
				
				
				break;
			case MOVE_DOORS:
				// add new recipe to feature
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
