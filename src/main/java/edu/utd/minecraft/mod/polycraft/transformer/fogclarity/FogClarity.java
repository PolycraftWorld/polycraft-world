package edu.utd.minecraft.mod.polycraft.transformer.fogclarity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaMask;

public class FogClarity {
	public static float getDensityWater(final EntityLivingBase entityLiving) {
		if (entityLiving.isEntityAlive() && entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entityLiving;
			if (player.isInWater()) {
				if (ItemScubaMask.isEquipped(player))
					return ItemScubaMask.getEquippedItem(player).fogDensity;
				else if (ItemPhaseShifter.isEquipped(player))
					return 0;
			}
		}
		return 0.1F - EnchantmentHelper.getRespiration(entityLiving) * 0.03F;
	}
}