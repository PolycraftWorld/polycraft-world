package edu.utd.minecraft.mod.polycraft.item;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityWaterCannonProjectile;

public class ItemWaterCannon extends ItemFueledProjectileLauncher {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemWaterCannon.class);
	}

	public static boolean allowsActivation(final EntityPlayer player) {
		return !player.isInWater() && isEquipped(player) && getEquippedItem(player).hasFuelRemaining(getEquippedItemStack(player));
	}
	
	public final int velocity;

	public ItemWaterCannon(final CustomObject config, String iconName) {
		super(config, iconName);
		this.velocity = config.params.getInt(2);
	}

	public void spawnProjectiles(final EntityPlayer player, final World world, final Random random) {
		world.spawnEntityInWorld(new EntityWaterCannonProjectile(this, world, player));
	}
}