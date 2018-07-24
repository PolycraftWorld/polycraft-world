package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.common.eventhandler.Event.Result;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemDevTool extends ItemCustom  {
	
	int[] block1xyz= new int[3];
	int[] block2xyz = new int[3];
	String tool;
	boolean setting;
	

	public ItemDevTool(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("gripped_engineered_diamond_axe"));
		this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.setting =false;
		
	}
		
	 @Override
	// Doing this override means that there is no localization for language
	// unless you specifically check for localization here and convert
	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
	    return "Dev Tool";
	}  
	 @Override
	 public boolean  hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_)
	 {
		 p_77644_2_.setHealth(0);
	     return true;
	 }
	 @Override
	 public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
	 {
		 if(!p_77659_2_.isRemote)
		 {
			 this.setting= !setting;
			 if(!setting){
				 ((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Set Position 1"));
			 }else {
				 ((EntityPlayer) p_77659_3_).addChatComponentMessage(new ChatComponentText("Set Position 2"));
			 }
		 }
		     return p_77659_1_;
	 }
	
	
	
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
	{
		if(!p_77648_3_.isRemote)
		{
			if(!setting) {
		    	block1xyz[0]=p_77648_4_;
		        block1xyz[1]=p_77648_5_;
		        block1xyz[2]=p_77648_6_;
		        String s="Position 1: "+block1xyz[0]+", " +block1xyz[1]+", "+block1xyz[2];
		        ((EntityPlayer) p_77648_2_).addChatComponentMessage(new ChatComponentText(s));
				
			}else {
		        
		        block2xyz[0]=p_77648_4_;
		        block2xyz[1]=p_77648_5_;
		        block2xyz[2]=p_77648_6_;
		        String s="Position 2: "+block2xyz[0]+", " +block2xyz[1]+", "+block2xyz[2];
		        ((EntityPlayer) p_77648_2_).addChatComponentMessage(new ChatComponentText(s));
			}
		}
	    return false;
	}
}
