package edu.utd.minecraft.mod.polycraft.experiment.creatures;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class PolycraftEntityAnimalBase extends EntityAnimal {

	public PolycraftEntityAnimalBase(World p_i1681_1_) {
		super(p_i1681_1_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EntityAgeable createChild(EntityAgeable p_90011_1_) {
		// TODO Auto-generated method stub
		return null;
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
