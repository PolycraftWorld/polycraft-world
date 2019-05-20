package edu.utd.minecraft.mod.polycraft.block.material;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BreakBlockPP extends Block{

	public final CustomObject config;
	
	public BreakBlockPP(CustomObject config) {
		super(Material.rock);
		this.config=config;
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setBlockName("Break Block");
		this.setHardness(2F);
		//this.setBlockUnbreakable();
	}
	
	
	@Override
	public void onBlockClicked(World p_149699_1_, int x, int y, int z, EntityPlayer player) 
	{
		//player.addChatMessage(new ChatComponentText("Test!"));
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{	
		return false;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity entity) 
	{
//		if(entity instanceof EntityPlayer)
//		{
//			EntityPlayer player =(EntityPlayer)entity;
//			player.addChatMessage(new ChatComponentText("Test!"));
//		}
		
	}
	
	@Override
	public boolean isOpaqueCube()
    {
        return true;
    }
	
	@Override
	public boolean isCollidable()
	{
		return true;
	}
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return Item.getItemById(0);
	}
	

	@SideOnly(Side.CLIENT)
    public Item getItem(World world, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.getItemById(0);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {
		return this.blockIcon;
	}

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
    	this.blockIcon =  Blocks.cobblestone.getBlockTextureFromSide(0);

        
    }
	
	
}
