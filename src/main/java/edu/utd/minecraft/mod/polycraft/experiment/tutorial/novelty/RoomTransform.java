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
			if(feature instanceof TutorialFeatureRoom) {
				((TutorialFeatureRoom)feature).setBlockFloorMeta(4);
				((TutorialFeatureRoom)feature).setBlockWallMeta(3);
				((TutorialFeatureRoom)feature).setBlockRefMeta(8);
			}
			if(feature instanceof TutorialFeatureWall) {
				((TutorialFeatureWall)feature).setWallBlockMeta(3);
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
