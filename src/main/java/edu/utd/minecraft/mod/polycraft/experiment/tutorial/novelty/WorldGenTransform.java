package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import net.minecraft.util.BlockPos;

public class WorldGenTransform extends ElementTransform{
	
	String blockName;
	
	
	public WorldGenTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.WORLDGEN);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features) {
		if(shouldApplyTransform(feature)) {
			Random rand = new Random(seed);
			int count = 0; // counter to track number of blocks already in the experiment
			for(BlockPos blockPos:((TutorialFeatureWorldBuilder)feature).getBlockList().keySet()){
				if(((TutorialFeatureWorldBuilder)feature).getBlockList().get(blockPos).equals(blockName)) {
					((TutorialFeatureWorldBuilder)feature).getBlockList().remove(blockPos);
					count++;
				}
			}
			//for(count = rand.nextInt(intensity) + intensity / 10;count > 0; count --) {
			while(count > 0) {
				BlockPos pos1 = feature.getPos(), pos2 = feature.getPos2();
				for(TutorialFeature tutFeat: features) {	// loop through features to find dimensions of specific data features
					if(tutFeat.getName().equals("room ne")) {	// this is specific to hunter gather task and should be done through config somehow
						pos1 = tutFeat.getPos();
						pos2 = tutFeat.getPos2();
						break;
					}
				}
				((TutorialFeatureWorldBuilder)feature).getBlockList().put(pos1.add(rand.nextInt(Math.abs(pos2.getX() - pos1.getX())), 
						0, rand.nextInt(Math.abs(pos2.getZ() - pos1.getZ()))), blockName);
				count--;
			}
		}
		return feature;
	}
	
	@Override
	public void loadJson(JsonObject transformJson) {
		super.loadJson(transformJson);
		this.blockName = transformJson.get("blockName").getAsString();
	}
	
	@Override
	public JsonObject saveJson() {
		super.saveJson();
		jobj.addProperty("blockName", blockName);
		return jobj;
	}
}
