package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class ItemKevlarVest extends ItemArmor {

	private final Plastic plastic;

	public ItemKevlarVest(final Plastic plastic) {
		super(plastic.kevlarArmorType, 1, 1);
		this.plastic = plastic;
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getTextureName("textures/models/armor/kevlar_layer_1.png");
	}
}
