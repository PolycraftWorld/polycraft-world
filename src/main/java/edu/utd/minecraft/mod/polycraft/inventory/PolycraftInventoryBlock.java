package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
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
		this(config, tileEntityClass, Material.iron, 3.5F);
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
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
		I tileEntity = getInventory(world, x, y, z);
		for (InventoryBehavior behavior : tileEntity.getBehaviors()) {
			if (behavior.breakBlock(tileEntity, world, x, y, z, p_149749_5_)) {
				return;
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

			world.func_147453_f(x, y, z, p_149749_5_);
		}

		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
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
		return config.renderID;
	}

	public static class BasicRenderingHandler implements ISimpleBlockRenderingHandler {

		protected final Inventory config;

		public BasicRenderingHandler(final Inventory config) {
			this.config = config;
		}

		@Override
		public int getRenderId() {
			return config.renderID;
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
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		BlockHelper.setFacingMetadata4(this, p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);
	}
}
