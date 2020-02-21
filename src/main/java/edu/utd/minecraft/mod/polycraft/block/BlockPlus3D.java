package edu.utd.minecraft.mod.polycraft.block;

import java.awt.Color;
import java.util.Random;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPlus3D extends Block{

	private int colorMult = 16777215;
	
	public BlockPlus3D(CustomObject config) {
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if (!world.isRemote){
			colorMult = (int) Math.random()* Color.WHITE.getRGB();
			player.addChatComponentMessage(new ChatComponentText("test"));
			return false;	
		}
		colorMult = (int) Math.random()* Color.WHITE.getRGB();
		player.addChatComponentMessage(new ChatComponentText("test"));
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player,
			EnumFacing side, float hitX, float hitY, float hitZ) {
//		if (!worldIn.isRemote){
//			colorMult = (int) (Math.random()* Color.BLACK.getRGB());
//			return super.onBlockActivated(worldIn, pos, state, player, side, hitX, hitY, hitZ);	
//		}
//		colorMult = (int) (Math.random()* Color.BLACK.getRGB());
//		return super.onBlockActivated(worldIn, pos, state, player, side, hitX, hitY, hitZ);
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return super.getItemDropped(state, rand, fortune);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return new AxisAlignedBB((double)pos.getX() + this.minX + 0.2, (double)pos.getY() + this.minY + 0.2, (double)pos.getZ() + this.minZ + 0.2, (double)pos.getX() + this.maxX - 0.2, (double)pos.getY() + this.maxY - 0.2, (double)pos.getZ() + this.maxZ - 0.2);
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if(entityIn instanceof EntityPlayer) {
			if(!worldIn.isRemote) {
				((EntityPlayer)entityIn).inventory.addItemStackToInventory(new ItemStack(Item.getItemFromBlock(worldIn.getBlockState(pos).getBlock()), 1));
				worldIn.destroyBlock(pos, false);
			}
		}
		super.onEntityCollidedWithBlock(worldIn, pos, entityIn);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
		if(entityIn instanceof EntityPlayer) {
			if(!worldIn.isRemote) {
				worldIn.destroyBlock(pos, true);
			}
		}
		super.onEntityCollidedWithBlock(worldIn, pos, entityIn);
	}
	
	@Override
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
		return colorMult;
	}
	
	@Override
	public int getRenderColor(IBlockState state) {
		return colorMult;
	}
	
	public int getColorMult() {
		return colorMult;
	}
	
	@Override
	public int getBlockColor() {
		return colorMult;
	}
	
	@SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }
}
