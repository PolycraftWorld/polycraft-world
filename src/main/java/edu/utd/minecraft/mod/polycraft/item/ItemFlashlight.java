package edu.utd.minecraft.mod.polycraft.item;

import java.util.Collection;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ItemFlashlight extends PolycraftUtilityItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemFlashlight.class);
	}

	public static ItemFlashlight getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	private static final float[][] lightSourceTransforms = new float[][] {
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

	public static Collection<PointLightSource> createLightSources(final World world) {
		final Collection<PointLightSource> lightSources = Lists.newLinkedList();
		for (int i = 0; i < lightSourceTransforms.length; i++)
			lightSources.add(new PointLightSource(world));
		return lightSources;
	}

	public static void createLightCone(final EntityPlayer player, final Collection<PointLightSource> lightSources) {
		final ItemFlashlight flashlightItem = getEquippedItem(player);
		int i = 0;
		for (final PointLightSource source : lightSources) {
			source.updateFromPlayerViewConePart(
					player, flashlightItem.maxLightLevel, flashlightItem.lightLevelDecreaseByDistance,
					lightSourceTransforms[i][0] * flashlightItem.viewingConeAngle,
					lightSourceTransforms[i][1] * flashlightItem.viewingConeAngle);
			i++;
		}
	}

	public final int maxLightLevel;
	public final float lightLevelDecreaseByDistance;
	public final int viewingConeAngle;

	public ItemFlashlight(final CustomObject config) {
		this.setTextureName(PolycraftMod.getAssetName("flashlight"));
		this.setCreativeTab(CreativeTabs.tabTools);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.maxLightLevel = config.params.getInt(0);
		this.lightLevelDecreaseByDistance = config.params.getFloat(1);
		this.viewingConeAngle = config.params.getInt(2);
	}
}
