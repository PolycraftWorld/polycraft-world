
package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.item.ItemSlingshot__Old.SlingshotType;

public class ItemSlingshot extends ItemCustom {
	  
	public enum SlingshotType{
		WOODEN, TACTICAL, SCATTER, BURST, GRAVITY, ICE;
	}
    private int numTicksSinceLastShot = 0;
	SlingshotType type;
	public ItemSlingshot(CustomObject config) {
		super(config);
	}
	

}
