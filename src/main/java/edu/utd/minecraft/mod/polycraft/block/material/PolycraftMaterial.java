package edu.utd.minecraft.mod.polycraft.block.material;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class PolycraftMaterial extends Material {

	final PolycraftMaterial plasticBlack = (new PolycraftMaterial(MapColor.blackColor)).setRequiresTool();
	final PolycraftMaterial plasticRed = (new PolycraftMaterial(MapColor.redColor)).setRequiresTool();
	final PolycraftMaterial plasticGreen = (new PolycraftMaterial(MapColor.greenColor)).setRequiresTool();
	final PolycraftMaterial plasticBrown = (new PolycraftMaterial(MapColor.brownColor)).setRequiresTool();
	final PolycraftMaterial plasticBlue = (new PolycraftMaterial(MapColor.blueColor)).setRequiresTool();
	final PolycraftMaterial plasticPurple = (new PolycraftMaterial(MapColor.purpleColor)).setRequiresTool();
	final PolycraftMaterial plasticCyan = (new PolycraftMaterial(MapColor.cyanColor)).setRequiresTool();
	final PolycraftMaterial plasticSilver = (new PolycraftMaterial(MapColor.silverColor)).setRequiresTool();
	final PolycraftMaterial plasticGray = (new PolycraftMaterial(MapColor.grayColor)).setRequiresTool();
	final PolycraftMaterial plasticPink = (new PolycraftMaterial(MapColor.pinkColor)).setRequiresTool();
	final PolycraftMaterial plasticLime = (new PolycraftMaterial(MapColor.limeColor)).setRequiresTool();
	final PolycraftMaterial plasticYellow = (new PolycraftMaterial(MapColor.yellowColor)).setRequiresTool();
	final PolycraftMaterial plasticLightBlue = (new PolycraftMaterial(MapColor.lightBlueColor)).setRequiresTool();
	final PolycraftMaterial plasticMagenta = (new PolycraftMaterial(MapColor.magentaColor)).setRequiresTool();
	final PolycraftMaterial plasticAdobe = (new PolycraftMaterial(MapColor.adobeColor)).setRequiresTool();
	final PolycraftMaterial plasticWhite = (new PolycraftMaterial(MapColor.snowColor)).setRequiresTool();
	
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