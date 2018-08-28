package edu.utd.minecraft.mod.polycraft.minigame;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockGameBlock extends Block {

	public final CustomObject config;
	private IIcon icon;
	public double radius;
	public boolean active;
	
	public BlockGameBlock(CustomObject config) {
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		this.config=config;
		this.radius=200;
	}
	
	
	public void shrinkKillWall()
	{
		if(getKillWallActive())
		{
			if(this.radius>0)
				this.radius=this.radius-.1;
			else
				this.radius=0;
		}
	}
	public boolean getKillWallActive()
	{
		return this.active;
	}
	
	public  boolean isInKillWall(EntityPlayer player)
	{
		if(Math.abs(player.posX)>this.getRadius() || Math.abs(player.posZ)>this.getRadius())
			return true;
		else
			return false;
	}

	
	public void setRadius(double r)
	{
		this.radius=r;
	}
	
	public double getRadius()
	{
		return this.radius;
	}
	
	
    public int quantityDropped(Random p_149745_1_)
    {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
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
    	this.blockIcon = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName("brick_top_black")));
    }


}
