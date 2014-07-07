package edu.utd.minecraft.mod.polycraft.item;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPlasticChest;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ItemPlasticChest extends  ItemBlock implements PolycraftItem {

	public final BlockPlasticChest blockPlasticChest;

	public ItemPlasticChest(BlockPlasticChest p_i45355_1_) {
		super(p_i45355_1_);
		this.setHasSubtypes(true);
		this.blockPlasticChest = (BlockPlasticChest) p_i45355_1_;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS;
	}

	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}