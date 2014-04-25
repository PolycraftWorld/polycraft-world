package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import edu.utd.minecraft.mod.polycraft.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.dynamiclights.PointLightSource;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;

public class PolycraftTickHandler extends PolycraftHandler
{
	private final Minecraft minecraft;
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

	private static final Map<Integer, String> jetPackLandingWarnings = new LinkedHashMap<Integer, String>();
	static {
		jetPackLandingWarnings.put(0, "Hope you packed a parachute...");
		jetPackLandingWarnings.put(1, "EJECT EJECT EJECT!!!");
		jetPackLandingWarnings.put(5, "vapor lock!!");
		jetPackLandingWarnings.put(10, "we are way low on fuel Mav!");
		jetPackLandingWarnings.put(20, "daredevil are we?");
		jetPackLandingWarnings.put(30, "might want to start thinking about landing...");
	}

	public PolycraftTickHandler() {
		minecraft = FMLClientHandler.instance().getClient();
	}

	@SubscribeEvent
	public void onTick(final TickEvent.RenderTickEvent tick) {
		if (tick.phase == Phase.END && minecraft.theWorld != null) {
			final EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
			handleDynamicLights(player);
			handleItemStatus(player);
		}
	}

	private void handleDynamicLights(final EntityPlayer player) {
		boolean lightsEnabled = false;
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
		DynamicLights.updateLights(minecraft.theWorld);
	}

	private void handleItemStatus(final EntityPlayer player) {
		if (player != null && player.isEntityAlive() && minecraft.currentScreen == null) {
			int vert = 10;
			int x = 5;
			int y = 5;

			final ItemStack chestItemStack = player.getCurrentArmor(ArmorSlot.CHEST.getInventoryArmorSlot());
			if (chestItemStack != null) {
				if (chestItemStack.getItem() instanceof ItemJetPack) {
					ItemJetPack jetPackItem = (ItemJetPack) chestItemStack.getItem();
					final int fuelRemainingPercent = jetPackItem.getFuelRemainingPercent(chestItemStack);
					String message = jetPackItem.getItemStackDisplayName(chestItemStack) + ": " + fuelRemainingPercent + "%";
					for (Entry<Integer, String> warningEntry : jetPackLandingWarnings.entrySet()) {
						if (fuelRemainingPercent <= warningEntry.getKey()) {
							message += " " + warningEntry.getValue();
							break;
						}
					}
					minecraft.fontRenderer.drawStringWithShadow(message, x, y, 16777215);
					y += vert;
				}
				else if (chestItemStack.getItem() instanceof ItemScubaTank) {
					final ItemScubaTank scubaTankItem = (ItemScubaTank) chestItemStack.getItem();
					minecraft.fontRenderer.drawStringWithShadow(scubaTankItem.getItemStackDisplayName(chestItemStack) + ": " + scubaTankItem.getAirRemainingPercent(chestItemStack) + "%", x, y, 16777215);
					y += vert;
				}
			}

			final ItemStack currentEquippedItemStack = player.getCurrentEquippedItem();
			if (currentEquippedItemStack != null) {
				if (currentEquippedItemStack.getItem() instanceof ItemFlameThrower) {
					ItemFlameThrower flameThrowerItem = (ItemFlameThrower) currentEquippedItemStack.getItem();
					minecraft.fontRenderer.drawStringWithShadow(flameThrowerItem.getItemStackDisplayName(currentEquippedItemStack) + ": " + flameThrowerItem.getFuelRemainingPercent(currentEquippedItemStack) + "%", x, y, 16777215);
					y += vert;
				}
			}
		}
	}
}
