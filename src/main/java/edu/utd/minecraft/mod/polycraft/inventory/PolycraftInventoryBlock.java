package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.ObjModelLoader;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.block.BlockHelper;
import edu.utd.minecraft.mod.polycraft.config.Inventory;

public class PolycraftInventoryBlock<I extends PolycraftInventory> extends BlockContainer {

	private final Random random = new Random();
	private static final Logger logger = LogManager.getLogger();

	protected final Inventory config;
	public final Class tileEntityClass;

	public final Map<BlockFace, IIcon> blockFaceIcons = Maps.newHashMap();
	private IIcon inventoryIcon;

	public PolycraftInventoryBlock(final Inventory config, final Class tileEntityClass, final Material material, final float hardness) {
		super(material);
		this.setHardness(hardness);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.config = config;
		this.tileEntityClass = tileEntityClass;
	}

	public PolycraftInventoryBlock(final Inventory config, final Class tileEntityClass) {
		this(config, tileEntityClass, config.render3D ? Material.glass : Material.iron, 3.5F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var1) {
		try {
			return (TileEntity) tileEntityClass.newInstance();
		} catch (Exception e) {
			logger.error("Can't create an instance of your tile entity: " + e.getMessage());
		}
		return null;
	}

	public I getInventory(World world, int x, int y, int z) {
		return (I) world.getTileEntity(x, y, z);
	}

	/**
	 * Drops all items in the container into the world.
	 */
	private void dropAllItems(World world, I tileEntity, int x, int y, int z) {
		for (int i1 = 0; i1 < tileEntity.getSizeInventory(); ++i1) {
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
							world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

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
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		I tileEntity = getInventory(world, x, y, z);
		if (tileEntity != null)
		{
			for (InventoryBehavior behavior : tileEntity.getBehaviors()) {
				if (behavior.breakBlock(tileEntity, world, x, y, z, block)) {
					return;
				}
			}
		}

		final I inventory = (I) world.getTileEntity(x, y, z);
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
						EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

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

			world.func_147453_f(x, y, z, block);
		}

		//super.breakBlock(world, x, y, z, block, meta);

		world.removeTileEntity(x, y, z);
		world.func_147480_a(x, y, z, false); //this is destroy block
		world.setBlockToAir(x, y, z);

		//get each neighbor if it is a BlockCollision facing this block, then destroy it too. 
		Block neighbor;
		ForgeDirection dir;

		neighbor = world.getBlock(x + 1, y, z);
		dir = ForgeDirection.values()[world.getBlockMetadata(x + 1, y, z) & 7];

		if ((neighbor instanceof BlockCollision) && (dir == ForgeDirection.EAST))
			neighbor.breakBlock(world, x + 1, y, z, block, meta);

		neighbor = world.getBlock(x - 1, y, z);
		dir = ForgeDirection.values()[world.getBlockMetadata(x - 1, y, z) & 7];

		if ((neighbor instanceof BlockCollision) && (dir == ForgeDirection.WEST))
			neighbor.breakBlock(world, x - 1, y, z, block, meta);

		neighbor = world.getBlock(x, y, z + 1);
		dir = ForgeDirection.values()[world.getBlockMetadata(x, y, z + 1) & 7];

		if ((neighbor instanceof BlockCollision) && (dir == ForgeDirection.NORTH))
			neighbor.breakBlock(world, x, y, z + 1, block, meta);

		neighbor = world.getBlock(x, y, z - 1);
		dir = ForgeDirection.values()[world.getBlockMetadata(x, y, z - 1) & 7];

		if ((neighbor instanceof BlockCollision) && (dir == ForgeDirection.SOUTH))
			neighbor.breakBlock(world, x, y, z - 1, block, meta);

		neighbor = world.getBlock(x, y + 1, z);
		dir = ForgeDirection.values()[world.getBlockMetadata(x, y + 1, z) & 7];

		if ((neighbor instanceof BlockCollision) && (dir == ForgeDirection.DOWN))
			neighbor.breakBlock(world, x, y + 1, z, block, meta);

		neighbor = world.getBlock(x, y - 1, z);
		dir = ForgeDirection.values()[world.getBlockMetadata(x, y - 1, z) & 7];

		if ((neighbor instanceof BlockCollision) && (dir == ForgeDirection.UP)) //As implemented with inventories on the bottom, this should never happen
			neighbor.breakBlock(world, x, y - 1, z, block, meta);

	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		I tileEntity = getInventory(world, x, y, z);
		if (tileEntity != null)
			for (InventoryBehavior behavior : tileEntity.getBehaviors()) {
				if (behavior.randomDisplayTick(tileEntity, world, x, y, z, random)) {
					return;
				}
			}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int front) {
		// If front face is explicitly defined, display that.
		if (side == front && blockFaceIcons.containsKey(BlockFace.FRONT)) {
			return blockFaceIcons.get(BlockFace.FRONT);
		}

		// If the block's side is explicitly defined, show that icon.
		BlockFace face = BlockFace.getBlockFace(side);
		if (blockFaceIcons.containsKey(face)) {
			return blockFaceIcons.get(face);
		}

		// Otherwise show the default icon.
		return blockFaceIcons.get(BlockFace.DEFAULT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		if (config.inventoryAsset != null)
			this.inventoryIcon = register.registerIcon(PolycraftMod.getAssetName(config.inventoryAsset));
		for (BlockFace face : EnumSet.allOf(BlockFace.class)) {
			String asset = config.blockFaceAssets.get(face);
			if (asset != null)
				blockFaceIcons.put(face, register.registerIcon(PolycraftMod.getAssetName(asset)));
		}
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		I inventory = getInventory(world, x, y, z);
		for (InventoryBehavior behavior : inventory.getBehaviors()) {
			if (behavior.onBlockActivated(inventory, world, x, y, z, player, metadata, what, these, are)) {
				return true;
			}
		}

		// Default behavior: open GUI
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity != null) {
				player.openGui(PolycraftMod.instance, config.guiID, world, x, y, z);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getRenderType() {
		//if (this.config.render3D)
		//return -1;
		//else
		return config.renderID; //TODO: Walter
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		if (this.config.render3D)
			return false;
		else
			return true;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
	{
		if (this.config.render3D)
			return false;
		else
			return true;
	}

	public static class BasicRenderingHandler extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

		protected final Inventory config;
		private IModelCustom inventoryModel;
		public ResourceLocation objFile;
		public ResourceLocation textureFile;

		public BasicRenderingHandler(final Inventory config) {
			this.config = config;
			if (this.config.render3D)
			{
				this.objFile = new ResourceLocation(PolycraftMod.MODID, "textures/models/inventories/" + PolycraftMod.getFileSafeName(config.name) + ".obj");
				//this.inventoryModel = AdvancedModelLoader.loadModel(this.objFile);
				this.inventoryModel = new ObjModelLoader().loadInstance(this.objFile);
				this.textureFile = new ResourceLocation(PolycraftMod.MODID, "textures/models/inventories/" + PolycraftMod.getFileSafeName(config.name) + ".png");
			}
			else
			{
				this.objFile = null;
				this.inventoryModel = null;
				this.textureFile = null;
			}

		}

		@Override
		public int getRenderId() {
			return config.renderID;
			//TODO
		}

		@Override
		public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
			int meta = 3;
			Tessellator tessellator = Tessellator.instance;
			block.setBlockBoundsForItemRender();
			renderer.setRenderBoundsFromBlock(block);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
			tessellator.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

		@Override
		public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
			int direction = renderer.blockAccess.getBlockMetadata(x, y, z) & 3;
			if (direction > 0)
				renderer.uvRotateTop = direction - 1;
			else
				renderer.uvRotateTop = 3;
			boolean flag = renderer.renderStandardBlock(block, x, y, z);
			renderer.uvRotateTop = 0;
			return flag;
		}

		@Override
		public boolean shouldRender3DInInventory(int modelId) {
			return true;
		}

		@Override
		public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {

			if (config.render3D)
			{
				ForgeDirection direction = null;
				boolean rotated = false;
				short angle = 0;

				direction = ForgeDirection.values()[(tileEntity.getWorldObj().getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) & 7)];
				rotated = (tileEntity.getWorldObj().getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)) >> 3 == 1;

				// System.out.println(direction + "|" + angle);
				GL11.glPushMatrix();
				scaleTranslateRotate(x, y, z, direction, rotated);

				Minecraft.getMinecraft().renderEngine.bindTexture(this.textureFile);
				this.inventoryModel.renderAll();
				GL11.glPopMatrix();
			}
		}

		private void scaleTranslateRotate(double x, double y, double z, ForgeDirection orientation, boolean rotated) {

			if ((orientation == ForgeDirection.NORTH) && (!rotated)) {
				// System.out.println("North");
				GL11.glTranslated(x + 1F, y, z);
				GL11.glRotatef(-90, 1F, 0F, 0F); //z axis
				GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
				GL11.glRotatef(-90, 0F, 0F, 1F); //x axis

			}
			else if ((orientation == ForgeDirection.NORTH) && (rotated)) {
				// System.out.println("North");
				GL11.glTranslated(x, y, z + 1F);
				GL11.glRotatef(-90, 1F, 0F, 0F); //z axis
				GL11.glRotatef(-90, 0F, 1F, 0F); //y axis
				GL11.glRotatef(-90, 0F, 0F, 1F); //x axis
				GL11.glRotatef(180, 0F, 1F, 0F); //y axis
			}

			else if ((orientation == ForgeDirection.EAST) && (!rotated)) {
				// System.out.println("East");
				GL11.glTranslated(x + 1F, y, z + 1F);
				//GL11.glRotatef(0, 1F, 0F, 0F);
				GL11.glRotatef(180, 0F, 1F, 0F);
				//GL11.glRotatef(0, 0F, 0F, 1F);
			}

			else if ((orientation == ForgeDirection.EAST) && (rotated)) {
				// System.out.println("East");
				GL11.glTranslated(x, y, z);
				//GL11.glRotatef(0, 1F, 0F, 0F);
				GL11.glRotatef(180, 0F, 1F, 0F);
				//GL11.glRotatef(0, 0F, 0F, 1F);
				GL11.glRotatef(180, 0F, 1F, 0F); //y axis

			} else if ((orientation == ForgeDirection.SOUTH) && (!rotated)) {
				// System.out.println("South");
				GL11.glTranslated(x, y, z + 1F);
				GL11.glRotatef(-90, 1F, 0F, 0F);
				GL11.glRotatef(90, 0F, 1F, 0F);
				GL11.glRotatef(90, 0F, 0F, 1F);

			} else if ((orientation == ForgeDirection.SOUTH) && (rotated)) {
				// System.out.println("South");
				GL11.glTranslated(x + 1F, y, z);
				GL11.glRotatef(-90, 1F, 0F, 0F);
				GL11.glRotatef(90, 0F, 1F, 0F);
				GL11.glRotatef(90, 0F, 0F, 1F);
				GL11.glRotatef(180, 0F, 1F, 0F); //y axis

			} else if ((orientation == ForgeDirection.WEST) && (!rotated)) {
				// System.out.println("West");
				GL11.glTranslated(x, y, z);
				//GL11.glRotatef(0, 1F, 0F, 0F);
				//GL11.glRotatef(0, 0F, 1F, 0F);
				//GL11.glRotatef(0, 0F, 0F, 1F);
			} else if ((orientation == ForgeDirection.WEST) && (rotated)) {
				// System.out.println("West");
				GL11.glTranslated(x + 1F, y, z + 1F);
				//GL11.glRotatef(180, 1F, 0F, 0F);
				//GL11.glRotatef(0, 0F, 1F, 0F);
				//GL11.glRotatef(0, 0F, 0F, 1F);
				GL11.glRotatef(180, 0F, 1F, 0F); //y axis

			}
		}

	}

	public boolean canPlaceBlockWithoutInterference(Block nextBlock)
	{
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
			return false;
		else
			return true;

	}

	//@Override
	public void onBlockPlacedBy(World worldObj, int xPos, int yPos, int zPos, EntityLivingBase player, ItemStack itemToPlace) {

		//BlockHelper.setFacingMetadata4(this, p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);

		if (this.config.render3D)
		{

			int facing = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			if (worldObj.getTileEntity(xPos, yPos, zPos) instanceof TileEntity) {
				TileEntity te = (TileEntity) worldObj.getTileEntity(xPos, yPos, zPos);
				ForgeDirection playerFacingDir = null;
				if (facing == 0) {
					playerFacingDir = ForgeDirection.SOUTH; //Facing South
				} else if (facing == 1) {
					playerFacingDir = ForgeDirection.WEST; //Facing West
				} else if (facing == 2) {
					playerFacingDir = ForgeDirection.NORTH; //Facing North
				} else if (facing == 3) {
					playerFacingDir = ForgeDirection.EAST; //Facing East
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

				int meta = BlockHelper.setFacingFlippableMetadata4(this, worldObj, xPos, yPos, zPos, player, itemToPlace, shiftPressed);
				//boolean flipped = (meta >> 4) == 1;
				//int direction = meta & 7;

				Block block = worldObj.getBlock(xPos, yPos, zPos);
				//int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
				boolean blockCanBePlaced = true;
				int notMirrored = 1;
				if (ctrlPressed)
				{
					//notMirrored = -1;
				}

				//TODO: make sure the blocks on all sides of this are not BlockCollision blocks

				for (int len = 0; len < this.config.length; len++)
				{
					for (int wid = 0; wid < this.config.width; wid++)
					{
						if ((len == 0) && (wid == 0)) // keeps the just placed block from triggering
							continue;

						if (((playerFacingDir == ForgeDirection.SOUTH) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
						{
							Block nextBlock = worldObj.getBlock(xPos - wid * notMirrored, yPos, zPos + len);
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
						}

						if (((playerFacingDir == ForgeDirection.WEST) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.EAST) && (shiftPressed))) // facing west (-x)
						{
							Block nextBlock = worldObj.getBlock(xPos - len, yPos, zPos - wid * notMirrored);
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
						}

						if (((playerFacingDir == ForgeDirection.NORTH) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.SOUTH) && (shiftPressed))) // facing north (-z)
						{
							Block nextBlock = worldObj.getBlock(xPos + wid * notMirrored, yPos, zPos - len);
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;
						}

						if (((playerFacingDir == ForgeDirection.EAST) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.WEST) && (shiftPressed))) // facing east (+x)
						{
							Block nextBlock = worldObj.getBlock(xPos + len, yPos, zPos + wid * notMirrored);
							if (!(blockCanBePlaced = canPlaceBlockWithoutInterference(nextBlock))) //this is ok, setting boolean and testing it, do not change to ==
								break;

						}
					} //of of inner for Loop
				} //end of outer for Loop

				int collisionMeta = ForgeDirection.DOWN.ordinal();

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
										collisionMeta = ForgeDirection.DOWN.ordinal();
									}

									if (((playerFacingDir == ForgeDirection.SOUTH) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.NORTH) && (shiftPressed))) // facing south (+z) or facing north and holding shift
									{
										if (height == 0)
										{
											if (len != 0)
												collisionMeta = ForgeDirection.NORTH.ordinal();
											else if (wid != 0)
												collisionMeta = ForgeDirection.WEST.ordinal();
										}

										worldObj.setBlock(xPos - wid, yPos + height, zPos + len, PolycraftMod.blockCollision, collisionMeta, 2);

									}
									if (((playerFacingDir == ForgeDirection.WEST) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.EAST) && (shiftPressed))) // facing west (-x)
									{
										if (height == 0)
										{
											if (wid != 0)
												collisionMeta = ForgeDirection.SOUTH.ordinal();
											else if (len != 0)
												collisionMeta = ForgeDirection.WEST.ordinal();
										}
										worldObj.setBlock(xPos - len, yPos + height, zPos - wid, PolycraftMod.blockCollision, collisionMeta, 2);

									}
									if (((playerFacingDir == ForgeDirection.NORTH) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.SOUTH) && (shiftPressed))) // facing north (-z)
									{
										if (height == 0)
										{
											if (len != 0)
												collisionMeta = ForgeDirection.SOUTH.ordinal();
											else if (wid != 0)
												collisionMeta = ForgeDirection.EAST.ordinal();
										}
										worldObj.setBlock(xPos + wid, yPos + height, zPos - len, PolycraftMod.blockCollision, collisionMeta, 2);
									}
									if (((playerFacingDir == ForgeDirection.EAST) && (!shiftPressed)) || ((playerFacingDir == ForgeDirection.WEST) && (shiftPressed))) // facing east (+x)
									{
										if (height == 0)
										{
											if (wid != 0)
												collisionMeta = ForgeDirection.NORTH.ordinal();
											else if (len != 0)
												collisionMeta = ForgeDirection.EAST.ordinal();
										}
										worldObj.setBlock(xPos + len, yPos + height, zPos + wid, PolycraftMod.blockCollision, collisionMeta, 2);
									}
								}
							}
						}
					}
					//				if ((playerFacingDir == ForgeDirection.SOUTH) && (!shiftPressed)) // facing south (+z)
					//				{
					//					worldObj.setBlock(xPos - this.config.width + 1, yPos, zPos + this.config.length - 1, this, meta, 2);
					//				}
					//				if ((playerFacingDir == ForgeDirection.NORTH) && (!shiftPressed)) // facing north (-z)
					//				{
					//					worldObj.setBlock(xPos, yPos, zPos, this, meta, 2);
					//				}
					//				if ((playerFacingDir == ForgeDirection.WEST) && (!shiftPressed)) // facing west (-x)
					//				{
					//					worldObj.setBlock(xPos - this.config.length + 1, yPos, zPos, this, meta, 2);
					//				}
					//				if ((playerFacingDir == ForgeDirection.EAST) && (!shiftPressed)) // facing east (x) 
					//				{
					//					worldObj.setBlock(xPos, yPos, zPos + this.config.width - 1, this, meta, 2);
					//				}
					//
					//				if ((playerFacingDir == ForgeDirection.SOUTH) && (shiftPressed)) // facing south (+z) with shift
					//				{
					//					worldObj.setBlock(xPos, yPos, zPos, this, meta, 2);
					//				}
					//
					//				if ((playerFacingDir == ForgeDirection.NORTH) && (shiftPressed)) // facing north (-z) with shift
					//				{
					//					worldObj.setBlock(xPos - this.config.width + 1, yPos, zPos + this.config.length - 1, this, meta, 2);
					//				}
					//
					//				if ((playerFacingDir == ForgeDirection.WEST) && (shiftPressed)) // facing west (-x) with shift
					//				{
					//					worldObj.setBlock(xPos, yPos, zPos + this.config.width - 1, this, meta, 2);
					//				}
					//
					//				if ((playerFacingDir == ForgeDirection.EAST) && (shiftPressed)) // facing east (x) with shift
					//				{
					//					worldObj.setBlock(xPos - this.config.length + 1, yPos, zPos, this, meta, 2);
					//				}

					//				worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
					//				te.markDirty();

				}
				else
				{
					worldObj.setBlock(xPos, yPos, zPos, Blocks.air);
					itemToPlace.stackSize += 1;

				}
			}
		}
		super.onBlockPlacedBy(worldObj, xPos, yPos, zPos, player, itemToPlace);

	}

}
