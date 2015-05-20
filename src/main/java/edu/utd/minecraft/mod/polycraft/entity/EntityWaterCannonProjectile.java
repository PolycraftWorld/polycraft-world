package edu.utd.minecraft.mod.polycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;

public class EntityWaterCannonProjectile extends EntitySnowball {

	private final ItemWaterCannon waterCannonItem;

	public EntityWaterCannonProjectile(final ItemWaterCannon waterCannonItem, World p_i1774_1_, EntityLivingBase p_i1774_2_) {
		super(p_i1774_1_, p_i1774_2_);
		this.waterCannonItem = waterCannonItem;
		this.motionX *= (double) waterCannonItem.velocity / 10d;
		this.motionY *= (double) waterCannonItem.velocity / 10d;
		this.motionZ *= (double) waterCannonItem.velocity / 10d;
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
				int y = p_70184_1_.blockY;
				int z = p_70184_1_.blockZ;
				if (worldObj.getBlock(x, y, z) == Blocks.fire
						|| worldObj.getBlock(x, y, z) == Blocks.deadbush
						|| worldObj.getBlock(x, y, z) == Blocks.sapling
						|| worldObj.getBlock(x, y, z) == Blocks.yellow_flower
						|| worldObj.getBlock(x, y, z) == Blocks.red_flower
						|| worldObj.getBlock(x, y, z) == Blocks.nether_wart
						|| worldObj.getBlock(x, y, z) == Blocks.carrots
						|| worldObj.getBlock(x, y, z) == Blocks.wheat
						|| worldObj.getBlock(x, y, z) == Blocks.potatoes
						|| worldObj.getBlock(x, y, z) == Blocks.snow_layer
						|| worldObj.getBlock(x, y, z) == Blocks.double_plant
						|| worldObj.getBlock(x, y, z) == Blocks.red_mushroom
						|| worldObj.getBlock(x, y, z) == Blocks.brown_mushroom
						|| worldObj.getBlock(x, y, z) == Blocks.tallgrass)
				{
					worldObj.setBlock(x, y, z, Blocks.water);
				}
				else if (worldObj.getBlock(x, y + 1, z) == Blocks.flowing_lava)
				{
					worldObj.setBlock(x, y + 1, z, Blocks.cobblestone);
				}
				else if (worldObj.getBlock(x, y + 1, z) == Blocks.lava)
				{
					worldObj.setBlock(x, y + 1, z, Blocks.obsidian);
				}
				else if (worldObj.isAirBlock(x, y + 1, z)
						|| worldObj.getBlock(x, y + 1, z) == PolycraftMod.blockLight
						|| worldObj.getBlock(x, y + 1, z) == Blocks.flowing_water)
				{
					worldObj.setBlock(x, y + 1, z, Blocks.water);
				}

			}
			this.setDead();
		}
	}
}