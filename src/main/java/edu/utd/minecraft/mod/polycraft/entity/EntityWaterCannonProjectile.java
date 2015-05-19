package edu.utd.minecraft.mod.polycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;

public class EntityWaterCannonProjectile extends EntitySnowball {
	
	private final ItemWaterCannon waterCannonItem;

    public EntityWaterCannonProjectile(final ItemWaterCannon waterCannonItem, World p_i1774_1_, EntityLivingBase p_i1774_2_) {
        super(p_i1774_1_, p_i1774_2_);
        this.waterCannonItem = waterCannonItem;
        this.motionX *= (double)waterCannonItem.velocity / 10d;
        this.motionY *= (double)waterCannonItem.velocity / 10d;
        this.motionZ *= (double)waterCannonItem.velocity / 10d;
    }
    
    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_)
    {
        if (!worldObj.isRemote) {
	        if (p_70184_1_.entityHit != null)
	        {
	        	if (p_70184_1_.entityHit instanceof EntityFlameThrowerProjectile) {
	                p_70184_1_.entityHit.setDead();
	        	}
	        	else if (p_70184_1_.entityHit.isBurning()) {
	        		p_70184_1_.entityHit.extinguish();
	        	}
	        }
    	
        	if (p_70184_1_.entityHit == null) {
        		int x = p_70184_1_.blockX;
        		int y = p_70184_1_.blockY + 1;
        		int z = p_70184_1_.blockZ;
                if (worldObj.isAirBlock(x, y, z) || worldObj.getBlock(x, y, z) == Blocks.fire) {
                	worldObj.setBlock(x, y, z, Blocks.water);
                }
            }
            this.setDead();
        }
	}
}