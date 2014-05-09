package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemPhaseShifter extends PolycraftUtilityItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemPhaseShifter.class);
	}

	public static ItemPhaseShifter getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public static void createBoundary(final ItemPhaseShifter phaseShifter, final EntityPlayer player, final World world) {
		final int playerX = (int) Math.floor(player.posX);
		final int playerY = (int) Math.floor(player.posY - player.getYOffset());
		final int playerZ = (int) Math.floor(player.posZ);
		for (int x = -phaseShifter.radius; x <= phaseShifter.radius; x++) {
			final boolean xBoundary = (Math.abs(x) == phaseShifter.radius);
			for (int y = -phaseShifter.radius; y <= phaseShifter.radius; y++) {
				final boolean yBoundary = (Math.abs(y) == phaseShifter.radius);
				for (int z = -phaseShifter.radius; z <= phaseShifter.radius; z++) {
					final boolean zBoundary = (Math.abs(z) == phaseShifter.radius);
					final int blockX = playerX + x;
					final int blockY = playerY + y;
					final int blockZ = playerZ + z;
					world.func_147480_a(blockX, blockY, blockZ, true);
					if (phaseShifter.boundaryBlock != null && (xBoundary || yBoundary || zBoundary))
						world.setBlock(blockX, blockY, blockZ, phaseShifter.boundaryBlock);
				}
			}
		}
		player.setPosition(playerX, playerY - phaseShifter.radius + 1, playerZ);
	}

	public final int radius;
	public final float flySpeedBuff;
	public final Block boundaryBlock;

	public ItemPhaseShifter(final CustomObject config) {
		this.setTextureName(PolycraftMod.getAssetName("phase_shifter"));
		this.setCreativeTab(CreativeTabs.tabTools);

		this.radius = config.params.getInt(0);
		this.flySpeedBuff = config.params.getFloat(1);
		this.boundaryBlock = config.params.hasParam(2) ? PolycraftRegistry.getBlock(config.params.get(2)) : null;
	}
}
