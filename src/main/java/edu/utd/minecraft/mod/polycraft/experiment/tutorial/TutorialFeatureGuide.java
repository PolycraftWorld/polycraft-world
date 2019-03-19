package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import net.minecraft.util.Vec3;

public class TutorialFeatureGuide extends TutorialFeature {
	private Vec3 pos2;
	
	public TutorialFeatureGuide(String name, Vec3 pos, Vec3 pos2){
		super(name, pos, Color.YELLOW);
		this.pos2 = pos2;
	}

	public Vec3 getPos2() {
		return pos2;
	}

	public void setPos2(Vec3 pos2) {
		this.pos2 = pos2;
	}
}
