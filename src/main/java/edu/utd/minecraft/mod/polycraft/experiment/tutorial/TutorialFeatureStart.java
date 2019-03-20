package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import net.minecraft.util.Vec3;

public class TutorialFeatureStart extends TutorialFeature{
	private Vec3 lookDir;
	
	public TutorialFeatureStart(String name, Vec3 pos, Vec3 lookDir){
		super(name, pos, Color.CYAN);
		this.lookDir = lookDir;
		this.featureType = TutorialFeatureType.START;
	}

	public Vec3 getLookDir() {
		return lookDir;
	}

	public void setLookDir(Vec3 lookDir) {
		this.lookDir = lookDir;
	}
}
