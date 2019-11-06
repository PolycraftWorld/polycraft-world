package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.awt.Color;

import com.google.gson.JsonElement;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public interface IObservation{
	
	//Initialization runs before the experiment starts and sets up any parameters used for this observation
	public abstract void init(ExperimentTutorial exp);

	//returns observation in JSON format
	public abstract JsonElement getObservation(ExperimentTutorial exp);
	
	//returns NBTTag format of any options needed for this observation type
	public NBTTagCompound save();
	
	//loads options from a stored observation configuration 
	public void load(NBTTagCompound nbtObs);
	
	public abstract String getName();
}
