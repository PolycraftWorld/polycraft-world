package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public abstract class ItemFueledProjectileLauncher  extends PolycraftUtilityItem {
	private static final String ACTIVATED = "activated";
	private static final String FUEL_UNITS_REMAINING = "fuelUnitsRemaining";

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemFueledProjectileLauncher.class);
	}

	public static ItemFueledProjectileLauncher getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return player.getCurrentEquippedItem();
	}

	public static double getFuelRemainingPercent(final EntityPlayer player) {
		return isEquipped(player) ? getEquippedItem(player).getFuelRemainingPercent(getEquippedItemStack(player)) : 0;
	}

	public static boolean getActivated(final EntityPlayer player) {
		return isEquipped(player) && PolycraftItemHelper.getBoolean(getEquippedItemStack(player), ACTIVATED, false);
	}

	public static void setActivated(final EntityPlayer player, final boolean activated) {
		if (isEquipped(player))
			PolycraftItemHelper.setBoolean(getEquippedItemStack(player), ACTIVATED, activated);
	}

	public static boolean burnFuel(final EntityPlayer player) {
		return getEquippedItem(player).burnFuel(getEquippedItemStack(player));
	}

	public final int fuelUnitsFull;
	public final int fuelUnitsBurnPerTick;

	public ItemFueledProjectileLauncher(final CustomObject config, String iconName) {
		this.setTextureName(PolycraftMod.getAssetName(iconName));
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxDamage(100);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.fuelUnitsFull = config.params.getInt(0);
		this.fuelUnitsBurnPerTick = config.params.getInt(1);
	}

	public abstract void spawnProjectiles(final EntityPlayer player, final World world, final Random random);

	@Override
	public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	@Override
	public int getMaxItemUseDuration(final ItemStack par1ItemStack) {
		return 10;
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		double percent = getFuelRemainingPercent(itemStack);
		if (percent > 0)
			par3List.add(String.format("%1$.1f%% fuel remaining", percent * 100));
	}

	private void setFuelUnitsRemaining(final ItemStack itemStack, int fuelUnitsRemaining) {
		PolycraftItemHelper.setInteger(itemStack, FUEL_UNITS_REMAINING, fuelUnitsRemaining);
	}

	private int getFuelUnitsRemaining(final ItemStack itemStack) {
		return PolycraftItemHelper.getInteger(itemStack, FUEL_UNITS_REMAINING, fuelUnitsFull);
	}

	protected boolean hasFuelRemaining(final ItemStack itemStack) {
		return getFuelUnitsRemaining(itemStack) > 0;
	}

	private boolean burnFuel(final ItemStack itemStack) {
		int fuelUnitsRemaining = getFuelUnitsRemaining(itemStack) - fuelUnitsBurnPerTick;
		if (fuelUnitsRemaining < 0)
			fuelUnitsRemaining = 0;
		setFuelUnitsRemaining(itemStack, fuelUnitsRemaining);
		return fuelUnitsRemaining > 0;
	}

	private double getFuelRemainingPercent(final ItemStack itemStack) {
		if (fuelUnitsFull > 0)
			return (double) getFuelUnitsRemaining(itemStack) / fuelUnitsFull;
		return 0;
	}
}
