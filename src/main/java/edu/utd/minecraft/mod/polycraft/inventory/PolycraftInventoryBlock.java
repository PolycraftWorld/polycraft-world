package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

//import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.block.BlockHelper;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonBlock;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonInventory;
import edu.utd.minecraft.mod.polycraft.inventory.oilderrick.OilDerrickBlock;
import edu.utd.minecraft.mod.polycraft.inventory.oilderrick.OilDerrickInventory;
import edu.utd.minecraft.mod.polycraft.inventory.textwall.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
//import net.minecraftforge.client.model.IModelCustom;
//import net.minecraftforge.client.model.obj.ObjModelLoader;

public class PolycraftInventoryBlock<I extends PolycraftInventory> extends BlockContainer {

	private final Random random = new Random();
	private static final Logger logger = LogManager.getLogger();

	public final Inventory config;
	public final Class tileEntityClass;

//	public final Map<BlockFace, IIcon> blockFaceIcons = Maps.newHashMap();
//	protected IIcon inventoryIcon;

	public PolycraftInventoryBlock(final Inventory config, final Class tileEntityClass, final Material material, final float hardness) {
		super(material);
		this.setHardness(hardness);
		this.setResistance(10.0F);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.config = config;
		this.tileEntityClass = tileEntityClass;
	}

	public PolycraftInventoryBlock(final Inventory config, final Class tileEntityClass) {
		this(config, tileEntityClass, config.render3D ? Material.glass : Material.iron, 1.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var1) {
		try {
			return (TileEntity) tileEntityClass.newInstance();
		} catch (Exception e) {
			logger.error("Can't create an instance of your tile entity: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public I getInventory(World world, BlockPos pos) {
		return (I) world.getTileEntity(pos);
	}

	public Vec3 getBlockCoords(int x, int y, int z, int meta, int[] offsets)
	{
		EnumFacing dir = EnumFacing.values()[meta & 7];
		boolean rotated = (meta >> 3) == 1;
		if ((dir == EnumFacing.NORTH) && (!rotated)) {
			return new Vec3(

					x - offsets[1], //x is width
					y + offsets[2],
					z + offsets[0]); //-z is length

		}
		else if ((dir == EnumFacing.NORTH) && (rotated)) {
			return new Vec3(
					x + offsets[1],
					y + offsets[2],
					z - offsets[0]);
		}

		else if ((dir == EnumFacing.EAST) && (!rotated)) {
			return new Vec3(
					x - offsets[0],
					y + offsets[2],
					z - offsets[1]);
		}

		else if ((dir == EnumFacing.EAST) && (rotated)) {
			return new Vec3(
					x + offsets[0],
					y + offsets[2],
					z + offsets[1]);

		} else if ((dir == EnumFacing.SOUTH) && (!rotated)) {
			return new Vec3(
					x + offsets[1],
					y + offsets[2],
					z - offsets[0]);

		} else if ((dir == EnumFacing.SOUTH) && (rotated)) {
			return new Vec3(
					x - offsets[1],
					y + offsets[2],
					z + offsets[0]);

		} else if ((dir == EnumFacing.WEST) && (!rotated)) {
			return new Vec3(
					x + offsets[0],
					y + offsets[2],
					z + offsets[1]);
		} else if ((dir == EnumFacing.WEST) && (rotated)) {

			return new Vec3(
					x - offsets[0],
					y + offsets[2],
					z - offsets[1]);
		}
		return new Vec3(
				x,
				y,
				z); //this should not happen: block should have valid metadata

	}

	public Vec3 getOutputBlockCoords(int x, int y, int z, int meta)
	{
		EnumFacing dir = EnumFacing.values()[meta & 7];
		boolean rotated = (meta >> 3) == 1;
		if ((dir == EnumFacing.NORTH) && (!rotated)) {
			return new Vec3(
					x - config.outputBlockOffset[1], //x is width
					y + config.outputBlockOffset[2], //y is height
					z + config.outputBlockOffset[0]); //-z is length
		}
		else if ((dir == EnumFacing.NORTH) && (rotated)) {
			return new Vec3(
					x + config.outputBlockOffset[1],
					y + config.outputBlockOffset[2],
					z - config.outputBlockOffset[0]);

		}

		else if ((dir == EnumFacing.EAST) && (!rotated)) {
			return new Vec3(
					x - config.outputBlockOffset[0],
					y + config.outputBlockOffset[2],
					z - config.outputBlockOffset[1]);
		}

		else if ((dir == EnumFacing.EAST) && (rotated)) {
			return new Vec3(
					x + config.outputBlockOffset[0],
					y + config.outputBlockOffset[2],
					z + config.outputBlockOffset[1]);

		} else if ((dir == EnumFacing.SOUTH) && (!rotated)) {
			return new Vec3(
					x + config.outputBlockOffset[1],
					y + config.outputBlockOffset[2],
					z - config.outputBlockOffset[0]);

		} else if ((dir == EnumFacing.SOUTH) && (rotated)) {
			return new Vec3(
					x - config.outputBlockOffset[1],
					y + config.outputBlockOffset[2],
					z + config.outputBlockOffset[0]);

		} else if ((dir == EnumFacing.WEST) && (!rotated)) {
			return new Vec3(
					x + config.outputBlockOffset[0],
					y + config.outputBlockOffset[2],
					z + config.outputBlockOffset[1]);
		} else if ((dir == EnumFacing.WEST) && (rotated)) {

			return new Vec3(
					x - config.outputBlockOffset[0],
					y + config.outputBlockOffset[2],
					z - config.outputBlockOffset[1]);
		}
		return new Vec3(x, y, z); //this should not happen: block should have metadata

	}

	/**
	 * Drops all items in the container into the world.
	 */
	private void dropAllItems(World world, I tileEntity, BlockPos pos) {
		//this.dropBlockAsItem(world, x, y, z, new ItemStack(this));
		int sizeNow = tileEntity.getSizeInventory(); //this is to prevent ticking memory crash
		for (int i1 = 0; i1 < sizeNow; ++i1) {
			ItemStack itemstack = tileEntity.getStackInSlot(i1);

			if (itemstack != null) {
				float f = this.random.nextFloat() * 0.8F + 0.1F;
				float f1 = this.random.nextFloat() * 0.8F + 0.1F;
				float f2 = this.random.nextFloat() * 0.8F + 0.1F;

				while (itemstack.stackSize > 0) {
					int j1 = this.random.nextInt(21) + 10;

					if (j1 > itemstack.stackSize) {
						j1 = itemstack.stackSize;
					}

					itemstack.stackSize -= j1;
					EntityItem entityitem = new EntityItem(
							world, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

					if (itemstack.hasTagCompound()) {
						entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
					}

					float f3 = 0.05F;
					entityitem.motionX = (float) this.random.nextGaussian() * f3;
					entityitem.motionY = (float) this.random.nextGaussian() * f3 + 0.2F;
					entityitem.motionZ = (float) this.random.nextGaussian() * f3;
					world.spawnEntityInWorld(entityitem);
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos blockPos, IBlockState state) {

		// Hotfix to OilDerrickBlock to prevent OilFields from being decremented when the OilDerrick failed to place. (PM-17)
		if (config.containerType == PolycraftContainerType.OIL_DERRICK)
		{
			PolycraftInventory inv = this.getInventory(world, blockPos);
			if (inv instanceof OilDerrickInventory && ((OilDerrickInventory) inv).isPlaced())
			{ 
				final Block oreBlock = world.getBlockState(blockPos.down()).getBlock();
				int metaOre = world.getBlockState(blockPos.down()).getBlock().getMetaFromState(world.getBlockState(blockPos.down()));
				if (oreBlock != null && oreBlock instanceof BlockOre) {
					if (config.params != null)
					{
						if (config.params.get(2) != null)
						{
							if (((BlockOre) oreBlock).ore.gameID.equals(Ore.registry.get(config.params.get(2)).gameID)) {
								if (metaOre > 0)
									world.setBlockState(blockPos.down(), oreBlock.getStateFromMeta(metaOre - 1),2); // remove the rest of the oil in this meta level
								else
									world.setBlockState(blockPos.down(), oreBlock.getStateFromMeta(0),2); // no more oil
								if (metaOre > 1)
									metaOre--;
								// System.out.println("Oil decremented to " + metaOre);
							}
						}
					}
				}
			}
		}

		final I inventory = (I) world.getTileEntity(blockPos);
		if (inventory != null)
		{
			//only call out inventory here if there are no collision blocks associated with the 3D inventory
			//if there are any collision blocks, the recursive function will handle this cleanup...

			if (config.containerType == PolycraftContainerType.PLASTIC_CHEST)
			{
				this.dropAllItems(world, inventory, blockPos);
				world.removeTileEntity(blockPos);
			}
			else if (config.containerType == PolycraftContainerType.PORTAL_CHEST)
			{
				this.dropAllItems(world, inventory, blockPos);
				world.removeTileEntity(blockPos);
			}
			else if (config.containerType == PolycraftContainerType.FLOODLIGHT)
			{
				this.dropAllItems(world, inventory, blockPos);
				world.removeTileEntity(blockPos);
			}
			else if (config.containerType == PolycraftContainerType.SPOTLIGHT)
			{
				this.dropAllItems(world, inventory, blockPos);
				world.removeTileEntity(blockPos);
			}
			else if (config.containerType == PolycraftContainerType.GASLAMP)
			{
				this.dropAllItems(world, inventory, blockPos);
				world.removeTileEntity(blockPos);
			}
			else if (config.containerType == PolycraftContainerType.PUMP)
			{
				this.dropAllItems(world, inventory, blockPos);
				world.removeTileEntity(blockPos);
			}
			else if (config.containerType == PolycraftContainerType.TREE_TAP)
			{
				this.dropAllItems(world, inventory, blockPos);
				world.removeTileEntity(blockPos);
			}

			else if (this.config.render3D)
			{
				breakBlockRecurse(world, blockPos, state, false);
			}
		}

		super.breakBlock(world, blockPos, state);
		//		}
	}

	public void breakBlockRecurse(World world, BlockPos blockPos, IBlockState state, boolean destroyBlock) {

		I tileEntity = getInventory(world, blockPos);
		if (tileEntity != null)
		{
			for (InventoryBehavior behavior : tileEntity.getBehaviors()) {
				if (behavior.breakBlock(tileEntity, world, blockPos, state)) {
					return;
				}
			}
		}

		final I inventory = (I) world.getTileEntity(blockPos);
		if (inventory != null) {
			for (int i1 = 0; i1 < inventory.getSizeInventory(); ++i1) {
				final ItemStack itemstack = inventory.getStackInSlot(i1);

				if (itemstack != null) {
					float f = random.nextFloat() * 0.8F + 0.1F;
					float f1 = random.nextFloat() * 0.8F + 0.1F;
					float f2 = random.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {
						int j1 = random.nextInt(21) + 10;

						if (j1 > itemstack.stackSize) {
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						EntityItem entityitem = new EntityItem(world, blockPos.getX() + f, blockPos.getY() + f1, blockPos.getZ() + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

						if (itemstack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}

						float f3 = 0.05F;
						entityitem.motionX = (float) random.nextGaussian() * f3;
						entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float) random.nextGaussian() * f3;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}

			//was this in 1.7.10 ->	world.func_147453_f(x, y, z, block);
			world.updateComparatorOutputLevel(blockPos, state.getBlock());
		}

		if (destroyBlock)
		{
			if (!world.isRemote)
			{
				if (getInventory(world, blockPos) != null)
				{
					this.dropAllItems(world, inventory, blockPos);
					world.removeTileEntity(blockPos);
					this.dropBlockAsItem(world, blockPos, state, 0);
				}
				world.destroyBlock(blockPos, false); //this is destroy block
				world.setBlockToAir(blockPos);
			}
		}

		//get each neighbor if it is a BlockCollision facing this block, then destroy it too. 
		Block neighbor;
		EnumFacing dir;

		neighbor = world.getBlockState(blockPos.west()).getBlock();
		if (neighbor instanceof BlockCollision)
		{
			dir = (EnumFacing) world.getBlockState(blockPos.west()).getProperties().get(BlockCollision.FACING);
			if (dir == EnumFacing.WEST)
				((BlockCollision) neighbor).breakBlockRecurse(world, blockPos.west(), state);
		}

		neighbor = world.getBlockState(blockPos.east()).getBlock();
		if (neighbor instanceof BlockCollision)
		{
			dir = (EnumFacing) world.getBlockState(blockPos.east()).getProperties().get(BlockCollision.FACING);
			if (dir == EnumFacing.EAST)
				((BlockCollision) neighbor).breakBlockRecurse(world, blockPos.east(), state);
		}

		neighbor = world.getBlockState(blockPos.north()).getBlock();
		if (neighbor instanceof BlockCollision)
		{
			dir = (EnumFacing) world.getBlockState(blockPos.north()).getProperties().get(BlockCollision.FACING);
			if (dir == EnumFacing.NORTH)
				((BlockCollision) neighbor).breakBlockRecurse(world,blockPos.north(), state);
		}

		neighbor = world.getBlockState(blockPos.south()).getBlock();
		if (neighbor instanceof BlockCollision)
		{
			dir = (EnumFacing) world.getBlockState(blockPos.south()).getProperties().get(BlockCollision.FACING);
			if (dir == EnumFacing.SOUTH)
				((BlockCollision) neighbor).breakBlockRecurse(world, blockPos.south(), state);
		}

		neighbor = world.getBlockState(blockPos.down()).getBlock();
		if (neighbor instanceof BlockCollision)
		{
			dir = (EnumFacing) world.getBlockState(blockPos.down()).getProperties().get(BlockCollision.FACING);
			if (dir == EnumFacing.DOWN)
				((BlockCollision) neighbor).breakBlockRecurse(world, blockPos.down(), state);
		}

		neighbor = world.getBlockState(blockPos.up()).getBlock();
		if (neighbor instanceof BlockCollision)
		{
			dir = (EnumFacing) world.getBlockState(blockPos.up()).getProperties().get(BlockCollision.FACING);
			if (dir == EnumFacing.UP) //As implemented with inventories on the bottom, this should never happen
				((BlockCollision) neighbor).breakBlockRecurse(world, blockPos.up(), state);
		}

	}

//	@Override
//	public void dropBlockAsItem(World world, BlockPos blockPos, IBlockState state, int fortune)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(itemstack); TODO: removed in 1.8, not sure if we actually need it - SGoss
//		super.dropBlockAsItem(world, blockPos, state, fortune);
//	}

//	@Override
//	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos blockPos, EntityPlayer player)
//	{
//		ItemStack polycraftItemStack = super.getPickBlock(target, world, blockPos, player);
//		//PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack); TODO: removed in 1.8, not sure if we actually need it - SGoss
//		return polycraftItemStack;
//
//	}
//
//	@Override
//	protected ItemStack createStackedBlock(IBlockState state)
//	{
//		ItemStack polycraftItemStack = super.createStackedBlock(state);
//		//PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack); TODO: removed in 1.8, not sure if we actually need it - SGoss
//		return polycraftItemStack;
//	}

	@Override
	public void randomDisplayTick(World world, BlockPos blockPos, IBlockState state, Random random) {
		I tileEntity = getInventory(world, blockPos);
		if (tileEntity != null)
			for (InventoryBehavior behavior : tileEntity.getBehaviors()) {
				if (behavior.randomDisplayTick(tileEntity, world, blockPos, state, random)) {
					return;
				}
			}
	}

//	@Override	Removed in 1.8
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int front) {
//		// If front face is explicitly defined, display that.
//		if (side == front && blockFaceIcons.containsKey(BlockFace.FRONT)) {
//			return blockFaceIcons.get(BlockFace.FRONT);
//		}
//
//
//		// If the block's side is explicitly defined, show that icon.
//		BlockFace face = BlockFace.getBlockFace(side);
//		if (blockFaceIcons.containsKey(face)) {
//			return blockFaceIcons.get(face);
//		}
//
//		// Otherwise show the default icon.
//		return blockFaceIcons.get(BlockFace.DEFAULT);
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister register) {
//		if (config.inventoryAsset != null)
//			this.inventoryIcon = register.registerIcon(PolycraftMod.getAssetName(config.inventoryAsset));
//		for (BlockFace face : EnumSet.allOf(BlockFace.class)) {
//			String asset = config.blockFaceAssets.get(face);
//			if (asset != null)
//				blockFaceIcons.put(face, register.registerIcon(PolycraftMod.getAssetName(asset)));
//		}
//	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		I inventory = getInventory(world, blockPos);
		for (InventoryBehavior behavior : inventory.getBehaviors()) {
			if (behavior.onBlockActivated(inventory, world, blockPos, state, player, hitX, hitY, hitZ)) {
				return true;
			}
		}

		// Default behavior: open GUI
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			TileEntity tileEntity = (TileEntity) world.getTileEntity(blockPos);
			if (tileEntity != null) {
				player.openGui(PolycraftMod.instance, config.guiID, world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
			}
			return true;
		}
		return false;
	}

	
	 /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
	@Override
	public int getRenderType() {
		//if (this.config.render3D)
		//return -1;
		//else
		//return config.renderID; //TODO: Walter
		return 3;
	}

	@Override
    public boolean isOpaqueCube() { return false; }

    @Override
    public boolean isFullCube() { return false; }

    @Override
    public boolean isVisuallyOpaque() { return false; }

	//0 width length and height box so no wireframe rendered.
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return AxisAlignedBB.fromBounds((double) par2, (double) par3, (double) par4, (double) par2, (double) par3, (double) par4);
	}

	@Override
	public boolean isNormalCube()
	{
		if (this.config.render3D)
			return false;
		else
			return true;
	}
	
	@SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer(){
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
	{
		if (this.config.render3D)
			return false;
		else
			return true;
	}
	


//	Removed during 1.8 update 
//  	public static class BasicRenderingHandler extends TileEntitySpecialRenderer //implements ISimpleBlockRenderingHandler
// 
//	{
//
//		protected final Inventory config;
//		//protected IModelCustom inventoryModel;
//		public ResourceLocation objFile;
//		public ResourceLocation textureFile;
//
//		public BasicRenderingHandler(final Inventory config) {
//			this.config = config;
//			if (this.config.render3D)
//			{
//				this.objFile = new ResourceLocation(PolycraftMod.MODID, "textures/models/inventories/" + PolycraftMod.getFileSafeName(config.name) + ".obj");
//				//this.inventoryModel = AdvancedModelLoader.loadModel(this.objFile);
//				//this.inventoryModel = new ObjModelLoader().loadInstance(this.objFile);
//				this.textureFile = new ResourceLocation(PolycraftMod.MODID, "textures/models/inventories/" + PolycraftMod.getFileSafeName(config.name) + ".png");
//			}
//			else
//			{
//				this.objFile = null;
//				//this.inventoryModel = null;
//				this.textureFile = null;
//			}
//
//		}
//
////		@Override
////		public int getRenderId() {
////			return config.renderID;
////		}
//		@Override
//		@SideOnly(Side.CLIENT)
//		public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
//			int meta = 3;
//
//			block.setBlockBoundsForItemRender();
//			renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
//			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
//			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
//
//			if (this.config.render3D)
//			{
//
//				if (config.containerType == PolycraftContainerType.DISTILLATION_COLUMN)
//				{
//					GL11.glScalef(0.25F, 0.125F, 0.25F);
//					GL11.glTranslatef(4F, -4.7F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.FLOODLIGHT)
//				{
//					GL11.glScalef(1.5F, 1.5F, 1.5F);
//					GL11.glTranslatef(.75F, -.75F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.GASLAMP)
//				{
//					GL11.glScalef(1.5F, 1.5F, 1.5F);
//					GL11.glTranslatef(.75F, -.75F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.CHEMICAL_PROCESSOR)
//				{
//					GL11.glScalef(.33F, .33F, .33F);
//					GL11.glTranslatef(.4F, 0.4F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.CONDENSER)
//				{
//					GL11.glScalef(1.2F, 1.2F, 1.2F);
//					GL11.glTranslatef(.8F, 0.25F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.TERRITORY_FLAG)
//				{
//					GL11.glScalef(0.05F, 0.05F, 0.05F);
//					GL11.glTranslatef(-11.1F, 1.1F, -10.1F);
//				}
//
//				else if (config.containerType == PolycraftContainerType.COMPUTER)
//				{
//					GL11.glScalef(0.6F, 0.6F, 0.6F);
//					GL11.glTranslatef(0.25F, 0F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.HOSPITAL_GENERATOR)
//				{
//					GL11.glScalef(0.6F, 0.6F, 0.6F);
//					GL11.glTranslatef(0.25F, 0F, 0F);
//				}
//
//
//				else if (config.containerType == PolycraftContainerType.SOLAR_ARRAY)
//				{
//					GL11.glScalef(0.125F, 0.125F, 0.125F);
//				}
//				else if (config.containerType == PolycraftContainerType.OIL_DERRICK)
//				{
//					GL11.glScalef(0.125F, 0.125F, 0.125F);
//				}
//				else if (config.containerType == PolycraftContainerType.INDUSTRIAL_OVEN)
//				{
//					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
//					GL11.glScalef(0.35F, 0.35F, 0.35F);
//					GL11.glTranslatef(-3.5F, -2.5F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.INJECTION_MOLDER)
//				{
//					GL11.glScalef(0.35F, 0.35F, 0.35F);
//					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
//					GL11.glTranslatef(-5.25F, -2.25F, 0F);
//
//				}
//				else if (config.containerType == PolycraftContainerType.EXTRUDER)
//				{
//					GL11.glScalef(0.35F, 0.35F, 0.35F);
//					GL11.glTranslatef(.25F, 1.0F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.MACHINING_MILL)
//				{
//					GL11.glScalef(0.6F, 0.6F, 0.6F);
//					GL11.glTranslatef(0.25F, 0F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.POLYCRAFTING_TABLE)
//				{
//					GL11.glScalef(0.6F, 0.6F, 0.6F);
//					GL11.glTranslatef(0.8F, 0F, 2F);
//				}
//				else if (config.containerType == PolycraftContainerType.TRADING_HOUSE)
//				{
//					GL11.glScalef(0.4F, 0.4F, 0.4F);
//					GL11.glTranslatef(0.25F, .4F, .1F);
//				}
//				else if (config.containerType == PolycraftContainerType.CONTACT_PRINTER)
//				{
//					GL11.glScalef(0.4F, 0.4F, 0.4F);
//					GL11.glTranslatef(0.25F, .4F, .1F);
//				}
//				else if (config.containerType == PolycraftContainerType.MASK_WRITER)
//				{
//					GL11.glScalef(0.4F, 0.4F, 0.4F);
//					GL11.glTranslatef(.25F, .4F, .1F);
//				}
//				else if (config.containerType == PolycraftContainerType.PRINTING_PRESS)
//				{
//					GL11.glScalef(0.4F, 0.4F, 0.4F);
//					GL11.glTranslatef(0.25F, .4F, .1F);
//				}
//
//				else if (config.containerType == PolycraftContainerType.MEROX_TREATMENT_UNIT)
//				{
//					GL11.glScalef(0.25F, 0.25F, 0.25F);
//					GL11.glTranslatef(2F, -1F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.STEAM_CRACKER)
//				{
//					GL11.glScalef(0.24F, 0.24F, 0.24F);
//
//				}
//				else if (config.containerType == PolycraftContainerType.TEXT_WALL)
//				{
//					GL11.glScalef(.16F, .24F, .16F);
//					GL11.glTranslatef(2F, -.7F, 3F);
//
//				}
//				else if (config.containerType == PolycraftContainerType.FLUORESCENT_LAMP)
//				{
//					// Scale GL11 accordingly to future fluorescent lamp model.
//					GL11.glScalef(1.2F, 1.2F, 1.2F);
//					GL11.glTranslatef(.8F, 0.25F, 0F);
//				}
//				else if (config.containerType == PolycraftContainerType.CANNON)
//				{
//					GL11.glScalef(0.25F, 0.25F, 0.25F);
//					GL11.glTranslatef(1.5F, 0F, 1.5F);
//					GL11.glRotatef(315F, 0F, 1F, 0F);
//				}
//
//				Minecraft.getMinecraft().renderEngine.bindTexture(this.textureFile);
//				//this.inventoryModel.renderAll();
//			}
//			else
//			{	
////				Tessellator tessellator = Tessellator.instance;
////				tessellator.startDrawingQuads();
////				tessellator.setNormal(0.0F, -1.0F, 0.0F);
////				renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
////				tessellator.draw();
////				tessellator.startDrawingQuads();
////				tessellator.setNormal(0.0F, 1.0F, 0.0F);
////				renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
////				tessellator.draw();
////				tessellator.startDrawingQuads();
////				tessellator.setNormal(0.0F, 0.0F, -1.0F);
////				renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
////				tessellator.draw();
////				tessellator.startDrawingQuads();
////				tessellator.setNormal(0.0F, 0.0F, 1.0F);
////				renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
////				tessellator.draw();
////				tessellator.startDrawingQuads();
////				tessellator.setNormal(-1.0F, 0.0F, 0.0F);
////				renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
////				tessellator.draw();
////				tessellator.startDrawingQuads();
////				tessellator.setNormal(1.0F, 0.0F, 0.0F);
////				renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
////				tessellator.draw();
//			}
//
//			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
//		}
//
//		@Override
//		@SideOnly(Side.CLIENT)
//		public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
//			int direction = renderer.blockAccess.getBlockMetadata(x, y, z) & 3;
//			if (direction > 0)
//				renderer.uvRotateTop = direction - 1;
//			else
//				renderer.uvRotateTop = 3;
//			boolean flag = renderer.renderStandardBlock(block, x, y, z);
//			renderer.uvRotateTop = 0;
//			return flag;
//		}
//
//		@Override
//		public boolean shouldRender3DInInventory(int modelId) {
//			return true;
//		}*/
//		
//		@Override
//		@SideOnly(Side.CLIENT)
//		public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick, int destroyStage) {
//
//			if (config.render3D)
//			{
//				if(config.containerType == PolycraftContainerType.CANNON)
//				{
//					EnumFacing direction = EnumFacing.WEST;
//					boolean rotated = false;
//					short angle = 0;
//	
//					//direction = ForgeDirection.values()[(tileEntity.getWorldObj().getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) & 7)];
//					rotated = tileEntity.getBlockMetadata() >> 3 == 1;
//					
//					GL11.glPushMatrix();
//					GL11.glDisable(GL11.GL_CULL_FACE);
//					
//					scaleTranslateRotate(x, y, z, direction, false);
//				}
//				else
//				{
//					EnumFacing direction = null;
//					boolean rotated = false;
//					short angle = 0;
//	
//					direction = EnumFacing.getFront(tileEntity.getBlockMetadata() & 7);
//					rotated = tileEntity.getBlockMetadata() >> 3 == 1;
//	
//					// System.out.println(direction + "|" + angle);
//					GL11.glPushMatrix();
//					GL11.glDisable(GL11.GL_CULL_FACE);
//	
//					scaleTranslateRotate(x, y, z, direction, rotated);
//				}
//				
//				if (config.containerType == PolycraftContainerType.DISTILLATION_COLUMN)
//				{
//					GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
//				}
//				else if (config.containerType == PolycraftContainerType.FLOODLIGHT)
//				{
//					GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
//				}
//				else if (config.containerType == PolycraftContainerType.GASLAMP)
//				{
//					GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
//				}
//				else if (config.containerType == PolycraftContainerType.TEXT_WALL)
//				{
//					GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
//					GL11.glTranslated(4F,0,0); //y axis
//				}
//				else if (config.containerType == PolycraftContainerType.POLYCRAFTING_TABLE)
//				{
//					GL11.glRotatef(180, 0F, 1F, 0F); //y axis
//					GL11.glTranslated(-1F,0F,0F); //y axis
//				}
//				else if (config.containerType == PolycraftContainerType.MASK_WRITER)
//				{
//					//GL11.glRotatef(-90, 0F, 1F, 0F); //z axis
//					//GL11.glTranslated(0F, 0F, -1F);
//				}
//				else if (config.containerType == PolycraftContainerType.CONDENSER)
//				{
//					GL11.glTranslated(1F, 0, 0);
//				}
//				else if (config.containerType == PolycraftContainerType.TERRITORY_FLAG)
//				{
//					GL11.glScalef(.2F, .2F, .2F);
//					GL11.glTranslated(-22.5F, 20F, -35F);
//					GL11.glRotatef(270, 0F, 0F, 1F);
//				}
//				else if (config.containerType == PolycraftContainerType.OIL_DERRICK)
//				{
//					GL11.glRotatef(180, 0F, 1F, 0F); //y axis
//					GL11.glTranslated(0, 0, -2F);
//				}
//				else if (config.containerType == PolycraftContainerType.INDUSTRIAL_OVEN)
//				{
//					GL11.glRotatef(180, 0F, 1F, 0F); //y axis
//					GL11.glTranslated(0, 0, -3F);
//				}
//				else if (config.containerType == PolycraftContainerType.FLUORESCENT_LAMP)
//				{
//					// Modify fluorescent lamp tile entity code when new model is made.
//					GL11.glRotatef(180f, 1f, 0, 0);
//					GL11.glRotatef(90f, 0, 1f, 0);
//					GL11.glTranslated(1, -1, 0);
//				}
//				else if (config.containerType == PolycraftContainerType.CANNON)
//				{
//					CannonInventory cannon = (CannonInventory)tileEntity;
//					
//					GL11.glScalef(.2F, .2F, .2F);
//					GL11.glTranslated(2.4763F, -.027F, 2.5F);
//					GL11.glRotatef(((float) cannon.theta)%360,0F, 1F, 0F);
//					GL11.glRotatef(((float) cannon.phi)%360,0F, 0F, 1F);
//					
//				}
//
//				Minecraft.getMinecraft().renderEngine.bindTexture(this.textureFile);
//				//this.inventoryModel.renderAll();
//
//				GL11.glEnable(GL11.GL_CULL_FACE);
//				GL11.glPopMatrix();
//			}
//		}
//
//		protected void scaleTranslateRotate(double x, double y, double z, EnumFacing orientation, boolean rotated) {
//
//			if ((orientation == EnumFacing.NORTH) && (!rotated)) {
//				// System.out.println("North");
//				GL11.glTranslated(x + 1F, y, z);
//				GL11.glRotatef(-90, 1F, 0F, 0F); //z axis
//				GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
//				GL11.glRotatef(-90, 0F, 0F, 1F); //x axis
//
//			}
//			else if ((orientation == EnumFacing.NORTH) && (rotated)) {
//				// System.out.println("North");
//				GL11.glTranslated(x, y, z + 1F);
//				GL11.glRotatef(-90, 1F, 0F, 0F); //z axis
//				GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
//				GL11.glRotatef(-90, 0F, 0F, 1F); //x axis
//				GL11.glRotatef(180, 0F, 1F, 0F); //y axis
//			}
//
//			else if ((orientation == EnumFacing.EAST) && (!rotated)) {
//				// System.out.println("East");
//				GL11.glTranslated(x + 1F, y, z + 1F);
//				//GL11.glRotatef(0, 1F, 0F, 0F);
//				GL11.glRotatef(180, 0F, 1F, 0F);
//				//GL11.glRotatef(0, 0F, 0F, 1F);
//			}
//
//			else if ((orientation == EnumFacing.EAST) && (rotated)) {
//				// System.out.println("East");
//				GL11.glTranslated(x, y, z);
//				//GL11.glRotatef(0, 1F, 0F, 0F);
//				GL11.glRotatef(180, 0F, 1F, 0F);
//				//GL11.glRotatef(0, 0F, 0F, 1F);
//				GL11.glRotatef(180, 0F, 1F, 0F); //y axis
//
//			} else if ((orientation == EnumFacing.SOUTH) && (!rotated)) {
//				// System.out.println("South");
//				GL11.glTranslated(x, y, z + 1F);
//				GL11.glRotatef(-90, 1F, 0F, 0F);
//				GL11.glRotatef(90, 0F, 1F, 0F);
//				GL11.glRotatef(90, 0F, 0F, 1F);
//
//			} else if ((orientation == EnumFacing.SOUTH) && (rotated)) {
//				// System.out.println("South");
//				GL11.glTranslated(x + 1F, y, z);
//				GL11.glRotatef(-90, 1F, 0F, 0F);
//				GL11.glRotatef(90, 0F, 1F, 0F);
//				GL11.glRotatef(90, 0F, 0F, 1F);
//				GL11.glRotatef(180, 0F, 1F, 0F); //y axis
//
//			} else if ((orientation == EnumFacing.WEST) && (!rotated)) {
//				// System.out.println("West");
//				GL11.glTranslated(x, y, z);
//				//GL11.glRotatef(0, 1F, 0F, 0F);
//				//GL11.glRotatef(0, 0F, 1F, 0F);
//				//GL11.glRotatef(0, 0F, 0F, 1F);
//			} else if ((orientation == EnumFacing.WEST) && (rotated)) {
//				// System.out.println("West");
//				GL11.glTranslated(x + 1F, y, z + 1F);
//				//GL11.glRotatef(180, 1F, 0F, 0F);
//				//GL11.glRotatef(0, 0F, 1F, 0F);
//				//GL11.glRotatef(0, 0F, 0F, 1F);
//				GL11.glRotatef(180, 0F, 1F, 0F); //y axis
//
//			}
//		}
//
//	}

	public boolean canPlaceBlockWithoutInterference(Block nextBlock)
	{
		if ((nextBlock != Blocks.air) && //TODO: add a check for chests b/c we dont want to replace them
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
				(nextBlock != PolycraftMod.blockPureAir) &&
				(nextBlock != PolycraftMod.blockLight))
			return false;
		else
			return true;

	}

	@Override
	public void onBlockPlacedBy(World worldObj, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemToPlace) {

		//BlockHelper.setFacingMetadata4(this, p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);

		if (this.config.render3D)
		{
			int facing = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			if (worldObj.getTileEntity(pos) instanceof TileEntity) {
				TileEntity te = worldObj.getTileEntity(pos);
				//worldObj.getTileEntity(xPos, yPos, zPos).getRenderBoundingBox().setBounds(te.xCoord - 20, te.yCoord, te.zCoord - 20, te.xCoord + 20, te.yCoord + 20, te.zCoord + 20);

				EnumFacing playerFacingDir = null;
				if (facing == 0) {
					playerFacingDir = EnumFacing.SOUTH; //Facing South
				} else if (facing == 1) {
					playerFacingDir = EnumFacing.WEST; //Facing West
				} else if (facing == 2) {
					playerFacingDir = EnumFacing.NORTH; //Facing North
				} else if (facing == 3) {
					playerFacingDir = EnumFacing.EAST; //Facing East
				}

				boolean shiftPressed = false;
				boolean ctrlPressed = false;

				//if (!player.worldObj.isRemote && (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)))
				if (player.isSneaking())
				{
					//TODO: implement rotating 180
					//TODO: respect shift press in the metaData value of this block
					shiftPressed = true;

				}

				if (player.isSprinting())
				{
					//ctrlPressed = true; //TODO: implement mirroring
				}

				//int meta = BlockHelper.setKnownFacingMetadata4(this, worldObj, pos, playerFacingDir, shiftPressed);

				//int meta = BlockHelper.setFacingFlippableMetadata4(this, worldObj, xPos, yPos, zPos, player, itemToPlace, shiftPressed);
				//boolean flipped = (meta >> 4) == 1;
				//int direction = meta & 7;

				//Block block = worldObj.getBlock(xPos, yPos, zPos);
				//int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
				boolean blockCanBePlaced = true;
				int notMirrored = 1;
				if (ctrlPressed)
				{
					//notMirrored = -1;
				}

				for (int len = 0; len < this.config.length; len++)
				{
					for (int wid = 0; wid < this.config.width; wid++)
					{
						for (int height = 0; height < this.config.height; height++)
						{
							if ((len == 0) && (wid == 0) && (height == 0)) // keeps the just placed block from triggering
								continue;

							if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
							{
								Block nextBlock = worldObj.getBlockState(pos.add(- wid * notMirrored,height, len)).getBlock();
								if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
									break;
							}

							if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
							{
								Block nextBlock = worldObj.getBlockState(pos.add(- len, height, - wid * notMirrored)).getBlock();
								if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
									break;
							}

							if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
							{
								Block nextBlock = worldObj.getBlockState(pos.add(wid * notMirrored, height, - len)).getBlock();
								if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
									break;
							}

							if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
							{
								Block nextBlock = worldObj.getBlockState(pos.add(len, height, wid * notMirrored)).getBlock();
								if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
									break;
							}
						}
						if (!blockCanBePlaced)
							break;
					} //of of inner for Loop
					if (!blockCanBePlaced)
						break;
				} //end of outer for Loop
				//test explicit blocks we decorate around the inventories added to make sure the inventory can be placed
				while (blockCanBePlaced)
				{
					if (config.containerType == PolycraftContainerType.DISTILLATION_COLUMN)
					{
						if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
						{
							Block nextBlock = worldObj.getBlockState(pos.add(1, 0, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(-1, 2, -1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(-2, 2, -1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(2, 2, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(-1, 4, 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(-2, 4, 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(-2, 4, 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;

						}
						if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(0, 0, 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1,  + 2,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1,  + 2,  - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(0, + 2, - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 2, + 4, - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 2, + 4, - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1, + 4, - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
						}
						if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(- 1, 0, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1, + 2, + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2, + 2, + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2, + 2, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1, + 4, - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2, + 4, - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2, + 4, - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;

						}
						if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(0, 0, - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1, + 2, + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1, + 2, + 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(0, + 2, + 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2, + 4, + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2, + 4, + 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1, + 4, + 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
						}

					}
					else if (config.containerType == PolycraftContainerType.MACHINING_MILL)
					{
						if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
						{
							Block nextBlock = worldObj.getBlockState(pos.add(- 1, 1, - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1, + 2, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
						}
						if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(+ 1, + 1, - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(0, 2, - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
						}
						if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(+ 1, + 1, 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1, 2, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;

						}
						if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(- 1, 1, 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(0, 2, 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
						}

					}
					else if (config.containerType == PolycraftContainerType.EXTRUDER)
					{

						if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
						{
							Block nextBlock = worldObj.getBlockState(pos.add(0, 0, 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(0,  + 1,  + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1, 0,  + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1,  + 1,  + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 2, 0,  + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 2,  + 1,  + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;

						}
						if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(- 1, 0, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1,  + 1, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1, 0,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1,  + 1,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1, 0,  - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(- 1,  + 1,  - 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
						}
						if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(0, 0,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(0,  + 1,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1, 0,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1,  + 1,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2, 0,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 2,  + 1,  - 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;

						}
						if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
						{
							Block nextBlock = worldObj.getBlockState(pos.add(+ 1, 0, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1,  + 1, 0)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1, 0,  + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1,  + 1,  + 1)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1, 0,  + 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
							nextBlock = worldObj.getBlockState(pos.add(+ 1,  + 1,  + 2)).getBlock();
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock)))
								break;
						}

					}
					break;
				}

				int collisionMeta = EnumFacing.DOWN.ordinal();

				if (blockCanBePlaced)
				{
					for (int len = 0; len < this.config.length; len++)
					{
						for (int wid = 0; wid < this.config.width; wid++)
						{
							for (int height = 0; height < this.config.height; height++)
							{
								if (!((len == 0) && (wid == 0) && (height == 0)))
								{
									if (height != 0)
									{
										collisionMeta = EnumFacing.DOWN.ordinal();
									}

									if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
									{
										if (height == 0)
										{
											if (len != 0)
												collisionMeta = EnumFacing.NORTH.ordinal();
											else if (wid != 0)
												collisionMeta = EnumFacing.EAST.ordinal();
										}

										worldObj.setBlockState(pos.add(wid, height, len), PolycraftMod.blockCollision.getStateFromMeta(collisionMeta), 2);

									}
									if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
									{
										if (height == 0)
										{
											if (wid != 0)
												collisionMeta = EnumFacing.SOUTH.ordinal();
											else if (len != 0)
												collisionMeta = EnumFacing.EAST.ordinal();
										}
										worldObj.setBlockState(pos.add(-len, height, -wid), PolycraftMod.blockCollision.getStateFromMeta(collisionMeta), 2);

									}
									if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
									{
										if (height == 0)
										{
											if (len != 0)
												collisionMeta = EnumFacing.SOUTH.ordinal();
											else if (wid != 0)
												collisionMeta = EnumFacing.WEST.ordinal();
										}
										worldObj.setBlockState(pos.add(wid, height, -len), PolycraftMod.blockCollision.getStateFromMeta(collisionMeta), 2);
									}
									if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
									{
										if (height == 0)
										{
											if (wid != 0)
												collisionMeta = EnumFacing.NORTH.ordinal();
											else if (len != 0)
												collisionMeta = EnumFacing.WEST.ordinal();
										}
										worldObj.setBlockState(pos.add(len, height, wid), PolycraftMod.blockCollision.getStateFromMeta(collisionMeta), 2);
									}
								}
							}
						}
					}

					//add custom collision blocks: FIXME: dependent on release 1.1.7, will change if inventories change...
					if (config.containerType == PolycraftContainerType.DISTILLATION_COLUMN)
					{
						if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
						{

							worldObj.setBlockState(pos.add(+ 1, 0, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 2,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 2,  + 2,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 2,  + 2, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);

							worldObj.setBlockState(pos.add(- 1,  + 4,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 2,  + 4,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 2,  + 4,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);

						}
						if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
						{

							worldObj.setBlockState(pos.add(0, 0,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 2,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 2,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(0,  + 2,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);

							worldObj.setBlockState(pos.add(- 2,  + 4,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 2,  + 4,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 4,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);

						}
						if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
						{
							worldObj.setBlockState(pos.add(- 1, 0, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 2,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 2,  + 2,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 2,  + 2, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);

							worldObj.setBlockState(pos.add(+ 1,  + 4,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 2,  + 4,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 2,  + 4,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
						}
						if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
						{
							worldObj.setBlockState(pos.add(0, 0,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 2,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 2,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(0,  + 2,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);

							worldObj.setBlockState(pos.add(+ 2,  + 4,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 2,  + 4,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 4,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
						}

					}
					else if (config.containerType == PolycraftContainerType.MACHINING_MILL)
					{
						if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
						{

							worldObj.setBlockState(pos.add(- 1,  + 1,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 2, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

						}
						if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
						{
							worldObj.setBlockState(pos.add(+ 1,  + 1,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(0,  + 2,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

						}
						if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
						{
							worldObj.setBlockState(pos.add(+ 1,  + 1,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 2, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

						}
						if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
						{
							worldObj.setBlockState(pos.add(- 1,  + 1,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(0,  + 2,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

						}

					}

					else if (config.containerType == PolycraftContainerType.EXTRUDER)
					{

						if (((playerFacingDir == EnumFacing.SOUTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
						{

							worldObj.setBlockState(pos.add(0, 0,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(0,  + 1,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(- 1, 0,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 1,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(- 2, 0,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.NORTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 2,  + 1,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

						}
						if (((playerFacingDir == EnumFacing.WEST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.EAST) && (shiftPressed))) // facing west (-x)
						{

							worldObj.setBlockState(pos.add(- 1, 0, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 1, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(- 1, 0,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 1,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(- 1, 0,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.EAST.ordinal()), 2);
							worldObj.setBlockState(pos.add(- 1,  + 1,  - 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

						}
						if (((playerFacingDir == EnumFacing.NORTH) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.SOUTH) && (shiftPressed))) // facing north (-z)
						{
							worldObj.setBlockState(pos.add(0, 0,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(0,  + 1,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(+ 1, 0,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 1,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(+ 2, 0,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.SOUTH.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 2,  + 1,  - 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);
						}
						if (((playerFacingDir == EnumFacing.EAST) && (!shiftPressed)) || ((playerFacingDir == EnumFacing.WEST) && (shiftPressed))) // facing east (+x)
						{
							worldObj.setBlockState(pos.add(+ 1, 0, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 1, 0), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(+ 1, 0,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 1,  + 1), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);

							worldObj.setBlockState(pos.add(+ 1, 0,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.WEST.ordinal()), 2);
							worldObj.setBlockState(pos.add(+ 1,  + 1,  + 2), PolycraftMod.blockCollision.getStateFromMeta(EnumFacing.DOWN.ordinal()), 2);
						}
					}
				}
				else //block cannot be placed
				{
					// TODO: Hotfix to OilDerrickBlock to prevent OilFields from being decremented when the OilDerrick failed to place. (PM-17)
					if (this instanceof OilDerrickBlock)
						((OilDerrickInventory) this.getInventory(worldObj, pos)).setPlaced(false);
					worldObj.setBlockToAir(pos);
					itemToPlace.stackSize += 1;
				}
			}
		}
		else
		{
			BlockHelper.setFacingMetadata4(this, worldObj,pos, player, itemToPlace);
		}
		super.onBlockPlacedBy(worldObj, pos, state, player, itemToPlace);
	}

	
}
