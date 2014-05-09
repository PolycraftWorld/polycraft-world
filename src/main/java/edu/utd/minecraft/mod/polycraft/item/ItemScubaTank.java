package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemScubaTank extends PolycraftArmorChest {
	private final static String AIR_UNITS_REMAINING = "airUnitsRemaining";
	private static final ArmorSlot armorSlot = ArmorSlot.CHEST;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, armorSlot, ItemScubaTank.class);
	}

	public static ItemScubaTank getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, armorSlot);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItemStack(player, armorSlot);
	}

	public static boolean allowsWaterBreathing(final EntityPlayer player) {
		return player.isInWater() && isEquipped(player) && getEquippedItem(player).hasAirRemaining(getEquippedItemStack(player));
	}

	public static double getAirRemainingPercent(final EntityPlayer player) {
		return isEquipped(player) ? getEquippedItem(player).getAirRemainingPercent(getEquippedItemStack(player)) : 0;
	}

	public static void consumeAir(final EntityPlayer player) {
		getEquippedItem(player).consumeAir(getEquippedItemStack(player));
	}

	public final int airUnitsFull;
	public final int airUnitsConsumePerTick;

	public ItemScubaTank(final CustomObject config) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getAssetName("scuba_tank"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.airUnitsFull = config.params.getInt(0);
		this.airUnitsConsumePerTick = config.params.getInt(1);
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		double percent = getAirRemainingPercent(itemStack);
		if (percent > 0)
			par3List.add(String.format("%1$.1f%% air remaining", percent * 100));
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/scuba_layer_1.png");
	}

	private double getAirRemainingPercent(final ItemStack itemStack) {
		if (airUnitsFull > 0)
			return (double) getAirUnitsRemaining(itemStack) / airUnitsFull;
		return 0;
	}

	private void setAirUnitsRemaining(final ItemStack itemStack, int airUnitsRemaining) {
		PolycraftItemHelper.setInteger(itemStack, AIR_UNITS_REMAINING, airUnitsRemaining);
	}

	private int getAirUnitsRemaining(final ItemStack itemStack) {
		return PolycraftItemHelper.getInteger(itemStack, AIR_UNITS_REMAINING, airUnitsFull);
	}

	private boolean hasAirRemaining(final ItemStack itemStack) {
		return getAirUnitsRemaining(itemStack) > 0;
	}

	private boolean consumeAir(final ItemStack itemStack) {
		int airUnitsRemaining = Math.max(0, getAirUnitsRemaining(itemStack) - airUnitsConsumePerTick);
		setAirUnitsRemaining(itemStack, airUnitsRemaining);
		return airUnitsRemaining > 0;
	}
}
