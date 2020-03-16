package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureEnd;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import net.minecraft.util.BlockPos;

public class TargetDestTransform extends ElementTransform{
	
	String blockName;
	
	
	public TargetDestTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.DATA);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seed) {
		if(shouldApplyTransform(feature) && feature.getName().startsWith("dest")) {
			if(this.seedOverride == -1)
				seedOverride = seed;
			Random rand = new Random(seedOverride);

			BlockPos pos1 = feature.getPos(), pos2 = feature.getPos2();
			for(TutorialFeature tutFeat: features) {	// loop through features to find dimensions of specific data features
				if(tutFeat.getName().equals("room sw")) {	// this is specific to hunter gather task and should be done through config somehow
					pos1 = tutFeat.getPos();
					pos2 = tutFeat.getPos2();
					break;
				}
			}			
			
			BlockPos blockPos = pos1.add(rand.nextInt(Math.abs(pos2.getX() - pos1.getX())), 
					0, rand.nextInt(Math.abs(pos2.getZ() - pos1.getZ())));
			feature.setPos(blockPos);
			feature.setPos2(blockPos);	
			
			for(TutorialFeature tutFeat: features) {	// loop through features to find end condition feature
				if(tutFeat instanceof TutorialFeatureEnd) {	// this is specific to hunter gather task and should be done through config somehow
					((TutorialFeatureEnd)tutFeat).locationToReach = blockPos;
					break;
				}
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
