package edu.utd.minecraft.mod.polycraft.experiment.creatures;

import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * 
 * @author ejk018000
 * New class for a variable speed, invincible cow
 */
public class PolycraftCow extends EntityCow {
	 
	// Set animal speed variable 
    public double animalSpeed = 4.0D;
    
	public PolycraftCow(World p_i1683_1_) {
		super(p_i1683_1_);
		
		this.tasks.removeTask(new EntityAIWander(this, 1.0D));
		this.tasks.addTask(5, new EntityAIWander(this, animalSpeed));
	}
	
	/**
	 * Make animals immortal in Polycraft experiments
	 */
	public boolean attackEntityFrom(DamageSource damageSource, float damageMagnitude)
    {
            damageMagnitude = 0; // New line for invulnerability
            return super.attackEntityFrom(damageSource, damageMagnitude);
    }

}
