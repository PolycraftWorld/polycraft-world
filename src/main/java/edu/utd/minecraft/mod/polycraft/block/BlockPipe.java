package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.inventory.pump.Flowable;
import edu.utd.minecraft.mod.polycraft.render.TileEntityBlockPipe;

public class BlockPipe extends Block implements ITileEntityProvider, Flowable {

	private static final Logger logger = LogManager.getLogger();

	@SideOnly(Side.CLIENT)
	public IIcon iconFront;

	@SideOnly(Side.CLIENT)
	public IIcon iconSolid;

	protected final InternalObject config;

	public BlockPipe(final InternalObject config) {
		super(Material.iron);
		this.setHardness(2);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setStepSound(Block.soundTypeMetal);
		this.config = config;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var1) {
		return new TileEntityBlockPipe();
	}

	@Override
	public boolean hasTileEntity(int metadata)
	{
		return true;
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
	 */
	public void addCollisionBoxesToList(World worldObj, int xCoord, int yCoord, int zCoord, AxisAlignedBB mask, List list, Entity collidingEntity)
	{

		//TODO: Working and Tested 10.12.13 with given pipe radius; should make this a member variable maybe for different pipes
		float pipeRadius = 0.25F;

		//sets the middle part of the pipe
		this.setBlockBounds(0.5F - pipeRadius, 0.5F - pipeRadius, 0.5F - pipeRadius, 0.5F + pipeRadius, 0.5F + pipeRadius, 0.5F + pipeRadius);
		super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask, list, collidingEntity);

		float f = 0.125F;

		int directionOut = BlockPipe.getDirectionFromMetadata(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		TileEntityBlockPipe bte = (TileEntityBlockPipe) worldObj.getTileEntity(xCoord, yCoord, zCoord);

		//sets the other parts of the pipes

		if (directionOut == ForgeDirection.DOWN.ordinal() || bte.hasDirectionIn(ForgeDirection.DOWN))
		{
			this.setBlockBounds(0.5F - pipeRadius, 0.0F, 0.5F - pipeRadius, 0.5F + pipeRadius, 0.5F - pipeRadius, 0.5F + pipeRadius);
			super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask, list, collidingEntity);
		}
		if (directionOut == ForgeDirection.UP.ordinal() || bte.hasDirectionIn(ForgeDirection.UP))
		{
			this.setBlockBounds(0.5F - pipeRadius, 0.5F + pipeRadius, 0.5F - pipeRadius, 0.5F + pipeRadius, 1.0F, 0.5F + pipeRadius);
			super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask, list, collidingEntity);
		}
		if (directionOut == ForgeDirection.WEST.ordinal() || bte.hasDirectionIn(ForgeDirection.WEST))
		{
			this.setBlockBounds(0.0F, 0.5F - pipeRadius, 0.5F - pipeRadius, 0.5F - pipeRadius, 0.5F + pipeRadius, 0.5F + pipeRadius);
			super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask, list, collidingEntity);
		}
		if (directionOut == ForgeDirection.EAST.ordinal() || bte.hasDirectionIn(ForgeDirection.EAST))
		{
			this.setBlockBounds(0.5F + pipeRadius, 0.5F - pipeRadius, 0.5F - pipeRadius, 1.0F, 0.5F + pipeRadius, 0.5F + pipeRadius);
			super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask, list, collidingEntity);
		}
		if (directionOut == ForgeDirection.NORTH.ordinal() || bte.hasDirectionIn(ForgeDirection.NORTH))
		{
			this.setBlockBounds(0.5F - pipeRadius, 0.5F - pipeRadius, 0.0F, 0.5F + pipeRadius, 0.5F + pipeRadius, 0.5F - pipeRadius);
			super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask, list, collidingEntity);
		}
		if (directionOut == ForgeDirection.SOUTH.ordinal() || bte.hasDirectionIn(ForgeDirection.SOUTH))
		{
			this.setBlockBounds(0.5F - pipeRadius, 0.5F - pipeRadius, 0.5F + pipeRadius, 0.5F + pipeRadius, 0.5F + pipeRadius, 1.0F);
			super.addCollisionBoxesToList(worldObj, xCoord, yCoord, zCoord, mask, list, collidingEntity);
		}

		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void onBlockPlacedBy(World worldObj, int xCoord, int yCoord, int zCoord, EntityLivingBase player, ItemStack itemStack) {
		BlockHelper.setFacingMetadataFlowable(this, worldObj, xCoord, yCoord, zCoord, player, itemStack);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World worldObj, int xCoord, int yCoord, int zCoord, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		//if you are trying to click on a pump or inventory or clickable block behind the pipe, enable that

		//this is recursive so you can move through pipes until you turn or hit and inventory

		int playerLookingDirection = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		int playerUpDown = MathHelper.floor_double(player.rotationPitch);

		//First test if the player is looking up and down. 
		if (playerUpDown >= 45) //player is looking down
		{
			worldObj.getBlock(xCoord, yCoord - 1, zCoord).onBlockActivated(worldObj, xCoord, yCoord - 1, zCoord, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		if (playerUpDown <= -45) //player is looking up
		{
			worldObj.getBlock(xCoord, yCoord + 1, zCoord).onBlockActivated(worldObj, xCoord, yCoord + 1, zCoord, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}

		if (playerLookingDirection == 0) //SOUTH
		{
			worldObj.getBlock(xCoord, yCoord, zCoord + 1).onBlockActivated(worldObj, xCoord, yCoord, zCoord + 1, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		if (playerLookingDirection == 1) //WEST
		{
			worldObj.getBlock(xCoord - 1, yCoord, zCoord).onBlockActivated(worldObj, xCoord - 1, yCoord, zCoord, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		if (playerLookingDirection == 2) //NORTH
		{
			worldObj.getBlock(xCoord, yCoord, zCoord - 1).onBlockActivated(worldObj, xCoord, yCoord, zCoord - 1, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		if (playerLookingDirection == 3) //EAST
		{
			worldObj.getBlock(xCoord + 1, yCoord, zCoord).onBlockActivated(worldObj, xCoord + 1, yCoord, zCoord, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}

		//do nothing else upon right click TODO: for now...could display if this is a valid network    
		return true;

	}

	@Override
	public void breakBlock(World worldObj, int xCoord, int yCoord, int zCoord, Block block, int metaData)
	{
		super.breakBlock(worldObj, xCoord, yCoord, zCoord, block, metaData);

	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return PolycraftRegistry.getItem(this.config.itemName);

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
	public IIcon getIcon(int side, int metaData)
	{
		if (side == metaData)
			return this.iconFront;
		return this.iconSolid;
	}

	public static int getDirectionFromMetadata(int metaData)
	{
		return metaData & 7;
	}

	public static boolean isLargestMetaDataBitSet(int metaData)
	{
		return (metaData & 8) != 8;
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
	//    public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
	//    {
	//        return Container.calcRedstoneFromInventory(func_149920_e(p_149736_1_, p_149736_2_, p_149736_3_, p_149736_4_));
	//    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.iconFront = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_front")));
		this.iconSolid = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_solid")));
	}

	public static TileEntityBlockPipe getTileEntityBlockPipeAtXYZ(IBlockAccess blockAccess, int xCoord, int yCoord, int zCoord)
	{
		return (TileEntityBlockPipe) blockAccess.getTileEntity(xCoord, yCoord, zCoord);
	}

	/**
	 * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers and pipes.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName()
	{
		return PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(this.config.itemName));
	}

	@Override
	public int getRenderType() {

		return config.renderID; //http://www.minecraftforge.net/wiki/Multiple_Pass_Render_Blocks

	}

	public String getUnlocalizedName(int itemDamage) {
		// TODO Auto-generated method stub
		return this.config.gameID;
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

}