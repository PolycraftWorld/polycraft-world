package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureAddItem;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureCake;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureData;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureEnd;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGroup;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGuide;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureInstruction;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureScore;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureStart;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;

public abstract class ElementTransform {

	Set<TutorialFeature.TutorialFeatureType> allowed;
	TransformType type = TransformType.GenericTransform;
	
	protected int intensity;
	protected long seed;

	protected JsonObject jobj = new JsonObject();
	
	public enum TransformType{
		WorldGenTransform(WorldGenTransform.class.getName()),
		GenericTransform(ElementTransform.class.getName());
		public String className;
		
		TransformType(String className) {
			this.className = className;
		}
		
		public TransformType next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public TransformType previous() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}

	public boolean shouldApplyTransform(TutorialFeature feature){
		if(allowed.contains(feature.getFeatureType()))
			return true;
		return false;
	}
	
	public abstract TutorialFeature applyTransform(TutorialFeature feature);
	
	public JsonObject saveJson()
	{
		jobj = new JsonObject();	//erase current jobj so we don't get duplicates?
		jobj.addProperty("type", type.name());
		jobj.addProperty("intensity", intensity);
		jobj.addProperty("seed", seed);
		return jobj;
	}
	
	public void loadJson(JsonObject transformJson)
	{
		this.type = TransformType.valueOf(transformJson.get("type").getAsString());
		this.intensity = transformJson.get("intensity").getAsInt();
		this.seed = transformJson.get("seed").getAsLong();
	}
	
}
