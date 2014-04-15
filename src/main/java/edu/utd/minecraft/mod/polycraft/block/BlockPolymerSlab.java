package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Polymer;

public class BlockPolymerSlab extends BlockSlab
{
	private final Polymer polymer;
	private final boolean isDouble;

	private final LabelTexture labelTexture;

	public BlockPolymerSlab(final Polymer polymer, boolean isDouble)
	{
		super(isDouble, Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymer = polymer;
		this.labelTexture = polymer.resinCode.recyclingNumber >= 1 && polymer.resinCode.recyclingNumber <= 7
				? new LabelTexture("polymer", null, "polymer_" + polymer.resinCode.recyclingNumber + "_bottom")
				: new LabelTexture("polymer", null, "polymer_bottom");
		this.isDouble = isDouble;
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		return labelTexture.getIcon(p_149691_1_, p_149691_2_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		labelTexture.registerBlockIcons(p_149651_1_);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, polymer.itemNameSlab);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int p_149644_1_)
	{
		return new ItemStack(Item.getItemFromBlock(Blocks.stone_slab), 2, p_149644_1_ & 7);
	}

	@Override
	public String func_150002_b(int p_150002_1_)
	{
		return PolycraftMod.getRegistryName(PolycraftMod.RegistryNamespace.Polymer, polymer.blockNameSlab);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
	{
		if (p_149666_1_ != PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, polymer.blockNameDoubleSlab))
		{
			for (int i = 0; i <= 7; ++i)
			{
				if (i != 2)
				{
					p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
				}
			}
		}
	}
}