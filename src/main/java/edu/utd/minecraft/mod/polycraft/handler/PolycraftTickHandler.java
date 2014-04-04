package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import edu.utd.minecraft.mod.polycraft.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.dynamiclights.PointLightSource;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;

public class PolycraftTickHandler
{
	private final Minecraft mincraft;
	private boolean flashlightEnabled = false;
	private final Collection<PointLightSource> flashlightDynamicLights = new LinkedList<PointLightSource>();
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

				if (flashlightDynamicLights.size() == 0)
					for (int i = 0; i < dynamicLightConeTransforms.length; i++)
						flashlightDynamicLights.add(new PointLightSource(player.worldObj));

				final ItemStack currentEquippedItemStack = player.getCurrentEquippedItem();
				if (currentEquippedItemStack != null) {
					if (currentEquippedItemStack.getItem() instanceof ItemFlashlight) {
						final ItemFlashlight flashlightItem = (ItemFlashlight) currentEquippedItemStack.getItem();

						int i = 0;
						for (final PointLightSource source : flashlightDynamicLights) {
							source.updateFromPlayerViewConePart(player, flashlightItem.maxLightLevel, flashlightItem.lightLevelDecreaseByDistance,
									dynamicLightConeTransforms[i][0] * flashlightItem.viewingConeAngle, dynamicLightConeTransforms[i][1] * flashlightItem.viewingConeAngle);
							i++;
						}
						lightsEnabled = true;
					}
				}
			}

			if (this.flashlightEnabled != lightsEnabled) {
				this.flashlightEnabled = lightsEnabled;
				for (final PointLightSource source : flashlightDynamicLights) {
					if (lightsEnabled)
						DynamicLights.addLightSource(source);
					else
						DynamicLights.removeLightSource(source);
				}
			}
			DynamicLights.updateLights(mincraft.theWorld);
		}
	}
}
