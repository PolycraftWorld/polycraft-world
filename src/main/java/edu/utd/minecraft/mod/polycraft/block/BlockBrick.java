package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.PlasticBrick;

public class BlockBrick extends Block {

	public final PlasticBrick Brick;
	private final BlockBrickHelper helper;
	private final int length, width;

	public BlockBrick(final PlasticBrick Brick, final int length, final int width) {
		super(Material.cloth);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.Brick = Brick;
		this.length = length;
		this.width = width;
		this.helper = new BlockBrickHelper(Brick.source, width, length, -1);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {
		return helper.getIcon(side, colorIndex);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		helper.registerBlockIcons(p_149651_1_);
	}

	// @Override
	// @SideOnly(Side.CLIENT)
	// public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
	// helper.getSubBlocks(p_149666_1_, p_149666_2_, p_149666_3_);
	// }

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

		helper.onBlockPlacedBy(worldObj, xPos, yPos, zPos, player, itemToPlace);
		Block block = worldObj.getBlock(xPos, yPos, zPos);
		int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
		boolean blockCanBePlaced = true;
		for (int len = 0; len < this.length; len++)
		{
			for (int wid = 0; wid < this.width; wid++)
			{
				if ((len == 0) && (wid == 0)) // keeps the just placed block from triggering
					continue;

				if (l == 0) // facing south (+z)
				{
					if ((worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.air) &&
							(worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
					{
						blockCanBePlaced = false;
						break;
					}
				}

				if (l == 1) // facing west (-x)
				{
					if ((worldObj.getBlock(xPos - len, yPos, zPos - wid) != Blocks.air) &&
							(worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
					{
						blockCanBePlaced = false;
						break;
					}

				}

				if (l == 2) // facing north (-z)
				{
					if ((worldObj.getBlock(xPos + wid, yPos, zPos - len) != Blocks.air) &&
							(worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
					{
						blockCanBePlaced = false;
						break;
					}
				}

				if (l == 3) // facing east (+x)
				{
					if ((worldObj.getBlock(xPos + len, yPos, zPos + wid) != Blocks.air) &&
							(worldObj.getBlock(xPos - wid, yPos, zPos + len) != Blocks.water))
					{
						blockCanBePlaced = false;
						break;
					}
				}
			}
		}

		if (blockCanBePlaced)
		{

			for (int len = 0; len < this.length; len++)
			{
				for (int wid = 0; wid < this.width; wid++)
				{
					if (l == 0) // facing south (+z)
						worldObj.setBlock(xPos - wid, yPos, zPos + len, this, meta, 2);
					if (l == 1) // facing west (-x)
						worldObj.setBlock(xPos - len, yPos, zPos - wid, this, meta, 2);
					if (l == 2) // facing north (-z)
						worldObj.setBlock(xPos + wid, yPos, zPos - len, this, meta, 2);
					if (l == 3) // facing east (+x)
						worldObj.setBlock(xPos + len, yPos, zPos + wid, this, meta, 2);
				}
			}

		}
		else
		{
			worldObj.setBlock(xPos, yPos, zPos, Blocks.air);
			itemToPlace.stackSize += 1;

		}

	}

	// @Override
	// public int damageDropped(int p_149692_1_) {
	// return helper.damageDropped(p_149692_1_);
	// }

	public String getUnlocalizedName(int colorIndex) {
		return Brick.itemGameID + "." + colorIndex;
	}
}