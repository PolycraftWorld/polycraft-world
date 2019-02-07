package edu.utd.minecraft.mod.polycraft.block;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.item.ItemCustom;
import edu.utd.minecraft.mod.polycraft.tileentity.TileEntityHPBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class HPBlock extends Block implements ITileEntityProvider{

	public HPBlock(CustomObject config) {
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setBlockName("HP Block");
		this.setBlockTextureName(PolycraftMod.getAssetName("hpblock"));
	}
	
	
	
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	    {
			//super.createTileEntity(p_149915_1_, p_149915_2_);
	        TileEntityHPBlock tileentitychest = new TileEntityHPBlock();
	        return tileentitychest;
	    }

	
	 public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	 {
		 if (!world.isRemote)
		 {
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("This block has " + ((TileEntityHPBlock)world.getTileEntity(x, y, z)).getHP() + " hp out of " + ((TileEntityHPBlock)world.getTileEntity(x, y, z)).getMaxHP() + " hp"));
		return false;	
		 }
		 return false;
	}

	
	
	
	
}
