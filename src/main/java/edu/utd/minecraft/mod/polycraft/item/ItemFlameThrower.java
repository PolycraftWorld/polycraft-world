package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemFlameThrower extends PolycraftUtilityItem {

	private static final String FUEL_UNITS_REMAINING = "fuelUnitsRemaining";

	public final int fuelUnitsFull;
	public final int fuelUnitsBurnPerTick;
	public final int range;
	public final int spread;
	public final int fireDuration;
	public final int damage;

	public ItemFlameThrower(final int fuelUnitsFull, final int fuelUnitsBurnPerTick, final int range, final int spread, final int fireDuration, final int damage) {
		this.setTextureName(PolycraftMod.getAssetName("flame_thrower"));
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxDamage(100);
		this.fuelUnitsFull = fuelUnitsFull;
		this.fuelUnitsBurnPerTick = fuelUnitsBurnPerTick;
		this.range = range;
		this.spread = spread;
		this.fireDuration = fireDuration;
		this.damage = damage;
	}

	@Override
	public void onCreated(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
		// TODO: this doesn't work when a player shift clicks a recipe to create multiple flame throwers at once
		PolycraftItemHelper.createTagCompound(itemStack);
		setFuelUnitsRemaining(itemStack, fuelUnitsFull);
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		PolycraftItemHelper.createTagCompound(itemStack);
		int fuel = getFuelRemainingPercent(itemStack);
		if (fuel > 0) {
			par3List.add(fuel + "% fuel remaining");
		}
	}

	public static void setFuelUnitsRemaining(final ItemStack itemStack, int fuelUnitsRemaining) {
		PolycraftItemHelper.setInteger(itemStack, FUEL_UNITS_REMAINING, fuelUnitsRemaining);
	}

	public static int getFuelUnitsRemaining(final ItemStack itemStack) {
		return PolycraftItemHelper.getIntegerOrDefault(itemStack, FUEL_UNITS_REMAINING, 0);
	}

	public static boolean hasFuelRemaining(final ItemStack itemStack) {
		return getFuelUnitsRemaining(itemStack) > 0;
	}

	public boolean burnFuel(final ItemStack itemStack) {
		int fuelUnitsRemaining = getFuelUnitsRemaining(itemStack) - fuelUnitsBurnPerTick;
		if (fuelUnitsRemaining < 0)
			fuelUnitsRemaining = 0;
		setFuelUnitsRemaining(itemStack, fuelUnitsRemaining);
		return fuelUnitsRemaining > 0;
	}

	public int getFuelRemainingPercent(final ItemStack itemStack) {
		if (fuelUnitsFull != 0) {
			return (int) (((double) getFuelUnitsRemaining(itemStack) / fuelUnitsFull) * 100);
		}
		return 0;
	}
}
