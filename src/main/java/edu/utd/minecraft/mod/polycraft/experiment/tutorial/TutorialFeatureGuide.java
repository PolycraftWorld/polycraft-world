package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class TutorialFeatureGuide extends TutorialFeature {
	private Vec3 pos2;
	
	public TutorialFeatureGuide(){}
	
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
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		int pos[] = {(int)this.pos2.xCoord, (int)this.pos2.yCoord, (int)this.pos2.zCoord};
		nbt.setIntArray("pos2",pos);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int featPos2[]=nbtFeat.getIntArray("pos2");
		this.pos2=Vec3.createVectorHelper(featPos2[0], featPos2[1], featPos2[2]);
	}
}
