package edu.utd.minecraft.mod.polycraft.block.material;

import net.minecraft.block.material.MapColor;

public class PolycraftMaterialLiquid extends PolycraftMaterial{
	
	
	public PolycraftMaterialLiquid(MapColor par1MapColor) {
		super(par1MapColor);
		// TODO Auto-generated constructor stub
	}
	
	 /**
     * Returns if blocks of these materials are liquids.
     */
	@Override
    public boolean isLiquid()
    {
        return true;
    }

    /**
     * Returns if this material is considered solid or not
     */
	@Override
    public boolean blocksMovement()
    {
        return false;
    }

    public boolean isSolid()
    {
        return false;
    }

}
