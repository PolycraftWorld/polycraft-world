package edu.utd.minecraft.mod.polycraft.inventory.oilderrick;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class OilDerrickBlock extends PolycraftInventoryBlock {

	@SideOnly(Side.CLIENT)
	public IIcon iconOutside;
	@SideOnly(Side.CLIENT)
	public IIcon iconTop;
	@SideOnly(Side.CLIENT)
	public IIcon iconInside;

	public OilDerrickBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 2.5F);
		this.setHardness(20);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		return p_149691_1_ == 1 ? this.iconTop : this.iconOutside;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.iconOutside = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_outside")));
		this.iconTop = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_top")));
		this.iconInside = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_inside")));
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
	 */
	@Override
	public void addCollisionBoxesToList(World worldObj, int xCoord, int yCoord, int zCoord, AxisAlignedBB mask_in, List list_in, Entity collide_in) {
		
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.33F, 1.0F);
		super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask_in, list_in, collide_in);

		float f = 0.25F;
		this.setBlockBounds(0.25F, 0.33F, 0.25F, 0.75F, 0.66F, 0.75F);
		super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask_in, list_in, collide_in);
		
		this.setBlockBounds(0.375F, 0.66F, 0.375F, 0.625F, 1.0F, 0.625F);
		super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask_in, list_in, collide_in);

//		this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
//		super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask_in, list_in, collide_in);
//		this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
//		super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask_in, list_in, collide_in);

		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void breakBlock(World world, int xCoord, int yCoord, int zCoord, Block p_149749_5_, int p_149749_6_)
	{
		final Block oreBlock = world.getBlock(xCoord, yCoord - 1, zCoord);
		int meta = world.getBlockMetadata(xCoord, yCoord - 1, zCoord);
		if (meta > 0)
			world.setBlock(xCoord, yCoord - 1, zCoord, oreBlock, meta - 1, 2); // remove the rest of the oil in this meta level
		else
			world.setBlock(xCoord, yCoord - 1, zCoord, oreBlock, 0, 2); // no more oil

		super.breakBlock(world, xCoord, yCoord, zCoord, p_149749_5_, p_149749_6_);

	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
	}

	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	@Override
	public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_) {
		int j1 = Facing.oppositeSide[p_149660_5_];

		if (j1 == 1) {
			j1 = 0;
		}

		return j1;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
	{
		this.func_149919_e(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
	}

	private void func_149919_e(World p_149919_1_, int p_149919_2_, int p_149919_3_, int p_149919_4_)
	{
		int l = p_149919_1_.getBlockMetadata(p_149919_2_, p_149919_3_, p_149919_4_);
		int i1 = OilDerrickInventory.getDirectionFromMetadata(l);
		boolean flag = !p_149919_1_.isBlockIndirectlyGettingPowered(p_149919_2_, p_149919_3_, p_149919_4_);
		boolean flag1 = func_149917_c(l);

		if (flag != flag1)
		{
			p_149919_1_.setBlockMetadataWithNotify(p_149919_2_, p_149919_3_, p_149919_4_, i1 | (flag ? 0 : 8), 4);
		}
	}

	public static boolean func_149917_c(int p_149917_0_)
	{
		return (p_149917_0_ & 8) != 8;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
	{
		return true;
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal strength when this block inputs to a comparator.
	 */
	@Override
	public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
	{
		return Container.calcRedstoneFromInventory(func_149920_e(p_149736_1_, p_149736_2_, p_149736_3_, p_149736_4_));
	}

	public static OilDerrickInventory func_149920_e(IBlockAccess p_149920_0_, int p_149920_1_, int p_149920_2_, int p_149920_3_)
	{
		return (OilDerrickInventory) p_149920_0_.getTileEntity(p_149920_1_, p_149920_2_, p_149920_3_);
	}

	/**
	 * Gets the icon name of the ItemBlock corresponding to this block. Used by oil derricks.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName() {
		return PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name));
	}
}
