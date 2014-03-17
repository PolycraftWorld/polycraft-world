package edu.utd.minecraft.mod.polycraft.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.ContainerChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.GuiChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.TileEntityChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.fracker.ContainerFracker;
import edu.utd.minecraft.mod.polycraft.inventory.fracker.GuiFracker;
import edu.utd.minecraft.mod.polycraft.inventory.fracker.TileEntityFracker;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityFracker) {
			return new ContainerFracker(player.inventory, (TileEntityFracker) tileEntity);
		}
		if (tileEntity instanceof TileEntityChemicalProcessor) {
			return new ContainerChemicalProcessor(player.inventory, (TileEntityChemicalProcessor) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityFracker) {
			return new GuiFracker(player.inventory, (TileEntityFracker) tileEntity);
		}
		if (tileEntity instanceof TileEntityChemicalProcessor) {
			return new GuiChemicalProcessor(player.inventory, (TileEntityChemicalProcessor) tileEntity);
		}
		return null;

	}
}