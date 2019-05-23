package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemParachute extends PolycraftUtilityItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemParachute.class);
	}

	public static ItemParachute getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public static boolean allowsSlowFall(final EntityPlayer player) {
		return ItemParachute.isEquipped(player) && !player.capabilities.isFlying;
	}

	public final float velocityDescent;

	public ItemParachute(final CustomObject config) {
		//this.setTextureName(PolycraftMod.getAssetName("parachute"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.velocityDescent = config.params.getFloat(0);
	}
}
