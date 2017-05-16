package edu.utd.minecraft.mod.polycraft.client;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPolymerBrick extends TileEntity {

	protected ForgeDirection orientation;

	public TileEntityPolymerBrick() {
		orientation = ForgeDirection.SOUTH;
	}

	public ForgeDirection getOrientation() {
		return orientation;
	}

	public void setOrientation(ForgeDirection orientation) {
		this.orientation = orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = ForgeDirection.getOrientation(orientation);
	}
}