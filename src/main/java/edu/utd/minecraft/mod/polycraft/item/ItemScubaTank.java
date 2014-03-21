package edu.utd.minecraft.mod.polycraft.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class ItemScubaTank extends ItemArmor {

	private static final ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("scuba_tank", 0, new int[] { 0, 0, 0, 0 }, 0);

	private final Plastic plastic;
	private final int airUnitsFull;
	private final int airUnitsConsumePerTick;

	public ItemScubaTank(final Plastic plastic, final int airUnitsFull, final int airUnitsConsumePerTick) {
		super(armorMaterial, 1, 1);
		this.plastic = plastic;
		this.airUnitsFull = airUnitsFull;
		this.airUnitsConsumePerTick = airUnitsConsumePerTick;
	}

	private void createTagCompound(final ItemStack itemStack) {
		if (itemStack.stackTagCompound == null)
			itemStack.setTagCompound(new NBTTagCompound());
	}

	@Override
	public void onCreated(final ItemStack itemStack, final World world, final EntityPlayer entityPlayer) {
		createTagCompound(itemStack);
		setAirUnitsRemaining(itemStack, airUnitsFull);
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		createTagCompound(itemStack);
		par3List.add(getAirRemainingPercent(itemStack) + "% air remaining");
	}

	public float getFlySpeedBuff() {
		return plastic.jetPackFlySpeedBuff;
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
