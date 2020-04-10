package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import net.minecraft.util.BlockPos;

public class WorldGenTransform extends ElementTransform{
	
	String blockName;
	
	enum TransformType{
		BLOCK_LOCATION,
		TREE_TYPE
	}
	
	TransformType transformType;
	
	public WorldGenTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.WORLDGEN);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seed) {
		if(shouldApplyTransform(feature)) {
			if(this.seedOverride == -1)
				seedOverride = seed;
			Random rand = new Random(seedOverride);
			switch(transformType) {
			case BLOCK_LOCATION:
				int count = 0; // counter to track number of blocks already in the experiment
				for(BlockPos blockPos:((TutorialFeatureWorldBuilder)feature).getBlockList().keySet()){
					if(((TutorialFeatureWorldBuilder)feature).getBlockList().get(blockPos).equals(blockName)) {
						((TutorialFeatureWorldBuilder)feature).getBlockList().remove(blockPos);
						count++;
					}
				}

				BlockPos pos1 = feature.getPos(), pos2 = feature.getPos2();
				for(TutorialFeature tutFeat: features) {	// loop through features to find dimensions of specific data features
					if(tutFeat.getName().equals("room ne")) {	// this is specific to hunter gather task and should be done through config somehow
						pos1 = tutFeat.getPos();
						pos2 = tutFeat.getPos2();
						break;
					}
				}
				
				if(blockName.startsWith("tree")) {
					// trees can't spawn next to walls
					pos1 = pos1.add(1, 0, 1);
					pos2 = pos2.add(-1, 0, -1);
				}
				
				
				while(count > 0) {
					BlockPos blockPos = pos1.add(rand.nextInt(Math.abs(pos2.getX() - pos1.getX())), 
							0, rand.nextInt(Math.abs(pos2.getZ() - pos1.getZ())));
					if(((TutorialFeatureWorldBuilder)feature).getBlockList().containsKey(blockPos))
						continue;
					((TutorialFeatureWorldBuilder)feature).getBlockList().put(blockPos, blockName);
					count--;
				}
			case TREE_TYPE:
				List<BlockPos> treeList = new LinkedList<BlockPos>();
				for(BlockPos blockPos:((TutorialFeatureWorldBuilder)feature).getBlockList().keySet()){
					if(((TutorialFeatureWorldBuilder)feature).getBlockList().get(blockPos).startsWith("tree")) {
						treeList.add(blockPos);
					}
				}
				
				// randomize list
				Collections.shuffle(treeList, rand);
				
				for(int i = 0; i < Math.min(treeList.size(), treeList.size() * (intensity/100.0)); i++){
					((TutorialFeatureWorldBuilder)feature).getBlockList().put(treeList.get(i), blockName);
				}
				break;
			}
			
		}
		return feature;
	}
	
	@Override
	public void loadJson(JsonObject transformJson) {
		super.loadJson(transformJson);
		this.blockName = transformJson.get("blockName").getAsString();
		this.transformType = TransformType.valueOf(transformJson.get("transformType").getAsString());
	}
	
	@Override
	public JsonObject saveJson() {
		super.saveJson();
		jobj.addProperty("blockName", blockName);
		jobj.addProperty("transformType", blockName);
		return jobj;
	}
}
