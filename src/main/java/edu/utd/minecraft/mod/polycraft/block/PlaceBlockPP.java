package edu.utd.minecraft.mod.polycraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class PlaceBlockPP extends BlockAir {

	public final CustomObject config;
	
	public PlaceBlockPP(CustomObject config) {
		super();
		this.config=config;
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setBlockName("Place Block");
		this.setBlockUnbreakable();
	}
	
	
	/**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return -1;
    }

	
    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
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
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player =(EntityPlayer)entity;
			//player.addChatMessage(new ChatComponentText("Test!"));
		}
		
	}
	
	@Override
	public Block setLightOpacity(int p_149713_1_) {
		// TODO Auto-generated method stub
		return super.setLightOpacity(p_149713_1_);
	}
	
	@Override
	public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
	public boolean isCollidable()
	{
		return false;
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
    	this.blockIcon = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName("Blank")));

        
    }

}
