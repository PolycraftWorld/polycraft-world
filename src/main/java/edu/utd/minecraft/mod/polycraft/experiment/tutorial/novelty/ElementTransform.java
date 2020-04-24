package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;

public abstract class ElementTransform {

	Set<TutorialFeature.TutorialFeatureType> allowed;
	TransformType type = TransformType.GenericTransform;
	
	protected int intensity;
	protected long seedOverride;

	protected JsonObject jobj = new JsonObject();
	protected Random rand;
	
	public enum TransformType{
		DetritusTransform(DetritusTransform.class.getName()),
		GenericTransform(ElementTransform.class.getName()),
		RecipeTransform(RecipeTransform.class.getName()),
		RoomTransform(RoomTransform.class.getName()),
		ShaderTransform(ShaderTransform.class.getName()),
		TargetDestTransform(TargetDestTransform.class.getName()),
		WallTransform(WallTransform.class.getName()),
		WorldGenTransform(WorldGenTransform.class.getName());
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
	
	public abstract TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seedOverride);
	
	public JsonObject saveJson()
	{
		jobj = new JsonObject();	//erase current jobj so we don't get duplicates?
		jobj.addProperty("type", type.name());
		jobj.addProperty("intensity", intensity);
		jobj.addProperty("seedOverride", seedOverride);
		return jobj;
	}
	
	public void loadJson(JsonObject transformJson, long seed, int intensity)
	{
		this.type = TransformType.valueOf(transformJson.get("type").getAsString());
		this.intensity = transformJson.get("intensity").getAsInt();
		if(transformJson.get("seedOverride")!=null)
			this.seedOverride = transformJson.get("seedOverride").getAsLong();
		else
			this.seedOverride = -1; // -1 means we don't override seed
		
		if(seed != -1)
			this.seedOverride = seed;
		if(intensity != -1)
			this.intensity = intensity;
		
		// set rand var here so we don't have similar behavior among different features
		rand = new Random(seedOverride);
	}
	
}
