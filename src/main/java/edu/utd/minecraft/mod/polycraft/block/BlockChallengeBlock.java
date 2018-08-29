package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockChallengeBlock extends Block {
	
	private IIcon icon;
	public final CustomObject config;

	
	public BlockChallengeBlock(CustomObject config) {
		super(Material.glass);
		this.config = config;
		//super(PolycraftMaterial.plasticWhite);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(5);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {
		return this.icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		this.icon = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName("Challenge_Block")));
	}

	 public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_) {
		  
		 //TODO: code here
		 
	 }
	 
	 /**
	     * Called when a player hits the block. Args: world, x, y, z, player
	     */
	 public void onBlockClicked(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_)
	 {
		 if(!p_149699_1_.isRemote)
		{
			 ((EntityPlayer) p_149699_5_).addChatComponentMessage(new ChatComponentText("Loot Loot"));
		}
	 }


	
	

}
