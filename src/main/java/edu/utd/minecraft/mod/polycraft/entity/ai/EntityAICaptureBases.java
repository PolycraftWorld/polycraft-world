package edu.utd.minecraft.mod.polycraft.entity.ai;



import java.util.ArrayList;
import java.util.Random;

import edu.utd.minecraft.mod.polycraft.experiment.Experiment1PlayerCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAICaptureBases extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    private final EntityCreature entityHost;
    private double entityMoveSpeed;
    private ArrayList<FeatureBase> bases;
    private FeatureBase currentBaseTarget;
    private static final String __OBFID = "CL_00001609";
    private static final int TICKS_TO_UPDATE = 5;
    private int counter = TICKS_TO_UPDATE;
    private int homeX, homeY, homeZ;
    private boolean goHome = false;
    private boolean wanderAround = false;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private int level = Experiment1PlayerCTB.level; // level 0 - passive, level 1 - balanced, level 2 - aggressive
    
    public EntityAICaptureBases(EntityCreature p_i1650_1_, double p_i1650_2_)
    {
        this.entityHost = p_i1650_1_;
        this.entityMoveSpeed = p_i1650_2_;
        this.setMutexBits(4);	//multiple AIs will not run if they have the same mutex bits
        
        //this AI should only run in experiments in dimension 8
        //TODO Stephen fix this from crashing the server when a spawnEgg is used.
        if(this.entityHost.worldObj.provider.getDimensionId() != 8) {
        	throw new IllegalArgumentException("CaptureBases AI requires mob be in Dimension 8");
        }
        
        //this line will crash the client if it runs on the client side
        if(!this.entityHost.worldObj.isRemote) {
            bases = (ArrayList<FeatureBase>)ExperimentManager.getExperiment(ExperimentManager.getRunningExperiment()).getFeature("bases");
            if (!bases.isEmpty()) {
            	 for(FeatureBase base: bases) {
            		 homeX+=base.getxPos()/bases.size();
            		 homeY+=base.getyPos()/bases.size();
            		 homeZ+=base.getzPos()/bases.size();
            	 }
            }
        }
    }

    /**
     * Returns whether the EntityAICatureBases should begin execution.
     */
    public boolean shouldExecute()
    {
        boolean result = false;	//preset result
        if (bases.isEmpty())	//in case bases is null, we don't want to crash
        {
            return result;
        }
        else
        {
        	// Passive Level (0)
        	if(level == 0) {
        		 Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.entityHost, 10, 7);

                 if (vec3 == null)
                 {
                     result = false;
                 }
                 else
                 {
                     this.xPosition = vec3.xCoord;
                     this.yPosition = vec3.yCoord;
                     this.zPosition = vec3.zCoord;
                     wanderAround = true;
                     result = true;
                 }
                 return result;
                 
        	}
        	
        	// Balanced Level (1)
	        if(level == 1) { 
        		double minDist = 999999;	//arbitrary large number for minimum dist
	            for(FeatureBase base: bases) {
	            	if(base.getCurrentTeamName() != "Animals" || base.currentState != FeatureBase.State.Claimed)	//if a base is already claimed, skip it	            		!
	               	{
	            		//checks the squared distance to find closest base
	            		if(this.entityHost.getDistanceSq(base.getxPos(), base.getyPos(), base.getzPos()) < minDist) {
	        				minDist = this.entityHost.getDistanceSq(base.getxPos(), base.getyPos(), base.getzPos());
	                		currentBaseTarget = base;
	            		}
	            		result = true;
	            	}
	            }
	            if(result == false) {	//for now, if all bases are claimed, go to the first base
	                goHome = true;
	            	result = true;
	            }
	            return result;
	        }
	        
	        // Aggressive Level (2)
	        if(level == 2) {
	        	double minDist = 999999;	//arbitrary large number for minimum dist
	            for(FeatureBase base: bases) {
	            	if(base.getCurrentTeamName() != "Animals" ||  base.currentState != FeatureBase.State.Claimed)	//if a base is already claimed, skip it
	            	
	            	{
	            		//checks the squared distance to find closest base
	            		if(this.entityHost.getDistanceSq(base.getxPos(), base.getyPos(), base.getzPos()) < minDist) {
	        				minDist = this.entityHost.getDistanceSq(base.getxPos(), base.getyPos(), base.getzPos());
	                		currentBaseTarget = base;
	            		}
	            		result = true;
	            	}
	            }
	            if(result == false) {	//for now, if all bases are claimed, go to random base          	
	            	Random RNG = new Random();
	            	currentBaseTarget = bases.get(RNG.nextInt(bases.size()));
	            	result = true;
	            }
	            return result;
	        }
        }
		return result;
    }

	/**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        currentBaseTarget = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
    	if(currentBaseTarget == null && wanderAround == false)	//don't run if we don't have a target
    		return;
    	if(this.entityHost.worldObj.isRemote)	//don't run on the client side
            return;
    	
    	double xPos, yPos, zPos;
    	
    	if(goHome == true) { 
			xPos = homeX;
			yPos = homeY;
			zPos = homeZ;
			goHome = false;
		}
    	else if (wanderAround == true) {
    		xPos = this.xPosition;
			yPos = this.yPosition;
			zPos = this.zPosition;
    	}
    	else{
    		xPos = currentBaseTarget.getxPos();
    		yPos = currentBaseTarget.getyPos() + 1;
        	zPos = currentBaseTarget.getzPos();
    	}
    	
    	
    	double d0 = this.entityHost.getDistanceSq(xPos, yPos, zPos);
        //if(--counter <= 0) {		//if we do this operation every tick, it gets resource expensive and lags the server
    	counter = TICKS_TO_UPDATE;
	    if (d0 <= 17.5D && wanderAround == false)
	    {
	    //if squared distance is less than 20, stop moving.  
	    	this.entityHost.getNavigator().clearPathEntity();	
	    }
	    else
	    {
	      	if(this.entityHost.getNavigator().noPath())	//the path routing takes up a lot of time, so we only want to reroute when we need to
	        	if(wanderAround == true) {
	        		this.entityHost.getNavigator().tryMoveToXYZ(xPos, yPos, zPos, this.entityMoveSpeed);
	        		wanderAround = false;
	        	}	      	
	        	else {
	        		this.entityHost.getNavigator().tryMoveToXYZ(xPos, yPos, zPos, this.entityMoveSpeed);
	        	}
	      	
	     }
        //}
        //make the entity look where it's going
        this.entityHost.getLookHelper().setLookPosition(xPos, yPos, zPos, 10.0F, 10.0F);
    }
}