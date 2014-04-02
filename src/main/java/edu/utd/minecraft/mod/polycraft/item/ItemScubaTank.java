package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemScubaTank extends PolycraftArmorChest {

	public final int airUnitsFull;
	public final int airUnitsConsumePerTick;

	public ItemScubaTank(final int airUnitsFull, final int airUnitsConsumePerTick) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getTextureName("scuba_tank"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.airUnitsFull = airUnitsFull;
		this.airUnitsConsumePerTick = airUnitsConsumePerTick;
	}

	private void createTagCompound(final ItemStack itemStack) {
		if (itemStack.stackTagCompound == null)
			itemStack.setTagCompound(new NBTTagCompound());
	}

	@Override
	public void onCreated(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
		//TODO this doesn't work when a player shift clicks a recipe to create multiple scuba tanks at once
		createTagCompound(itemStack);
		setAirUnitsRemaining(itemStack, airUnitsFull);
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		createTagCompound(itemStack);
		int air = getAirRemainingPercent(itemStack);
		if (air > 0)
			par3List.add(air + "% air remaining");
	}

	public static void setAirUnitsRemaining(final ItemStack itemStack, int airUnitsRemaining) {
		itemStack.stackTagCompound.setInteger("airUnitsRemaining", airUnitsRemaining);
	}

	public static int getAirUnitsRemaining(final ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger("airUnitsRemaining");
	}

	public static boolean hasAirRemaining(final ItemStack itemStack) {
		return getAirUnitsRemaining(itemStack) > 0;
	}

	public boolean consumeAir(final ItemStack itemStack) {
		int airUnitsRemaining = getAirUnitsRemaining(itemStack) - airUnitsConsumePerTick;
		if (airUnitsRemaining < 0)
			airUnitsRemaining = 0;
		setAirUnitsRemaining(itemStack, airUnitsRemaining);
		return airUnitsRemaining > 0;
	}

	public int getAirRemainingPercent(final ItemStack itemStack) {
		return (int) (((double) getAirUnitsRemaining(itemStack) / airUnitsFull) * 100);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getTextureName("textures/models/armor/scuba_layer_1.png");
	}
}
