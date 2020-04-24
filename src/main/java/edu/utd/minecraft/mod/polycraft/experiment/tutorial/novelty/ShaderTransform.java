package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureShader;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureShader.ShaderType;

public class ShaderTransform extends ElementTransform{
	

	public ShaderTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.SHADER);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seed) {
		if(shouldApplyTransform(feature)) {
			if(this.seedOverride == -1)
				seedOverride = seed;
			Random rand = new Random(seedOverride);
			if(feature instanceof TutorialFeatureShader) {
				((TutorialFeatureShader)feature).setIntensity(intensity);
				((TutorialFeatureShader)feature).setShaderType(ShaderType.CUSTOM);
			}
			
		}
		return feature;
	}
	
	@Override
	public void loadJson(JsonObject transformJson, long seed, int intensity) {
		super.loadJson(transformJson, seed, intensity);
	}
	
	@Override
	public JsonObject saveJson() {
		super.saveJson();
		return jobj;
	}
}
