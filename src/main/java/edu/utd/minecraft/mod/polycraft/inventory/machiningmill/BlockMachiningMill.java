package edu.utd.minecraft.mod.polycraft.inventory.machiningmill;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockMachiningMill extends BlockContainer {

	private final Random random = new Random();

	@SideOnly(Side.CLIENT)
	private IIcon iconTop;
	@SideOnly(Side.CLIENT)
	private IIcon iconFront;

	public BlockMachiningMill() {
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return Item.getItemFromBlock(PolycraftMod.getBlock("Machining Mill"));
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int front) {
		// if the front is the top, it must be in the player inventory, so just render the front as side 3 (like furnace)
		if (front == 0 && side == 3)
			return this.iconFront;
		if (side == 0 || side == 1)
			return this.iconTop;
		if (side != front)
			return this.blockIcon;
		return this.iconFront;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		this.blockIcon = p_149651_1_.registerIcon(PolycraftMod.getAssetName("machining_mill_side"));
		this.iconTop = p_149651_1_.registerIcon(PolycraftMod.getAssetName("machining_mill_top"));
		this.iconFront = p_149651_1_.registerIcon(PolycraftMod.getAssetName("machining_mill_front"));
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			TileEntityMachiningMill tileentitymachiningmill = (TileEntityMachiningMill) world.getTileEntity(x, y, z);
			if (tileentitymachiningmill != null) {
				player.openGui(PolycraftMod.instance, PolycraftMod.guiMachiningMillID, world, x, y, z);
			}

			return true;
		}
		return false;
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityMachiningMill();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		if (p_149689_6_.hasDisplayName()) {
			((TileEntityMachiningMill) p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_)).setInventoryName(p_149689_6_.getDisplayName());
		}
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal strength when this block inputs to a comparator.
	 */
	@Override
	public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_) {
		return Container.calcRedstoneFromInventory((IInventory) p_149736_1_.getTileEntity(p_149736_2_, p_149736_3_, p_149736_4_));
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
		return Item.getItemFromBlock(PolycraftMod.getBlock("Machining Mill"));
	}
}