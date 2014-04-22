package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolycraftInventoryBlock extends BlockContainer {
	private final PolycraftInventory inventory;
	private final Random random = new Random();
	private static final Logger logger = LogManager.getLogger();

	@SideOnly(Side.CLIENT)
	private Map<BlockFace, IIcon> blockFaceIcons = Maps.newHashMap();
	
	@SideOnly(Side.CLIENT)
	private IIcon inventoryIcon;
	
	protected PolycraftInventoryBlock(final PolycraftInventory inventory) {
		super(inventory.getBlockMaterial());
		this.inventory = inventory;
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int var1, int var2, int var3) {
		return Item.getItemFromBlock(inventory.getBlock());
	}

	@Override
	public Item getItemDropped(int var1, Random random, int val2) {
		return Item.getItemFromBlock(inventory.getBlock());
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var1) {
		return inventory.createTileEntity(world);
	}

	/**
	 * Drops all items in the container into the world.
	 */
	private void dropAllItems(World world, PolycraftInventory tileEntity, int x, int y, int z) {
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
		for (InventoryBehavior behavior : inventory.getBehaviors()) {
			if (behavior.breakBlock(world, x, y, z, p_149749_5_, this.inventory)) {
				return;
			}
		}
		PolycraftInventory tileEntity = (PolycraftInventory)world.getTileEntity(x, y, z);

		if (tileEntity != null) {
			dropAllItems(world, tileEntity, x, y, z);
			// TODO: What is this method call?
			world.func_147453_f(x, y, z, p_149749_5_);
		}
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		for (InventoryBehavior behavior : inventory.getBehaviors()) {
			if (behavior.randomDisplayTick(world, x, y, z, random, inventory)) {
				return;
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int front) {
		// if the front is the top, it must be in the player inventory, so just render the front as side 3 (like furnace)
		if (front == 0 && side == 3) {
			return this.inventoryIcon;
		}
		
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
		// Register all icons; avoid dupes
		Map<String, IIcon> iconNameMap = Maps.newHashMap();
		if (inventory.getInventoryIcon() != null) {
			logger.info("Adding icon for asset " + inventory.getInventoryIcon());
			iconNameMap.put(inventory.getInventoryIcon(), register.registerIcon(inventory.getInventoryIcon()));
		}
		for (BlockFace face : EnumSet.allOf(BlockFace.class)) {
			String asset = inventory.getBlockFaceIcon(face);
			if (asset != null && !iconNameMap.containsKey(asset)) {
				logger.info("Adding icon for asset " + asset);
				iconNameMap.put(asset, register.registerIcon(asset));
			}
		}
		
		// Now set all the icons
		this.inventoryIcon = iconNameMap.get(inventory.getInventoryIcon());
		for (BlockFace face : EnumSet.allOf(BlockFace.class)) {			
			String asset = inventory.getBlockFaceIcon(face);
			if (asset != null) {
				if (iconNameMap.containsKey(asset)) {
					blockFaceIcons.put(face, iconNameMap.get(asset));
				} else {
					logger.warn("No icon for asset " + asset);
				}
			}
		}
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		for (InventoryBehavior behavior : inventory.getBehaviors()) {
			if (behavior.onBlockActivated(world, x, y, z, player, metadata, what, these, are, this.inventory)) {
				return true;
			}
		}
		
		// Default behavior: open GUI
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			TileEntity tileEntity = (TileEntity)world.getTileEntity(x, y, z);
			if (tileEntity != null) {
				player.openGui(PolycraftMod.instance, this.inventory.getGuiId(), world, x, y, z);
			}
			return true;
		}
		return false;
	}

}
