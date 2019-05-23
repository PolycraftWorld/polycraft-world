package edu.utd.minecraft.mod.polycraft.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;

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
		if (Enforcer.getInstance(worldObj).possiblyKillProjectile((EntityPlayer) getThrower(), this, p_70184_1_, PrivateProperty.PermissionSet.Action.UseWaterCannon))
			return;

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
				BlockPos blockPos = p_70184_1_.getBlockPos();
				Block block = worldObj.getBlockState(blockPos).getBlock();
				if (block == Blocks.deadbush
						|| block == Blocks.sapling
						|| block == Blocks.yellow_flower
						|| block == Blocks.red_flower
						|| block == Blocks.nether_wart
						|| block == Blocks.carrots
						|| block == Blocks.wheat
						|| block == Blocks.potatoes
						|| block == Blocks.snow_layer
						|| block == Blocks.double_plant
						|| block == Blocks.red_mushroom
						|| block == Blocks.brown_mushroom
						|| block == Blocks.tallgrass)
				{
					worldObj.setBlockState(blockPos, Blocks.flowing_water.getDefaultState());
				}
				else {
					final Vec3 blockCoords = PolycraftMod.getAdjacentCoordsSideHit(p_70184_1_);
					blockPos = new BlockPos(blockCoords);
					block = worldObj.getBlockState(blockPos).getBlock();
					if (block == Blocks.flowing_lava)
					{
						worldObj.setBlockState(blockPos, Blocks.cobblestone.getDefaultState());
					}
					else if (block == Blocks.lava)
					{
						worldObj.setBlockState(blockPos, Blocks.obsidian.getDefaultState());
					}
					else if (block == Blocks.fire)
					{
						worldObj.setBlockToAir(blockPos);
					}

					else if (block == Blocks.air
							|| block == PolycraftMod.blockLight
							|| block == Blocks.flowing_water)
					{
						worldObj.setBlockState(blockPos, Blocks.flowing_water.getDefaultState());
					}
				}

			}
			this.setDead();
		}
	}
}