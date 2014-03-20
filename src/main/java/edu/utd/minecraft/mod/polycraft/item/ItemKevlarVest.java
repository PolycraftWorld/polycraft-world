package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemArmor;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class ItemKevlarVest extends ItemArmor {

	private final Plastic plastic;

	public ItemKevlarVest(final Plastic plastic) {
		super(plastic.kevlarArmorType, 1, 1);
		this.plastic = plastic;
	}
}
