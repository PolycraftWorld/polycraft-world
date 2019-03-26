package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemSlingshot extends ItemCustom{
	
	public enum SlingshotType{
		WOODEN, TACTICAL, SCATTER, BURST, GRAVITY, ICE;
	}

	public ItemSlingshot(CustomObject config) {
		super(config);
	}
	

}
