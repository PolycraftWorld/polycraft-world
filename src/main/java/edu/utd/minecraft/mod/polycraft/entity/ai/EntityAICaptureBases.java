package edu.utd.minecraft.mod.polycraft.entity.ai;



import java.util.ArrayList;

import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.MathHelper;

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

    public EntityAICaptureBases(EntityCreature p_i1650_1_, double p_i1650_2_)
    {
        this.entityHost = p_i1650_1_;
        this.entityMoveSpeed = p_i1650_2_;
        this.setMutexBits(4);	//multiple AIs will not run if they have the same mutex bits
        
        //this AI should only run in experiments in diminsion 8
        if(this.entityHost.worldObj.provider.dimensionId != 8) {
        	throw new IllegalArgumentException("CaptureBases AI requires mob be in Dimension 8");
        }
        
        //this line will crash the client if it runs on the client side
        if(!this.entityHost.worldObj.isRemote) {
            bases = (ArrayList<FeatureBase>)ExperimentManager.getExperiment(ExperimentManager.getRunningExperiment()).getFeature("bases");
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
        	double minDist = 999999;	//arbitrary large number for minimum dist
            for(FeatureBase base: bases) {
            	if(base.currentState != FeatureBase.State.Claimed)	//if a base is already claimed, skip it
            	// only checks if the base is claimed NOT who has claimed it, find way to check who owns a base
            		//make the entities work together, move for separate bases not all for one
            	// attacking players, using a separate AI task "offensive tactics" 
            		// if a player is within so many blocks from me, look at player, attack,
            		// levels of intensity 1-5, 5 will keep you in a corner while others claim bases
            		// 		item use/conservation
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
            	currentBaseTarget = bases.get(0);
            	result = true;
            }
            return result;
        }
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
    	if(currentBaseTarget == null)	//don't run if we don't have a target
    		return;
    	if(this.entityHost.worldObj.isRemote)	//don't run on the client side
            return;
    	
    	int xPos = currentBaseTarget.getxPos();
    	int yPos = currentBaseTarget.getyPos() + 1;
    	int zPos = currentBaseTarget.getzPos();
    	
        double d0 = this.entityHost.getDistanceSq(xPos, yPos, zPos);
        //if(--counter <= 0) {		//if we do this operation every tick, it gets resource expensive and lags the server
    		counter = TICKS_TO_UPDATE;
	        if (d0 <= 17.5D)
	        {
	        	//if squared distance is less than 20, stop moving.  
	            this.entityHost.getNavigator().clearPathEntity();	
	        }
	        else
	        {
	        	if(this.entityHost.getNavigator().noPath())	//the path routing takes up a lot of time, so we only want to reroute when we need to
	        		this.entityHost.getNavigator().tryMoveToXYZ(xPos, yPos, zPos, this.entityMoveSpeed);
	        }
        //}
        //make the entity look where it's going
        this.entityHost.getLookHelper().setLookPosition(xPos, yPos, zPos, 10.0F, 10.0F);
    }
}