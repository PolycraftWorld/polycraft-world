package edu.utd.minecraft.mod.polycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;

public class EntityFlameThrowerProjectile extends EntitySmallFireball {
	
	private final ItemFlameThrower flameThrowerItem;

    public EntityFlameThrowerProjectile(final ItemFlameThrower flameThrowerItem, World p_i1771_1_, EntityLivingBase p_i1771_2_, double posY, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_) {
        super(p_i1771_1_);
        this.shootingEntity = p_i1771_2_;
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(p_i1771_2_.posX, p_i1771_2_.posY, p_i1771_2_.posZ, p_i1771_2_.rotationYaw, p_i1771_2_.rotationPitch);
        this.setPosition(this.posX, posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.accelerationX = p_i1771_3_;
        this.accelerationY = p_i1771_5_;
        this.accelerationZ = p_i1771_7_;
    	this.flameThrowerItem = flameThrowerItem;
    }
    
    @Override
    protected void onImpact(MovingObjectPosition p_70227_1_) {
        if (!this.worldObj.isRemote) {
            if (p_70227_1_.entityHit != null) {
                if (!p_70227_1_.entityHit.isImmuneToFire() && p_70227_1_.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), flameThrowerItem.damage)) {
                    p_70227_1_.entityHit.setFire(flameThrowerItem.fireDuration);
                }
            }
            else {
                int i = p_70227_1_.blockX;
                int j = p_70227_1_.blockY;
                int k = p_70227_1_.blockZ;

                switch (p_70227_1_.sideHit) {
                    case 0:
                        --j;
                        break;
                    case 1:
                        ++j;
                        break;
                    case 2:
                        --k;
                        break;
                    case 3:
                        ++k;
                        break;
                    case 4:
                        --i;
                        break;
                    case 5:
                        ++i;
                }

                if (this.worldObj.isAirBlock(i, j, k)) {
                    this.worldObj.setBlock(i, j, k, Blocks.fire);
                }
            }

            this.setDead();
        }
    }
}