package edu.utd.minecraft.mod.polycraft.item;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityFreezeRayProjectile;

public class ItemFreezeRay extends ItemFueledProjectileLauncher {
	
	private static final float particleVelocity = 1f;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemFreezeRay.class);
	}

	public static boolean allowsActivation(final EntityPlayer player) {
		return !player.isInWater() && isEquipped(player) && getEquippedItem(player).hasFuelRemaining(getEquippedItemStack(player));
	}
	
	public final int velocity;
	public final int freezeDuration;
	public final int damage;

	public ItemFreezeRay(final CustomObject config, String iconName) {
		super(config, iconName);
		this.velocity = config.params.getInt(2);
		this.freezeDuration = config.params.getInt(3);
		this.damage = config.params.getInt(4);
	}

	public void spawnProjectiles(final EntityPlayer player, final World world, final Random random) {
		world.spawnEntityInWorld(new EntityFreezeRayProjectile(this, world, player));
	}
}