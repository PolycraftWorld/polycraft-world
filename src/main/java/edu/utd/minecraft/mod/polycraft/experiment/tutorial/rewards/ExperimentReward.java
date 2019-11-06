package edu.utd.minecraft.mod.polycraft.experiment.tutorial.rewards;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class ExperimentReward {
	
	public abstract void init(ExperimentTutorial exp);

	public abstract int rewardEvent(ExperimentTutorial exp, Event event);

	public NBTTagCompound save() {
		return null;
	}

	public void load(NBTTagCompound nbtObs) {
	}
}
