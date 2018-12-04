package edu.utd.minecraft.mod.polycraft.experiment.feature;

import net.minecraft.world.World;

public class FeatureSpawn extends ExperimentFeature {

	protected Integer xMax;
	protected Integer yMax;
	protected Integer zMax;
	
	protected final static Integer DEFAULT_SIZE = 3; //default radius of spawning
	
	public FeatureSpawn(Integer x, Integer y, Integer z, Integer xMax, Integer yMax, Integer zMax) {
		super(x,y,z);
		this.xMax = xMax;
		this.yMax = yMax;
		this.zMax = zMax;
	}
	
	public FeatureSpawn(Integer x, Integer y, Integer z, Integer xMax, Integer zMax) {
		super(x,y,z);
		this.xMax = xMax;
		this.yMax = y+1;
		this.zMax = zMax;
	}
	
	public FeatureSpawn(Integer centerX, Integer centerY, Integer centerZ) {
		super(centerX - DEFAULT_SIZE, centerY, centerZ - DEFAULT_SIZE);
		this.xMax = centerX + DEFAULT_SIZE;
		this.yMax = centerY + 1;
		this.zMax = centerZ + DEFAULT_SIZE;

		
	}
	
	@Override
	public void build(World world) {
		// TODO Auto-generated method stub

	}

}
