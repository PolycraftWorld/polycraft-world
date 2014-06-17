package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.RenderIDs;
import edu.utd.minecraft.mod.polycraft.client.TileEntityPolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;

public class BlockPolymerBrick extends BlockContainer {

	public final PolymerBrick Brick;
	private final BlockPolymerBrickHelper helper;
	private final int length, width;

	private IIcon icon;

	public BlockPolymerBrick(final PolymerBrick Brick, final int length, final int width) {
		super(Material.cloth);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.Brick = Brick;
		this.length = length;
		this.width = width;
		this.helper = new BlockPolymerBrickHelper(Brick.source, width, length, -1);

	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityPolymerBrick();
	}

	@Override
	public int getRenderType() {
		return RenderIDs.PolymerBrickID;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
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

	// /**
	// * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	// */
	// @Override
	// @SideOnly(Side.CLIENT)
	// public boolean shouldSideBeRendered(IBlockAccess blockAccess, int xPos, int yPos, int zPos, int side)
	// {
	// return side == 0 ? super.shouldSideBeRendered(blockAccess, xPos, yPos, zPos, side) : true;
	// }
	//
	// /**
	// * Updates the blocks bounds based on its current state. Args: world, x, y, z
	// */
	// @Override
	// public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	// {
	// boolean flag = this.canConnectWallTo(p_149719_1_, p_149719_2_, p_149719_3_, p_149719_4_ - 1);
	// boolean flag1 = this.canConnectWallTo(p_149719_1_, p_149719_2_, p_149719_3_, p_149719_4_ + 1);
	// boolean flag2 = this.canConnectWallTo(p_149719_1_, p_149719_2_ - 1, p_149719_3_, p_149719_4_);
	// boolean flag3 = this.canConnectWallTo(p_149719_1_, p_149719_2_ + 1, p_149719_3_, p_149719_4_);
	// float f = 0.25F;
	// float f1 = 0.75F;
	// float f2 = 0.25F;
	// float f3 = 0.75F;
	// float f4 = 1.0F;
	//
	// if (flag)
	// {
	// f2 = 0.0F;
	// }
	//
	// if (flag1)
	// {
	// f3 = 1.0F;
	// }
	//
	// if (flag2)
	// {
	// f = 0.0F;
	// }
	//
	// if (flag3)
	// {
	// f1 = 1.0F;
	// }
	//
	// if (flag && flag1 && !flag2 && !flag3)
	// {
	// f4 = 0.8125F;
	// f = 0.3125F;
	// f1 = 0.6875F;
	// }
	// else if (!flag && !flag1 && flag2 && flag3)
	// {
	// f4 = 0.8125F;
	// f2 = 0.3125F;
	// f3 = 0.6875F;
	// }
	//
	// this.setBlockBounds(f, 0.0F, f2, f1, f4, f3);
	// }

	// /**
	// * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been cleared to be reused)
	// */
	// @Override
	// public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldObj, int xPos, int yPos, int zPos)
	// {
	// this.setBlockBoundsBasedOnState(worldObj, xPos, yPos, zPos);
	// this.maxY = 1.5D;
	// return super.getCollisionBoundingBoxFromPool(worldObj, xPos, yPos, zPos);
	// }

	// /**
	// * Return whether an adjacent block can connect to a wall.
	// */
	// public boolean canConnectWallTo(IBlockAccess p_150091_1_, int p_150091_2_, int p_150091_3_, int p_150091_4_)
	// {
	// Block block = p_150091_1_.getBlock(p_150091_2_, p_150091_3_, p_150091_4_);
	// // return block != this && block != Blocks.fence_gate ? (block.blockMaterial.isOpaque() && block.renderAsNormalBlock() ? block.blockMaterial != Material.gourd : false) : true;
	// return block != this ? false : true;
	// }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {
		// return helper.getIcon(side, colorIndex);
		return this.icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		// helper.registerBlockIcons(p_149651_1_);
		icon = par1IconRegister.registerIcon(PolycraftMod.MODID.toLowerCase() + ":PolymerBrick");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		helper.getSubBlocks(p_149666_1_, p_149666_2_, p_149666_3_);
	}

	// @Override
	// public int getActiveBounceHeight() {
	// return Brick.bounceHeight;
	// }

	// @Override
	// public float getMomentumReturnedOnPassiveFall() {
	// return helper.getMomentumReturnedOnPassiveFall();
	// }

	@Override
	public void onBlockPlacedBy(World worldObj, int xPos, int yPos, int zPos, EntityLivingBase player, ItemStack itemToPlace) {

		// helper.onBlockPlacedBy(worldObj, xPos, yPos, zPos, player, itemToPlace);

		if (worldObj.getTileEntity(xPos, yPos, zPos) instanceof TileEntityPolymerBrick) {
			TileEntityPolymerBrick te = (TileEntityPolymerBrick) worldObj.getTileEntity(xPos, yPos, zPos);
			int direction = 0;
			int facing = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			if (facing == 0) {
				direction = ForgeDirection.NORTH.ordinal();
			} else if (facing == 1) {
				direction = ForgeDirection.EAST.ordinal();
			} else if (facing == 2) {
				direction = ForgeDirection.SOUTH.ordinal();
			} else if (facing == 3) {
				direction = ForgeDirection.WEST.ordinal();
			}
			te.setOrientation(direction);
		}

		// Block block = worldObj.getBlock(xPos, yPos, zPos);
		// int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		// int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
		// boolean blockCanBePlaced = true;
		// for (int len = 0; len < this.length; len++)
		// {
		// for (int wid = 0; wid < this.width; wid++)
		// {
		// if ((len == 0) && (wid == 0)) // keeps the just placed block from triggering
		// continue;
		//
		// if (l == 0) // facing south (+z)
		// {
		// if ((worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.air) &&
		// (worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
		// {
		// blockCanBePlaced = false;
		// break;
		// }
		// }
		//
		// if (l == 1) // facing west (-x)
		// {
		// if ((worldObj.getBlock(xPos - len, yPos, zPos - wid) != Blocks.air) &&
		// (worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
		// {
		// blockCanBePlaced = false;
		// break;
		// }
		//
		// }
		//
		// if (l == 2) // facing north (-z)
		// {
		// if ((worldObj.getBlock(xPos + wid, yPos, zPos - len) != Blocks.air) &&
		// (worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
		// {
		// blockCanBePlaced = false;
		// break;
		// }
		// }
		//
		// if (l == 3) // facing east (+x)
		// {
		// if ((worldObj.getBlock(xPos + len, yPos, zPos + wid) != Blocks.air) &&
		// (worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
		// {
		// blockCanBePlaced = false;
		// break;
		// }
		// }
		// }
		// }
		//
		// if (blockCanBePlaced)
		// {
		//
		// for (int len = 0; len < this.length; len++)
		// {
		// for (int wid = 0; wid < this.width; wid++)
		// {
		// if (l == 0) // facing south (+z)
		// worldObj.setBlock(xPos - wid, yPos, zPos + len, this, meta, 2);
		// if (l == 1) // facing west (-x)
		// worldObj.setBlock(xPos - len, yPos, zPos - wid, this, meta, 2);
		// if (l == 2) // facing north (-z)
		// worldObj.setBlock(xPos + wid, yPos, zPos - len, this, meta, 2);
		// if (l == 3) // facing east (+x)
		// worldObj.setBlock(xPos + len, yPos, zPos + wid, this, meta, 2);
		// }
		// }
		//
		// }
		// else
		// {
		// worldObj.setBlock(xPos, yPos, zPos, Blocks.air);
		// itemToPlace.stackSize += 1;
		//
		// }

	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return helper.damageDropped(p_149692_1_);
	}

	public String getUnlocalizedName(int colorIndex) {
		return Brick.gameID + "." + colorIndex;
	}
}