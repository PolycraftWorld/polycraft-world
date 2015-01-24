package edu.utd.minecraft.mod.polycraft.item;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ItemPhaseShifter extends PolycraftUtilityItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemPhaseShifter.class);
	}

	public static ItemPhaseShifter getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public static boolean isGlowing(final EntityPlayer player) {
		return isEquipped(player) && (player.isInsideOfMaterial(Material.air) || player.isInsideOfMaterial(Material.water));
	}

	public static Collection<PointLightSource> createLightSources(final World world) {
		final Collection<PointLightSource> lightSources = Lists.newLinkedList();
		lightSources.add(new PointLightSource(world));
		return lightSources;
	}

	public static void createGlow(final EntityPlayer player, final WorldClient world, final Collection<PointLightSource> lightSources) {
		for (final PointLightSource source : lightSources)
			source.update(15, player.posX, player.posY, player.posZ);
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
					final int blockY = playerY + y + 5;
					final int blockZ = playerZ + z;
					world.func_147480_a(blockX, blockY, blockZ, true);
					if (phaseShifter.boundaryBlock != null && (xBoundary || yBoundary || zBoundary))
						world.setBlock(blockX, blockY, blockZ, phaseShifter.boundaryBlock);
				}
			}
		}
	}

	public final int radius;
	public final float velocity;
	public final Block boundaryBlock;

	public ItemPhaseShifter(final CustomObject config) {
		this.setTextureName(PolycraftMod.getAssetName("phase_shifter"));
		this.setCreativeTab(CreativeTabs.tabTools);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.radius = config.params.getInt(0);
		this.velocity = config.params.getFloat(1);
		this.boundaryBlock = config.params.hasParam(2) ? PolycraftRegistry.getBlock(config.params.get(2)) : null;
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	@Override
	public int getMaxItemUseDuration(final ItemStack par1ItemStack) {
		return 10;
	}
}
