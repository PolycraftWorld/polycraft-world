package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemJetPack extends ItemArmor {

	public final int fuelUnitsFull;
	public final int fuelUnitsBurnPerTick;
	public final float flySpeedBuff;

	public ItemJetPack(final int fuelUnitsFull, final int fuelUnitsBurnPerTick, final float flySpeedBuff) {
		super(PolycraftMod.armorMaterialNone, 1, 1);
		this.setTextureName(PolycraftMod.getTextureName("jet_pack"));
		this.fuelUnitsFull = fuelUnitsFull;
		this.fuelUnitsBurnPerTick = fuelUnitsBurnPerTick;
		this.flySpeedBuff = flySpeedBuff;
	}

	private void createTagCompound(final ItemStack itemStack) {
		if (itemStack.stackTagCompound == null)
			itemStack.setTagCompound(new NBTTagCompound());
	}

	@Override
	public void onCreated(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
		createTagCompound(itemStack);
		setFuelUnitsRemaining(itemStack, fuelUnitsFull);
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		createTagCompound(itemStack);
		int fuel = getFuelRemainingPercent(itemStack);
		if (fuel > 0)
			par3List.add(fuel + "% fuel remaining");
	}

	public static void setFuelUnitsRemaining(final ItemStack itemStack, int fuelUnitsRemaining) {
		itemStack.stackTagCompound.setInteger("fuelUnitsRemaining", fuelUnitsRemaining);
	}

	public static int getFuelUnitsRemaining(final ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger("fuelUnitsRemaining");
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
		return (int) (((double) getFuelUnitsRemaining(itemStack) / fuelUnitsFull) * 100);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getTextureName("textures/models/armor/jet_pack_layer_1.png");
	}
}