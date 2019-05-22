package edu.utd.minecraft.mod.polycraft.block;

import com.google.common.base.Preconditions;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockPasswordDoor extends BlockPolycraftDoor{

	private String pswd;
	
	public BlockPasswordDoor(final CustomObject config, Material material, String password) {
		super(config, material);
		pswd = password;
	}
	
	/**
     * Called upon block activation (right click on the block.)
     */
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		if (!world.isRemote)
	    {
	        PolycraftMod.proxy.openDoorGui(this, player, x, y, z); 
	    }
	        
	    return true;
    }
	
	
	
	

}
