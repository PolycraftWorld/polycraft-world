package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.Vec3;

public class TutorialFeatureEnd extends TutorialFeature{

	public TutorialFeatureEnd() {}
	
	public TutorialFeatureEnd(String name, Vec3 pos){
		super(name, pos, Color.YELLOW);
		super.featureType = TutorialFeatureType.END;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
	}
}
