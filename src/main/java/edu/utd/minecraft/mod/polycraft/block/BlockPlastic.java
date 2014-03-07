package edu.utd.minecraft.mod.polycraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.Plastic;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockPlastic extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon PlainTexture;
    @SideOnly(Side.CLIENT)
    private IIcon LabelTexture;

	public final Plastic plastic;
	
	public BlockPlastic(Plastic plastic)
	{
		super(Material.cloth);
		this.plastic = plastic;
        setCreativeTab(CreativeTabs.tabBlock);
	}

	public BlockPlastic setDurability(double durability)
	{
		return this;
	}

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
    	switch(p_149691_1_)
	    {
	    	//TOP
    		//case 1:
	    	//	return this.PlainTexture;
	    	default:
	    		return this.LabelTexture;
	    }
    }
  
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.PlainTexture = p_149651_1_.registerIcon(PolycraftMod.MODID + ":plastic"); 
        this.LabelTexture = p_149651_1_.registerIcon(PolycraftMod.MODID + ":" + plastic.gameName + "_label");
    }
}
