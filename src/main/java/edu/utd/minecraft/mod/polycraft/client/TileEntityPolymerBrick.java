package edu.utd.minecraft.mod.polycraft.client;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityPolymerBrick extends TileEntity {

	protected EnumFacing orientation;

	public TileEntityPolymerBrick() {
		orientation = EnumFacing.SOUTH;
	}

	public EnumFacing getOrientation() {
		return orientation;
	}

	public void setOrientation(EnumFacing orientation) {
		this.orientation = orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = EnumFacing.getFront(orientation);
	}
}