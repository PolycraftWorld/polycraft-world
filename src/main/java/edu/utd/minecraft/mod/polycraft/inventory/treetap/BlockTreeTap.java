package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockTreeTap extends BlockContainer
{
	private final Random field_149922_a = new Random();
	@SideOnly(Side.CLIENT)
	private IIcon field_149921_b;
	@SideOnly(Side.CLIENT)
	private IIcon field_149923_M;
	@SideOnly(Side.CLIENT)
	private IIcon field_149924_N;

	public BlockTreeTap()
	{
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
	 */
	@Override
	public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
		super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
		float f = 0.125F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
		this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
		this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	@Override
	public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
	{
		int j1 = Facing.oppositeSide[p_149660_5_];

		if (j1 == 1)
		{
			j1 = 0;
		}

		return j1;
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityTreeTap();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
	{
		super.onBlockPlacedBy(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);

		if (p_149689_6_.hasDisplayName())
		{
			TileEntityTreeTap tileentitytreetap = func_149920_e(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_);
			tileentitytreetap.setCustomInventoryName(p_149689_6_.getDisplayName());
		}
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
	{
		super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
		this.func_149919_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			TileEntityTreeTap tileentitytreetap = (TileEntityTreeTap) world.getTileEntity(x, y, z);
			if (tileentitytreetap != null) {
				player.openGui(PolycraftMod.instance, PolycraftMod.guiTreeTapID, world, x, y, z);
			}

			return true;
		}
		return false;
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
		int i1 = getDirectionFromMetadata(l);
		boolean flag = !p_149919_1_.isBlockIndirectlyGettingPowered(p_149919_2_, p_149919_3_, p_149919_4_);
		boolean flag1 = func_149917_c(l);

		if (flag != flag1)
		{
			p_149919_1_.setBlockMetadataWithNotify(p_149919_2_, p_149919_3_, p_149919_4_, i1 | (flag ? 0 : 8), 4);
		}
	}

	@Override
	public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
	{
		TileEntityTreeTap tileentitytreetap = (TileEntityTreeTap) p_149749_1_.getTileEntity(p_149749_2_, p_149749_3_, p_149749_4_);

		if (tileentitytreetap != null)
		{
			for (int i1 = 0; i1 < tileentitytreetap.getSizeInventory(); ++i1)
			{
				ItemStack itemstack = tileentitytreetap.getStackInSlot(i1);

				if (itemstack != null)
				{
					float f = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
					float f1 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;
					float f2 = this.field_149922_a.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0)
					{
						int j1 = this.field_149922_a.nextInt(21) + 10;

						if (j1 > itemstack.stackSize)
						{
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						EntityItem entityitem = new EntityItem(p_149749_1_, p_149749_2_ + f, p_149749_3_ + f1, p_149749_4_ + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

						if (itemstack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}

						float f3 = 0.05F;
						entityitem.motionX = (float) this.field_149922_a.nextGaussian() * f3;
						entityitem.motionY = (float) this.field_149922_a.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float) this.field_149922_a.nextGaussian() * f3;
						p_149749_1_.spawnEntityInWorld(entityitem);
					}
				}
			}

			p_149749_1_.func_147453_f(p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_);
		}

		super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
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
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		return p_149691_1_ == 1 ? this.field_149923_M : this.field_149921_b;
	}

	public static int getDirectionFromMetadata(int p_149918_0_)
	{
		return p_149918_0_ & 7;
	}

	public static boolean func_149917_c(int p_149917_0_)
	{
		return (p_149917_0_ & 8) != 8;
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.field_149921_b = p_149651_1_.registerIcon(PolycraftMod.getAssetName("tree_tap_outside"));
		this.field_149923_M = p_149651_1_.registerIcon(PolycraftMod.getAssetName("tree_tap_top"));
		this.field_149924_N = p_149651_1_.registerIcon(PolycraftMod.getAssetName("tree_tap_inside"));
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getTreeTapIcon(String p_149916_0_)
	{
		final BlockTreeTap treeTap = (BlockTreeTap) PolycraftMod.getBlock("Tree Tap");
		return p_149916_0_.equals("tree_tap_outside") ? treeTap.field_149921_b : (p_149916_0_.equals("tree_tap_inside") ? treeTap.field_149924_N : null);
	}

	public static TileEntityTreeTap func_149920_e(IBlockAccess p_149920_0_, int p_149920_1_, int p_149920_2_, int p_149920_3_)
	{
		return (TileEntityTreeTap) p_149920_0_.getTileEntity(p_149920_1_, p_149920_2_, p_149920_3_);
	}

	/**
	 * Gets the icon name of the ItemBlock corresponding to this block. Used by tree taps.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName()
	{
		return PolycraftMod.getAssetName("tree_tap");
	}

	@Override
	public int getRenderType()
	{
		return PolycraftMod.renderTreeTapID;
	}
}