package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockChemicalProcessor extends BlockContainer {
	private final Random chemicalProcessorRand = new Random();
	private final boolean isActive;
	private static boolean keepChemicalProcessorInventory;
	@SideOnly(Side.CLIENT)
	private IIcon iconTop;
	@SideOnly(Side.CLIENT)
	private IIcon iconFront;

	public BlockChemicalProcessor(boolean isActive) {
		super(Material.rock);
		this.isActive = isActive;
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setStepSound(Block.soundTypeStone);
		if (!isActive)
			setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return Item.getItemFromBlock(PolycraftMod.blockChemicalProcessor);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_) {
		super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
		this.func_149930_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
	}

	private void func_149930_e(World p_149930_1_, int p_149930_2_, int p_149930_3_, int p_149930_4_) {
		if (!p_149930_1_.isRemote) {
			Block block = p_149930_1_.getBlock(p_149930_2_, p_149930_3_, p_149930_4_ - 1);
			Block block1 = p_149930_1_.getBlock(p_149930_2_, p_149930_3_, p_149930_4_ + 1);
			Block block2 = p_149930_1_.getBlock(p_149930_2_ - 1, p_149930_3_, p_149930_4_);
			Block block3 = p_149930_1_.getBlock(p_149930_2_ + 1, p_149930_3_, p_149930_4_);
			byte b0 = 3;

			if (block.func_149730_j() && !block1.func_149730_j()) {
				b0 = 3;
			}

			if (block1.func_149730_j() && !block.func_149730_j()) {
				b0 = 2;
			}

			if (block2.func_149730_j() && !block3.func_149730_j()) {
				b0 = 5;
			}

			if (block3.func_149730_j() && !block2.func_149730_j()) {
				b0 = 4;
			}

			p_149930_1_.setBlockMetadataWithNotify(p_149930_2_, p_149930_3_, p_149930_4_, b0, 2);
		}
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int front) {
		// if the front is the top, it must be in the player inventory, so just render the front as side 3 (like furnace)
		if (front == 0 && side == 3)
			return this.iconFront;
		if (side == 0 || side == 1)
			return this.iconTop;
		if (side != front)
			return this.blockIcon;
		return this.iconFront;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		this.blockIcon = p_149651_1_.registerIcon(PolycraftMod.getAssetName("chemical_processor_side"));
		this.iconFront = p_149651_1_.registerIcon(PolycraftMod.getAssetName(this.isActive ? "chemical_processor_front_on" : "chemical_processor_front_off"));
		this.iconTop = p_149651_1_.registerIcon(PolycraftMod.getAssetName("chemical_processor_top"));
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			TileEntityChemicalProcessor tileentitychemicalprocessor = (TileEntityChemicalProcessor) world.getTileEntity(x, y, z);
			if (tileentitychemicalprocessor != null) {
				player.openGui(PolycraftMod.instance, PolycraftMod.guiChemicalProcessorID, world, x, y, z);
			}

			return true;
		}
		return false;
	}

	/**
	 * Update which block the chemical processor is using depending on whether or not it is burning
	 */
	public static void updateChemicalProcessorBlockState(boolean p_149931_0_, World p_149931_1_, int p_149931_2_, int p_149931_3_, int p_149931_4_) {
		int l = p_149931_1_.getBlockMetadata(p_149931_2_, p_149931_3_, p_149931_4_);
		TileEntity tileentity = p_149931_1_.getTileEntity(p_149931_2_, p_149931_3_, p_149931_4_);
		keepChemicalProcessorInventory = true;

		if (p_149931_0_) {
			p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, PolycraftMod.blockChemicalProcessorActive);
		} else {
			p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, PolycraftMod.blockChemicalProcessor);
		}

		keepChemicalProcessorInventory = false;
		p_149931_1_.setBlockMetadataWithNotify(p_149931_2_, p_149931_3_, p_149931_4_, l, 2);

		if (tileentity != null) {
			tileentity.validate();
			p_149931_1_.setTileEntity(p_149931_2_, p_149931_3_, p_149931_4_, tileentity);
		}
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityChemicalProcessor();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		int l = MathHelper.floor_double(p_149689_5_.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l == 0) {
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 2, 2);
		}

		if (l == 1) {
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 5, 2);
		}

		if (l == 2) {
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 3, 2);
		}

		if (l == 3) {
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 4, 2);
		}

		if (p_149689_6_.hasDisplayName()) {
			((TileEntityChemicalProcessor) p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_)).setInventoryName(p_149689_6_.getDisplayName());
		}
	}

	@Override
	public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_) {
		if (!keepChemicalProcessorInventory) {
			TileEntityChemicalProcessor tileentitychemicalprocessor = (TileEntityChemicalProcessor) p_149749_1_.getTileEntity(p_149749_2_, p_149749_3_, p_149749_4_);

			if (tileentitychemicalprocessor != null) {
				for (int i1 = 0; i1 < tileentitychemicalprocessor.getSizeInventory(); ++i1) {
					ItemStack itemstack = tileentitychemicalprocessor.getStackInSlot(i1);

					if (itemstack != null) {
						float f = this.chemicalProcessorRand.nextFloat() * 0.8F + 0.1F;
						float f1 = this.chemicalProcessorRand.nextFloat() * 0.8F + 0.1F;
						float f2 = this.chemicalProcessorRand.nextFloat() * 0.8F + 0.1F;

						while (itemstack.stackSize > 0) {
							int j1 = this.chemicalProcessorRand.nextInt(21) + 10;

							if (j1 > itemstack.stackSize) {
								j1 = itemstack.stackSize;
							}

							itemstack.stackSize -= j1;
							EntityItem entityitem = new EntityItem(p_149749_1_, p_149749_2_ + f, p_149749_3_ + f1, p_149749_4_ + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

							if (itemstack.hasTagCompound()) {
								entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
							}

							float f3 = 0.05F;
							entityitem.motionX = (float) this.chemicalProcessorRand.nextGaussian() * f3;
							entityitem.motionY = (float) this.chemicalProcessorRand.nextGaussian() * f3 + 0.2F;
							entityitem.motionZ = (float) this.chemicalProcessorRand.nextGaussian() * f3;
							p_149749_1_.spawnEntityInWorld(entityitem);
						}
					}
				}

				p_149749_1_.func_147453_f(p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_);
			}
		}

		super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
		if (this.isActive) {
			int l = p_149734_1_.getBlockMetadata(p_149734_2_, p_149734_3_, p_149734_4_);
			float f = p_149734_2_ + 0.5F;
			float f1 = p_149734_3_ + 0.0F + p_149734_5_.nextFloat() * 6.0F / 16.0F;
			float f2 = p_149734_4_ + 0.5F;
			float f3 = 0.52F;
			float f4 = p_149734_5_.nextFloat() * 0.6F - 0.3F;

			if (l == 4) {
				p_149734_1_.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				p_149734_1_.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
			} else if (l == 5) {
				p_149734_1_.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				p_149734_1_.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
			} else if (l == 2) {
				p_149734_1_.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
				p_149734_1_.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
			} else if (l == 3) {
				p_149734_1_.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
				p_149734_1_.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal strength when this block inputs to a comparator.
	 */
	@Override
	public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_) {
		return Container.calcRedstoneFromInventory((IInventory) p_149736_1_.getTileEntity(p_149736_2_, p_149736_3_, p_149736_4_));
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
		return Item.getItemFromBlock(PolycraftMod.blockChemicalProcessor);
	}
}