package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class BlockCollision extends Block {

	@SideOnly(Side.CLIENT)
	public IIcon iconFront;

	protected final InternalObject config;

	public BlockCollision(final InternalObject config) {
		super(Material.iron);
		this.config = config;
		// TODO Auto-generated constructor stub
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
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
	{
		return false;
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
	{
		return true;
	}

	@Override
	public boolean onBlockActivated(World worldObj, int xCoord, int yCoord, int zCoord, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		ForgeDirection dir = ForgeDirection.values()[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];

		if (dir == ForgeDirection.DOWN)
		{
			worldObj.getBlock(xCoord, yCoord - 1, zCoord).onBlockActivated(worldObj, xCoord, yCoord - 1, zCoord, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (dir == ForgeDirection.WEST)
		{
			worldObj.getBlock(xCoord + 1, yCoord, zCoord).onBlockActivated(worldObj, xCoord + 1, yCoord, zCoord, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (dir == ForgeDirection.EAST)
		{
			worldObj.getBlock(xCoord - 1, yCoord, zCoord).onBlockActivated(worldObj, xCoord - 1, yCoord, zCoord, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (dir == ForgeDirection.NORTH)
		{
			worldObj.getBlock(xCoord, yCoord, zCoord - 1).onBlockActivated(worldObj, xCoord, yCoord, zCoord - 1, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (dir == ForgeDirection.SOUTH)
		{
			worldObj.getBlock(xCoord, yCoord, zCoord + 1).onBlockActivated(worldObj, xCoord, yCoord, zCoord + 1, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		//TODO: throw some error here: misdefined inventory
		return false;

	}

	@Override
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
	{
		return false;
	}

	static public TileEntity findConnectedInventory(World worldObj, int xCoord, int yCoord, int zCoord)
	{
		ForgeDirection dir = ForgeDirection.values()[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
		TileEntity target = null;
		int xCoordNext = xCoord;
		int yCoordNext = yCoord;
		int zCoordNext = zCoord;

		if (dir == ForgeDirection.DOWN)
		{
			yCoordNext--;

		}
		else if (dir == ForgeDirection.WEST)
		{
			xCoordNext++;
		}
		else if (dir == ForgeDirection.EAST)
		{
			xCoordNext--;
		}
		else if (dir == ForgeDirection.NORTH)
		{
			zCoordNext--;
		}
		else if (dir == ForgeDirection.SOUTH)
		{
			zCoordNext++;
		}
		target = worldObj.getTileEntity(xCoordNext, yCoordNext, zCoordNext);
		if (target != null) {
			return target;
		}

		return findConnectedInventory(worldObj, xCoordNext, yCoordNext, zCoordNext);

	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		breakBlockRecurse(world, x, y, z, block, meta);
	}

	public void breakBlockRecurse(World world, int x, int y, int z, Block block, int meta)
	{
		//world.setBlockToAir(x, y, z);
		world.func_147480_a(x, y, z, false);
		Block neighbor;
		ForgeDirection dir;

		//get the specific neighbor that this block points to and break it if it is an Collision Block or the inventory

		dir = ForgeDirection.values()[meta & 7]; //meta = world.getBlockMetadata(x, y, z) right now

		if (dir == ForgeDirection.WEST)
		{
			neighbor = world.getBlock(x + 1, y, z); //follow the way it is pointing
			meta = world.getBlockMetadata(x + 1, y, z);
			if (!world.isRemote)
			{
				if (neighbor instanceof BlockCollision)
					((BlockCollision) neighbor).breakBlockRecurse(world, x + 1, y, z, neighbor, meta);
				if (neighbor instanceof PolycraftInventoryBlock)
					((PolycraftInventoryBlock) neighbor).breakBlockRecurse(world, x + 1, y, z, neighbor, meta, true);
			}
		}
		else if (dir == ForgeDirection.EAST)
		{
			neighbor = world.getBlock(x - 1, y, z); //follow the way it is pointing
			meta = world.getBlockMetadata(x - 1, y, z);
			if (!world.isRemote)
			{
				if (neighbor instanceof BlockCollision)
					((BlockCollision) neighbor).breakBlockRecurse(world, x - 1, y, z, neighbor, meta);
				if (neighbor instanceof PolycraftInventoryBlock)
					((PolycraftInventoryBlock) neighbor).breakBlockRecurse(world, x - 1, y, z, neighbor, meta, true);
			}
		}
		else if (dir == ForgeDirection.NORTH)
		{
			neighbor = world.getBlock(x, y, z - 1); //follow the way it is pointing
			meta = world.getBlockMetadata(x, y, z - 1);
			if (!world.isRemote)
			{
				if (neighbor instanceof BlockCollision)
					((BlockCollision) neighbor).breakBlockRecurse(world, x, y, z - 1, neighbor, meta);
				if (neighbor instanceof PolycraftInventoryBlock)
					((PolycraftInventoryBlock) neighbor).breakBlockRecurse(world, x, y, z - 1, neighbor, meta, true);
			}
		}
		else if (dir == ForgeDirection.SOUTH)
		{
			neighbor = world.getBlock(x, y, z + 1); //follow the way it is pointing
			meta = world.getBlockMetadata(x, y, z + 1);
			if (!world.isRemote)
			{
				if (neighbor instanceof BlockCollision)
					((BlockCollision) neighbor).breakBlockRecurse(world, x, y, z + 1, neighbor, meta);
				if (neighbor instanceof PolycraftInventoryBlock)
					((PolycraftInventoryBlock) neighbor).breakBlockRecurse(world, x, y, z + 1, neighbor, meta, true);
			}
		}
		else if (dir == ForgeDirection.DOWN)
		{
			neighbor = world.getBlock(x, y - 1, z); //follow the way it is pointing
			meta = world.getBlockMetadata(x, y - 1, z);
			if (!world.isRemote)
			{
				if (neighbor instanceof BlockCollision)
					((BlockCollision) neighbor).breakBlockRecurse(world, x, y - 1, z, neighbor, meta);
				if (neighbor instanceof PolycraftInventoryBlock)
					((PolycraftInventoryBlock) neighbor).breakBlockRecurse(world, x, y - 1, z, neighbor, meta, true);
			}
		}
		else if (dir == ForgeDirection.UP)
		{
			neighbor = world.getBlock(x, y + 1, z); //follow the way it is pointing
			meta = world.getBlockMetadata(x, y + 1, z);
			if (!world.isRemote)
			{
				if (neighbor instanceof BlockCollision)
					((BlockCollision) neighbor).breakBlockRecurse(world, x, y + 1, z, neighbor, meta);
				if (neighbor instanceof PolycraftInventoryBlock)
					((PolycraftInventoryBlock) neighbor).breakBlockRecurse(world, x, y + 1, z, neighbor, meta, true);
			}
		}

		//get each neighbor if it is a BlockCollision facing this block, then destroy it too. 
		//this eliminates the network going away from the inventory

		//neighbor = world.getBlock(x + 1, y, z);
		if ((neighbor = world.getBlock(x + 1, y, z)) instanceof BlockCollision)
			if ((dir = ForgeDirection.values()[world.getBlockMetadata(x + 1, y, z)]) == ForgeDirection.EAST)
				((BlockCollision) neighbor).breakBlockRecurse(world, x + 1, y, z, neighbor, dir.ordinal());

		if ((neighbor = world.getBlock(x - 1, y, z)) instanceof BlockCollision)
			if ((dir = ForgeDirection.values()[world.getBlockMetadata(x - 1, y, z)]) == ForgeDirection.WEST)
				((BlockCollision) neighbor).breakBlockRecurse(world, x - 1, y, z, neighbor, dir.ordinal());

		if ((neighbor = world.getBlock(x, y, z + 1)) instanceof BlockCollision)
			if ((dir = ForgeDirection.values()[world.getBlockMetadata(x, y, z + 1)]) == ForgeDirection.NORTH)
				((BlockCollision) neighbor).breakBlockRecurse(world, x, y, z + 1, neighbor, dir.ordinal());

		if ((neighbor = world.getBlock(x, y, z - 1)) instanceof BlockCollision)
			if ((dir = ForgeDirection.values()[world.getBlockMetadata(x, y, z - 1)]) == ForgeDirection.SOUTH)
				((BlockCollision) neighbor).breakBlockRecurse(world, x, y, z - 1, neighbor, dir.ordinal());

		if ((neighbor = world.getBlock(x, y + 1, z)) instanceof BlockCollision)
			if ((dir = ForgeDirection.values()[world.getBlockMetadata(x, y + 1, z)]) == ForgeDirection.DOWN)
				((BlockCollision) neighbor).breakBlockRecurse(world, x, y + 1, z, neighbor, dir.ordinal());

		if ((neighbor = world.getBlock(x, y - 1, z)) instanceof BlockCollision)
			if ((dir = ForgeDirection.values()[world.getBlockMetadata(x, y - 1, z)]) == ForgeDirection.UP)
				((BlockCollision) neighbor).breakBlockRecurse(world, x, y - 1, z, neighbor, dir.ordinal());

	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return null;

	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metaData)
	{
		return this.iconFront;

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.iconFront = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name)));
	}

}