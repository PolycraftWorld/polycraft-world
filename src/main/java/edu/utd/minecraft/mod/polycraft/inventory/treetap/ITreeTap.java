package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public interface ITreeTap extends IInventory
{
	/**
	 * Returns the worldObj for this tileEntity.
	 */
	World getWorldObj();

	/**
	 * Gets the world X position for this hopper entity.
	 */
	double getXPos();

	/**
	 * Gets the world Y position for this hopper entity.
	 */
	double getYPos();

	/**
	 * Gets the world Z position for this hopper entity.
	 */
	double getZPos();
}