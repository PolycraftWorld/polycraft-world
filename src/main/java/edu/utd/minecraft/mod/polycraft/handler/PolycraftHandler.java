package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class PolycraftHandler {

	protected static final Random random = new Random();

	protected static boolean checkCurrentEquippedItem(final EntityPlayer player, Class itemClass) {
		return checkCurrentEquippedItem(player, itemClass, false);
	}

	protected static boolean checkCurrentEquippedItem(final EntityPlayer player, Class itemClass, boolean notBroken) {
		return checkItem(player.getCurrentEquippedItem(), itemClass, notBroken);
	}

	protected static boolean checkItem(final ItemStack itemStack, Class itemClass) {
		return checkItem(itemStack, itemClass, false);
	}

	protected static boolean checkItem(final ItemStack itemStack, Class itemClass, boolean notBroken) {
		return (itemStack != null && itemStack.getItem().getClass().equals(itemClass) && (!notBroken || itemStack.getItemDamage() < itemStack.getMaxDamage()));
	}

	protected static Block getBlockUnderPlayer(final EntityPlayer player) {
		return player.worldObj.getBlock((int) Math.floor(player.posX), (int) Math.floor(player.posY - player.getYOffset()) - 1, (int) Math.floor(player.posZ));
	}

	protected static boolean isClient(final EntityPlayer player) {
		return player.worldObj.isRemote;
	}

	protected static boolean isServer(final EntityPlayer player) {
		return !player.worldObj.isRemote;
	}
}
