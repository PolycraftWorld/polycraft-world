package edu.utd.minecraft.mod.polycraft.block.material;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;

public class PolycraftMaterial extends Material {

	public final static PolycraftMaterial plasticBlack = (new PolycraftMaterial(MapColor.blackColor)).setRequiresTool();
	public final static PolycraftMaterial plasticRed = (new PolycraftMaterial(MapColor.redColor)).setRequiresTool();
	public final static PolycraftMaterial plasticGreen = (new PolycraftMaterial(MapColor.greenColor)).setRequiresTool();
	public final static PolycraftMaterial plasticBrown = (new PolycraftMaterial(MapColor.brownColor)).setRequiresTool();
	public final static PolycraftMaterial plasticBlue = (new PolycraftMaterial(MapColor.blueColor)).setRequiresTool();
	public final static PolycraftMaterial plasticPurple = (new PolycraftMaterial(MapColor.purpleColor)).setRequiresTool();
	public final static PolycraftMaterial plasticCyan = (new PolycraftMaterial(MapColor.cyanColor)).setRequiresTool();
	public final static PolycraftMaterial plasticSilver = (new PolycraftMaterial(MapColor.silverColor)).setRequiresTool();
	public final static PolycraftMaterial plasticGray = (new PolycraftMaterial(MapColor.grayColor)).setRequiresTool();
	public final static PolycraftMaterial plasticPink = (new PolycraftMaterial(MapColor.pinkColor)).setRequiresTool();
	public final static PolycraftMaterial plasticLime = (new PolycraftMaterial(MapColor.limeColor)).setRequiresTool();
	public final static PolycraftMaterial plasticYellow = (new PolycraftMaterial(MapColor.yellowColor)).setRequiresTool();
	public final static PolycraftMaterial plasticLightBlue = (new PolycraftMaterial(MapColor.lightBlueColor)).setRequiresTool();
	public final static PolycraftMaterial plasticMagenta = (new PolycraftMaterial(MapColor.magentaColor)).setRequiresTool();
	public final static PolycraftMaterial plasticAdobe = (new PolycraftMaterial(MapColor.adobeColor)).setRequiresTool();
	public final static PolycraftMaterial plasticWhite = (new PolycraftMaterial(MapColor.snowColor)).setRequiresTool();
	public static final Material oil = (new PolycraftMaterialLiquid(MapColor.blackColor)).setNoPushMobility();
	    
	
	private boolean requiresNoTool;	
		

	public PolycraftMaterial(MapColor par1MapColor) {
		super(par1MapColor);
        this.setReplaceable();
        this.setNoPushMobility();
    }

    /**
     * Returns if blocks of these materials are liquids.
     */
	@Override
    public boolean isLiquid()
    {
        return false;
    }

    /**
     * Returns if this material is considered solid or not
     */
	@Override
    public boolean blocksMovement()
    {
        return true;
    }

    public boolean isSolid()
    {
        return true;
    }
    
    /**
     * Makes blocks with this material require the correct tool to be harvested.
     */
    protected PolycraftMaterial setRequiresTool()
    {
        this.requiresNoTool = false;
        return this;
    }
}