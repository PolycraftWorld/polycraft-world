package edu.utd.minecraft.mod.polycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;

public class EntityFreezeRayProjectile extends EntitySnowball {

	private final ItemFreezeRay freezeRayItem;

	public EntityFreezeRayProjectile(final ItemFreezeRay freezeRayItem, World p_i1774_1_, EntityLivingBase p_i1774_2_) {
		super(p_i1774_1_, p_i1774_2_);
		this.freezeRayItem = freezeRayItem;
		this.motionX *= (double) freezeRayItem.velocity / 10d;
		this.motionY *= (double) freezeRayItem.velocity / 10d;
		this.motionZ *= (double) freezeRayItem.velocity / 10d;
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
				else {
					p_70184_1_.entityHit.attackEntityFrom(DamageSource.generic, freezeRayItem.damage);
					if (p_70184_1_.entityHit.isBurning()) {
						p_70184_1_.entityHit.extinguish();
					}
				}
				//FIXME eventually figure out how to "freeze" a player
			}

			if (p_70184_1_.entityHit == null) {
				int x = p_70184_1_.blockX;
				int y = p_70184_1_.blockY + 1;
				int z = p_70184_1_.blockZ;
				if (worldObj.getBlock(x, y, z) == Blocks.water)
				{
					worldObj.setBlock(x, y, z, Blocks.ice);
				}
				else if ((worldObj.isAirBlock(x, y, z)
						|| worldObj.getBlock(x, y, z) == PolycraftMod.blockLight)
						&& Blocks.snow_layer.canPlaceBlockAt(worldObj, x, y, z))
				{
					worldObj.setBlock(x, y, z, Blocks.snow_layer);
				}
			}
			this.setDead();
		}
	}
}