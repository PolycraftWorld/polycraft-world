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
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureRoom;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWall;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureWorldBuilder;
import net.minecraft.util.BlockPos;

public class RoomTransform extends ElementTransform{
	

	public RoomTransform() {
		allowed = EnumSet.of(TutorialFeature.TutorialFeatureType.ROOM, TutorialFeature.TutorialFeatureType.WALL);
	}

	@Override
	public TutorialFeature applyTransform(TutorialFeature feature, ArrayList<TutorialFeature> features, long seed) {
		if(shouldApplyTransform(feature)) {
			if(this.seedOverride == -1)
				seedOverride = seed;
			Random rand = new Random(seedOverride);
			int wallMeta = rand.nextInt(15);
			if(feature instanceof TutorialFeatureRoom) {
				((TutorialFeatureRoom)feature).setBlockFloorMeta(rand.nextInt(15));
				((TutorialFeatureRoom)feature).setBlockWallMeta(wallMeta);
				((TutorialFeatureRoom)feature).setBlockRefMeta(rand.nextInt(15));
				((TutorialFeatureRoom)feature).setIntensity(intensity);
			}
			if(feature instanceof TutorialFeatureWall) {
				((TutorialFeatureWall)feature).setWallBlockMeta(wallMeta);
				((TutorialFeatureWall)feature).setIntensity(intensity);
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
