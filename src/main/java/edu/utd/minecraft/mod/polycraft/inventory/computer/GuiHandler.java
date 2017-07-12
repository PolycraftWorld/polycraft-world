package edu.utd.minecraft.mod.polycraft.inventory.computer;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler /*implements IGuiHandler*/{
	
/*	GuiHandler(){
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance,this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof ComputerInventory)
			return new ComputerContainer(player.inventory, (ComputerInventory) tileEntity);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof ComputerInventory)
			return new ComputerGui(player.inventory, (ComputerInventory) tileEntity);
		return null;


	}
	*/

}
