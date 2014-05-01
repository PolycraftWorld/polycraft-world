package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemScubaTank extends PolycraftArmorChest {
	private final static String AIR_UNITS_REMAINING = "airUnitsRemaining";

	public final int airUnitsFull;
	public final int airUnitsConsumePerTick;

	public ItemScubaTank(final CustomObject config) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getAssetName("scuba_tank"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.airUnitsFull = config.getParamInteger(0);
		this.airUnitsConsumePerTick = config.getParamInteger(1);
	}

	@Override
	public void onCreated(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
		//TODO this doesn't work when a player shift clicks a recipe to create multiple scuba tanks at once
		PolycraftItemHelper.createTagCompound(itemStack);
		setAirUnitsRemaining(itemStack, airUnitsFull);
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		PolycraftItemHelper.createTagCompound(itemStack);
		int air = getAirRemainingPercent(itemStack);
		if (air > 0) {
			par3List.add(air + "% air remaining");
		}
	}

	public static void setAirUnitsRemaining(final ItemStack itemStack, int airUnitsRemaining) {
		PolycraftItemHelper.setInteger(itemStack, AIR_UNITS_REMAINING, airUnitsRemaining);
	}

	public static int getAirUnitsRemaining(final ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger(AIR_UNITS_REMAINING);
	}

	public static boolean hasAirRemaining(final ItemStack itemStack) {
		return getAirUnitsRemaining(itemStack) > 0;
	}

	public boolean consumeAir(final ItemStack itemStack) {
		int airUnitsRemaining = Math.max(0, getAirUnitsRemaining(itemStack) - airUnitsConsumePerTick);
		setAirUnitsRemaining(itemStack, airUnitsRemaining);
		return airUnitsRemaining > 0;
	}

	public int getAirRemainingPercent(final ItemStack itemStack) {
		if (airUnitsFull == 0) {
			return 0;
		}
		return (int) (((double) getAirUnitsRemaining(itemStack) / airUnitsFull) * 100);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/scuba_layer_1.png");
	}
}
