package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.SourcedVesselConfig;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class ItemBlockPipe extends ItemBlock implements PolycraftItem {

	public final BlockPipe blockPipe;
	
	public ItemBlockPipe(Block block) {
		super(block);
		this.blockPipe = (BlockPipe) block;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return blockPipe.getUnlocalizedName(par1ItemStack.getItemDamage());
	}
}