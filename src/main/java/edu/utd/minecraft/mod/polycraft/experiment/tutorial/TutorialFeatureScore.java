package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.Vec3;

public class TutorialFeatureScore extends TutorialFeature{
	//Working variables
	protected int score = 0;

	public TutorialFeatureScore() {}
	
	public TutorialFeatureScore(String name, Vec3 pos){
		super(name, pos, Color.WHITE);
		super.featureType = TutorialFeatureType.SCORE;
	}

	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		canProceed = true;
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setInteger("score", score);
		return nbt;
	}
	
	@Override
	public void end(ExperimentTutorial exp) {
		super.end(exp);
		for(String playerName: exp.getPlayersInExperiment())
			ServerEnforcer.INSTANCE.updateSkillLevel(playerName, 100);
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		this.score=nbtFeat.getInteger("score");
	}
}
