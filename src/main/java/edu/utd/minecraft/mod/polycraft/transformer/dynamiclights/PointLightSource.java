package edu.utd.minecraft.mod.polycraft.transformer.dynamiclights;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PointLightSource implements IDynamicLightSource
{
	private class PointLightEntity extends Entity {
		public PointLightEntity(final World world) {
			super(world);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound var1) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound var1) {
		}
	}

	private final Entity entity;
	private int lightLevel = 0;

	public PointLightSource(final World world) {
		entity = new PointLightEntity(world);
	}

	@Override
	public Entity getAttachmentEntity() {
		return entity;
	}

	@Override
	public int getLightLevel() {
		return lightLevel;
	}

	public void update(final int lightLevel, final double x, final double y, final double z) {
		this.lightLevel = lightLevel;
		entity.posX = x;
		entity.posY = y;
		entity.posZ = z;
	}

	public void updateFromPlayerViewConePart(final EntityPlayer player, final int maxLightLevel, final float lightLevelDecreaseByDistance, final float yawRot, final float pitchRot) {
		final Vec3 pos = player.getPosition(1.0f);
		player.rotationPitch += pitchRot;
		player.rotationYaw += yawRot;
		Vec3 look = player.getLook(1.0f);
		player.rotationPitch -= pitchRot;
		player.rotationYaw -= yawRot;
		look = pos.addVector(look.xCoord * 16d, look.yCoord * 16d, look.zCoord * 16d);
		final MovingObjectPosition mop = player.worldObj.rayTraceBlocks(pos, look);
		if (mop != null) {
			final int dist = (int) Math.round(player.getDistance(mop.blockX + 0.5d, mop.blockY + 0.5d, mop.blockZ + 0.5d));
			update(maxLightLevel - Math.round(dist * lightLevelDecreaseByDistance), mop.blockX + 0.5d, mop.blockY + 0.5d, mop.blockZ + 0.5d);
		}
		else {
			lightLevel = 0;
		}
	}
}