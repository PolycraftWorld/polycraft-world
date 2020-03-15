package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;

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
	
	public abstract TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features);
	
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
