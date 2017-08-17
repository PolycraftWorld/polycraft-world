package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;

/**
 * Created this class for the sole purpose of adding durability to fluorescent
 * bulbs...
 */
public class ItemFluorescentBulbs extends ItemCustom {

	public ItemFluorescentBulbs(CustomObject config) {
		super(config);
		this.setMaxDamage(100);
	}

}
