package edu.utd.minecraft.mod.polycraft.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.IGuiHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.block.GuiScreenPasswordDoor;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class GuiHandler implements IGuiHandler {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof PolycraftInventory) {
			return ((PolycraftInventory) tileEntity).getCraftingContainer(player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof PolycraftInventory) {
			return ((PolycraftInventory) tileEntity).getGui(player.inventory);
		} 
		
		
		return null;

	}
}