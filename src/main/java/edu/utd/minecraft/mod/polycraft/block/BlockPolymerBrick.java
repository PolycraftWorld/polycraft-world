package edu.utd.minecraft.mod.polycraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper.EnumColor;
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;

public class BlockPolymerBrick extends Block { //implements ITileEntityProvider {

	public final PolymerBrick Brick;
	private final BlockPolymerBrickHelper helper;
	private final int length, width;
	
	public static final PropertyEnum PROPERTYCOLOR = PropertyEnum.create("color", EnumColor.class);

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

//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int colorIndex) {
//		return helper.getIcon(side, colorIndex);
//		//return this.icon;
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister par1IconRegister) {
//		helper.registerBlockIcons(par1IconRegister);
//		//icon = par1IconRegister.registerIcon(PolycraftMod.MODID.toLowerCase() + ":PolymerBrick");
//	}

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

	/**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(PROPERTYCOLOR, EnumColor.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumColor)state.getValue(PROPERTYCOLOR)).getMetadata();
    }
	
	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
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
	public List<ItemStack> getDrops(IBlockAccess access, BlockPos blockPos, IBlockState state, int fortune)
	{
		return helper.getDrops(this, blockPos, state, fortune);
	}
	
	 // when the block is placed, set the appropriate facing direction based on which way the player is looking
    // the color of block is contained in meta, it corresponds to the values we used for getSubBlocks
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
      EnumColor color = EnumColor.byMetadata(meta);

      return this.getDefaultState().withProperty(PROPERTYCOLOR, color);
    }
	

	@Override
	public void onBlockPlacedBy(World worldObj, BlockPos blockPos, IBlockState state, EntityLivingBase player, ItemStack itemToPlace) {

		helper.onBlockPlacedBy(worldObj, blockPos, state, player, itemToPlace);

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

		Block block = worldObj.getBlockState(blockPos).getBlock();
		int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		int meta = this.getMetaFromState(worldObj.getBlockState(blockPos));
		boolean blockCanBePlaced = true;
		int notMirrored = 1;
		if (ctrlPressed)
		{
			//notMirrored = -1;
		}

		else if (meta == 0)
		{
			meta = (int) (Math.random() * 15 + 1);
			worldObj.setBlockState(blockPos, this.getStateFromMeta(meta),3);
		}

		for (int len = 0; len < this.length; len++)
		{
			for (int wid = 0; wid < this.width; wid++)
			{
				if ((len == 0) && (wid == 0)) // keeps the just placed block from triggering
					continue;

				if (((l == 0) && (!shiftPressed)) || ((l == 2) && (shiftPressed))) // facing south (+z) or facing north and holding shift
				{
					Block nextBlock = worldObj.getBlockState(blockPos.add(-wid * notMirrored,0, len)).getBlock();
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
					Block nextBlock = worldObj.getBlockState(blockPos.add(-len, 0, -wid * notMirrored)).getBlock();
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
					Block nextBlock = worldObj.getBlockState(blockPos.add(wid * notMirrored, 0, -len)).getBlock();
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
					Block nextBlock = worldObj.getBlockState(blockPos.add(len, 0, wid * notMirrored)).getBlock();
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
						worldObj.setBlockState(blockPos.add(-wid * notMirrored, 0, len), this.getStateFromMeta(meta), 2);
					if (((l == 1) && (!shiftPressed)) || ((l == 3) && (shiftPressed))) // facing west (-x)
						worldObj.setBlockState(blockPos.add(-len, 0, -wid *notMirrored), this.getStateFromMeta(meta), 2);
					if (((l == 2) && (!shiftPressed)) || ((l == 0) && (shiftPressed))) // facing north (-z)
						worldObj.setBlockState(blockPos.add(wid * notMirrored, 0, -len), this.getStateFromMeta(meta), 2);
					if (((l == 3) && (!shiftPressed)) || ((l == 1) && (shiftPressed))) // facing east (+x)
						worldObj.setBlockState(blockPos.add(len, 0, wid * notMirrored), this.getStateFromMeta(meta), 2);
				}
			}

		}
		else
		{
			worldObj.setBlockToAir(blockPos);
			itemToPlace.stackSize += 1;

		}

	}

	 protected BlockState createBlockState()
	    {
	        return new BlockState(this, new IProperty[] {PROPERTYCOLOR});
	    }
	
	 // this function returns the correct item type corresponding to the colour of our block;
    // i.e. when a sign is broken, it will drop the correct item.  Ignores Facing, because we get the same item
    //   no matter which way the block is facing
    @Override
    public int damageDropped(IBlockState state)
    {
      EnumColor enumColor = (EnumColor)state.getValue(PROPERTYCOLOR);
      return enumColor.getMetadata();
    }

//	@Override
//	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
//		super.dropBlockAsItem(world, x, y, z, itemstack);
//	}
//
//	@Override
//	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
//	{
//		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
//		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
//		return polycraftItemStack;
//
//	}

	public String getUnlocalizedName(int colorIndex) {
		return super.getUnlocalizedName();
	}
}