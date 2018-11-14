package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class TextWallBlock extends PolycraftInventoryBlock<TextWallInventory> {

	@SideOnly(Side.CLIENT)
	public IIcon iconOutside;
	@SideOnly(Side.CLIENT)
	public IIcon iconTop;
	@SideOnly(Side.CLIENT)
	public IIcon iconInside;
	
	
	public TextWallBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass);
		this.setBlockBounds(0.0F, 0F, -.13F, 8.0F, 5.0F, .13F);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onBlockActivated(World world, int posX, int posY, int posZ, 
			EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if(world.isRemote) {
			//open the consent GUI on right-click.
			PolycraftMod.proxy.openConsentGui(player, posX, posY, posZ);
			
		}
		return false;
	}

	


}
