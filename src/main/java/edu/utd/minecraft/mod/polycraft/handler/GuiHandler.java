package edu.utd.minecraft.mod.polycraft.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.IGuiHandler;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.ContainerChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.GuiChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.TileEntityChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.ContainerMachiningMill;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.GuiMachiningMill;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.TileEntityMachiningMill;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.ContainerTreeTap;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.GuiTreeTap;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TileEntityTreeTap;

public class GuiHandler implements IGuiHandler {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityTreeTap) {
			return new ContainerTreeTap(player.inventory, (TileEntityTreeTap) tileEntity);
		}
		if (tileEntity instanceof TileEntityMachiningMill) {
			return new ContainerMachiningMill(player.inventory, (TileEntityMachiningMill) tileEntity);
		}
		if (tileEntity instanceof TileEntityChemicalProcessor) {
			return new ContainerChemicalProcessor(player.inventory, (TileEntityChemicalProcessor) tileEntity);
		}
		if (tileEntity instanceof PolycraftInventory) {
			return new PolycraftCraftingContainerGeneric(player.inventory, (PolycraftInventory)tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityTreeTap) {
			return new GuiTreeTap(player.inventory, (TileEntityTreeTap) tileEntity);
		}
		if (tileEntity instanceof TileEntityMachiningMill) {
			return new GuiMachiningMill(player.inventory, (TileEntityMachiningMill) tileEntity);
		}
		if (tileEntity instanceof TileEntityChemicalProcessor) {
			return new GuiChemicalProcessor(player.inventory, (TileEntityChemicalProcessor) tileEntity);
		}
		if (tileEntity instanceof PolycraftInventory) {
			return new PolycraftInventoryGui(player.inventory, (PolycraftInventory)tileEntity);
		}
		return null;

	}
}