package edu.utd.minecraft.mod.polycraft.phaseshifter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;

public class PhaseShifter {
	public static boolean itemRendererIsEntityInsideOpaqueBlock(final EntityPlayer player) {
		final ItemStack currentEquippedItem = player.getCurrentEquippedItem();
		if (currentEquippedItem != null && currentEquippedItem.getItem() instanceof ItemPhaseShifter)
			return false;
		return player.isEntityInsideOpaqueBlock();
	}
}