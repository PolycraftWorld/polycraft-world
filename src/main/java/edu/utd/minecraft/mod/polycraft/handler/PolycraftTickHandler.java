package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import edu.utd.minecraft.mod.polycraft.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.dynamiclights.IDynamicLightSource;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;

public class PolycraftTickHandler
{
	private final Minecraft mincraft;
	private boolean lightsEnabled = false;
	private final Collection<PolycraftLightSource> dynamicLights = new LinkedList<PolycraftLightSource>();
	private final float[][] dynamicLightConeTransforms = new float[][] {
			new float[] { 0, 0 },
			new float[] { .25f, .25f },
			new float[] { .25f, -.25f },
			new float[] { -.25f, .25f },
			new float[] { -.25f, -.25f },
			new float[] { .5f, .5f },
			new float[] { .5f, -.5f },
			new float[] { -.5f, .5f },
			new float[] { -.5f, -.5f },
			new float[] { .75f, .75f },
			new float[] { .75f, -.75f },
			new float[] { -.75f, .75f },
			new float[] { -.75f, -.75f },
			new float[] { 1, 1 },
			new float[] { 1, -1 },
			new float[] { -1, 1 },
			new float[] { -1, -1 } };

	public PolycraftTickHandler() {
		mincraft = FMLClientHandler.instance().getClient();
	}

	@SubscribeEvent
	public void onTick(final TickEvent.RenderTickEvent tick) {
		if (tick.phase == Phase.END && mincraft.theWorld != null) {
			boolean lightsEnabled = false;
			final EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
			if (player != null && player.isEntityAlive()) {

				if (dynamicLights.size() == 0)
					for (int i = 0; i < dynamicLightConeTransforms.length; i++)
						dynamicLights.add(new PolycraftLightSource(player.worldObj));

				final ItemStack currentEquippedItemStack = player.getCurrentEquippedItem();
				if (currentEquippedItemStack != null) {
					if (currentEquippedItemStack.getItem() instanceof ItemFlashlight) {
						final ItemFlashlight flashlightItem = (ItemFlashlight) currentEquippedItemStack.getItem();

						int i = 0;
						for (final PolycraftLightSource source : dynamicLights) {
							handleLightConePart(player, source, flashlightItem.maxLightLevel, flashlightItem.lightLevelDecreaseByDistance,
									dynamicLightConeTransforms[i][0] * flashlightItem.viewingConeAngle, dynamicLightConeTransforms[i][1] * flashlightItem.viewingConeAngle);
							i++;
						}
						lightsEnabled = true;
					}
				}
			}

			if (this.lightsEnabled != lightsEnabled) {
				this.lightsEnabled = lightsEnabled;
				for (final PolycraftLightSource source : dynamicLights) {
					if (lightsEnabled)
						DynamicLights.addLightSource(source);
					else
						DynamicLights.removeLightSource(source);
				}
			}
			DynamicLights.updateLights(mincraft.theWorld);
		}
	}

	private void handleLightConePart(final EntityPlayer player, final PolycraftLightSource source, final int maxLightLevel, final float lightLevelDecreaseByDistance, final float yawRot, final float pitchRot) {
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
			source.lightLevel = maxLightLevel - Math.round(dist * lightLevelDecreaseByDistance);
			source.entity.posX = mop.blockX + 0.5d;
			source.entity.posY = mop.blockY + 0.5d;
			source.entity.posZ = mop.blockZ + 0.5d;
		}
		else {
			source.lightLevel = 0;
		}
	}

	private class PolycraftLightSource implements IDynamicLightSource
	{
		private class PolycraftLightEntity extends Entity {
			public PolycraftLightEntity(final World world) {
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

		private PolycraftLightSource(final World world) {
			entity = new PolycraftLightEntity(world);
		}

		@Override
		public Entity getAttachmentEntity() {
			return entity;
		}

		@Override
		public int getLightLevel() {
			return lightLevel;
		}
	}
}
