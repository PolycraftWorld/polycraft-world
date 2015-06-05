package edu.utd.minecraft.mod.polycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;

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
		if (Enforcer.getInstance(worldObj).possiblyKillProjectile((EntityPlayer) getThrower(), this, p_70184_1_, PrivateProperty.PermissionSet.Action.UseFreezeRay))
			return;

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

				final Vec3 blockCoords = PolycraftMod.getAdjacentCoordsSideHit(p_70184_1_);
				int x = (int) blockCoords.xCoord;
				int y = (int) blockCoords.yCoord;
				int z = (int) blockCoords.zCoord;
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