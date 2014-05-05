package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemJetPack extends PolycraftArmorChest {
	private static final String FUEL_UNITS_REMAINING = "fuelUnitsRemaining";

	public final int fuelUnitsFull;
	public final int fuelUnitsBurnPerTick;
	public final float flySpeedBuff;

	public ItemJetPack(final CustomObject config) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getAssetName("jet_pack"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.fuelUnitsFull = config.params.getInt(0);
		this.fuelUnitsBurnPerTick = config.params.getInt(1);
		this.flySpeedBuff = config.params.getFloat(2);
	}

	@Override
	public void onCreated(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
		// TODO: this doesn't work when a player shift clicks a recipe to create multiple jet packs at once
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

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/jet_pack_layer_1.png");
	}
}
