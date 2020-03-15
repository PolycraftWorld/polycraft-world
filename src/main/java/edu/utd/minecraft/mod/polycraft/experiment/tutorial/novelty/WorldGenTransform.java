package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;

public class WorldGenTransform extends ElementTransform{
	
	String blockName;
	
	
	public WorldGenTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.WORLDGEN);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, long seed) {
		if(this.seedOverride == -1)
			seedOverride = seed;
		if(shouldApplyTransform(feature)) {
			Random rand = new Random(seedOverride);
				
			for(int count = rand.nextInt(intensity) + intensity / 10;count > 0; count --) {
				((TutorialFeatureWorldBuilder)feature).getBlockList().put(feature.getPos().add(rand.nextInt(feature.getPos2().getX()), 
						rand.nextInt(feature.getPos2().getY()), rand.nextInt(feature.getPos2().getZ())), blockName);
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
