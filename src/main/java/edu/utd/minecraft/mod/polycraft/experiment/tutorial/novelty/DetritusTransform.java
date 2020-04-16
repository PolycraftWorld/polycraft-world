package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureEnd;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureEnd.EndCondition;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureStart;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder.GenType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.BlockDef;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class DetritusTransform extends ElementTransform{
	
	public DetritusTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.WORLDGEN);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seed) {
		if(shouldApplyTransform(feature) && feature instanceof TutorialFeatureWorldBuilder 
				&& ((TutorialFeatureWorldBuilder)feature).getGenType() == GenType.BLOCK_LIST) {
			if(this.seedOverride == -1)
				seedOverride = seed;
			Random rand = new Random(seedOverride);
						
			BlockPos blockPos = null;
			boolean spawnCorners = false;
			
			BlockDef blockDef = new BlockDef("minecraft:stained_glass", 0);
			
			if(intensity > 1)
				if(intensity > 33) {
					if(intensity > 66) {	// intensity 67-100: spawn detritus around player
						for(TutorialFeature expFeat: features) {
							if(expFeat instanceof TutorialFeatureStart) {
								blockPos = expFeat.getPos();
								if(intensity > 75)	// upper level of this intensity bracket, spawn corners
									spawnCorners = true;
								break;
							}
						}
					}
					else { 	// intensity 34-66: spawn detritus around macguffin
		featureSearch:	for(TutorialFeature expFeat: features) {
							if(expFeat instanceof TutorialFeatureWorldBuilder && ((TutorialFeatureWorldBuilder)expFeat).getGenType() == GenType.BLOCK_LIST) {
								for(BlockPos element:((TutorialFeatureWorldBuilder)expFeat).getBlockList().keySet()) {	// serach for macguffin block position
									if(((TutorialFeatureWorldBuilder)expFeat).getBlockList().get(element).blockName.equals("polycraft:macguffin")) {
										blockPos = element;
										if(intensity > 50)	// upper level of this intensity bracket, spawn corners
											spawnCorners = true;
										break featureSearch;	// break upper for loop
									}
								}
							}
						}
					}
				}
				else { // intensity 1-33: spawn around target position (should we do anything at 0 intensity???)
					for(TutorialFeature expFeat: features) {
						if(expFeat instanceof TutorialFeatureEnd && ((TutorialFeatureEnd)expFeat).endCondition == EndCondition.BLOCK_TO_LOCATION) {
							blockPos = ((TutorialFeatureEnd)expFeat).locationToReach;
							if(intensity > 16)	// upper level of this intensity bracket, spawn corners
								spawnCorners = true;
							break;
						}
					}
				}
			
			// check to make sure we actually have a target position
			if(blockPos != null) {
				for(EnumFacing facing: EnumFacing.HORIZONTALS)
					if(!((TutorialFeatureWorldBuilder)feature).getBlockList().containsKey(blockPos.offset(facing)))
						((TutorialFeatureWorldBuilder)feature).getBlockList().put(blockPos.offset(facing), blockDef);
				if(spawnCorners) {
					if(!((TutorialFeatureWorldBuilder)feature).getBlockList().containsKey(blockPos.add(1,0,1)))
						((TutorialFeatureWorldBuilder)feature).getBlockList().put(blockPos.add(1,0,1), blockDef);
					if(!((TutorialFeatureWorldBuilder)feature).getBlockList().containsKey(blockPos.add(1,0,-1)))
						((TutorialFeatureWorldBuilder)feature).getBlockList().put(blockPos.add(1,0,-1), blockDef);
					if(!((TutorialFeatureWorldBuilder)feature).getBlockList().containsKey(blockPos.add(-1,0,1)))
						((TutorialFeatureWorldBuilder)feature).getBlockList().put(blockPos.add(-1,0,1), blockDef);
					if(!((TutorialFeatureWorldBuilder)feature).getBlockList().containsKey(blockPos.add(-1,0,-1)))
						((TutorialFeatureWorldBuilder)feature).getBlockList().put(blockPos.add(-1,0,-1), blockDef);
				}
			}else {
				PolycraftMod.logger.error("Configuration Error: No feature found to perform Detritus Transformation on at given intensity level");
			}
			
			
		}
		return feature;
	}
	
	@Override
	public void loadJson(JsonObject transformJson) {
		super.loadJson(transformJson);
	}
	
	@Override
	public JsonObject saveJson() {
		super.saveJson();
		return jobj;
	}
}
