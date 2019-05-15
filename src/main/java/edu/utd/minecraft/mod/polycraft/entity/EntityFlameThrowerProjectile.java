package edu.utd.minecraft.mod.polycraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;

public class EntityFlameThrowerProjectile extends EntitySmallFireball {

	private final ItemFlameThrower flameThrowerItem;

	public EntityFlameThrowerProjectile(final ItemFlameThrower flameThrowerItem, World p_i1771_1_, EntityLivingBase p_i1771_2_, double posY, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_) {
		super(p_i1771_1_);
		this.shootingEntity = p_i1771_2_;
		this.setSize(1.0F, 1.0F);
		this.setLocationAndAngles(p_i1771_2_.posX, p_i1771_2_.posY, p_i1771_2_.posZ, p_i1771_2_.rotationYaw, p_i1771_2_.rotationPitch);
		this.setPosition(this.posX, posY, this.posZ);
		this.setRenderYawOffset(0.0F);
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.accelerationX = p_i1771_3_;
		this.accelerationY = p_i1771_5_;
		this.accelerationZ = p_i1771_7_;
		this.flameThrowerItem = flameThrowerItem;
	}

	@Override
	protected void onImpact(MovingObjectPosition objPos) {
		if (Enforcer.getInstance(worldObj).possiblyKillProjectile((EntityPlayer) shootingEntity, this, objPos, PrivateProperty.PermissionSet.Action.UseFlameThrower))
			return;

		if (!this.worldObj.isRemote) {

			if (objPos.entityHit != null) {
				if (!objPos.entityHit.isImmuneToFire() && objPos.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), flameThrowerItem.damage)) {
					objPos.entityHit.setFire(flameThrowerItem.fireDuration);
				}
			}
			else {

				Block block = worldObj.getBlockState(objPos.getBlockPos()).getBlock();
				if (block == Blocks.ice) {
					this.worldObj.setBlockState(objPos.getBlockPos(), Blocks.flowing_water.getDefaultState());
				}
				else if (block == Blocks.deadbush
						|| block == Blocks.sapling
						|| block == Blocks.yellow_flower
						|| block == Blocks.red_flower
						|| block == Blocks.nether_wart
						|| block == Blocks.carrots
						|| block == Blocks.wheat
						|| block == Blocks.potatoes
						|| block == Blocks.double_plant
						|| block == Blocks.red_mushroom
						|| block == Blocks.brown_mushroom
						|| block == Blocks.tallgrass)
				{
					this.worldObj.setBlockState(objPos.getBlockPos(), Blocks.fire.getDefaultState());
				}
				else if (block == Blocks.snow_layer)
				{
					this.worldObj.setBlockState(objPos.getBlockPos(), Blocks.air.getDefaultState());
				}

				else {
					final Vec3 blockCoords = PolycraftMod.getAdjacentCoordsSideHit(objPos);
					BlockPos blockPos = new BlockPos(blockCoords.xCoord, blockCoords.yCoord, blockCoords.zCoord);
					if (worldObj.getBlockState(blockPos).getBlock() == Blocks.water)
					{
						this.worldObj.setBlockState(objPos.getBlockPos(), Blocks.air.getDefaultState());
					}
					else if (this.worldObj.isAirBlock(objPos.getBlockPos())) {
						this.worldObj.setBlockState(objPos.getBlockPos(), Blocks.fire.getDefaultState());
					}
				}
			}

			this.setDead();
		}
	}
}