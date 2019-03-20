package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class TutorialFeatureStart extends TutorialFeature{
	private Vec3 lookDir;
	
	public TutorialFeatureStart() {}
	
	public TutorialFeatureStart(String name, Vec3 pos, Vec3 lookDir){
		super(name, pos, Color.CYAN);
		this.lookDir = lookDir;
	}

	public Vec3 getLookDir() {
		return lookDir;
	}

	public void setLookDir(Vec3 lookDir) {
		this.lookDir = lookDir;
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		int lookDir[] = {(int)this.lookDir.xCoord, (int)this.lookDir.yCoord, (int)this.lookDir.zCoord};
		nbt.setIntArray("lookDir",lookDir);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int featLookDir[]=nbtFeat.getIntArray("lookDir");
		this.lookDir=Vec3.createVectorHelper(featLookDir[0], featLookDir[1], featLookDir[2]);
	}
	
}
