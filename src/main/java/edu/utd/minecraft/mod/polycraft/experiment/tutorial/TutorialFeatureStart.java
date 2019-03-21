package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class TutorialFeatureStart extends TutorialFeature{
	private Vec3 lookDir;
	
	public TutorialFeatureStart() {}
	
	public TutorialFeatureStart(String name, Vec3 pos, Vec3 lookDir){
		super(name, pos, Color.CYAN);
		this.lookDir = lookDir;
		this.featureType = TutorialFeatureType.START;
	}
	
	
	/**
	 * Used to dimensionally transport a player into the Experiments dimension (dim. 8)
	 * Player randomly is placed within the experiment zone using Math.random().
	 * TODO: spawn players within their "Team Spawn" Zones.
	 * @param player player to be teleported
	 * @param y height they should be dropped at.
	 */
	private void spawnPlayer(EntityPlayerMP player,int x, int y, int z){
		double xOff = Math.random()*6 + x - 3;	//3 block radius
		double zOff = Math.random()*6 + z - 3;	//3 block radius
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 8,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(8), (int)xOff, y, (int)zOff));
		player.setPositionAndUpdate(x + .5, y, z + .5);
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
