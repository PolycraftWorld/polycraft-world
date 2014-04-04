package edu.utd.minecraft.mod.polycraft.fogclarity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaMask;

public class FogClarity {
	public static float getDensityWater(final EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entityLiving;

			final ItemStack flashlightItemStack = player.getCurrentEquippedItem();
			if (player.isEntityAlive() && player.isInWater()) {
				final ItemStack scubaMaskItemStack = player.getCurrentArmor(ArmorSlot.HEAD.getInventoryArmorSlot());
				if (scubaMaskItemStack != null && scubaMaskItemStack.getItem() instanceof ItemScubaMask) {
					return ((ItemScubaMask) scubaMaskItemStack.getItem()).fogDensity;
				}
			}
		}
		return 0.1F - EnchantmentHelper.getRespiration(entityLiving) * 0.03F;
	}
}