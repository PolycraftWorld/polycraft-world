package edu.utd.minecraft.mod.polycraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;

public class BlockPolymerBrick extends Block { //implements ITileEntityProvider {

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
		this.helper = new BlockPolymerBrickHelper(Brick.source, width, length, -1, Brick.subBrick);

	}

	//	@Override
	//	public TileEntity createNewTileEntity(World var1, int var2) {
	//		return new TileEntityPolymerBrick();
	//	}

	//	@Override
	//	public int getRenderType() {
	//		return RenderIDs.PolymerBrickID;
	//	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	//	@Override
	//	public boolean renderAsNormalBlock()
	//	{
	//		return false;
	//	}
	//TODO: Walter to fix for 3D additions

	//
	//	@Override
	//	public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
	//	{
	//		return false;
	//	}
	//
	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */

	//TODO: Walter to fix for 3D additions

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {
		return helper.getIcon(side, colorIndex);
		//return this.icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		helper.registerBlockIcons(par1IconRegister);
		//icon = par1IconRegister.registerIcon(PolycraftMod.MODID.toLowerCase() + ":PolymerBrick");
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
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		//return null;
		//TODO: Check to make this implementation works
		if (this.Brick.subBrick != null)
		{
			return PolycraftRegistry.getItem(this.Brick.subBrick.itemName);
		}
		else
		{
			return PolycraftRegistry.getItem(this.Brick.itemName);
		}

	}

	//overriding this so we can add ntb stack compounds to the itemstack immediately so they can stack right when broken
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		return helper.getDrops(this, world, x, y, z, metadata, fortune);
	}

	//@Override
	public void onBlockPlacedBy(World worldObj, int xPos, int yPos, int zPos, EntityLivingBase player, ItemStack itemToPlace) {

		helper.onBlockPlacedBy(worldObj, xPos, yPos, zPos, player, itemToPlace);

		//		if (worldObj.getTileEntity(xPos, yPos, zPos) instanceof TileEntityPolymerBrick) {
		//			TileEntityPolymerBrick te = (TileEntityPolymerBrick) worldObj.getTileEntity(xPos, yPos, zPos);
		//			int direction = 0;
		//			int facing = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		//			if (facing == 0) {
		//				direction = ForgeDirection.NORTH.ordinal();
		//			} else if (facing == 1) {
		//				direction = ForgeDirection.EAST.ordinal();
		//			} else if (facing == 2) {
		//				direction = ForgeDirection.SOUTH.ordinal();
		//			} else if (facing == 3) {
		//				direction = ForgeDirection.WEST.ordinal();
		//			}
		//			te.setOrientation(direction);
		//		}
		boolean shiftPressed = false;
		boolean ctrlPressed = false;

		//if (!player.worldObj.isRemote && (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)))
		if (player.isSneaking())
		{
			//need to send a packet to the server informing them of this
			shiftPressed = true;
		}

		if (player.isSprinting())
		{
			ctrlPressed = true; //TODO: implement mirroring

		}

		Block block = worldObj.getBlock(xPos, yPos, zPos);
		int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
		boolean blockCanBePlaced = true;
		int notMirrored = 1;
		if (ctrlPressed)
		{
			//notMirrored = -1;
		}

		else if (meta == 0)
		{
			meta = (int) (Math.random() * 15 + 1);
			worldObj.setBlock(xPos, yPos, zPos, this, meta, 2);
		}

		for (int len = 0; len < this.length; len++)
		{
			for (int wid = 0; wid < this.width; wid++)
			{
				if ((len == 0) && (wid == 0)) // keeps the just placed block from triggering
					continue;

				if (((l == 0) && (!shiftPressed)) || ((l == 2) && (shiftPressed))) // facing south (+z) or facing north and holding shift
				{
					Block nextBlock = worldObj.getBlock(xPos - wid * notMirrored, yPos, zPos + len);
					if ((nextBlock != Blocks.air) &&
							(nextBlock != Blocks.water) &&
							(nextBlock != Blocks.deadbush) &&
							(nextBlock != Blocks.flowing_water) &&
							(nextBlock != Blocks.sapling) &&
							(nextBlock != Blocks.snow_layer) &&
							(nextBlock != Blocks.tallgrass) &&
							(nextBlock != Blocks.yellow_flower) &&
							(nextBlock != Blocks.red_flower) &&
							(nextBlock != Blocks.red_mushroom) &&
							(nextBlock != Blocks.brown_mushroom) &&
							(nextBlock != PolycraftMod.blockLight))
					{
						blockCanBePlaced = false;
						break;
					}
				}

				if (((l == 1) && (!shiftPressed)) || ((l == 3) && (shiftPressed))) // facing west (-x)
				{
					Block nextBlock = worldObj.getBlock(xPos - len, yPos, zPos - wid * notMirrored);
					if ((nextBlock != Blocks.air) &&
							(nextBlock != Blocks.water) &&
							(nextBlock != Blocks.deadbush) &&
							(nextBlock != Blocks.flowing_water) &&
							(nextBlock != Blocks.sapling) &&
							(nextBlock != Blocks.snow_layer) &&
							(nextBlock != Blocks.tallgrass) &&
							(nextBlock != Blocks.yellow_flower) &&
							(nextBlock != Blocks.red_flower) &&
							(nextBlock != Blocks.red_mushroom) &&
							(nextBlock != Blocks.brown_mushroom) &&
							(nextBlock != PolycraftMod.blockLight))
					{
						blockCanBePlaced = false;
						break;
					}

				}

				if (((l == 2) && (!shiftPressed)) || ((l == 0) && (shiftPressed))) // facing north (-z)
				{
					Block nextBlock = worldObj.getBlock(xPos + wid * notMirrored, yPos, zPos - len);
					if ((nextBlock != Blocks.air) &&
							(nextBlock != Blocks.water) &&
							(nextBlock != Blocks.deadbush) &&
							(nextBlock != Blocks.flowing_water) &&
							(nextBlock != Blocks.sapling) &&
							(nextBlock != Blocks.snow_layer) &&
							(nextBlock != Blocks.tallgrass) &&
							(nextBlock != Blocks.yellow_flower) &&
							(nextBlock != Blocks.red_flower) &&
							(nextBlock != Blocks.red_mushroom) &&
							(nextBlock != Blocks.brown_mushroom) &&
							(nextBlock != PolycraftMod.blockLight))
					{
						blockCanBePlaced = false;
						break;
					}
				}

				if (((l == 3) && (!shiftPressed)) || ((l == 1) && (shiftPressed))) // facing east (+x)
				{
					Block nextBlock = worldObj.getBlock(xPos + len, yPos, zPos + wid * notMirrored);
					if ((nextBlock != Blocks.air) &&
							(nextBlock != Blocks.water) &&
							(nextBlock != Blocks.deadbush) &&
							(nextBlock != Blocks.flowing_water) &&
							(nextBlock != Blocks.sapling) &&
							(nextBlock != Blocks.snow_layer) &&
							(nextBlock != Blocks.tallgrass) &&
							(nextBlock != Blocks.yellow_flower) &&
							(nextBlock != Blocks.red_flower) &&
							(nextBlock != Blocks.red_mushroom) &&
							(nextBlock != Blocks.brown_mushroom) &&
							(nextBlock != PolycraftMod.blockLight))
					{
						blockCanBePlaced = false;
						break;
					}

				}
			} //of of inner for Loop
		} //end of outer for Loop

		if (blockCanBePlaced)
		{

			for (int len = 0; len < this.length; len++)
			{
				for (int wid = 0; wid < this.width; wid++)
				{
					if (((l == 0) && (!shiftPressed)) || ((l == 2) && (shiftPressed))) // facing south (+z)
						worldObj.setBlock(xPos - wid * notMirrored, yPos, zPos + len, this, meta, 2);
					if (((l == 1) && (!shiftPressed)) || ((l == 3) && (shiftPressed))) // facing west (-x)
						worldObj.setBlock(xPos - len, yPos, zPos - wid * notMirrored, this, meta, 2);
					if (((l == 2) && (!shiftPressed)) || ((l == 0) && (shiftPressed))) // facing north (-z)
						worldObj.setBlock(xPos + wid * notMirrored, yPos, zPos - len, this, meta, 2);
					if (((l == 3) && (!shiftPressed)) || ((l == 1) && (shiftPressed))) // facing east (+x)
						worldObj.setBlock(xPos + len, yPos, zPos + wid * notMirrored, this, meta, 2);
				}
			}

		}
		else
		{
			worldObj.setBlock(xPos, yPos, zPos, Blocks.air);
			itemToPlace.stackSize += 1;

		}

	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return helper.damageDropped(p_149692_1_);
	}

	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack)
	{
		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
		super.dropBlockAsItem(world, x, y, z, itemstack);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
		return polycraftItemStack;

	}

	public String getUnlocalizedName(int colorIndex) {
		return Brick.gameID + "." + colorIndex;
	}
}